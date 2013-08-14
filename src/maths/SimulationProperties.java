package maths;

import enums.RouterType;

public final class SimulationProperties {
	
	/**
	 * Quantidade de pacotes que cabem no Buffer do roteador.
	 */
	private static Integer bufferSize = 40;
	
	/**
	 * Quantidade de sess�es TCP de grupo 1.
	 */
	private static Integer quantityOfG1 = 10;
	
	/**
	 * O intervalo aleatório do início de uma sessão deve ser
	 * um tempo dentro deste intervalo, em milissegundos.
	 * Definido no Cenário 2 como 100 e no cenário 3 como 1000.
	 */
	private static double assyncInterval = 100;
	
	/**
	 * Quantidade de sess�es TCP de grupo 2.
	 */
	private static Integer quantityOfG2 = 10;
	
	/**
	 * Taxa do enlace de saída do gargalo em bps.
	 */
	private static double cg = 10E6;
	
	/**
	 * Atraso de propagação para o grupo 1.
	 */
	private static Integer TP1 = 100;
	
	/**
	 * Roteador que será usado como padrão na simulação.
	 */
	private static RouterType routerType = RouterType.RED;
	
	/**
	 * Atraso de propagação para o grupo 2.
	 */
	private static Integer TP2 = 50;
	
	private static Integer AckG1PropagationTime = 100;
	
	private static Integer AckG2PropagationTime = 50;
	
	/**
	 * Tamanho padr�o de um pacote.
	 */
	private static Long MSS = 1500L;
	
	/**
	 * Valor alterável do threshold.
	 */
	private static long threshold = 65535;
	
	/**
	 * Se terá tráfego de fundo.
	 */
	private static boolean withCongestion = true;
	
	/**
	 * Número de eventos da fase transiente.
	 */
	private static int transientPhaseEvents = 0;
	
	/**
	 * Taxa do enlace de saída dos servidores em bps.
	 */
	private static double cs = 1E9;
	
	/**
	 * Número de eventos de uma rodada de simulação.
	 */
	private static Integer eventsInARow = 1000000;
	
	public static Integer getBufferSize() {
		return bufferSize;
	}

	public static void setBufferSize(Integer bufferSize) {
		SimulationProperties.bufferSize = bufferSize;
	}

	public static Integer getQuantityOfG1() {
		return quantityOfG1;
	}

	public static void setQuantityOfG1(Integer quantityOfG1) {
		SimulationProperties.quantityOfG1 = quantityOfG1;
	}

	public static Integer getQuantityOfG2() {
		return quantityOfG2;
	}

	public static void setQuantityOfG2(Integer quantityOfG2) {
		SimulationProperties.quantityOfG2 = quantityOfG2;
	}

	public static Long getMSS() {
		return MSS;
	}

	public static void setMSS(Long mSS) {
		MSS = mSS;
	}

	public static Integer getTP1() {
		return TP1;
	}

	public static void setTP1(Integer tP1) {
		TP1 = tP1;
	}

	public static Integer getTP2() {
		return TP2;
	}

	public static void setTP2(Integer tP2) {
		TP2 = tP2;
	}

	public static Integer getAckG1PropagationTime() {
		return AckG1PropagationTime;
	}

	public static void setAckG1PropagationTime(Integer ackG1PropagationTime) {
		AckG1PropagationTime = ackG1PropagationTime;
	}

	public static Integer getAckG2PropagationTime() {
		return AckG2PropagationTime;
	}

	public static void setAckG2PropagationTime(Integer ackG2PropagationTime) {
		AckG2PropagationTime = ackG2PropagationTime;
	}

	public static long getThreshold() {
		return threshold;
	}

	public static void setThreshold(long threshold) {
		SimulationProperties.threshold = threshold;
	}

	public static double getCg() {
		return cg;
	}

	public static void setCg(double cg) {
		SimulationProperties.cg = cg;
	}

	public static RouterType getRouterType() {
		return routerType;
	}

	public static void setRouterType(RouterType routerType) {
		SimulationProperties.routerType = routerType;
	}

	public static Integer getEventsInARow() {
		return eventsInARow;
	}

	public static void setEventsInARow(Integer eventsInARow) {
		SimulationProperties.eventsInARow = eventsInARow;
	}

	public static boolean isWithCongestion() {
		return withCongestion;
	}

	public static void setWithCongestion(boolean withCongestion) {
		SimulationProperties.withCongestion = withCongestion;
	}

	public static Double getCs() {
		return cs;
	}

	public static void setCs(Double cs) {
		SimulationProperties.cs = cs;
	}

	public static int getTransientPhaseEvents() {
		return transientPhaseEvents;
	}

	public static void setTransientPhaseEvents(int transientPhaseEvents) {
		SimulationProperties.transientPhaseEvents = transientPhaseEvents;
	}

	public static double getAssyncInterval() {
		return assyncInterval;
	}

	public static void setAssyncInterval(double assyncInterval) {
		SimulationProperties.assyncInterval = assyncInterval;
	}
}
