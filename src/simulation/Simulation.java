package simulation;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeMap;

import enums.EventType;
import enums.Group;
import enums.PackType;

import maths.SimulatedRandom;
import maths.SimulationProperties;
import maths.Statistics;
import network.Event;
import network.Pack;
import network.Rx;
import network.SACK;
import network.Sys;
import network.TransientPhaseData;
import network.Tx;

/**
 * Responsável pelo loop de simulação.
 */
public class Simulation {
	//-------------------------------------------------------------------
	// Dados armazenados para os cenários.
	/**
	 * Armazena o valor de Cwnd/MSS ao longo do tempo de simulação.
	 */
	private TreeMap<Double, Double> mapCurrentTimePerCwndMSS = new TreeMap<Double, Double>();
	
	/**
	 * Armazena (próximo byte esperado)/tempo x tempo para cada TX. 
	 */
	private ArrayList<TreeMap<Double, Double>> flowPerTx = new ArrayList<TreeMap<Double,Double>>();
	//-------------------------------------------------------------------

	/**
	 *  Engloba todos os Tx e Rx do sistema, e também o roteador, cuja disciplina é passada por parâmetro.
	 */
	private Sys system = new Sys(SimulationProperties.getRouterType());
	
	/**
	 * Tempo atual, que muda ao longo da simulação.
	 */
	private double currentTime = 0.0;
	
	/**
	 * Fila de eventos. É priorizada sobre o tempo de ocorrência.
	 * Veja o compareTo() da classe Event.
	 */
	private PriorityQueue<Event> events = new PriorityQueue<Event>();
	
	/**
	 * Gerador de números aleatórios.
	 */
	private SimulatedRandom random;
	
	/**
	 * Intervalo médio entre chegadas Poisson de tráfego de fundo. 24ms.
	 */
	private final int congestionPacketFrequency = 24;
	
	private double transmissionTime = SimulationProperties.getMSS() / SimulationProperties.getCs();
	private double transmissionTimeMs = transmissionTime * (8/1E-3);
	
	/**
	 * Cada conexão Tx-Rx terá suas estatísticas estimadas
	 */
	private ArrayList<Statistics> statisticsPerTx = new ArrayList<Statistics>();

	/**
	 * Construtor. Seta valores iniciais e parâmetros iniciais.
	 */
	public Simulation() {
		random  = new SimulatedRandom();
		
		for(int i = 0 ; i < SimulationProperties.getQuantityOfG1() +
				            SimulationProperties.getQuantityOfG2() ; i++) {
			statisticsPerTx.add(new Statistics());
			flowPerTx.add(new TreeMap<Double,Double>());
		}
	}
	
	/**
	 * Reseta a lista de eventos e o sistema.
	 */
	private void resetSimulation() {
		system = new Sys(SimulationProperties.getRouterType());
		events = new PriorityQueue<Event>();
		currentTime = 0.0;
	}
	
	/**
	 * Avalia a qualidade dos resultados com confiança de 90%.
	 * @return True se é satisfatório.
	 */
	private boolean confidenceNotAcceptable() {
		for(Statistics s : statisticsPerTx) {
			if( s.getQuantityOfSamples() == 0 ||
				s.getQuantityOfSamples() == 1 ||
				// 2 * significa = Toda a distância, do limite inferior ao superior.
				(2 * s.getAverageConfidenceIntervalDistance(0.9) > 0.1 * s.estimateAverage()) )
				return false;
		}
		return true;
	}
	
	/**
	 * Informa o atraso de propagação de um Tx, com base em seu grupo.
	 * @param tx O servidor em análise.
	 * @return O atraso de propagação.
	 */
	private double setPropagationDelay(Tx tx) {
		if(tx.getGroup().equals(Group.Group1))
			return SimulationProperties.getTP1();
		else if(tx.getGroup().equals(Group.Group2))
			return SimulationProperties.getTP2();
		else // Significa que existe um terceiro grupo. O programa deveria finalizar.
			throw new RuntimeException();
	}
	
