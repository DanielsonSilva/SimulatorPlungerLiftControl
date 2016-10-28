package algorithm;

/**
 * Conversion of measures
 * @author Danielson Flávio Xavier da Silva
 *
 */
public class Conversion {
	
	double pi;
	double r;
	double airMolarMass;
	double g;
	
	/**
	 * Constructor
	 */
	public Conversion() {
		this.pi = 3.1415926535897932384626433832795f;
		this.r = 8.314472;
		this.airMolarMass = 0.02897;
		this.g = 9.80665;	
	}
	
	/**
	 * Convert Pascal to Psi measure
	 * @param pasc Value in pascal
	 * @return Value in Pascal
	 */
	public double  pascalToPsi( double pasc ) {
		return pasc/6894.757;
	}
	
	/************************************
	 * Conversões de ângulo
	 ************************************/

	double degreesToRadians( double deg ) {
		//DataConstants c = DataConstants.getInstance();
		return deg * 2 * this.pi / 360;
	}

	double radiansToDegrees( double rad ) {
		return rad * 360/ ( 2 * this.pi );
	}

	/************************************
	 * Conversões de comprimento
	 ************************************/

	//! Converte de metro para milímetro
	double mToMm( double m ) {
		return m * 1000.0f;
	}

	//! Converte de metro para centímetro
	double mToCm( double m ) {
		return m * 100.0f;
	}

	//! Converte de metro para polegada
	double mToInch( double m ) {
		return m * 39.37007874015748f;
	}

	//! Converte de metro para pé
	double mToFt( double m ) {
		return m * 3.280839895013123f;
	}

	//! Converte de milímetro para metro
	double mmToM( double mm ) {
		return mm * 0.001f;
	}

	//! Converte de milímetro para centímetro
	double mmToCm( double mm ) {
		return mm * 0.01f;
	}

	//! Converte de milímetro para polegada
	double mmToInch( double mm ) {
		return mm * 0.03937007874015748f;
	}

	//! Converte de milímetro para pé
	double mmToFt( double mm ) {
		return mm * 0.003280839895013123f;
	}

	//! Converte de centímetro para metro
	double cmToM( double cm ) {
		return cm * 0.01f;
	}

	//! Converte de centímetro para milímetro
	double cmToMm( double cm ) {
		return cm * 10.0f;
	}

	//! Converte de centímetro para polegada
	double cmToInch( double cm ) {
		return cm * 0.3937007874015748f;
	}

	//! Converte de centímetro para pé
	double cmToFt( double cm ) {
		return cm * 0.03280839895013123f;
	}

	//! Converte de polegadas para metro
	double inchToM( double inch ) {
		return inch * 0.0254f;
	}

	//! Converte de polegadas para centímetro
	double inchToCm( double inch ) {
		return inch * 2.54f;
	}

	//! Converte de polegadas para milímetro
	double inchToMm( double inch ) {
		return inch * 25.4f;
	}

	//! Converte de polegadas para pé
	double inchToFt( double inch ) {
		return inch * 0.0833333333333333f;
	}

	//! Converte de pé para metro
	double ftToM( double ft ) {
		return ft * 0.3048f;
	}

	//! Converte de pé para centímetro
	double ftToCm( double ft ) {
		return ft * 30.48f;
	}

	//! Converte de pé para milímetro
	double ftToMm( double ft ) {
		return ft * 304.8f;
	}

	//! Converte de pé para polegada
	double ftToInch( double ft ) {
		return ft * 12.0f;
	}


	/*********************
	 * Conversões de área
	 *********************/

	//! Converte de m² para mm²
	double m2ToMm2( double m2 ) {
		return m2 * 1000000.0f;
	}

	//! Converte de m² para pé²
	double m2ToFt2( double m2 ) {
		return m2 * 10.763910416709722f;
	}

	//! Converte de m² para polegadas²
	double m2ToInch2( double m2 ) {
		return m2 * 1550.0031000062002f;
	}

	//! Converte de mm² para m²
	double mm2ToM2( double mm2 ) {
		return mm2 * 0.000001f;
	}

	//! Converte de mm² para pé²
	double mm2ToFt2( double mm2 ) {
		return mm2 * 0.000010763910417f;
	}

	//! Converte de mm² para polegada²
	double mm2ToInch2( double mm2 ) {
		return mm2 * 0.001550003100006f;
	}

	//! Converte de pé² para m²
	double ft2ToM2( double ft2 ) {
		return ft2 * 0.09290304f;
	}

