/**
 * 
 */
package algorithm;

import static java.lang.Math.*;
/**
 * @author Danielson Flávio Xavier da Silva
 * Utilitary equations used in Simulation.java
 */
public class UtilEquations {
	
	public UtilEquations () {
		// Empty
	}
	
	/**
	 * @brief 
	 * @param p1 Pressao montante abFS em (kPa).
	 * @param p2 Pressao jusante abFs em (kPa).
	 * @param d Diametro de passagem (mm).
	 * @param T1 Temperatura montante abs (K).
	 * @return
	 */
	public double QSC(double p1,double p2,double d,double T1) {
		Entities f = Entities.getInstance();
		
		double A,B;
		double temp;
		double Z1;	/* Z1 fator de compressibilidade a T1 e p1 */
		double k;   /* Cp/Cv   Adimensional */
		double Rpc; /* Pressure ratio at which gas flow becomes critical */
		double Cn;  /* Metric. Coefficient based on system of units, discharge */
								/* coefficient and standard conditions. */
		Z1 = Z(p1/ DataConstants.getInstance().Ppc,T1/ DataConstants.getInstance().Tpc);
		k = f.fluido.GAMA;
		Rpc = pow(2.0/(k+1),k/(k-1));
		Cn = 3.7915;

		A = (Cn*p1*pow(d,2))/sqrt(f.fluido.SGgas*T1*Z1);
		if ( p2/p1 < Rpc )
			B = Rpc; /* fluxo critico */
		else
			B=p2/p1; /* sub-critico   */

		temp = (k/(k-1)) * (pow(B,2/k) - pow(B,(k+1)/k));
		if ( temp < 0.0 ) {
			return 0.0;
		}
		return A*sqrt(temp);
	}

	/**
	 * Funcao para calcular a viscosidade dinamica do gas pela correlacao
	 *	de Lee et. al. (Transctions AIME, 1966, pg.997).
	 * @param T temperatura
	 * @param RO densidade do gás
	 * @return
	 */
	public double VISGAS(double T, double RO) {
		 double Trank, PMg, aK, X, ROgcm3, ay;
		 Trank = 1.8 * T;
		 PMg = DataConstants.getInstance().PM * 1000.0;
		 ROgcm3 = RO / 1000.0;
		 aK = ((9.4+0.02*PMg) * pow(Trank,1.5))/(209.0+19.0*PMg + Trank);
		 X  = 3.5 + 986.0/Trank + 0.01*PMg;
		 ay = 2.4 - 0.2*X;
		 return pow(10,-7) * aK * exp(pow(X*ROgcm3, ay));
	}

	/**
	 * Calcula a viscosidade dinâmica de uma mistura óleo/água.
	 *  É considerada a viscosidade do óleo morto pela correlação de Beal e
	 *  a viscosidade da água pela correlação de Van Wingen. Não é considerado
	 *  gás em solução.
	 * @param T Temperatura
	 * @return Viscosidade dinâmica da mistura em uma dada temperatura.
	 */
	public double VISC (double T) {
		double TF, APi, VISCw, a, b, c, VISCo;

		TF = (T-273.15)*9.0/5 + 32.0;
		VISCw = exp(1.003-1.479*pow(10,-2)*TF + 1.982*pow(10,-5)*pow(TF,2));
		APi = 141.5/ DataConstants.getInstance().SGoleo - 131.5;
		a = 0.32 + (1.8*pow(10,7.0f))/pow(APi,4.53);
		b = 360.0/(TF + 200.0);
		c = pow(10,(0.43 + 8.33/APi));
		VISCo = a * pow(b, c);
		return (VISCo*(1.0 - DataConstants.getInstance().FW) + VISCw * DataConstants.getInstance().FW ) * 0.001;
	}

	/**
	 * Calcula a temperatura devido ao gradiente geotérmico.
	 * @param H (m)
	 * @return T em °K
	*/
	public double TEMP(double H) {
		double Tsup,GRADT;

		Tsup = 299.8167;
		GRADT = 0.03098571;
		return  Tsup + GRADT*H;
	}
	
