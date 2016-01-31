/**
 * 
 */
package view;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Danielson Flávio Xavier da Silva
 *
 */
public class InitialCondition {

private static InitialCondition instance;
	
	public static synchronized InitialCondition getInstance() {
		if (instance == null) {
			instance = new InitialCondition();
		}
		return instance;
	}
	
	private Map<String,Double> variables;
	
	private InitialCondition() {
		variables = new HashMap<String, Double>();
		// Tubing variables
		variables.put("tubingLength",0.0);
		variables.put("tubingRoughness",0.0);
		variables.put("tubingInternal",0.0);
		variables.put("tubingExternal",0.0);
		variables.put("tubingWeigth",0.0);
		// Casing variables
		variables.put("casingLength",0.0);
		variables.put("casingRoughness",0.0);
		variables.put("casingInternal",0.0);
		variables.put("casingExternal",0.0);
		variables.put("casingWeigth",0.0);
		// Motor valve variables
		variables.put("motorvalveDiameter",0.0);
		// Production line variables
		variables.put("prodlineSepPressure",0.0);
		// Reservoir variables
		variables.put("reservoirStaticP",0.0);
		variables.put("reservoirTestFlow",0.0);
		variables.put("reservoirTestPressure",0.0);
		variables.put("reservoirRGL",0.0);
		// Plunger Variables
		variables.put("plungerEfi",0.0);
		variables.put("plungerLength",0.0);
		variables.put("plungerMass",0.0);
		variables.put("plungerDiameter",0.0);
		// Fluid variables
		variables.put("fluidBSW",0.0);
		variables.put("fluidAPI",0.0);
		variables.put("fluidSGWater",0.0);
		variables.put("fluidSGGas",0.0);
		variables.put("fluidGammaGas",0.0);
		// Time variables
		variables.put("initialOpenValve",0.0);
		variables.put("initialCloseValve",0.0);
		variables.put("initialAfterflow",0.0);
		variables.put("initialSlug",0.0);
		variables.put("initialCasingTop",0.0);
		// Sample variables
		variables.put("sampleRise",0.0);
		variables.put("sampleProduction",0.0);
		variables.put("sampleAfterflow",0.0);
		variables.put("sampleBuildGas",0.0);
		variables.put("sampleBuildGasLiq",0.0);
		variables.put("sampleBuildLiq",0.0);
		// Step variables
		variables.put("stepRise",0.0);
		variables.put("stepProduction",0.0);
		variables.put("stepAfterflow",0.0);
		variables.put("stepBuildGas",0.0);
		variables.put("stepBuildGasLiq",0.0);
		variables.put("stepBuidLiq",0.0);
	}
	
	public void Insert (double[] v) {
		// Tubing variables
		variables.put("tubingLength",v[0]);
		variables.put("tubingRoughness",v[1]);
		variables.put("tubingInternal",v[2]);
		variables.put("tubingExternal",v[3]);
		variables.put("tubingWeigth",v[4]);
		// Casing variables
		variables.put("casingLength",v[5]);
		variables.put("casingRoughness",v[6]);
		variables.put("casingInternal",v[7]);
		variables.put("casingExternal",v[8]);
		variables.put("casingWeigth",v[9]);
		// Motor valve variables
		variables.put("motorvalveDiameter",v[10]);
		// Production line variables
		variables.put("prodlineSepPressure",v[11]);
		// Reservoir variables
		variables.put("reservoirStaticP",v[12]);
		variables.put("reservoirTestFlow",v[13]);
		variables.put("reservoirTestPressure",v[14]);
		variables.put("reservoirRGL",v[15]);
		// Plunger Variables
		variables.put("plungerEfi",v[16]);
		variables.put("plungerLength",v[17]);
		variables.put("plungerMass",v[18]);
		variables.put("plungerDiameter",v[19]);
		// Fluid variables
		variables.put("fluidBSW",v[20]);
		variables.put("fluidAPI",v[21]);
		variables.put("fluidSGWater",v[22]);
		variables.put("fluidSGGas",v[23]);
		variables.put("fluidGammaGas",v[24]);
		// Time variables
		variables.put("initialOpenValve",v[25]);
		variables.put("initialCloseValve",v[26]);
		variables.put("initialAfterflow",v[27]);
		variables.put("initialSlug",v[28]);
		variables.put("initialCasingTop",v[29]);
		// Sample variables
		variables.put("sampleRise",v[30]);
		variables.put("sampleProduction",v[31]);
		variables.put("sampleAfterflow",v[32]);
		variables.put("sampleBuildGas",v[33]);
		variables.put("sampleBuildGasLiq",v[34]);
		variables.put("sampleBuildLiq",v[35]);
		// Step variables
		variables.put("stepRise",v[36]);
		variables.put("stepProduction",v[37]);
		variables.put("stepAfterflow",v[38]);
		variables.put("stepBuildGas",v[39]);
		variables.put("stepBuildGasLiq",v[40]);
		variables.put("stepBuidLiq",v[41]);
	}
	
	/**
	 * @return the variables
	 */
	public Map<String, Double> getVariables() {
		return variables;
	}	
	
}
