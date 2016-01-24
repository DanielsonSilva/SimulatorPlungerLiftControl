package algorithm;

public final class DataTimes {
	
	// Dados utilizados na simulação
	int Afterflow;  /*!< Tempo de afterflow (s)               */
	int Offtime;    /*!< Tempo de BuildUp                     */
	int Ontime;     /*!< Tempo de válvula aberta              */
	double PcsgT;   /*!< Pressao no anular na superficie (Pa) */
	double Ltbg;    /*!< Nivel de liquido no fundo da coluna  */
	double Lslg;    /*!< Comprimento da glofada (m)           */

	// Dados não utilizados na simulação apenas para informação
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

}