	/**
	 * A fila de eventos precisa ser setada com eventos iniciais.
	 * Este método seta envios de pacotes de congestionamento (tráfego de fundo)
	 * e também envios de pacotes vindo dos Txs.
	 */
	private void setFirstEvents() {
		resetSimulation();
		
		// Prepara o tráfego de fundo.
		if(SimulationProperties.isWithCongestion()) {
			Event firstCongestionPack = new Event(EventType.CongestionPacketIntoRouter); // Evento "Pacote de Tráfego de Fundo chegar ao roteador"
			firstCongestionPack.setTime(random.generateExponential(1.0 / congestionPacketFrequency));
			getEvents().add(firstCongestionPack);
		}
		
		// Prepara os servidores para transmissão de Tx-Rx.
		for(Tx tx : getSystem().getTxs()) {
			double propagationDelay = setPropagationDelay(tx);
			double randTime = random.generateDouble() * SimulationProperties.getAssyncInterval(); // Fator aleatório além da propagação e da transmissão.
			
			Event packSent = new Event(EventType.TxPacketHeadsToRouter); // Evento "Pacote sair do Tx"
			packSent.setTime(randTime + transmissionTime);
			packSent.setTxIndex(tx.getIndex());
			getEvents().add(packSent);
			
			Pack pack = tx.sendPacket(randTime);
			Event firstTcpPack = new Event(EventType.TxPacketIntoRouter); // Evento "Pacote TCP chegar ao roteador"
			firstTcpPack.setTime(randTime + transmissionTime + propagationDelay);
			firstTcpPack.setPack(pack);
			getEvents().add(firstTcpPack);
			
			Event possibleTimeout = new Event(EventType.Timeout); // Evento "Possível Timeout do pacote"
			possibleTimeout.setTime(randTime + tx.getRTO());
			possibleTimeout.setTxIndex(tx.getIndex());
			pack.setTimeout(possibleTimeout);
			getEvents().add(possibleTimeout);
		}
	}
	
	/**
	 * Executa a fase transiente.
	 * @return Tempo de término da fase e a sequência de bytes esperados nos receptores.
	 */
	public TransientPhaseData transientPhase() {
		for(int i = 0 ; i < SimulationProperties.getTransientPhaseEvents() ; i++)
			handleEvents();
		
		double transientPhaseEndingTime = currentTime;
		ArrayList<Long> expectedBytes = new ArrayList<Long>();
		
		for(Rx rx: getSystem().getRxs())
			expectedBytes.add(rx.getNextExpectedByte());
		
		return new TransientPhaseData(transientPhaseEndingTime, expectedBytes);
	}
	
	/**
	 * Executa a simulação, em Loop.
	 */
	public void run() {
		while (!confidenceNotAcceptable()) {	
			setFirstEvents();
			
			// Os dados desta fase são subtraídos das estatísticas finais.
			TransientPhaseData transientPhaseData = transientPhase();
			
			for (int i = 0; i < SimulationProperties.getEventsInARow(); i++) {
				// Usado no cenário 1.
				mapCurrentTimePerCwndMSS.put(currentTime, getSystem().getTxs().get(0).getCongestionWindow()/SimulationProperties.getMSS().doubleValue());
				// Armazena dados de vazão ao longo do tempo, para cada Tx.
				populateFlowPerTx();
				handleEvents();
			}
			
			for(int i = 0 ; i < getSystem().getRxs().size() ; i++) {
				double a = getSystem().getRxs().get(i).getNextExpectedByte() - transientPhaseData.getExpectedBytes().get(i);
				double b = currentTime - transientPhaseData.getTransientPhaseEndingTime();
				statisticsPerTx.get(i).addSample((a/b) * (8/1E-3));
			}
		}
	}
	
	private void populateFlowPerTx() {
		for(int i = 0 ; i < flowPerTx.size() ; i++) {
			flowPerTx.get(i).put(currentTime, system.getRxs().get(i).getNextExpectedByte()/currentTime);
		}
	}

	/**
	 * Retira o próximo evento da fila de prioridades, e o trata.
	 */
	public void handleEvents() {
		// Não é esperado que esta lista esteja vazia...
		if(getEvents().isEmpty()) throw new RuntimeException();
		// Evento é retornado e deletado da fila.
		Event event = getEvents().poll();
		
		// Seria uma falha séria na lógica do simulador, mas é importante testar os tempos de início.
		if(event.getTime() < currentTime) throw new RuntimeException(); 
		
		currentTime = event.getTime();
		switch (event.getType()) {
			case CongestionPacketIntoRouter:
				handleCongestionPacketIntoRouterEvent(event); break;
			case TxPacketIntoRouter:
				handleTxPacketIntoRouter(event); break;
			case Timeout:
				handleTimeout(event); break;
			case RouterSuccessfullySentPacket:
				handleRouterSuccessfullySentPacket(event); break;
			case TxPacketHeadsToRouter:
				handleTxPacketHeadsToRouter(event); break;
			case SackArrives:
				handleSackArrives(event); break;
			default:
				throw new RuntimeException(); // Não deve existir outro evento...
		}
	}

