package algorithm;

import java.io.PrintWriter;

public final class DataProductionLine {
	
	double Psep;    /*!< Pressao no separador */
	
	private static DataProductionLine instance;
	public static synchronized DataProductionLine getInstance() {
		if (instance == null) {
			instance = new DataProductionLine();
		}
		return instance;
	}
	
	private DataProductionLine() {
		//Empty
	}
	
	void Limpar() {
		this.Psep = 0;
	}
	
	public void imprimirVariaveis(PrintWriter writer) {
		writer.println("Production Line Data:");
		writer.println("Psep: " + this.Psep);
		writer.println("-----------------------------------");
	}

}
