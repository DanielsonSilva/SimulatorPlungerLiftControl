package algorithm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import controller.Controller;
import controller.TimingController;

public class SimulationThread implements Runnable {
	/**
	 * Variable for the calculation
	 */
	private Simulation simulation;
	/**
	 * Represents the stop flag
	 */
	private boolean stop;
	/**
	 * Represents the controller
	 */
	private Controller control;
	/** 
	 * Variable for printing the variables in file
	 */
	PrintWriter writer;
	
	/**
	 * Constructor
	 */
	public SimulationThread() {
		this.stop = false;		
		this.simulation = Simulation.getInstance();
	}
	
	/**
	 * Runnable method
	 */
	public void run() {
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		
		while ( !stop ) {
			switch ( c.estagio ) {
				case 0:
					// Only the first time that has started the simulation
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
					this.stop = true;
					break;
			}
		}
		// When the thread will be killed
		f.Limpar();
		c.init();
		v.init();
	}
	
	private void imprimirVariaveis() {
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		
		try {
			writer = new PrintWriter("DataPlungerJava.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		f.imprimirVariaveis(writer);
		c.imprimirVariaveis(writer);
		v.imprimirVariaveis(writer);
		
		writer.close();
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
		
		double gasflow = f.varSaida.Qlres * f.reservat.RGL;
		point.put("stage", (double)(c.estagio + 1));
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
		point.put("tempo", this.simulation.tempo);//tempo de simulação
		
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
		Conversion       conv = new Conversion();
		
		
		//Timing Controller
		this.control = new TimingController();
		Map<String,Double> controllerVariables = new HashMap<String,Double>();
		controllerVariables.put("value", 50.);
		controllerVariables.put("lowPlungerTime", 170.);
		controllerVariables.put("highPlungerTime", 200.);
		control.setVariables(controllerVariables);
		this.simulation.setController(control);
		
		
		f.fluido.BSW = variables.get("fluidBSW").floatValue();
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

		f.valvula.Dab = conv.inchToMm(variables.get("motorvalveDiameter"));

		f.pistao.Mplg = variables.get("plungerMass");
		f.pistao.EfVed = (variables.get("plungerEfi").floatValue()) / 100;
		f.pistao.Lplg = variables.get("plungerLength").floatValue();
		f.pistao.Dplg = conv.inchToM(variables.get("plungerDiameter"));

		f.reservat.Pest = conv.kgfPerCm2ToPa(variables.get("reservoirStaticP")) + 101325;
		f.reservat.Pteste = conv.kgfPerCm2ToPa(variables.get("reservoirTestFlow")) + 101325;
		f.reservat.Qteste = variables.get("reservoirTestPressure");
		f.reservat.RGL = variables.get("reservoirRGL").intValue();
		
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
		f.tempos.Ontime    = variables.get("initialOpenValve").intValue();
		f.tempos.Offtime   = variables.get("initialCloseValve").intValue();
		f.tempos.Afterflow = variables.get("initialAfterflow").intValue();
		
		/*System.out.println("01 :" + f.fluido.BSW); 
		System.out.println("02 :" + f.fluido.APi);
		System.out.println("03 :" + f.fluido.SGagua);
		System.out.println("04 :" + f.fluido.SGgas);
		System.out.println("05 :" + f.fluido.GAMA);	
		System.out.println("06 :" + f.tubing.Lcauda); 
		System.out.println("07 :" + f.tubing.E);
		System.out.println("08 :" + f.tubing.DItbg);
		System.out.println("09 :" + f.tubing.DOtbg);
		System.out.println("10 :" + f.tubing.peso);
		System.out.println("11 :" + f.casing.comprimento);
		System.out.println("12 :" + f.casing.rugosidade);
		System.out.println("13 :" + f.casing.DIcsg);
		System.out.println("14 :" + f.casing.DEcsg);
		System.out.println("15 :" + f.casing.peso);
		System.out.println("16 :" + f.linhaPro.Psep);
		System.out.println("17 :" + f.valvula.Dab);
		System.out.println("18 :" + f.pistao.Mplg);
		System.out.println("19 :" + f.pistao.EfVed);
		System.out.println("20 :" + f.pistao.Lplg);
		System.out.println("21 :" + f.pistao.Dplg);
		System.out.println("22 :" + f.reservat.Pest);
		System.out.println("23 :" + f.reservat.Pteste);
		System.out.println("24 :" + f.reservat.Qteste); 
		System.out.println("25 :" + f.reservat.RGL); 
		System.out.println("26 :" + c.step);
		System.out.println("27 :" + c.step_);
		System.out.println("28 :" + c.step_aft);
		System.out.println("29 :" + c._stepGas);
		System.out.println("30 :" + c._stepGas2Liq);
		System.out.println("31 :" + c._stepLiq);
		System.out.println("32 :" + f.tempos.Lslg);
		System.out.println("33 :" + f.tempos.PcsgT);
		System.out.println("34 :" + f.tempos.Ontime);
		System.out.println("35 :" + f.tempos.Offtime);
		System.out.println("36 :" + f.tempos.Afterflow);*/	
	}
	
}
