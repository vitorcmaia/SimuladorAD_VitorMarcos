package maths;

import java.util.Random;

/**
 * Gera números aleatórios, com base em distribuições.
 */
public final class SimulatedRandom {
	
	/**
	 * Armazena o último seed.
	 */
	private static Long seed = 0L;
	
	/**
	 * Seed desta instância.
	 */
	private Long thisSeed = 0L;
	
	/**
	 * Construtor. O seed é escolhido de modo aleatório, mas seu sorteio
	 * é baseado no seed da última instância de SimulatedRandom.
	 * Um seed deve ser ao menos 1000000000 maior ou menor que o anterior.
	 */
	public SimulatedRandom() {
		Long seed;
		Long dist;
		do {
			seed = new Random().nextLong();
			dist = Math.abs(seed - SimulatedRandom.getSeed());
		} while(dist < 1000000000); // 1000000000 é Arbitrário.
		SimulatedRandom.seed = seed;
		thisSeed = seed;
	}
	
	/**
	 * Gera um inteiro aleatório menor do que o limite do tipo int.
	 * @return O número gerado.
	 */
	public Integer generateInteger() {
		return new Random(thisSeed).nextInt();
	}
	
	/**
	 * Gera um ponto flutuante aleatório entre 0 e 1.
	 * @return O número gerado.
	 */
	public Double generateDouble() {
		return new Random(thisSeed).nextDouble();
	}
	
	/**
	 * Gera um número com distribuição exponencial. É usada a inversa da cdf da distribuição.
	 * @param rate Taxa da distribuição.
	 * @return O número gerado.
	*/
	public double generateExponential(Double rate) {
		return - Math.log(1 - generateDouble()) / rate;
	}
	
	/**
	 * Gera um número com distribuição geométrica,
	 * dada uma probabilidade Bernoulli de sucesso.
	 * @param p Probabilidade de sucesso.
	 * @return O número gerado.
	 */
	public double generateGeometric(double p) {
		return Math.ceil( Math.log(1 - generateDouble()) / Math.log(1 - p) );
	}

	/**
	 * Informa o último seed gerado.
	 * @return O seed da última instância.
	 */
	public static Long getSeed() {
		return seed;
	}
}
