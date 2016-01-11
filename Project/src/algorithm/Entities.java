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
	
	/**
	 * Constructor
	 */
	public Entities() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Clear the variables
	 */
	public void Limpar() {
		tubing->Limpar()  ;
		casing->Limpar()  ;
		valvula->Limpar() ;
		linhaPro->Limpar();
		reservat->Limpar();
		pistao->Limpar()  ;
		fluido->Limpar()  ;
		tempos->Limpar()  ;
		varSaida->Limpar();
	}

}
