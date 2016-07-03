/**
 * Controller Package
 */
package controller;

import java.util.Map;
import algorithm.DataConstants;
import algorithm.Entities;
import algorithm.SimulationVariables;
import algorithm.Conversion;
import algorithm.Simulation;

/**
 * Class for the Casing Pressure Controller
 * 
 * @author Danielson Flavio Xavier da Silva (danielson_fx@yahoo.com.br)
 */
public class CasingPressureController {
	/**
	 * Determine the low travel time for the plunger to rise to the surface
	 */
	private double lowPlungerRiseTime;
	/**
	 * Determine the high travel time for the plunger to rise to the surface
	 */
	private double highPlungerRiseTime;
	/**
	 * Value that increment or decrement the low and high trigger for casing
	 * pressure
	 */
	private double value;
	/**
	 * Low trigger for the pressure of the casing
	 */
	private double lowCasingPressure;
	/**
	 * High trigger for the pressure of the casing
	 */
	private double highCasingPressure;

	/**
	 * Set the variables for this controller
	 * 
	 * @param var
	 *            parameters for this controller <br>
	 *            expected the private variables in this class
	 */
	public void setVariables(Map<String, Double> var) {
		this.lowPlungerRiseTime = var.get("lowPlungerTime");
		this.highPlungerRiseTime = var.get("highPlungerTime");
		this.value = var.get("value");
		this.lowCasingPressure = var.get("lowCasingPressure");
		this.highCasingPressure = var.get("highCasingPressure");
	}

	/**
	 * Does the controller based in the casing pressure: <br>
	 * The controller closes the motor valve when the low casing pressure
	 * trigger hits in afterflow stage and opens it when the high casing
	 * pressure trigger hits in build-up stage. <br>
	 * The verification in control stage for the time that the plunger rises to
	 * the surface is done. If is lower or higher then the lowPlungerRiseTime or
	 * highPlungerRiseTime, the controller will change the values of the
	 * triggers for the casing pressure.
	 */
	public void check() {
		// Simulation variables
		Entities f = Entities.getInstance();
		DataConstants c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		Simulation simulation = Simulation.getInstance();
		Conversion cv = new Conversion();

		switch (c.estagio) {
		// PLUNGER RISE STAGE
		case 2:
			break;
		// PRODUCTION STAGE
		case 3:
			break;
		// CONTROL STAGE
		case 4:
			// Check if plunger rose to the surface
			if (v.piston_arrival != 0) {
				// Check if is lower than the lowPlungerRiseTime variable
				if (v.piston_arrival < this.lowPlungerRiseTime) {
					// Decrement the triggers for the casing pressure
					this.lowCasingPressure -= this.value;
					this.highCasingPressure -= this.value;
				}
				// Check if is higher than the highPlungerRiseTime variable
				if (v.piston_arrival > this.highPlungerRiseTime) {
					// Increment the triggers for the casing pressure
					this.lowCasingPressure += this.value;
					this.highCasingPressure += this.value;
				}
			}
			break;
		// AFTERFLOW STAGE
		case 5:
			// Checks when the low casing pressure will trigger
			// This is done checking the average casing pressure and if
			// it is lower than the lowCasingPressure variables
			if ((cv.paToPsi(f.varSaida.PcsgB) + cv.paToPsi(f.tempos.PcsgT)) / 2 <= this.lowCasingPressure) {
				// If so, close the motor valve
				simulation.changeStateValve();
			}
			break;
		// BUILD-UP STAGE
		case 6:
			// Checks when the high casing pressure will trigger
			// This is done checking the average casing pressure and if
			// it is higher than the lowCasingPressure variables
			if ((cv.paToPsi(f.varSaida.PcsgB) + cv.paToPsi(f.tempos.PcsgT)) / 2 >= this.highCasingPressure) {
				// If so, opens the motor valve
				simulation.changeStateValve();
			}
			break;
		// IF THE STAGE IS UNKNOWN
		default:
			break;
		}
	}

}