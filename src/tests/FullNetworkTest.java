package tests;

import static org.junit.Assert.*;

import network.Pack;
import network.SACK;
import network.Sys;

import org.junit.Test;

import enums.PackType;
import enums.RouterType;

public class FullNetworkTest {
	
	@Test
	// Mando 5 pacotes em ordem para o roteador e em seguida para o Rx correspondente.
	public void PacksFromRouterIntoSameRx() {
		Sys s = new Sys(RouterType.FIFO);
		
		// 5 Pacotes, que teoricamente vieram do Tx 3...
		Pack pack3_1 = new Pack(PackType.CommonTCP, 3, 0L, 1499L);
		Pack pack3_2 = new Pack(PackType.CommonTCP, 3, 1500L, 2999L);
		Pack pack3_3 = new Pack(PackType.CommonTCP, 3, 3000L, 4499L);
		Pack pack3_4 = new Pack(PackType.CommonTCP, 3, 4500L, 5999L);
		Pack pack3_5 = new Pack(PackType.CommonTCP, 3, 6000L, 7499L);
		
		// Por enquanto, zero pacotes no roteador...
		assertEquals(s.getRouter().getBuffer().quantityOfRemainingPackets(), 0, 0);
		
		// 5 Pacotes adicionados ao buffer do roteador.
		s.getRouter().receivePacket(pack3_1, 0.0);
		s.getRouter().receivePacket(pack3_2, 0.0);
		s.getRouter().receivePacket(pack3_3, 0.0);
		s.getRouter().receivePacket(pack3_4, 0.0);
		s.getRouter().receivePacket(pack3_5, 0.0);
		
		// 5 pacotes no roteador.
		assertEquals(s.getRouter().getBuffer().quantityOfRemainingPackets(), 5, 0);
		
		// Envia o pacote para o Rx correspondente.
		SACK sack;
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		sack = s.getRouter().sendPackToRx(0.0);
		
		// Deve ser vazia a sequência, pois todos os pacotes chegaram, na ordem certa.
		assertEquals(sack.getSequences().size(), 0);
	}
	
	@Test
	// Crio 5 pacotes e envio 3, "esqueço" os pacotes 2 e 4.
	// Depois, envio.
	public void MissingPacksArrivingLateIntoSameRx() {
		Sys s = new Sys(RouterType.FIFO);
		
		Pack pack3_1 = new Pack(PackType.CommonTCP, 3, 0L, 1499L);
		Pack pack3_3 = new Pack(PackType.CommonTCP, 3, 3000L, 4499L);
		Pack pack3_5 = new Pack(PackType.CommonTCP, 3, 6000L, 7499L);
		
		// Por enquanto, zero pacotes no roteador...
		assertEquals(s.getRouter().getBuffer().quantityOfRemainingPackets(), 0, 0);
		
		// Adiciono apenas os pacotes 1, 3 e 5.
		s.getRouter().receivePacket(pack3_1, 0.0);
		s.getRouter().receivePacket(pack3_3, 0.0);
		s.getRouter().receivePacket(pack3_5, 0.0);
		
		// 5 pacotes no roteador.
		assertEquals(s.getRouter().getBuffer().quantityOfRemainingPackets(), 3, 0);
		
		SACK sack;
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		sack = s.getRouter().sendPackToRx(0.0);
		
		assertEquals(sack.getSequences().get(0).get(0), 3000, 0);
		assertEquals(sack.getSequences().get(0).get(1), 4500, 0);
		assertEquals(sack.getSequences().get(1).get(0), 6000, 0);
		assertEquals(sack.getSequences().get(1).get(1), 7500, 0);
		
		// Agora os pacotes 2 e 4 chegam, fora de ordem...
		Pack pack3_2 = new Pack(PackType.CommonTCP, 3, 1500L, 2999L);
		Pack pack3_4 = new Pack(PackType.CommonTCP, 3, 4500L, 5999L);
		
		s.getRouter().receivePacket(pack3_2, 0.0);
		s.getRouter().receivePacket(pack3_4, 0.0);
		
		s.getRouter().sendPackToRx(0.0);
		sack = s.getRouter().sendPackToRx(0.0);
		
		// Deve ser vazia a sequência, pois todos os pacotes chegaram, mesmo fora de ordem.
		assertEquals(sack.getSequences().size(), 0);
	}
	
	@Test
	// Entrego todos os pacotes, numa ordem aleatória.
	public void PacksInStrangeOrderIntoSameRx() {
		Sys s = new Sys(RouterType.FIFO);
		
		Pack pack3_1 = new Pack(PackType.CommonTCP, 3, 0L, 1499L);
		Pack pack3_2 = new Pack(PackType.CommonTCP, 3, 1500L, 2999L);
		Pack pack3_3 = new Pack(PackType.CommonTCP, 3, 3000L, 4499L);
		Pack pack3_4 = new Pack(PackType.CommonTCP, 3, 4500L, 5999L);
		Pack pack3_5 = new Pack(PackType.CommonTCP, 3, 6000L, 7499L);
		
		// Por enquanto, zero pacotes no roteador...
		assertEquals(s.getRouter().getBuffer().quantityOfRemainingPackets(), 0, 0);
		
		// Adiciono apenas os pacotes 1, 3 e 5.
		s.getRouter().receivePacket(pack3_3, 0.0);
		s.getRouter().receivePacket(pack3_5, 0.0);
		s.getRouter().receivePacket(pack3_2, 0.0);
		s.getRouter().receivePacket(pack3_4, 0.0);
		s.getRouter().receivePacket(pack3_1, 0.0);
		
		// 5 pacotes no roteador.
		assertEquals(s.getRouter().getBuffer().quantityOfRemainingPackets(), 5, 0);
		
		SACK sack;
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		sack = s.getRouter().sendPackToRx(0.0);
		
		// Deve ser vazia a sequência, pois todos os pacotes chegaram, mesmo fora de ordem.
		assertEquals(sack.getSequences().size(), 0);
	}
	
