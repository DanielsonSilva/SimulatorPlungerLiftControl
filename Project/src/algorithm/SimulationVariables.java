package algorithm;

public class SimulationVariables {
	
	// Variables
	double Ntotal;        /*!< Numero de moles total do gas             */
	double PtbgB;         /*!< Pressao na base do tubing                */
	double save_Hplg;     /*!< Guarda a posicao da base do pistao       */
	double CP;            /*!< Controle de pressão (pressão crítica)    */
	double LslgX;         /*!< Armazena o Comprimento da glofada (m)    */
	float velocity; 	  /*!< Velocidade desconhecida????              */
	double H;             /*!< Profundidade do topo da golfada          */
	double delta_h;       /*!< Variacao de posicao do pistao            */
	double delta_v;       /*!< Variacao de velocidade do pistao         */
	int temp_Offtime;     /*!< Auxiliar para o tempo de Offtime do poço */
	double temp;          /*!< Variavel temporaria de múltiplos propósitos    */
	double templ;         /*!< Distância entre a base do pistão e a superfície*/
	double Ppart_csg;     /*!< Armazena Pressão no topo do anular             */
	double Ppart_tbg;     /*!< Armazena Pressão no topo do tubing             */
	double V;             /*!< Volume do Tubing nao ocupada pela golfada      */
	double delta_V;       /*!< Diferença de volume no tubing                  */
	double PPcsg;         /*!< Pressao media no anular                        */
	double N;             /*!< Numero de moles - diferentes sentidos          */
	double PP;            /*!< Armazena valor de pressao do tubing            */
	double TT;            /*!< Temperatura média do Tubing                    */
	double z;             /*!< Fator de compressibilidade do gás              */
	double B;             /*!< Base ou montante                               */
	double Transient;     /*!< Tempo do som no gas do tubing ate a golfada (s)*/
	double n;             /*!< Numero de moles do gas                 */
	double q;             /*!< Vazão de gás da linha de surgência     */
	double qq;            /*!< Vazao de gas media                     */
	double qqq;           /*!< Guarda o valor de vazao de gas         */
	double nn;            /*!< Numero de moles médio do gas           */
	double ppp;           /*!< Guarda o valor da pressao media        */
	long y;               /*!< Serve de contador                      */
	double Pt;            /*!< Armazena pressões no topo              */
	double fatorT;        /*!< Diferenca de pressao do topo da coluna-separador*/
	double fatorB;        /*!< Diferenca da base da coluna-coluna de gas       */
	float APIapar;        /*!< Não utilizado                                   */
	float I;              /*!< Fator I                                    */
	double Pfric;         /*!< Perdas por friccao                         */
	long j;               /*!< Contador do laço da etapa de Producao      */
	long cont3;						/*!< Contador para resolver o problema da subida do pistão*/
	long k;               /*!< Contador do laço da etapa de Afterflow     */
	long m;               /*!< Contador do laço da etapa de Build-up      */
	double LtbgY;         /*!< Comprimento da coluna de fundo             */
	double LtbgX;         /*!< Comprimento da coluna de fundo (interface) */
	double LtbgZ;         /*!< Comprimento da futura golfada              */
	int flag;             /*!< Identificador do movimento do pistao
							-Se = 0 o pistao está em movimentação ascendente
							-Se = 1 o pistao chegou a superfície
							-Se = 2 o pistao nao chegou a superfície  */
	long d;               /*!< Contador auxiliar                          */
	long i;               /*!< Contador do laço para etapa Subida Pistao  */
	long o;               /*!< Contador auxiliar                          */
	int u;                /*!< Contadores para iteracoes for              */
	double Ptt;           /*!< Pressao no Tubing na superficie            */
	double PplgJ;  	      /*!< Pressao do pistao a jusante                */
	double PplgM;         /*!< Pressao do pistao a montante               */
	double save_PplgM;    /*!< Salvar a pressao do pistao a montante      */
	double Visc;          /*!< Viscosidade Dinâmica (Pa*s)                */
	double Rey;           /*!< Constante Universal de Reynolds(N*m/mol*K) */
	double Fric;          /*!< Fator de friccao                           */
	double Pwf;           /*!< Pressao de fluxo no fundo do poco          */
	double Vt;            /*!< Volume do gas abaixo do pistao             */
	double TTt;           /*!< Temperatura média entre partes do poço     */
	double Nt;            /*!< Numero de moles do gas no anular           */
	double v0;            /*!< Velocidade do pistão                       */
	double save_v0;       /*!< Guarda a velocidade da golfada             */
	double Na;            /*!< Numero de moles do gas no anular           */
	double save_Na;       /*!< Armazena nº moles no anulas                */
	double save_PPt;      /*!< Guarda o valor da pressao media abaixo do pistao */
	double save_PPcsg;    /*!< Armazena pressao media no anular                 */
	double p_;            /*!< Guarda o valor de pressoes                       */
	double PPt;           /*!< Pressao media abaixo do pistao                   */
	double Pbt;           /*!< Pressao media na base da coluna de gas abaixo do pistao */
	double F_;            /*!< Diferenca entre a pressao na base do anular e do tubing */
	double Pba;           /*!< Pressao na base do espaco anular               */
	double Nt_;           /*!< Armazena o numero de moles do gas no tubing    */
	double Ntt;           /*!< Guarda o valor do numero de moles (do tubing)  */
	double delta_P;       /*!< Diferença de Pressão                           */
	int piston_arrival;   /*!< Indicador de chegada do pistão à superfície    */
	double count;         /*!< Contador de ciclos          */
	int save_Offtime;     /*!< Armazena tempo de Offtime   */
	int save_Afterflow;   /*!< Armazena tempo de Afterflow */
	int contador;         /*!< Contador                    */
	int cont;             /*!< Contador                    */
	float production;     /*!< Calcula a vazão produzida               */
	float total_production; /*!< Calcula o acumulo de produção              */
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

}
