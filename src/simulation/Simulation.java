package simulation;

import java.util.ArrayList;
import java.util.PriorityQueue;

import enums.EventType;
import enums.Group;

import maths.SimulatedRandom;
import maths.SimulationProperties;
import maths.Statistics;
import network.Event;
import network.Pack;
import network.Rx;
import network.Sys;
import network.TransientPhaseData;
import network.Tx;

/**
 * Responsável pelo loop de simulação.
 */
public class Simulation {
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
		for(int i = 0 ; i < SimulationProperties.getQuantityOfG1() +
				            SimulationProperties.getQuantityOfG2() ; i++)
			statisticsPerTx.add(new Statistics());
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
		SimulatedRandom r = new SimulatedRandom();
		
		resetSimulation();
		
		// Prepara o tráfego de fundo.
		if(SimulationProperties.isWithCongestion()) {
			Event firstCongestionPack = new Event(EventType.CongestionPacketIntoRouter); // Evento "Pacote de Tráfego de Fundo chegar ao roteador"
			firstCongestionPack.setTime(r.generateExponential(1.0 / congestionPacketFrequency));
			events.add(firstCongestionPack);
		}
		
		// Prepara os servidores para transmissão de Tx-Rx.
		for(Tx tx : system.getTxs()) {
			double propagationDelay = setPropagationDelay(tx);
			double randTime = r.generateDouble() * 150; // Fator aleatório além da propagação e da transmissão.
			
			Event packSent = new Event(EventType.TxPacketHeadsToRouter); // Evento "Pacote sair do Tx"
			packSent.setTime(randTime + transmissionTime);
			packSent.setTxIndex(tx.getIndex());
			events.add(packSent);
			
			Pack pack = tx.sendPacket(randTime);
			Event firstTcpPack = new Event(EventType.TxPacketIntoRouter); // Evento "Pacote TCP chegar ao roteador"
			firstTcpPack.setTime(randTime + transmissionTime + propagationDelay);
			firstTcpPack.setPack(pack);
			events.add(firstTcpPack);
			
			Event possibleTimeout = new Event(EventType.Timeout); // Evento "Possível Timeout do pacote"
			possibleTimeout.setTime(randTime + tx.getRTO());
			possibleTimeout.setTxIndex(tx.getIndex());
			pack.setTimeout(possibleTimeout);
			events.add(possibleTimeout);
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
		
		for(Rx rx: system.getRxs())
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
			
			for (int i = 0; i < SimulationProperties.getEventsInARow(); i++)
				handleEvents();
			
			for(int i = 0 ; i < system.getRxs().size() ; i++) {
				double a = system.getRxs().get(i).getNextExpectedByte() - transientPhaseData.getExpectedBytes().get(i);
				double b = currentTime - transientPhaseData.getTransientPhaseEndingTime();
				statisticsPerTx.get(i).addSample((a/b) * (8/1E-3));
			}
		}
	}
	
	/**
	 * Retira o próximo evento da fila de prioridades, e o trata.
	 */
	public void handleEvents() {
		// Não é esperado que esta lista esteja vazia...
		if(events.isEmpty()) throw new RuntimeException();
		
		// Evento é retornado e deletado da fila.
		Event event = events.poll();
		
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
		// TODO Auto-generated method stub
		
	}

	private void handleTxPacketHeadsToRouter(Event event) {
		Tx tx = system.getTxs().get(event.getTxIndex());
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
		events.add(endOfTransmition);
		
		Event routerReceivesTcp = new Event(EventType.TxPacketIntoRouter);
		routerReceivesTcp.setTime(currentTime + transmissionTimeMs + propagation);
		routerReceivesTcp.setPack(nextTcpPack);
		events.add(routerReceivesTcp);
		
		Event timeout = new Event(EventType.Timeout);
		timeout.setTime(currentTime + tx.getRTO());
		timeout.setTxIndex(tx.getIndex());
		nextTcpPack.setTimeout(timeout);
		events.add(timeout);
		
		if(tx.getNotDeliveredPacks().contains(nextTcpPack)) {
			int index = tx.getNotDeliveredPacks().indexOf(nextTcpPack);
			Pack p = tx.getNotDeliveredPacks().get(index);
			events.remove(p.getTimeout());
			tx.getNotDeliveredPacks().remove(index);
		}
		
		tx.getNotDeliveredPacks().add(nextTcpPack);
	}

	private void handleRouterSuccessfullySentPacket(Event event) {
		// TODO Auto-generated method stub
		
	}

	private void handleTimeout(Event event) {
		// TODO Auto-generated method stub
		
	}

	private void handleTxPacketIntoRouter(Event event) {
		// TODO Auto-generated method stub
		
	}

	private void handleCongestionPacketIntoRouterEvent(Event event) {
		// TODO Auto-generated method stub
	}
}
