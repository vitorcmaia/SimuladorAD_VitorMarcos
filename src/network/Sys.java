package network;
import java.util.ArrayList;

import maths.SimulationProperties;

import enums.Group;
import enums.RouterType;

/**
 * Representa o conjunto das estruturas do sistema: RXs, TXs e Roteador.
 */
public class Sys {
	
	/**
	 * Conjunto de Txs do sistema.Geram os pacotes.
	 */
	private ArrayList<Tx> txs = new ArrayList<Tx>();
	
	/**
	 * Conjunto de Rxs do sistema. Recebem os pacotes e retornam Sack para o Tx.
	 */
	private ArrayList<Rx> rxs = new ArrayList<Rx>();
	
	/**
	 * Roteador do sistema. Enfilera pacotes e os envia ao Rx.
	 */
	private Router router;
	
	public ArrayList<Tx> getTxs() {
		return txs;
	}

	public ArrayList<Rx> getRxs() {
		return rxs;
	}
	
	/**
	 * Construtor. Cria os Tx e Rx, de acordo com os parâmetros do simulador.
	 * @param routerType
	 */
	public Sys(RouterType routerType) {
		int index = 0;
		for(int i = 0 ; i < SimulationProperties.getQuantityOfG1() ; i++) {
			Tx tx = new Tx(Group.Group1, index++);
			tx.setRtt(2 * SimulationProperties.getTP1() + SimulationProperties.getAckG1PropagationTime());
			txs.add(tx);
		}
		
		for(int i = 0 ; i < SimulationProperties.getQuantityOfG2() ; i++) {
			Tx tx = new Tx(Group.Group2, index++);
			tx.setRtt(2 * SimulationProperties.getTP2() + SimulationProperties.getAckG2PropagationTime());
			txs.add(tx);
		}
		
		for(@SuppressWarnings("unused") Tx tx : txs)
			rxs.add(new Rx());
		
		router = new Router(routerType);
		router.setRxs(rxs);
	}
	
	/**
	 * Acessa o roteador.
	 * @return O roteador do sistema.
	 */
	public Router getRouter() {
		return router;
	}
}
