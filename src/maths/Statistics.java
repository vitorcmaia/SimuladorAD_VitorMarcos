package maths;

import umontreal.iro.lecuyer.probdist.StudentDist;

/**
 * Calcula estatísticas básicas, dado um conjunto de amostras.
 * Estima média, variância, desvio padrão e IC.
 */
public final class Statistics {
	
	/**
	 * É possível calcular a média incrementalmente. A cada amostra adicionada,
	 * mantém-se este somatório parcial delas, e então o cálculo da média é simplesmente
	 * este valor dividido pelo número de amostras, sem necessidade de loops.
	 */
	private double partialSumOfSamples = 0.0;
	
	/**
	 * É possível calcular a variância incrementalmente. A cada amostra adicionada,
	 * calcula-se este somatório dos quadrados das amostras, e este valor é usado
	 * para calcular o valor da variância sem loops.
	 */
	private double partialSquareOfSamples = 0.0;
	
	/**
	 * Armazena a quantidade de amostras. Não guarda as amostras em si, pois estes valores
	 * são irrelevante separados. A intenção do algoritmo é não guardar vetor aqui, para
	 * não sobrecarregar a memória.
	 */
	private int quantityOfSamples = 0;
	
	/**
	 * Informa a quantidade de amostras.
	 * @return Quantidade de amostras.
	 */
	public int getQuantityOfSamples() {
		return quantityOfSamples;
	}
	
	/**
	 * Adiciona as amostras, e já atualiza os parâmetros necessários no cálculo de estatísticas.
	 * @param sample Nova amostra
	 */
	public void addSample(Double sample) {
		quantityOfSamples++;
		partialSumOfSamples += sample;
		partialSquareOfSamples += Math.pow(sample,2);
	}
	
	/**
	 * Calcula a média das amostras. Divide o somatório parcial pelo número de amostras.
	 * @return Média das amostras.
	 * @throws RuntimeException Quando pede-se a média de nenhuma amostra.
	 */
	public Double estimateAverage() throws RuntimeException {
		if(getQuantityOfSamples() == 0)
			throw new RuntimeException();
		return partialSumOfSamples / getQuantityOfSamples();
	}
	
	/**
	 * Calcula a variância das amostras. Assim como a média, é calculado incrementalmente.
	 * Faz a seguinte conta (quads/n-1) - ((sum*sum)/((n-1)*n))
	 * @return A variância das amostras.
	 * @throws RuntimeException Quando pede-se a variância de menos de duas amostras.
	 */
	public Double estimateVariance() throws RuntimeException {
		if(getQuantityOfSamples() == 0 || getQuantityOfSamples() == 1)
			throw new RuntimeException();
		
		return  (partialSquareOfSamples/(getQuantityOfSamples() - 1)) -
				((Math.pow(partialSumOfSamples,2))/((getQuantityOfSamples() - 1) * getQuantityOfSamples()));
	}
	
	/**
	 * Calcula o desvio padrão das amostras. É apenas a raiz da variância.
	 * @return O desvio padrão das amostras.
	 */
	public Double estimateStandardDeviation() {
		return Math.sqrt(estimateVariance());
	}
	
	/**
	 * Calcula a distância entre a média e um dos limites. 
	 * @param confidence Confiança desejada.
	 * @return A distância entre a média e os limites.
	 * @throws RuntimeException Caso existam menos de duas amostras. 
	 */
	public double getAverageConfidenceIntervalDistance(Double confidence) throws RuntimeException {
		if(getQuantityOfSamples() == 0 || getQuantityOfSamples() == 1)
			throw new RuntimeException();
		
		Double a = 1 - confidence;
		// Uso uma biblioteca externa.
		Double p = StudentDist.inverseF(getQuantityOfSamples() - 1, 1 - a / 2);
		
		return p * Math.sqrt(estimateVariance() / getQuantityOfSamples());
	}
}
