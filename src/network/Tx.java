package network;

import java.util.ArrayList;

import maths.SimulationProperties;

import enums.Group;
import enums.PackType;

public class Tx {
	
	/**
	 * Índice do Tx, usado para o roteador saber qual Rx deve receber um pacote.
	 * O Tx k deve ter seus pacotes entregues no Rx k.
	 */
	private Integer index;
	
	/**
	 * Grupo do Tx, que pode ser 1 ou 2.
	 */
	private Group group;
	
	/**
	 * Matriz de pacotes recebidos do Rx, que permite inferir quais pacotes não chegaram.
	 */
	private ArrayList<ArrayList<Long>> receivedSequencesFromRx = null;
	
	/**
	 * Se algum pacote está em transmissão.
	 */
	private boolean isTransmiting = false;
	
	/**
	 * Indica se o TxTCP está em estado de Fast Retransmit.
	 * Caso a confirmação de chegada de um pacote não ocorra
	 * 3 ocorrências de Sack, inicia-se o estado de fast-retransmit,
	 * para reenviar o provável pacote perdido.
	 */
	private boolean isFastRetransmit = false;
	
	/**
	 * Desvio médio do rtt.
	 */
	private double RttDeviation = 0;
	
	/**
	 * Atraso de transmissão.
	 */
	private double rtt = 0;
	
	/**
	 * Janela de congestionamento.
	 */
	private long congestionWindow = SimulationProperties.getMSS();

	/**
	 * Janela de recuperação.
	 */
	private long retransmitionWindow = SimulationProperties.getMSS();
	
	private long threshold = SimulationProperties.getThreshold();

	/**
	 * Informa qual dos pacotes que ainda não chegaram é o mais antigo.
	 */
	private long oldestNotReceivedPacket = 0;

	/**
	 * Informa qual o próximo pacote que deve ser enviado.
	 */
	private long nextPacketToSend = 0;

	/**
	 * Quantidade de SACKS recebidos numa mesma sessão (mesmo valor da congestion window).
	 */
	private long quantityOfReceivedSacks = 0;

	/**
	 * Conta acks duplicados para ativar o Fast Retransmit.
	 */
	private int duplicatedAcksCounter = 0;

	/**
	 * Próximo byte esperado do Ack duplicado anterior.
	 */
	private long lastDuplicatedACK = -1;
	
	public Tx(Group group, Integer index) {
		this.group = group;
		this.index = index;
	}
		
	/**
	 * Informa o grupo do Tx.
	 * @return Grupo do Tx.
	 */
	public Group getGroup() {
		return group;
	}
	
	/**
	 * Informa o índice do Tx.
	 * @return  Índice do Tx.
	 */
	public Integer getIndex() {
		return index;
	}

	public double getRtt() {
		return rtt;
	}

	public void setRtt(double rtt) {
		this.rtt = rtt;
	}

	public double getRttDeviation() {
		return RttDeviation;
	}

	public void setRttDeviation(double rttDeviation) {
		RttDeviation = rttDeviation;
	}
	
	/**
	 * Envia pacotes para o roteador.
	 * @param sendingTime Tempo de envio.
	 * @return O pacote a ser enviado.
	 */
	public Pack sendPacket(double sendingTime) {
		if (!(nextPacketToSend < oldestNotReceivedPacket + congestionWindow)) throw new RuntimeException();

		Pack pack = new Pack(PackType.CommonTCP, index);
		pack.setStartSendingTime(sendingTime);		
		if(receivedSequencesFromRx == null || receivedSequencesFromRx.isEmpty()) {
			pack.setStartingByte(nextPacketToSend);
			pack.setEndingByte(nextPacketToSend + SimulationProperties.getMSS() - 1);
			nextPacketToSend += SimulationProperties.getMSS();
			return pack;
		} else
			return handleLostPacket(pack);
	}

	private Pack handleLostPacket(Pack pack) {
		if (receivedSequencesFromRx.get(receivedSequencesFromRx.size() - 1).get(1) <= nextPacketToSend) {
			pack.setStartingByte(nextPacketToSend);
			pack.setEndingByte(nextPacketToSend + SimulationProperties.getMSS() - 1);
			nextPacketToSend += SimulationProperties.getMSS();
			return pack;
		} else {
			for(ArrayList<Long> pair : receivedSequencesFromRx) {
				if (nextPacketToSend >= pair.get(0) && nextPacketToSend <= pair.get(1)) {
					nextPacketToSend = pair.get(1);
					pack.setStartingByte(nextPacketToSend);
					pack.setEndingByte(nextPacketToSend + SimulationProperties.getMSS() - 1);
					nextPacketToSend += SimulationProperties.getMSS();
					return pack;
				}
			}
			pack.setStartingByte(nextPacketToSend);
			pack.setEndingByte(nextPacketToSend + SimulationProperties.getMSS() - 1);
			nextPacketToSend += SimulationProperties.getMSS();
			return pack;
		}
	}

	/**
	 * Recebe o SACK do Rx e o trata, de acordo com o estado de Fast Retransmit.
	 * @param sack Sack recebido.
	 * @param receiveTime Tempo de recebimento.
	 */
	public void receiveSack(SACK sack, double receiveTime) {
		updateRTT(sack.getStarterSendingTime(), receiveTime);
		setReceivedSequencesFromRx(sack.getSequences());
		if(fastRetransmitIsOn())
			fastRetransmitActions(sack, receiveTime);
		else
			notFastRetransmitActions(sack, receiveTime);
	}
	
