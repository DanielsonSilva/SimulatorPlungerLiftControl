package algorithm;

import java.io.PrintWriter;

public final class DataTimes {
	
	// Dados utilizados na simula��o
	public int Afterflow;  /*!< Tempo de afterflow (s)               */
	public int Offtime;    /*!< Tempo de BuildUp                     */
	int Ontime;     /*!< Tempo de v�lvula aberta              */
	public double PcsgT;   /*!< Pressao no anular na superficie (Pa) */
	double Ltbg;    /*!< Nivel de liquido no fundo da coluna  */
	double Lslg;    /*!< Comprimento da glofada (m)           */

	// Dados n�o utilizados na simula��o apenas para informa��o
	boolean Controller; /*!< Deve-se utilizar o controlador      */

	private static DataTimes instance;
	
	public static synchronized DataTimes getInstance() {
		if (instance == null) {
			instance = new DataTimes();
		}
		return instance;
	}	
	
	private DataTimes() {
		// Empty
	}
	
	public void Limpar() {
		this.Afterflow = 0;
		this.Offtime = 0;
		this.Ontime = 0;
		this.PcsgT = 0;
		this.Ltbg = 0;
		this.Lslg = 0;
		this.Controller = false;
	}
	
	public void imprimirVariaveis(PrintWriter writer) {
		writer.println("Initial Data Times:");
		writer.println("Afterflow: " + this.Afterflow);
		writer.println("Offtime: " + this.Offtime);
		writer.println("Ontime: " + this.Ontime);
		writer.println("PcsgT: " + this.PcsgT);
		writer.println("Ltbg: " + this.Ltbg);
		writer.println("Lslg: " + this.Lslg);
		writer.println("Controller: " + this.Controller);
		writer.println("-----------------------------------");
	}

}
