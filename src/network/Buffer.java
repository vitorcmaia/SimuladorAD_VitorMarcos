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
	 * Tamanho do buffer, edit�vel atrav�s da classe de propriedades.
	 */
	private Integer bufferSize = SimulationProperties.getBufferSize();
	
	/**
	 * Adiciona um pacote ao buffer. Adiciona infinitamente,
	 * sem considerar o tamanho do buffer. Este descarte deve ser feito
	 * pelo m�todo de tratamento de pol�tica.
	 * @param packet O novo pacote.
	 */
	public void addPacket(Pack packet) {
		queue.add(packet);
	}
	
	/**
	 * Retorna e remove o pr�ximo da fila de pacotes.
	 */
	public Pack pop() {
		if(queue.isEmpty()) return null;
		
		Pack p = queue.get(0);
		queue.remove(0);
		return p;
	}
	
	/**
	 * Quantidade de pacotes atualmente no Buffer.
	 * @return N�mero restante de pacotes.
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
