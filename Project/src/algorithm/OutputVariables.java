package algorithm;

public final class OutputVariables {
	
	// Dados utilizados na simulação
	public double PtbgT;   /*!< Pressao no topo do tubing                    */
	public double PcsgB;   /*!< Pressao na base do anular                    */
	public double Hplg;    /*!< Posiçao do pistão (0 = fundo poco)           */
	public double Qlres;   /*!< Vazao de liquido instantanea do reservatorio */
	public double pp;      /*!< Pressão no topo da golfada                   */
	
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

}