	//! Converte de pé² para mm²
	double ft2ToMm2( double ft2 ) {
		return ft2 * 92903.04f;
	}

	//! Converte de pé² para polegada²
	double ft2ToInch2( double ft2 ) {
		return ft2 * 144.0f;
	}

	//! Converte de polegada² para m²
	double inch2ToM2( double inch2 ) {
		return inch2 * 0.00064516f; 
	}

	//! Converte de polegada² para mm²
	double inch2ToMm2( double inch2 ) {
		return inch2 * 645.16f; 
	}

	//! Converte de polegada² para pé²
	double inch2ToFt2( double inch2 ) {
		return inch2 * 0.006944444444444f; 
	}


	/************************************
	 * Conversões de densidade
	 ************************************/

	//! Converte de Kg/m³ para g/cm³
	double kgPerM3ToGPerCm3( double kgPerM3 ) {
		return kgPerM3 * 0.001f;
	}

	//! Converte de Kg/m³ para g/m³
	double kgPerM3ToGPerM3( double kgPerM3 ) {
		return kgPerM3 * 1000.0f;
	}

	//! Converte de Kg/m³ para lb/ft³
	double kgPerM3ToLbPerFt3( double kgPerM3 ) {
		return kgPerM3 * 0.06242797f;
	}

	//! Converte de Kg/m³ para lb/in³
	double kgPerM3ToLbPerInch3( double kgPerM3 ) {
		return kgPerM3 * 0.00003613f;
	}

	//! Converte de g/cm³ para kg/m³
	double gPerCm3ToKgPerM3( double gPerCm3 ) {
		return gPerCm3 * 1000.0f;
	}

	//! Converte de g/cm³ para g/m³
	double gPerCm3ToGPerM3( double gPerCm3 ) {
		return gPerCm3 * 1000000.0f;
	}

	//! Converte de g/cm³ para lb/ft³
	double gPerCm3ToLbPerFt3( double gPerCm3 ) {
		return gPerCm3 * 62.42797f;
	}

	//! Converte de g/cm³ para lb/in³
	double gPerCm3ToLbPerInch3( double gPerCm3 ) {
		return gPerCm3 * 0.03613f;
	}

	//! Converte de g/m³ para kg/m³
	double gPerM3ToKgPerM3( double gPerM3 ) {
		return gPerM3 * 0.001f;
	}

	//! Converte de g/m³ para g/cm³
	double gPerM3ToGPerCm3( double gPerM3 ) {
		return gPerM3 * 0.000001f;
	}

	//! Converte de g/m³ para lb/ft³
	double gPerM3ToLbPerFt3( double gPerM3 ) {
		return gPerM3 * 0.00006242797f;
	}

	//! Converte de g/m³ para lb/in³
	double gPerM3ToLbPerInch3( double gPerM3 ) {
		return gPerM3 * 0.00000003613f;
	}

	//! Converte de lb/ft³ para kg/m³
	double lbPerFt3ToKgPerM3( double lbPerFt3 ) {
		return lbPerFt3 * 16.01846f;
	}

	//! Converte de lb/ft³ para g/cm³
	double lbPerFt3ToGPerCm3( double lbPerFt3 ) {
		return lbPerFt3 * 0.01601846f;
	}

	//! Converte de lb/ft³ para g/m³
	double lbPerFt3ToGPerM3( double lbPerFt3 ) {
		return lbPerFt3 * 16018.46f;
	}

	//! Converte de lb/ft³ para kg/m³
	double lbPerFt3ToLbPerInch3( double lbPerFt3 ) {
		return lbPerFt3 * 0.00057875f;
	}

	//! Converte de lb/in³ para kg/m³
	double lbPerInch3ToKgPerM3( double lbPerInch3 ) {
		return lbPerInch3 * 27679.9f;
	}

	//! Converte de lb/in³ para g/cm³
	double lbPerInch3ToGPerCm3( double lbPerInch3 ) {
		return lbPerInch3 * 27.6799f;
	}

	//! Converte de lb/in³ para g/m³
	double lbPerInch3ToGPerM3( double lbPerInch3 ) {
		return lbPerInch3 * 27679900.0f;
	}

	//! Converte de lb/in³ para kg/m³
	double lbPerInch3ToLbPerFt3( double lbPerInch3 ) {
		return lbPerInch3 * 1728.0f;
	}


	/************************************
	 * Conversões de força
	 ************************************/

