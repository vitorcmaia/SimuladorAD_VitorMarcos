package views;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DefineParams extends JFrame {
	
	private JTextField cs; // Taxa de transmissão dos servidores
	private JTextField cg; // Taxa de esvaziamento da fila
	private JTextField tp1; // Atraso de propagação para o grupo 1
	private JTextField tp2; // Atraso de propagação para o grupo 2
	private JTextField averageBackTraffic; // Taxa média do tráfego de fundo
	private JTextField timeBetweenSquall; // Tempo médio entre as rajadas
	private JTextField bufferSize; // Tamanho do buffer do roteador
	private JTextField numberStationsGroup1; // Número de estações para o grupo 1
	private JTextField numberStationsGroup2; // Número de estações para o grupo 2
	private JTextField ackG1PropagationTime; // Tempo de chegada do Ack 1
	private JTextField ackG2PropagationTime; // Tempo de chegada do Ack 2
//	private JComboBox<String> routerPolitic;
	private JButton save;
	
	public DefineParams() {
		super("Definir Dados da Simulação");
		start();
	}
	
	private void start() {
		this.setLayout(new GridLayout(0, 2));
		
		this.add(new JLabel("Taxa de Transmissão dos Servidores (em bps): "));
		this.cs = new JTextField();
		this.add(cs);
		
		this.add(new JLabel("Taxa de Esvaziamento da Fila (em bps): "));
		this.cg = new JTextField();
		this.add(cg);
		
		this.add(new JLabel("Atraso de Propagação para o Grupo 1 (em ms): "));
		this.tp1 = new JTextField();
		this.add(tp1);
		
		this.add(new JLabel("Atraso de Propagação para o Grupo 2 (em ms): "));
		this.tp2 = new JTextField();
		this.add(tp2);
		
		this.add(new JLabel("Táxa Média do Tráfego de Fundo: "));
		this.averageBackTraffic = new JTextField();
		this.add(averageBackTraffic);
		
		this.add(new JLabel("Tempo Médio entre as Rajadas (em ms): "));
		this.timeBetweenSquall = new JTextField();
		this.add(timeBetweenSquall);
		
		this.add(new JLabel("Tamanho do Buffer do Roteador (em número de pacotes): "));
		this.bufferSize = new JTextField();
		this.add(bufferSize);
		
		this.add(new JLabel("Número de Estações para o Grupo 1: "));
		this.numberStationsGroup1= new JTextField();
		this.add(numberStationsGroup1);
		
		this.add(new JLabel("Número de Estações para o Grupo 2: "));
		this.numberStationsGroup2 = new JTextField();
		this.add(numberStationsGroup2);
		
		this.add(new JLabel("Tempo de chegada do Ack 1"));
		this.ackG1PropagationTime = new JTextField();
		this.add(ackG1PropagationTime);
		
		this.add(new JLabel("Tempo de chegada do Ack 2"));
		this.ackG2PropagationTime = new JTextField();
		this.add(ackG2PropagationTime);
		
		this.add(new JLabel("Selecione a Política do Roteador: "));
		String[] routers = new String[2];
		routers[0] = "FIFO";
		routers[1] = "RED";
//		this.routerPolitic = new JComboBox<String>(routers);
//		this.add(routerPolitic);
		
		save = new JButton("Salvar");
		save.setActionCommand("Salvar");
		this.add(save);
		
		this.pack();
		this.setVisible(true);
	}
	
	public void close() {
		WindowEvent closeEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeEvent);
	}
	
	public JTextField getCs() {
		return cs;
	}
	
	public JTextField getCg() {
		return  cg;
	}
	
	public JTextField getTp1() {
		return tp1;
	}
	
	public JTextField getTp2() {
		return tp2;
	}
	
	public JTextField getAverageBackTraffic() {
		return averageBackTraffic;
	}
	
	public JTextField getTimeBetweenSquall() {
		return timeBetweenSquall;
	}
	
	public JTextField getBufferSize() {
		return bufferSize;
	}
	
	public JTextField getNumberStationsGroup1() {
		return numberStationsGroup1;
	}
	
	public JTextField getNumberStationsGroup2() {
		return numberStationsGroup2;
	}
	
	public JTextField getAckG1PropagationTime() {
		return ackG1PropagationTime;
	}
	
	public JTextField getAckG2PropagationTime() {
		return ackG2PropagationTime;
	}
	
//	public JComboBox<String> getRouterPolitic() {
//		return routerPolitic;
//	}
	
	public JButton getSave() {
		return save;
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new DefineParams();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}
	
}
