package algorithm;

public class DataPlunger {
	
	// Dados utilizados na simula��o
	float EfVed;   /*!< Eficiencia de Vedacao ao Gas (%)      */
	double Mplg;   /*!< Massa do pistao (kg)                  */
	float Lplg;    /*!< Comprimento do pistao (m)                */
	double Vqpl;   /*!< Velocidade de Queda no Liquido (m/s)  */
	double Vqpg;   /*!< Velocidade de Queda no Gas (m/s)      */
	double Dplg;   /*!< Di�metro do pist�o (m)                    */
	
	private static DataPlunger instance;
	
	public static synchronized DataPlunger getInstance() {
		if (instance == null) {
			instance = new DataPlunger();
		}
		return instance;
	}	
	
	private DataPlunger() {
		//Empty
	}
	
	public void Limpar() {
		this.EfVed= 0;
		this.Mplg = 0;
		this.Lplg = 0;
		this.Vqpl = 0;
		this.Vqpg = 0;
		this.Dplg = 0;
	}

}
