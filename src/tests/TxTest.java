package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import maths.SimulationProperties;
import network.Pack;
import network.SACK;
import network.Tx;

import org.junit.Test;

import enums.Group;
import enums.PackType;

public class TxTest {
	
	@Test
	public void sendPack() {
		Tx tx = new Tx(Group.Group1, 0);
		Pack p1 = new Pack(PackType.CommonTCP, 0, 0L, SimulationProperties.getMSS() - 1); 
		
		Pack p2 = tx.sendPacket(0);
		p2.setDestination(0);
		assertEquals(p1, p2);
	}
	
	@Test
	public void checkVariables() {
		Tx tx = new Tx(Group.Group1, 15);
		assertTrue(tx.getNextPacketToSend() < tx.getOldestNotReceivedPacket() + tx.getCongestionWindow());
		assertEquals(15, tx.sendPacket(0).getDestination().intValue());
		assertEquals(SimulationProperties.getMSS().longValue(), tx.getCongestionWindow());
		assertEquals(SimulationProperties.getThreshold(), tx.getThreshold());
	}
	
	@Test
	public void packRetransmission() {
		Tx tx = new Tx(Group.Group1, 9);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(9, 1500L, null), 0);
		Pack p = new Pack(PackType.CommonTCP, 9, 1500L, 2999L);
		assertEquals(p, tx.sendPacket(0));
	}
	
	@Test
	public void firstSackTest() {
		Tx tx = new Tx(Group.Group1, 9);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, SimulationProperties.getMSS(), null), 0.0);
		assertEquals(2 * SimulationProperties.getMSS(), tx.getCongestionWindow());
		assertEquals(SimulationProperties.getMSS().longValue(), tx.getNextPacketToSend());
		assertEquals(SimulationProperties.getMSS().longValue(), tx.getOldestNotReceivedPacket());
	}
	
	@Test
	public void checkCongestionWindowIncrement() {
		Tx tx = new Tx(Group.Group1, 9);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, SimulationProperties.getMSS(), null), 0.0);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, 2 * SimulationProperties.getMSS(), null), 0.0);
		assertEquals(3 * SimulationProperties.getMSS(), tx.getCongestionWindow());
	}
	
	@Test
	public void slowStart() {
		Tx tx = new Tx(Group.Group1, 9);
		int rtt = 0;
		while(tx.getCongestionWindow() < tx.getThreshold()) {
			long limit = tx.getCongestionWindow() / SimulationProperties.getMSS();
			for (int i = 0; i < limit; i++) {
				tx.sendPacket(0);
				tx.receiveSack(new SACK(0, tx.getNextPacketToSend(), null), 0.0);
			}
			rtt++;
		}
		assertEquals(6, rtt);
		//--
		tx = new Tx(Group.Group1, 9);
		for (rtt = 0; rtt < 3; rtt++) {
			long limit = tx.getCongestionWindow() / SimulationProperties.getMSS();
			for (int i = 0; i < limit; i++) {
				tx.sendPacket(0);
				tx.receiveSack(new SACK(0, tx.getNextPacketToSend(), null), 0.0);
			}
		}
		assertEquals(Math.pow(2, 3) * SimulationProperties.getMSS(), tx.getCongestionWindow(), 0);
		//--
		tx = new Tx(Group.Group1, 9);
		for (rtt = 0; rtt < 5; rtt++) {
			long limit = tx.getCongestionWindow() / SimulationProperties.getMSS();
			for (int i = 0; i < limit; i++) {
				tx.sendPacket(0);
				tx.receiveSack(new SACK(0, tx.getNextPacketToSend(), null), 0.0);
			}
		}
		assertEquals(Math.pow(2, 5) * SimulationProperties.getMSS(), tx.getCongestionWindow(), 0);
	}
	
	@Test
	public void slowStart1() {
		Tx tx = new Tx(Group.Group1, 9);
		
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, SimulationProperties.getMSS(), null), 0);

		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, 2 * SimulationProperties.getMSS(), null), 0);
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), null), 0);

		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.sendPacket(0);
		
		ArrayList<ArrayList<Long>> matrix1 = new ArrayList<ArrayList<Long>>();
		ArrayList<Long> bounds1 = new ArrayList<Long>();
		bounds1.add(4 * SimulationProperties.getMSS());
		bounds1.add(5 * SimulationProperties.getMSS());
		matrix1.add(bounds1);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix1), 0);
		
		ArrayList<ArrayList<Long>> matrix2 = new ArrayList<ArrayList<Long>>();
		ArrayList<Long> bounds2 = new ArrayList<Long>();
		bounds2.add(4 * SimulationProperties.getMSS());
		bounds2.add(6 * SimulationProperties.getMSS());
		matrix2.add(bounds2);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix2), 0);
		
		assertEquals(3 * SimulationProperties.getMSS(), tx.getOldestNotReceivedPacket());
	}
	
	@Test
	public void SecondSendPack() {
		Tx tx = new Tx(Group.Group1, 9);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, SimulationProperties.getMSS(), null), 0.0);
		Pack p1 = new Pack(PackType.CommonTCP, 0, SimulationProperties.getMSS(), 2 * SimulationProperties.getMSS() - 1);
		Pack p2 = tx.sendPacket(0);
		p2.setDestination(0);

		assertEquals(p1, p2);
	}
	
	@Test
	public void DuplicationBehaviour() {
		Tx tx = new Tx(Group.Group1, 9);
		tx.receiveSack(new SACK(0, SimulationProperties.getMSS(), null), 0.0);

		tx.receiveSack(new SACK(0, 2 * SimulationProperties.getMSS(), null), 0.0);
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), null), 0.0);

		ArrayList<ArrayList<Long>> matrix1 = new ArrayList<ArrayList<Long>>();
		ArrayList<Long> bounds1 = new ArrayList<Long>();
		bounds1.add(4 * SimulationProperties.getMSS());
		bounds1.add(5 * SimulationProperties.getMSS());
		matrix1.add(bounds1);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix1), 0);
		
		ArrayList<ArrayList<Long>> matrix2 = new ArrayList<ArrayList<Long>>();
		ArrayList<Long> bounds2 = new ArrayList<Long>();
		bounds2.add(4 * SimulationProperties.getMSS());
		bounds2.add(6 * SimulationProperties.getMSS());
		matrix2.add(bounds2);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix2), 0);
		
		tx.receiveSack(new SACK(0, 6 * SimulationProperties.getMSS(), null), 0.0);
		tx.receiveSack(new SACK(0, 7 * SimulationProperties.getMSS(), null), 0.0);

		assertEquals(8 * SimulationProperties.getMSS(), tx.getCongestionWindow());
		
		tx = new Tx(Group.Group1, 9);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, SimulationProperties.getMSS(), null), 0);

		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, 2 * SimulationProperties.getMSS(), null), 0);
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), null), 0);

		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.sendPacket(0);
		
		matrix1 = new ArrayList<ArrayList<Long>>();
		bounds1 = new ArrayList<Long>();
		bounds1.add(4 * SimulationProperties.getMSS());
		bounds1.add(5 * SimulationProperties.getMSS());
		matrix1.add(bounds1);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix1), 0.0);
		
		matrix2 = new ArrayList<ArrayList<Long>>();
		bounds2 = new ArrayList<Long>();
		bounds2.add(4 * SimulationProperties.getMSS());
		bounds2.add(6 * SimulationProperties.getMSS());
		matrix2.add(bounds2);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix2), 0.0);
		
		assertEquals(6 * SimulationProperties.getMSS(), tx.getCongestionWindow());
	}

	@Test
	public void timeOutCheck() {
		Tx tx = new Tx(Group.Group1, 9);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, SimulationProperties.getMSS(), null), 0);
		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, 2 * SimulationProperties.getMSS(), null), 0);
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), null), 0);
		Pack p = tx.sendPacket(0);
		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.sendPacket(0);		
		tx.handleTimeOutEvent();
		assertEquals(tx.getThreshold(), 2 * SimulationProperties.getMSS());
		assertEquals(tx.getCongestionWindow(), SimulationProperties.getMSS().longValue());
		assertEquals(p, tx.sendPacket(0));
		
		tx = new Tx(Group.Group1, 9);
		tx.sendPacket(0);
		tx.handleTimeOutEvent();
		p = new Pack(PackType.CommonTCP, 9, 0L, 1499L);
		assertEquals(p, tx.sendPacket(0));
	}
	
	@Test(expected = RuntimeException.class)
	public void testNotReadException() {
		Tx tx = new Tx(Group.Group1, 9);
		tx.sendPacket(0);
		tx.sendPacket(0);
	}
	
	/**
	 * Descarta um pacote e testa o comportamento do TxTCP no Fast Retransmit.
	 */
	@Test
	public void FastRetransmitCheck() {
		Tx tx = new Tx(Group.Group1, 9);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, SimulationProperties.getMSS(), null), 0.0);

		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, 2 * SimulationProperties.getMSS(), null), 0.0);
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), null), 0.0);

		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.sendPacket(0);
		
		ArrayList<ArrayList<Long>> matrix1 = new ArrayList<ArrayList<Long>>();
		ArrayList<Long> bounds1 = new ArrayList<Long>();
		bounds1.add(4 * SimulationProperties.getMSS());
		bounds1.add(5 * SimulationProperties.getMSS());
		matrix1.add(bounds1);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix1), 0.0);
		
		ArrayList<ArrayList<Long>> matrix2 = new ArrayList<ArrayList<Long>>();
		ArrayList<Long> bounds2 = new ArrayList<Long>();
		bounds2.add(4 * SimulationProperties.getMSS());
		bounds2.add(6 * SimulationProperties.getMSS());
		matrix2.add(bounds2);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix2), 0.0);
		
		ArrayList<ArrayList<Long>> matrix3 = new ArrayList<ArrayList<Long>>();
		ArrayList<Long> bounds3 = new ArrayList<Long>();
		bounds3.add(4 * SimulationProperties.getMSS());
		bounds3.add(7 * SimulationProperties.getMSS());
		matrix3.add(bounds3);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix3), 0.0);
		
		assertTrue(tx.fastRetransmitIsOn());
		
		tx.receiveSack(new SACK(0, 7 * SimulationProperties.getMSS(), null), 0.0);

		assertTrue(!tx.fastRetransmitIsOn());
		assertEquals(tx.getCongestionWindow(), tx.getThreshold());
	}


	@Test
	public void FastRetransmitCheck2() {
		Tx tx = new Tx(Group.Group1, 9);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, SimulationProperties.getMSS(), null), 0);

		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.receiveSack(new SACK(0, 2 * SimulationProperties.getMSS(), null), 0);
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), null), 0);

		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.sendPacket(0);
		tx.sendPacket(0);
		
		ArrayList<ArrayList<Long>> matrix1 = new ArrayList<ArrayList<Long>>();
		ArrayList<Long> bounds1 = new ArrayList<Long>();
		bounds1.add(4 * SimulationProperties.getMSS());
		bounds1.add(5 * SimulationProperties.getMSS());
		matrix1.add(bounds1);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix1), 0.0);
		
		ArrayList<ArrayList<Long>> matrix2 = new ArrayList<ArrayList<Long>>();
		ArrayList<Long> bounds2 = new ArrayList<Long>();
		bounds2.add(4 * SimulationProperties.getMSS());
		bounds2.add(6 * SimulationProperties.getMSS());
		matrix2.add(bounds2);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix2), 0.0);
		
		ArrayList<ArrayList<Long>> matrix3 = new ArrayList<ArrayList<Long>>();
		ArrayList<Long> bounds3 = new ArrayList<Long>();
		bounds3.add(4 * SimulationProperties.getMSS());
		bounds3.add(7 * SimulationProperties.getMSS());
		matrix3.add(bounds3);
		
		tx.receiveSack(new SACK(0, 3 * SimulationProperties.getMSS(), matrix3), 0.0);
		
		assertEquals(3 * SimulationProperties.getMSS(), tx.getNextPacketToSend());
		
		tx.sendPacket(0);
		Pack pack = new Pack(PackType.CommonTCP, 9);
		pack.setDestination(tx.getIndex());
		pack.setStartingByte(7 * SimulationProperties.getMSS());
		pack.setEndingByte(8 * SimulationProperties.getMSS() - 1);
		assertEquals(pack, tx.sendPacket(0));
	}
}
