package network;

import enums.Group;

public class Tx {
	/**
	 * Grupo do Tx, que pode ser 1 ou 2.
	 */
	private Group group;
	
	/**
	 * Índice do Tx, usado para o roteador saber qual Rx deve receber um pacote.
	 * O Tx k deve ter seus pacotes entregues no Rx k.
	 */
	private Integer index;
	
	public Tx(Group group, Integer index) {
		this.group = group;
		this.index = index;
	}
	
	/**
	 * Informa o grupo do Tx.
	 * @return Grupo do Tx.
	 */
	public Group getGroup() {
		return group;
	}
	
	/**
	 * Informa o índice do Tx.
	 * @return Índice do Tx.
	 */
	public Integer getIndex() {
		return index;
	}
}
