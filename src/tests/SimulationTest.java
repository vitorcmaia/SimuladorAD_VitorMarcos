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
	public void testTxTCPTerminaTransmissao0() {
		tx.setTransmiting(true);
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		Event ev = simulation.getEvents().peek();
		assertEquals(ev.getType(), EventType.TxPacketIntoRouter);
	}
	
	@Test
	public void testTxTCPTerminaTransmissao1() {
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
	public void testTxTCPTerminaTransmissao2() {
		tx.setTransmiting(true);
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(3, simulation.getEvents().size());
	}

	@Test
	public void testTxTCPTerminaTransmissao3() {
		tx.setTransmiting(true);
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		simulation.getEvents().poll();
		assertEquals(simulation.getEvents().poll().getType(), EventType.Timeout);
	}

	@Test
	public void testTxTCPTerminaTransmissao4() {
		tx.setTransmiting(true);
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		simulation.getEvents().poll();
		assertEquals(100 + tx.getRTO(), simulation.getEvents().poll().getTime(), 0);
	}

	@Test
	public void testTxTCPTerminaTransmissao5() {
		tx.setTransmiting(true);
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(simulation.getEvents().poll().getType(),EventType.TxPacketHeadsToRouter);
	}

	@Test
	public void testTxTCPTerminaTransmissao6() {
		tx.setTransmiting(true);
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(100.012, simulation.getEvents().poll().getTime(), 0);
	}

	@Test
	public void testTxTCPTerminaTransmissao7() {
		tx.setTransmiting(true);
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		Pack p = simulation.getEvents().poll().getPack();
		assertEquals(p.getTimeout(), simulation.getEvents().poll());
	}

	@Test
	public void testTxTCPTerminaTransmissao8() {
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100.012);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		tx.sendPacket(0);
		simulation.handleEvents();
		assertEquals(0, simulation.getEvents().size());
	}

	@Test
	public void testTxTCPTerminaTransmissao9() {
		simulation.getSystem().getTxs().get(0).sendPacket(0);
		tx.setTransmiting(true);
		Event e = new Event(EventType.TxPacketHeadsToRouter);
		e.setTime(100.012);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertFalse(tx.isTransmiting());
	}

	/* --------------Testes do RoteadorRecebePacoteTxTCP-------------- */
	@Test
	public void testRoteadorRecebePacoteTxTCP1() {
		Pack p = new Pack(PackType.CommonTCP, 0, 0L, 1499L);
		Event e = new Event(EventType.TxPacketIntoRouter);
		e.setTime(100);
		e.setPack(p);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(simulation.getEvents().peek().getType(), EventType.RouterSuccessfullySentPacket);
	}

	@Test
	public void testRoteadorRecebePacoteTxTCP2() {
		Pack p = new Pack(PackType.CommonTCP, 0, 0L, 1499L);
		Event e = new Event(EventType.TxPacketIntoRouter);
		e.setTime(100);
		e.setPack(p);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(1.2 + 100, simulation.getEvents().peek().getTime(), 0);
	}

	@Test
	public void testRoteadorRecebePacoteTxTCP3() {
		Pack p = new Pack(PackType.CommonTCP, 0, 0L, 1499L);
		Event e = new Event(EventType.TxPacketIntoRouter);
		e.setTime(100);
		e.setPack(p);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		assertNull(simulation.getEvents().peek());
	}

	@Test
	public void testRoteadorRecebePacoteTxTCP4() {
		Pack p = new Pack(PackType.CommonTCP, 0, 0L, 1499L);
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.CommonTCP, 0, 0L, 1499L), 0.0);
		Event e = new Event(EventType.TxPacketIntoRouter);
		e.setTime(100);
		e.setPack(p);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(0, simulation.getEvents().size());
	}

	@Test
	public void testRoteadorRecebePacoteTxTCP5() {
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

	/* --------------Testes do EventoRoteadorTerminaEnvio-------------- */
	@Test(expected = NullPointerException.class)
	public void testRoteadorTerminaEnvio1() {
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(1.2);
		simulation.getEvents().add(e);
		simulation.handleEvents();
	}

	@Test
	public void testRoteadorTerminaEnvio2() {
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(1.2);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertNull(simulation.getEvents().poll());
	}

	@Test
	public void testRoteadorTerminaEnvio3() {

		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(1.2);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		
		assertEquals(simulation.getEvents().poll().getType(), EventType.RouterSuccessfullySentPacket);
	}

	@Test
	public void testRoteadorTerminaEnvio4() {
		
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(1.2);
		simulation.getEvents().add(e);
		simulation.handleEvents();

		assertEquals(1.2 + 1.2, simulation.getEvents().poll().getTime(),0);

	}

	@Test
	public void testRoteadorTerminaEnvio5() {
		simulation.getSystem().getRouter().receivePacket(tx.sendPacket(0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(100);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		
		assertEquals(simulation.getEvents().poll().getType(), EventType.SackArrives);
	}

	@Test
	public void testRoteadorTerminaEnvio6() {
		simulation.getSystem().getRouter().receivePacket(tx.sendPacket(0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(100);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(200, simulation.getEvents().poll().getTime(), 0);
	}

	@Test
	public void testRoteadorTerminaEnvio7() {
		simulation.getSystem().getRouter().receivePacket(tx.sendPacket(0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(100);
		simulation.getEvents().add(e);
		simulation.handleEvents();

		assertEquals(1, simulation.getEvents().size());

	}

	@Test
	public void testRoteadorTerminaEnvio8() {
		simulation.getSystem().getRouter().receivePacket(tx.sendPacket(0), 0.0);
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(100);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		
		assertEquals(simulation.getEvents().poll().getType(), EventType.RouterSuccessfullySentPacket);
	}

	@Test
	public void testRoteadorTerminaEnvio9() {
		simulation.getSystem().getRouter().receivePacket(tx.sendPacket(0), 0.0);
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(100);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(100 + 1.2, simulation.getEvents().poll().getTime(), 0);
	}

	@Test
	public void testRoteadorTerminaEnvio10() {
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
	public void testRoteadorTerminaEnvio12() {
		simulation.getSystem().getRouter().receivePacket(tx.sendPacket(0), 0.0);
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.RouterSuccessfullySentPacket);
		e.setTime(100);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		
		assertEquals(2, simulation.getEvents().size());

	}

	/* -----------Testes do EventoRoteadorRecebeTafegoFundo----------- */

	@Test
	public void testEventoRoteadorRecebeTafegoFundo1() {
		Event e = new Event(EventType.CongestionPacketIntoRouter);
		e.setTime(50);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		Event e2 = simulation.getEvents().poll();

		assertTrue((e2.getType().equals(EventType.CongestionPacketIntoRouter))
				|| (e2.getType().equals(EventType.RouterSuccessfullySentPacket)));

	}

	@Test
	public void testEventoRoteadorRecebeTafegoFundo2() {
		Event e = new Event(EventType.CongestionPacketIntoRouter);
		e.setTime(50);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		Event e2 = simulation.getEvents().poll();
		
		if (e2.getType().equals(EventType.CongestionPacketIntoRouter)) {
			assertEquals(50 + 1.2, simulation.getEvents().poll().getTime(), 0);
		} else if (e2.getType().equals(EventType.RouterSuccessfullySentPacket)) {
			assertEquals(50 + 1.2, e2.getTime(), 0);
		} else {
			fail("Evento inválido na fila: " + e2.getClass());
		}
	}

	@Test
	public void testEventoRoteadorRecebeTafegoFundo3() {
		Event e = new Event(EventType.CongestionPacketIntoRouter);
		e.setTime(50);
		simulation.getEvents().add(e);
		simulation.handleEvents();

		assertEquals(2, simulation.getEvents().size());

	}


	@Test
	public void testEventoRoteadorRecebeTafegoFundo4() {
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.CongestionPacketIntoRouter);
		e.setTime(50);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(simulation.getEvents().poll().getType(), EventType.CongestionPacketIntoRouter);
	}
	
	@Test
	public void testEventoRoteadorRecebeTafegoFundo5() {
		simulation.getSystem().getRouter().receivePacket(new Pack(PackType.Congestion, 0), 0.0);
		Event e = new Event(EventType.CongestionPacketIntoRouter);
		e.setTime(50);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(1, simulation.getEvents().size());
	}

	/* -----------------Testes do EventoTimeOut----------------- */
	@Test
	public void testEventoTimeOut1() {
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
	public void testEventoTimeOut2() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(3, simulation.getEvents().size());

	}

	@Test
	public void testEventoTimeOut3() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		
		assertEquals(simulation.getEvents().poll().getType(), EventType.TxPacketHeadsToRouter);
	}

	@Test
	public void testEventoTimeOut4() {
		Tx tx = simulation.getSystem().getTxs().get(0);
		tx.sendPacket(0);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(400 + 0.012, simulation.getEvents().poll().getTime(), 0);
	}

	@Test
	public void testEventoTimeOut5() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		assertEquals(simulation.getEvents().poll().getType(), EventType.TxPacketIntoRouter);
	}

	@Test
	public void testEventoTimeOut6() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		assertEquals(simulation.getEvents().poll().getTime(), 500.012, 0);
	}

	@Test
	public void testEventoTimeOut7() {
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
	public void testEventoTimeOut8() {
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
	public void testEventoTimeOut9() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();

		assertTrue(tx.isTransmiting());
	}

	@Test
	public void testEventoTimeOut10() {
		tx.sendPacket(0);
		tx.setTransmiting(true);
		Event e = new Event(EventType.Timeout);
		e.setTime(400);
		e.setTxIndex(tx.getIndex());
		simulation.getEvents().add(e);
		simulation.handleEvents();

		assertTrue(tx.isTransmiting());

	}

	/* -----------------Testes do EventoTxTCPRecebeSACK----------------- */

	@Test
	public void testEventoTxTCPRecebeSACK1() {
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
	public void testEventoTxTCPRecebeSACK2() {
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
	public void testEventoTxTCPRecebeSACK3() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		assertEquals(3, simulation.getEvents().size());
	}

	@Test
	public void testEventoTxTCPRecebeSACK4() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		
		assertEquals(simulation.getEvents().poll().getType(), EventType.TxPacketHeadsToRouter);
	}

	@Test
	public void testEventoTxTCPRecebeSACK5() {
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
	public void testEventoTxTCPRecebeSACK6() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		assertEquals(simulation.getEvents().poll().getType(), EventType.TxPacketIntoRouter);
	}
	
	@Test
	public void testEventoTxTCPRecebeSACK7() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		assertEquals(simulation.getEvents().poll().getTime(), 450.012, 0);
	}

	@Test
	public void testEventoTxTCPRecebeSACK8() {
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
	public void testEventoTxTCPRecebeSACK9() {
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
		assertEquals(350 + tx.getRTO(), simulation.getEvents().poll().getTime(), 0);
	}

	@Test
	public void testEventoTxTCPRecebeSACK10() {
		tx.sendPacket(0);
		tx.setTransmiting(false);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();
		simulation.getEvents().poll();
		Pack p = new Pack(PackType.CommonTCP, 0, 1500L, 2999L);
		
		assertEquals(p, simulation.getEvents().poll().getPack());
	}

	@Test
	public void testEventoTxTCPRecebeSACK11() {
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
	public void testEventoTxTCPRecebeSACK12() {
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
	public void testEventoTxTCPRecebeSACK13() {
		tx.setTransmiting(false);
		SACK sack = new SACK(0, 1500L, null);
		Event e = new Event(EventType.SackArrives);
		e.setTime(350);
		e.setSack(sack);
		simulation.getEvents().add(e);
		simulation.handleEvents();

		assertTrue(tx.isTransmiting());
	}
	/* ----------------------Testes Legados-------------------- */

	@Test
	public void testEventoTxTCPRecebeSACKDuranteTransmissao1() {
		Event txFinishesSending = new Event(EventType.TxPacketHeadsToRouter);
		txFinishesSending.setTime(100.012);
		txFinishesSending.setTxIndex(tx.getIndex());
		tx.setTransmiting(true);
		simulation.getEvents().add(txFinishesSending);
		SACK sack = new SACK(tx.getIndex(), 1500L, null);
		Event txReceivesSack = new Event(EventType.SackArrives);
		txReceivesSack.setTime(100.006);
		txReceivesSack.setSack(sack);
		simulation.getEvents().add(txReceivesSack);
		simulation.handleEvents();
		assertEquals(1, simulation.getEvents().size());
	}

	@Test
	public void testEventoTxTCPRecebeSACKDuranteTransmissao2() {
		Event txFinishesSending = new Event(EventType.TxPacketHeadsToRouter);
		txFinishesSending.setTime(100.012);
		txFinishesSending.setTxIndex(tx.getIndex());
		tx.setTransmiting(true);
		simulation.getEvents().add(txFinishesSending);
		SACK sack = new SACK(tx.getIndex(), 1500L, null);
		Event txReceivesSack = new Event(EventType.SackArrives);
		txReceivesSack.setTime(100.006);
		txReceivesSack.setSack(sack);
		simulation.getEvents().add(txReceivesSack);
		simulation.handleEvents();

		assertEquals(txFinishesSending, simulation.getEvents().poll());
	}

	/* ----------------------Testes Especiais-------------------- */

	@Test
	public void testCancelamentoTimeOut1() {
		Event txFinishesSending = new Event(EventType.TxPacketHeadsToRouter);
		txFinishesSending.setTime(50);
		txFinishesSending.setTxIndex(tx.getIndex());
		SACK sack = new SACK(tx.getIndex(), 4 * SimulationProperties.getMSS(), null);
		Event sackArrives = new Event(EventType.SackArrives);
		sackArrives.setTime(50.006);
		sackArrives.setSack(sack);
		simulation.getEvents().add(sackArrives);
		simulation.getEvents().add(txFinishesSending);
		simulation.handleEvents();
		simulation.handleEvents();
		assertEquals(2, simulation.getEvents().size());
	}

	@Test
	public void testCancelamentoTimeOut2() {
		Event txFinishesSending = new Event(EventType.TxPacketHeadsToRouter);
		txFinishesSending.setTime(50);
		txFinishesSending.setTxIndex(tx.getIndex());
		SACK sack = new SACK(tx.getIndex(), 4 * SimulationProperties.getMSS(), null);
		Event sackArrives = new Event(EventType.SackArrives);
		sackArrives.setTime(50.006);
		sackArrives.setSack(sack);
		simulation.getEvents().add(sackArrives);
		simulation.getEvents().add(txFinishesSending);
		simulation.handleEvents();
		simulation.handleEvents();
		assertFalse(simulation.getEvents().peek().getType().equals(EventType.Timeout));
	}
	
	@Test
	public void testCancelamentoTimeOut3() {
		Event txFinishesSending = new Event(EventType.TxPacketHeadsToRouter);
		txFinishesSending.setTime(50);
		txFinishesSending.setTxIndex(tx.getIndex());
		SACK sack = new SACK(tx.getIndex(), 4 * SimulationProperties.getMSS(), null);
		Event sackArrives = new Event(EventType.SackArrives);
		sackArrives.setTime(50.006);
		sackArrives.setSack(sack);
		simulation.getEvents().add(sackArrives);
		simulation.getEvents().add(txFinishesSending);
		simulation.handleEvents();
		simulation.handleEvents();
		simulation.getEvents().poll();
		
		assertFalse(simulation.getEvents().peek().getType().equals(EventType.Timeout));

	}

	@Test
	public void testCancelamentoTimeOut4() {		
		tx.receiveSack(new SACK(0, 1500L, null), 0.0);
		tx.setTransmiting(false);
		Event txFinishesSending = new Event(EventType.TxPacketHeadsToRouter);
		txFinishesSending.setTime(50);
		txFinishesSending.setTxIndex(tx.getIndex());
		
		SACK sack = new SACK(tx.getIndex(), 4 * SimulationProperties.getMSS(), null);
		Event sackArrives = new Event(EventType.SackArrives);
		sackArrives.setTime(50.018);
		sackArrives.setSack(sack);
		
		simulation.getEvents().add(sackArrives);
		simulation.getEvents().add(txFinishesSending);
		
		simulation.handleEvents();
		simulation.handleEvents();
		simulation.handleEvents();

		assertEquals(3, simulation.getEvents().size());

	}

	@Test
	public void testCancelamentoTimeOut5() {
		tx.receiveSack(new SACK(0, 1500L, null), 0.0);
		tx.setTransmiting(false);
		Event txFinishesSending = new Event(EventType.TxPacketHeadsToRouter);
		txFinishesSending.setTime(50);
		txFinishesSending.setTxIndex(tx.getIndex());
		
		SACK sack = new SACK(tx.getIndex(), 4 * SimulationProperties.getMSS(), null);
		Event sackArrives = new Event(EventType.SackArrives);
		sackArrives.setTime(50.018);
		sackArrives.setSack(sack);
		
		simulation.getEvents().add(sackArrives);
		simulation.getEvents().add(txFinishesSending);
		
		simulation.handleEvents();
		simulation.handleEvents();
		simulation.handleEvents();
		
		assertEquals(simulation.getEvents().poll().getType(), EventType.TxPacketHeadsToRouter);
	}

	@Test
	public void testCancelamentoTimeOut6() {
		tx.receiveSack(new SACK(0, 1500L, null), 0.0);
		tx.setTransmiting(false);
		Event txFinishesSending = new Event(EventType.TxPacketHeadsToRouter);
		txFinishesSending.setTime(50);
		txFinishesSending.setTxIndex(tx.getIndex());
		
		SACK sack = new SACK(tx.getIndex(), 4 * SimulationProperties.getMSS(), null);
		Event sackArrives = new Event(EventType.SackArrives);
		sackArrives.setTime(50.018);
		sackArrives.setSack(sack);
		
		simulation.getEvents().add(sackArrives);
		simulation.getEvents().add(txFinishesSending);
		
		simulation.handleEvents();
		simulation.handleEvents();
		simulation.handleEvents();

		simulation.getEvents().poll();
		
		assertEquals(simulation.getEvents().poll().getType(), EventType.TxPacketIntoRouter);
	}

	@Test
	public void testCancelamentoTimeOut7() {
		tx.receiveSack(new SACK(0, 1500L, null), 0.0);
		tx.setTransmiting(false);
		Event txFinishesSending = new Event(EventType.TxPacketHeadsToRouter);
		txFinishesSending.setTime(50);
		txFinishesSending.setTxIndex(tx.getIndex());
		
		SACK sack = new SACK(tx.getIndex(), 4 * SimulationProperties.getMSS(), null);
		Event sackArrives = new Event(EventType.SackArrives);
		sackArrives.setTime(50.018);
		sackArrives.setSack(sack);
		
		simulation.getEvents().add(sackArrives);
		simulation.getEvents().add(txFinishesSending);
		
		simulation.handleEvents();
		simulation.handleEvents();
		simulation.handleEvents();

		simulation.getEvents().poll();
		simulation.getEvents().poll();
		
		assertEquals(simulation.getEvents().poll().getType(), EventType.TxPacketIntoRouter);
	}
}
