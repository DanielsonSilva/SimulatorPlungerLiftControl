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
		
		System.out.println("Check for the controller: " + f.getVarSaida().Hplg);
	}

}
