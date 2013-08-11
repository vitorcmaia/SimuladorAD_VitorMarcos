package views;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

// Essa é a janela principal/inicial da aplicação

public class StartWindow extends JFrame {
	
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem runSimulator;
	private JMenuItem exit;
	private JMenuItem defineParams; // Opção que permite definir os Dados da Simulação
	
	public StartWindow() {
		super("Trabalho de Simulação - Avaliação de Desempenho");
		start();
	}
	
	private void start() {
		this.setPreferredSize(new Dimension(800, 600));
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		menuBar.add(menu);
		
		menu.setMnemonic(KeyEvent.VK_M); //Associa comandos
		
		// Definições da janela para a tela de definição de parâmetros
		defineParams = new JMenuItem("Definir Dados da Simulação");
		defineParams.setActionCommand("DefinirDados");
		menu.add(defineParams);
		
		runSimulator = new JMenuItem("Rodar Simulador");
		runSimulator.setActionCommand("RodarSimulador");
		menu.add(runSimulator);
		
		menu.addSeparator();
		
		exit = new JMenuItem("Sair");
		exit.setActionCommand("Exit");
		menu.add(exit);
		
		menuBar.setOpaque(true);
		this.setJMenuBar(menuBar);
		
		this.pack();
		this.setVisible(true);		
	}

	public void close() {
		WindowEvent closeEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closeEvent);
	}
	
	public JMenuItem getDefineParams () {
		return defineParams;
	}
	
	public JMenuItem getRunSimulator() {
		return runSimulator;
	}
	
	public JMenuItem getExit() {
		return exit;
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new StartWindow();
			}
		});
	}
	
}