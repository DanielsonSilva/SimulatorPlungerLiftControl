package algorithm;

public class DataFluid {
	
	/** Fra��o de �gua no l�quido */
	public int BSW;
	/** Grau API do �leo do reservat�rio */
	public double APi;
	/** Massa espec�fica do g�s */
	public double SGgas;
	/** Massa espec�fica da �gua */
	public double SGagua;
	/** Peso espec�fico (Ro*g) */
	public double GAMA;
	
	/**
	 * Constructor
	 */	
	public DataFluid() {
		
	}
	
	public void Limpar() {
		this.BSW = 0;
		this.APi = 0;
		this.SGgas = 0;
		this.SGagua = 0;
		this.GAMA = 0;
	}

}
