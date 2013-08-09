package network;

import java.util.ArrayList;

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
	 * Fila de pacotes.
	 */
	private Buffer buffer = new Buffer();
	
	public Router(RouterType type) {
		this.type = type;
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
		
		Pack nextPack = getBuffer().pop();		
		if(nextPack.getType() != PackType.Congestion) // Tráfego de fundo se perde.
			return Rxs.get(nextPack.getDestination()).receivePacket(nextPack);
		else
			return null;
	}
	
	private Boolean receivePacketRED(Pack pack, Double arrivalTime) {
		// TODO Auto-generated method stub
		return null;
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

}