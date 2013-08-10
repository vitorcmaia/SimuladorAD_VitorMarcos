package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import network.SACK;

import org.junit.Test;

public class SACKTest {
	
	@Test
	public void compareSacksWithDifferentDestinations() {
		ArrayList<ArrayList<Long>> matrix1 = new ArrayList<ArrayList<Long>>();
		
		ArrayList<Long> array1 = new ArrayList<Long>();
		array1.add(3000L);
		array1.add(4499L);
		
		ArrayList<Long> array2 = new ArrayList<Long>();
		array2.add(4500L);
		array2.add(5999L);
		
		matrix1.add(array1);
		matrix1.add(array2);
		
		SACK s1 = new SACK(1, 1500L, matrix1);
		SACK s2 = new SACK(2, 1500L, matrix1);
		
		assertTrue(!s1.equals(s2));
	}
	
	@Test
	public void compareSacksWithDifferentExpectedBytes() {
		ArrayList<ArrayList<Long>> matrix1 = new ArrayList<ArrayList<Long>>();
		
		ArrayList<Long> array1 = new ArrayList<Long>();
		array1.add(3000L);
		array1.add(4499L);
		
		ArrayList<Long> array2 = new ArrayList<Long>();
		array2.add(4500L);
		array2.add(5999L);
		
		matrix1.add(array1);
		matrix1.add(array2);
		
		SACK s1 = new SACK(1, 3000L, matrix1);
		SACK s2 = new SACK(1, 1500L, matrix1);
		
		assertTrue(!s1.equals(s2));
	}
	
	@Test
	public void compareSacksWithDifferentMatrixes() {
		ArrayList<ArrayList<Long>> matrix1 = new ArrayList<ArrayList<Long>>();
		ArrayList<ArrayList<Long>> matrix2 = new ArrayList<ArrayList<Long>>();
		
		ArrayList<Long> array1 = new ArrayList<Long>();
		array1.add(3000L);
		array1.add(4499L);
		
		ArrayList<Long> array2 = new ArrayList<Long>();
		array2.add(4500L);
		array2.add(5999L);
		
		matrix1.add(array1);
		matrix1.add(array2);
		matrix2.add(array2);
		
		SACK s1 = new SACK(1, 1500L, matrix1);
		SACK s2 = new SACK(1, 1500L, matrix2);
		
		assertTrue(!s1.equals(s2));
	}
	
	@Test
	public void compareSacksWithNullMatrixes() {
		SACK s1 = new SACK(1, 1500L, null);
		SACK s2 = new SACK(1, 1500L, null);
		
		assertTrue(s1.equals(s2));
	}
	
	@Test
	public void compareEmptyAndNullSequences() {
		ArrayList<ArrayList<Long>> matrix1 = new ArrayList<ArrayList<Long>>();
		
		SACK s1 = new SACK(1, 1500L, matrix1);
		SACK s2 = new SACK(1, 1500L, null);
		
		assertTrue(s1.equals(s2));
	}
	
	@Test
	public void compareSacksWithOnlyOneNullMatrix() {
		ArrayList<ArrayList<Long>> matrix1 = new ArrayList<ArrayList<Long>>();
		
		ArrayList<Long> array1 = new ArrayList<Long>();
		array1.add(3000L);
		array1.add(4499L);
		
		ArrayList<Long> array2 = new ArrayList<Long>();
		array2.add(4500L);
		array2.add(5999L);
		
		matrix1.add(array1);
		matrix1.add(array2);
		
		SACK s1 = new SACK(1, 1500L, matrix1);
		SACK s2 = new SACK(1, 1500L, null);
		
		assertTrue(!s1.equals(s2));
	}
	
	@Test
	public void compareSacksWithAlmostSameSequences() {
		ArrayList<ArrayList<Long>> matrix1 = new ArrayList<ArrayList<Long>>();
		ArrayList<ArrayList<Long>> matrix2 = new ArrayList<ArrayList<Long>>();
		
		ArrayList<Long> array1 = new ArrayList<Long>();
		array1.add(3000L);
		array1.add(4499L);
		
		ArrayList<Long> array2 = new ArrayList<Long>();
		array2.add(4500L);
		array2.add(5999L);
		
		ArrayList<Long> array3 = new ArrayList<Long>();
		array3.add(4500L);
		array3.add(6000L);
		
		matrix1.add(array1);
		matrix1.add(array2);
		matrix2.add(array1);
		matrix2.add(array3);
		
		SACK s1 = new SACK(1, 1500L, matrix1);
		SACK s2 = new SACK(1, 1500L, matrix2);
		
		assertTrue(!s1.equals(s2));
	}
}
