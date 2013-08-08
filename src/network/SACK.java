package network;

import java.util.ArrayList;

public class SACK {
	
	/**
	 * Início do próximo pacote esperado.
	 */
	private Long nextexpectedByte;
	
	/**
	 * Tx que deve receber este SACK.
	 */
	private Integer destination;
	
	private Double originalPackSendingTime;
	
	/**
	 * Sequencias recebidas corretamente, estes dados devem ser entregues ao Tx.
	 */
	private ArrayList<ArrayList<Long>> sequences;
	
	public SACK(Integer destination, Long nextExpectedByte,
			ArrayList<ArrayList<Long>> allReceivedSequences) {
		this.nextexpectedByte = nextExpectedByte;
		this.destination = destination;
		this.sequences = allReceivedSequences;
	}

	public Long getNextExpectedByte() {
		return nextexpectedByte;
	}

	public void setNextexpectedByte(Long nextexpectedByte) {
		this.nextexpectedByte = nextexpectedByte;
	}

	public Integer getDestination() {
		return destination;
	}

	public void setDestination(Integer destination) {
		this.destination = destination;
	}

	public ArrayList<ArrayList<Long>> getSequences() {
		return sequences;
	}

	public void setSequences(ArrayList<ArrayList<Long>> sequences) {
		this.sequences = sequences;
	}

	public Double getOriginalPackSendingTime() {
		return originalPackSendingTime;
	}

	public void setOriginalPackSendingTime(Double originalPackSendingTime) {
		this.originalPackSendingTime = originalPackSendingTime;
	}

}
