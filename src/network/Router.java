package network;

import java.util.ArrayList;

import maths.SimulatedRandom;
import maths.SimulationProperties;

import enums.PackType;
import enums.RouterType;

/**
 * Representa um roteador, com um Buffer de pacotes que pode funcionar como FIFO ou RED.
 */
public final class Router {
	/**
	 * Tipo de tratamento de pacotes do roteador, que pode ser FIFO ou RED.
	 */
	private RouterType type;
	
	/**
	 * Os receptores do sistema.
	 */
	private ArrayList<Rx> Rxs;
	
	/**
	 * Atributos usados no roteador RED.
	 */
	private REDAttributes redAttributes;
	
	/**
	 * Fila de pacotes.
	 */
	private Buffer buffer = new Buffer();
	
	/**
	 * Construtor, seta apenas o tipo, e as variáveis relacionadas ao RED.
	 * @param type Tipo do roteador.
	 */
	public Router(RouterType type) {
		this.type = type;
		redAttributes = new REDAttributes();
	}
	
	/**
	 * Trata a chegada de um pacote no roteador. 
	 * @param pack Pacote que chegou.
	 * @param arrivalTime Tempo de chegada do pacote.
	 * @return true se o pacote não foi descartado.
	 * @throws RuntimeException Se o tipo do roteador não for FIFO nem RED.
	 */
	public Boolean receivePacket(Pack pack, Double arrivalTime) throws RuntimeException {
		switch (type) {
			case FIFO:
				return receivePacketFIFO(pack);
			case RED:
				return receivePacketRED(pack,arrivalTime);
			default:
				throw new RuntimeException();
		}
	}
	
	/**
	 * Envia o próximo pacote para o Rx correspondente.
	 * Se for tráfego de fundo, simplesmente o remove do buffer.
	 * @param removalTime Momento de ocorrência.
	 * @return SACK resultante da transação do pacote atual.
	 * @throws RuntimeException Se a listagem de receptores for nula.
	 */
	public SACK sendPackToRx(Double removalTime) throws RuntimeException {
		// Se estiver nulo, significa que o construtor de Sys está incorreto.
		if(Rxs == null)
			throw new RuntimeException();
		
		SACK sack = null;
		Pack nextPack = getBuffer().pop();		
		if(nextPack.getType() != PackType.Congestion) // Tráfego de fundo se perde.
			sack = Rxs.get(nextPack.getDestination()).receivePacket(nextPack);
		if(getBuffer().quantityOfRemainingPackets() == 0 && type == RouterType.RED) {
			redAttributes.setIdleIntervalStart(removalTime);
		}
		
		return sack;
	}
	
	/**
	 * Atualiza os parâmetros de média do RED, a cada novo pacote.
	 * @param currentTime Tempo atual da simulação.
	 */
	private void updateRED(double currentTime) {
		if (getBuffer().quantityOfRemainingPackets().equals(0)) {
			redAttributes.setIdleIntervalEnd(currentTime);
			redAttributes.getIdleIntervalStatistics().addSample(
					redAttributes.getIdleIntervalEnd() - redAttributes.getIdleIntervalStart());

			double transTime = SimulationProperties.getMSS() / SimulationProperties.getCg();
			redAttributes.setM(
					Math.round(redAttributes.getIdleIntervalStatistics().estimateAverage() / transTime));
		}
		
		if (getBuffer().quantityOfRemainingPackets().compareTo(0) > 0) {
			redAttributes.setAverage(
					(1 - redAttributes.getWq()) * redAttributes.getAverage() +
					redAttributes.getWq() * getBuffer().quantityOfRemainingPackets());
		} else {
			redAttributes.setAverage(
					Math.pow(1 - redAttributes.getWq(), redAttributes.getM()) * redAttributes.getAverage());
		}
	}
	
	/**
	 * Comportamento do roteador com a política RED.
	 * Pacotes novos podem ser descartados antes do buffer lotar,
	 * com base em um valor de média definido.
	 * @param pack Novo pacote.
	 * @param arrivalTime Tempo de chegada do pacote.
	 * @return true se o pacote foi adicionado ao buffer.
	 */
	private Boolean receivePacketRED(Pack pack, Double arrivalTime) {
		updateRED(arrivalTime);
		
		if(getBuffer().quantityOfRemainingPackets().compareTo(getBuffer().getBufferSize()) >= 0) {
			redAttributes.setQuantityOfNotDiscardedPacks(0);
			return false;
		}
		
		if(redAttributes.getAverage() < redAttributes.getMinth()) {
			getBuffer().addPacket(pack);
			redAttributes.incrementQuantityOfNotDiscardedPacks();
			return true;
		}
		
		if(redAttributes.getAverage() > redAttributes.getMaxth()) {
			redAttributes.setQuantityOfNotDiscardedPacks(0);
			return false;
		}
		
		SimulatedRandom random = new SimulatedRandom();
		
		if(random.generateDouble() < redAttributes.Pa()) {
			redAttributes.setQuantityOfNotDiscardedPacks(0);
			return false;
		} else {
			getBuffer().addPacket(pack);
			redAttributes.incrementQuantityOfNotDiscardedPacks();
			return true;
		}
	}
	
	/**
	 * Comportamento do roteador para o caso FIFO, na chegada de pacote.
	 * Pacotes chegam e entram no fim da fila, até que o tamanho máximo
	 * do buffer seja alcançado. A partir de então, os pacotes são descartados.
	 * @param pack Pacote que chegou.
	 * @return true se não foi descartado.
	 */
	private Boolean receivePacketFIFO(Pack packet) {
		if(getBuffer().quantityOfRemainingPackets().compareTo(getBuffer().getBufferSize()) < 0) {
			getBuffer().addPacket(packet);
			return true;
		}
		return false;
	}
	
	/**
	 * Acessa o buffer.
	 * @return O buffer de pacotes do roteador.
	 */
	public Buffer getBuffer() {
		return buffer;
	}

	/**
	 * Seta os receptores ligados ao roteador.
	 * @param rxs Lista de receptores
	 */
	public void setRxs(ArrayList<Rx> rxs) {
		Rxs = rxs;
	}
	
	public REDAttributes getRedAttributes() {
		return redAttributes;
	}	
}