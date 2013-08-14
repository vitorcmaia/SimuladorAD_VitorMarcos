package network;

import enums.EventType;

/**
 * Eventos de simulação. O comportamento varia de acordo com o valor do atributo Type.
 * Implementa Comparable, pois a fila de prioridades de eventos da simulação depende
 * do método compareTo().
 */
public class Event implements Comparable<Event> {
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
	
	/**
	 * Sack enviado do Rx para o Tx correspondente.
	 */
	private SACK sack;
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

	public SACK getSack() {
		return sack;
	}

	public void setSack(SACK sack) {
		this.sack = sack;
	}

	@Override
	public int compareTo(Event event) {
		if(getTime() < event.getTime()) return -1;
		else if(getTime() > event.getTime()) return 1;
		else return 0; // Caso ocorram no mesmo tempo.
	}
}
