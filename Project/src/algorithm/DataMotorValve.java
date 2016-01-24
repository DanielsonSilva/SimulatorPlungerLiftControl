package algorithm;

public final class DataMotorValve {

	public double Dab; /*!< Diâmetro de abertura da válvula (mm) */
	
	private static DataMotorValve instance;
	
	public static synchronized DataMotorValve getInstance() {
		if (instance == null) {
			instance = new DataMotorValve();
		}
		return instance;
	}	
	
	private DataMotorValve() {
		// Empty
	}

	public void Limpar() {
		this.Dab = 0;
	}
}
