package algorithm;

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
	long y;               /*!< Serve de contador                      */
	double Pt;            /*!< Armazena press�es no topo              */
	double fatorT;        /*!< Diferenca de pressao do topo da coluna-separador*/
	double fatorB;        /*!< Diferenca da base da coluna-coluna de gas       */
	float APIapar;        /*!< N�o utilizado                                   */
	float I;              /*!< Fator I                                    */
	double Pfric;         /*!< Perdas por friccao                         */
	long j;               /*!< Contador do la�o da etapa de Producao      */
	long cont3;						/*!< Contador para resolver o problema da subida do pist�o*/
	long k;               /*!< Contador do la�o da etapa de Afterflow     */
	long m;               /*!< Contador do la�o da etapa de Build-up      */
	double LtbgY;         /*!< Comprimento da coluna de fundo             */
	double LtbgX;         /*!< Comprimento da coluna de fundo (interface) */
	double LtbgZ;         /*!< Comprimento da futura golfada              */
	int flag;             /*!< Identificador do movimento do pistao
							-Se = 0 o pistao est� em movimenta��o ascendente
							-Se = 1 o pistao chegou a superf�cie
							-Se = 2 o pistao nao chegou a superf�cie  */
	long d;               /*!< Contador auxiliar                          */
	long i;               /*!< Contador do la�o para etapa Subida Pistao  */
	long o;               /*!< Contador auxiliar                          */
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
	int piston_arrival;   /*!< Indicador de chegada do pist�o � superf�cie    */
	double count;         /*!< Contador de ciclos          */
	int save_Offtime;     /*!< Armazena tempo de Offtime   */
	int save_Afterflow;   /*!< Armazena tempo de Afterflow */
	int contador;         /*!< Contador                    */
	int cont;             /*!< Contador                    */
	float production;     /*!< Calcula a vaz�o produzida               */
	float total_production; /*!< Calcula o acumulo de produ��o              */
	int limite;           /*!<                             */
	double Cav;           /*!< Coeficiente de amortecimento viscoso (Kg/s^2) */

	/**
	 * Constructor
	 */
	public SimulationVariables() {
		// TODO Empty
	}

}
