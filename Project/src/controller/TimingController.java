package controller;

import java.util.Map;

import algorithm.DataConstants;
import algorithm.Entities;
import algorithm.SimulationVariables;
/**
 * Class for Timing Controller
 * @author Danielson Flavio Xavier da Silva
 *
 */
public class TimingController implements Controller{
	/**
	 * Determine the low travel time for the plunger to
	 * rise to the surface
	 */
	private double low_plungerRiseTime;
	/**
	 * Determine the high travel time for the plunger to
	 * rise to the surface
	 */	
	private double high_plungerRiseTime;
	/**
	 * Check if 
	 */

	/**
	 * Set the variables for this controller
	 * @param var
	 */
	public void setVariables(Map<String,Double> var) {
		this.low_plungerRiseTime  = var.get ("low_time");
		this.high_plungerRiseTime = var.get("high_time");
	}
	
	@Override
	public void check() {
		// TODO Auto-generated method stub
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();

		switch (c.estagio) {
		//PLUNGER RISE STAGE
		case 2:
			break;
		//PRODUCTION STAGE
		case 3:
			break;
		//CONTROL STAGE
		case 4:
			break;
		//AFTERFLOW STAGE
		case 5:
			break;
		//BUILD-UP STAGE
		case 6:
			break;
		default:
			break;
		}
		
	}

}
