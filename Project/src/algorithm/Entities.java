package algorithm;

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
