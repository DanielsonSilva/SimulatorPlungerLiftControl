package algorithm;

/**
 * Conversion of measures
 * @author Danielson Flávio Xavier da Silva
 *
 */
public class Conversion {
	
	/**
	 * Constructor
	 */
	public Conversion() {
		// Empty	
	}
	
	/**
	 * Convert Pascal to Psi measure
	 * @param pasc Value in pascal
	 * @return Value in Pascal
	 */
	public double  pascalToPsi( double pasc ) {
		return pasc/6894.757;
	}

}
