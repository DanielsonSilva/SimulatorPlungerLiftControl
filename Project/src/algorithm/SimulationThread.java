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
	
}
