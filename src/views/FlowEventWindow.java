package views;

import java.awt.Dimension;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

public class FlowEventWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	JFreeChart chart;
	DefaultXYDataset ds;
	
	public FlowEventWindow(TreeMap<Double, Double> treeMap) {
		super("Gráfico Vazão x Número de Eventos");
		
		int size = treeMap.entrySet().size();
		int cont = 0;		
		double[][] data = new double[2][size];
		
		for (Entry<Double, Double> entry : treeMap.entrySet()) {
			data[0][cont] = entry.getKey();
			data[1][cont] = entry.getValue();
		}
			
		this.setPreferredSize(new Dimension(800, 600));
		this.setExtendedState(MAXIMIZED_BOTH);
		this.ds = new DefaultXYDataset();
		this.ds.addSeries("CWND/MSS Tx0", data);
		
		this.chart= ChartFactory.createXYLineChart("Vazão Média x Número de Eventos", "Eventos", "Vázão Média (bps)", ds, PlotOrientation.VERTICAL, true, true, false);
		
		ChartPanel cp = new ChartPanel(this.chart);
		this.add(cp);
		this.pack();
		this.setVisible(true);
	}
}