	private void handleSackArrives(Event event) {
		SACK sack = event.getSack();
		Tx tx = getSystem().getTxs().get(sack.getDestination());
		tx.receiveSack(sack, currentTime);
		
		if(!tx.isTransmiting())
			if(tx.getNextPacketToSend() < tx.getOldestNotReceivedPacket() + tx.getCongestionWindow())
				newTxPacketHeadsToRouter(tx);
		
		getEvents().remove(sack.getTimeout());
		
		ArrayList<Pack> packs = new ArrayList<Pack>();
		for(Pack pack : tx.getNotDeliveredPacks())
			packs.add(pack);
		
		for(Pack pack : packs) {
			if(pack.getEndingByte() < sack.getNextExpectedByte()) {
				getEvents().remove(pack.getTimeout());
				tx.getNotDeliveredPacks().remove(pack);
			}
		}
	}

	private void handleTxPacketHeadsToRouter(Event event) {
		Tx tx = getSystem().getTxs().get(event.getTxIndex());
		if(tx.getNextPacketToSend() < tx.getOldestNotReceivedPacket() + tx.getCongestionWindow())
			newTxPacketHeadsToRouter(tx);
		else
			tx.setTransmiting(false);
	}
	
	/**
	 * Agenda eventos de envio e de chegada de pacote no roteador.
	 * @param tx Servidor de saída do pacote.
	 */
	private void newTxPacketHeadsToRouter(Tx tx) {
		Pack nextTcpPack = tx.sendPacket(currentTime);
		tx.setTransmiting(true);
		double propagation = setPropagationDelay(tx);
		
		Event endOfTransmition = new Event(EventType.TxPacketHeadsToRouter);
		endOfTransmition.setTime(currentTime + transmissionTimeMs);
		endOfTransmition.setTxIndex(tx.getIndex());
		getEvents().add(endOfTransmition);
		
		Event routerReceivesTcp = new Event(EventType.TxPacketIntoRouter);
		routerReceivesTcp.setTime(currentTime + transmissionTimeMs + propagation);
		routerReceivesTcp.setPack(nextTcpPack);
		getEvents().add(routerReceivesTcp);
		
		Event timeout = new Event(EventType.Timeout);
		timeout.setTime(currentTime + tx.getRTO());
		timeout.setTxIndex(tx.getIndex());
		nextTcpPack.setTimeout(timeout);
		getEvents().add(timeout);
		
		if(tx.getNotDeliveredPacks().contains(nextTcpPack)) {
			int index = tx.getNotDeliveredPacks().indexOf(nextTcpPack);
			Pack p = tx.getNotDeliveredPacks().get(index);
			getEvents().remove(p.getTimeout());
			tx.getNotDeliveredPacks().remove(index);
		}
		
		tx.getNotDeliveredPacks().add(nextTcpPack);
	}

	private void handleRouterSuccessfullySentPacket(Event event) {
		SACK sack = getSystem().getRouter().sendPackToRx(currentTime);
		if(sack != null) { // Tráfego de fundo se perde
			int ackPropagation = getSystem().getTxs().get(sack.getDestination())
					.getGroup().equals(Group.Group1) ?
					SimulationProperties.getAckG1PropagationTime() :
					SimulationProperties.getAckG2PropagationTime();
			Event sendSack = new Event(EventType.SackArrives);
			sendSack.setTime(currentTime + ackPropagation);
			sendSack.setSack(sack);
			getEvents().add(sendSack);
		}
		// Mais um pacote sai do roteador para um Rx.
		if(getSystem().getRouter().getBuffer().quantityOfRemainingPackets().intValue() >= 1) {
			Event sendPack = new Event(EventType.RouterSuccessfullySentPacket);
			double packTransmission = packTransmission(SimulationProperties.getMSS());
			sendPack.setTime(currentTime + packTransmission);
			getEvents().add(sendPack);
		}
	}
	
