/**
 * Controller Package
 */
package controller;

import java.util.Map;

import algorithm.DataConstants;
import algorithm.Entities;
import algorithm.SimulationVariables;

/**
 * Class for Timing Controller
 * 
 * @author Danielson Flávio Xavier da Silva (danielson_fx@yahoo.com.br)
 */
public class TimingController implements Controller {
	/**
	 * Determine the low travel time for the plunger to rise to the surface
	 */
	private double lowPlungerRiseTime;
	/**
	 * Determine the high travel time for the plunger to rise to the surface
	 */
	private double highPlungerRiseTime;
	/**
	 * Value that increment or decrement the Ontime and Offtime
	 */
	private double value;

	/**
	 * Set the variables for this controller
	 * 
	 * @param var
	 *            Map with the name of the variables used in this class
	 */
	public void setVariables(Map<String, Double> var) {
		this.lowPlungerRiseTime = var.get("lowPlungerTime");
		this.highPlungerRiseTime = var.get("highPlungerTime");
		this.value = var.get("value");
	}

	/**
	 * This controller checks in control stage if the plunger has arrived in the
	 * surface: <br>
	 * If yes, then the controller checks the time that the plunger had taken to
	 * rise to surface and change the Offtime and Afterflowtime depending if the
	 * time was too low or too high for given lowPlungerRiseTime and
	 * highPlungerRiseTime variables <br>
	 * If not, does nothing
	 */
	public void check() {
		// Simulation variables
		Entities f = Entities.getInstance();
		DataConstants c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();

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
					// Decrement Offtime and increment Afterflow time by value
					f.tempos.Afterflow += this.value;
					f.tempos.Offtime -= this.value;
				}
				// Check if is higher than the highPlungerRiseTime variable
				if (v.piston_arrival > this.highPlungerRiseTime) {
					// Decrement Afterflow and increment Offtime time by value
					f.tempos.Afterflow -= this.value;
					f.tempos.Offtime += this.value;
				}
			}
			break;
		// AFTERFLOW STAGE
		case 5:
			break;
		// BUILD-UP STAGE
		case 6:
			break;
		// IF THE STAGE IS UNKNOWN
		default:
			break;
		}

	}

}
