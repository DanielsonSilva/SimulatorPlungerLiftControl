package algorithm;

public final class DataTubing {
	
	// Dados utilizados na simulação
	public double Lcauda;  /*!< Comprimento da coluna de produção   */
	public double E;       /*!< Rugosidade do Tubing                */
	public double DItbg;   /*!< Diametro interno do Tubing (m)      */
	public double DOtbg;   /*!< diametro externo do Tubing          */

	// Dados não utilizados na simulação apenas para informação
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
	
}
