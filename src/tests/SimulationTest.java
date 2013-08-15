package tests;

import static org.junit.Assert.*;
import maths.SimulationProperties;
import network.Event;
import network.Pack;
import network.SACK;
import network.Tx;

import org.junit.Before;
import org.junit.Test;

import enums.EventType;
import enums.PackType;
import enums.RouterType;

import simulation.Simulation;

public class SimulationTest {
	
	private Simulation simulation;
	private Tx tx;
	
	@Before
	public void setUp() throws Exception {
		SimulationProperties.setQuantityOfG1(1);
		SimulationProperties.setQuantityOfG2(0);
		SimulationProperties.setRouterType(RouterType.FIFO);
		simulation = new Simulation();
		tx = simulation.getSystem().getTxs().get(0);
	}
	
	@Test
	public void Timeout1() {
		tx.sendPacket(0);
		tx.setTransmiting(true);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(0, simulation.getEvents().size());
	}
	
	@Test
	public void Timeout2() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		simulation.getEvents().poll();
		
		assertEquals(simulation.getEvents().poll().getTime(), 400 + tx.getRTO(), 0);
	}

	@Test
	public void Timeout3() {
		tx.sendPacket(0);
		tx.setTransmiting(true);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertTrue(tx.isTransmiting());
	}

	@Test
	public void Timeout4() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		simulation.getEvents().poll();
		assertEquals(simulation.getEvents().poll().getType(), EventType.Timeout);
	}
	
	@Test
	public void SackArrives1() {
		SACK sack = new SACK(tx.getIndex(), 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		tx.setTransmiting(true);
		simulation.handleEvents();
		
		Pack p1 = tx.sendPacket(350);
		Pack p2 = new Pack(PackType.CommonTCP, 0, 1500L, 2999L);
		assertEquals(p1, p2);
	}
	
	@Test
	public void SackArrives2() {
		tx.setTransmiting(false);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		Pack p1 = tx.sendPacket(350);
		Pack p2 = new Pack(PackType.CommonTCP, 0, 3000L, 4499L);
		assertEquals(p1, p2);
	}

	@Test
	public void SackArrives3() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		
		assertEquals(simulation.getEvents().poll().getTime(), 350.012, 0);
	}
	
	@Test
	public void SackArrives4() {
		tx.setTransmiting(true);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertTrue(tx.isTransmiting());
	}
	
	@Test
	public void SackArrives5() {
		tx.setTransmiting(true);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();

		assertEquals(0, simulation.getEvents().size());
	}

	
	@Test
	public void SackArrives6() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		simulation.getEvents().poll();
		
		assertEquals(simulation.getEvents().poll().getType(), EventType.Timeout);
	}

	@Test
	public void TxPacketIntoRouter1() {
		Pack p = simulation.getSystem().getTxs().get(0).sendPacket(0);
		tx.setTransmiting(false);
		Event e = new Event(EventType.TxPacketIntoRouter);
		e.setTime(100.012);
		e.setPack(p);		
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.handleEvents();
		simulation.handleEvents();
		assertTrue(tx.isTransmiting());
	}
	
	@Test
	public void TxPacketIntoRouter2() {
		Pack p = new Pack(PackType.CommonTCP, 0, 0L, 1499L);
		Event e = new Event(EventType.TxPacketIntoRouter);
		e.setTime(100);
		e.setPack(p);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(1.2 + 100, simulation.getEvents().peek().getTime(), 0);
	}

	@Test
	public void RouterSuccessfullySentPacket1() {
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(1.2);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		
		assertEquals(simulation.getEvents().poll().getType(), EventType.RouterSuccessfullySentPacket);
	}
	
	@Test(expected = NullPointerException.class)
	public void RouterSuccessfullySentPacket2() {
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(1.2);
		simulation.getEvents().add(e);
		simulation.handleEvents();
	}
	
	@Test
	public void RouterSuccessfullySentPacket3() {
		simulation.getSystem().getRouter().receivePacket(tx.sendPacket(0), 0.0);
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(100);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		assertEquals(simulation.getEvents().peek().getType(), EventType.SackArrives);
		assertEquals(simulation.getEvents().peek().getTime(), 200, 0);
	}

	@Test
	public void RouterSuccessfullySentPacket4() {
		simulation.getSystem().getRouter().receivePacket(tx.sendPacket(0), 0.0);
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(100);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		
		assertEquals(2, simulation.getEvents().size());

	}
	
	@Test
	public void TxPacketHeadsToRouter1() {
		tx.setTransmiting(true);
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		assertEquals(simulation.getEvents().peek().getTime(), 200.012, 0);
	}
	
	@Test
	public void TxPacketHeadsToRouter2() {
		simulation.getSystem().getTxs().get(0).sendPacket(0);
		tx.setTransmiting(true);
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100.012);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertFalse(tx.isTransmiting());
	}
	
	@Test
	public void TxPacketHeadsToRouter3() {
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100.012);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		tx.sendPacket(0);
		simulation.handleEvents();
		assertEquals(0, simulation.getEvents().size());
	}
	
	@Test
	public void CongestionPacketIntoRouter1() {
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.CongestionPacketIntoRouter);
		e.setTime(50);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(1, simulation.getEvents().size());
	}	
	
	@Test
	public void CongestionPacketIntoRouter2() {
		Event e = new Event(EventType.CongestionPacketIntoRouter);
		e.setTime(50);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		Event e2 = simulation.getEvents().poll();

		assertTrue((e2.getType().equals(EventType.CongestionPacketIntoRouter))
				|| (e2.getType().equals(EventType.RouterSuccessfullySentPacket)));

	}

	@Test
	public void CongestionPacketIntoRouter3() {
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.CongestionPacketIntoRouter);
		e.setTime(50);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(simulation.getEvents().poll().getType(), EventType.CongestionPacketIntoRouter);
	}
}
