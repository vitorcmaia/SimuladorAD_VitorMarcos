package network;

import maths.SimulationProperties;
import enums.PackType;

/**
 * Pacote, que é transmitido de um Tx para um Rx através de um roteador.
 */
public final class Pack {
	/**
	 * Tipo do pacote, que pode ser tráfego de fundo ou TCP comum.
	 */
	private PackType type;
	
	private Long startingByte;
	
	private Long endingByte;
	
	/**
	 * Momento em que o pacote teve seu envio iniciado no Tx.
	 */
	private Double startSendingTime;
	
	/**
	 * Destino do pacote. Existem n Tx e n Rx. Se um pacote veio do Tx k, ele deve ir para o Rx k.
	 */
	private Integer destination = -1;
	
	/**
	 * Informa o tipo do pacote.
	 * @return Tipo do pacote.
	 */
	public PackType getType() {
		return type;
	}
	
	/**
	 * Construtor padrão do pacote. Devo informar o tipo.
	 * @param type Tipo de origem do pacote
	 * @param destination Destino do pacote.
	 */
	public Pack(PackType type, Integer destination) {
		this.type = type;
		if(type == PackType.Congestion)
			this.destination = -1; // Não vai ser usado.
		else
			this.destination = destination;
		
		this.startingByte = 0L;
		this.endingByte = SimulationProperties.getMSS() - 1;
	}
	
	public Pack(PackType type, Integer destination, Long start, Long end) {
		this.type = type;		
		if(type == PackType.Congestion)
			this.destination = -1; // Não vai ser usado.
		else
			this.destination = destination;
		
		this.startingByte = start;
		
		if( (end - start + 1) != SimulationProperties.getMSS() )
			throw new RuntimeException();
		
		this.endingByte = end;
	}
	
	public Integer getDestination() {
		return destination;
	}

	public Long getStartingByte() {
		return startingByte;
	}

	public void setStartingByte(Long startingByte) {
		this.startingByte = startingByte;
	}

	public Long getEndingByte() {
		return endingByte;
	}

	public void setEndingByte(Long endingByte) {
		this.endingByte = endingByte;
	}

	public Double getStartSendingTime() {
		return startSendingTime;
	}

	public void setStartSendingTime(Double startSendingTime) {
		this.startSendingTime = startSendingTime;
	}
}
