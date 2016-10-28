package algorithm;

import java.io.PrintWriter;

public final class DataTubing {
	
	// Dados utilizados na simula��o
	public double Lcauda;  /*!< Comprimento da coluna de produ��o   */
	public double E;       /*!< Rugosidade do Tubing                */
	public double DItbg;   /*!< Diametro interno do Tubing (m)      */
	public double DOtbg;   /*!< diametro externo do Tubing          */

	// Dados n�o utilizados na simula��o apenas para informa��o
	public double peso;

	private static DataTubing instance;
	
	public static synchronized DataTubing getInstance() {
		if (instance == null) {
			instance = new DataTubing();
		}
		return instance;
	}	
	
	/**
	 * Constructor
	 */
	private DataTubing() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Clean the variables
	 */
	public void Limpar() {
		this.Lcauda = 0;
		this.E      = 0;
		this.DItbg  = 0;
		this.DOtbg  = 0;
	}
	
	public void imprimirVariaveis(PrintWriter writer) {
		writer.println("Tubing Data:");
		writer.println("Lcauda: " + this.Lcauda);
		writer.println("E: " + this.E);
		writer.println("DItbg: " + this.DItbg);
		writer.println("DOtbg: " + this.DOtbg);
		writer.println("-----------------------------------");
	}
	
}