	/**
	 * Calcula o fator de Fricção de Darcy-Weisbach por Colebrook.
	 * @param REY Número de Reynolds.
	 * @param E  (mm)
	 * @param DH  (mm)
	 * @return
	 */
	public double FRIC(double REY,double E,double DH) {
		double ED;
		double A,B,X,Y,YPRIM;
		ED = E/DH;
		//CALCULO PARA O REGIME LAMINAR
		if ( REY <= 2100.0 )
			return 64.0 / REY;
		//CALCULO PARA O REGIME TURBULENTO
		else
			if ( ED > 0.05 )
				return (1.0/pow((4.0*log10(0.27*ED)),2) + 0.67*pow(ED,1.73))*4.0;
			else {
				A = ED / 3.7;
				B = 2.51 / REY;
				X = (-2.0) * log10(A + pow(10,-12));
				//Tentativa de substituir GOTO
				//Loop:
				Y = X + 2.0*log10(A + B*X);
				while (!(abs(Y) <= pow(10,-6))) {
					YPRIM = 1.0 + (0.43429448*2.0*B) / (A + B*X);
					X = X - Y / YPRIM;
					Y = X + 2.0*log10(A + B*X);
				}
				return 1.0 / pow(X,2);
				//goto Loop;
			}
	}
	
	/**
	 * Calcula o fator de compressibilidade Z pelo método de Dranchuk,
	 *	Purvis e Robinson.
	 * @param Pr
	 * @param Tr
	 * @return
	 */
	public double Z(double Pr,double Tr) {
		double Tr3, Z, RR, RR2, RR4, RR5, A, B, C, D, A8, AUX, FZ, DFDZ, DELTAZ;
		Tr3 = pow(Tr,3);
		Z = 1.0;
		//return 0.9;
		for ( int K = 1; K < 51; K++ ) {
			RR = (0.27 * Pr) / (Z * Tr);
			RR2 = pow(RR,2);
			RR4 = pow(RR,4);
			RR5 = pow(RR,5);
			A = 0.31506237 - 1.0467099 / Tr - 0.57832729 / Tr3;
			B = 0.53530771 - 0.61232032 / Tr;
			C = 0.61232032 * 0.10488813 /Tr;
			D = 0.68157001 / Tr3;
			A8 = 0.68446549;
			AUX = (1+A8*RR2)*exp(-A8*RR2);
			FZ = 1 + A*RR + B*RR2 + C*RR5 + D*RR2*AUX - Z;
			DFDZ = (-A)*RR/Z -2*B*RR2/Z - 5*C*RR5/Z - 2*D*(RR2/Z)*AUX - 2*D*(RR4/Z)*A8*exp(-A8*RR2) + D*(RR4/Z)*AUX*2*A8-1;
			DELTAZ = FZ / DFDZ;
			if( !(abs(DELTAZ) > 0.001) ) {
				return Z;
			}
			else {
				Z = Z - DELTAZ;
			}
		}
		return Z;
	}
	
	/**
	 * Calcula a pressão na base de uma coluna de gás de comprimento h,
	 *	levando em conta a hidrostática.
	 * @param P1 Pressão acima da variavel do PONTO2.
	 * @param T1
	 * @param T2
	 * @param H
	 * @return
	 */
	public double GASOSTB (double P1,double T1,double T2,double H) {
		double TT, P2, FP2, dP, dFP2dP2, DELTA;
		DataConstants c = DataConstants.getInstance();

		TT = (T1+T2)/2.0;
		P2 = P1 * exp(c.PM*c.G*H/(c.R*TT));
		//Tentativa de substituir o GOTO
		//Loop:
		for (int k = 1; k <= 50 ; k++ ) {
			FP2 = P2 - P1*EXPOB(P1,P2,TT,H);
			dP = 1.0;
			dFP2dP2 = 1.0 - P1*(EXPOB(P1,P2+dP,TT,H)-EXPOB(P1,P2-dP,TT,H))/(2*dP);
			DELTA = FP2 / dFP2dP2;
			P2 = P2 - DELTA;
			if ( abs(DELTA) <= 1.0 ) {
				return P2;
			}
		}
		return 0.0;
	}
	
	/**
	 * @brief
	 * @param P1
	 * @param P2
	 * @param TT
	 * @param H
	 * @return
	 */
	public double EXPOB(double P1,double P2,double TT,double H) {
		DataConstants c = DataConstants.getInstance();
		double PP;
		PP = (P1+P2)/2.0;
		return exp(c.PM*c.G*H/ (Z(PP/c.Ppc, TT/c.Tpc) * c.R * TT));
	}
	