	//! Converte de Newton para quilo-Newton
	double nToKn( double n ) {
		return n * 0.001f;
	}

	//! Converte de Newton para quilograma-força
	double nToKgf( double n ) {
		return n * 0.10197162f;
	}

	//! Converte de Newton para libra-força
	double nToLbf( double n ) {
		return n * 0.22480892f;
	}

	//! Converte de quilo-Newton para Newton
	double knToN( double kn ) {
		return kn * 1000.0f;
	}

	//! Converte de quilo-Newton para quilograma-força
	double knToKgf( double kn ) {
		return kn * 101.9716213f;
	}

	//! Converte de quilo-Newton para libra-força
	double knToLbf( double kn ) {
		return kn * 224.80892366f;
	}

	//! Converte de quilograma-força para Newton
	double kgfToN( double kgf ) {
		return kgf * 9.80665f;
	}

	//! Converte de quilograma-força para quilo-Newton
	double kgfToKn( double kgf ) {
		return kgf * 0.00980665f;
	}

	//! Converte de quilograma-força para libra-força
	double kgfToLbf( double kgf ) {
		return kgf * 2.20462243f;
	}

	//! Converte de libra-força para Newton
	double lbfToN( double lbf ) {
		return lbf * 4.448222f;
	}

	//! Converte de libra-força para Newton
	double lbfToKn( double lbf ) {
		return lbf * 0.00444822f;
	}

	//! Converte de libra-força para Newton
	double lbfToKgf( double lbf ) {
		return lbf * 0.45359241f;
	}


	/**************************
	 * Conversões de peso
	 **************************/

	//! Converte de quilograma para grama
	double kgToG( double kg ) {
		return kg * 1000.0f;
	}

	//! Converte de quilograma para libra
	double kgToLb( double kg ) {
		return kg * 2.2046225f;
	}

	//! Converte de grama para quilograma
	double gToKg( double g ) {
		return g * 0.001f;
	}

	//! Converte de grama para libra
	double gToLb( double g ) {
		return g * 0.0022046f;
	}

	//! Converte de libra para quilograma
	double lbToKg( double lb ) {
		return lb * 0.4535924f;
	}

	//! Converte de libra para grama
	double lbToG( double lb ) {
		return lb * 453.5924f;
	}


	/**************************
	 * Conversões de potência
	 **************************/

	//! Converte de Watt para Hp
	double wToHp( double w ) {
		return w * 0.00134102203849f;
	}

	//! Converte de Watt para CV
	double wToCv( double w ) {
		return w * 0.001359621617304f;
	}

	//! Converte de Hp para Watt
	double hpToW( double hp ) {
		return hp * 745.6999f;
	}

	//! Converte de Hp para CV
	double hpToCv( double hp ) {
		return hp * 1.01386970406136f;
	}

	//! Converte de CV para Watt
	double cvToW( double cv ) {
	    return cv * 735.49875f;
	}

	//! Converte de CV para Hp
	double cvToHp( double cv ) {
	    return cv * 0.986320033032055f;
	}


	/**************************
	 * Conversões de pressão
	 **************************/

	//! Converte de Pascal para quilo-Pascal
	double paToKpa( double pa ) {
		return pa * 0.001f;
	}

	//! Converte de Pascal para Newton / metro²
	double paToNPerM2( double pa ) {
		return pa;
	}

	//! Converte de Pascal para atm
	double paToAtm( double pa ) {
		return pa * 0.000009869232667f;
	}

	//! Converte de Pascal para Kgf / cm²
	double paToKgfPerCm2( double pa ) {
		return pa * 0.00001019716213f;
	}

	//! Converte de Pascal para psi
	public double paToPsi( double pa ) {
		return pa * 0.000145037743897f;
	}

	//! Converte de Pascal para Libra / pé²
	double paToLbfPerFt2( double pa ) {
		return pa * 0.020885433788371f;
	}

	//! Converte de quilo-Pascal para Pascal
	double kpaToPa( double kpa ) {
		return kpa * 1000.0f;
	}

	//! Converte de quilo-Pascal para Newton / metro²
	double kpaToNPerM2( double kpa ) {
		return kpa * 1000.0f;
	}

	//! Converte de quilo-Pascal para atm
	double kpaToAtm( double kpa ) {
		return kpa * 0.009869232667f;
	}

	//! Converte de quilo-Pascal para kgf / cm²
	double kpaToKgfPerCm2( double kpa ) {
		return kpa * 0.010197162129779f;
	}

