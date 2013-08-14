package views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ViewsController implements ActionListener {
	
	private StartWindow startWindow;
	//private ParamsController paramsController;
	
	public ViewsController() {
		startWindow = new StartWindow();
		
		startWindow.getDefineParams().addActionListener(this);
		startWindow.getRunSimulator().addActionListener(this);
		startWindow.getCongestionWindow().addActionListener(this);
		startWindow.getFlowEventWindow().addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if ("Sair".equals(e.getActionCommand())) {
				startWindow.close();
			} else if ("DefinirDados".equals(e.getActionCommand())) {
				new ParamsController();
			} else if ("RodarSimulador".equals(e.getActionCommand())) {
				new RunSimulator();
			} else if ("GerarGraficoCongestionWindow".equals(e.getActionCommand())) {
				new CongestionWindowController();
			} else if ("GerarGraficoVazaoEventos".equals(e.getActionCommand())) {
				new FlowEventController();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}  finally {
			System.out.println("Final da execução!");
		}
	}
	
	public static void main(String[] args) {
		new ViewsController();
	}

}
