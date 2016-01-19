package algorithm;

public class DataReservoir {	

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

}
