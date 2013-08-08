package network;

import java.util.ArrayList;

public class Rx {
	
	/**
	 * Próximo byte que deveria vir na sequência.
	 */
	private Long nextExpectedByte = 0L;
	
	/**
	 * Sequências de pacotes que chegaram fora de ordem.
	 * Se não estiver vazio, significa que algum pacote foi perdido.
	 */
	private ArrayList<ArrayList<Long>> receivedSequences = new ArrayList<ArrayList<Long>>();
	
	private ArrayList<ArrayList<Long>> allReceivedSequences() {
		return receivedSequences;
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
		if(pack.getStartingByte().compareTo(nextExpectedByte) < 0) {
			sack = new SACK(pack.getDestination(), nextExpectedByte, allReceivedSequences());
		} else if(pack.getStartingByte().equals(nextExpectedByte)) {
			update(pack);
			ArrayList<Long> seq = receivedSequences.get(0);
			
			if(seq.size() >= 2) {
				nextExpectedByte = seq.get(1);
			}
			
			this.receivedSequences.remove(0);
			
			sack = new SACK(pack.getDestination(), nextExpectedByte, allReceivedSequences());
		} else {
			update(pack);
			sack = new SACK(pack.getDestination(), nextExpectedByte, allReceivedSequences());
		}
		
		sack.setOriginalPackSendingTime(pack.getStartSendingTime());
		
		return sack;
	}
	
	private void update(Pack p) {
		ArrayList<Long> bounds = new ArrayList<Long>();
		bounds.add(p.getStartingByte());
		bounds.add(p.getEndingByte() + 1);
		if (receivedSequences.size() == 0) {
			receivedSequences.add(bounds);
			return;
		} else {
			if (receivedSequences.get(0).get(0).compareTo(p.getEndingByte()) > 0) {
				if (receivedSequences.get(0).get(0).equals(p.getEndingByte() + 1)) {
					receivedSequences.get(0).set(0, p.getStartingByte());
					return;
				} else {
					receivedSequences.add(0, bounds);
					return;
				}
			}
			for (int i = receivedSequences.size() - 1; i >= 0; i--) {
				if (sequenceContainsPack(p, i)) {
					return;
				}

				if (receivedSequences.get(i).get(1) <= p.getStartingByte()) {
					include(p, i);
					return;
				}
			}
		}
	}
	
	private void include(Pack p, int i) {
		if (receivedSequences.get(i).get(1).equals(p.getStartingByte())) {
			receivedSequences.get(i).set(1, p.getEndingByte() + 1);

			if (i < receivedSequences.size() - 1) {
				if (receivedSequences.get(i).get(1).equals(receivedSequences.get(i + 1).get(0))) {
					receivedSequences.get(i).set(1, receivedSequences.get(i + 1).get(1));
					receivedSequences.remove(i + 1);
				}
			}
		} else {
			if (i < receivedSequences.size() - 1
					&& receivedSequences.get(i + 1).get(0).equals(p.getEndingByte() + 1)) {
				receivedSequences.get(i + 1).set(0, p.getStartingByte());
			} else {
				ArrayList<Long> seq = new ArrayList<Long>();
				seq.add(p.getStartingByte());
				seq.add(p.getEndingByte() + 1);
				
				receivedSequences.add(i + 1, seq);
			}
		}
	}
	
	private boolean sequenceContainsPack(Pack p, int seq) {
		if(receivedSequences.get(seq).get(0) <= p.getStartingByte()
				&& receivedSequences.get(seq).get(1) > p.getEndingByte()) {
			return true;
		} else {
			return false;
		}
	}
}
