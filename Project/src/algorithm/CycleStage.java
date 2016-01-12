/**
 * 
 */
package algorithm;

/**
 * @author Danielson Fl�vio Xavier da Silva
 * Define numbers for the stages of Plunger Lift
 */
public enum CycleStage {
	/** Subida do pist�o */
	PLUNGER_RISE(0) ,
	/** Produ��o */
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