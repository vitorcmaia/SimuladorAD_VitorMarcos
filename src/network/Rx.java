package network;

import java.util.ArrayList;

public class Rx {
	
	/**
	 * Pr�ximo byte que deveria vir na sequ�ncia.
	 */
	private Long nextExpectedByte = 0L;
	
	/**
	 * Sequ�ncias de pacotes que chegaram fora de ordem.
	 * Se n�o estiver vazio, significa que algum pacote foi perdido.
	 */
	private ArrayList<ArrayList<Long>> sequencesReceivedOutOfOrder = new ArrayList<ArrayList<Long>>();
	
	/**
	 * Se esta matriz n�o for vazia, significa que pacotes n�o chegaram, ou chegaram fora de ordem.
	 */
	private ArrayList<ArrayList<Long>> getSequencesReceivedOutOfOrder() {
		return sequencesReceivedOutOfOrder;
	}
	
	public Long getNextExpectedByte() {
		return nextExpectedByte;
	}

	public void setNextExpectedByte(Long nextExpectedByte) {
		this.nextExpectedByte = nextExpectedByte;
	}
	
	/**
	 * Recebe um pacote do roteador e retorna um Sack atualizado com esta chegada.
	 * @param pack O pacote que chegou no Rx.
	 * @return O Sack.
	 */
	public SACK receivePacket(Pack pack) {
		SACK sack = null;
		if(pack.getStartingByte().compareTo(nextExpectedByte) < 0);
			// Neste caso apenas retorna o Sack como j� est�...
		else if(pack.getStartingByte().equals(nextExpectedByte)) {
			update(pack);
			ArrayList<Long> seq = sequencesReceivedOutOfOrder.get(0);
			sequencesReceivedOutOfOrder.remove(0);
			if(seq.size() >= 2) nextExpectedByte = seq.get(1);
		} else {
			update(pack);
		}
		
		sack = new SACK(pack.getDestination(), nextExpectedByte, getSequencesReceivedOutOfOrder());
		sack.setOriginalPackSendingTime(pack.getStartSendingTime());
		return sack;
	}
	
	private void update(Pack p) {
		ArrayList<Long> bounds = new ArrayList<Long>();
		bounds.add(p.getStartingByte());
		bounds.add(p.getEndingByte() + 1);
		if (sequencesReceivedOutOfOrder.size() == 0) {
			sequencesReceivedOutOfOrder.add(bounds);
			return;
		} else {
			if (sequencesReceivedOutOfOrder.get(0).get(0).compareTo(p.getEndingByte()) > 0) {
				if (sequencesReceivedOutOfOrder.get(0).get(0).equals(p.getEndingByte() + 1)) {
					sequencesReceivedOutOfOrder.get(0).set(0, p.getStartingByte());
				} else sequencesReceivedOutOfOrder.add(0, bounds);
				return;
			}
			for (int i = sequencesReceivedOutOfOrder.size() - 1; i >= 0; i--) {
				if (sequenceContainsPack(p, i)) return;
				if (sequencesReceivedOutOfOrder.get(i).get(1) <= p.getStartingByte()) {
					include(p, i);
					return;
				}
			}
		}
	}
	
	private void include(Pack p, int i) {
		if (sequencesReceivedOutOfOrder.get(i).get(1).equals(p.getStartingByte())) {
			sequencesReceivedOutOfOrder.get(i).set(1, p.getEndingByte() + 1);

			if (i < sequencesReceivedOutOfOrder.size() - 1) {
				if (sequencesReceivedOutOfOrder.get(i).get(1).equals(sequencesReceivedOutOfOrder.get(i + 1).get(0))) {
					sequencesReceivedOutOfOrder.get(i).set(1, sequencesReceivedOutOfOrder.get(i + 1).get(1));
					sequencesReceivedOutOfOrder.remove(i + 1);
				}
			}
		} else {
			if (i < sequencesReceivedOutOfOrder.size() - 1
					&& sequencesReceivedOutOfOrder.get(i + 1).get(0).equals(p.getEndingByte() + 1)) {
				sequencesReceivedOutOfOrder.get(i + 1).set(0, p.getStartingByte());
			} else {
				ArrayList<Long> seq = new ArrayList<Long>();
				seq.add(p.getStartingByte());
				seq.add(p.getEndingByte() + 1);
				
				sequencesReceivedOutOfOrder.add(i + 1, seq);
			}
		}
	}
	
	private boolean sequenceContainsPack(Pack p, int seq) {
		if(sequencesReceivedOutOfOrder.get(seq).get(0) <= p.getStartingByte()
				&& sequencesReceivedOutOfOrder.get(seq).get(1) > p.getEndingByte()) {
			return true;
		} else {
			return false;
		}
	}
}