	//! Converte de quilo-Pascal para psi
	double kpaToPsi( double kpa ) {
		return kpa * 0.145037743897f;
	}

	//! Converte de quilo-Pascal para Libra / pé²
	double kpaToLbfPerFt2( double kpa ) {
		return kpa * 20.88543378837124f;
	}

	//! Converte de Newton / metro² para Pascal
	double nPerM2ToPa( double nPerM2 ) {
		return nPerM2;
	}

	//" Converte de Newton / metro² para quilo-Pascal
	double nPerM2ToKpa( double nPerM2 ) {
		return nPerM2 * 0.001f;
	}

	//! Converte de Newton / metro² para atm
	double nPerM2ToAtm( double nPerM2 ) {
		return nPerM2 * 0.000009869232667f;
	}

	//! Converte de Newton / metro² para Kgf / cm²
	double nPerM2ToKgfPerCm2( double nPerM2 ) {
		return nPerM2 * 0.00001019716213f;
	}

	//! Converte de Newton / metro² para psi
	double nPerM2ToPsi( double nPerM2 ) {
		return nPerM2 * 0.000145037743897f;
	}

	//! Converte de Newton / metro² para Libra / pé²
	double nPerM2ToLbfPerFt2( double nPerM2 ) {
		return nPerM2 * 0.020885433788371f;
	}

	//! Converte de atm para Pascal
	double atmToPa( double atm ) {
		return atm * 101325.0f;
	}

	//! Converte de atm para quilo-Pascal
	double atmToKpa( double atm ) {
		return atm * 101.325f;
	}

	//! Converte de atm para Newton / metro²
	double atmToNPerM2( double atm ) {
		return atm * 101325.0f;
	}

	//! Converte de atm para Kgf / cm²
	double atmToKgfPerCm2( double atm ) {
		return atm * 1.033227452799f;
	}

	//! Converte de atm para psi
	double atmToPsi( double atm ) {
		return atm * 14.695949400392212f;
	}

	//! Converte de atm para Libra / pé²
	double atmToLbfPerFt2( double atm ) {
		return atm * 2116.2165786f;
	}

	//! Converte de Kgf / cm² para Pascal
	double kgfPerCm2ToPa( double kgfPerCm2 ) {
		return kgfPerCm2 * 98066.5f;
	}

	//! Converte de Kgf / cm² para quilo-Pascal
	double kgfPerCm2ToKpa( double kgfPerCm2 ) {
		return kgfPerCm2 * 98.0665f;
	}

	//! Converte de Kgf / cm² para Newton / metro²
	double kgfPerCm2ToNPerM2( double kgfPerCm2 ) {
		return kgfPerCm2 * 98066.5f;
	}

	//! Converte de Kgf / cm² para atm
	double kgfPerCm2ToAtm( double kgfPerCm2 ) {
		return kgfPerCm2 * 0.967841105354059f;
	}

	//! Converte de Kgf / cm² para psi
	double kgfPerCm2ToPsi( double kgfPerCm2 ) {
		return kgfPerCm2 * 14.223343911902914f;
	}

	//! Converte de Kgf / cm² para Libra / pé²
	double kgfPerCm2ToLbfPerFt2( double kgfPerCm2 ) {
		return kgfPerCm2 * 2048.1613926f;
	}

	//! Converte de psi para Pascal
	double psiaToPa( double psi ) {
		return psi * 6894.757f;
	}

	//! Converte de psi para quilo-Pascal
	double psiaToKpa( double psi ) {
		return psi * 6.894757f;
	}

	//! Converte de psi para Newton / metro²
	double psiaToNPerM2( double psi ) {
		return psi * 6894.757f;
	}

	//! Converte de psi para atm
	double psiaToAtm( double psi ) {
		return psi * 0.068045961016531f;
	}

	//! Converte de psi para kgf / cm²
	double psiaToKgfPerCm2( double psi ) {
		return psi * 0.070306954974431f;
	}

	//! Converte de psi para Libra / pé²
	double psiaToLbfPerFt2( double psi ) {
		return psi * 144.0f;
	}

	//! Converte de psig para Pascal
	double psigToPa( double psi ) {
		return psi * 6.8947573e+3f + 101.32500e+3 ;
	}

	//! Converte de Libra / pe² para Pascal
	double lbfPerFt2ToPa( double lbfPerFt2 ) {
		return lbfPerFt2 * 47.88025694444f;
	}

	//! Converte de Libra / pe² para quilo-Pascal
	double lbfPerFt2ToKpa( double lbfPerFt2 ) {
		return lbfPerFt2 * 0.047880256944444f;
	}

