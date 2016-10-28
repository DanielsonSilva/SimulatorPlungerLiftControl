package algorithm;

/**
 * Conversion of measures
 * @author Danielson Fl�vio Xavier da Silva
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
	 * Convers�es de �ngulo
	 ************************************/

	double degreesToRadians( double deg ) {
		//DataConstants c = DataConstants.getInstance();
		return deg * 2 * this.pi / 360;
	}

	double radiansToDegrees( double rad ) {
		return rad * 360/ ( 2 * this.pi );
	}

	/************************************
	 * Convers�es de comprimento
	 ************************************/

	//! Converte de metro para mil�metro
	double mToMm( double m ) {
		return m * 1000.0f;
	}

	//! Converte de metro para cent�metro
	double mToCm( double m ) {
		return m * 100.0f;
	}

	//! Converte de metro para polegada
	double mToInch( double m ) {
		return m * 39.37007874015748f;
	}

	//! Converte de metro para p�
	double mToFt( double m ) {
		return m * 3.280839895013123f;
	}

	//! Converte de mil�metro para metro
	double mmToM( double mm ) {
		return mm * 0.001f;
	}

	//! Converte de mil�metro para cent�metro
	double mmToCm( double mm ) {
		return mm * 0.01f;
	}

	//! Converte de mil�metro para polegada
	double mmToInch( double mm ) {
		return mm * 0.03937007874015748f;
	}

	//! Converte de mil�metro para p�
	double mmToFt( double mm ) {
		return mm * 0.003280839895013123f;
	}

	//! Converte de cent�metro para metro
	double cmToM( double cm ) {
		return cm * 0.01f;
	}

	//! Converte de cent�metro para mil�metro
	double cmToMm( double cm ) {
		return cm * 10.0f;
	}

	//! Converte de cent�metro para polegada
	double cmToInch( double cm ) {
		return cm * 0.3937007874015748f;
	}

	//! Converte de cent�metro para p�
	double cmToFt( double cm ) {
		return cm * 0.03280839895013123f;
	}

	//! Converte de polegadas para metro
	double inchToM( double inch ) {
		return inch * 0.0254f;
	}

	//! Converte de polegadas para cent�metro
	double inchToCm( double inch ) {
		return inch * 2.54f;
	}

	//! Converte de polegadas para mil�metro
	double inchToMm( double inch ) {
		return inch * 25.4f;
	}

	//! Converte de polegadas para p�
	double inchToFt( double inch ) {
		return inch * 0.0833333333333333f;
	}

	//! Converte de p� para metro
	double ftToM( double ft ) {
		return ft * 0.3048f;
	}

	//! Converte de p� para cent�metro
	double ftToCm( double ft ) {
		return ft * 30.48f;
	}

	//! Converte de p� para mil�metro
	double ftToMm( double ft ) {
		return ft * 304.8f;
	}

	//! Converte de p� para polegada
	double ftToInch( double ft ) {
		return ft * 12.0f;
	}


	/*********************
	 * Convers�es de �rea
	 *********************/

	//! Converte de m� para mm�
	double m2ToMm2( double m2 ) {
		return m2 * 1000000.0f;
	}

	//! Converte de m� para p�
	double m2ToFt2( double m2 ) {
		return m2 * 10.763910416709722f;
	}

	//! Converte de m� para polegadas�
	double m2ToInch2( double m2 ) {
		return m2 * 1550.0031000062002f;
	}

	//! Converte de mm� para m�
	double mm2ToM2( double mm2 ) {
		return mm2 * 0.000001f;
	}

	//! Converte de mm� para p�
	double mm2ToFt2( double mm2 ) {
		return mm2 * 0.000010763910417f;
	}

	//! Converte de mm� para polegada�
	double mm2ToInch2( double mm2 ) {
		return mm2 * 0.001550003100006f;
	}

	//! Converte de p� para m�
	double ft2ToM2( double ft2 ) {
		return ft2 * 0.09290304f;
	}

	//! Converte de p� para mm�
	double ft2ToMm2( double ft2 ) {
		return ft2 * 92903.04f;
	}

	//! Converte de p� para polegada�
	double ft2ToInch2( double ft2 ) {
		return ft2 * 144.0f;
	}

	//! Converte de polegada� para m�
	double inch2ToM2( double inch2 ) {
		return inch2 * 0.00064516f; 
	}

	//! Converte de polegada� para mm�
	double inch2ToMm2( double inch2 ) {
		return inch2 * 645.16f; 
	}

	//! Converte de polegada� para p�
	double inch2ToFt2( double inch2 ) {
		return inch2 * 0.006944444444444f; 
	}


	/************************************
	 * Convers�es de densidade
	 ************************************/

	//! Converte de Kg/m� para g/cm�
	double kgPerM3ToGPerCm3( double kgPerM3 ) {
		return kgPerM3 * 0.001f;
	}

	//! Converte de Kg/m� para g/m�
	double kgPerM3ToGPerM3( double kgPerM3 ) {
		return kgPerM3 * 1000.0f;
	}

	//! Converte de Kg/m� para lb/ft�
	double kgPerM3ToLbPerFt3( double kgPerM3 ) {
		return kgPerM3 * 0.06242797f;
	}

	//! Converte de Kg/m� para lb/in�
	double kgPerM3ToLbPerInch3( double kgPerM3 ) {
		return kgPerM3 * 0.00003613f;
	}

	//! Converte de g/cm� para kg/m�
	double gPerCm3ToKgPerM3( double gPerCm3 ) {
		return gPerCm3 * 1000.0f;
	}

	//! Converte de g/cm� para g/m�
	double gPerCm3ToGPerM3( double gPerCm3 ) {
		return gPerCm3 * 1000000.0f;
	}

	//! Converte de g/cm� para lb/ft�
	double gPerCm3ToLbPerFt3( double gPerCm3 ) {
		return gPerCm3 * 62.42797f;
	}

	//! Converte de g/cm� para lb/in�
	double gPerCm3ToLbPerInch3( double gPerCm3 ) {
		return gPerCm3 * 0.03613f;
	}

	//! Converte de g/m� para kg/m�
	double gPerM3ToKgPerM3( double gPerM3 ) {
		return gPerM3 * 0.001f;
	}

	//! Converte de g/m� para g/cm�
	double gPerM3ToGPerCm3( double gPerM3 ) {
		return gPerM3 * 0.000001f;
	}

	//! Converte de g/m� para lb/ft�
	double gPerM3ToLbPerFt3( double gPerM3 ) {
		return gPerM3 * 0.00006242797f;
	}

	//! Converte de g/m� para lb/in�
	double gPerM3ToLbPerInch3( double gPerM3 ) {
		return gPerM3 * 0.00000003613f;
	}

	//! Converte de lb/ft� para kg/m�
	double lbPerFt3ToKgPerM3( double lbPerFt3 ) {
		return lbPerFt3 * 16.01846f;
	}

	//! Converte de lb/ft� para g/cm�
	double lbPerFt3ToGPerCm3( double lbPerFt3 ) {
		return lbPerFt3 * 0.01601846f;
	}

	//! Converte de lb/ft� para g/m�
	double lbPerFt3ToGPerM3( double lbPerFt3 ) {
		return lbPerFt3 * 16018.46f;
	}

	//! Converte de lb/ft� para kg/m�
	double lbPerFt3ToLbPerInch3( double lbPerFt3 ) {
		return lbPerFt3 * 0.00057875f;
	}

	//! Converte de lb/in� para kg/m�
	double lbPerInch3ToKgPerM3( double lbPerInch3 ) {
		return lbPerInch3 * 27679.9f;
	}

	//! Converte de lb/in� para g/cm�
	double lbPerInch3ToGPerCm3( double lbPerInch3 ) {
		return lbPerInch3 * 27.6799f;
	}

	//! Converte de lb/in� para g/m�
	double lbPerInch3ToGPerM3( double lbPerInch3 ) {
		return lbPerInch3 * 27679900.0f;
	}

	//! Converte de lb/in� para kg/m�
	double lbPerInch3ToLbPerFt3( double lbPerInch3 ) {
		return lbPerInch3 * 1728.0f;
	}


	/************************************
	 * Convers�es de for�a
	 ************************************/

	//! Converte de Newton para quilo-Newton
	double nToKn( double n ) {
		return n * 0.001f;
	}

	//! Converte de Newton para quilograma-for�a
	double nToKgf( double n ) {
		return n * 0.10197162f;
	}

	//! Converte de Newton para libra-for�a
	double nToLbf( double n ) {
		return n * 0.22480892f;
	}

	//! Converte de quilo-Newton para Newton
	double knToN( double kn ) {
		return kn * 1000.0f;
	}

	//! Converte de quilo-Newton para quilograma-for�a
	double knToKgf( double kn ) {
		return kn * 101.9716213f;
	}

	//! Converte de quilo-Newton para libra-for�a
	double knToLbf( double kn ) {
		return kn * 224.80892366f;
	}

	//! Converte de quilograma-for�a para Newton
	double kgfToN( double kgf ) {
		return kgf * 9.80665f;
	}

	//! Converte de quilograma-for�a para quilo-Newton
	double kgfToKn( double kgf ) {
		return kgf * 0.00980665f;
	}

	//! Converte de quilograma-for�a para libra-for�a
	double kgfToLbf( double kgf ) {
		return kgf * 2.20462243f;
	}

	//! Converte de libra-for�a para Newton
	double lbfToN( double lbf ) {
		return lbf * 4.448222f;
	}

	//! Converte de libra-for�a para Newton
	double lbfToKn( double lbf ) {
		return lbf * 0.00444822f;
	}

	//! Converte de libra-for�a para Newton
	double lbfToKgf( double lbf ) {
		return lbf * 0.45359241f;
	}


	/**************************
	 * Convers�es de peso
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
	 * Convers�es de pot�ncia
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
	 * Convers�es de press�o
	 **************************/

	//! Converte de Pascal para quilo-Pascal
	double paToKpa( double pa ) {
		return pa * 0.001f;
	}

	//! Converte de Pascal para Newton / metro�
	double paToNPerM2( double pa ) {
		return pa;
	}

	//! Converte de Pascal para atm
	double paToAtm( double pa ) {
		return pa * 0.000009869232667f;
	}

	//! Converte de Pascal para Kgf / cm�
	double paToKgfPerCm2( double pa ) {
		return pa * 0.00001019716213f;
	}

	//! Converte de Pascal para psi
	public double paToPsi( double pa ) {
		return pa * 0.000145037743897f;
	}

	//! Converte de Pascal para Libra / p�
	double paToLbfPerFt2( double pa ) {
		return pa * 0.020885433788371f;
	}

	//! Converte de quilo-Pascal para Pascal
	double kpaToPa( double kpa ) {
		return kpa * 1000.0f;
	}

	//! Converte de quilo-Pascal para Newton / metro�
	double kpaToNPerM2( double kpa ) {
		return kpa * 1000.0f;
	}

	//! Converte de quilo-Pascal para atm
	double kpaToAtm( double kpa ) {
		return kpa * 0.009869232667f;
	}

	//! Converte de quilo-Pascal para kgf / cm�
	double kpaToKgfPerCm2( double kpa ) {
		return kpa * 0.010197162129779f;
	}

	//! Converte de quilo-Pascal para psi
	double kpaToPsi( double kpa ) {
		return kpa * 0.145037743897f;
	}

	//! Converte de quilo-Pascal para Libra / p�
	double kpaToLbfPerFt2( double kpa ) {
		return kpa * 20.88543378837124f;
	}

	//! Converte de Newton / metro� para Pascal
	double nPerM2ToPa( double nPerM2 ) {
		return nPerM2;
	}

	//" Converte de Newton / metro� para quilo-Pascal
	double nPerM2ToKpa( double nPerM2 ) {
		return nPerM2 * 0.001f;
	}

	//! Converte de Newton / metro� para atm
	double nPerM2ToAtm( double nPerM2 ) {
		return nPerM2 * 0.000009869232667f;
	}

	//! Converte de Newton / metro� para Kgf / cm�
	double nPerM2ToKgfPerCm2( double nPerM2 ) {
		return nPerM2 * 0.00001019716213f;
	}

	//! Converte de Newton / metro� para psi
	double nPerM2ToPsi( double nPerM2 ) {
		return nPerM2 * 0.000145037743897f;
	}

	//! Converte de Newton / metro� para Libra / p�
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

	//! Converte de atm para Newton / metro�
	double atmToNPerM2( double atm ) {
		return atm * 101325.0f;
	}

	//! Converte de atm para Kgf / cm�
	double atmToKgfPerCm2( double atm ) {
		return atm * 1.033227452799f;
	}

	//! Converte de atm para psi
	double atmToPsi( double atm ) {
		return atm * 14.695949400392212f;
	}

	//! Converte de atm para Libra / p�
	double atmToLbfPerFt2( double atm ) {
		return atm * 2116.2165786f;
	}

	//! Converte de Kgf / cm� para Pascal
	double kgfPerCm2ToPa( double kgfPerCm2 ) {
		return kgfPerCm2 * 98066.5f;
	}

	//! Converte de Kgf / cm� para quilo-Pascal
	double kgfPerCm2ToKpa( double kgfPerCm2 ) {
		return kgfPerCm2 * 98.0665f;
	}

	//! Converte de Kgf / cm� para Newton / metro�
	double kgfPerCm2ToNPerM2( double kgfPerCm2 ) {
		return kgfPerCm2 * 98066.5f;
	}

	//! Converte de Kgf / cm� para atm
	double kgfPerCm2ToAtm( double kgfPerCm2 ) {
		return kgfPerCm2 * 0.967841105354059f;
	}

	//! Converte de Kgf / cm� para psi
	double kgfPerCm2ToPsi( double kgfPerCm2 ) {
		return kgfPerCm2 * 14.223343911902914f;
	}

	//! Converte de Kgf / cm� para Libra / p�
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

	//! Converte de psi para Newton / metro�
	double psiaToNPerM2( double psi ) {
		return psi * 6894.757f;
	}

	//! Converte de psi para atm
	double psiaToAtm( double psi ) {
		return psi * 0.068045961016531f;
	}

	//! Converte de psi para kgf / cm�
	double psiaToKgfPerCm2( double psi ) {
		return psi * 0.070306954974431f;
	}

	//! Converte de psi para Libra / p�
	double psiaToLbfPerFt2( double psi ) {
		return psi * 144.0f;
	}

	//! Converte de psig para Pascal
	double psigToPa( double psi ) {
		return psi * 6.8947573e+3f + 101.32500e+3 ;
	}

	//! Converte de Libra / pe� para Pascal
	double lbfPerFt2ToPa( double lbfPerFt2 ) {
		return lbfPerFt2 * 47.88025694444f;
	}

	//! Converte de Libra / pe� para quilo-Pascal
	double lbfPerFt2ToKpa( double lbfPerFt2 ) {
		return lbfPerFt2 * 0.047880256944444f;
	}

	//! Converte de Libra / pe� para Newton / metro�
	double lbfPerFt2ToNPerM2( double lbfPerFt2 ) {
		return lbfPerFt2 * 47.88025694444f;
	}

	//! Converte de Libra / pe� para atm
	double lbfPerFt2ToAtm( double lbfPerFt2 ) {
		return lbfPerFt2 * 0.000472541395948f;
	}

	//! Converte de Libra / pe� para kgf / cm�
	double lbfPerFt2ToKgfPerCm2( double lbfPerFt2 ) {
		return lbfPerFt2 * 0.000488242742878f;
	}

	//! Converte de Libra / pe� para psi
	double lbfPerFt2ToPsi( double lbfPerFt2 ) {
		return lbfPerFt2 * 0.006944444444444f;
	}


	/*****************************
	 * Convers�es de temperatura
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
	 * Convers�es de tempo
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
	 * Convers�es de torque
	 ***********************/

	//! Converte de Newton*metro para kgf*metro
	double nMToKgfM( double nM ) {
	    return nM * 0.1019716213f;
	}

	//! Converte de Newton*metro para kgf*cm
	double nMToKgfCm( double nM ) {
	    return nM * 10.19716213f;
	}

	//! Converte de Newton*metro para lbf*p�
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

	//! Converte de kgf*metro para lbg*p�
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

	//! Converte de kgf*cm para lbf*p�
	double kgfCmToLbfFt( double kgfCm ) {
	    return kgfCm * 0.072330135759f;
	}

	//! Converte de kgf*cm para lbf*polegada
	double kgfCmToLbfInch( double kgfCm ) {
	    return kgfCm * 0.867961885136f;
	}

	//! Converte de lbf*p� para N*m
	double lbfFtToNM( double lbfFt ) {
	    return lbfFt * 1.355818f;
	}

	//! Converte de lbf*p� para N*m
	double lbfFtToKgfM( double lbfFt ) {
	    return lbfFt * 0.1382549596f;
	}

	//! Converte de lbf*p� para N*m
	double lbfFtToKgfCm( double lbfFt ) {
	    return lbfFt * 13.8254959f;
	}

	//! Converte de lbf*p� para N*m
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
	 * Convers�es de vaz�o
	 ***********************/

	//! Converte de m� / s para m� / dia
	double m3PerSecondToM3PerDay( double m3PerSecond ) {
		float aux = ((int)( m3PerSecond*10000))/10000;
		return aux * 86400.0;
	}

	//! Converte de m� / s para m� / hora
	double m3PerSecondToM3PerHour( double m3PerSecond ) {
		return m3PerSecond * 3600.0f;
	}

	//! COnverte de m� / h para m� / s
	double m3PerHourToM3PerSecond( double m3PerHour ) {
		return m3PerHour * 0.0002777778f;
	}

	//! Converte de m� / h para m� / dia
	double m3PerHourToM3PerDay( double m3PerHour ) {
		return m3PerHour * 24.0f;
	}

	//! Converte de m� / dia para m� / s
	double m3PerDayToM3PerSecond( double m3PerDay ) {
		return m3PerDay / 86400.0f;
	}

	//! Converte de m� / dia para m� / h
	double m3PerDayToM3PerHour( double m3PerDay ) {
		return m3PerDay / 24.0f;
	}


	/***************************
	 * Convers�es de velocidade
	 ***************************/

	//! Converte de m / s para P� / min
	double mPerSecondToFtPerMinute( double mPerSecond ) {
		return mPerSecond * 196.85039370078738f;
	}

	//! Converte de m / s para km / h
	double mPerSecondToKmPerHour( double mPerSecond ) {
		return mPerSecond * 3.6f;
	}

	//! Converte de P� / min para m / s
	double ftPerMinuteToMPerSecond( double ftPerMinute ) {
		return ftPerMinute * 0.00508f;
	}

	//! Converte de P� / min para km / h
	double ftPerMinuteToKmPerHour( double ftPerMinute ) {
		return ftPerMinute * 0.018288f;
	}

	//! Converte de km / h para m / s
	double kmPerHourToMPerSecond( double kmPerHour ) {
		return kmPerHour * 0.277777777777778f;
	}

	//! Converte de km / h para P� / min
	double kmPerHourToFtPerMinute( double kmPerHour ) {
		return kmPerHour * 54.680664916885235f;
	}


	/*************************
	 * Convers�es espec�ficas
	 *************************/

	//! Converte de api para Sg
	double apiToSg( double api ) {
		return 141.5f / ( api + 131.5f ) ;
	}

}
