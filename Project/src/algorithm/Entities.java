package algorithm;

import java.io.PrintWriter;

public class Entities {
	
	/** Dados referentes � coluna de producao.  */
	DataTubing tubing;
	/** Dados referentes ao anular.             */
	DataCasing casing;
	/** Dados referentes � v�lvula motora.      */
	DataMotorValve valvula;
	/** Dados referentes � linha de produ��o.   */
	DataProductionLine linhaPro;
	/** Dados do reservat�rio do po�o.          */
	DataReservoir reservat;
	/** Dados referentes ao pist�o usado.       */
	DataPlunger pistao;
	/** Dados do fluido do reservat�rio.        */
	DataFluid fluido;
	/** Tempos de abertura e fechamento.        */
	DataTimes tempos;
	/** Variaveis concernentes � interface.     */
	OutputVariables varSaida;
	
	public DataTubing getTubing() {
		return tubing;
	}

	public void setTubing(DataTubing tubing) {
		this.tubing = tubing;
	}

	public DataCasing getCasing() {
		return casing;
	}

	public void setCasing(DataCasing casing) {
		this.casing = casing;
	}

	public DataMotorValve getValvula() {
		return valvula;
	}

	public void setValvula(DataMotorValve valvula) {
		this.valvula = valvula;
	}

	public DataProductionLine getLinhaPro() {
		return linhaPro;
	}

	public void setLinhaPro(DataProductionLine linhaPro) {
		this.linhaPro = linhaPro;
	}

	public DataReservoir getReservat() {
		return reservat;
	}

	public void setReservat(DataReservoir reservat) {
		this.reservat = reservat;
	}

	public DataPlunger getPistao() {
		return pistao;
	}

	public void setPistao(DataPlunger pistao) {
		this.pistao = pistao;
	}

	public DataFluid getFluido() {
		return fluido;
	}

	public void setFluido(DataFluid fluido) {
		this.fluido = fluido;
	}

	public DataTimes getTempos() {
		return tempos;
	}

	public void setTempos(DataTimes tempos) {
		this.tempos = tempos;
	}

	public OutputVariables getVarSaida() {
		return varSaida;
	}

	public void setVarSaida(OutputVariables varSaida) {
		this.varSaida = varSaida;
	}	
	
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
	
	public void imprimirVariaveis(PrintWriter writer) {
		tubing.imprimirVariaveis(writer);
	    casing.imprimirVariaveis(writer);
	    valvula.imprimirVariaveis(writer);
	    linhaPro.imprimirVariaveis(writer);
	    reservat.imprimirVariaveis(writer);
	    pistao.imprimirVariaveis(writer);
	    fluido.imprimirVariaveis(writer);
	    tempos.imprimirVariaveis(writer);
	    varSaida.imprimirVariaveis(writer);
	}

}
