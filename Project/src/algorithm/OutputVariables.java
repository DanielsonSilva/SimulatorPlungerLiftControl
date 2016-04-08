package algorithm;

import java.io.PrintWriter;

public final class OutputVariables {
	
	// Dados utilizados na simula��o
	public double PtbgT;   /*!< Pressao no topo do tubing                    */
	public double PcsgB;   /*!< Pressao na base do anular                    */
	public double Hplg;    /*!< Posi�ao do pist�o (0 = fundo poco)           */
	public double Qlres;   /*!< Vazao de liquido instantanea do reservatorio */
	public double pp;      /*!< Press�o no topo da golfada                   */
	
	private static OutputVariables instance;
	
	public static synchronized OutputVariables getInstance() {
		if (instance == null) {
			instance = new OutputVariables();
		}
		return instance;
	}	
	
	/**
	 * Constructor
	 */
	private OutputVariables() {
		// Empty
	}
	
	public void Limpar() {
		this.PtbgT = 0;
		this.PcsgB = 0;
		this.Hplg  = 0;
		this.Qlres = 0;
		this.pp    = 0; 
	}
	
	public void imprimirVariaveis(PrintWriter writer) {
		writer.println("Output Variables Data:");
		writer.println("PtbgT: " + this.PtbgT);
		writer.println("PcsgB: " + this.PcsgB);
		writer.println("Hplg: " + this.Hplg);
		writer.println("Qlres: " + this.Qlres);
		writer.println("pp: " + this.pp);
		writer.println("-----------------------------------");
	}

}
