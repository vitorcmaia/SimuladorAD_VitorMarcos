package tests;

import static org.junit.Assert.*;

import maths.SimulatedRandom;

import org.junit.Test;

public class RandomTest {
	
	@Test
	public void checkSeeds() {
		SimulatedRandom a = new SimulatedRandom();
		SimulatedRandom b = new SimulatedRandom();
		assertEquals(a.getSeed(), b.getSeed());
	}
	
	@Test
	public void changeSeed() {
		SimulatedRandom a = new SimulatedRandom();
		SimulatedRandom b = new SimulatedRandom();
		assertEquals(a.getSeed(), b.getSeed());
		
		SimulatedRandom.changeSeed();
		assertEquals(a.getSeed(), b.getSeed());
	}
}
