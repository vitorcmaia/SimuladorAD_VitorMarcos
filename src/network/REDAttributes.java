package network;

import maths.Statistics;
import static org.junit.Assert.*;

/**
 * Atributos usados pelo roteador do tipo RED.
 * Como não são alteráveis, não adicionei à lista de parâmetros.
 * E não mantive na classe roteador propositalmente, para não
 * confundir quando o utilizado for o FIFO.
 */
public class REDAttributes {
	
	// Parâmetros definidos no enunciado.
	private int minth;
	private int maxth;
	private double wq;
	private double maxp;
	private long m;
	
	private int quantityOfNotDiscardedPacks;
	private double average;
	private double idleIntervalStart;
	private double idleIntervalEnd;
	
	private Statistics idleIntervalStatistics;
	
	public REDAttributes() {
		// Valores fixos.
		minth = 5;
		maxth = 15;
		wq = 0.002;
		maxp = 1.0 / 50.0;
		m = 0;
		
		quantityOfNotDiscardedPacks = 0;
		average = 0.0;
		idleIntervalEnd = idleIntervalStart = 0.0;
		setIdleIntervalStatistics(new Statistics());
	}
	
	public double Pa() {
		assertTrue((1-quantityOfNotDiscardedPacks*Pb()) != 0);
		return Pb()/(1-quantityOfNotDiscardedPacks*Pb());
	}

	public double Pb() {
		assertTrue(maxth != minth);
		return maxp*(average - minth)/(maxth-minth);
	}
	
	public double getIdleIntervalEnd() {
		return idleIntervalEnd;
	}

	public void setIdleIntervalEnd(double idleIntervalEnd) {
		this.idleIntervalEnd = idleIntervalEnd;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public double getIdleIntervalStart() {
		return idleIntervalStart;
	}

	public void setIdleIntervalStart(double idleIntervalStart) {
		this.idleIntervalStart = idleIntervalStart;
	}

	public int getQuantityOfNotDiscardedPacks() {
		return quantityOfNotDiscardedPacks;
	}

	public void setQuantityOfNotDiscardedPacks(int quantityOfNotDiscardedPacks) {
		this.quantityOfNotDiscardedPacks = quantityOfNotDiscardedPacks;
	}
	
	public void incrementQuantityOfNotDiscardedPacks() {
		quantityOfNotDiscardedPacks++;
	}

	public long getM() {
		return m;
	}

	public void setM(long m) {
		this.m = m;
	}

	public double getMaxp() {
		return maxp;
	}

	public void setMaxp(double maxp) {
		this.maxp = maxp;
	}

	public double getWq() {
		return wq;
	}

	public void setWq(double wq) {
		this.wq = wq;
	}

	public int getMinth() {
		return minth;
	}

	public void setMinth(int minth) {
		this.minth = minth;
	}

	public int getMaxth() {
		return maxth;
	}

	public void setMaxth(int maxth) {
		this.maxth = maxth;
	}

	public Statistics getIdleIntervalStatistics() {
		return idleIntervalStatistics;
	}

	private void setIdleIntervalStatistics(Statistics idleIntervalStatistics) {
		this.idleIntervalStatistics = idleIntervalStatistics;
	}
}
