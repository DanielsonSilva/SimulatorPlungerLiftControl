package algorithm;

public class DataTubing {
	
	// Dados utilizados na simula��o
	public double Lcauda;  /*!< Comprimento da coluna de produ��o   */
	public double E;       /*!< Rugosidade do Tubing                */
	public double DItbg;   /*!< Diametro interno do Tubing (m)      */
	public double DOtbg;   /*!< diametro externo do Tubing          */

	// Dados n�o utilizados na simula��o apenas para informa��o
	public double peso;

	/**
	 * Constructor
	 */
	public DataTubing() {
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
