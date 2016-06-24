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
	 * Value that increment or decrement the Ontime and Offtime 
	 */
	private double value;
	/**
	 * Set the variables for this controller
	 * @param var
	 */
	public void setVariables(Map<String,Double> var) {
		this.low_plungerRiseTime  = var.get("low_time");
		this.high_plungerRiseTime = var.get("high_time");
		this.value = var.get("value");
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
			// Check if plunger rose to the surface
			if (v.piston_arrival != 0) {
				// Check if is lower than the low_plungerRiseTime variable
				if ( v.piston_arrival < this.low_plungerRiseTime ) {
					// Decrement Offtime and increment Afterflow time by value
					f.tempos.Afterflow += this.value;
					f.tempos.Offtime   -= this.value;
				}
				// Check if is higher than the high_plungerRiseTime variable
				if ( v.piston_arrival > this.high_plungerRiseTime ) {
					// Decrement Afterflow and increment Offtime time by value
					f.tempos.Afterflow -= this.value;
					f.tempos.Offtime   += this.value;
				}
			}
			break;
		//AFTERFLOW STAGE
		case 5:
			break;
		//BUILD-UP STAGE
		case 6:
			break;
		//IF THE STAGE IS UNKNOWN
		default:
			break;
		}

	}

}
