package algorithm;

import java.io.PrintWriter;

public class SimulationVariables {
	
	// Variables
	double Ntotal;        /*!< Numero de moles total do gas             */
	double PtbgB;         /*!< Pressao na base do tubing                */
	double save_Hplg;     /*!< Guarda a posicao da base do pistao       */
	double CP;            /*!< Controle de press�o (press�o cr�tica)    */
	double LslgX;         /*!< Armazena o Comprimento da glofada (m)    */
	float velocity; 	  /*!< Velocidade desconhecida????              */
	double H;             /*!< Profundidade do topo da golfada          */
	double delta_h;       /*!< Variacao de posicao do pistao            */
	double delta_v;       /*!< Variacao de velocidade do pistao         */
	int temp_Offtime;     /*!< Auxiliar para o tempo de Offtime do po�o */
	double temp;          /*!< Variavel temporaria de m�ltiplos prop�sitos    */
	double templ;         /*!< Dist�ncia entre a base do pist�o e a superf�cie*/
	double Ppart_csg;     /*!< Armazena Press�o no topo do anular             */
	double Ppart_tbg;     /*!< Armazena Press�o no topo do tubing             */
	double V;             /*!< Volume do Tubing nao ocupada pela golfada      */
	double delta_V;       /*!< Diferen�a de volume no tubing                  */
	double PPcsg;         /*!< Pressao media no anular                        */
	double N;             /*!< Numero de moles - diferentes sentidos          */
	double PP;            /*!< Armazena valor de pressao do tubing            */
	double TT;            /*!< Temperatura m�dia do Tubing                    */
	double z;             /*!< Fator de compressibilidade do g�s              */
	double B;             /*!< Base ou montante                               */
	double Transient;     /*!< Tempo do som no gas do tubing ate a golfada (s)*/
	double n;             /*!< Numero de moles do gas                 */
	double q;             /*!< Vaz�o de g�s da linha de surg�ncia     */
	double qq;            /*!< Vazao de gas media                     */
	double qqq;           /*!< Guarda o valor de vazao de gas         */
	double nn;            /*!< Numero de moles m�dio do gas           */
	double ppp;           /*!< Guarda o valor da pressao media        */
	int y;               /*!< Serve de contador                      */
	double Pt;            /*!< Armazena press�es no topo              */
	double fatorT;        /*!< Diferenca de pressao do topo da coluna-separador*/
	double fatorB;        /*!< Diferenca da base da coluna-coluna de gas       */
	float APIapar;        /*!< N�o utilizado                                   */
	float I;              /*!< Fator I                                    */
	double Pfric;         /*!< Perdas por friccao                         */
	int j;               /*!< Contador do la�o da etapa de Producao      */
	int cont3;						/*!< Contador para resolver o problema da subida do pist�o*/
	int k;               /*!< Contador do la�o da etapa de Afterflow     */
	int m;               /*!< Contador do la�o da etapa de Build-up      */
	double LtbgY;         /*!< Comprimento da coluna de fundo             */
	double LtbgX;         /*!< Comprimento da coluna de fundo (interface) */
	double LtbgZ;         /*!< Comprimento da futura golfada              */
	int flag;             /*!< Identificador do movimento do pistao
							-Se = 0 o pistao est� em movimenta��o ascendente
							-Se = 1 o pistao chegou a superf�cie
							-Se = 2 o pistao nao chegou a superf�cie  */
	int d;               /*!< Contador auxiliar                          */
	int i;               /*!< Contador do la�o para etapa Subida Pistao  */
	int o;               /*!< Contador auxiliar                          */
	int u;                /*!< Contadores para iteracoes for              */
	double Ptt;           /*!< Pressao no Tubing na superficie            */
	double PplgJ;  	      /*!< Pressao do pistao a jusante                */
	double PplgM;         /*!< Pressao do pistao a montante               */
	double save_PplgM;    /*!< Salvar a pressao do pistao a montante      */
	double Visc;          /*!< Viscosidade Din�mica (Pa*s)                */
	double Rey;           /*!< Constante Universal de Reynolds(N*m/mol*K) */
	double Fric;          /*!< Fator de friccao                           */
	double Pwf;           /*!< Pressao de fluxo no fundo do poco          */
	double Vt;            /*!< Volume do gas abaixo do pistao             */
	double TTt;           /*!< Temperatura m�dia entre partes do po�o     */
	double Nt;            /*!< Numero de moles do gas no anular           */
	double v0;            /*!< Velocidade do pist�o                       */
	double save_v0;       /*!< Guarda a velocidade da golfada             */
	double Na;            /*!< Numero de moles do gas no anular           */
	double save_Na;       /*!< Armazena n� moles no anulas                */
	double save_PPt;      /*!< Guarda o valor da pressao media abaixo do pistao */
	double save_PPcsg;    /*!< Armazena pressao media no anular                 */
	double p_;            /*!< Guarda o valor de pressoes                       */
	double PPt;           /*!< Pressao media abaixo do pistao                   */
	double Pbt;           /*!< Pressao media na base da coluna de gas abaixo do pistao */
	double F_;            /*!< Diferenca entre a pressao na base do anular e do tubing */
	double Pba;           /*!< Pressao na base do espaco anular               */
	double Nt_;           /*!< Armazena o numero de moles do gas no tubing    */
	double Ntt;           /*!< Guarda o valor do numero de moles (do tubing)  */
	double delta_P;       /*!< Diferen�a de Press�o                           */
	public int piston_arrival;   /*!< Indicador de chegada do pist�o � superf�cie    */
	double count;         /*!< Contador de ciclos          */
	int save_Offtime;     /*!< Armazena tempo de Offtime   */
	int save_Afterflow;   /*!< Armazena tempo de Afterflow */
	int contador;         /*!< Contador                    */
	int cont;             /*!< Contador                    */
	float production;     /*!< Calcula a vaz�o produzida               */
	float total_production; /*!< Calcula o acumulo de produ��o              */
	int limite;           /*!<                             */
	double Cav;           /*!< Coeficiente de amortecimento viscoso (Kg/s^2) */

