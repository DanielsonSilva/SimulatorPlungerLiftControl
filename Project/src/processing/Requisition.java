package processing;

import algorithm.SimulationThread;
/**
 * @author Danielson Flávio Xavier da Silva
 * Point requisition between algorithm and view
 *
 */
public class Requisition implements Runnable {
	
	// Thread of the simulation
	private SimulationThread simulationthread;
	private Thread t;
	/**
	 * Constructor
	 */
	public Requisition() {
		simulationthread = new SimulationThread();
		t = new Thread(simulationthread);
	}
	
	/**
	 * Runnable method
	 */
	public void run() {
		t.start();
	}
}
