package views;

import java.awt.Dimension;
import java.util.Map.Entry;
import java.util.TreeMap;

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
	
	public CongestionWindowWindow(TreeMap<Double, Double> treeMap) {
		
		super("Gráfico CongestionWindow/MSS x Tempo");
		
		int size = treeMap.entrySet().size();
		int cont = 0;		
		double[][] data = new double[2][size];
		
		for (Entry<Double, Double> entry : treeMap.entrySet()) {
			data[0][cont] = entry.getKey();
			data[1][cont] = entry.getValue();
		}
		
		
		
		this.setPreferredSize(new Dimension(600, 400));
		this.setExtendedState(MAXIMIZED_BOTH);		
		this.ds = new DefaultXYDataset();
		this.ds.addSeries("CWND/MSS Tx0", data);
		
		this.chart= ChartFactory.createXYLineChart("CNWD x Tempo", "Tempo (ms)", "CNWD/MSS (pacotes)", ds, PlotOrientation.VERTICAL, true, true, false);
		
		ChartPanel cp = new ChartPanel(this.chart);
		this.add(cp);
		this.pack();
		this.setVisible(true);
	}
	
}
