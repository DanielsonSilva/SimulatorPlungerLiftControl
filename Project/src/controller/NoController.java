package controller;

import java.util.Map;

/**
 * This class determines the action for a simulation with no controller
 * @author Danielson Flávio Xavier da Silva (danielson_fx@yahoo.com.br)
 */
public class NoController implements Controller {
	/**
	 * Set the variables for this controller
	 * @param var No parameters expected
	 */
	public void setVariables(Map<String,Double> var) {
		//Nothing to do
	}

	/**
	 * If no controller has been chosen, then let the simulation go without
	 * checking
	 */
	public void check() {
		//Nothing to do
	}
	
	/**
	 * Prints the information about this controller
	 */
	public void print() {
		System.out.println("This simulation is not using a controller.");
	}

}
