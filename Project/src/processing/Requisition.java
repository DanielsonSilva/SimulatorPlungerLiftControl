package processing;

import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import algorithm.SimulationThread;
import view.Principal;
/**
 * @author Danielson Flávio Xavier da Silva
 * Point requisition between algorithm and view
 *
 */
public class Requisition implements Runnable {
	
	private SimulationThread simulationthread;	// Thread of the simulation
	private Thread t; // Thread to manipulate the simulation
	private boolean stop; // Variable to stop or not
	private Map<String,Double> point; // One point of the cycle
	private ResourceBundle messages; // Just to send to Principal
	private Map<String,Double> variables; // Initial condition for the simulation
	
	/**
	 * Constructor
	 */
	public Requisition() {
		stop = false;
		simulationthread = new SimulationThread();
		t = new Thread(simulationthread);
	}
	
	/**
	 * Runnable method
	 */
	public void run() {
		simulationthread.setStop(false);
		t.start();
		while ( !stop ) {
			System.out.println("Pegando os dados");
			point = simulationthread.requirePoint();
			Principal.getInstance(messages).paint(point);
		}
		simulationthread.setStop(true);
	}

	/**
	 * @param stop the stop to set
	 */
	public void setStop(boolean stop) {
		t.interrupt();
	}
	
	public void notifySimulation() {
		t.notify();
	}

	/**
	 * @param variables the variables to set
	 */
	public void setVariables(Map<String, Double> variables) {
		this.variables = variables;
	}
	
	/**
	 * Pass the variables to simulation to start properly
	 */
	public void passVariablesToSimulation() {
		simulationthread.setInitialCondition(variables);
	}
	
	/**
	 * Pauses the simulation and the requisition of points to the interface
	 */
	@SuppressWarnings("deprecation")
	public void pauseSimulation() {
		t.suspend();
	}
	
	/**
	 * Stop the simulation thread
	 */
	@SuppressWarnings("deprecation")
	public void stopSimulation() {
		t.stop();
	}
	
	/**
	 * Resumes the simulation to the point it stoped
	 */
	@SuppressWarnings("deprecation")
	public void resumeSimulation() {
		t.resume();
	}
	
}
