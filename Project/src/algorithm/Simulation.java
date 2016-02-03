package algorithm;

import static java.lang.Math.*;

/**
 * Simulation Methods
 * @author Danielson Flávio Xavier da Silva
 *
 */
public class Simulation {
	
	/** Guarda o tempo de simulação */
	public double tempo;
	/** Identifica a simulação (não usada) */
	public int idSimulacao;
	/** Identifica a simulação (não usada) */
	public int idRamoSimulacao;
	/** Guarda o número de pontos descartados para comparar com a amostragem */
	public int quantidadePontos;
	/** Mostra quantos pontos são descartados para se enviar um ponto para
			interface */
	public int periodoAmostragem;
	/** Tamanho da fila de espera de pontos para ser enviado para a interface */
	public int bufferSendPoints;
	/** Mostra se passa ou não pelo controle no algoritmo */
	public boolean byPassController;
	/** força o envio do ponto inicial da etapa */
	public boolean forcarPontosI;
	/** força o envio do ponto final da etapa */
	public boolean forcarPontosF;
	/** Variável que iria para desenho do processo */
	public double M_PI;
	/** Parâmetro que verifica se o pedido de alteração da válvula motora foi
			feito ou não */
	public boolean alterarValvula;
	
	private static Simulation instance;
	
	public static synchronized Simulation getInstance() {
		if (instance == null) {
			instance = new Simulation();
		}
		return instance;
	}	
	
	/**
	 * Constructor
	 * @param pool Caixa de correio para levar ou trazer as mensagens.
	 */
	private Simulation() {
		//Configuracoes iniciais do algoritmo
		tempo             = 0;
		idRamoSimulacao   = 0;
		quantidadePontos  = 0;
		bufferSendPoints  = 6;
		periodoAmostragem = 120;
		byPassController  = true;
		forcarPontosI     = true;
		forcarPontosF     = true;
		alterarValvula	  = false;
		M_PI              = 3.14159265;
	}
		
