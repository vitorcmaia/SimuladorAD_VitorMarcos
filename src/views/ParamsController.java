package views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;

import network.Params;

public class ParamsController implements ActionListener {

	private Params params;
	private DefineParams defineParams;
	
	public ParamsController() throws IOException {
		params = new Params();
		defineParams = new DefineParams();
		
		defineParams.getCs().setText(params.getCsProperty());
		defineParams.getCg().setText(params.getCgProperty());
		defineParams.getTp1().setText(params.getTp1Property());
		defineParams.getTp2().setText(params.getTp2Property());
		defineParams.getAverageBackTraffic().setText(params.getAverageBackTrafficProperty());
		defineParams.getTimeBetweenSquall().setText(params.getTimeBetweenSquallProperty());
		defineParams.getBufferSize().setText(params.getBufferSizeProperty());
		defineParams.getNumberStationsGroup1().setText(params.getNumberStationsGroup1Property());
		defineParams.getNumberStationsGroup2().setText(params.getNumberStationsGroup2Property());
		defineParams.getAckG1PropagationTime().setText(params.getAckG1PropagationTimeProperty());
		defineParams.getAckG2PropagationTime().setText(params.getAckG2PropagationTimeProperty());
		
		defineParams.getSave().addActionListener(this);
	} 

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("Salvar".equals(e.getActionCommand())) {
			try {
				params.setProperty("cs", defineParams.getCs().getText());
				params.setProperty("cg", defineParams.getCg().getText());
				params.setProperty("tp1", defineParams.getTp1().getText());
				params.setProperty("tp2", defineParams.getTp2().getText());
				params.setProperty("averageBackTraffic", defineParams.getAverageBackTraffic().getText());
				params.setProperty("timeBetweenSquall", defineParams.getTimeBetweenSquall().getText());
				params.setProperty("bufferSize", defineParams.getBufferSize().getText());
				params.setProperty("numberStationsGroup1", defineParams.getNumberStationsGroup1().getText());
				params.setProperty("numberStationsGroup2", defineParams.getNumberStationsGroup2().getText());
				params.setProperty("ackG1PropagationTime", defineParams.getAckG1PropagationTime().getText());
				params.setProperty("ackG2PropagationTime", defineParams.getAckG2PropagationTime().getText());
			} /*catch (IOException e1) {
				e1.printStackTrace();
			}*/ finally {
				System.out.println("Final da execução!");
			}
		}		
	}
	
	public static void main(String args[]) throws IOException {
		ParamsController controller = new ParamsController();
		controller.defineParams.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
