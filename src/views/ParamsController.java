package views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParamsController implements ActionListener {

	private Params params;
	private DefineParams defineParams;
	
	public ParamsController() throws IOException {
		params = new Params();
		defineParams = new DefineParams();
		
		defineParams.getCs().setText(params.getCs());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
