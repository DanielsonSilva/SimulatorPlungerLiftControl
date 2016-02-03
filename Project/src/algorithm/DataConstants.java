package algorithm;

/**
 * Store all the Data Constants of the simulation
 * @author DANJUH
 */
public final class DataConstants {
	
	public double Qmax; /** Maximum Flow of the Reservoir */
	public double Ppc; /** Pseudo Critical Pressure */
	public double Tpc; /** Pseudo Critical Temperature */
	public double R; /** Universal Constants of gases (N.m/mol.K) */
	public double Tsup; /** Assumed 80F = 26,7 C in the surface and gradient 1.7 F / 100 feet */
	public double PMar; /** Air molar mass (kg/mol) */
	public double G; /** Gravity */
	public double PM; /** Gas molar mass (kg/mol) */

	public double step; /** Integration step in the Plunger Rise Phase */
	public double step_; /** Integration step in the Production Phase */
	public double step_aft; /** Integration step in the Afterflow Phase */
	public double _step; /** Integration step in the BuildUp Phase (Plunger at the bottom) */
	public double _stepGas; /** Integration step in the BuildUp Phase (Plunger falling in gas) */
	public double _stepLiq; /** Integration step in the BuildUp Phase (Plunger falling in Liquid) */
	public double _stepGas2Liq; /** Integration step in the transition between Gas and Liquid in the BuildUp Phase */

	public double AIcsg; /** Annular internal area */
	public double Vcsg; /** Annular internal volume */
	public double FW; /** Water ration in the liquid produced */
	public double SGoleo; /** Relative density in oil in relation to water */
	public double ROliq; /** Liquid Specific mass (Kg/m3) */
	public double ROgas; /** Gas Specific mass (Kg/m3) */
	
	public double Rgn;/** Constante para gás natural (R/mol) constante universal dos gases sobre massa molar do gas */
	public double AItbg;	/** Área interna do tubing */
	public double TTcsg; /** Temperatura média do anular */
	public double Tstd; /** K - temperatura padrão (0°C) */
	public double Pstd; /** Pressão padrao (Pa) */
	public double Slow; /** Velocidade do pistão para ser considerada lenta */
	public double Fast; /** Velocidade do pistão para ser considerada rapida */
	public double good_velocity; /** Chute de uma boa velocidade (não usada) */
	
	public int estagio; /** Variável para representar o estagio */
	public int INICIAR; /** Variável para representar o iniciar */
	public int INICIO_CICLO; /** Variável para representar o inicio ciclo */
	public int SUBIDA_PISTAO; /** Variável para representar a subida do pistão */
	public int PRODUCAO_LIQUIDO; /** Variável para representar a produção de líquido */
	public int CONTROLE; /** Variável para representar o estágio de controle */
	public int AFTERFLOW; /** Variável para representar o afterflow */
	public int OFF_BUILD_UP; /** Variável para representar o buildup */
	
	private static DataConstants instance;
	/**
	 * Constructor
	 */
	private DataConstants() {
		init();
	}
	
	public static synchronized DataConstants getInstance() {
		if (instance == null) {
			instance = new DataConstants();
		}
		return instance;
	}
	
	public void init() {
		//Inicialização dos valores das etapas
		this.INICIAR          = 1;
		this.INICIO_CICLO     = 2;
		this.SUBIDA_PISTAO    = 3;
		this.PRODUCAO_LIQUIDO = 4;
		this.CONTROLE         = 5;
		this.AFTERFLOW        = 6;
		this.OFF_BUILD_UP     = 7;
		this.estagio          = 0;

		//Inicialização das constantes
		this.G    = 9.81;
		this.PMar = 0.02897;
		this.Tsup = 5 * (80.0-32.0)/9.0 + 273.15;
		this.R    = 8.314;
		this.Pstd = 101325;
		this.Tstd = 273.16;
		this.good_velocity = 5.0;
		this.ROgas = 0.766;
	}

	/**
	 * Verifica o valor da variável INICIAR.
	 * @return Valor da variável INICIAR.
	 */
	public double getINICIAR() {
			return this.INICIAR ;
	}

	/**
	 * Verifica o valor da variável INICIO_CICLO.
	 * @return Valor da variável INICIO_CICLO.
	 */
	public double getINICIO_CICLO() {
			return this.INICIO_CICLO ;
	}

	/**
	 * Verifica o valor da variável SUBIDA_PISTAO.
	 * @return Valor da variável SUBIDA_PISTAO.
	 */
	public double getSUBIDA_PISTAO() {
			return this.SUBIDA_PISTAO ;
	}

	/**
	 * Verifica o valor da variável PRODUCAO_LIQUIDO.
	 * @return Valor da variável PRODUCAO_LIQUIDO.
	 */
	public double getPRODUCAO_LIQUIDO() {
			return this.PRODUCAO_LIQUIDO ;
	}
	
	/**
	 * Verifica o valor da variável CONTROLE.
	 * @return Valor da variável CONTROLE.
	 */
	public double getCONTROLE() {
			return this.CONTROLE;
	}
	
	/**
	 * Verifica o valor da variável AFTERFLOW.
	 * @return Valor da variável AFTERFLOW.
	 */
	public double getAFTERFLOW() {
			return this.AFTERFLOW ;
	}
	
	/**
	 * Verifica o valor da variável BUILDUP.
	 * @return Valor da variável BUILDUP.
	 */
	public double getOFF_BUILD_UP() {
			return this.OFF_BUILD_UP ;
	}
	
	/**
	 * Verifica o valor da variável Estagio.
	 * @return Valor da variável Estagio.
	 */
	public double getEstagio() {
			return this.estagio ;
	}	

}