	//! Converte de Libra / pe² para Newton / metro²
	double lbfPerFt2ToNPerM2( double lbfPerFt2 ) {
		return lbfPerFt2 * 47.88025694444f;
	}

	//! Converte de Libra / pe² para atm
	double lbfPerFt2ToAtm( double lbfPerFt2 ) {
		return lbfPerFt2 * 0.000472541395948f;
	}

	//! Converte de Libra / pe² para kgf / cm²
	double lbfPerFt2ToKgfPerCm2( double lbfPerFt2 ) {
		return lbfPerFt2 * 0.000488242742878f;
	}

	//! Converte de Libra / pe² para psi
	double lbfPerFt2ToPsi( double lbfPerFt2 ) {
		return lbfPerFt2 * 0.006944444444444f;
	}


	/*****************************
	 * Conversões de temperatura
	 *****************************/

	//! Converte de Celsius para Fahrenheit
	double celsiusToFahrenheit( double celsius ) {
		return celsius * 1.8f + 32.0f;
	}

	//! Converte de Celsius para Kelvin
	double celsiusToKelvin( double celsius ) {
		return celsius + 273.0f;
	}

	//! Converte de Fahrenheit para Celsius
	double fahrenheitToCelsius( double fahrenheit ) {
		return ( fahrenheit - 32.0f ) / 1.8f;
	}

	//! Converte de Fahrenheit para Kelvin
	double fahrenheitToKelvin( double fahrenheit ) {
		return ( fahrenheit + 459.67f ) / 1.8f;
	}

	//! Converte de Kelvin para Celsius
	double kelvinToCelsius( double kelvin ) {
		return kelvin - 273.0f;
	}

	//! Converte de Kelvin para fahrenheit
	double kelvinToFahrenheit( double kelvin ) {
		return ( kelvin * 1.8f ) - 459.67f;
	}


	/**********************
	 * Conversões de tempo
	 **********************/

	//! Converte de segundos para minuto 
	double secondToMinute( double second ) {
		return second / 60.0f;
	}

	//! Converte de segundos para hora 
	double secondToHour( double second ) {
		return second / 3600.0f;
	}

	//! Converte de segundos para dia 
	double secondToDay( double second ) {
		return second / 86400.0f;
	}

	//! Converte de minuto para segundo
	double minuteToSecond( double minute ) {
		return minute * 60.0f;
	}

	//! Converte de minuto para hora
	double minuteToHour( double minute ) {
		return minute / 60.0f;
	}

	//! Converte de minuto para dia
	double minuteToDay( double minute ) {
		return minute / 1440.0f;
	}

	//! Converte de hora para segundo
	double hourToSecond( double hour ) {
		return hour * 3600.0f;
	}

	//! Converte de hora para minuto
	double hourToMinute( double hour ) {
		return hour * 60.0f;
	}

	//! Converte de hora para segundo
	double hourToDay( double hour ) {
		return hour / 24.0f;
	}

	//! Converte de dia para segundo
	double dayToSecond( double day ) {
		return day * 86400.0f;
	}

	//! Converte de dia para minuto
	double dayToMinute( double day ) {
		return day * 1440.0f;
	}

	//! Converte de dia para segundo
	double dayToHour( double day ) {
		return day * 24.0f;
	}


	/***********************
	 * Conversões de torque
	 ***********************/

	//! Converte de Newton*metro para kgf*metro
	double nMToKgfM( double nM ) {
	    return nM * 0.1019716213f;
	}

	//! Converte de Newton*metro para kgf*cm
	double nMToKgfCm( double nM ) {
	    return nM * 10.19716213f;
	}

	//! Converte de Newton*metro para lbf*pé
	double nMToLbfFt( double nM ) {
	    return nM * 0.7375621212f;
	}

	//! Converte de Newton*metro para lbf*polegada
	double nMToLbfInch( double nM ) {
	    return nM * 8.8507480652f;
	}

	//! Converte de kgf*metro para Newton*metro
	double kgfMToNM( double kgfM ) {
	    return kgfM * 9.80665f;
	}

	//! Converte de kgf*metro para kgf*cm
	double kgfMToKgfCm( double kgfM ) {
		return kgfM * 100.0f;
	}

	//! Converte de kgf*metro para lbg*pé
	double kgfMToLbfFt( double kgfM ) {
		return kgfM * 7.2330135759f;
	}

