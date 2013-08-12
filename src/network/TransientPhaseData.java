package network;

import java.util.ArrayList;

/**
 * Dados que devem ser retornados da fase transiente.
 */
public class TransientPhaseData {

	double transientPhaseEndingTime = 0.0;
	ArrayList<Long> expectedBytes = new ArrayList<Long>();
	
	public TransientPhaseData(double transientPhaseEndingTime, ArrayList<Long> expectedBytes) {
		this.transientPhaseEndingTime = transientPhaseEndingTime;
		this.expectedBytes = expectedBytes;
	}
	
	public double getTransientPhaseEndingTime() {
		return transientPhaseEndingTime;
	}

	public void setTransientPhaseEndingTime(double transientPhaseEndingTime) {
		this.transientPhaseEndingTime = transientPhaseEndingTime;
	}

	public ArrayList<Long> getExpectedBytes() {
		return expectedBytes;
	}

	public void setExpectedBytes(ArrayList<Long> expectedBytes) {
		this.expectedBytes = expectedBytes;
	}
}
