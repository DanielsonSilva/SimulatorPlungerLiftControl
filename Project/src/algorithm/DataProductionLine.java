package algorithm;

public class DataProductionLine {
	
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

}
