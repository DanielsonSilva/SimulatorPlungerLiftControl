package algorithm;

public class Entities {

	/** Dados referentes à coluna de producao.  */
	DataTubing tubing;
	/** Dados referentes ao anular.             */
	DataCasing casing;
	/** Dados referentes à válvula motora.      */
	DataMotorValve valvula;
	/** Dados referentes à linha de produção.   */
	DataProductionLine linhaPro;
	/** Dados do reservatório do poço.          */
	DataReservoir reservat;
	/** Dados referentes ao pistão usado.       */
	DataPlunger pistao;
	/** Dados do fluido do reservatório.        */
	DataFluid fluido;
	/** Tempos de abertura e fechamento.        */
	DataTimes tempos;
	/** Variaveis concernentes à interface.     */
	OutputVariables varSaida;
	
	
	private static Entities instance;
	
	public static synchronized Entities getInstance() {
		if (instance == null) {
			instance = new Entities();
		}
		return instance;
	}	
	
	/**
	 * Constructor
	 */
	private Entities() {
		this.tubing   = DataTubing.getInstance();
	    this.casing   = DataCasing.getInstance();
	    this.valvula  = DataMotorValve.getInstance();
	    this.linhaPro = DataProductionLine.getInstance();
	    this.reservat = DataReservoir.getInstance();
	    this.pistao   = DataPlunger.getInstance();
	    this.fluido   = DataFluid.getInstance();
	    this.tempos   = DataTimes.getInstance();
	    this.varSaida = OutputVariables.getInstance();
	}
	
	/**
	 * Clear the variables
	 */
	public void Limpar() {
		this.tubing.Limpar()  ;
		this.casing.Limpar()  ;
		this.valvula.Limpar() ;
		this.linhaPro.Limpar();
		this.reservat.Limpar();
		this.pistao.Limpar()  ;
		this.fluido.Limpar()  ;
		this.tempos.Limpar()  ;
		this.varSaida.Limpar();
	}

}
