package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Params extends Properties {
	
	public final static String pathParams = "params.txt"; // Caminho onde será salvo o arquivo com os parâmetros
	
	public final static long MSS = 1500L;
	
	// Intialize the recorded params on a file
	public Params() throws IOException {
		File fileParams = new File(pathParams);
		fileParams.createNewFile();
		FileInputStream in = new FileInputStream(fileParams);
		this.load(in);
		in.close();
	}
	
	public void save() throws IOException {
		FileOutputStream out = new FileOutputStream(Params.pathParams);
		this.store(out, "Mudança de Parâmetros");
		out.close();
	}
	
	public String getCsProperty() {
		return this.getProperty("cs", "1E9");
	}
	
	// Retorna a taxa de transmissão entre o TCP e o roteador
	public double getCs() {
		return Double.parseDouble(this.getProperty("cs", "1E9"));
	}
	
	public String getCgProperty() {
		return this.getProperty("cg", "10E6");
	}
	
	public double getCg() {
		return Double.parseDouble(this.getProperty("cg", "10E6"));
	}
	
	public String getTp1Property() {
		return this.getProperty("tp1", "100");
	}
	
	// Retorna o tempo de propagação para o grupo 1 em 'ms'
	public double getTp1() {
		return Double.parseDouble(getTp1Property());
	}
	
	public String getTp2Property() {
		return this.getProperty("tp2", "50");
	}
	
	// Retorna o tempo de propagação para o grupo 2 em 'ms'
	public double getTp2() {
		return Double.parseDouble(getTp2Property());
	}
	
	public String getAverageBackTrafficProperty() {
		return this.getProperty("averageBackTraffic", "10");
	}
	
	// Retorna a taxa média de pacotes por rajada do tráfego de fundo
	public double getAverageBackTraffic() {
		return Double.parseDouble(getAverageBackTrafficProperty());
	}
	
	public String getTimeBetweenSquallProperty() {
		return this.getProperty("timeBetweenSquall", "24");
	}
	
	// Retorna o tempo médio entre as rajdas
	public double getTimeBetweenSquall() {
		return Double.parseDouble(getTimeBetweenSquallProperty());
	}
	
	public String getBufferSizeProperty() {
		return this.getProperty("bufferSize", "40");
	}
	
	public double getBufferSize() {
		return Double.parseDouble(getBufferSizeProperty());
	}
	
	public String getNumberStationsGroup1Property() {
		return this.getProperty("numberStationsGroup1", "10");
	}
	
	public double getNumberStationsGroup1() {
		return Double.parseDouble(getNumberStationsGroup1Property());
	}
	
	public String getNumberStationsGroup2Property() {
		return this.getProperty("numberStationsGroup2", "10");
	}
	
	public double getNumberStationsGroup2() {
		return Double.parseDouble(getNumberStationsGroup2Property());
	}
	
	public String getAckG1PropagationTimeProperty() {
		return this.getProperty("ackG1PropagationTime", "100");
	}
	
	public double getAckG1PropagationTime() {
		return Double.parseDouble(getAckG1PropagationTimeProperty());
	}
	
	public String getAckG2PropagationTimeProperty() {
		return this.getProperty("ackG2PropagationTime", "50");
	}
	
	public double getAckG2PropagationTime() {
		return Double.parseDouble(getAckG2PropagationTimeProperty());
	}

}
