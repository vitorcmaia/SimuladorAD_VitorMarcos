package views;

import java.io.IOException;

import javax.swing.JFrame;

import simulation.Simulation;

public class CongestionWindowController {
	
	private CongestionWindowWindow congestionWindowWindow;
	private Simulation simulation;
	
	public CongestionWindowController() throws IOException {
		this.simulation = new Simulation();
		
		congestionWindowWindow = new CongestionWindowWindow(simulation.getMapCurrentTimePerCwndMSS());
	}
	
	public CongestionWindowWindow getCongestionWindowWindow() {
		return congestionWindowWindow;
	}
	
	public static void main(String[] args) throws IOException {
		CongestionWindowController controller = new CongestionWindowController();
		controller.getCongestionWindowWindow().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
