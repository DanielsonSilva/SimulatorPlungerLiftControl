package algorithm;

public class SimulationThread implements Runnable {
	
	private Simulation simulation;
	private boolean stop;
	
	/**
	 * Constructor
	 */
	public SimulationThread() {
		simulation = Simulation.getInstance();
		stop = false;
	}
	
	/**
	 * Runnable method
	 */
	public void run() {
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		
		while ( !stop ) {
			// Somente a primeira vez que estiver iniciando a simulação.
			switch ( c.estagio ) {
				case 0:
					simulation.iniciarSimulacao();
					break;
				case 7:
					simulation.inicioCiclo();
					break;
				case 2:
					simulation.subidaPistao();
					break;
				case 3:
					simulation.producaoLiquido();
					break;
				case 4:
					simulation.Controle();
					break;
				case 5:
					simulation.Afterflow();
					break;
				case 6:
					simulation.OffBuildUp(true);
					break;
				default:
					continue;
			}
		}
		// When the thread will be killed
		f.Limpar();
		c.init();
		v.init();
	}
	
	/**
	 * Set the stop for killing the thread
	 * @param stop the stop to set
	 */
	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
}
