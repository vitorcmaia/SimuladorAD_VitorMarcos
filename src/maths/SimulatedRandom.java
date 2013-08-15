package maths;

import java.util.Random;

/**
 * Gera números aleatórios, com base em distribuições exponencial e geométrica.
 */
public final class SimulatedRandom {
	
	/**
	 * Armazena o seed.
	 */
	private Long seed = null;
	
	/**
	 * Construtor. Seta apenas um seed para toda a rodada de simulação.
	 */
	public SimulatedRandom() {
		seed = new Random().nextLong();
	}
	
	/**
	 * Gera um numero com distribuição geométrica, dada uma probabilidade Bernoulli de sucesso.
	 * Este número é arredondado para inteiro, pois o resultado é discreto.
	 * @param p Probabilidade de sucesso.
	 * @return O número gerado.
	 */
	public double generateGeometric(double p) {
		return Math.ceil( Math.log(1 - generateDouble()) / Math.log(1 - p) );
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
	 * Gera um ponto flutuante aleatório entre 0 e 1.
	 * @return O número gerado.
	 */
	public Double generateDouble() {
		return new Random(seed).nextDouble();
	}
	
	/**
	 * Informa o último seed gerado.
	 * @return O seed da última inst�ncia.
	 */
	public Long getSeed() {
		return seed;
	}
}
