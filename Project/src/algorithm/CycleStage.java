/**
 * 
 */
package algorithm;

/**
 * @author Danielson Flávio Xavier da Silva
 * Define numbers for the stages of Plunger Lift
 */
public enum CycleStage {
	/** Indicate the start of a cycle */
	CYCLE_START(10),
	/** Indicate the Plunger Rise stage */
	PLUNGER_RISE(100) ,
	/** Indicate the Production stage */
	PRODUCTION(200) ,
	/** Indicate the Afterflow stage */
	AFTERFLOW(300) ,
	/** Indicate the BuildUp stage */
	BUILDUP(400),
	/** Plunger average speed */
	AVERAGE_PL_VELOCITY(0),
	/** Rise time of the plunger */
	PL_RISE_TIME(1),
	/** Velocity of impact */
	IMPACT_VEL(2),
	/** Production volume */
	PRODUCTION_VOLUME(3),
	/** Total volume produced */
	TOTAL_PRODUCTION(4),
	/** Duration of Rise plunger stage */
	STAGE_RISE_DURATION(5),
	/** Duration of Production stage */
	STAGE_PRODUC_DURATION(6),
	/** Duration of Afterflow stage */
	STAGE_AFTER_DURATION(7),
	/** Duration of BuildUp stage */
	STAGE_BUILDUP_DURATION(8),
	/** Total Duration of the cycle */
	CYCLE_DURATION(9);
	
	private final int valor;
	CycleStage(int valorOpcao){
		valor = valorOpcao;
	}

	public int getValor(){
		return valor;
	}
	
}