	/**
	 * Ações para o caso de Fast Retransmit ativado.
	 * @param sack Sack recebido do Rx.
	 * @param receiveTime Tempo de recebimento do Sack.
	 */
	private void fastRetransmitActions(SACK sack, double receiveTime) {
		congestionWindow += SimulationProperties.getMSS();

		if (sack.getNextExpectedByte() > oldestNotReceivedPacket) {
			oldestNotReceivedPacket = sack.getNextExpectedByte();
			if (oldestNotReceivedPacket > nextPacketToSend)
				nextPacketToSend = oldestNotReceivedPacket;
		}
		
		if (sack.getNextExpectedByte() >= retransmitionWindow) {
			setFastRetransmit(false);
			duplicatedAcksCounter = 0;
			congestionWindow = threshold;
			quantityOfReceivedSacks = 0;
		}
	}

	/**
	 * Ações para o caso de Fast Retransmit desativado.
	 * @param sack Sack recebido do Rx.
	 * @param receiveTime Tempo de recebimento do Sack.
	 */
	private void notFastRetransmitActions(SACK sack, double receiveTime) {
		if(congestionWindow < threshold)
			congestionWindow += SimulationProperties.getMSS();
		else {
			quantityOfReceivedSacks++;
			if(quantityOfReceivedSacks * SimulationProperties.getMSS() >= congestionWindow) {
				quantityOfReceivedSacks = 0;
				congestionWindow += SimulationProperties.getMSS();
			}
		}
		if (sack.getNextExpectedByte() == oldestNotReceivedPacket) {
			duplicatedAck(sack);
		} else if (sack.getNextExpectedByte() > oldestNotReceivedPacket) {
			oldestNotReceivedPacket = sack.getNextExpectedByte();
			
			if (oldestNotReceivedPacket > nextPacketToSend)
				nextPacketToSend = oldestNotReceivedPacket;
		}
	}
	
	/**
	 * Trata a duplicação de Ack.
	 * @param sack SACK recebido do RX
	 */
	private void duplicatedAck(SACK sack) {
		if (lastDuplicatedACK == sack.getNextExpectedByte()) {
			duplicatedAcksCounter++;
			if (duplicatedAcksCounter == 3) {
				recalculateThreshold();
				setFastRetransmit(true);
				if (sack.getSequences() != null && sack.getSequences().size() != 0)
					retransmitionWindow = sack.getSequences().get(sack.getSequences().size() - 1).get(1);
				else
					retransmitionWindow = nextPacketToSend;

				nextPacketToSend = oldestNotReceivedPacket;
			}
		} else {
			lastDuplicatedACK = sack.getNextExpectedByte();
			duplicatedAcksCounter = 1;
		}
	}
	
	/**
	 * Recalcula o Threshold e a Congestion Window de acordo com
	 * a expressão mostrada na sessão 3.3.2 do enunciado.
	 */
	private void recalculateThreshold() {
		threshold = congestionWindow / 2;
		congestionWindow = threshold + 3 * SimulationProperties.getMSS();
	}
	
	public void handleTimeOutEvent() {
		setFastRetransmit(false);
		nextPacketToSend = oldestNotReceivedPacket;
		duplicatedAcksCounter = 0;
		quantityOfReceivedSacks = 0;
		threshold = congestionWindow / 2;
		congestionWindow = SimulationProperties.getMSS();
	}
	
	/**
	 * Atualiza valor do rtt.
	 * @param sendTime Tempo de envio
	 * @param arrivalTime Tempo de chegada.
	 */
	private void updateRTT(double sendTime, double arrivalTime) {
		double delta = arrivalTime - sendTime - rtt;
		RttDeviation = RttDeviation + 0.25 * (Math.abs(delta) - RttDeviation);
		rtt = rtt + 0.125 * delta;
	}
	
	/**
	 * Usado no agendamento de Timeout.
	 * @return estimativa do RTO
	 */
	public double getRTO() {
		return rtt + 4 * RttDeviation;
	}

	public boolean isTransmiting() {
		return isTransmiting;
	}

	public void setTransmiting(boolean isTransmiting) {
		this.isTransmiting = isTransmiting;
	}
	
	/**
	 * Pacotes cuja entrega não aconteceu. São usados diretamente no contexto da simulação.
	 */
	private ArrayList<Pack> notDeliveredPacks = new ArrayList<Pack>();
	
	public ArrayList<Pack> getNotDeliveredPacks() {
		return notDeliveredPacks;
	}

	public void setNotDeliveredPacks(ArrayList<Pack> notDeliveredPacks) {
		this.notDeliveredPacks = notDeliveredPacks;
	}

	public ArrayList<ArrayList<Long>> getReceivedSequencesFromRx() {
		return receivedSequencesFromRx;
	}

	public void setReceivedSequencesFromRx(ArrayList<ArrayList<Long>> receivedSequencesFromRx) {
		this.receivedSequencesFromRx = receivedSequencesFromRx;
	}
	
	public long getCongestionWindow() {
		return congestionWindow;
	}
	
	public long getThreshold() {
		return threshold;
	}
	
	public long getNextPacketToSend() {
		return nextPacketToSend;
	}
	
	public long getOldestNotReceivedPacket() {
		return oldestNotReceivedPacket;
	}

	public boolean fastRetransmitIsOn() {
		return isFastRetransmit;
	}

	public void setFastRetransmit(boolean isFastRetransmit) {
		this.isFastRetransmit = isFastRetransmit;
	}
}