	@Test
	// Pacotes para 2 Rxs diferentes, fora de ordem.
	public void PacksIntoManyRxs() {
		Sys s = new Sys(RouterType.FIFO);
		
		Pack pack3_1 = new Pack(PackType.CommonTCP, 3, 0L, 1499L);
		Pack pack3_2 = new Pack(PackType.CommonTCP, 3, 1500L, 2999L);
		Pack pack3_3 = new Pack(PackType.CommonTCP, 3, 3000L, 4499L);
		Pack pack3_4 = new Pack(PackType.CommonTCP, 3, 4500L, 5999L);
		Pack pack3_5 = new Pack(PackType.CommonTCP, 3, 6000L, 7499L);
		
		Pack pack4_1 = new Pack(PackType.CommonTCP, 4, 0L, 1499L);
		Pack pack4_2 = new Pack(PackType.CommonTCP, 4, 1500L, 2999L);
		Pack pack4_3 = new Pack(PackType.CommonTCP, 4, 3000L, 4499L);
		
		s.getRouter().receivePacket(pack3_1, 0.0);
		s.getRouter().receivePacket(pack3_5, 0.0);
		s.getRouter().receivePacket(pack3_4, 0.0);
		s.getRouter().receivePacket(pack4_2, 0.0);
		s.getRouter().receivePacket(pack3_3, 0.0);
		s.getRouter().receivePacket(pack4_3, 0.0);
		s.getRouter().receivePacket(pack3_2, 0.0);
		s.getRouter().receivePacket(pack4_1, 0.0);
		
		SACK sackA;
		SACK sackB;
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		// Sei que os dois últimos pacotes são de origens diferentes :D
		sackA = s.getRouter().sendPackToRx(0.0);
		sackB = s.getRouter().sendPackToRx(0.0);
		
		assertEquals(sackA.getSequences().size(), 0);
		assertEquals(sackB.getSequences().size(), 0);
	}
	
	@Test
	// Pacotes para 2 Rxs diferentes, fora de ordem.
	public void PacksIntoManyRxsAndCongestionPackets() {
		Sys s = new Sys(RouterType.FIFO);
		
		Pack congestion0 = new Pack(PackType.Congestion, 0);
		Pack congestion1 = new Pack(PackType.Congestion, 0);
		Pack congestion2 = new Pack(PackType.Congestion, 0);
		Pack congestion3 = new Pack(PackType.Congestion, 0);
		Pack congestion4 = new Pack(PackType.Congestion, 0);
		Pack congestion5 = new Pack(PackType.Congestion, 0);
		Pack congestion6 = new Pack(PackType.Congestion, 0);
		Pack congestion7 = new Pack(PackType.Congestion, 0);
		Pack congestion8 = new Pack(PackType.Congestion, 0);
		Pack congestion9 = new Pack(PackType.Congestion, 0);
		
		Pack pack3_1 = new Pack(PackType.CommonTCP, 3, 0L, 1499L);
		Pack pack3_2 = new Pack(PackType.CommonTCP, 3, 1500L, 2999L);
		Pack pack3_3 = new Pack(PackType.CommonTCP, 3, 3000L, 4499L);
		Pack pack3_4 = new Pack(PackType.CommonTCP, 3, 4500L, 5999L);
		Pack pack3_5 = new Pack(PackType.CommonTCP, 3, 6000L, 7499L);
		
		Pack pack4_1 = new Pack(PackType.CommonTCP, 4, 0L, 1499L);
		Pack pack4_2 = new Pack(PackType.CommonTCP, 4, 1500L, 2999L);
		Pack pack4_3 = new Pack(PackType.CommonTCP, 4, 3000L, 4499L);
		
		s.getRouter().receivePacket(congestion1, 0.0);
		s.getRouter().receivePacket(pack3_1, 0.0);
		s.getRouter().receivePacket(congestion0, 0.0);
		s.getRouter().receivePacket(congestion2, 0.0);
		s.getRouter().receivePacket(pack3_5, 0.0);
		s.getRouter().receivePacket(congestion3, 0.0);
		s.getRouter().receivePacket(pack3_4, 0.0);
		s.getRouter().receivePacket(congestion4, 0.0);
		s.getRouter().receivePacket(pack4_2, 0.0);
		s.getRouter().receivePacket(congestion5, 0.0);
		s.getRouter().receivePacket(pack3_3, 0.0);
		s.getRouter().receivePacket(congestion6, 0.0);
		s.getRouter().receivePacket(pack4_3, 0.0);
		s.getRouter().receivePacket(congestion7, 0.0);
		s.getRouter().receivePacket(pack3_2, 0.0);
		s.getRouter().receivePacket(congestion8, 0.0);
		s.getRouter().receivePacket(pack4_1, 0.0);
		s.getRouter().receivePacket(congestion9, 0.0);
		
		SACK sackA;
		SACK sackB;
		SACK sackC;
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		s.getRouter().sendPackToRx(0.0);
		
		sackA = s.getRouter().sendPackToRx(0.0);
		sackB = s.getRouter().sendPackToRx(0.0);
		sackC = s.getRouter().sendPackToRx(0.0);
		
		s.getRouter().sendPackToRx(0.0);
		
		assertEquals(sackA.getSequences().size(), 0);
		assertEquals(sackC.getSequences().size(), 0);
		assertNull(sackB);
		assertEquals(s.getRouter().getBuffer().quantityOfRemainingPackets(), 0, 0);
	}
	
}