	//! Converte de kgf*metro para lbg*polegada
	double kgfMToLbfInch( double kgfM ) {
	    return kgfM * 86.7961885136f;
	}

	//! Converte de kgf*cm para Newton*metro
	double kgfCmToNM( double kgfCm ) {
	    return kgfCm * 0.0980665f;
	}

	//! Converte de kgf*cm para kgf*m
	double kgfCmToKgfM( double kgfCm ) {
	    return kgfCm * 0.01f;
	}

	//! Converte de kgf*cm para lbf*pé
	double kgfCmToLbfFt( double kgfCm ) {
	    return kgfCm * 0.072330135759f;
	}

	//! Converte de kgf*cm para lbf*polegada
	double kgfCmToLbfInch( double kgfCm ) {
	    return kgfCm * 0.867961885136f;
	}

	//! Converte de lbf*pé para N*m
	double lbfFtToNM( double lbfFt ) {
	    return lbfFt * 1.355818f;
	}

	//! Converte de lbf*pé para N*m
	double lbfFtToKgfM( double lbfFt ) {
	    return lbfFt * 0.1382549596f;
	}

	//! Converte de lbf*pé para N*m
	double lbfFtToKgfCm( double lbfFt ) {
	    return lbfFt * 13.8254959f;
	}

	//! Converte de lbf*pé para N*m
	double lbfFtToLbfInch( double lbfFt ) {
	    return lbfFt * 12.0f;
	}

	//! Converte de lbf*polegada para N*m
	double lbfInchToNM( double lbfInch ) {
	    return lbfInch * 0.1129848f;
	}

	//! Converte de lbf*polegada para kgf*m
	double lbfInchToKgfM( double lbfInch ) {
	    return lbfInch * 0.0115212466f;
	}

	//! Converte de lbf*polegada para N*m
	double lbfInchToKgfCm( double lbfInch ) {
	    return lbfInch * 1.15212466f;
	}

	//! Converte de lbf*polegada para N*m
	double lbfInchToLbfFt( double lbfInch ) {
	    return lbfInch * 0.0833333333f;
	}


	/***********************
	 * Conversões de vazão
	 ***********************/

	//! Converte de m³ / s para m³ / dia
	double m3PerSecondToM3PerDay( double m3PerSecond ) {
		float aux = ((int)( m3PerSecond*10000))/10000;
		return aux * 86400.0;
	}

	//! Converte de m³ / s para m³ / hora
	double m3PerSecondToM3PerHour( double m3PerSecond ) {
		return m3PerSecond * 3600.0f;
	}

	//! COnverte de m³ / h para m³ / s
	double m3PerHourToM3PerSecond( double m3PerHour ) {
		return m3PerHour * 0.0002777778f;
	}

	//! Converte de m³ / h para m³ / dia
	double m3PerHourToM3PerDay( double m3PerHour ) {
		return m3PerHour * 24.0f;
	}

	//! Converte de m³ / dia para m³ / s
	double m3PerDayToM3PerSecond( double m3PerDay ) {
		return m3PerDay / 86400.0f;
	}

	//! Converte de m³ / dia para m³ / h
	double m3PerDayToM3PerHour( double m3PerDay ) {
		return m3PerDay / 24.0f;
	}


	/***************************
	 * Conversões de velocidade
	 ***************************/

	//! Converte de m / s para Pé / min
	double mPerSecondToFtPerMinute( double mPerSecond ) {
		return mPerSecond * 196.85039370078738f;
	}

	//! Converte de m / s para km / h
	double mPerSecondToKmPerHour( double mPerSecond ) {
		return mPerSecond * 3.6f;
	}

	//! Converte de Pé / min para m / s
	double ftPerMinuteToMPerSecond( double ftPerMinute ) {
		return ftPerMinute * 0.00508f;
	}

	//! Converte de Pé / min para km / h
	double ftPerMinuteToKmPerHour( double ftPerMinute ) {
		return ftPerMinute * 0.018288f;
	}

	//! Converte de km / h para m / s
	double kmPerHourToMPerSecond( double kmPerHour ) {
		return kmPerHour * 0.277777777777778f;
	}

	//! Converte de km / h para Pé / min
	double kmPerHourToFtPerMinute( double kmPerHour ) {
		return kmPerHour * 54.680664916885235f;
	}


	/*************************
	 * Conversões específicas
	 *************************/

	//! Converte de api para Sg
	double apiToSg( double api ) {
		return 141.5f / ( api + 131.5f ) ;
	}

}
