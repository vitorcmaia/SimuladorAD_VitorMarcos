package network;
import java.util.ArrayList;

import maths.SimulationProperties;

import enums.Group;
import enums.RouterType;

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
	
	/**
	 * Construtor. Cria os Tx e Rx, de acordo com os parâmetros do simulador.
	 * @param routerType
	 */
	public Sys(RouterType routerType) {
		int index = 0;
		for(int i = 0 ; i < SimulationProperties.getQuantityOfG1() ; i++)
			txs.add(new Tx(Group.Group1, index++));
		
		for(int i = 0 ; i < SimulationProperties.getQuantityOfG2() ; i++)
			txs.add(new Tx(Group.Group2, index++));
		
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
