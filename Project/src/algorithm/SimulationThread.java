package algorithm;

import java.util.HashMap;
import java.util.Map;

public class SimulationThread implements Runnable {
	
	private Simulation simulation;
	private boolean stop;
	
	/**
	 * Constructor
	 */
	public SimulationThread() {
		stop = false;
		simulation = Simulation.getInstance();
		stop = false;
	}
	
	/**
	 * Runnable method
	 */
	public void run() {
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		
		while ( !stop ) {
			// Somente a primeira vez que estiver iniciando a simulação.
			switch ( c.estagio ) {
				case 0:
					simulation.iniciarSimulacao();
					break;
				case 7:
					simulation.inicioCiclo();
					break;
				case 2:
					simulation.subidaPistao();
					break;
				case 3:
					simulation.producaoLiquido();
					break;
				case 4:
					simulation.Controle();
					break;
				case 5:
					simulation.Afterflow();
					break;
				case 6:
					simulation.OffBuildUp(true);
					break;
				default:
					continue;
			}
		}
		// When the thread will be killed
		f.Limpar();
		c.init();
		v.init();
	}
	
	/**
	 * Sends a point of the calculation
	 * @return map variable containing all variables to plot
	 */
	public Map<String,Double> requirePoint() {
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		Map<String,Double> point = new HashMap<String, Double>();
		double stage;
		
		stage = c.estagio + 1;
		if (stage == 8) {
			stage = 2;
		}
		point.put("stage", stage);
		
		double gasflow = f.varSaida.Qlres * f.reservat.RGL;
		point.put("gasflow", gasflow); //CALCULAR A VAZAO DE GAS
		point.put("PtbgT", f.varSaida.PtbgT);//Pressão no topo da coluna de produção
		point.put("pp", f.varSaida.pp);//Pressão no topo da golfada
		point.put("PcsgB", f.varSaida.PcsgB);//Pressão na base do anular
		point.put("PcsgT", f.tempos.PcsgT);//Pressão no topo do anular
		point.put("Lslg", f.tempos.Lslg);//Comprimento da golfada
		point.put("Ltbg", f.tempos.Ltbg);//Altura da golfada no fundo da coluna
		point.put("Hplg", f.varSaida.Hplg);//Posição do pistão
		point.put("v0", v.v0);//Velocidade do pistão
		point.put("Qlres", f.varSaida.Qlres);//Vazão de líquido do reservatório
		point.put("tempo", (double)simulation.tempo);//tempo de simulação
		
		return point;
	}
	
	/**
	 * Set the stop for killing the thread
	 * @param stop the stop to set
	 */
	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	/**
	 * Set the initialVariables
	 * @param v The variables for the simulation
	 */
	public void setInitialCondition(Map<String, Double> variables) {
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		Conversion       conv = new Conversion();
		
		f.fluido.BSW = Float.parseFloat(String.valueOf(variables.get("fluidBSW")));
		f.fluido.APi = variables.get("fluidAPI");
		f.fluido.SGagua = variables.get("fluidSGWater");
		f.fluido.SGgas = variables.get("fluidSGGas");
		f.fluido.GAMA = variables.get("fluidGammaGas");
		
		f.tubing.Lcauda = variables.get("tubingLength");
		f.tubing.E = conv.mmToM(variables.get("tubingRoughness"));
		f.tubing.DItbg = conv.inchToM(variables.get("tubingInternal"));
		f.tubing.DOtbg = conv.inchToM(variables.get("tubingExternal"));
		f.tubing.peso = variables.get("tubingWeigth");
		
		f.casing.comprimento = variables.get("casingLength");
		f.casing.rugosidade = conv.mmToM(variables.get("casingRoughness"));
		f.casing.DIcsg = conv.inchToM(variables.get("casingInternal"));
		f.casing.DEcsg = conv.inchToM(variables.get("casingExternal"));
		f.casing.peso = variables.get("casingWeigth");

		f.linhaPro.Psep = conv.psiaToNPerM2(variables.get("prodlineSepPressure"));

		f.valvula.Dab = conv.inchToM(variables.get("motorvalveDiameter"));

		f.pistao.Mplg = variables.get("plungerMass");
		f.pistao.EfVed = Float.parseFloat(String.valueOf(variables.get("plungerEfi")));
		f.pistao.Lplg = Float.parseFloat(String.valueOf(variables.get("plungerLength")));
		f.pistao.Dplg = conv.inchToM(variables.get("plungerDiameter"));

		f.reservat.Pest = conv.kgfPerCm2ToPa(variables.get("reservoirStaticP")) + 101325;
		f.reservat.Pteste = conv.kgfPerCm2ToPa(variables.get("reservoirTestFlow")) + 101325;
		f.reservat.Qteste = variables.get("reservoirTestPressure");
		f.reservat.RGL = Integer.parseInt(String.valueOf(variables.get("reservoirRGL")));
		
		//Setando os passos de integracao
		c.step     	   = variables.get("stepRise")/1000.0;
		c.step_ 	   = variables.get("stepProduction")/1000.0;
		c.step_aft     = variables.get("stepAfterflow")/1000.0;
		c._stepGas     = variables.get("stepBuildGas")/1000.0;
		c._stepGas2Liq = variables.get("stepBuildGasLiq")/1000.0;
		c._stepLiq     = variables.get("stepBuidLiq")/1000.0;

		//Setando dados de amostragem
		/*TimeStepsData samplingData ;
		samplingData.slugLiftTimeStep      = editSampSlugLift->getFloat()     /1000.0;
		samplingData.slugProductionTimeStep= editSampSlugProd->getFloat()     /1000.0;
		samplingData.afterflowTimeStep     = editSampAfter->getFloat()        /1000.0;
		samplingData.buildupTimeStepGas    = editSampBuildupGas->getFloat()   /1000.0;
		samplingData.buildupTimeStepGasLiq = editSampBuildupGasLiq->getFloat()/1000.0;
		samplingData.buildupTimeStepLiq    = editSampBuildupLiq->getFloat()   /1000.0;*/

		//Setando dados iniciais e variaveis de tempo de controle
		f.tempos.Lslg      = variables.get("initialSlug");
		f.tempos.PcsgT     = conv.psigToPa(variables.get("initialCasingTop"));
		f.tempos.Ontime    = Integer.parseInt(String.valueOf(variables.get("initialOpenValve")));
		f.tempos.Offtime   = Integer.parseInt(String.valueOf(variables.get("initialCloseValve")));
		f.tempos.Afterflow = Integer.parseInt(String.valueOf(variables.get("initialAfterflow")));
	}
	
}
