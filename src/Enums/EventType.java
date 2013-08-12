package enums;

public enum EventType {
	// Eventos de confirmação de chegada
	CongestionPacketIntoRouter,    // Pacote de tráfego de fundo é adicionado ao roteador.
	TxPacketIntoRouter,            // Pacote de um Tx é adicionado ao roteador.
	
	// Eventos de confirmação de envio.
	TxPacketHeadsToRouter,         // Pacote normal de sessão TCP foi transmitido, mas ainda não chegou ao roteador.
	RouterSuccessfullySentPacket,  // Pacote foi entregue corretamente no Rx correspondente.
	SackArrives,                   // Um Sack chega no Tx correspondente.
	
	Timeout,                       // Possível timeout no envio de um pacote.
}