	private static SimulationVariables instance;
	
	public static synchronized SimulationVariables getInstance() {
		if (instance == null) {
			instance = new SimulationVariables();
		}
		return instance;
	}	
	
	/**
	 * Constructor
	 */
	private SimulationVariables() {
		// TODO Empty
	}
	
	public void init() {
		this.Ntotal    = 0;
		this.PtbgB     = 0;
		this.save_Hplg = 0;
		this.CP        = 0;
		this.LslgX     = 0;
		this.velocity  = 0;
		this.H         = 0;
		this.delta_h   = 0;
		this.delta_v   = 0;
		this.temp      = 0;
		this.templ     = 0;
		this.Ppart_csg = 0;
		this.Ppart_tbg = 0;
		this.V         = 0;
		this.delta_V   = 0;
		this.PPcsg     = 0;
		this.N         = 0;
		this.PP        = 0;
		this.TT        = 0;
		this.z         = 0;
		this.B         = 0;
		this.Transient = 0;
		this.n         = 0;
		this.qq        = 0;
		this.qqq       = 0;
		this.nn        = 0;
		this.ppp       = 0;
		this.y         = 0;
		this.Pt        = 0;
		this.fatorT    = 0;
		this.fatorB    = 0;
		this.APIapar   = 0;
		this.I         = 0;
		this.Pfric     = 0;
		this.j         = 0;
	    this.cont3     = 0;
		this.k         = 0;
		this.m         = 0;
		this.LtbgY     = 0;
		this.LtbgZ     = 0;
		this.flag      = 0;
		this.d         = 0;
		this.i         = 0;
		this.o         = 0;
		this.u         = 0;
		this.Ptt       = 0;
		this.PplgJ     = 0;
		this.PplgM     = 0;
		this.Visc      = 0;
		this.Rey       = 0;
		this.Fric      = 0;
		this.Pwf       = 0;
		this.Vt        = 0;
		this.TTt       = 0;
		this.Nt        = 0;
		this.v0        = 0;
		this.save_v0   = 0;
		this.Na        = 0;
		this.save_Na   = 0;
		this.save_PPt  = 0;
		this.p_        = 0;
		this.PPt       = 0;
		this.Pbt       = 0;
		this.F_        = 0;
		this.Pba       = 0;
		this.Nt_       = 0;
		this.Ntt       = 0;
		this.delta_P   = 0;
		this.count     = 0;
		this.contador  = 0;
		this.cont      = 0;
		this.limite    = 0;
		this.LtbgX     = 0;
		this.q         = 0;
		this.temp_Offtime     = 0;
		this.production       = 0;
		this.total_production = 0;
		this.piston_arrival   = 0;
		this.save_Offtime     = 0;
		this.save_Afterflow   = 0;
		this.save_PPcsg       = 0;
		this.save_PplgM       = 0;
	}
	
