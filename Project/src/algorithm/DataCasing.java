
package algorithm;

/**
 * Data for the Casing of Plunger Lift Well
 * @author Danielson Flávio Xavier da Silva
 *
 */
public class DataCasing {
	
	// Simulation Data
	/** Casing internal diameter */
	public double DIcsg;
	/** Casing Length of the system */
	public double comprimento;
	/** Casing roughness */
	public double rugosidade;
	/** Casing external diameter */
	public double DEcsg;
	/** Casing Weight */
	public double peso;
			
	/**
	 * Constructor
	 */
	public DataCasing() {
		// Empty
	}
	
	/**
	 * Set all the parameters at once
	 * @param p1 Casing internal diameter
	 * @param p2 Casing Length of the system
	 * @param p3 Casing roughness
	 * @param p4 Casing external diameter
	 * @param p5 Casing Weight
	 */
	public void SetAll(double p1, double p2, double p3, double p4, double p5) {
		this.DIcsg = p1;
		this.comprimento = p2;
		this.rugosidade = p3;
		this.DEcsg = p4;
		this.peso = p5;		
	}
	
	/**
	 * Reset Casing values
	 */
	public void Clean() {
		this.DIcsg = 0;
		this.comprimento = 0;
		this.rugosidade = 0;
		this.DEcsg = 0;
		this.peso = 0;
	}

}
