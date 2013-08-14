package network;

import java.util.ArrayList;

import enums.EventType;

public class SACK {
	
	/**
	 * Início do próximo pacote esperado.
	 */
	private long nextExpectedByte;
	
	/**
	 * Tx que deve receber este SACK.
	 */
	private Integer destination;
	
	private double originalPackSendingTime;
	
	/**
	 * Possibilidade de timeout.
	 */
	private Event timeout;
	
	/**
	 * Sequencias recebidas corretamente, que permitem inferir os pacotes perdidos.
	 * Estes dados devem ser entregues ao Tx.
	 */
	private ArrayList<ArrayList<Long>> sequences;
	
	public SACK(Integer destination, Long nextExpectedByte,
			ArrayList<ArrayList<Long>> allReceivedSequences) {
		this.nextExpectedByte = nextExpectedByte;
		this.destination = destination;
		this.sequences = allReceivedSequences;
	}

	public long getNextExpectedByte() {
		return nextExpectedByte;
	}

	public void setNextexpectedByte(long nextexpectedByte) {
		this.nextExpectedByte = nextexpectedByte;
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

	public double getOriginalPackSendingTime() {
		return originalPackSendingTime;
	}

	public void setOriginalPackSendingTime(double originalPackSendingTime) {
		this.originalPackSendingTime = originalPackSendingTime;
	}
	
	/**
	 * Equals reescrito para o SACK.
	 */
	public boolean equals(Object o) {
		// Se não for instância de SACK, não faz sentido comparar os dados.
		if( !(o instanceof SACK) ) return false;
		
		SACK s = (SACK) o;
		
		// Se os destinos não forem os mesmos, os SACKs são diferentes.
		if(!s.getDestination().equals(getDestination())) return false;
		
		// Se os valores do próximo byte esperado forem diferentes, os SACKs são diferentes.
		if(s.getNextExpectedByte() != getNextExpectedByte()) return false;
		
		// Se as sequências de ambos os SACKs forem nulas, sendo que destino e próximo byte são os mesmos,
		// então os SACKs são iguais.
		if(s.getSequences() == null && getSequences() == null) return true;
		
		// Uma matriz nula e a outra vazia também podem ser vistos como iguais.
		if(s.getSequences() == null && getSequences().size() == 0) return true;
		
		// Uma matriz nula e a outra vazia também podem ser vistos como iguais.
		if(getSequences() == null && s.getSequences().size() == 0) return true;
		
		// Se uma matriz for nula e a outra tiver elementos, elas são diferentes.
		if(s.getSequences() == null && getSequences().size() > 0) return false;
		if(getSequences() == null && s.getSequences().size() > 0) return false;
		
		if(s.getSequences().size() != getSequences().size()) return false;
		
		for(int i = 0; i < getSequences().size() ; i++) {
			// As linhas devem ter o mesmo tamanho, e além disso, devem ter tamanho 2.
			// Se não há dois elementos por linha, então há algum bug sério no código.
			if(s.getSequences().get(i).size() != 2) throw new RuntimeException();
			if(  getSequences().get(i).size() != 2) throw new RuntimeException();
			
			// Comparo cada elemento da matriz.
			for(int j = 0; j < getSequences().get(i).size(); j++) {
				if(!getSequences().get(i).get(j).equals(s.getSequences().get(i).get(j))) return false;
			}
		}
		
		return true;
	}
	
	public Event getTimeout() {
		return timeout;
	}
	
	/**
	 * Seta o evento de timeout do sack.
	 * @param timeout Evento de Timeout.
	 */
	public void setTimeout(Event timeout) {
		if(!timeout.getType().equals(EventType.Timeout))
			throw new RuntimeException();
		this.timeout = timeout;
	}
}
