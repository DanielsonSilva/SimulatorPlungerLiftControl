package algorithm;

public class DataFluid {
	
	/** Fração de água no líquido */
	public int BSW;
	/** Grau API do óleo do reservatório */
	public double APi;
	/** Massa específica do gás */
	public double SGgas;
	/** Massa específica da água */
	public double SGagua;
	/** Peso específico (Ro*g) */
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