	public void imprimirVariaveis(PrintWriter writer) {
		writer.println("Auxiliary Variables Data:");
	    writer.println("Ntotal = " + this.Ntotal);
	    writer.println("PtbgB = " + this.PtbgB);
	    writer.println("save_Hplg = " + this.save_Hplg);
	    writer.println("CP = " + this.CP);
	    writer.println("LslgX = " + this.LslgX);
	    writer.println("velocity = " + this.velocity);
	    writer.println("H = " + this.H);
	    writer.println("delta_h = " + this.delta_h);
	    writer.println("delta_v = " + this.delta_v);
	    writer.println("temp_Offtime = " + this.temp_Offtime);
	    writer.println("temp = " + this.temp);
	    writer.println("templ = " + this.templ);
	    writer.println("Ppart_csg = " + this.Ppart_csg);
	    writer.println("Ppart_tbg = " + this.Ppart_tbg);
	    writer.println("V = " + this.V);
	    writer.println("delta_V = " + this.delta_V);
	    writer.println("PPcsg = " + this.PPcsg);
	    writer.println("N = " + this.N);
	    writer.println("PP = " + this.PP);
	    writer.println("TT = " + this.TT);
	    writer.println("z = " + this.z);
	    writer.println("B = " + this.B);
	    writer.println("transient = " + this.Transient);
	    writer.println("n = " + this.n);
	    writer.println("q = " + this.q);
	    writer.println("qq = " + this.qq);
	    writer.println("qqq = " + this.qqq);
	    writer.println("nn = " + this.nn);
	    writer.println("ppp = " + this.ppp);
	    writer.println("y = " + this.y);
	    writer.println("Pt = " + this.Pt);
	    writer.println("fatorT = " + this.fatorT);
	    writer.println("fatorB = " + this.fatorB);
	    writer.println("APIapar = " + this.APIapar);
	    writer.println("I = " + this.I);
	    writer.println("Pfric = " + this.Pfric);
	    writer.println("j = " + this.j);
	    writer.println("cont3 = " + this.cont3);
	    writer.println("k = " + this.k);
	    writer.println("m = " + this.m);
	    writer.println("LtbgY = " + this.LtbgY);
	    writer.println("LtbgX = " + this.LtbgX);
	    writer.println("LtbgZ = " + this.LtbgZ);
	    writer.println("flag = " + this.flag);
	    writer.println("d = " + this.d);
	    writer.println("i = " + this.i);
	    writer.println("o = " + this.o);
	    writer.println("u = " + this.u);
	    writer.println("Ptt = " + this.Ptt);
	    writer.println("PplgJ = " + this.PplgJ);
	    writer.println("PplgM = " + this.PplgM);
	    writer.println("save_PplgM = " + this.save_PplgM);
	    writer.println("Visc = " + this.Visc);
	    writer.println("Rey = " + this.Rey);
	    writer.println("Fric = " + this.Fric);
	    writer.println("Pwf = " + this.Pwf);
	    writer.println("Vt = " + this.Vt);
	    writer.println("TTt = " + this.TTt);
	    writer.println("Nt = " + this.Nt);
	    writer.println("v0 = " + this.v0);
	    writer.println("save_v0 = " + this.save_v0);
	    writer.println("Na = " + this.Na);
	    writer.println("save_Na = " + this.save_Na);
	    writer.println("save_PPt = " + this.save_PPt);
	    writer.println("save_PPcsg = " + this.save_PPcsg);
	    writer.println("p_ = " + this.p_);
	    writer.println("PPt = " + this.PPt);
	    writer.println("Pbt = " + this.Pbt);
	    writer.println("F_ = " + this.F_);
	    writer.println("Pba = " + this.Pba);
	    writer.println("Nt_ = " + this.Nt_);
	    writer.println("Ntt = " + this.Ntt);
	    writer.println("delta_P = " + this.delta_P);
	    writer.println("piston_arrival = " + this.piston_arrival);
	    writer.println("count = " + this.count);
	    writer.println("save_Offtime = " + this.save_Offtime);
	    writer.println("save_Afterflow = " + this.save_Afterflow);
	    writer.println("contador = " + this.contador);
	    writer.println("cont = " + this.cont);
	    writer.println("production = " + this.production);
	    writer.println("total_production = " + this.total_production);
	    writer.println("limite = " + this.limite);
	    writer.println("Cav = " + this.Cav);
	    writer.println("--------------------------------");
	}

}
