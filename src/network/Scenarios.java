package network;

import java.util.Map.Entry;
import java.util.ArrayList;
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
	// Vazão de cada Tx, Tempo x PróximoByte/Tempo
	private static TreeMap<Double, Double> FlowGroup1_FIFO;
	private static TreeMap<Double, Double> FlowGroup2_FIFO;
	private static TreeMap<Double, Double> FlowGroup1_RED;
	private static TreeMap<Double, Double> FlowGroup2_RED;
	//-------------------------------------
	// Cenário 2, Tarefa 1:
	// Vazão de todos os Tx, 10 no total.
	private static ArrayList<TreeMap<Double, Double>> AllTxFlows_FIFO;
	private static ArrayList<TreeMap<Double, Double>> AllTxFlows_RED;
	
	
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
		// Não é falado nada sobre início assíncrono, então setarei como zero.
		SimulationProperties.setAssyncInterval(0);
		
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
		// Não é falado nada sobre início assíncrono, então setarei como zero.
		SimulationProperties.setAssyncInterval(0);
		
		Simulation simulation = new Simulation();
		simulation.run();
		
		if(SimulationProperties.getRouterType().equals(RouterType.FIFO))
			FlowGroup1_FIFO = simulation.getFlowPerTx().get(0);
		else
			FlowGroup1_RED = simulation.getFlowPerTx().get(0);
		
		// Defino uma conexão do Grupo 2 e nenhuma do grupo 1.
		SimulationProperties.setQuantityOfG1(0);
		SimulationProperties.setQuantityOfG2(1);
		
		simulation = new Simulation();
		simulation.run();
		
		if(SimulationProperties.getRouterType().equals(RouterType.FIFO))
			FlowGroup2_FIFO = simulation.getFlowPerTx().get(0);
		else
			FlowGroup2_RED = simulation.getFlowPerTx().get(0);
	}

	
	public static void Scenario2_Task1(RouterType type) {
		// Defino que terá tráfego de fundo.
		SimulationProperties.setWithCongestion(true);
		// Mudo o valor do Cg para 5Mbps.
		SimulationProperties.setCg((10E6)/2.0);
		// Defino 5 conexões de cada grupo.
		SimulationProperties.setQuantityOfG1(5);
		SimulationProperties.setQuantityOfG2(5);
		// Defino 10 mil eventos na simulação, para que seja longa.
		SimulationProperties.setEventsInARow(1000);
		// Defino a política do roteador, de acordo com o parâmetor passado no método.
		SimulationProperties.setRouterType(type);
		// Pelo enunciado, seto com 100ms.
		SimulationProperties.setAssyncInterval(100);
		// Defini a fase transiente como 1/3 do número de eventos.
		SimulationProperties.setTransientPhaseEvents(SimulationProperties.getEventsInARow()/3);
		
		Simulation simulation = new Simulation();
		simulation.run();
		
		if(SimulationProperties.getRouterType().equals(RouterType.FIFO))
			AllTxFlows_FIFO = simulation.getFlowPerTx();
		else
			AllTxFlows_RED = simulation.getFlowPerTx();
	}
	
	public static void main(String[] args) {
		//Scenario1_Task1(RouterType.FIFO);
		//Scenario1_Task1(RouterType.RED);
		//Scenario1_Task2(RouterType.FIFO);
		//Scenario1_Task2(RouterType.RED);
		Scenario2_Task1(RouterType.FIFO);
		Scenario2_Task1(RouterType.RED);
		
		for(Entry<Double, Double> entry : ResultsGroup1_FIFO.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(Entry<Double, Double> entry : ResultsGroup1_RED.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(Entry<Double, Double> entry : ResultsGroup2_FIFO.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(Entry<Double, Double> entry : ResultsGroup2_RED.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(Entry<Double, Double> entry : FlowGroup1_FIFO.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(Entry<Double, Double> entry : FlowGroup1_RED.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(Entry<Double, Double> entry : FlowGroup2_FIFO.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(Entry<Double, Double> entry : FlowGroup2_RED.entrySet())
			System.out.println(entry.getKey() + " => " + entry.getValue());
		
		for(TreeMap<Double, Double> map : AllTxFlows_FIFO)
			for(Entry<Double, Double> entry : map.entrySet())
				System.out.println(AllTxFlows_FIFO.indexOf(map) + ": " + entry.getKey() + " => " + entry.getValue());
		
		for(TreeMap<Double, Double> map : AllTxFlows_RED)
			for(Entry<Double, Double> entry : map.entrySet())
				System.out.println(AllTxFlows_RED.indexOf(map) + ": " + entry.getKey() + " => " + entry.getValue());
	}

}
