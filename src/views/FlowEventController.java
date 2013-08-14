package views;

import java.io.IOException;

import javax.swing.JFrame;

import network.Scenarios;
import simulation.Simulation;
import enums.RouterType;

public class FlowEventController {

	private FlowEventWindow flowEventWindow;
	private Simulation simulation;
	
	public FlowEventController() throws IOException {		
		Scenarios.Scenario2_Task1(RouterType.FIFO);
		
		flowEventWindow = new FlowEventWindow(Scenarios.All10TxFlows_FIFO.get(0));
	}
	
	public FlowEventWindow getFlowEventWindow() {
		return flowEventWindow;
	}
	
	public static void main(String[] args) throws IOException {
		FlowEventController controller = new FlowEventController();
		controller.getFlowEventWindow().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
