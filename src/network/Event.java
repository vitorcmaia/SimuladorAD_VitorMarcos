package network;

import enums.EventType;

public class Event {
	/**
	 * Identificador do evento.
	 */
	private EventType type;
	
	/**
	 * Momento de ocorrência do evento.
	 */
	private double time;
	
	//-------------------------------------------------------------
	// Atributos que variam com o evento.
	// Se um evento não usar certo atributo, ele será null ou zero.
	
	/**
	 * Índice do tx que está transmitindo um pacote.
	 */
	private int TxIndex;
	
	/**
	 * Pacote em transmissão de um Tx para o roteador.
	 */
	private Pack pack;
	//-------------------------------------------------------------
	
	/**
	 * Constrói um evento, que será tratado durante a simulação.
	 * @param type Identificador do evento.
	 */
	public Event(EventType type) {
		this.type = type;
	}

	/**
	 * Getter para o identificador do evento.
	 * @return Identificador do evento.
	 */
	public EventType getType() {
		return type;
	}
	
	/**
	 * Informa o momento de ocorrência.
	 * @return Momento de ocorrência.
	 */
	public double getTime() {
		return time;
	}
	
	/**
	 * Momento em que o evento ocorre.
	 * @param time Momento de ocorrência.
	 */
	public void setTime(double time) {
		this.time = time;
	}

	public int getTxIndex() {
		return TxIndex;
	}

	public void setTxIndex(int txIndex) {
		TxIndex = txIndex;
	}

	public Pack getPack() {
		return pack;
	}

	public void setPack(Pack pack) {
		this.pack = pack;
	}
}
