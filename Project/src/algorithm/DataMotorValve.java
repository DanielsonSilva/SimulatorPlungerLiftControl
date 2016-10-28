package algorithm;

import java.io.PrintWriter;

public final class DataMotorValve {

	public double Dab; /*!< Di�metro de abertura da v�lvula (mm) */
	
	private static DataMotorValve instance;
	
	public static synchronized DataMotorValve getInstance() {
		if (instance == null) {
			instance = new DataMotorValve();
		}
		return instance;
	}	
	
	private DataMotorValve() {
		// Empty
	}

	public void Limpar() {
		this.Dab = 0;
	}
	
	public void imprimirVariaveis(PrintWriter writer) {
		writer.println("Motor Valve Data:");
		writer.println("Dab: " + this.Dab);
		writer.println("-----------------------------------");
	}
}
