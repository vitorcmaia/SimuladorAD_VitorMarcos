package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import maths.SimulationProperties;
import network.Pack;
import network.Router;
import network.Sys;

import org.junit.Test;

import enums.PackType;
import enums.RouterType;

public class RouterTest {
	
	//----------------------------------------------------------------------------
	// Testes de FIFO.
	@Test
	public void emptyRouter() {
		Router router = new Router(RouterType.FIFO);
		assertEquals(router.getBuffer().getBufferSize(), SimulationProperties.getBufferSize());
		assertEquals(router.getBuffer().quantityOfRemainingPackets(), 0, 0);
	}
	
	@Test
	public void addOnePackToBuffer() {
		Router router = new Router(RouterType.FIFO);
		router.receivePacket(new Pack(PackType.CommonTCP, 0), 0.0);
		
		assertEquals(router.getBuffer().quantityOfRemainingPackets(), 1, 0);
	}
	
	@Test
	public void completeBuffer() {
		Router router = new Router(RouterType.FIFO);
		for(int i = 0 ; i < SimulationProperties.getBufferSize() ; i++)
			router.receivePacket(new Pack(PackType.CommonTCP, 0), 0.0);
		
		assertEquals(router.getBuffer().quantityOfRemainingPackets(), SimulationProperties.getBufferSize());
		router.receivePacket(new Pack(PackType.CommonTCP, 0), 0.0);
		assertEquals(router.getBuffer().quantityOfRemainingPackets(), SimulationProperties.getBufferSize());
	}
	//----------------------------------------------------------------------------
	// Testes de RED.
	@Test
	public void REDWithOnePack() {
		Router router = new Router(RouterType.RED);
		
		Pack pack = new Pack(PackType.CommonTCP, 0);
		router.receivePacket(pack, 500.0);
		assertEquals(1, router.getBuffer().quantityOfRemainingPackets().intValue());
	}

	@Test
	public void REDWithFivePacks() {
		Router router = new Router(RouterType.RED);
		router.receivePacket(new Pack(PackType.CommonTCP, 0), 500.0);
		router.receivePacket(new Pack(PackType.CommonTCP, 0), 500.0);
		router.receivePacket(new Pack(PackType.CommonTCP, 0), 500.0);
		router.receivePacket(new Pack(PackType.CommonTCP, 0), 500.0);
		router.receivePacket(new Pack(PackType.CommonTCP, 0), 500.0);

		assertEquals(5, router.getBuffer().quantityOfRemainingPackets().intValue());
	}

	@Test
	public void testMinthValue() {
		Router router = new Router(RouterType.RED);
		router.getBuffer().setBufferSize(1000);
		
		int i = 0;
		while (i < router.getBuffer().getBufferSize() &&
				router.getRedAttributes().getMinth() >
		        router.getRedAttributes().getAverage()) {
			i++;
			router.receivePacket(new Pack(PackType.CommonTCP, 0), 0.0);
		}

		assertEquals(i, router.getBuffer().quantityOfRemainingPackets().intValue());
	}
	
	@Test
	public void testMaxthValue() {
		Sys s = new Sys(RouterType.RED);
		Router router = s.getRouter();
		router.getBuffer().setBufferSize(1000000);
		ArrayList<Pack> packs1 = new ArrayList<Pack>();
		int i = 0;
		while(i < router.getBuffer().getBufferSize() &&
				router.getRedAttributes().getMaxth() >=
				router.getRedAttributes().getAverage()) {
			Pack p = new Pack(PackType.CommonTCP, 0);
			if(router.receivePacket(p, 500.0 + i)) {
				packs1.add(p);
				i++;
			}	
		}
		
		for(int k = 0 ; k < 2000 ; k++) {
			router.receivePacket(new Pack(PackType.CommonTCP, 0), 0.0);
		}
		
		ArrayList<Pack> packs2 = new ArrayList<Pack>();
		i = 0;
		while(router.getBuffer().quantityOfRemainingPackets() > 0) {
			packs2.add(router.getBuffer().getNextPack());
			router.sendPackToRx(0.0);
			i++;
		}
		
		assertEquals(packs1.size(), packs2.size());
		
		for(Pack p1 : packs1)
			for(Pack p2 : packs2)
				assertEquals(p1,p2);
	}

	@Test
	public void BufferSizeOneReceivesTwoPacks() {
		Router router = new Router(RouterType.RED);
		router.getBuffer().setBufferSize(1);

		router.receivePacket(new Pack(PackType.CommonTCP, 0), 500.0);
		router.receivePacket(new Pack(PackType.CommonTCP, 0), 501.0);

		assertEquals(1, router.getBuffer().quantityOfRemainingPackets().intValue());
	}

	@Test
	public void testEnviarProximoPacote() {
		Sys s = new Sys(RouterType.RED);
		Router router = s.getRouter();
		router.receivePacket(new Pack(PackType.CommonTCP, 0), 500.0);
		router.sendPackToRx(510.0);

		assertEquals(0, router.getBuffer().quantityOfRemainingPackets().intValue());
	}
	
	//----------------------------------------------------------------------------
}
