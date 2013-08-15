package views;

import java.io.IOException;

import javax.swing.JFrame;

import enums.RouterType;

import network.Scenarios;

import simulation.Simulation;

public class CongestionWindowController {
	
	private CongestionWindowWindow congestionWindowWindow;
	private Simulation simulation;
	
	public CongestionWindowController() throws IOException {
		Scenarios.Scenario1_Task1(RouterType.RED);
		
		congestionWindowWindow = new CongestionWindowWindow(Scenarios.ResultsGroup1_RED);
	}
	
	public CongestionWindowWindow getCongestionWindowWindow() {
		return congestionWindowWindow;
	}
	
	public static void main(String[] args) throws IOException {
		CongestionWindowController controller = new CongestionWindowController();
		controller.getCongestionWindowWindow().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
