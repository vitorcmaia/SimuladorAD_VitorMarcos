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
	 * @return true se o pacote n�o foi descartado.
	 * @throws RuntimeException Se o tipo do roteador n�o for FIFO nem RED.
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
	 * Envia o pr�ximo pacote para o Rx correspondente.
	 * Se for tr�fego de fundo, simplesmente o remove do buffer.
	 * @param removalTime Momento de ocorr�ncia.
	 * @return SACK resultante da transa��o do pacote atual.
	 * @throws RuntimeException Se a listagem de receptores for nula.
	 */
	public SACK sendPackToRx(Double removalTime) throws RuntimeException {
		// Se estiver nulo, significa que o construtor de System est� incorreto.
		if(Rxs == null)
			throw new RuntimeException();
		
		Pack p = getBuffer().pop();
		
		SACK sack = null;
		if(p.getType() != PackType.Congestion) { // Tr�fego de fundo se perde.
			int dest = p.getDestination();
			sack = Rxs.get(dest).receivePacket(p);
		}
		return sack;
	}
	
	private Boolean receivePacketRED(Pack pack, Double arrivalTime) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Comportamento do roteador para o caso FIFO, na chegada de pacote.
	 * Pacotes chegam e entram no fim da fila, at� que o tamanho m�ximo
	 * do buffer seja alcan�ado. A partir de ent�o, os pacotes s�o descartados.
	 * @param pack Pacote que chegou.
	 * @return true se n�o foi descartado.
	 */
	private Boolean receivePacketFIFO(Pack packet) {
		if(getBuffer().quantityOfRemainingPackets() < getBuffer().getBufferSize()) {
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