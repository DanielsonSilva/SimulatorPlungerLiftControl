package controller;

import java.util.Map;

/**
 * Basic class for the controllers existent
 * @author Danielson Flavio Xavier da Silva
 *
 */
public interface Controller {
	/**
	 * Basic function for the controller
	 * This function will check instant variables at a moment and do
	 * something or don't
	 * This function have to be implemented by the several controllers
	 * that may exist
	 */
	public void check();
	/**
	 * Function for setting the variables into the controller
	 */
	public void setVariables(Map<String,Double> var);

}
