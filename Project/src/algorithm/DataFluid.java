package algorithm;

import java.io.PrintWriter;

public final class DataFluid {
	
	/** Fra��o de �gua no l�quido */
	public float BSW;
	/** Grau API do �leo do reservat�rio */
	public double APi;
	/** Massa espec�fica do g�s */
	public double SGgas;
	/** Massa espec�fica da �gua */
	public double SGagua;
	/** Peso espec�fico (Ro*g) */
	public double GAMA;
	
	private static DataFluid instance;
	
	public static synchronized DataFluid getInstance() {
		if (instance == null) {
			instance = new DataFluid();
		}
		return instance;
	}	
	
	/**
	 * Constructor
	 */	
	private DataFluid() {
		
	}
	
	public void Limpar() {
		this.BSW = 0;
		this.APi = 0;
		this.SGgas = 0;
		this.SGagua = 0;
		this.GAMA = 0;
	}
	
	public void imprimirVariaveis(PrintWriter writer) {
		writer.println("Fluid Data:");
		writer.println("BSW: " + this.BSW);
		writer.println("APi: " + this.APi);
		writer.println("SGgas: " + this.SGgas);
		writer.println("SGagua: " + this.SGagua);
		writer.println("GAMA: " + this.GAMA);
		writer.println("-----------------------------------");
	}

}
