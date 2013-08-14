package views;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

public class CongestionWindowWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	JFreeChart chart;
	DefaultXYDataset ds;
	
	public CongestionWindowWindow(double[][] timeXbit) {
		super("Gráfico CongestionWindow/MSS x Tempo");
		
		this.setPreferredSize(new Dimension(600, 400));
		this.setExtendedState(MAXIMIZED_BOTH);		
		this.ds = new DefaultXYDataset();
		this.ds.addSeries("CWND/MSS Tx0", timeXbit);
		
		this.chart= ChartFactory.createXYLineChart("CNWD x Tempo", "Tempo (ms)", "CNWD/MSS (pacotes)", ds, PlotOrientation.VERTICAL, true, true, false);
		
		ChartPanel cp = new ChartPanel(this.chart);
		this.add(cp);
		this.pack();
		this.setVisible(true);
	}
	
}
