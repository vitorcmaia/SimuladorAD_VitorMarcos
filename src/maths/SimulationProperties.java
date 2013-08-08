package maths;

public final class SimulationProperties {
	
	/**
	 * Quantidade de pacotes que cabem no Buffer do roteador.
	 */
	private static Integer bufferSize = 40;
	
	/**
	 * Quantidade de sessões TCP de grupo 1.
	 */
	private static Integer quantityOfG1 = 10;
	
	/**
	 * Quantidade de sessões TCP de grupo 2.
	 */
	private static Integer quantityOfG2 = 10;
	
	/**
	 * Tamanho padrão de um pacote.
	 */
	private static Long MSS = 1500L;
	
	public static Integer getBufferSize() {
		return bufferSize;
	}

	public static void setBufferSize(Integer bufferSize) {
		SimulationProperties.bufferSize = bufferSize;
	}

	public static Integer getQuantityOfG1() {
		return quantityOfG1;
	}

	public static void setQuantityOfG1(Integer quantityOfG1) {
		SimulationProperties.quantityOfG1 = quantityOfG1;
	}

	public static Integer getQuantityOfG2() {
		return quantityOfG2;
	}

	public static void setQuantityOfG2(Integer quantityOfG2) {
		SimulationProperties.quantityOfG2 = quantityOfG2;
	}

	public static Long getMSS() {
		return MSS;
	}

	public static void setMSS(Long mSS) {
		MSS = mSS;
	}
}
