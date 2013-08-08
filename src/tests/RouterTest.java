package tests;

import static org.junit.Assert.*;

import maths.SimulationProperties;
import network.Pack;
import network.Router;

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
}
