package tests;

import static org.junit.Assert.*;

import maths.SimulatedRandom;

import org.junit.Test;

public class RandomTest {
	
	@Test
	public void checkSeeds() {
		Long i = 0L;
		new SimulatedRandom();
		Long lastSeed = SimulatedRandom.getSeed();
		do {
			new SimulatedRandom();
			Long newSeed = SimulatedRandom.getSeed();
			Long diff = Math.abs(lastSeed - newSeed);
			assertTrue(diff > 1000000000);
			lastSeed = newSeed;
			i++;
		} while(i < 5000000);
	}
}
