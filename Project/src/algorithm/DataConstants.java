package algorithm;

/**
 * Store all the Data Constants of the simulation
 * @author DANJUH
 *
 */
public class DataConstants {
	
	double Qmax; /** Maximum Flow of the Reservoir */
	double Ppc; /** Pseudo Critical Pressure */
	double Tpc; /** Pseudo Critical Temperature */
	double R; /** Universal Constants of gases (N.m/mol.K) */
	double Tsup; /** Assumed 80F = 26,7 C in the surface and gradient 1.7 F / 100 feet */
	double PMar; /** Air molar mass (kg/mol) */
	double G; /** Gravity */
	double PM; /** Gas molar mass (kg/mol) */

	double step; /** Integration step in the Plunger Rise Phase */
	double step_; /** Integration step in the Production Phase */
	double step_aft; /** Integration step in the Afterflow Phase */
	double _step; /** Integration step in the BuildUp Phase (Plunger at the bottom) */
	double _stepGas; /** Integration step in the BuildUp Phase (Plunger falling in gas) */
	double _stepLiq; /** Integration step in the BuildUp Phase (Plunger falling in Liquid) */
	double _stepGas2Liq; /** Integration step in the transition between Gas and Liquid in the BuildUp Phase */

	double AIcsg; /** Annular internal area */
	double Vcsg; /** Annular internal volume */
	double FW; /** Water ration in the liquid produced */
	double SGoleo; /** Relative density in oil in relation to water */
	double ROliq; /** Liquid Specific mass (Kg/m3) */
	double ROgas; /** Gas Specific mass (Kg/m3) */
	/** Constante para gás natural (R/mol) constante universal dos gases sobre
			massa molar do gas */
	double Rgn;
	/** Área interna do tubing */
	double AItbg;
	/** Temperatura média do anular */
	double TTcsg;
	/** K - temperatura padrão (0°C) */
	double Tstd;
	/** Pressão padrao (Pa) */
	double Pstd;
	/** Velocidade do pistão para ser considerada lenta */
	double Slow;
	/** Velocidade do pistão para ser considerada rapida */
	double Fast;
	/** Chute de uma boa velocidade (não usada) */
	double good_velocity;
	
	/**
	 * Constructor
	 */
	public DataConstants() {
		//Empty
	}
	
	

}