	/**************************************************************************
			 Calcula a pressao no topo de uma coluna de gas de
			 comprimento h, levando em conta a hidrostatica.(PONTO 1 acima do PONTO2)
	*/
	/**
	 * Calcula a pressao no topo de uma coluna de gas levando em conta a
	 *	hidrostatica.
	 * @param P2 Pressao abaixo do Ponto1.
	 * @param T2
	 * @param T1
	 * @param H
	 * @return
	 */
	public double GASOSTT (double P2,double T2,double T1,double H) {
		double TT, P1, PP, FP1, dP, dFP1dP1, DELTA;
		DataConstants c = DataConstants.getInstance();


		TT= (T1+T2)/2.0;
		P1 = P2 / exp(c.PM*c.G*H/(c.R*TT));
		//Tentativa de substituir o GOTO
		//Loop:
		for (int k = 1; k <= 50; k++) {
			PP = (P1+P2)/2;
			FP1 = P1 - P2/exp(c.PM*c.G*H/(Z(PP/c.Ppc,TT/c.Tpc)*c.R*TT));
			dP = 1.0;
			dFP1dP1 = 1 - P2*(EXPOT(P1+dP,P2,TT,H)-EXPOT(P1-dP,P2,TT,H))/(2*dP);
			DELTA = FP1 / dFP1dP1;
			P1 = P1 - DELTA;
			if ( abs(DELTA) <= 1.0) {
				return P1;
			}
		}
		return 0.0;
	}
	
	/**
	 * @brief
	 * @param P1
	 * @param P2
	 * @param TT
	 * @param H
	 * @return
	 */
	public double EXPOT (double P1,double P2,double TT,double H) {
		DataConstants c = DataConstants.getInstance();
		double PP;
		PP = (P1+P2) / 2.0;
		return exp((-c.PM)*c.G*H/(Z(PP/c.Ppc,TT/c.Tpc)*c.R*TT));
	}
	
	/**************************************************************************
				Calcula a pressao no topo de uma coluna de gas de
				comprimento h, levando em conta a hidrostatica e a friccao,
				entrada: P2, T2, T1, H, V, D(diam hidr.) (PONTO 1 acima do PONTO2)
				saida  : P1
	*/
	/**
	 * Calcula a pressao no topo de uma coluna de gas levando em conta a
	 *	hidrostatica e a friccao.
	 * @param P2 Pressao abaixo do Ponto1
	 * @param T2
	 * @param T1
	 * @param H Comprimento da coluna de gas.
	 * @param VV
	 * @param D
	 * @return
	 */
	public double GASOSTF (double P2, double T2, double T1, double H, double VV,double D) {
		double TT, P1, FP1, dP, dFP1dP1, DELTA;
		DataConstants c = DataConstants.getInstance();

		TT = (T1+T2)/2.0;
		P1 = P2 / exp(c.PM*c.G*H/(c.R*TT));
		//Tentativa de substituir o GOTO
		//Loop:
		for (int k = 1; k <= 50 ; k++ ) {
			FP1 = Gas2f(P1,P2,TT,H,VV,D);
			dP = 1.0;
			dFP1dP1 = (Gas2f(P1+dP,P2,TT,H,VV,D)-Gas2f(P1-dP,P2,TT,H,VV,D))/(2*dP);
			DELTA = FP1 / dFP1dP1;
			P1 = P1 - DELTA;
			if ( abs(DELTA) <= 1.0) {
				return P1;
			}
		}
		return 0.0;
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief
	 * @param P1
	 * @param P2
	 * @param TT
	 * @param H
	 * @param VV
	 * @param D
	 * @return
	 */
	public double Gas2f(double P1, double P2, double TT, double H, double VV, double D) {
		Entities f = Entities.getInstance();
		DataConstants c = DataConstants.getInstance();
		double PP, ZZ, RRO, VVISCg, RReyg, FF;
		PP  = (P1 + P2)/2.0;
		ZZ  = Z(PP/c.Ppc,TT/c.Tpc);
		RRO = PP*c.PM / (ZZ*TT*c.R);
		VVISCg = VISGAS(TT, RRO);
		RReyg  = RRO*abs(VV) * D/VVISCg;
		FF     = FRIC(RReyg, f.tubing.E, D);
		return P1- P2/exp((c.PM*H/(ZZ*c.R*TT))*(c.G+FF*abs(VV)*VV/(2.0*D)));
	}
	//---------------------------------------------------------------------------
	/**
	 * Realiza o controle fuzzy do sistema (ainda nao implementado)
	 * @param difference
	 * @return
	 */
	public double fuzzy_control (float difference) {
		/*** VAZIO **/
		return difference;
	}
}
