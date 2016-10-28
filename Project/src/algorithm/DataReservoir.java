package algorithm;

import java.io.PrintWriter;

public final class DataReservoir {	

	double Pest;    /*!< Pressao Estatica    */
	double Qteste;  /*!< Vazao de Teste???   */
	double Pteste;  /*!< Pressao de Teste??? */
	int RGL;        /*!< Razao gas-liquido de producao (m3 std / m3) */

	private static DataReservoir instance;
	
	public static synchronized DataReservoir getInstance() {
		if (instance == null) {
			instance = new DataReservoir();
		}
		return instance;
	}	
	
	private DataReservoir() {
		// Empty
	}
	
	public void Limpar() {
		this.Pest = 0;
		this.Qteste = 0;
		this.Pteste = 0;
		this.RGL = 0;
	}
	
	public void imprimirVariaveis(PrintWriter writer) {
		writer.println("Reservoir Data:");
		writer.println("Pest: " + this.Pest);
		writer.println("Qteste: " + this.Qteste);
		writer.println("Pteste: " + this.Pteste);
		writer.println("RGL: " + this.RGL);
		writer.println("-----------------------------------");
	}

}
