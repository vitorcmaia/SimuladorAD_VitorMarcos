package tests;

import static org.junit.Assert.*;
import model.Statistics;

import org.junit.Test;

import Enums.ICBound;

public class StatisticsTest {
	
	@Test (expected = RuntimeException.class)
	public void averageOfNoSamples() {
		Statistics statistics = new Statistics();
		assertEquals(statistics.estimateAverage(), 0, 0);
	}
	
	@Test
	public void averageOfOneSample() {
		Statistics statistics = new Statistics();
		statistics.addSample(30.0);
		assertEquals(statistics.estimateAverage(), 30, 0);
	}
	
	@Test
	public void averageOfTwoSamples() {
		Statistics statistics = new Statistics();
		statistics.addSample(100.0);
		statistics.addSample(200.0);
		
		Double average = statistics.estimateAverage();	
		assertEquals(average, 150, 0);
	}
	
	@Test
	public void averageOfFirstThousandNumbers() {
		Statistics statistics = new Statistics();
		for(Double i = 1.0 ; i <= 1000 ; i++) {
			statistics.addSample(i);
		}
		
		// 500.5 -> (((1+1000)*1000)/2) / 1000
		// -> Somatório de 1 a 1000 dividido por 1000
		assertEquals(statistics.estimateAverage(), 500.5, 0);
	}
	
	@Test (expected = RuntimeException.class)
	public void varianceOfNoSamples() {
		Statistics statistics = new Statistics();
		assertEquals(statistics.estimateVariance(), 0, 0);
	}
	
	@Test (expected = RuntimeException.class)
	public void varianceOfOneSample() {
		Statistics statistics = new Statistics();
		statistics.addSample(20.0);
		
		assertEquals(statistics.estimateVariance(), 0, 0);
	}
	
	@Test
	public void varianceOfTenSamples() {
		Statistics statistics = new Statistics();
		
		statistics.addSample(10.0);
		statistics.addSample(15.0);
		statistics.addSample(37.0);
		statistics.addSample(92.0);
		statistics.addSample(55.0);
		statistics.addSample(99.0);
		statistics.addSample(61.0);
		statistics.addSample(47.0);
		statistics.addSample(82.0);
		statistics.addSample( 1.0);
		
		Double avg = statistics.estimateAverage();
		Double variance = Math.pow(10-avg,2) + Math.pow(15-avg,2) + Math.pow(37-avg,2) + Math.pow(92-avg,2) + Math.pow(55-avg,2) +
						  Math.pow(99-avg,2) + Math.pow(61-avg,2) + Math.pow(47-avg,2) + Math.pow(82-avg,2) + Math.pow(1-avg,2);
		variance /= (10.0 - 1);
		
		assertEquals(variance, statistics.estimateVariance(), 0.001);
	}
	
	@Test (expected = RuntimeException.class)
	public void standardDeviationOfNoSamples() {
		Statistics statistics = new Statistics();
		statistics.estimateStandardDeviation();
	}
	
	@Test (expected = RuntimeException.class)
	public void standardDeviationOfOneSample() {
		Statistics statistics = new Statistics();
		statistics.addSample(10.0);
		statistics.estimateStandardDeviation();
	}
	
	@Test
	public void standardDeviationOfTenSample() {
		Statistics statistics = new Statistics();
		
		statistics.addSample( 5.0);
		statistics.addSample(49.0);
		statistics.addSample(36.0);
		statistics.addSample(98.0);
		statistics.addSample(31.0);
		statistics.addSample( 3.0);
		statistics.addSample(61.0);
		statistics.addSample(28.3);
		statistics.addSample(11.1);
		statistics.addSample(22.2);
		
		Double avg = statistics.estimateAverage();
		Double variance = Math.pow(5-avg,2) + Math.pow(49-avg,2) + Math.pow(36-avg,2) + Math.pow(98-avg,2) + Math.pow(31-avg,2) + 
				          Math.pow(3-avg,2) + Math.pow(61-avg,2) + Math.pow(28.3-avg,2) + Math.pow(11.1-avg,2) + Math.pow(22.2-avg,2);
		variance /= (10.0 - 1);
		Double deviation = Math.sqrt(variance);
		
		assertEquals(statistics.estimateStandardDeviation(), deviation, 0.001);
	}
	
	@Test (expected = RuntimeException.class)
	public void confidenceIntervalWithNoSamples() {
		Statistics statistics = new Statistics();
		statistics.ConfidenceIntervalDistance(0.90, ICBound.Lower);
	}
	
	@Test (expected = RuntimeException.class)
	public void confidenceIntervalWithOneSample() {
		Statistics statistics = new Statistics();
		statistics.addSample(35.8);
		statistics.ConfidenceIntervalDistance(0.90, ICBound.Lower);
	}
	
	@Test
	public void confidenceIntervalWithFiveSamples() {
		Statistics statistics = new Statistics();
		
		statistics.addSample(35.8);
		statistics.addSample(40.1);
		statistics.addSample(98.9);
		statistics.addSample(14.7);
		statistics.addSample(59.3);
		
		Double dist1 = statistics.estimateAverage() - statistics.ConfidenceIntervalDistance(0.98, ICBound.Lower);
		Double dist2 = statistics.ConfidenceIntervalDistance(0.98, ICBound.Upper) - statistics.estimateAverage();
		
		assertEquals(dist1, dist2, 0.0001);
	}
}



