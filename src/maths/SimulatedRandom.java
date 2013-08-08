package maths;

import java.util.Random;

/**
 * Gera n�meros aleat�rios, com base em distribui��es.
 */
public final class SimulatedRandom {
	
	/**
	 * Armazena o �ltimo seed.
	 */
	private static Long seed = 0L;
	
	/**
	 * Seed desta inst�ncia.
	 */
	private Long thisSeed = 0L;
	
	/**
	 * Construtor. O seed � escolhido de modo aleat�rio, mas seu sorteio
	 * � baseado no seed da �ltima inst�ncia de SimulatedRandom.
	 * Um seed deve ser ao menos 1000000000 maior ou menor que o anterior.
	 */
	public SimulatedRandom() {
		Long seed;
		Long dist;
		do {
			seed = new Random().nextLong();
			dist = Math.abs(seed - SimulatedRandom.getSeed());
		} while(dist < 1000000000); // 1000000000 � Arbitr�rio.
		SimulatedRandom.seed = seed;
		thisSeed = seed;
	}
	
	/**
	 * Gera um inteiro aleat�rio menor do que o limite do tipo int.
	 * @return O n�mero gerado.
	 */
	public Integer generateInteger() {
		return new Random(thisSeed).nextInt();
	}
	
	/**
	 * Gera um ponto flutuante aleat�rio entre 0 e 1.
	 * @return O n�mero gerado.
	 */
	public Double generateDouble() {
		return new Random(thisSeed).nextDouble();
	}
	
	/**
	 * Gera um n�mero com distribui��o exponencial. � usada a inversa da cdf da distribui��o.
	 * @param rate Taxa da distribui��o.
	 * @return O n�mero gerado.
	*/
	public double generateExponential(Double rate) {
		return - Math.log(1 - generateDouble()) / rate;
	}
	
	/**
	 * Gera um n�mero com distribui��o geom�trica,
	 * dada uma probabilidade Bernoulli de sucesso.
	 * @param p Probabilidade de sucesso.
	 * @return O n�mero gerado.
	 */
	public double generateGeometric(double p) {
		return Math.ceil( Math.log(1 - generateDouble()) / Math.log(1 - p) );
	}

	/**
	 * Informa o �ltimo seed gerado.
	 * @return O seed da �ltima inst�ncia.
	 */
	public static Long getSeed() {
		return seed;
	}
}
