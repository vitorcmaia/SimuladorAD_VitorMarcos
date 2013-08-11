package network;

import java.util.ArrayList;

import maths.SimulationProperties;

/**
 * O Buffer do roteador.
 */
public final class Buffer {
	/**
	 * Armazena os pacotes.
	 */
	private ArrayList<Pack> queue = new ArrayList<Pack>();
	
	/**
	 * Tamanho do buffer, editável através da classe de propriedades.
	 */
	private Integer bufferSize = SimulationProperties.getBufferSize();
	
	/**
	 * Adiciona um pacote ao buffer. Adiciona infinitamente,
	 * sem considerar o tamanho do buffer. Este descarte deve ser feito
	 * pelo método de tratamento de política.
	 * @param packet O novo pacote.
	 */
	public void addPacket(Pack packet) {
		queue.add(packet);
	}
	
	/**
	 * Retorna e remove o próximo da fila de pacotes.
	 */
	public Pack pop() {
		if(queue.isEmpty()) return null;
		
		Pack p = queue.get(0);
		queue.remove(0);
		return p;
	}
	
	/**
	 * Quantidade de pacotes atualmente no Buffer.
	 * @return Número restante de pacotes.
	 */
	public Integer quantityOfRemainingPackets() {
		return queue.size();
	}
	
	/**
	 * Getter para o tamanho do buffer.
	 * @return O tamanho do Buffer do roteador.
	 */
	public Integer getBufferSize() {
		return bufferSize;
	}
}
