package tests;

import static org.junit.Assert.*;

import maths.SimulatedRandom;

import org.junit.Test;

public class RandomTest {
	
	@Test
	public void checkSeeds() {
		for(int i = 0 ; i < 100000 ; i++) {
			SimulatedRandom a = new SimulatedRandom();
			SimulatedRandom b = new SimulatedRandom();
			assertTrue(a.getSeed().longValue() != b.getSeed().longValue());
		}
	}
}
