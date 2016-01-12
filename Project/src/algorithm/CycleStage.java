/**
 * 
 */
package algorithm;

/**
 * @author Danielson Flávio Xavier da Silva
 * Define numbers for the stages of Plunger Lift
 */
public enum CycleStage {
	/** Subida do pistão */
	PLUNGER_RISE(0) ,
	/** Produção */
	PRODUCTION(100) ,
	/** Afterflow */
	AFTERFLOW(200) ,
	/** Build-up */
	BUILDUP(300);
	
	private final int valor;
	CycleStage(int valorOpcao){
		valor = valorOpcao;
	}

	public int getValor(){
		return valor;
	}
	
}