	public double packTransmission(long size) {
		return 1E3 * size * (8 / SimulationProperties.getCg());
	}
	
	private void handleTimeout(Event event) {
		Tx tx = getSystem().getTxs().get(event.getTxIndex());
		
		if(!tx.isTransmiting()) {
			tx.handleTimeOutEvent();
			newTxPacketHeadsToRouter(tx);
		} else
			tx.handleTimeOutEvent();
	}

	private void handleTxPacketIntoRouter(Event event) {
		// Sempre que o roteador fica vazio, um novo envio é agendado.
		if(getSystem().getRouter().getBuffer().quantityOfRemainingPackets().intValue() == 0) {
			Event sendPack = new Event(EventType.RouterSuccessfullySentPacket);
			double packTransmission = packTransmission(SimulationProperties.getMSS());
			sendPack.setTime(currentTime + packTransmission);
			getEvents().add(sendPack);
		}
		getSystem().getRouter().receivePacket(event.getPack(), currentTime);
	}

	private void handleCongestionPacketIntoRouterEvent(Event event) {
		double scheduleTime = random.generateExponential(1.0 / congestionPacketFrequency);
		Event scheduleNextCongestionPack = new Event(EventType.CongestionPacketIntoRouter);
		scheduleNextCongestionPack.setTime(currentTime + scheduleTime);
		getEvents().add(scheduleNextCongestionPack);
		
		if(getSystem().getRouter().getBuffer().quantityOfRemainingPackets().equals(0)) {
			Event sendPack = new Event(EventType.RouterSuccessfullySentPacket);
			double packTransmission = packTransmission(SimulationProperties.getMSS());
			sendPack.setTime(currentTime + packTransmission);
			getEvents().add(sendPack);
		}
		
		long numberOfCongestionPacks = Math.round(random.generateGeometric(1.0 / congestionPacketFrequency));
		for(long i = 0 ; i < numberOfCongestionPacks ; i++)
			getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), currentTime);
	}
	
	public static void main(String[] args) {
		Simulation simulador = new Simulation();
		simulador.run();

		System.out.println("VAZÃO MÉDIA POR CONEXÃO:");
		for (int i = 0; i < simulador.statisticsPerTx.size(); i++) {
			System.out.println("\tTx"
					+ i
					+ ":\t"
					+ simulador.statisticsPerTx.get(i).estimateAverage()
					+ " ± "
					+ simulador.statisticsPerTx.get(i).getAverageConfidenceIntervalDistance(0.9));
		}

		System.out.println("Rodadas = "
				+ simulador.statisticsPerTx.get(0).getQuantityOfSamples());

		System.out.println("VAZÃO MÉDIA GRUPO 1:");
		Statistics estimadorVazaoMediaGrupo1 = new Statistics();
		int i = 0;
		while (i < SimulationProperties.getQuantityOfG1()) {
			estimadorVazaoMediaGrupo1
					.addSample(simulador.statisticsPerTx.get(i)
							.estimateAverage());
			i++;
		}
		System.out.println("\t\t" + estimadorVazaoMediaGrupo1.estimateAverage() + "±"
				+ estimadorVazaoMediaGrupo1.getAverageConfidenceIntervalDistance(0.9));

		System.out.println("VAZÃO MÉDIA GRUPO 2:");
		Statistics estimadorVazaoMediaGrupo2 = new Statistics();
		while (i < simulador.getSystem().getTxs().size()) {
			estimadorVazaoMediaGrupo2
					.addSample(simulador.statisticsPerTx.get(i)
							.estimateAverage());
			i++;
		}
		System.out.println("\t\t" + estimadorVazaoMediaGrupo2.estimateAverage() + "±"
				+ estimadorVazaoMediaGrupo2.getAverageConfidenceIntervalDistance(0.9));
	}

	public Sys getSystem() {
		return system;
	}

	public PriorityQueue<Event> getEvents() {
		return events;
	}

	public TreeMap<Double, Double> getMapCurrentTimePerCwndMSS() {
		return mapCurrentTimePerCwndMSS;
	}
	
	public ArrayList<Statistics> getStatisticsPerTx() {
		return statisticsPerTx;
	}
	
	public ArrayList<TreeMap<Double, Double>> getFlowPerTx() {
		return flowPerTx;
	}
}