	/**
	 * Resetar a classe.
	 */
	public void Limpar() {
		//Atividades de resetar a classe
		this.tempo = 0;
		this.quantidadePontos = 0;
	}
	/**
	 * Inicia os parâmetros para poder começar a simulação.
	 */
	public void iniciarSimulacao() {

		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		// Variáveis de controle de passos de integração
		//c->_stepGas2Liq = 0.01;//valor antigo = 0.001
		//c->_stepGas = 0.1;//valor antigo = 0.01
		//c->_stepLiq = 0.1;//valor antigo = 0.005

		v.CP   = 30;   //Controle de Pressão (Pressão crítica)
		c.Fast = 5.5;  //Velocidade do pistao para ser considerada rápida
		c.Slow = 4.5;  //Velocidade do pistao para ser considerada lenta

		//Razão de água no líquido produzido
		c.FW = f.fluido.BSW/100;

		/*
		 * Specific Gravity do óleo retirada a partir da API
		 * a fórmula pode ser obtida em
		 * http://en.wikipedia.org/wiki/API_gravity
		 */
		c.SGoleo = 141.5/(131.5 + f.fluido.APi);

		//Valor da constante de ROliq em Kg/m3
		c.ROliq = ( c.SGoleo*(1-c.FW) + f.fluido.SGagua*c.FW ) * 1000;

		//PM massa molar do gas (kg/mol)
		c.PM = c.PMar * f.fluido.SGgas;

		//Rgn - constante para gas natural (R/mol) ->
		//constante universal dos gases sobre massa molar do gas
		c.Rgn = c.R/c.PM;

		//Area interna do tubing
		c.AItbg = M_PI * pow(f.tubing.DItbg, 2)/4;

		//FORMULAS DE GREEN  PARA Ppc e Tpc (Pressao e temperatura pseudo critica)
		c.Ppc = (677 + 15*f.fluido.SGgas - 37.5*pow(f.fluido.SGgas,2)) * 6.894757*pow(10,3);
		c.Tpc = ((168 + 325*f.fluido.SGgas - 12.5*pow(f.fluido.SGgas,2))-491.67) * 5.0/9.0+273.15;

		//VAZAO MAXIMA DO RESERVATORIO
		c.Qmax = f.reservat.Qteste/(1-.2*f.reservat.Pteste/f.reservat.Pest - .8*pow(f.reservat.Pteste/f.reservat.Pest,2));

		//Casing - Área Interna
		c.AIcsg = 3.14*(pow(f.casing.DIcsg,2)-pow(f.tubing.DOtbg,2))/4;

		//Casing - Volume interno
		c.Vcsg = c.AIcsg * f.tubing.Lcauda;

		//Pressão na base do Anular
		f.varSaida.PcsgB = ue.GASOSTB(f.tempos.PcsgT, c.Tsup,ue.TEMP(f.tubing.Lcauda),f.tubing.Lcauda);

		//Temperatura média do Anular
		c.TTcsg=( ue.TEMP ( f.tubing.Lcauda ) + c.Tsup)/2;

		//Inicializa número de moles do gás
		v.Ntotal = 0;

		//Iniciando Contadores
		v.cont = 0;
		v.count = 0;
		v.contador = 1;

		//Iniciar as variáveis em 0 (zero)
		f.varSaida.Qlres = 0;
		f.varSaida.PtbgT = 0;
		v.PtbgB = 0;
		f.varSaida.Hplg = 0;
		v.v0 = 0;
		f.tempos.Ltbg = 0;

		//Pressao na base do Tubing = Pressao na base do casing -
		//( densidade*gravidade*comprimento da golfada +
		//a massa do pistao pela razão entre a gravidade e a área interna do tubing
		v.PtbgB = f.varSaida.PcsgB - ( c.ROliq * c.G * f.tempos.Lslg + f.pistao.Mplg * c.G/c.AItbg);

		//Pressao no topo do tubing pela equacao GASOSTT
		f.varSaida.PtbgT = ue.GASOSTT(v.PtbgB,ue.TEMP(v.H), c.Tsup, v.H);

		//Passando o valor da entidade para variaveis auxiliares
		v.temp_Offtime = f.tempos.Offtime;

		//Opção de esperar o pistao
		byPassController = !(f.tempos.Controller);

		//O Estagio e setado como BuildUp para que o inicioCiclo() seja iniciado
		c.estagio = c.OFF_BUILD_UP;
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Função que inicia o ciclo ajustando os parâmetros para começar um
	 *				novo ciclo.
	 * @return True se ocorreu a função com sucesso, false caso contrário.
	 */
	public boolean inicioCiclo(){

		//Objetos de manipulacao de variaveis do poco
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		v.Ppart_csg = f.tempos.PcsgT;
		v.Ppart_tbg = f.varSaida.PtbgT;
		v.cont++;
		//ARMAZENAR A VARIAVEL Lslg
		v.LslgX = f.tempos.Lslg;
		//Volume do Tubing
		v.V = c.AItbg * f.tubing.Lcauda;
		//PPcsg - Pressão média no casing
		v.PPcsg = (f.tempos.PcsgT + f.varSaida.PcsgB)/2.0;
		//SE O NUMERO DE MOLES AINDA NAO FOI INICIADO
		if ( v.Ntotal == 0 )
			//INICIALIZA O NUMERO DE MOLES COM O CALCULO
			v.N = v.Ntotal = (v.PPcsg * c.Vcsg)/(ue.Z(v.PPcsg/c.Ppc, c.TTcsg/c.Tpc) * c.R * c.TTcsg);
		else
			//ARMAZENA O NUMERO DE MOLES ATUAL EM N
			v.N = v.Ntotal;
		//Posição (altura) inicial do pistão (m)
		//f.varSaida.Hplg = 0.0;
		//H profundidade do topo do slug (m)
		v.H = f.tubing.Lcauda - f.varSaida.Hplg - f.pistao.Lplg - f.tempos.Lslg;

		// Se o poco afogou acaba funcao
		if( v.PtbgB <= 0 ) {
			return false;
		}

		//PP - pressao media no tubing (Pa)
		v.PP = (f.varSaida.PtbgT + v.PtbgB)/2.0;
		//TT temperatura media (K)
		v.TT = (ue.TEMP(v.H) + c.Tsup)/2.0;
		//Z Fator de compressibilidade médio
		v.z = ue.Z(v.PP/c.Ppc, v.TT/c.Tpc);

		//B velocidade de onda acustica no gas (m/s)
		v.B = sqrt(f.fluido.GAMA * v.z * c.Rgn * v.TT);
		//transient tempo (seg) de som no gas do tubing ate a golfada
		v.Transient = v.H/v.B;

		/* Ate aqui: - Considerou B constante durante o transiente
		 *			- B igual ao valor inicial na condicao estatica
		 */

		//Volume do Tubing
		v.V = c.AItbg * v.H;
		//Massa de gas (moles) no tubing acima de slug (Kg)
		v.n = (v.PP * v.V)/(v.z * c.R * v.TT);
		//Vazao de escape de gas (m3/d)
		v.q = ue.QSC(f.varSaida.PtbgT/1000.0,f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);

		//Estimativa de p media apos transiente
		v.nn = v.n - (((v.q/86400) * v.Transient) * c.Pstd)/(c.R * c.Tstd);

		//Pressão média do gás acima da slug, com z=0.98
		f.varSaida.pp = 0.98 * v.nn * c.R * v.TT/v.V;

		//Atualiza a pressão média e fator de compressibilidade para o
		//gás acima do slug
		do{
			v.PP = f.varSaida.pp;
			v.z = ue.Z(v.PP/c.Ppc, v.TT/c.Tpc);
			f.varSaida.pp = v.z * v.nn * c.R * v.TT/v.V;
		} while ( abs(v.PP - f.varSaida.pp) > 1.0 );

		//Inicializa pressão no topo do tubing descontando a influência do pistão
		f.varSaida.PtbgT = ( f.varSaida.pp * 2 ) / ( 1 + exp( ( c.PM * c.G *
							( f.tubing.Lcauda - f.tempos.Lslg - f.varSaida.Hplg ))/
							( v.z * c.R * v.TT ) ));
		//INICIALIZA O CONTADOR
		v.y=0;
		do {
			v.Pt = f.varSaida.PtbgT;
			v.qq = (v.q + ue.QSC(v.Pt/1000.0,
												 f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup))/2.0;
			v.nn = v.n - ((v.qq/86400 * v.Transient) * c.Pstd)/(c.R * c.Tstd);
			f.varSaida.pp = 0.98 * v.nn * c.R * v.TT/v.V;
			do {
				v.PP = f.varSaida.pp;
				v.z=ue.Z(v.PP/c.Ppc, v.TT/c.Tpc);
				f.varSaida.pp = v.z * v.nn * c.R * v.TT/v.V;
			} while( abs(v.PP - f.varSaida.pp) > 1.0);
			f.varSaida.PtbgT = (f.varSaida.pp * 2)/(1 + exp((c.PM * c.G *
								(f.tubing.Lcauda - f.tempos.Lslg - f.varSaida.Hplg))/
								(v.z * c.R * v.TT)));
		} while( abs(f.varSaida.PtbgT - v.Pt) > 1.0 && v.y++ < 50);
		//CALCULA A DIFERENCA DE PRESSAO NO TOPO DO TUBING E NO SEPARADOR
		v.fatorT = f.varSaida.PtbgT - f.linhaPro.Psep;
		//CALCULA A DIFERENCA DA PRESSAO NA BASE DO TUBING E A COLUNA DE GAS
		v.fatorB = v.PtbgB-ue.GASOSTB(f.linhaPro.Psep, c.Tsup, ue.TEMP(v.H), v.H);

		//Ate aqui calculou o tempo de transiente e queda de pressao,
		//n mois de gas no tubing e p media

		// Estima perda de gas atraves do pistao na subida
		v.I = (float)( pow( f.tubing.DItbg,2 )*pow( f.fluido.APi,0.5 )*1000 / pow( f.tempos.Lslg + 100,0.3 ) )*( 1 - f.pistao.EfVed );

		//INICIA A PERDAS POR FRICCAO
		v.Pfric = 0;
		//INICIA OS CONTADORES
		v.j = v.k = v.m = v.d = 0;
		v.i = 1;
		//INICIA COMPRIMENTO DE GOLFADA
		v.LtbgY = 0;
		//INICIA O COMPRIMENTO DA FUTURA GOLFADA
		v.LtbgZ = 0;
		//INICIA O NIVEL DE LIQUIDO NO FUNDO DA COLUNA
		//f.tempos.Ltbg = 0;
		//INICIA A FLAG
		v.flag = 0;

		//INDICA O ESTAGIO QUE FOI FINALIZADO
		c.estagio = c.INICIO_CICLO;

		return true;
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Modelo matemático da parte de subida do pistão.
	 */
	public void subidaPistao() {
		//CRIACAO DE VARIAVEIS
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		//FORÇAR A PLOTAGEM DO PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		//ALTER: Afirmando que delta h antes da subida é sempre igual a zero
		v.delta_h = 0;
		/* Abertura da Valvula Motora */
		for( v.d = 0, v.i = 1; (v.H - v.delta_h) > 0 &&
				(f.tempos.Ontime - v.i * c.step - v.Transient) > 0 &&
				(!this.alterarValvula); v.i++ ){
			if( (int)(v.i * c.step * 10) % 10 == 0) {
				v.templ = 0.0 /*Lcauda-Hplg*/;
				v.LtbgX = (v.flag == 0 ? f.tempos.Ltbg : v.LtbgZ);
			}
			v.qqq = ue.QSC(f.varSaida.PtbgT/1000.0, f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);
			//ATRIBUI À VARIÁVEL N O Nº MÉDIO DE MOLES DO GÁS ACIMA DA GOLFADA
			v.n = v.nn;
			//SE O Nº DE MOLES MÉDIO(nn) FOR MENOR DO QUE O VALOR DO Nº DE MOLES
			//CALCULADO (COM TEMPERATURA MÉDIA (tt) NA SUPERFÍCIE) O VALOR DE N É
			//ATUALIZADO COM A TEMPERATURA.
			if( v.n < (v.temp = f.linhaPro.Psep * v.V/(v.z * c.R * v.TT)) )
				v.n = v.temp;
			//CONTADOR PARA O DO-WHILE
			v.y = 0;

			//LAÇO DO-WHILE (SE A DIFERENÇA ENTRE AS PRESSÕES NO TUBING NA SUPERFÍCIE DA
			// ITERAÇÃO ATUAL E PASSADA DOR MAIOR QUE 1 E ATINGIR 50 ITERAÇÕES)
			do{
				//PRESSÃO DO TUBING NA SUPERFÍCIE
				v.Ptt = f.varSaida.PtbgT;
				//VAZÃO DE GÁS NA LINHA DE SURGÊNCIA
				v.q = ue.QSC(f.varSaida.PtbgT/1000.0,f.linhaPro.Psep/1000.0,f.valvula.Dab,c.Tsup);
				if(v.q == 0.0) {
					break;
				}
				//VAZÃO MÉDIA NA LINHA DE SURGÊNCIA(O VALOR DE q MUDA A CADA PASSO)
				v.qq = (v.qqq + v.q)/2.0;
				//CÁLCULO DO NÚMERO DE MOLES MÉDIO CALCULADO NA PRESSÃO E TEMPERATURA PADRÕES
				v.nn = v.n - c.step * ((v.qq/86400) * c.Pstd)/(c.R * c.Tstd);
				//SE O Nº DE MOLES MÉDIO CALCULADO ANTERIORMENTE FOR MENOR QUE O Nº DE MOLES DO GÁS ACIMA DA GOLFADA,LEVANDO EM CONTA A PRESSÃO DO SEPARADOR, ENTÃO O Nº DE MOLES RECEBE O VALOR DE TEMP
				if( v.nn < (v.temp = f.linhaPro.Psep * v.V/(v.z * c.R * v.TT)) )
					v.nn=v.temp;
				v.ppp = f.varSaida.pp;
				//RECALCULA PRESSÃO MÉDIA(pp)DO GÁS ACIMA DO TOPO DA GOLFADA USANDO Z=0.98
				f.varSaida.pp = 0.98 * v.nn * c.R * v.TT/v.V;
				//LAÇO DO-WHILE (SE A DIFERENÇA ENTRE OS pp DAS ITERAÇÕES PRESENTE E PASSADA É MAIOR QUE 1)
				do{
					//SALVA A PRESSÃO MÉDIA DO GÁS ACIMA DA GOLFADA NA VARIÁVEL PP
					v.PP = f.varSaida.pp;
					//AJUSTA O FATOR DE COMPRESSIBILIDADE Z
					v.z = ue.Z( v.PP/c.Ppc, v.TT/c.Tpc );
					//RECALCULA pp
					f.varSaida.pp = v.z * v.nn * c.R * v.TT/v.V;
					v.cont3++;
					if (v.cont3>50) {
							v.PP = f.varSaida.pp;
					}
				}while( abs(v.PP - f.varSaida.pp) > 1.0 );
				v.cont3 = 0;
				//CALCULA A PRESSÃO NO TUBING NA SUPERFÍCIE
				f.varSaida.PtbgT = v.Ptt - (v.ppp - f.varSaida.pp) * 2 * v.fatorT/(v.fatorT + v.fatorB);
			}while( abs( v.Ptt - f.varSaida.PtbgT) > 1.0 && v.y++ < 50);
			//CALCULA A PRESSÃO NA BASE DA COLUNA
			v.PtbgB = (f.varSaida.pp * 2)/(1 + exp(- (c.PM * c.G * (f.tubing.Lcauda - f.tempos.Lslg - f.varSaida.Hplg))/(v.z * c.R * v.TT)) );
			//FATOR T:DIFERENÇA ENTRE AS PRESSÕES DO TOPO DA COLUNA E DO SEPARADOR
			v.fatorT = f.varSaida.PtbgT - f.linhaPro.Psep;
			//FATOR B:DIFERENÇA ENTRE AS PRESSÕES NA BASE DA COLUNA E DA BASE DA COLUNA DE GÁS DE COMPRIMENTO H (DO TOPO TUBING AO TOPO DA GOLFADA)LEVANDO EM CONTA A PRESSÃO EM CONTA A PRESSÃO DO SEPARADOR A TEMPERATURA DA SUPERFÍCIE
			v.fatorB = v.PtbgB - ue.GASOSTB(f.linhaPro.Psep, c.Tsup, ue.TEMP(v.H),v.H);

			//SE O PISTÃO NÃO ESTÁ NO FUNDO DO POÇO
			if(f.varSaida.Hplg >= 0){
				//PERDA DE PRESSÃO A JUSANTE: LEVA EM CONTA A PRESSÃO NA BASE DO TUBING,A PERDA POR PESO DA COLUNA DE LÍQUIDO E A PERDA DE´PRESSÃO NO PISTÃO
				v.PplgJ = v.PtbgB + f.tempos.Lslg * c.ROliq * c.G + f.pistao.Mplg * c.G/c.AItbg;
				//ARMAZENA A POSIÇÃO DA BASE DO PISTÃO
				v.save_Hplg = f.varSaida.Hplg;
				//ARMAZENA A VELOCIDADE DA GOLFADA
				v.save_v0 = v.v0;
				//SE FOR A PRIMEIRA ITERACAO
				if( v.i == 1 ){
					//PRESSÃO NO PLUNGER A MONTANTE,ACIMA DA COLUNA DE LÍQUIDO FORMADA NO FUNDO OU NO TOPO DA COLUNA DE GÁS ABAIXO DO PISTÃO
					v.PplgM = ue.GASOSTT(f.varSaida.PcsgB - c.ROliq * c.G * f.tempos.Ltbg, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg), f.varSaida.Hplg - f.tempos.Ltbg);
					//ARMAZENA A PRESSÃO DO PLUNGER A MONTANTE MENOS PERDA POR FRICÇÃO
					v.save_PplgM = v.PplgM -= v.Pfric;
					//CALCULA A VARIAÇÃO DE VELOCIDADE DO PISTÃO
					v.delta_v = c.step *
					(((v.PplgM - v.PplgJ) * c.AItbg)/(c.ROliq * f.tempos.Lslg * c.AItbg + f.pistao.Mplg)- c.G);
					//CALCULA A VARIAÇÃO DE POSIÇÃO DO PISTÃO
					v.delta_h = v.v0 * c.step + c.step * v.delta_v/2;
					//CALCULA A VELOCIDADE DA GOLFADA
					v.v0 = v.save_v0 + v.delta_v;
					//CALCULA A POSIÇÃO DO PISTÃO
					f.varSaida.Hplg = v.save_Hplg + v.delta_h;
					//CALCULA A VISCOSIDADE
					v.Visc   = ue.VISC ( ue.TEMP( f.tubing.Lcauda - f.varSaida.Hplg - f.tempos.Lslg/2 ) );
					//CALCULA O Nº DE REYNOLDS
					v.Rey    = c.ROliq * abs(v.v0) * f.tubing.DItbg/v.Visc;
					//CALCULA O FATOR DE FRICÇÃO
					v.Fric   = ue.FRIC(v.Rey, f.tubing.E, f.tubing.DItbg);
					//CALCULA A PERDA POR FRICÇÃO
					v.Pfric  = c.ROliq * v.Fric * f.tempos.Lslg * abs(v.v0) * v.v0/(2 * f.tubing.DItbg);
				}
				for( v.o = 0; v.o < 4; v.o++ ){ //PQ REPETE 4 VEZES
					//PRESSÃO NO PLUNGER A MONTANTE, ACIMA DA COLUNA DE LÍQUIDO FORMADA NO FUNDO
					v.PplgM   = ue.GASOSTT(f.varSaida.PcsgB - c.ROliq * c.G * f.tempos.Ltbg, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg), f.varSaida.Hplg - f.tempos.Ltbg);
					//SUBTRAI A PRESSAO DO PLUNGER A MONTANTE DA PERDA POR FRICCAO
					v.PplgM  -= v.Pfric;
					//CALCULA A VARIACAO DE VELOCIDADE DO PISTAO, OBTIDA DA SEGUNDA LEI DE NEWTON
					v.delta_v = c.step * (((v.save_PplgM + v.PplgM)/2.0 - v.PplgJ) * c.AItbg)/(c.ROliq * f.tempos.Lslg * c.AItbg + f.pistao.Mplg);
					//CALCULA A VARIACAO DE POSICAO DO PISTAO
					v.delta_h = v.v0 * c.step + c.step * v.delta_v/2;
					//CALCULA A VELOCIDADE DA GOLFADA
					v.v0      = v.save_v0 + v.delta_v;
					//CALCULA A POSICAO DO PISTAO
					f.varSaida.Hplg = v.save_Hplg + v.delta_h;
					//CALCULA A VISCOSIDADE
					v.Visc   = ue.VISC ( ue.TEMP( f.tubing.Lcauda - f.varSaida.Hplg - f.tempos.Lslg/2 ) );
					//CALCULA O NUMERO DE REYNOLDS
					v.Rey    = c.ROliq * abs(v.v0) * f.tubing.DItbg/v.Visc;
					//CALCULA O FATOR DE FRICCAO
					v.Fric   = ue.FRIC(v.Rey, f.tubing.E, f.tubing.DItbg);
					//CALCULA A PERDA POR FRICCAO
					v.Pfric  = c.ROliq * v.Fric * f.tempos.Lslg * abs(v.v0) * v.v0/(2 * f.tubing.DItbg);
				}
				//ARMAZENA A PRESSAO DO PLUNGER A MONTANTE
				v.save_PplgM = v.PplgM;
				//PROFUNDIDADE DO TOPO DA GOLFADA DEVIDO ASUBIDA DO PISTAO
				v.H       = f.tubing.Lcauda - f.varSaida.Hplg - f.pistao.Lplg - f.tempos.Lslg;
				//VARIACAO DO VOLUME DA GOLFADA DEVIDO A SUBIDA DO PISTAO
				v.delta_V = v.delta_h * c.AItbg;
				//CALCULA O VOLUME DO TUBING DEVIDO A VARIACAO DO VOLUME DA GOLFADA
				v.V      -= v.delta_V;
				//PRESSAO DE FLUXO NO FUNDO DO POCO RECEBE A PRESSAO NO FUNDO DO REVESTIMENTO
				v.Pwf     = f.varSaida.PcsgB;
				//EQUACAO (4.10) . CALCULO DA VAZAO DO RESERVATORIO
				f.varSaida.Qlres = c.Qmax * (1 - .2*(v.Pwf/f.reservat.Pest) - .8*pow(v.Pwf/f.reservat.Pest, 2));
				//CALCULO DO NUMERO DE MOLES DO RESERVATORIO BASEADO NO FATOR I, CALCULADO NA INICIALIZACAO, COM UMA VAZAO EM m³/d
				v.Ntotal       +=((c.step * f.varSaida.Qlres * f.reservat.RGL * c.Pstd/86400)/(c.R * c.Tstd) - c.step * v.I);
				//EQUACAO (2.4.1??) CALCULO DA ALTURA DA COLUNA DE LIQUIDO NO FUNDO DO POCO
				f.tempos.Ltbg +=(c.step * f.varSaida.Qlres/86400)/c.AItbg;

				//SE O PISTAO ESTIVER SUBINDO
				if (v.flag == 1)
					//CALCULO QUE FICA ACUMULANDO NO FUNDO DO POCO PREPARANDO A GOLFADA
					v.LtbgZ += (c.step * f.varSaida.Qlres/86400)/c.AItbg;
				//SE O PISTAO ESTIVER MERGULHADO NA GOLFADA
				if ( (v.temp = f.tempos.Ltbg - f.varSaida.Hplg) >= 0) {
					//SE A GOLFADA POSSUIR VELOCIDADE
					if( v.v0 >= 0) {
						//AUMENTA O COMPRIMENTO DA GOLFADA
						f.tempos.Lslg += v.temp;
						//DIMINUI A ALTURA DA COLUNA DE LIQUIDO NO FUNDO
						f.tempos.Ltbg -= v.temp;
						//ARMAZENA O TAMANHO DA GOLFADA
						v.LslgX = f.tempos.Lslg;
					}
					//SENAO, SE O PISTAO ESTIVER PARADO NO FUNDO
					else if ( v.flag == 0 ) {
						//CALCULA A FUTURA GOLFADA
						v.LtbgZ = f.tempos.Lslg + f.tempos.Ltbg;
						//PISTAO VAI COMECAR A SUBIR ???
						v.flag  = 1;
					}
				}
				//VOLUME DO GAS ABAIXO DO PISTAO
				v.Vt = c.AItbg * (f.varSaida.Hplg - f.tempos.Ltbg);
				//SE O PISTAO ESTIVER EMBAIXO DA COLUNA DE LIQUIDO NO FUNDO
				if( v.Vt <= 0 ) {
					//CALCULA A PRESSAO DE FLUXO DE FUNDO = FUNDO DO REVESTIMENTO NORMALIZADA PELO NUMERO DE MOLES
					v.Pwf = f.varSaida.PcsgB = f.varSaida.PcsgB * v.Ntotal/v.N;
					//ASSOCIA O CONTADOR d AO CONTADOR i
					v.d   = v.i;
					//CALCULA A PRESSAO NO TOPO DO REVESTIMENTO NORMALIZADA
					f.tempos.PcsgT = f.tempos.PcsgT * v.Ntotal/v.N;
				}
				else{
					/*Calcula a temperatura média da seguinte forma:
					Encontra a temperatura entre a base do pistão e o topo do tubing
					Encontra a temperatura entre o topo da coluna de líquido no fundo e o topo do tubing
					Essa diferença corresponde à temperatura da coluna de gás abaixo do pistão*/
					v.TTt=(ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg) + ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg))/2.0;
					//SE O CONTADOR i ESTIVER UMA ITERAÇÃO À FRENTE DO CONTADOR d
					if ( v.i == v.d + 1) {
						//CALCULA O Nº DE MOLES DO GÁS ABAIXO DO PISTÃO
						v.Nt=f.varSaida.PcsgB*v.Vt/(c.R*v.TTt);
						//CALCULA E ARMAZENA O Nº DE MOLES DE GÁS NO ESPAÇO ANULAR
						v.save_Na=v.Na=v.Ntotal-v.Nt;
						//AMAZENA 0 NA PRESSÃO MÉDIA DE GÁS ABAIXO DO PISTÃO
						v.save_PPt=0;
						//AMAZENA 0 NA PRESSÃO MÉDIA DO REVESTIMENTO
						v.save_PPcsg=0;
					}
					else {
						//ATRIBUI AO Nº DE MOLES DO GÁS NO ESPAÇO ANULAR O Na DA ITERAÇÃO ANTERIOR
						v.Na=v.save_Na;
						//CALCULLA O Nº DE MOLES DO GÁS ABAIXO DO PISTÃO BASADO EM Na E NO Nº DE MOLES DE GÁS DE SAÍDA DO RESERVATÓIO
						v.Nt=v.Ntotal-v.Na;
					}
					//INICIALIZA O CONTADOR Y COM 0
					v.y=0;
					do{
						//CRIA VARIÁVEL PARA ARMAZENAR A PRSSÃO MÉDIA DO GÁS ABAIXO DO PISTÃO DA ITERAÇÃO ANTERIOR
						v.p_=v.save_PPt;
						//ATRIBUI À PRESSÃO MÉDIA ABAIXO DO PISTÃO A PRESSÃO NO FUNDO DO REVESTIMENTO
						v.PPt=f.varSaida.PcsgB;
						//LAÇO for (SE O VALOR ABSOLUTO DADIFERENÇA ENTRE AS PRESSÕES MÉDIAS DO GÁS ABAIXO DO PISTÃO DAS ITERAÇÕES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATÉ 100 ITERAÇÕES)
						for( v.u=0; abs(v.PPt-v.p_)>1.0 && v.u < 100; v.u++) {
							//ATUALIZA A PRESSÃO MÉDIA DO GÁS ABAIXO DO PISTÃO
							v.PPt=v.p_;
							//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O GÁS ABAIXO DO PISTÃO
							v.z=ue.Z(v.PPt/c.Ppc,v.TTt/c.Tpc);
							//CALCULA A PRESSÃO MÉDIA DO GÁS ABAIXO DO PISTÃO
							v.p_ = v.z * v.Nt * c.R * v.TTt/v.Vt;
						}
						//ARMAZENA A PRESSÃO MÉDIA DO GÁS ABAIXO DO PISTÃO
						v.save_PPt=v.p_;
						//CALCULA A PRESSÃO MÉDIA NA BASE DA COLUNA DE GÁS ABAIXO DO PISTÃO
						v.Pbt=(v.p_*2)/(1+exp(-(c.PM*c.G*(f.varSaida.Hplg-f.tempos.Ltbg))/(v.z*c.R*v.TTt)));
						//SOMA A PRESSÃO DA COLUNA D LÍQUIDO NO FUNDO À PRESSÃO CALCULADA ANTERIOMENTE
						v.Pbt+=c.ROliq*c.G*f.tempos.Ltbg;
						//ARMAZENA A PRESSÃO MÉDIA DO REVESTIMENTO NA ITERAÇÃO ANTERIOR NA VARIÁVEL p
						v.p_=v.save_PPcsg;
						//A PRESSÃO MÉDIA DO REVESTIMENTO RECEBE A PRESSÃO NA BASE DO REVESTIMENTO
						v.PPcsg=f.varSaida.PcsgB;
						//LAÇO for (SE O VALOR ABSOLUTO DA DIFERENÇA ENTRE AS PRESÕES MÉDIAS DO REVESTIMENTO DAS ITERAÇÕES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATÉ 100 ITERAÇÕES)
						for(v.u=0;abs(v.PPcsg-v.p_)>1.0&&v.u<100;v.u++){
							//ATUALIZA A PRESSÃO MÉDIA DO REVESTIMENTO
							v.PPcsg = v.p_;
							//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O GÁS NO REVESTIMENTO
							v.z=ue.Z(v.PPcsg/c.Ppc, c.TTcsg/c.Tpc);
							//CACULA A PRESSÃO MÉDIA DO GÁS NO REVESTIMENTO
							v.p_ = v.z * v.Na * c.R * c.TTcsg/c.Vcsg;
						}
						//ARMAZENA A PRESSÃO MÉDIA DO REVESTIMENTO
						v.save_PPcsg = v.p_;
						//CALCULA A PRESSÃO NA BASE DO ESPAÇO ANULAR
						v.Pba = (v.p_ * 2)/(1 + exp(-(c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg)));
						//SE O VALOR ABSOLUTO DA DIFERENÇA ENTRE AS PRESSÕES NA BASE DO TUBING E NA BASE DO ESPAÇO ANULAR É MENOR OU IGUAL A 1 ENTÃO ENCERRA O PROGRAMA
						if ( abs(v.Pbt - v.Pba) <= 1.0 ) {
							break;
						}
						//SENÃO,SE O CONTADOR Y ESTIVER NA PRIMEIRA ITERAÇÃO
						else if( v.y == 0 ){
							//ARMAZENA NA VARIÁVEL Nt O NÚMERO DE MOLES DO GÁS NO TUBING
							v.Nt_ = v.Nt;
							//ARMAZENA NA VARIÁVEL F_ A DIFERENÇA ENTRE AS PRESSÕES NAS BASES DO ESPAÇO ANULAR E DO TUBING
							v.F_ = v.Pbt-v.Pba;
							//SE A PRESSÃO NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ESPAÇO ANULAR O NÚMERO DE MOLES DE GÁS NO TUBING É MULTIPLICADO POR 0,5 E RECALCULA O NÚMERO DE MOLES DO GÁS NO ESPAÇO ANULAR
							if( v.Pbt > v.Pba ) {
								v.Nt *= 0.5;
								v.Na = v.Ntotal - v.Nt;
							}
							//O Nº DE MOLES DO GÁS NO TUBING É MULTIPLICADO POR 1,5 E RECALCULA O NÚMERO DE MOLES DO GÁS NO ESPAÇO ANULAR
							else {
								v.Nt *= 1.5;
								v.Na = v.Ntotal - v.Nt;
							}
						}
						else{
							//A VARIÁVEL Ntt RECEBE O NÚMERO DE MOLES DE GÁS NO TUBING
							v.Ntt=v.Nt;
							//RECALCULA O NÚMERO DE MOLES DO GÁS NO TUBING ATRAVÉS DE UM EXPRESSÃO EMPÍRICA
							v.Nt=v.Nt-(v.Pbt-v.Pba)*(v.Nt_-v.Nt)/(v.F_-(v.Pbt-v.Pba));
							//RECALCULA O NÚMERO DE MOLES DO GÁS NO ESPAÇO ANULAR
							v.Na = v.Ntotal - v.Nt;
							//A VARIÁVEL Nt_ RECEBE O VALOR DE Ntt
							v.Nt_ = v.Ntt;
							//ARMAZENA NA VARIÁVEL F_ A DIFERENÇA ENTRE AS PRESSÕES NAS BASES DO ESPAÇO ANULAR E DO TUBING
							v.F_ = v.Pbt - v.Pba;
						}
						//INCREMENTA CONTADOR
						v.y++;
					}while(v.y<150);
					//ARMAZENA O NÚMERO DE MOLES DO GÁS NO ESPAÇO ANULAR
					v.save_Na = v.Na;
					//ATRIBUI À PRESSÃO DE FLUXO NO FUNDO DO REVESTIMENTO A PRESSÃO NA BASE DO REVESTIMENTO A QUAL RECEBE A PRESSÃO NA BASE DO ESPAÇO ANULAR
					v.Pwf = f.varSaida.PcsgB = v.Pba;
					//CALCULA A PRESSÃO NO TOPO DO REVESTIMENTO
					f.tempos.PcsgT = (v.p_*2)/(1+exp((c.PM*c.G*f.tubing.Lcauda)/(v.z*c.R*c.TTcsg)));
				}
				//ATRIBUI À VARIAVEL N O NÚMERO DE MOLES NO TUBING,QUE É O MESMO PRODUZIDO PELO RESERVATÓRIO
				v.N=v.Ntotal;
				//CALCULA A TEMPERATURA MÉDIA ENTRE A TEMPERATURA DA COLUNA DE GÁS ACIMA DA GOLFADA E A TEMPERATURA NA SUPERFÍCIE
				v.TT=(ue.TEMP(v.H)+c.Tsup)/2.0;
			}
			else{
				//INICIALIZA A POSIÇÃO DO PLUNGER
				f.varSaida.Hplg=0;
				//PRESSÃO DE FLUXO NO FUNDO DO POÇO IGUAL À PRESSÃO NA BASE DO REVESTIMENTO
				v.Pwf=f.varSaida.PcsgB;
				//CALCULA A VAZÃO DE LÍQUIDO NO RESERVATÓRIO
				f.varSaida.Qlres = c.Qmax*(1-.2*(v.Pwf/f.reservat.Pest)-.8*pow(v.Pwf/f.reservat.Pest,2));
				//EQ.4.1 AUMENTO DA COLUNA DE LIQUIDO NO FUNDO DO TUBING
				f.tempos.Ltbg+=(c.step*f.varSaida.Qlres/86400)/c.AItbg;
				//INCREMENTA O NÚMERO DE MOLES DE GÁS NO RESERVATÓRIO
				v.Ntotal+=((c.step*f.varSaida.Qlres*f.reservat.RGL*c.Pstd/86400)/(c.R*c.Tstd)-c.step*v.I);
				//NORMALIZA A PRESSÃO NA BASE DO REVESTIMENTO PEO NÚMERO DE MOLES
				f.varSaida.PcsgB=f.varSaida.PcsgB*v.Ntotal/v.N;
				//NORMALIZA A PRESSÃO NO TOPO DO REVESTIMENTO PELO NÚMERO DE MOLES
				f.tempos.PcsgT=f.tempos.PcsgT*v.Ntotal/v.N;
				//ATRIBUI A N O NÚMERO DE MOLES DO GÁS NO RESERVATÓRIO
				v.N=v.Ntotal;

			}
			//criarMensagem(CycleStage.PLUNGER_RISE);

		}/* fim do FOR Abrir Valv Motora */

		//FORÇANDO O PLOTE DO ÚLTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
			//criarMensagem(CycleStage.PLUNGER_RISE);
		}

		//NÃO CONSEGUIU CHEGAR NA SUPERFÍCIE
		if (f.tempos.Ontime - (v.i*c.step + v.Transient) <= 0) {
			v.j = 0;
			v.Ntotal += v.nn;
			Controle();
			//break;
		}

		//ENVIANDO MENSAGEM COM O TEMPO DE DURACAO DA SUBIDA PISTAO
		//this.enviarVarCiclo(SLUG_RISE_TIME, (--v.i/(1.0/c.step)+v.transient) );
		this.enviarVarCiclo(CycleStage.STAGE_RISE_DURATION,(--v.i/(1.0/c.step)+v.Transient));
		//this.enviarVarCiclo(STAGE_RISE_DURATION, v.i * c.step);
		//Se a válvula foi alterada, então chamar buildup
		if ( this.alterarValvula ) {
			this.alterarValvula = false;
			this.OffBuildUp(false);
		}
		else {
			//ETAPA QUE ACABOU FOI A SUBIDA DO PISTAO
			c.estagio = c.SUBIDA_PISTAO;
		}
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Modelo matemático da etapa de produção de líquido.
	 */
	public void producaoLiquido(){

		//CRIACAO DE VARIAVEIS PARA A SIMULACAO
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		//FORÇANDO PLOTAR O PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		//CALCULA AS PERDAS POR FRICCAO
		v.Pfric += v.delta_h * c.ROliq * v.v0 * (pow(f.tubing.DItbg, 2)/pow(f.valvula.Dab/1000, 2) - 1)/c.step; //CUIDADO! , SEM EFEITO
		//ENQUANTO O PISTAO NAO CHEGAR TOTALMENTE NA SUPERFICIE E O TEMPO DA VALVULA ABERTA NAO TIVER TERMINADO
		for(v.j = 0; f.tubing.Lcauda > f.varSaida.Hplg &&
					f.tempos.Ontime - (v.i*c.step + v.j*c.step_ + v.Transient) > 0 &&
					(!this.alterarValvula); v.j++){//for1
			/* A CADA INTERVALO CONSTANTE DE TEMPO, É ATUALIZADO A VARIAVEL TEMPL(MAIS ABAIXO)
			 * j É O CONTADOR DO FOR(VARIAVEL) E step_ É UM DOS PASSOS DE INTEGRACAO(CONSTANTE)
			 * QUANDO A MULTIPLICACAO DELES FOR X.0X, A CONDICAO É COMPLETA
			 */
			if( (int)(v.j * c.step_ * 10) % 10 == 0) {
				//TEMPL RECEBE A DISTANCIA ENTRE A BASE DO PISTAO E A SUPERFICIE
				v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
				v.LtbgX = f.tempos.Ltbg;
			}
			//PRESSAO DE FLUXO DO UNFOD RECEBE A PRESSAO NA BASE DO ANULAR
			v.Pwf     = f.varSaida.PcsgB;
			//VAZÃO DE LÍQUIDO E GÁS NO RESERVATÓRIO
			f.varSaida.Qlres = c.Qmax * (1 - .2 *(v.Pwf/f.reservat.Pest) - .8 * pow(v.Pwf/f.reservat.Pest,  2));
			//INCREMENTA O NUMERO DE MOLES DO GAS NO RESERVATORIO
			v.Ntotal += ((c.step_ * f.varSaida.Qlres * f.reservat.RGL * c.Pstd/86400)/(c.R * c.Tstd) - c.step_ * v.I);
			//INCREMENTA A ALTURA DE COLUNA DE LIQUIDO NO FUNDO DO POCO
			f.tempos.Ltbg   += (c.step_ * f.varSaida.Qlres/86400)/c.AItbg;
			//CALCULA O VOLUME DA COLUNA DE GAS ABAIXO DO PISTAO
			v.Vt      = c.AItbg * (f.varSaida.Hplg - f.tempos.Ltbg);
			//CALCULA A TEMPERATURA MEDIA ENTRE A BASE DO PISTAO E A COLUNA DE LIQUIDO NO FUNDO DO POCO
			v.TTt     = (ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg) + ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg))/2.0;
			//NUMERO DE MOLES DO GAS NO ANULAR ATUAL RECEBE O VALOR PASSADO
			v.Na      = v.save_Na;
			//NUMERO DE MOLES DO GAS NO TUBING
			v.Nt      = v.Ntotal - v.Na;
			//INICIALIZA O CONTADOR y
			v.y       = 0;

			do{//do1
				//SALVA O VALOR PARA ARMAZENAR A PRESSAO MEDIA DO GAS BAIXO DO PISTAO DA ITERACAO ANTERIOR
				v.p_  = v.save_PPt;
				//PRESSAO MEDIA ABAIXO DO PISTAO = PRESSAO NA BASE DO ANULAR
				v.PPt = f.varSaida.PcsgB;
				//ENQUANTO A DIFERENCA ENTRE A PRESSAO MEDIA DO GAS ABAIXO DO PISTAO ATUAL E ANTERIOR FOR MAIOR QUE 1 E AS ITERACOES NAO CHEGAR ATE 100
				for(v.u = 0; abs(v.PPt - v.p_) > 1.0 && v.u < 100; v.u++){
					//ATUALIZA A PRESSAO MEDIA ABAIXO DO PISTAO
					v.PPt = v.p_;
					//CALCULA O FATOR DE COMPRESSIBILIDADE ABAIXO DO PISTAO
					v.z   = ue.Z(v.PPt/c.Ppc, v.TTt/c.Tpc);
					//CALCULA A PRESSAO MEDIA DO GAS ABAIXO DO PISTAO
					v.p_  = v.z * v.Nt * c.R * v.TTt/v.Vt;
				}
				//ARMAZENA A PRESSAO MEDIA ABAIXO DO PISTAO
				v.save_PPt = v.p_;
				//PRESSAO MEDIA NA BASE DA COLUNA DE GAS ABAIXO DO PISTAO
				v.Pbt      = (v.p_ * 2) / (1 + exp( -(c.PM * c.G * (f.varSaida.Hplg - f.tempos.Ltbg))/(v.z * c.R * v.TTt) ));
				//ADICIONA O TERMO DENSIDADE * GRAVIDADE * ALTURA DA COLUNA DE FUNDO
				v.Pbt     += c.ROliq * c.G * f.tempos.Ltbg;
				//ARMAZENA O VALOR DA PRESSAO MEDIA DO ANULAR
				v.p_       = v.save_PPcsg;
				//ASSUME PRESSAO MEDIA DO ANULAR COMO PRESSAO NA BASE DO TUBING
				v.PPcsg    = f.varSaida.PcsgB;
				//ENQUANTO A DIFERENCA ENTRE A PRESSAO MEDIA NO ANULAR ATUAL E ANTERIOR FOR MAIOR QUE 1 E AS ITERACOES NAO CHEGAR ATE 100
				for(v.u = 0; abs(v.PPcsg - v.p_) > 1.0 && v.u < 100; v.u++){
					//ARMAZENA O VALOR DA PRESSAO ANTERIOR
					v.PPcsg = v.p_;
					//CALCULA O VALOR DA COMPRESSIBILIDADE PARA O GAS NO ANULAR
					v.z     = ue.Z(v.PPcsg/c.Ppc, c.TTcsg/c.Tpc);
					//CALCULA A PRESSAO MEDIA NO ANULAR
					v.p_    = v.z * v.Na * c.R * c.TTcsg/c.Vcsg;
				}
				//ARMAZENA O VALOR DA PRESSAO MEDIA DO ANULAR RESULTADO DO FOR ANTERIOR
				v.save_PPcsg = v.p_;
				//CALCULA A PRESSAO NA BASE DO ANULAR
				v.Pba        = (v.p_ * 2) / (1 + exp( -(c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg) ));
				//SE A DIFERENCA ENTRE AS PRESSOES NA BASE DO TUBING E NA BASE DO ANULAR FOR MENOR QUE 1, ENTAO SAI DA ITERACAO DO1
				if(abs(v.Pbt - v.Pba) <= 1.0) break;
				//SE O CONTADOR ESTIVER NA PRIMEIRA ITERACAO
				else if ( v.y == 0 ) {
					//ARMAZENA O NUMERO DE MOLES DO GAS NO TUBING
					v.Nt_ = v.Nt;
					//F_ RECEBE A DIFERENCA ENTRE AS PRESSOES NAS BASES DO ESPACO ANULAR E DO TUBING
					v.F_  = v.Pbt - v.Pba;
					//SE A PRESSAO NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ANULAR
					if(v.Pbt > v.Pba){
						//DIVIDE POR 2(*0.5) O NUMERO DE MOLES DE GAS NO TUBING
						v.Nt *= 0.5;
						//RECALCULA O NUMERO DE MOLES DO GAS NO ANULAR
						v.Na  = v.Ntotal - v.Nt;
					}
					//CASO A PRESSAO NA BASE DO TUBING FOR MENOR OU IGUAL A DA BASE DO ANULAR
					else {
						//SOMA A METADE(*1.5) O NUMERO DE MOLES DO GAS NO TUBING
						v.Nt *= 1.5;
						//RECALCULA O NUMERO DE MOLES DO GAS NO ANULAR
						v.Na  = v.Ntotal - v.Nt;
					}
				}
				//SE NAO ESTIVER NA PRIMEIRA ITERACAO
				else{
					//Ntt ARMAZENA O NUMERO DE MOLES DE GAS NO TUBING
					v.Ntt = v.Nt;
					//RECALCULA O NUMERO DE MOLES DO GAS NO TUBING
					v.Nt  = v.Nt - (v.Pbt - v.Pba)*(v.Nt_ - v.Nt)/(v.F_ - (v.Pbt - v.Pba));
					//RECALCULA O NUMERO DE MOLES DO GAS NO ANULAR
					v.Na  = v.Ntotal - v.Nt;
					//VARIAVEL Nt_ RECEBE O NUMERO DE MOLES DE GAS ANTERIOR
					v.Nt_ = v.Ntt;
					//F_ RECEBE A DIFERENCA ENTRE AS PRESSOES NAS BASES DO TUBING E ANULAR
					v.F_  = v.Pbt - v.Pba;
				}
				//INCREMENTA O CONTADOR Y
				v.y++;
			//ENQUANTO NAO CHEGAR EM 150 ITERACOES
			} while( v.y < 150 );//fim do1
			//ARMAZENA O VALOR DO NUMERO DE MOLES NO ANULAR
			v.save_Na = v.Na;
			//ATRIBUI AO FLUXO NO FUNDO DO ANULAR A PRESSAO NA BASE DO ANULAR QUE RECEBE A PRESSAO NA BASE DO ESPACO ANULAR
			v.Pwf     = f.varSaida.PcsgB = v.Pba;
			//CALCULA A PRESSAO NO TOPO DO ANULAR
			f.tempos.PcsgT   = (v.p_ * 2) / (1 + exp((c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg) ));
			//A PRESSAO NA BASE DO TUBING É A PRESSAO MEDIA NA BASE DA COLUNA DE GAS ABAIXO DO PISTAO SUBTRAIDA DA PRESSAO DA COLUNA DE LIQUIDO NO FUNDO DO POCO
			v.PtbgB   = v.Pbt - c.ROliq * c.G * f.tempos.Ltbg;
			//PRESSAO DO PISTAO A MONTANTE, NO TOPO DA COLUNA DE GAS ABAIXO DO PISTAO
			v.PplgM   = ue.GASOSTT(v.PtbgB, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg),ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg), f.varSaida.Hplg - f.tempos.Ltbg);
			/* CALCULA A PRESSAO DO TUBING NA SUPERFICIE
			 * É calculada pela pressao no pistao à montante retirando a perda
			 * de pressao por G da massa do pistao e da golfada
			 */
			f.varSaida.PtbgT   = v.PplgM - (c.ROliq * c.G * f.tempos.Lslg + f.pistao.Mplg* c.G/c.AItbg);
			//CALCULA A PERDA DE PRESSAO A MONTANTE DO PISTAO ATRAVES DE EXPRESSAO NAO UTILIZADA NO MATERIAL DO BARUZZI
			v.PplgM  -= (v.Pfric + c.ROliq * pow(v.v0*pow(f.tubing.DItbg,2) / pow(f.valvula.Dab/1000,2),2) / 2.0);
			//CALCULA A PRESSAO A JUSANTE DO PISTAO DEVISO AO PESO DA MASSA DO PISTAO E DA GOLFADA
			v.PplgJ   = f.tempos.Lslg * c.ROliq * c.G + f.pistao.Mplg * c.G/c.AItbg;
			//CALCULA A VISCOSIDADE DO FLUIDO
			double Visc = ue.VISC(ue.TEMP(f.tempos.Ltbg));
			//COEFICIENTE DE AMORTECIMENTO VISCOSO
			double amort = (Visc * 3 * M_PI * pow(f.pistao.Dplg,3) * f.tempos.Ltbg) * (1 + ((f.tubing.DItbg - f.pistao.Dplg) / f.pistao.Dplg)) / (4*pow((f.tubing.DItbg - f.pistao.Dplg) / 2,3));
			//CALCULA VARIACAO DE VELOCIDADE DO PISTAO, SEGUNDA LEI DE NEWTON
			v.delta_v = c.step_ * ((v.PplgM - v.PplgJ) * c.AItbg) / (c.ROliq * f.tempos.Lslg * c.AItbg + f.pistao.Mplg + amort * c.step_);
			//CALCULA VARIACAO DA POSICAO DO PISTAO
			v.delta_h = v.v0 * c.step_ + c.step_ * v.delta_v/2;
			//INCREMENTA A VELOCIDADE DA GOLFADA
			v.v0 += v.delta_v;
			//INCREMENTA A POSICAO DO PISTAO
			f.varSaida.Hplg += v.delta_h;
			//SE A POSICAO DO PISTAO FOR MAIOR QUE A ALTURA DO TUBING
			if( f.varSaida.Hplg > f.tubing.Lcauda ){
				//POSICAO DO PISTAO RECEBE O VALOR DA ALTURA DO TUBING
				f.varSaida.Hplg = f.tubing.Lcauda;
			}
			//O COMPRIMENTO DA GOLFADA É SUBTRAIDO DE delta_h
			f.tempos.Lslg  -= v.delta_h;
			//SE O COMPRIMENTO DA GOLFADA FOR MENOR QUE ZERO
			if ( f.tempos.Lslg < 0 ) {
				//MANTEM O COMPRIMENTO DA GOLFADA IGUAL A ZERO
				f.tempos.Lslg = 0;
			}
			//CALCULA A VISCOSIDADE COM A METADE DO COMPRIMENTO DA GOLFADA
			v.Visc   = ue.VISC(ue.TEMP(( f.tempos.Lslg/2.0 < 0.0 ? 0.0 : f.tempos.Lslg/2.0)));
			//CALCULA O NUMERO DE REYNOLDS
			v.Rey    = c.ROliq * abs(v.v0) * f.tubing.DItbg/v.Visc;
			//CALCULA O FATOR DE FRICCAO
			v.Fric   = ue.FRIC(v.Rey, f.tubing.E, f.tubing.DItbg);
			//CALCULA AS PERDAS POR FRICCAO
			v.Pfric  = c.ROliq * v.Fric * f.tempos.Lslg * abs(v.v0) * v.v0/(2 * f.tubing.DItbg);
			//DECREMENTA A PERDA POR FRICCAO DE UM FATOR NAO COMENTADO NO BARUZZI???????
			v.Pfric += v.delta_h*c.ROliq*v.v0*(pow(f.tubing.DItbg,2) /pow(f.valvula.Dab/1000,2)-1)/ c.step_;
			//CRIA UMA MENSAGEM PARA A INTERFACE ATUALIZAR SEUS ATRIBUTOS
			//criarMensagem(CycleStage.PRODUCTION);

		}//fim for1

		//FORÇANDO O PLOTE DO ULTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
			//criarMensagem(CycleStage.PRODUCTION);
		}

		//Enviando o tempo de duração da etapa de produção
		enviarVarCiclo(CycleStage.STAGE_PRODUC_DURATION, v.j * c.step_);
		//Se a válvula foi alterada de estado
		if ( this.alterarValvula ) {
			this.alterarValvula = false;
			this.OffBuildUp(false);
		}
		else {
			//ATRIBUI O ESTAGIO QUE FOI TERMINADO COMO PRODUCAO_LIQUIDO
			c.estagio = c.PRODUCAO_LIQUIDO;
		}
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Parte de controle da simulação.
	 */
	public void Controle() {

		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		/*CALCULA A VARIACAO DE PRESSAO COMO SENDO A DIFERENCA ENTRE AS PRESSOES
		 *NO TOPO DO ANULAR DO INICIO DO PROGRAMA E DA ITERACAO ATUAL
		 */
		v.delta_P = v.Ppart_csg - f.tempos.PcsgT;
		//SE O PISTAO ESTIVER ACIMA DA SUPERFICIE OU NO TOPO DO TUBING
		if( f.tubing.Lcauda <= f.varSaida.Hplg){
			//TEMPO DE CHEGADA DO PISTAO RECEBE A SOMA  DOS TEMPOS DE OCORRENCIA
			//DAS ETAPAS ANTERIORES MAIS O TEMPO QUE A ONDA ACUSTICA ATRAVESSA
			//O GAS NO TUBING ATE ATINGIR A GOLFADA
			v.piston_arrival = (int)(v.j*c.step_ + v.i*c.step + v.Transient);
		}
		//SE O PISTAO NAO CHEGOU NA SUPERFICIE
		else{
			//TEMPO DE CHEGADA DO PISTAO E ZERO
			v.piston_arrival = 0;
		}

		//CALCULA A PRODUCAO
		v.production = (float)((v.LslgX - f.tempos.Lslg)*c.AItbg*6.2848352758387*(1- c.FW));
	  //PRODUÇÃO ACUMULADA
		v.total_production = v.total_production + v.production;
		//TEMP TEM DIMENSOES DE VELOCIDADE
		v.temp = f.tubing.Lcauda/(v.j/(1.0/c.step_) + v.i/(1.0/ c.step) + v.Transient);
		//INCREMENTA O CONTADOR DE CICLOS
		v.count++;
		//ENVIAR O DADO DE PRODUCAO DO POCO PARA INTERFACE
		this.enviarVarCiclo(CycleStage.PRODUCTION_VOLUME, v.production);
		//ENVIAR O DADO DE PRODUCAO DO POCO PARA INTERFACE
		this.enviarVarCiclo(CycleStage.TOTAL_PRODUCTION, v.total_production);
		//ENVIAR O DADO DE TEMPO DE CHEGADA DO PISTAO NA SUPERFICIE
		this.enviarVarCiclo(CycleStage.PL_RISE_TIME, v.piston_arrival);
		//SE O PISTAO JA CHEGOU NA SUPERFICIE
		if( v.piston_arrival != 0 ){
			//CALCULA A VELOCIDADE DA MESMA MANEIRA QUE O TEMP
			v.velocity = (float)(f.tubing.Lcauda/(v.j/(1.0/ c.step_) + v.i/(1.0/ c.step) + v.Transient));
			//ENVIANDO OS DADOS PARA A INTERFACE
			//DADO DE VELOCIDADE MEDIA DE SUBIDA DO PISTAO
			this.enviarVarCiclo(CycleStage.AVERAGE_PL_VELOCITY, v.velocity);
			//DADO DE VELOCIDADE DE IMPACTO
			this.enviarVarCiclo(CycleStage.IMPACT_VEL, v.v0);
		}
		//SE CASO O PISTAO NAO CHEGOU NA SUPERFICIE
		else {
			//ATRIBUI LtbgY O COMPRIMENTO DA GOLFADA
			v.LtbgY = f.tempos.Lslg;
			//VELOCIDADE MEDIA DO PISTAO É ZERO
			v.velocity = 0;
			// ??????????????
			if( v.contador == 1){
				f.tempos.Ontime    += 0;
				f.tempos.Afterflow += 0;
				f.tempos.Offtime   += 0;
			}
			//VAI PARA A ETAPA DE BUILDUP
			OffBuildUp(false);
			//break;
		}
		//PARTE DO CONTROLADOR
		if ( !byPassController ) {
			//SE ESTIVER NO PRIMEIRO CICLO
			if( v.cont == 1 ){
				//ARMAZENA OFFTIME
				v.save_Offtime   = f.tempos.Offtime;
				//ARMAZENA AFTERFLOW
				v.save_Afterflow = f.tempos.Afterflow;
			}
			//SE HOUVER DIFERENCA ENTRE OS TEMPOS DE AFTERFLOW ATUAL E O ANTERIOR
			if( v.save_Afterflow != f.tempos.Afterflow){
				//ARMAZENA OFFTIME
				v.save_Offtime   = f.tempos.Offtime;
				//ARMAZENA AFTERFLOW
				v.save_Afterflow = f.tempos.Afterflow;
			}
			//SE CONTADOR É ZERO, O CONTROLE FUZZY ATUA(CP NÃO É MODIFICADO POIS O CONTROLE FUZZY ESTÁ VAZIO)
			if( v.contador == 0){
				//INCREMENTA CP
				v.CP += v.CP*ue.fuzzy_control((float)(10*(c.good_velocity - v.velocity)));
			}
			//AINDA NAO FOI IMPLEMENTADO O CONTROLE, AS VARIAVEIS NAO SE MODIFICAM
			else{
				if( v.contador==1 ){
					if( v.velocity < c.Slow ){
						f.tempos.Afterflow += 0;
						f.tempos.Offtime   += 0;
					}
					else{
						if( v.velocity > c.Fast ){
							f.tempos.Afterflow += 0;
							f.tempos.Offtime   -= 0;
						}
						else{
							f.tempos.Afterflow += 0;
							f.tempos.Offtime   -= 0;
						}
					}
				}
			}
		}
		//ESTAGIO RECEBE A ETAPA QUE FOI FINALIZADA - CONTROLE
		c.estagio = c.CONTROLE;

	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Parte do modelo matemático da etapa de Afterflow.
	 */
	public void Afterflow() {

		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		//FORÇAR A PLOTAGEM DO PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		//O PISTÃO SE ENCONTRA NA SUPERFÍCIE
		f.varSaida.Hplg = f.tubing.Lcauda;
		//ANULA A VELOCIDADE DA GOLFADA
		v.v0 = 0;
		//A VARIÁVEL Pt RECEBE A PRESSÃO NO TOPO DO REVESTIMENTO SUBTRAÍDA DA PRESSÃO NO TOPO DA COLUNA DE LÍQUIDO FORMADA NO FUNDO DO TUBING
		//PRESSÃO NA COLUNA DE GÁS ABAIXO DO PISTÃO
		v.Pt = f.tempos.PcsgT - c.ROliq * c.G * f.tempos.Ltbg;
		//PARA UM TEMPO MENOR QUE O DE AFTERFLOW
		for(v.k = 1; v.k*c.step_aft < f.tempos.Afterflow && (!this.alterarValvula); v.k++) {
			/*****/
			if((int)(v.j*c.step_aft*10) % 10 == 0){
				v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
				v.LtbgX = f.tempos.Ltbg;
			}

			//INCREMENTA O COMPRIMENTO DA GOLFADA QUE ESTÁ SENDO FORMADA NO FUNDO
			f.tempos.Ltbg += (c.step_aft * f.varSaida.Qlres/86400)/ c.AItbg;
			//CALCULA O VOLUME ENTRE O TOPO DA COLUNA DE LÍQUIDO FORMADA NO FUNDO E O TOPO DE TUBING
			v.V = c.AItbg * (f.tubing.Lcauda - f.tempos.Ltbg);
			//CALCULA A VAZÃO DE GÁS NA LINHA DE SURGÊNCIA(CONSIDERA PRESSÃO A MONTANTE,PRESSÃO A JUSANTE,DIÂMETRO DE ABERTURA E TEMPERATURA DE SUPERFÍCIE)
			v.qqq = ue.QSC( v.Pt/1000.0 , f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);
			//ARMAZENA O NÚMERO TOTAL DE MOLES TOTAL(GÁS NO TUBING E ANULAR)
			v.n = v.Ntotal;

			do{
				//VAZÃO DE LÍQUIDO DO RESERVATÓRIO
				f.varSaida.Qlres = c.Qmax * ( 1- .2 * (v.Pwf/ f.reservat.Pest) - .8*pow(v.Pwf/ f.reservat.Pest,2));
				//Pt É ATRIBUÍDA À VARIÁVEL Ptt(PRESSÃO NA COLUNA DE GÁS ABAIXO DO PISTÃO)
				v.Ptt=v.Pt;
				//CALCULA A NOVA VAZÃO NA LINHA DE SURGÊNCIA
				v.q=ue.QSC(v.Pt/1000.0, f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);
				//SE A VAZÃO FOR NULA ENCERRA O PROGRAMA
				if(v.q == 0.0){break;}
				//CALCULA A VAZÃO MÉDIA
				v.qq=(v.qqq + v.q)/2.0;
				//CALCULA O NÚMERO TOTAL DE MOLES RESTANTES APÓS A VAZÃO COM OS PROVENIENTES DO RESERVATÓRIO
				v.Ntotal = v.n - c.step_aft*( (v.qq/86400) * c.Pstd)/(c.R * c.Tstd) + (c.step_aft * f.varSaida.Qlres * f.reservat.RGL * c.Pstd/86400)/(c.R * c.Tstd);
				//CALCULA A TEMPERATURA MÉDIA DA COLUNA ABAIXO DO PISTÃO
				v.TTt=(ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg) + ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg))/2.0;
				//ATUALIZA O NÚMERO DE MOLES NO ANULAR COM O VALOR DA ITERAÇÃO ANTERIOR
				v.Na = v.save_Na;
				//ATUALIZA O NÚMERO DE MOLES NO TUBING
				v.Nt = v.Ntotal - v.Na;
				//INICIALIZA O CONTADOR
				v.y = 0;

				do{
					//CRIA VARIÁVEL PARA ARMAZENAR A PRESSÃO MÉDIA DO GÁS ABAIXO DO PISTÃO DA ITERAÇÃO ANTERIOR
					v.p_= v.save_PPt;
					//ATRIBUI À PRESSÃO MÉDIA ABAIXO DO PISTÃO A PRESSÃO NO FUNDO DO REVESTIMENTO
					v.PPt = f.varSaida.PcsgB;
					//LAÇO for (SE O VALOR ABSOLUTO DA DIFERENÇA ENTRE AS PRESSÕES MÉDIAS DO GÁS ABAIXO DO PISTÃO DAS ITERAÇÕES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATÉ 100 ITERAÇÕES)
					for(v.u = 0; abs(v.PPt - v.p_) > 1.0 && v.u < 100; v.u++ ) {
						//ATUALIZA A PRESSÃO MÉDIA DO GÁS ABAIXO DO PISTÃO COM O VALOR ANTERIOR CALCULADO
						v.PPt = v.p_;
						//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O GÁS ABAIXO DO PISTÃO
						v.z = ue.Z(v.PPt/ c.Ppc, v.TTt/ c.Tpc);
						//CALCULA A PRESSÃO MÉDIA DO GÁS ABAIXO DO PISTÃO
						v.p_ = v.z*v.Nt*c.R*v.TTt/ v.V;
					}
					//ARMAZENA A PRESSÃO MÉDIA DO GÁS ABAIXO DO PISTÃO
					v.save_PPt = v.p_;
					//PRESSÃO MÉDIA NA BASE DA COLUNA DE GÁS ABAIXO DO PISTÃO
					v.Pbt = (v.p_ * 2)/(1 + exp(-(c.PM * c.G * (f.varSaida.Hplg - f.tempos.Ltbg))/(v.z * c.R * v.TTt)));
					//SOMA A PRESSÃO DA COLUNA DE LÍQUIDO NO FUNDO À PRESSÃO CALCULADA ANTERIORMENTE
					v.Pbt += c.ROliq * c.G * f.tempos.Ltbg;
					//ARMAZENA A PRESSÃO MÉDIA DO REVESTIMENTO NA ITERAÇÃO ANTERIOR NA VARIÁVEL p_
					v.p_ = v.save_PPcsg;
					//A PRESSÃO MÉDIA DO REVESTIMENTO RECEBE A PRESSÃO NA BASE DO REVESTIMENTO
					v.PPcsg = f.varSaida.PcsgB;
					//LAÇO for (SE O VALOR ABSOLUTO DA DIFERENÇA ENTRE AS PRESSÕES MÉDIAS DO REVESTIMENTO DAS ITERAÇÕES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATÉ 100)
					for(v.u = 0; abs(v.PPcsg - v.p_) > 1.0 && v.u<100; v.u++){
						//ATUALIZA A PRESSÃO MÉDIA DO REVESTIMENTO CALCULADO ANTERIORMENTE COM O VALOR ARMAZENADO
						v.PPcsg = v.p_;
						//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O GÁS NO REVESTIMENTO
						v.z = ue.Z(v.PPcsg/ c.Ppc, c.TTcsg/ c.Tpc);
						//CALCULA A PRESSÃO MÉDIA DO GÁS NO REVESTIMENTO
						v.p_ = v.z*v.Na*c.R*c.TTcsg/ c.Vcsg;
					}
					//ARMAZENA A PRESSÃO MÉDIA DO REVESTIMENTO
					v.save_PPcsg = v.p_;
					//CALCULA A PRESSÃO NA BASE DO ESPAÇO ANULAR
					v.Pba = (v.p_ * 2)/(1 + exp(-( c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg)));
					//SE O VALOR ABSOLUTO DA DIFERENÇA ENTRE AS PRESSÕES NA BASE DO TUBING E NA BASE DO ESPAÇO ANULAR É MENOR OU IGUAL A 1 ENTÃO ENCERRA O PROGRAMA
					if(abs(v.Pbt - v.Pba)<=1.0){break;}
					//SENÃO,SE O CONTADOR Y ESTIVER NA PRIMEIRA ITERAÇÃO
					else if(v.y == 0){
						//ARMAZENA NA VARIÁVEL Nt_ O NÚMERO DE MOLES DO GÁS NO TUBING
						v.Nt_ = v.Nt;
						//ARMAZENA NA VARIÁVEL F_ A DIFERENÇA ENTRE AS PRESSÕES NAS BASES DO ESPAÇO ANULAR E DO TUBING
						v.F_ = v.Pbt - v.Pba;
						//SE A PRESSÃO NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ESPAÇO ANULAR,O NÚMERO DE MOLES DO GÁS NO TUBING É MULTIPLICADO POR 0,5. RECALCULA O NÚMERO DE MOLES DO GÁS NO ESPAÇO ANULAR
						if(v.Pbt > v.Pba){
							v.Nt *= 0.5;
							v.Na = v.Ntotal - v.Nt;
						}
						//O NÚMERO DE MOLES DO GÁS NO TUBING É MULTIPLICADO POR 1,5
						else {
							v.Nt *= 1.5;
							v.Na = v.Ntotal - v.Nt;
						}
					}
					else{
						//A VARIÁVEL Ntt RECEBE O NÚMERO DE MOLES DE GÁS NO TUBING
						v.Ntt = v.Nt;
						//RECALCULA O NÚMERO DE MOLES DE GÁS NO TUBING ATRAVÉS DE UMA EXPRESSÃO EMPÍRICA
						v.Nt = v.Nt - (v.Pbt - v.Pba) * (v.Nt_ - v.Nt)/(v.F_ - (v.Pbt - v.Pba));
						//RECALCULA O NÚMERO DE MOLES DO GÁS NO ANULAR
						v.Na = v.Ntotal - v.Nt;
						//A VARIÁVEL Nt_ RECEBE A VARIÁVEL Ntt
						v.Nt_ = v.Ntt;
						//ARMAZENA NA VARIÁVEL F_ A DIFERENÇA ENTRE AS PRESSÕES NAS BASES DO ESPAÇO ANULAR E DO TUBING
						v.F_ = v.Pbt - v.Pba;
					}
					//INCREMENTA CONTADOR
					v.y++;
				} while( v.y < 150 );    /*   fim do DO - linha 1481  */
				//ARMAZENA O NÚMERO DE MOLES DO GÁS NO ESPAÇO ANULAR
				v.save_Na = v.Na;
				//ATRIBUI À PRESSÃO DE FLUXO NO FUNDO DO REVESTIMENTO A PRESSÃO NA BASE DO REVESTIMENTO A QUAL RECEBE A PRESSÃO NA BASE DO ANULAR
				v.Pwf = v.Pba;
				//CALCULA A PRESSÃO NO TOPO DA COLUNA DE GÁS, A PARTIR DO TOPO DA COLUNA DE LÍQUIDO FORMADA NO FUNDO
				v.Pt= ue.GASOSTT(v.PtbgB/*v.Pba - c.ROliq * c.G * f.tempos.Ltbg*/, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), c.Tsup, f.tubing.Lcauda - f.tempos.Ltbg);
			} while( abs(v.Ptt - v.Pt) > 1.0 );   /* fim do DO */
			//ATRIBUI À PRESSÃO NO TOPO DO TUBING A VARIÁVEL Pt
			f.varSaida.PtbgT = v.Pt;
			//ATRIBUI À PRESSÃO NA BASE DO TUBING A VARIÁVEL Pa MENOS PRESSÃO DA COLUNA DE LÍQUIDO FORMADA NO FUNDO
			v.PtbgB = v.Pba - c.ROliq * c.G * f.tempos.Ltbg;
			//CALCULA A PRESSÃO NO TOPO DO REVESTIMENTO
			f.tempos.PcsgT = (v.p_ * 2)/(1 + exp((c.PM * c.G * (f.tubing.Lcauda))/(v.z * c.R * c.TTcsg)));
			//ATRIBUI À PRESSÃO NO FUNDO DO REVESTIMENTO A PRESSÃO DE FLUXO NO FUNDO
			f.varSaida.PcsgB = v.Pwf;

			//criarMensagem(CycleStage.AFTERFLOW);
		} /*   fim do FOR  AfterFlow  -  linha 1415    */
		//Colocando valor default para a alteracao da variável
		this.alterarValvula = false;

		//FORÇANDO O PLOTE DO ULTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
			//criarMensagem(CycleStage.AFTERFLOW);
		}

		//Enviando o tempo de duração do afterflow
		enviarVarCiclo(CycleStage.STAGE_AFTER_DURATION, v.k * c.step_aft);

		//INFORMA QUE A ETAPA QUE ACABOU FOI O AFTERFLOW
		c.estagio = c.AFTERFLOW;
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Modelo matemático da etapa de Build Up da simulação.
	 * @param ChegouSup Informa se o pistão, na etapa de subida e produção,
	 *									conseguiu chegar à superfície, se sim true ou false caso
	 *									contrário.
	 */
	public void OffBuildUp(boolean ChegouSup){

		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		//FORÇAR A PLOTAGEM DO PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}
		// Variável para o tempo gasto no buildup
		double tpgasto = 0;
		/* Variável que diz qual etapa do build up está para modificar o passo de
		 *integração:
		 * 0 - Queda do pistão no gás
		 * 1 - Transição do pistão do gás para o lìquido
		 * 2 - Queda do pistão no líquido
		 * 3 - Pistão no fundo do poço
		 */
		int modo_passo = 0;

		// Passo de integração do pistão caindo no gás
		c._step = c._stepGas;

		//Pt RECEBE PRESSÃO NO TOPO DO REVESTIMENTO
		v.Pt = f.tempos.PcsgT;
		//N RECEBE O NÚMERO DE MOLES TOTAL
		v.N = v.Ntotal;
		//ARMAZENA A PRESSÃO NA BASE DO REVESTIMENTO DIVIDIDA POR 1.5 EM Ptt
		v.save_PPt = f.varSaida.PcsgB/1.5;
		//ARMAZENA A PRESSÃO NA BASE DO REVESTIMENTO DIVIDIDA POR 1.5 EM PPcsg
		v.save_PPcsg = f.varSaida.PcsgB/1.5;
		//LIMITE PARA QUE O BUILDUP SEJA FINALIZADO
		//Danielson: comentei por usar a variavel Vqpl e Vqpg que foram excluidas,
		//assim como comentei o if depois do bypasscontroller que usa limite.
//		v.limite = (f.tubing.Lcauda - f.tempos.Ltbg) * (60*3.2808)/ f.pistao.Vqpg + ((v.Ppart_csg - v.Ppart_tbg) * (60*3.2808)/(c.ROliq * c.G * f.pistao.Vqpl));
		//INICIA A ITERACAO COM M = 1 E AUMENTA A CADA ITERACAO
		//Enquanto (não tiver chegado no tempo de offtime ou
		//(estiver passando pelo controle e o pistao ainda nao chegou no fundo)) e
		//pedido de alteração de válvula motora

		for( v.m = 1; ( tpgasto < f.tempos.Offtime ||
									 (!byPassController && f.varSaida.Hplg > 0) ) &&
									 (!this.alterarValvula);
									 v.m++ ) {
			//PARTE DO CONTROLADOR PARA SABER SE CHEGOU A HORA DE ABRIR A VALVULA
			if ( !byPassController ) {
				/*if ( v.contador == 0 && (f.varSaida.PtbgT - v.delta_P - f.linhaPro.Psep)/6894.757 >= v.CP && c._step * v.m > v.limite) {
					break;
				} */
					if(v.contador == 1 && c._step * v.m >= f.tempos.Offtime && v.v0 == 0) {
						break;
				}
			}
			///////////////////////////////////////////////////
			if ( v.m >= 100) {
				periodoAmostragem = 120;
			}
			///////////////////////////////////////////////////
			v.save_v0 = v.v0;
			// Parte modificada
			// Cálculos intermediários para facilitar compreensão das equações
			//double eqk = (3*M_PI*f.pistao.Dplg + 17600*M_PI*f.pistao.Dplg*f.pistao.Lplg)*0.0072;
			double eqt = c._step*v.m;
			double DplgEsf = pow(pow(f.pistao.Dplg,2) * f.pistao.Lplg * 1.5, 0.3333);
			//Se pistão estiver no gás
			if ( f.varSaida.Hplg > f.tempos.Ltbg ) {
				//double eqFgas = f.pistao.Mplg*c.G - c.ROgas*c.G*pow(f.pistao.Dplg,2)*(M_PI/4)*f.pistao.Lplg;
				double visgas = ue.VISGAS(ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), c.ROgas);
				double eqk = 3 * M_PI * DplgEsf * visgas + 10;
				double eqFgas = f.pistao.Mplg*c.G - c.ROgas*c.G*pow(DplgEsf,3)*(M_PI/6);
				if ((f.varSaida.Hplg - f.tempos.Ltbg)>(f.tubing.Lcauda - f.tempos.Ltbg)*0.15) {
					v.v0 = -(eqFgas/eqk)*(1 - exp(-eqk*eqt/f.pistao.Mplg));
				}
				else{
				eqt = c._step*v.m*0.01;
				v.v0 = -(eqFgas/eqk)*(1 - exp(-eqk*eqt/f.pistao.Mplg));
				}
				modo_passo = 0;
			}
			//Se pistão estiver no Líquido
			else if ( f.varSaida.Hplg < f.tempos.Ltbg && f.varSaida.Hplg > 0 ) {
				double visliq = ue.VISC(ue.TEMP(f.tempos.Ltbg));
				double eqk = 3 * M_PI * DplgEsf * visliq + 300;
				//double eqFliq = f.pistao.Mplg*c.G - c.ROliq*c.G*pow(f.pistao.Dplg,2)*(M_PI/4)*f.pistao.Lplg;
				double eqFliq = f.pistao.Mplg*c.G - c.ROliq*c.G*pow(DplgEsf,3)*(M_PI/6);
				v.v0 = -(eqFliq/eqk)*(1 - exp(-eqk*eqt/f.pistao.Mplg));
				modo_passo = 2;
			}
			//Se pistão estiver no fundo
			else if ( f.varSaida.Hplg == 0 ) {
				v.v0 = 0;
				modo_passo = 2;
			}
			// Testa se o pistão está na interface do líquido
			if ( f.tempos.Ltbg - f.varSaida.Hplg < f.pistao.Lplg &&
					 (f.tempos.Ltbg - f.varSaida.Hplg) > 0 ) {
		//Calcula velocidade na interface
				double visgas = ue.VISGAS(ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), c.ROgas);
				double visliq = ue.VISC(ue.TEMP(f.tempos.Ltbg));
				double eqkG = 3 * M_PI * DplgEsf * visgas + 10;
				double eqkL = 3 * M_PI * DplgEsf * visliq + 300;
				double eqFgas = f.pistao.Mplg*c.G - c.ROgas*c.G*pow(DplgEsf,3)*(M_PI/6);
				double eqFliq = f.pistao.Mplg*c.G - c.ROliq*c.G*pow(DplgEsf,3)*(M_PI/6);
				double porcentagliq = (f.tempos.Ltbg - f.varSaida.Hplg)/(f.pistao.Lplg);

				v.v0 = porcentagliq*(-(eqFliq/eqkL)*(1 - exp(-eqkL*eqt/f.pistao.Mplg))) +
								(1 - porcentagliq)*(-(eqFgas/eqkG)*(1 - exp(-eqkG*eqt/f.pistao.Mplg)));
				// muda passo de integração
				modo_passo = 1;
				// Força a plotagem de pontos enquanto essa condição for verdadeira
				this.quantidadePontos = this.periodoAmostragem + 1;
			}

			if ( abs(f.tempos.Ltbg - f.varSaida.Hplg) < f.pistao.Lplg ) {
				// muda passo de integração
				modo_passo = 1;
				// Força a plotagem de pontos enquanto essa condição for verdadeira
				this.quantidadePontos = this.periodoAmostragem + 1;
			}
			//CALCULA A DISTÂNCIA ENTRE A SUPERFÍCIE E O PISTÃO
			v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
			v.LtbgX = f.tempos.Ltbg;
			/*
			// Fim de Parte modificada
			if((int)(v.m * c._step *10) % 10==0){
				// DANIELSON: Atribuições da variável v0(velocidade do pistao) dependendo
				//da localização que ele está.
				if ( f.varSaida.Hplg > f.tempos.Ltbg ) {
					v.v0 = - f.pistao.Vqpg/(60*3.2808);
				}
				else if ( f.varSaida.Hplg < f.tempos.Ltbg && f.varSaida.Hplg > 0 )

					v.v0 = -(f.pistao.Vqpl - (f.varSaida.Qlres /( c.AItbg*86400)))/(60*3.2808);
				else if ( f.varSaida.Hplg == 0 )
					v.v0 = 0;

				//CALCULA A DISTÂNCIA ENTRE A SUPERFÍCIE E O PISTÃO
				v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
				v.LtbgX = f.tempos.Ltbg;
			} /* fim do IF BUILDUP - linha 1453 */

			v.temp = (v.flag == 0 ? f.tempos.Ltbg + f.tempos.Lslg : (v.flag == 1 ? v.LtbgZ : v.LtbgY) );
			//CALCULA O VOLUME DA COLUNA DE PRODUÇÃO ACIMA DA COLUNA DE LÍQUIDO DEPENDENDO DA VARIÁVEL temp
			v.Vt = c.AItbg * (f.tubing.Lcauda - v.temp);
			//CALCULA A TEMPERATURA MÉDIA NO TUBING BASEADA NA TEMPERATURA DA SUPERFÍCIE
			v.TTt= (ue.TEMP(f.tubing.Lcauda - v.temp) + c.Tsup)/2.0;
			//INICIALIZA CONTADOR
			v.y=0;

			do{
				//ARMAZENA PPt DA ITERAÇÃO ANTERIOR EM pp
				f.varSaida.pp = v.save_PPt;
				//ATUALIZA PPt PARA A PRESSÃO NA BASE DO REVESTIMENTO
				v.PPt = f.varSaida.PcsgB;
				//LAÇO FOR (SE O VALOR ABSOLUTO DA DIFERENÇA ENTRE AS PRESSÕES MÉDIAS DO TUBING DAS ITERAÇÕES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATÉ 20)
				for( v.u = 0; abs(v.PPt - f.varSaida.pp) > 1.0 && v.u < 20; v.u++ ) {
					//ATUALIZA A PRESSÃO MÉDIA DO TUBING
					v.PPt = f.varSaida.pp;
					//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O GÁS NO TUBING
					v.z = ue.Z(v.PPt/ c.Ppc, v.TTt/ c.Tpc);
					//CALCULA A PRESSÃO MÉDIA DO GÁS NO TUBING
					f.varSaida.pp = v.z * v.Nt * c.R * v.TTt/ v.Vt;
				}
				//ARMAZENA A PRESSÃO MÉDIA NO TUBING DESTA ITERAÇÃO
				v.save_PPt = f.varSaida.pp;
				//CALCULA A PRESSÃO NA BASE DO TUBING LEVANDO EM CONTA A COLUNA DE LÍQUIDO DE COMPRIMENTO DEFINIDO PELA VARIÁVEL temp
				v.Pbt = ( f.varSaida.pp * 2)/(1 + exp(-(c.PM * c.G * (f.tubing.Lcauda - v.temp))/(v.z * c.R * v.TTt)));
				//SOMA À PRESSÃO NA BASE DO TUBING O PESO DA COLUNA DE LÍQUIDO DE COMPRIMENTO DEFINIDO PELA VARIÁVEL temp
				v.Pbt += c.ROliq * c.G * v.temp;
				//ARMAZENA EM pp A PRESSÃO MÉDIA DO REVESTIMENTO DA ITERAÇÃO ANTERIOR
				f.varSaida.pp = v.save_PPcsg;
				//ATUALIZA A PRESSÃO MÉDIA NO REVESTIMENTO COM O VALOR DA PRESSÃO NA BASE DO REVESTIMENTO
				v.PPcsg = f.varSaida.PcsgB;
				//LAÇO for (SE O VALOR ABSOLUTO DA DIFERENÇA ENTRE AS PRESSÕES MÉDIAS DO REVESTIMENTO DAS ITERAÇÕES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATÉ 20)
				for(v.u = 0; abs(v.PPcsg - f.varSaida.pp) > 1.0 && v.u<20; v.u++){
					//ATUALIZA A PRESSÃO MÉDIA DO REVESTIMENTO
					v.PPcsg = f.varSaida.pp;
					//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O GÁS NO REVESTIMENTO
					v.z = ue.Z(v.PPcsg/ c.Ppc, c.TTcsg/ c.Tpc);
					//PRESSÃO MÉDIA DO GÁS NO REVESTIMENTO
					f.varSaida.pp = v.z*v.Na*c.R*c.TTcsg/ c.Vcsg;
				}
				//ARMAZENA A PRESSÃO MÉDIA DO REVESTIMENTO DESTA ITERAÇÃO EM PPcsg
				v.save_PPcsg = f.varSaida.pp;
				//CALCULA A PRESSÃO NA BASE DO ESPAÇO ANULAR
				v.Pba = (f.varSaida.pp*2)/(1 + exp(-(c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg)));
				//SE O VALOR ABSOLUTO DA DIFERENÇA ENTRE AS PRESSÕES NA BASE DO TUBING E NA BASE DO ESPAÇO ANUAR É MENOR OU IGUAL A 1 ENTÃO ENCERRA O PROGRAMA
				if ( abs(v.Pbt - v.Pba) <= 1.0 ) {
					break;
				}
				//SENÃO,SE O CONTADOR Y ESTIVER NA PRIMEIRA ITERAÇÃO
				else if(v.y == 0){
					//ARMAZENA NA VARIÁVEL Nt_ O NÚMERO DE MOLES DO GÁS NO TUBING
					v.Nt_ = v.Nt;
					//ARMAZENA NA VARIÁVEL F_ A DIFERENÇA ENTRE AS PRESSÕES NAS BASES DO ESPAÇO ANULAR E DO TUBING
					v.F_ = v.Pbt - v.Pba;
					//SE A PRESSÃO NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ESPAÇO ANULAR O NÚMERO DE MOLES DO GÁS É MULTIPLICADO POR 0,5. RECALCULA O NÚMERO DE MOLES DO GÁS NO ESPAÇO ANULAR
					if ( v.Pbt > v.Pba ) {
						v.Nt /= 0.5;
						v.Na = v.N - v.Nt;
					}
					//O NÚMERO DE MOLES DO GÁS NO TUBING É MULTIPLICADO POR 1,5
					else {
						v.Nt *= 1.5;
						v.Na = v.N - v.Nt;
					}

				}
				else {
					//A VARIÁVEL Ntt RECEBE O NÚMERO DE MOLES DE GÁS NO TUBING
					v.Ntt = v.Nt;
					//RECALCULA O NÚMERO DE MOLES DE GÁS NO TUBING ATRAVÉS DE UMA EXPRESSÃO EMPÍRICA
					v.Nt = v.Nt - (v.Pbt - v.Pba)*(v.Nt_ - v.Nt)/(v.F_ - (v.Pbt - v.Pba));
					//RECALCULA O NÚMERO DE MOLES DO GÁS NO ESPAÇO ANULAR
					v.Na = v.N - v.Nt;
					//A VARIÁVEL Nt_ RECEBE A VARIÁVEL Ntt
					v.Nt_ = v.Ntt;
					//ARMAZENA NA VARIÁVEL F_ A DIFERENÇA ENTRE AS PRESSÕES NAS BASES DO ESPAÇO ANULAR E DO TUBING
					v.F_ = v.Pbt - v.Pba;
				}
				//INCREMENTA O CONTADOR
				v.y++;
			} while ( v.y < 150 );
			//ARMAZENA O NÚMERO DE MOLES DO GÁS NO ESPAÇO ANULAR
			v.save_Na = v.Na;
			//ATRIBUI À PRESSÃO DE FLUXO NO FUNDO A PRESSÃO NA BASE DO REVESTIMENTO,QUE É IGUAL À PRESSÃO NA BASE DO TUBING
			v.Pwf = f.varSaida.PcsgB = v.Pbt;
			//CALCULA A PRESSÃO NO TOPO DO REVESTIMENTO
			f.tempos.PcsgT = (v.save_PPcsg * 2)/(1 + exp((c.PM * c.G * (f.tubing.Lcauda))/(v.z * c.R * c.TTcsg)));
			//CALCULA A PRESSÃO NA BASE DA COLUNA DE PRODUÇÃO,QUE É A PRESSÃO NA BASE DO TUBING MENOS AS PRESSÕES DEVIDO AO PESO DA COLUNA DE LÍQUIDO,CUJO COMPRIMENTO FOI DEFINIDO NA VARIÁVEL temp, E AO PESO DO PISTÃO
			v.PtbgB = v.Pbt - (c.ROliq * c.G * v.temp + f.pistao.Mplg * c.G/ c.AItbg);

			//CALCULA A PRESSÃO NO TOPO DA COLUNA DE PRODUÇÃO,QUE É PRESSÃO NO TOPO DO REVESTIMENTO MENOS AS PRESSÕES DEVIDO AO PESO DA COLUNA DE LÍQUIDO,CUJO COMPRIMENTO FOI DEFINIDO NA VARIÁVEL temp, E AO PESO DO PISTÃO
			// ALTER: Foi trocado PcsgT por PcsgB até confirmação de troca
			//f.varSaida.PtbgT = f.tempos.PcsgT - (c.ROliq * c.G * v.temp + f.pistao.Mplg * c.G/ c.AItbg);
			f.varSaida.PtbgT = ue.GASOSTT(v.PtbgB,ue.TEMP(f.tubing.Lcauda - v.temp), c.Tsup, f.tubing.Lcauda - v.temp );
			//CALCULA A VAZÃO DE LÍQUIDO DO REVESTIMENTO
			f.varSaida.Qlres = c.Qmax * (1 - .2*(v.Pwf/ f.reservat.Pest) - .8*pow(v.Pwf/ f.reservat.Pest,2));
			//INCREMENTA O NÚMERO DE MOLES TOTAL,ARMAZENADO NA VARIÁVEL N
			v.N += c._step * (((f.varSaida.Qlres * f.reservat.RGL/86400)) * c.Pstd)/(c.R * c.Tstd);
			//INCREMENTA O COMPRIMENTO DA COLUNA DE LÍQUIDO FORMADA NO FUNDO
			f.tempos.Ltbg += (c._step * f.varSaida.Qlres/86400)/(c.AItbg);// * 86400);
			if(v.flag == 1)
				v.LtbgZ += c._step * f.varSaida.Qlres/(c.AItbg * 86400);
			if(v.flag == 2)
				v.LtbgY += c._step * f.varSaida.Qlres/(c.AItbg * 86400);
			// PARTE MODIFICADA
			//SE O PISTÃO NÃO ESTIVER NO FUNDO
			//DANIELSON: Aqui que vai mudar alguma coisa
			// Como vem a variável Hplg e você calculou delta_v, delta_v seria calculado e substituido
			//	 pelo f.pistao.VqpX?
			if(f.varSaida.Hplg > 0) {
				v.delta_h = v.save_v0*c._step + (v.delta_v/2)*c._step;
				f.varSaida.Hplg += v.delta_h;
			}
			// FIM DE PARTE MODIFICADA
			/* CÓPIA DE SEGURANÇA
			//SE O PISTÃO NÃO ESTIVER NO FUNDO
			//DANIELSON: Aqui que vai mudar alguma coisa
			// Como vem a variável Hplg e você calculou delta_v, delta_v seria calculado e substituido
			//	 pelo f.pistao.VqpX?
			if(f.varSaida.Hplg > 0){
				//SE O PISTÃO ESTIVER ACIMA DO TOPO DA COLUNA DE LÍQUIDO FORMADA NO FUNDO DECREMENTA A POSIÇÃO DESTE FATOR
				if ( f.varSaida.Hplg > f.tempos.Ltbg ) {
					f.varSaida.Hplg -= (c._step * f.pistao.Vqpg/(60 * 3.2808));
				}
				//SE O PISTÃO ESTIVER ABAIXO DO TOPO DA COLUNA DE LÍQUIDO FORMADA NO FUNDO DECREMENTA A POSIÇÃO DO PISTÃO DESTE OUTRO FATOR
				if ( f.varSaida.Hplg < f.tempos.Ltbg ) {
					f.varSaida.Hplg -= (c._step * (f.pistao.Vqpl - (f.varSaida.Qlres/(c.AItbg * 86400)))/(60 * 3.2808));
				}
			}*/
			//EVITA QUE A POSIÇÃO DO PISTÃO ASSUMA VALORES NEGATIVOS
			if ( f.varSaida.Hplg < 0 ) {
				f.varSaida.Hplg = 0;
			}
			if ( v.flag == 0 && (f.tempos.Ltbg - f.varSaida.Hplg) >= 0) {
				v.LtbgY += f.tempos.Ltbg;
				v.flag = 2;
			}
			// PARTE DO CONTROLADOR (MUDA O TEMPO DE FECHAMENTO DA VALVULA)
			if ( !byPassController ) {
				v.temp_Offtime = (int)(c._step * v.m);
			}

			//ENVIA MENSAGEM PARA PLOTAR A SITUACAO ATUAL
			//criarMensagem(CycleStage.BUILDUP);

			tpgasto += c._step;
			switch (modo_passo) {
				case 0:
					c._step = c._stepGas;
					break;
				case 1:
					c._step = c._stepGas2Liq;
					break;
				case 2:
					c._step = c._stepLiq;
					break;
				default:
					continue;
			}

		}/*  fim do FOR (shut-in) OFF: - linha 1429  */
		this.alterarValvula = false;
		//FORÇANDO O PLOTE DO ULTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
			//criarMensagem(CycleStage.BUILDUP);
		}

		//ATUALIZA O NÚMERO TOTAL DE MOLES SOMENTE COM O NÚMERO DE MOLES DO ESPAÇO
		// ANULAR
		v.Ntotal = v.Na;
		// PARTE DO CONTROLADOR (MUDA O TEMPO DE FECHAMENTO DA VALVULA)
		if ( !byPassController ) {
			//INCREMENTA temp_Offtime
			v.temp_Offtime++;

			//O TEMPO DE Offtime RECEBE temp_Offtime
			f.tempos.Offtime = v.temp_Offtime;
		}
		//O COMPRIMENTO DA GOLFADA É ATUALIZADO, MAS BASEADO NA FLAG
		// ALTER: alterar para uma condição IF correspondente
		//f.tempos.Lslg = (v.flag == 1 ? v.LtbgZ : v.LtbgY);
		/*switch ( v.flag ) {
			case 1:
				f.tempos.Lslg = v.LtbgZ;
				break;
			default:
				f.tempos.Lslg = v.LtbgY;
				break;
		}*/
		// O pistao chegou na superficie?
		if ( ChegouSup ) {
			// O pistao não chegou no liquido do fundo da coluna?
			if ( f.varSaida.Hplg > f.tempos.Ltbg ) {
				f.tempos.Lslg = 0;
			}
			// O pistao chegou no liquido do fundo da coluna
			else {
				f.tempos.Lslg = f.tempos.Ltbg - f.varSaida.Hplg;
				f.tempos.Ltbg -= f.tempos.Lslg;
			}
		}
		// Caso o pistao nao tenha chegado na superficie
		else {
			// O pistao chegou no liquido do fundo da coluna?
			if ( f.varSaida.Hplg < f.tempos.Ltbg ) {
				f.tempos.Lslg = f.tempos.Lslg + f.tempos.Ltbg - f.varSaida.Hplg;
				f.tempos.Ltbg -= f.tempos.Ltbg - f.varSaida.Hplg;
			}
		}

		// INFORMA QUE A ETAPA QUE ACABOU FOI A DO BUILDUP
		c.estagio = c.OFF_BUILD_UP;

		//Enviando o tempo de duração do buildup
		enviarVarCiclo(CycleStage.STAGE_BUILDUP_DURATION, c._step * v.m);

		//Enviando o tempo de duração do ciclo atual(SUBIDA + PRODUCAO + AFTERFLOW
		// + BUILDUP)
		enviarFimCiclo( v.i * c.step +  v.j * c.step_ + v.k * c.step + v.m * c._step );
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Função que seta o ID da simulação.
	 */
	public void setIdSimulacao(int id){
		this.idSimulacao = id;
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Função que retorna o ID da simulação.
	 */
	public int getIdSimulacao() {
		return idSimulacao;
	}
	
	/**
	 * @brief Seta a precisão de um double na quantidade de casas decimais.
	 * @param x Número para ser ajustado.
	 * @param precisao Precisão do double resultante.
	 * @return Retorna o valor X com PRECISAO casas decimais.
	 */
	public double setPrecision(double x, int precisao) {
		//Para pegar somente a precisao, multiplica o numero X por 10 elevado a
		//precisao e então transforma em inteiro. Com o numero inteiro, divide por
		//10 elevado a precisao e depois da virgula tera somente precisao numeros.
		return (((int)(x * pow(10,precisao)))/pow(10,precisao));
	}
	/**
	 * @brief Função que trata de enviar um dado para ser inserido no histórico
	 * 				mostrado em uma das abas na interface a cada ciclo.
	 * @param ciclovar estágio do ciclo atual ou variável de ciclo
	 * @param valor Valor que deve ser enviado caracterizando a ocorrência.
	 */
	public void enviarVarCiclo(CycleStage ciclovar, double valor) {
		
	}
	/**
	 * @brief Função que trata de informar a interface que chegou ao fim do ciclo
	 * 				em determinado tempo.
	 * @param cycle Tempo em que o ciclo foi finalizado.
	 */
	public void enviarFimCiclo(double cycle) {		

	}
	/**
	 * @brief Função que recebe o pedido de alteração no estado da válvula motora.
	 */
	public void alterValvula() {
		this.alterarValvula = true;
	}

}
