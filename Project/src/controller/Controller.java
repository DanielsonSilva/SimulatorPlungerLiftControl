/**
 * Controller Package
 */
package controller;

import java.util.Map;

/**
 * Template class for the controllers existent
 * 
 * @author Danielson Flavio Xavier da Silva (danielson_fx@yahoo.com.br)
 *
 */
public interface Controller {
	/**
	 * Basic function for the controller This function will check instant
	 * variables at a moment and do something or don't. <br>
	 * This function have to be implemented by the several controllers that may
	 * exist
	 */
	public void check();

	/**
	 * Function for setting the needed variables into the controller
	 */
	public void setVariables(Map<String, Double> var);
	
	/**
	 * Prints the information requested in the controller
	 */
	public void print();

}
