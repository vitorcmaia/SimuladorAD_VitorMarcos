package network;

import java.util.Map.Entry;
import java.util.TreeMap;

import enums.RouterType;

import simulation.Simulation;
import maths.SimulationProperties;

/**
 * Executa os cenários. Cada um em um método.
 */
public class Scenarios {
	
	// ATENÇÃO: Resultados separados pelo tipo de política do roteador!!
	//-------------------------------------
	// Cenário 1, Tarefa 1:
	// Armazenam dados no formato Tempo x Cwnd/MSS
	private static TreeMap<Double, Double> ResultsGroup1_FIFO;
	private static TreeMap<Double, Double> ResultsGroup2_FIFO;
	private static TreeMap<Double, Double> ResultsGroup1_RED;
	private static TreeMap<Double, Double> ResultsGroup2_RED;
	//-------------------------------------
	// Cenário 1, Tarefa 2:
	// Média e distância média de IC para os grupos 1 e 2.
	private static Double g1Average_FIFO;
	private static Double g1IC_FIFO;
	private static Double g2Average_FIFO;
	private static Double g2IC_FIFO;
	private static Double g1Average_RED;
	private static Double g1IC_RED;
	private static Double g2Average_RED;
	private static Double g2IC_RED;
	//-------------------------------------
	
	/**
	 * Calcular o comportamento de CongestionWindow/MSS ao longo do tempo para
	 * apenas uma sessão TCP. Com tráfego de fundo. Cg com valor 5Mbps.
	 * Executar para Grupo 1 e em seguida para grupo 2.
	 */
	public static void Scenario1_Task1(RouterType type) {
		// Defino que terá tráfego de fundo.
		SimulationProperties.setWithCongestion(true);
		// Mudo o valor do Cg para 5Mbps.
		SimulationProperties.setCg((10E6)/2.0);
		// Defino uma conexão do Grupo 1 e nenhuma do grupo 2.
		SimulationProperties.setQuantityOfG1(1);
		SimulationProperties.setQuantityOfG2(0);
		// Defino 100 mil eventos na simulação, para que seja longa.
		SimulationProperties.setEventsInARow(100000);
		// Defino a política do roteador, de acordo com o parâmetor passado no método.
		SimulationProperties.setRouterType(type);
		
		Simulation simulation = new Simulation();
		simulation.run();
		
		if(SimulationProperties.getRouterType().equals(RouterType.FIFO))
			ResultsGroup1_FIFO = simulation.getMapCurrentTimePerCwndMSS();
		else 
			ResultsGroup1_RED = simulation.getMapCurrentTimePerCwndMSS();
		
		// Defino uma conexão do Grupo 2 e nenhuma do grupo 1.
		SimulationProperties.setQuantityOfG1(0);
		SimulationProperties.setQuantityOfG2(1);
		
		simulation = new Simulation();
		simulation.run();
		
		if(SimulationProperties.getRouterType().equals(RouterType.FIFO))
			ResultsGroup2_FIFO = simulation.getMapCurrentTimePerCwndMSS();
		else 
			ResultsGroup2_RED = simulation.getMapCurrentTimePerCwndMSS();
	}
	
	/**
	 * Obter a vazão da sessão TCP para cada grupo, com IC de 90%.
	 */
	public static void Scenario1_Task2(RouterType type) {
		// Defino que terá tráfego de fundo.
		SimulationProperties.setWithCongestion(true);
		// Mudo o valor do Cg para 5Mbps.
		SimulationProperties.setCg((10E6)/2.0);
		// Defino uma conexão do Grupo 1 e nenhuma do grupo 2.
		SimulationProperties.setQuantityOfG1(1);
		SimulationProperties.setQuantityOfG2(0);
		// Defino 100 mil eventos na simulação, para que seja longa.
		SimulationProperties.setEventsInARow(100000);
		// Defino a política do roteador, de acordo com o parâmetor passado no método.
		SimulationProperties.setRouterType(type);
		
		Simulation simulation = new Simulation();
		simulation.run();
		
		if(SimulationProperties.getRouterType().equals(RouterType.FIFO)) {
			g1Average_FIFO = simulation.getStatisticsPerTx().get(0).estimateAverage();
			g1IC_FIFO  = simulation.getStatisticsPerTx().get(0).getAverageConfidenceIntervalDistance(0.9);
		}
		else {
			g1Average_RED = simulation.getStatisticsPerTx().get(0).estimateAverage();
			g1IC_RED  = simulation.getStatisticsPerTx().get(0).getAverageConfidenceIntervalDistance(0.9);
		}
		
		// Defino uma conexão do Grupo 2 e nenhuma do grupo 1.
		SimulationProperties.setQuantityOfG1(0);
		SimulationProperties.setQuantityOfG2(1);
		
		simulation = new Simulation();
		simulation.run();
		
		if(SimulationProperties.getRouterType().equals(RouterType.FIFO)) {
			g2Average_FIFO = simulation.getStatisticsPerTx().get(0).estimateAverage();
			g2IC_FIFO  = simulation.getStatisticsPerTx().get(0).getAverageConfidenceIntervalDistance(0.9);
		}
		else {
			g2Average_RED = simulation.getStatisticsPerTx().get(0).estimateAverage();
			g2IC_RED  = simulation.getStatisticsPerTx().get(0).getAverageConfidenceIntervalDistance(0.9);
		}
	}
	
	public static void main(String[] args) {
		Scenario1_Task1(RouterType.FIFO);
		Scenario1_Task1(RouterType.RED);
		Scenario1_Task2(RouterType.FIFO);
		Scenario1_Task2(RouterType.RED);
		
		for(Entry<Double, Double> entry : ResultsGroup1_FIFO.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(Entry<Double, Double> entry : ResultsGroup1_RED.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(Entry<Double, Double> entry : ResultsGroup2_FIFO.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(Entry<Double, Double> entry : ResultsGroup2_RED.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		System.out.println(g1Average_FIFO + " " + g1IC_FIFO);
		System.out.println(g1Average_RED + " " + g1IC_RED);
		System.out.println(g2Average_FIFO + " " + g2IC_FIFO);
		System.out.println(g2Average_RED + " " + g2IC_RED);
	}

}
