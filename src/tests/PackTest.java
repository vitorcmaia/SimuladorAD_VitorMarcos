package tests;

import static org.junit.Assert.*;

import network.Pack;

import org.junit.Test;

import enums.PackType;

public class PackTest {
	
	@Test
	public void ComparePacks() {
		Pack p1 = new Pack(PackType.CommonTCP, 9, 0L, 1499L);
		Pack p2 = new Pack(PackType.CommonTCP, 9, 0L, 1499L);
		
		assertEquals(p1, p2);
		
		Pack p3 = new Pack(PackType.CommonTCP, 0, 0L, 1499L);
		Pack p4 = new Pack(PackType.CommonTCP, 4, 0L, 1499L);
		
		assertTrue(!p3.equals(p4));
		
		Pack p5 = new Pack(PackType.CommonTCP, 4, 0L, 1499L);
		Pack p6 = new Pack(PackType.CommonTCP, 4, 1500L, 2999L);
		
		assertTrue(!p5.equals(p6));
		
		Pack p7 = new Pack(PackType.Congestion, 9, 0L, 1499L);
		Pack p8 = new Pack(PackType.Congestion, 3, 0L, 1499L);
		
		assertEquals(p7, p8);
	}

}
