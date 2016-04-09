package algorithm;

import java.io.PrintWriter;

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
	
	public double Rgn;/** Constante para g�s natural (R/mol) constante universal dos gases sobre massa molar do gas */
	public double AItbg;	/** �rea interna do tubing */
	public double TTcsg; /** Temperatura m�dia do anular */
	public double Tstd; /** K - temperatura padr�o (0�C) */
	public double Pstd; /** Press�o padrao (Pa) */
	public double Slow; /** Velocidade do pist�o para ser considerada lenta */
	public double Fast; /** Velocidade do pist�o para ser considerada rapida */
	public double good_velocity; /** Chute de uma boa velocidade (n�o usada) */
	
	public int estagio; /** Vari�vel para representar o estagio */
	public int INICIAR; /** Vari�vel para representar o iniciar */
	public int INICIO_CICLO; /** Vari�vel para representar o inicio ciclo */
	public int SUBIDA_PISTAO; /** Vari�vel para representar a subida do pist�o */
	public int PRODUCAO_LIQUIDO; /** Vari�vel para representar a produ��o de l�quido */
	public int CONTROLE; /** Vari�vel para representar o est�gio de controle */
	public int AFTERFLOW; /** Vari�vel para representar o afterflow */
	public int OFF_BUILD_UP; /** Vari�vel para representar o buildup */
	
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
		//Inicializa��o dos valores das etapas
		this.INICIAR          = 1;
		this.INICIO_CICLO     = 2;
		this.SUBIDA_PISTAO    = 3;
		this.PRODUCAO_LIQUIDO = 4;
		this.CONTROLE         = 5;
		this.AFTERFLOW        = 6;
		this.OFF_BUILD_UP     = 7;
		this.estagio          = 0;

		//Inicializa��o das constantes
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
	 * Verifica o valor da vari�vel INICIAR.
	 * @return Valor da vari�vel INICIAR.
	 */
	public double getINICIAR() {
			return this.INICIAR ;
	}

	/**
	 * Verifica o valor da vari�vel INICIO_CICLO.
	 * @return Valor da vari�vel INICIO_CICLO.
	 */
	public double getINICIO_CICLO() {
			return this.INICIO_CICLO ;
	}

	/**
	 * Verifica o valor da vari�vel SUBIDA_PISTAO.
	 * @return Valor da vari�vel SUBIDA_PISTAO.
	 */
	public double getSUBIDA_PISTAO() {
			return this.SUBIDA_PISTAO ;
	}

	/**
	 * Verifica o valor da vari�vel PRODUCAO_LIQUIDO.
	 * @return Valor da vari�vel PRODUCAO_LIQUIDO.
	 */
	public double getPRODUCAO_LIQUIDO() {
			return this.PRODUCAO_LIQUIDO ;
	}
	
	/**
	 * Verifica o valor da vari�vel CONTROLE.
	 * @return Valor da vari�vel CONTROLE.
	 */
	public double getCONTROLE() {
			return this.CONTROLE;
	}
	
	/**
	 * Verifica o valor da vari�vel AFTERFLOW.
	 * @return Valor da vari�vel AFTERFLOW.
	 */
	public double getAFTERFLOW() {
			return this.AFTERFLOW ;
	}
	
	/**
	 * Verifica o valor da vari�vel BUILDUP.
	 * @return Valor da vari�vel BUILDUP.
	 */
	public double getOFF_BUILD_UP() {
			return this.OFF_BUILD_UP ;
	}
	
	/**
	 * Verifica o valor da vari�vel Estagio.
	 * @return Valor da vari�vel Estagio.
	 */
	public double getEstagio() {
			return this.estagio ;
	}
	
	public void imprimirVariaveis(PrintWriter writer) {
		writer.println("Universal Constants Data:");
	    writer.println("Qmax = " + this.Qmax);
	    writer.println("Ppc = " + this.Ppc);
	    writer.println("Tpc = " + this.Tpc);
	    writer.println("R = " + this.R);
	    writer.println("Tsup = " + this.Tsup);
	    writer.println("PMar = " + this.PMar);
	    writer.println("G = " + this.G);
	    writer.println("PM = " + this.PM);
	    writer.println("step = " + this.step);
	    writer.println("step_ = " + this.step_);
	    writer.println("step_aft = " + this.step_aft);
	    writer.println("_step = " + this._step);
	    writer.println("_stepGas = " + this._stepGas);
	    writer.println("_stepLiq = " + this._stepLiq);
	    writer.println("_stepGas2Liq = " + this._stepGas2Liq);
	    writer.println("AIcsg = " + this.AIcsg);
	    writer.println("Vcsg = " + this.Vcsg);
	    writer.println("FW = " + this.FW);
	    writer.println("SGoleo = " + this.SGoleo);
	    writer.println("ROliq = " + this.ROliq);
	    writer.println("ROgas = " + this.ROgas);
	    writer.println("Rgn = " + this.Rgn);
	    writer.println("AItbg = " + this.AItbg);
	    writer.println("TTcsg = " + this.TTcsg);
	    writer.println("Tstd = " + this.Tstd);
	    writer.println("Pstd = " + this.Pstd);
	    writer.println("Slow = " + this.Slow);
	    writer.println("Fast = " + this.Fast);
	    writer.println("good_velocity = " + this.good_velocity);
	    writer.println("Tstd = " + this.Tstd);
	    writer.println("Pstd = " + this.Pstd);
	    writer.println("--------------------------------");
	}
	
}
