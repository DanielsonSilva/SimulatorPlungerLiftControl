package algorithm;

import static java.lang.Math.*;

import java.io.PrintWriter;

/**
 * Simulation Methods
 * @author Danielson Fl�vio Xavier da Silva
 *
 */
public class Simulation {
	
	/** Guarda o tempo de simula��o */
	public double tempo;
	/** Identifica a simula��o (n�o usada) */
	public int idSimulacao;
	/** Identifica a simula��o (n�o usada) */
	public int idRamoSimulacao;
	/** Guarda o n�mero de pontos descartados para comparar com a amostragem */
	public int quantidadePontos;
	/** Mostra quantos pontos s�o descartados para se enviar um ponto para
			interface */
	public int periodoAmostragem;
	/** Tamanho da fila de espera de pontos para ser enviado para a interface */
	public int bufferSendPoints;
	/** Mostra se passa ou n�o pelo controle no algoritmo */
	public boolean byPassController;
	/** for�a o envio do ponto inicial da etapa */
	public boolean forcarPontosI;
	/** for�a o envio do ponto final da etapa */
	public boolean forcarPontosF;
	/** Vari�vel que iria para desenho do processo */
	public double M_PI;
	/** Par�metro que verifica se o pedido de altera��o da v�lvula motora foi
			feito ou n�o */
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
	 * Inicia os par�metros para poder come�ar a simula��o.
	 */
	public void iniciarSimulacao() {

		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		// Vari�veis de controle de passos de integra��o
		//c->_stepGas2Liq = 0.01;//valor antigo = 0.001
		//c->_stepGas = 0.1;//valor antigo = 0.01
		//c->_stepLiq = 0.1;//valor antigo = 0.005

		v.CP   = 30;   //Controle de Press�o (Press�o cr�tica)
		c.Fast = 5.5;  //Velocidade do pistao para ser considerada r�pida
		c.Slow = 4.5;  //Velocidade do pistao para ser considerada lenta

		//Raz�o de �gua no l�quido produzido
		c.FW = f.fluido.BSW/100;

		/*
		 * Specific Gravity do �leo retirada a partir da API
		 * a f�rmula pode ser obtida em
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
		c.Tpc = ((168 + 325*f.fluido.SGgas - 12.5*pow(f.fluido.SGgas,2))-491.67) * 5.0/9.0 + 273.15;

		//VAZAO MAXIMA DO RESERVATORIO
		c.Qmax = f.reservat.Qteste/(1-0.2*f.reservat.Pteste/f.reservat.Pest - 0.8*pow(f.reservat.Pteste/f.reservat.Pest,2));

		//Casing - �rea Interna
		c.AIcsg = 3.14*(pow(f.casing.DIcsg,2)-pow(f.tubing.DOtbg,2))/4;

		//Casing - Volume interno
		c.Vcsg = c.AIcsg * f.tubing.Lcauda;

		//Press�o na base do Anular
		f.varSaida.PcsgB = ue.GASOSTB(f.tempos.PcsgT, c.Tsup,ue.TEMP(f.tubing.Lcauda),f.tubing.Lcauda);

		//Temperatura m�dia do Anular
		c.TTcsg=( ue.TEMP ( f.tubing.Lcauda ) + c.Tsup)/2;

		//Inicializa n�mero de moles do g�s
		v.Ntotal = 0;

		//Iniciando Contadores
		v.cont = 0;
		v.count = 0;
		v.contador = 1;

		//Iniciar as vari�veis em 0 (zero)
		f.varSaida.Qlres = 0;
		f.varSaida.PtbgT = 0;
		v.PtbgB = 0;
		f.varSaida.Hplg = 0;
		v.v0 = 0;
		f.tempos.Ltbg = 0;

		//Pressao na base do Tubing = Pressao na base do casing -
		//( densidade*gravidade*comprimento da golfada +
		//a massa do pistao pela raz�o entre a gravidade e a �rea interna do tubing
		v.PtbgB = f.varSaida.PcsgB - ( c.ROliq * c.G * f.tempos.Lslg + f.pistao.Mplg * c.G/c.AItbg);

		//Pressao no topo do tubing pela equacao GASOSTT
		f.varSaida.PtbgT = ue.GASOSTT(v.PtbgB,ue.TEMP(v.H), c.Tsup, v.H);

		//Passando o valor da entidade para variaveis auxiliares
		v.temp_Offtime = f.tempos.Offtime;

		//Op��o de esperar o pistao
		byPassController = !(f.tempos.Controller);

		//O Estagio e setado como BuildUp para que o inicioCiclo() seja iniciado
		c.estagio = c.OFF_BUILD_UP;
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Fun��o que inicia o ciclo ajustando os par�metros para come�ar um
	 *				novo ciclo.
	 * @return True se ocorreu a fun��o com sucesso, false caso contr�rio.
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
		//PPcsg - Press�o m�dia no casing
		v.PPcsg = (f.tempos.PcsgT + f.varSaida.PcsgB)/2;
		//SE O NUMERO DE MOLES AINDA NAO FOI INICIADO
		if ( v.Ntotal <= 0.0001 ) {
			//INICIALIZA O NUMERO DE MOLES COM O CALCULO
			v.Ntotal = (v.PPcsg * c.Vcsg)/(ue.Z(v.PPcsg/c.Ppc, c.TTcsg/c.Tpc) * c.R * c.TTcsg);
			v.N = v.Ntotal;
		}
		else {
			//ARMAZENA O NUMERO DE MOLES ATUAL EM N
			v.N = v.Ntotal;
		}
		//Posi��o (altura) inicial do pist�o (m)
		//f.varSaida.Hplg = 0.0;
		//H profundidade do topo do slug (m)
		v.H = f.tubing.Lcauda - f.varSaida.Hplg - f.pistao.Lplg - f.tempos.Lslg;

		// Se o poco afogou acaba funcao
		if( v.PtbgB <= 0.00001 ) {
			return false;
		}

		//PP - pressao media no tubing (Pa)
		v.PP = (f.varSaida.PtbgT + v.PtbgB)/2;
		//TT temperatura media (K)
		v.TT = (ue.TEMP(v.H) + c.Tsup)/2.0;
		//Z Fator de compressibilidade m�dio
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

		//Press�o m�dia do g�s acima da slug, com z=0.98
		f.varSaida.pp = 0.98 * v.nn * c.R * v.TT/v.V;

		//Atualiza a press�o m�dia e fator de compressibilidade para o g�s acima do slug
		do{
			v.PP = f.varSaida.pp;
			v.z = ue.Z(v.PP/c.Ppc, v.TT/c.Tpc);
			f.varSaida.pp = v.z * v.nn * c.R * v.TT/v.V;
		} while ( abs(v.PP - f.varSaida.pp) > 1.0 );

		//Inicializa press�o no topo do tubing descontando a influ�ncia do pist�o
		f.varSaida.PtbgT = ( f.varSaida.pp * 2 ) / ( 1 + exp( ( c.PM * c.G * ( f.tubing.Lcauda - f.tempos.Lslg - f.varSaida.Hplg ))/( v.z * c.R * v.TT ) ));
		//INICIALIZA O CONTADOR
		v.y = 0;
		do {
			v.Pt = f.varSaida.PtbgT;
			v.qq = (v.q + ue.QSC(v.Pt/1000.0, f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup))/2.0;
			v.nn = v.n - ((v.qq/86400 * v.Transient) * c.Pstd)/(c.R * c.Tstd);
			f.varSaida.pp = 0.98 * v.nn * c.R * v.TT/v.V;
			do {
				v.PP = f.varSaida.pp;
				v.z = ue.Z(v.PP/c.Ppc, v.TT/c.Tpc);
				f.varSaida.pp = v.z * v.nn * c.R * v.TT/v.V;
			} while( abs(v.PP - f.varSaida.pp) > 1.0);
			f.varSaida.PtbgT = (f.varSaida.pp * 2)/(1 + exp((c.PM * c.G *(f.tubing.Lcauda - f.tempos.Lslg - f.varSaida.Hplg))/(v.z * c.R * v.TT)));
		} while( abs(f.varSaida.PtbgT - v.Pt) > 1.0 && v.y++ < 50);
		//CALCULA A DIFERENCA DE PRESSAO NO TOPO DO TUBING E NO SEPARADOR
		v.fatorT = f.varSaida.PtbgT - f.linhaPro.Psep;
		//CALCULA A DIFERENCA DA PRESSAO NA BASE DO TUBING E A COLUNA DE GAS
		v.fatorB = v.PtbgB - ue.GASOSTB(f.linhaPro.Psep, c.Tsup, ue.TEMP(v.H), v.H);

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
		f.tempos.Ltbg = 0;
		//INICIA A FLAG
		v.flag = 0;

		//INDICA O ESTAGIO QUE FOI FINALIZADO
		c.estagio = c.INICIO_CICLO;

		return true;
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Modelo matem�tico da parte de subida do pist�o.
	 */
	public void subidaPistao() {
		//CRIACAO DE VARIAVEIS
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();
		
		//FOR�AR A PLOTAGEM DO PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		//ALTER: Afirmando que delta h antes da subida � sempre igual a zero
		v.delta_h = 0;
		/* Abertura da Valvula Motora */
		for( v.d = 0, v.i = 1; (v.H - v.delta_h) > 0 &&
				(f.tempos.Ontime - v.i * c.step - v.Transient) > 0 &&
				(!this.alterarValvula); v.i++ ) {
			//System.out.println("Pri cond > 0: " + (v.H - v.delta_h) );
			//System.out.println("Seg cond > 0: " + (f.tempos.Ontime - v.i * c.step - v.Transient) );
			if( (int)(v.i * c.step * 10) % 10 == 0) {
				v.templ = 0.0 /*Lcauda-Hplg*/;
				if ( v.flag == 0 ) {
					v.LtbgX = f.tempos.Ltbg;
				}
				else {
					v.LtbgX = v.LtbgZ;
				}
			}
			v.qqq = ue.QSC(f.varSaida.PtbgT/1000.0, f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);
			//ATRIBUI � VARI�VEL N O N� M�DIO DE MOLES DO G�S ACIMA DA GOLFADA
			v.n = v.nn;
			//SE O N� DE MOLES M�DIO(nn) FOR MENOR DO QUE O VALOR DO N� DE MOLES
			//CALCULADO (COM TEMPERATURA M�DIA (tt) NA SUPERF�CIE) O VALOR DE N �
			//ATUALIZADO COM A TEMPERATURA.
			v.temp = f.linhaPro.Psep * v.V/(v.z * c.R * v.TT);
			if( v.n < v.temp ) {
				v.n = v.temp;
			}
			//CONTADOR PARA O DO-WHILE
			v.y = 0;
			
			//LA�O DO-WHILE (SE A DIFEREN�A ENTRE AS PRESS�ES NO TUBING NA SUPERF�CIE DA
			// ITERA��O ATUAL E PASSADA DOR MAIOR QUE 1 E ATINGIR 50 ITERA��ES)
			do {
				// Incrementar o valor de Y
				v.y = v.y + 1;
				//System.out.println("Chegou nessa parte 5");
				//PRESS�O DO TUBING NA SUPERF�CIE
				v.Ptt = f.varSaida.PtbgT;
				//VAZ�O DE G�S NA LINHA DE SURG�NCIA
				v.q = ue.QSC(f.varSaida.PtbgT/1000.0, f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);
				if ( v.q <= 0.00001 ) {
					break;
				}
				//VAZ�O M�DIA NA LINHA DE SURG�NCIA(O VALOR DE q MUDA A CADA PASSO)
				v.qq = (v.qqq + v.q)/2;
				//C�LCULO DO N�MERO DE MOLES M�DIO CALCULADO NA PRESS�O E TEMPERATURA PADR�ES
				v.nn = v.n - c.step * ((v.qq/86400) * c.Pstd)/(c.R * c.Tstd);
				//SE O N� DE MOLES M�DIO CALCULADO ANTERIORMENTE FOR MENOR QUE O N� DE MOLES DO G�S ACIMA DA GOLFADA,LEVANDO EM CONTA A PRESS�O DO SEPARADOR, ENT�O O N� DE MOLES RECEBE O VALOR DE TEMP
				v.temp = f.linhaPro.Psep * v.V/(v.z * c.R * v.TT);
				if( v.nn < v.temp ) {
					v.nn = v.temp;
				}
				v.ppp = f.varSaida.pp;
				//RECALCULA PRESS�O M�DIA(pp)DO G�S ACIMA DO TOPO DA GOLFADA USANDO Z=0.98
				f.varSaida.pp = 0.98 * v.nn * c.R * v.TT/v.V;
				//LA�O DO-WHILE (SE A DIFEREN�A ENTRE OS pp DAS ITERA��ES PRESENTE E PASSADA � MAIOR QUE 1)
				do {
					//SALVA A PRESS�O M�DIA DO G�S ACIMA DA GOLFADA NA VARI�VEL PP
					v.PP = f.varSaida.pp;
					//AJUSTA O FATOR DE COMPRESSIBILIDADE Z
					v.z = ue.Z( v.PP/c.Ppc, v.TT/c.Tpc );
					//RECALCULA pp
					f.varSaida.pp = v.z * v.nn * c.R * v.TT/v.V;
					v.cont3++;
					if ( v.cont3 > 50 ) {
						v.PP = f.varSaida.pp;
					}
				} while( abs(v.PP - f.varSaida.pp) > 1.0 );
				v.cont3 = 0;
				//CALCULA A PRESS�O NO TUBING NA SUPERF�CIE
				f.varSaida.PtbgT = v.Ptt - (v.ppp - f.varSaida.pp) * 2 * v.fatorT/(v.fatorT + v.fatorB);
			} while( abs( v.Ptt - f.varSaida.PtbgT) > 1.0 && v.y <= 50);
			//CALCULA A PRESS�O NA BASE DA COLUNA
			v.PtbgB = (f.varSaida.pp * 2)/(1 + exp(- (c.PM * c.G * (f.tubing.Lcauda - f.tempos.Lslg - f.varSaida.Hplg))/(v.z * c.R * v.TT)) );
			//FATOR T:DIFEREN�A ENTRE AS PRESS�ES DO TOPO DA COLUNA E DO SEPARADOR
			v.fatorT = f.varSaida.PtbgT - f.linhaPro.Psep;
			//FATOR B:DIFEREN�A ENTRE AS PRESS�ES NA BASE DA COLUNA E DA BASE DA COLUNA DE G�S DE COMPRIMENTO H (DO TOPO TUBING AO TOPO DA GOLFADA)LEVANDO EM CONTA A PRESS�O EM CONTA A PRESS�O DO SEPARADOR A TEMPERATURA DA SUPERF�CIE
			v.fatorB = v.PtbgB - ue.GASOSTB(f.linhaPro.Psep, c.Tsup, ue.TEMP(v.H), v.H);

			//SE O PIST�O N�O EST� NO FUNDO DO PO�O
			if ( f.varSaida.Hplg >= 0.0 ) {
				//System.out.println("Chegou nessa parte 6");
				//PERDA DE PRESS�O A JUSANTE: LEVA EM CONTA A PRESS�O NA BASE DO TUBING,A PERDA POR PESO DA COLUNA DE L�QUIDO E A PERDA DE�PRESS�O NO PIST�O
				v.PplgJ = v.PtbgB + f.tempos.Lslg * c.ROliq * c.G + f.pistao.Mplg * c.G/c.AItbg;
				//ARMAZENA A POSI��O DA BASE DO PIST�O
				v.save_Hplg = f.varSaida.Hplg;
				//ARMAZENA A VELOCIDADE DA GOLFADA
				v.save_v0 = v.v0;
				//SE FOR A PRIMEIRA ITERACAO
				if( v.i == 1 ){
					//PRESS�O NO PLUNGER A MONTANTE,ACIMA DA COLUNA DE L�QUIDO FORMADA NO FUNDO OU NO TOPO DA COLUNA DE G�S ABAIXO DO PIST�O
					v.PplgM = ue.GASOSTT(f.varSaida.PcsgB - c.ROliq * c.G * f.tempos.Ltbg, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg), f.varSaida.Hplg - f.tempos.Ltbg);
					//ARMAZENA A PRESS�O DO PLUNGER A MONTANTE MENOS PERDA POR FRIC��O
					v.PplgM -= v.Pfric;
					v.save_PplgM = v.PplgM;
					//CALCULA A VARIA��O DE VELOCIDADE DO PIST�O
					v.delta_v = c.step * (((v.PplgM - v.PplgJ) * c.AItbg)/(c.ROliq * f.tempos.Lslg * c.AItbg + f.pistao.Mplg)- c.G);
					//CALCULA A VARIA��O DE POSI��O DO PIST�O
					v.delta_h = v.v0 * c.step + c.step * v.delta_v/2;
					//CALCULA A VELOCIDADE DA GOLFADA
					v.v0 = v.save_v0 + v.delta_v;
					//CALCULA A POSI��O DO PIST�O
					f.varSaida.Hplg = v.save_Hplg + v.delta_h;
					//CALCULA A VISCOSIDADE
					v.Visc   = ue.VISC ( ue.TEMP( f.tubing.Lcauda - f.varSaida.Hplg - f.tempos.Lslg/2 ) );
					//CALCULA O N� DE REYNOLDS
					v.Rey    = c.ROliq * abs(v.v0) * f.tubing.DItbg/v.Visc;
					//CALCULA O FATOR DE FRIC��O
					v.Fric   = ue.FRIC(v.Rey, f.tubing.E, f.tubing.DItbg);
					//CALCULA A PERDA POR FRIC��O
					v.Pfric  = c.ROliq * v.Fric * f.tempos.Lslg * abs(v.v0) * v.v0/(2 * f.tubing.DItbg);
				}
				for( v.o = 0; v.o < 4; v.o++ ){ //PQ REPETE 4 VEZES
					//PRESS�O NO PLUNGER A MONTANTE, ACIMA DA COLUNA DE L�QUIDO FORMADA NO FUNDO
					v.PplgM   = ue.GASOSTT(f.varSaida.PcsgB - c.ROliq * c.G * f.tempos.Ltbg, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg), f.varSaida.Hplg - f.tempos.Ltbg);
//					System.out.println("---1ueTEMP = " + ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg));
//					System.out.println("---2ueTEMP = " + ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg));
//					System.out.println("---GASOSTT = " + ue.GASOSTT(f.varSaida.PcsgB - c.ROliq * c.G * f.tempos.Ltbg, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg), f.varSaida.Hplg - f.tempos.Ltbg));
//					System.out.println("---PplgM = " + v.PplgM);
//					System.out.println("---Pfric = " + v.Pfric);
//					System.out.println("---PplgJ = " + v.PplgJ);
					//SUBTRAI A PRESSAO DO PLUNGER A MONTANTE DA PERDA POR FRICCAO
					v.PplgM  -= v.Pfric;
					//CALCULA A VARIACAO DE VELOCIDADE DO PISTAO, OBTIDA DA SEGUNDA LEI DE NEWTON
					v.delta_v = c.step * (((v.save_PplgM + v.PplgM)/2.0 - v.PplgJ) * c.AItbg)/(c.ROliq * f.tempos.Lslg * c.AItbg + f.pistao.Mplg);
					//CALCULA A VARIACAO DE POSICAO DO PISTAO
					v.delta_h = v.v0 * c.step + c.step * v.delta_v/2;
					//System.out.println("delta_h = " + v.delta_h + "|deltaV = " + v.delta_v);
					//CALCULA A VELOCIDADE DA GOLFADA
					v.v0      = v.save_v0 + v.delta_v;
					//CALCULA A POSICAO DO PISTAO
					f.varSaida.Hplg = v.save_Hplg + v.delta_h;
					//CALCULA A VISCOSIDADE					
					//System.out.println("--Lcauda = " + f.tubing.Lcauda + " |Hplg = " + f.varSaida.Hplg + " |Lslg = " + f.tempos.Lslg );
					//System.out.println("ue TEMP = " + ue.TEMP( f.tubing.Lcauda - f.varSaida.Hplg - f.tempos.Lslg/2 ));
					//System.out.println("ue VISC = " + ue.VISC ( ue.TEMP( f.tubing.Lcauda - f.varSaida.Hplg - f.tempos.Lslg/2 ) ));
					v.Visc   = ue.VISC ( ue.TEMP( f.tubing.Lcauda - f.varSaida.Hplg - f.tempos.Lslg/2 ) );
					//CALCULA O NUMERO DE REYNOLDS					
					v.Rey    = c.ROliq * abs(v.v0) * f.tubing.DItbg/v.Visc;
					//CALCULA O FATOR DE FRICCAO
					v.Fric   = ue.FRIC(v.Rey, f.tubing.E, f.tubing.DItbg);
					//System.out.println("3-Reynolds = " + v.Rey + "|" + f.tubing.E + "|" + f.tubing.DItbg);					
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
				f.varSaida.Qlres = c.Qmax * (1 - 0.2*(v.Pwf/f.reservat.Pest) - 0.8*pow(v.Pwf/f.reservat.Pest, 2));
				//CALCULO DO NUMERO DE MOLES DO RESERVATORIO BASEADO NO FATOR I, CALCULADO NA INICIALIZACAO, COM UMA VAZAO EM m�/d
				v.Ntotal += ((c.step * f.varSaida.Qlres * f.reservat.RGL * c.Pstd/86400)/(c.R * c.Tstd) - c.step * v.I);
				//EQUACAO (2.4.1??) CALCULO DA ALTURA DA COLUNA DE LIQUIDO NO FUNDO DO POCO
				f.tempos.Ltbg += (c.step * f.varSaida.Qlres/86400)/c.AItbg;
				
				//SE O PISTAO ESTIVER SUBINDO
				if (v.flag == 1) {
					//CALCULO QUE FICA ACUMULANDO NO FUNDO DO POCO PREPARANDO A GOLFADA
					v.LtbgZ += (c.step * f.varSaida.Qlres/86400)/c.AItbg;
				}
				//SE O PISTAO ESTIVER MERGULHADO NA GOLFADA
				v.temp = f.tempos.Ltbg - f.varSaida.Hplg;
				if ( v.temp >= 0 ) {
					//SE A GOLFADA POSSUIR VELOCIDADE
					if( v.v0 >= 0 ) {
						//AUMENTA O COMPRIMENTO DA GOLFADA
						f.tempos.Lslg += v.temp;
						//DIMINUI A ALTURA DA COLUNA DE LIQUIDO NO FUNDO
						f.tempos.Ltbg -= v.temp;
						//ARMAZENA O TAMANHO DA GOLFADA
						v.LslgX = f.tempos.Lslg;
					}
					//SENAO, SE O PISTAO ESTIVER PARADO NO FUNDO
					else
						if ( v.flag == 0 ) {
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
					f.varSaida.PcsgB = f.varSaida.PcsgB * v.Ntotal/v.N;
					v.Pwf = f.varSaida.PcsgB;
					//ASSOCIA O CONTADOR d AO CONTADOR i
					v.d = v.i;
					//CALCULA A PRESSAO NO TOPO DO REVESTIMENTO NORMALIZADA
					f.tempos.PcsgT = f.tempos.PcsgT * v.Ntotal/v.N;
				}
				else {
					/*Calcula a temperatura m�dia da seguinte forma:
					Encontra a temperatura entre a base do pist�o e o topo do tubing
					Encontra a temperatura entre o topo da coluna de l�quido no fundo e o topo do tubing
					Essa diferen�a corresponde � temperatura da coluna de g�s abaixo do pist�o*/
					v.TTt = ( ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg) + ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg))/2.0;
					//SE O CONTADOR i ESTIVER UMA ITERA��O � FRENTE DO CONTADOR d
					if ( v.i == (v.d + 1) ) {
						//CALCULA O N� DE MOLES DO G�S ABAIXO DO PIST�O
						v.Nt = f.varSaida.PcsgB * v.Vt / (c.R * v.TTt);
						//CALCULA E ARMAZENA O N� DE MOLES DE G�S NO ESPA�O ANULAR
						v.Na = v.Ntotal - v.Nt;
						v.save_Na = v.Na;
						//AMAZENA 0 NA PRESS�O M�DIA DE G�S ABAIXO DO PIST�O
						v.save_PPt = 0;
						//AMAZENA 0 NA PRESS�O M�DIA DO REVESTIMENTO
						v.save_PPcsg = 0;
					}
					else {
						//ATRIBUI AO N� DE MOLES DO G�S NO ESPA�O ANULAR O Na DA ITERA��O ANTERIOR
						v.Na = v.save_Na;
						//CALCULLA O N� DE MOLES DO G�S ABAIXO DO PIST�O BASADO EM Na E NO N� DE MOLES DE G�S DE SA�DA DO RESERVAT�IO
						v.Nt = v.Ntotal - v.Na;
					}
					//INICIALIZA O CONTADOR Y COM 0
					v.y=0;
					do {
						//System.out.println("Chegou nessa parte 7");
						//CRIA VARI�VEL PARA ARMAZENAR A PRSS�O M�DIA DO G�S ABAIXO DO PIST�O DA ITERA��O ANTERIOR
						v.p_ = v.save_PPt;
						//ATRIBUI � PRESS�O M�DIA ABAIXO DO PIST�O A PRESS�O NO FUNDO DO REVESTIMENTO
						v.PPt = f.varSaida.PcsgB;
						//LA�O for (SE O VALOR ABSOLUTO DADIFEREN�A ENTRE AS PRESS�ES M�DIAS DO G�S ABAIXO DO PIST�O DAS ITERA��ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E AT� 100 ITERA��ES)
						for ( v.u = 0; abs(v.PPt - v.p_) > 1.0 && v.u < 100; v.u++ ) {
							//ATUALIZA A PRESS�O M�DIA DO G�S ABAIXO DO PIST�O
							v.PPt = v.p_;
							//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O G�S ABAIXO DO PIST�O
							v.z = ue.Z(v.PPt/c.Ppc, v.TTt/c.Tpc);
							//CALCULA A PRESS�O M�DIA DO G�S ABAIXO DO PIST�O
							v.p_ = v.z * v.Nt * c.R * v.TTt/v.Vt;
						}
						//ARMAZENA A PRESS�O M�DIA DO G�S ABAIXO DO PIST�O
						v.save_PPt = v.p_;
						//CALCULA A PRESS�O M�DIA NA BASE DA COLUNA DE G�S ABAIXO DO PIST�O
						v.Pbt = (v.p_ * 2) / (1 + exp(-(c.PM * c.G*(f.varSaida.Hplg - f.tempos.Ltbg)) / (v.z*c.R*v.TTt)));
						//SOMA A PRESS�O DA COLUNA D L�QUIDO NO FUNDO � PRESS�O CALCULADA ANTERIOMENTE
						v.Pbt += c.ROliq*c.G*f.tempos.Ltbg;
						//ARMAZENA A PRESS�O M�DIA DO REVESTIMENTO NA ITERA��O ANTERIOR NA VARI�VEL p
						v.p_ = v.save_PPcsg;
						//A PRESS�O M�DIA DO REVESTIMENTO RECEBE A PRESS�O NA BASE DO REVESTIMENTO
						v.PPcsg = f.varSaida.PcsgB;
						//LA�O for (SE O VALOR ABSOLUTO DA DIFEREN�A ENTRE AS PRES�ES M�DIAS DO REVESTIMENTO DAS ITERA��ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E AT� 100 ITERA��ES)
						for( v.u = 0; abs(v.PPcsg - v.p_) > 1.0 && v.u < 100; v.u++) {
							//ATUALIZA A PRESS�O M�DIA DO REVESTIMENTO
							v.PPcsg = v.p_;
							//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O G�S NO REVESTIMENTO
							v.z = ue.Z(v.PPcsg/c.Ppc, c.TTcsg/c.Tpc);
							//CACULA A PRESS�O M�DIA DO G�S NO REVESTIMENTO
							v.p_ = v.z * v.Na * c.R * c.TTcsg/c.Vcsg;
							//System.out.println(v.p_);
						}
						//ARMAZENA A PRESS�O M�DIA DO REVESTIMENTO
						v.save_PPcsg = v.p_;
						//CALCULA A PRESS�O NA BASE DO ESPA�O ANULAR
						v.Pba = (v.p_ * 2)/(1 + exp(-(c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg)));
						//System.out.println("Pbt=" + v.Pbt + " |Pba=" + v.Pba + " | Subtracao=" + abs(v.Pbt - v.Pba));
						//SE O VALOR ABSOLUTO DA DIFEREN�A ENTRE AS PRESS�ES NA BASE DO TUBING E NA BASE DO ESPA�O ANULAR � MENOR OU IGUAL A 1 ENT�O ENCERRA O PROGRAMA
						if ( abs(v.Pbt - v.Pba) <= 1.0 ) {
							//System.out.println("CHEGOU NO BREAK");
							break;
						}
						//SEN�O,SE O CONTADOR Y ESTIVER NA PRIMEIRA ITERA��O
						else if ( v.y == 0 ) {
							//ARMAZENA NA VARI�VEL Nt O N�MERO DE MOLES DO G�S NO TUBING
							v.Nt_ = v.Nt;
							//ARMAZENA NA VARI�VEL F_ A DIFEREN�A ENTRE AS PRESS�ES NAS BASES DO ESPA�O ANULAR E DO TUBING
							v.F_ = v.Pbt - v.Pba;
							//SE A PRESS�O NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ESPA�O ANULAR O N�MERO DE MOLES DE G�S NO TUBING � MULTIPLICADO POR 0,5 E RECALCULA O N�MERO DE MOLES DO G�S NO ESPA�O ANULAR
							if( v.Pbt > v.Pba ) {
								v.Nt *= 0.5;
								v.Na = v.Ntotal - v.Nt;
							}
							//O N� DE MOLES DO G�S NO TUBING � MULTIPLICADO POR 1,5 E RECALCULA O N�MERO DE MOLES DO G�S NO ESPA�O ANULAR
							else {
								v.Nt *= 1.5;
								v.Na = v.Ntotal - v.Nt;
							}
						}
						else {
							//A VARI�VEL Ntt RECEBE O N�MERO DE MOLES DE G�S NO TUBING
							v.Ntt = v.Nt;
							//RECALCULA O N�MERO DE MOLES DO G�S NO TUBING ATRAV�S DE UM EXPRESS�O EMP�RICA
							v.Nt = v.Nt - (v.Pbt-v.Pba) * (v.Nt_-v.Nt) / (v.F_ - (v.Pbt-v.Pba));
							//RECALCULA O N�MERO DE MOLES DO G�S NO ESPA�O ANULAR
							v.Na = v.Ntotal - v.Nt;
							//A VARI�VEL Nt_ RECEBE O VALOR DE Ntt
							v.Nt_ = v.Ntt;
							//ARMAZENA NA VARI�VEL F_ A DIFEREN�A ENTRE AS PRESS�ES NAS BASES DO ESPA�O ANULAR E DO TUBING
							v.F_ = v.Pbt - v.Pba;
						}
						
						//INCREMENTA CONTADOR
						v.y = v.y + 1;
					} while (v.y < 150);
					//System.out.println("Chegou nessa parte 2");
					//ARMAZENA O N�MERO DE MOLES DO G�S NO ESPA�O ANULAR
					v.save_Na = v.Na;
					//ATRIBUI � PRESS�O DE FLUXO NO FUNDO DO REVESTIMENTO A PRESS�O NA BASE DO REVESTIMENTO A QUAL RECEBE A PRESS�O NA BASE DO ESPA�O ANULAR
					f.varSaida.PcsgB = v.Pba;
					v.Pwf = f.varSaida.PcsgB;
					//CALCULA A PRESS�O NO TOPO DO REVESTIMENTO
					f.tempos.PcsgT = (v.p_ * 2) / (1 + exp((c.PM*c.G*f.tubing.Lcauda) / (v.z*c.R*c.TTcsg)));
				}
				//ATRIBUI � VARIAVEL N O N�MERO DE MOLES NO TUBING,QUE � O MESMO PRODUZIDO PELO RESERVAT�RIO
				v.N = v.Ntotal;
				//CALCULA A TEMPERATURA M�DIA ENTRE A TEMPERATURA DA COLUNA DE G�S ACIMA DA GOLFADA E A TEMPERATURA NA SUPERF�CIE
				v.TT = (ue.TEMP(v.H) + c.Tsup)/2.0;
			}
			else {
				//INICIALIZA A POSI��O DO PLUNGER
				f.varSaida.Hplg = 0;
				//PRESS�O DE FLUXO NO FUNDO DO PO�O IGUAL � PRESS�O NA BASE DO REVESTIMENTO
				v.Pwf = f.varSaida.PcsgB;
				//CALCULA A VAZ�O DE L�QUIDO NO RESERVAT�RIO
				f.varSaida.Qlres = c.Qmax*(1 - 0.2*(v.Pwf/f.reservat.Pest)- 0.8*pow(v.Pwf/f.reservat.Pest,2));
				//EQ.4.1 AUMENTO DA COLUNA DE LIQUIDO NO FUNDO DO TUBING
				f.tempos.Ltbg += (c.step*f.varSaida.Qlres/86400)/c.AItbg;
				//INCREMENTA O N�MERO DE MOLES DE G�S NO RESERVAT�RIO
				v.Ntotal += ((c.step*f.varSaida.Qlres*f.reservat.RGL*c.Pstd/86400)/(c.R*c.Tstd)-c.step*v.I);
				//NORMALIZA A PRESS�O NA BASE DO REVESTIMENTO PEO N�MERO DE MOLES
				f.varSaida.PcsgB = f.varSaida.PcsgB*v.Ntotal/v.N;
				//NORMALIZA A PRESS�O NO TOPO DO REVESTIMENTO PELO N�MERO DE MOLES
				f.tempos.PcsgT = f.tempos.PcsgT*v.Ntotal/v.N;
				//ATRIBUI A N O N�MERO DE MOLES DO G�S NO RESERVAT�RIO
				v.N = v.Ntotal;				
			}
			addTempo();
		}/* fim do FOR Abrir Valv Motora */
		
		//FOR�ANDO O PLOTE DO �LTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		int chamaControle = 0;
		//N�O CONSEGUIU CHEGAR NA SUPERF�CIE
		if ( (f.tempos.Ontime - (v.i*c.step + v.Transient)) <= 0.000001) {
			v.j = 0;
			v.Ntotal += v.nn;
			chamaControle = 1;
		}

		//ENVIANDO MENSAGEM COM O TEMPO DE DURACAO DA SUBIDA PISTAO
		//this.enviarVarCiclo(SLUG_RISE_TIME, (--v.i/(1.0/c.step)+v.transient) );
		//this.enviarVarCiclo(STAGE_RISE_DURATION, v.i * c.step);
		//Se a v�lvula foi alterada, ent�o chamar buildup
		if ( this.alterarValvula ) {
			this.alterarValvula = false;
			this.OffBuildUp(false);
		}
		else {
			if ( chamaControle == 1 ) {
				c.estagio = c.PRODUCAO_LIQUIDO;
			}
			else {
				//ETAPA QUE ACABOU FOI A SUBIDA DO PISTAO
				c.estagio = c.SUBIDA_PISTAO;
			}
		}
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Modelo matem�tico da etapa de produ��o de l�quido.
	 */
	public void producaoLiquido(){

		//System.out.println("Chegou na Produ��o de L�quido");
		
		//CRIACAO DE VARIAVEIS PARA A SIMULACAO
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		//FOR�ANDO PLOTAR O PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		//CALCULA AS PERDAS POR FRICCAO
		v.Pfric += v.delta_h * c.ROliq * v.v0 * (pow(f.tubing.DItbg, 2)/pow(f.valvula.Dab/1000, 2) - 1)/c.step; //CUIDADO! , SEM EFEITO
		//ENQUANTO O PISTAO NAO CHEGAR TOTALMENTE NA SUPERFICIE E O TEMPO DA VALVULA ABERTA NAO TIVER TERMINADO
		for(v.j = 0; f.tubing.Lcauda > f.varSaida.Hplg &&
					f.tempos.Ontime - (v.i*c.step + v.j*c.step_ + v.Transient) > 0 &&
					(!this.alterarValvula); v.j++){//for1
			/* A CADA INTERVALO CONSTANTE DE TEMPO, � ATUALIZADO A VARIAVEL TEMPL(MAIS ABAIXO)
			 * j � O CONTADOR DO FOR(VARIAVEL) E step_ � UM DOS PASSOS DE INTEGRACAO(CONSTANTE)
			 * QUANDO A MULTIPLICACAO DELES FOR X.0X, A CONDICAO � COMPLETA
			 */
			if( (int)(v.j * c.step_ * 10) % 10 == 0) {
				//TEMPL RECEBE A DISTANCIA ENTRE A BASE DO PISTAO E A SUPERFICIE
				v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
				v.LtbgX = f.tempos.Ltbg;
			}
			//PRESSAO DE FLUXO DO UNFOD RECEBE A PRESSAO NA BASE DO ANULAR
			v.Pwf     = f.varSaida.PcsgB;
			//VAZ�O DE L�QUIDO E G�S NO RESERVAT�RIO
			f.varSaida.Qlres = c.Qmax * (1 - 0.2 *(v.Pwf/f.reservat.Pest) - 0.8 * pow(v.Pwf/f.reservat.Pest,  2));
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
				if ( abs(v.Pbt - v.Pba) <= 1.0) {
					break;
				}
				//SE O CONTADOR ESTIVER NA PRIMEIRA ITERACAO
				else if ( v.y == 0 ) {
					//ARMAZENA O NUMERO DE MOLES DO GAS NO TUBING
					v.Nt_ = v.Nt;
					//F_ RECEBE A DIFERENCA ENTRE AS PRESSOES NAS BASES DO ESPACO ANULAR E DO TUBING
					v.F_  = v.Pbt - v.Pba;
					//SE A PRESSAO NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ANULAR
					if( v.Pbt > v.Pba ) {
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
			f.varSaida.PcsgB = v.Pba;
			v.Pwf     = f.varSaida.PcsgB;
			//CALCULA A PRESSAO NO TOPO DO ANULAR
			f.tempos.PcsgT   = (v.p_ * 2) / (1 + exp((c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg) ));
			//A PRESSAO NA BASE DO TUBING � A PRESSAO MEDIA NA BASE DA COLUNA DE GAS ABAIXO DO PISTAO SUBTRAIDA DA PRESSAO DA COLUNA DE LIQUIDO NO FUNDO DO POCO
			v.PtbgB   = v.Pbt - c.ROliq * c.G * f.tempos.Ltbg;
			//PRESSAO DO PISTAO A MONTANTE, NO TOPO DA COLUNA DE GAS ABAIXO DO PISTAO
			v.PplgM   = ue.GASOSTT(v.PtbgB, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg),ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg), f.varSaida.Hplg - f.tempos.Ltbg);
			/* CALCULA A PRESSAO DO TUBING NA SUPERFICIE
			 * � calculada pela pressao no pistao � montante retirando a perda
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
			if( (f.varSaida.Hplg - f.tubing.Lcauda) > 0.000001  ){
				//POSICAO DO PISTAO RECEBE O VALOR DA ALTURA DO TUBING
				f.varSaida.Hplg = f.tubing.Lcauda-1;
			}
			//O COMPRIMENTO DA GOLFADA � SUBTRAIDO DE delta_h
			f.tempos.Lslg  -= v.delta_h;
			//SE O COMPRIMENTO DA GOLFADA FOR MENOR QUE ZERO
			if ( f.tempos.Lslg < 0.0001 ) {
				//MANTEM O COMPRIMENTO DA GOLFADA IGUAL A ZERO
				f.tempos.Lslg = 0;
			}
			//CALCULA A VISCOSIDADE COM A METADE DO COMPRIMENTO DA GOLFADA
			if ( f.tempos.Lslg/2 < 0.0001 ) {
				v.Visc   = ue.VISC(ue.TEMP(0));
			}
			else {
				v.Visc   = ue.VISC(ue.TEMP((f.tempos.Lslg/2.0)));
			}			
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
			addTempo();
		}//fim for1

		//FOR�ANDO O PLOTE DO ULTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
			//criarMensagem(CycleStage.PRODUCTION);
		}

		//Enviando o tempo de dura��o da etapa de produ��o
		enviarVarCiclo(CycleStage.STAGE_PRODUC_DURATION, v.j * c.step_);
		//Se a v�lvula foi alterada de estado
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
	 * @brief Parte de controle da simula��o.
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
	  //PRODU��O ACUMULADA
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
			//VELOCIDADE MEDIA DO PISTAO � ZERO
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
			//SE CONTADOR � ZERO, O CONTROLE FUZZY ATUA(CP N�O � MODIFICADO POIS O CONTROLE FUZZY EST� VAZIO)
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
	 * @brief Parte do modelo matem�tico da etapa de Afterflow.
	 */
	public void Afterflow() {

		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		//FOR�AR A PLOTAGEM DO PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		//O PIST�O SE ENCONTRA NA SUPERF�CIE
		f.varSaida.Hplg = f.tubing.Lcauda;
		//ANULA A VELOCIDADE DA GOLFADA
		v.v0 = 0;
		//A VARI�VEL Pt RECEBE A PRESS�O NO TOPO DO REVESTIMENTO SUBTRA�DA DA PRESS�O NO TOPO DA COLUNA DE L�QUIDO FORMADA NO FUNDO DO TUBING
		//PRESS�O NA COLUNA DE G�S ABAIXO DO PIST�O
		v.Pt = f.tempos.PcsgT - c.ROliq * c.G * f.tempos.Ltbg;
		//PARA UM TEMPO MENOR QUE O DE AFTERFLOW
		for(v.k = 1; v.k*c.step_aft < f.tempos.Afterflow && (!this.alterarValvula); v.k++) {
			/*****/
			if((int)(v.j*c.step_aft*10) % 10 == 0){
				v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
				v.LtbgX = f.tempos.Ltbg;
			}

			//INCREMENTA O COMPRIMENTO DA GOLFADA QUE EST� SENDO FORMADA NO FUNDO
			f.tempos.Ltbg += (c.step_aft * f.varSaida.Qlres/86400)/ c.AItbg;
			//CALCULA O VOLUME ENTRE O TOPO DA COLUNA DE L�QUIDO FORMADA NO FUNDO E O TOPO DE TUBING
			v.V = c.AItbg * (f.tubing.Lcauda - f.tempos.Ltbg);
			//CALCULA A VAZ�O DE G�S NA LINHA DE SURG�NCIA(CONSIDERA PRESS�O A MONTANTE,PRESS�O A JUSANTE,DI�METRO DE ABERTURA E TEMPERATURA DE SUPERF�CIE)
			v.qqq = ue.QSC( v.Pt/1000.0 , f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);
			//ARMAZENA O N�MERO TOTAL DE MOLES TOTAL(G�S NO TUBING E ANULAR)
			v.n = v.Ntotal;

			do{
				//VAZ�O DE L�QUIDO DO RESERVAT�RIO
				f.varSaida.Qlres = c.Qmax * ( 1- .2 * (v.Pwf/ f.reservat.Pest) - .8*pow(v.Pwf/ f.reservat.Pest,2));
				//Pt � ATRIBU�DA � VARI�VEL Ptt(PRESS�O NA COLUNA DE G�S ABAIXO DO PIST�O)
				v.Ptt=v.Pt;
				//CALCULA A NOVA VAZ�O NA LINHA DE SURG�NCIA
				v.q=ue.QSC(v.Pt/1000.0, f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);
				//SE A VAZ�O FOR NULA ENCERRA O PROGRAMA
				if(v.q == 0.0){break;}
				//CALCULA A VAZ�O M�DIA
				v.qq=(v.qqq + v.q)/2.0;
				//CALCULA O N�MERO TOTAL DE MOLES RESTANTES AP�S A VAZ�O COM OS PROVENIENTES DO RESERVAT�RIO
				v.Ntotal = v.n - c.step_aft*( (v.qq/86400) * c.Pstd)/(c.R * c.Tstd) + (c.step_aft * f.varSaida.Qlres * f.reservat.RGL * c.Pstd/86400)/(c.R * c.Tstd);
				//CALCULA A TEMPERATURA M�DIA DA COLUNA ABAIXO DO PIST�O
				v.TTt=(ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg) + ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg))/2.0;
				//ATUALIZA O N�MERO DE MOLES NO ANULAR COM O VALOR DA ITERA��O ANTERIOR
				v.Na = v.save_Na;
				//ATUALIZA O N�MERO DE MOLES NO TUBING
				v.Nt = v.Ntotal - v.Na;
				//INICIALIZA O CONTADOR
				v.y = 0;

				do{
					//CRIA VARI�VEL PARA ARMAZENAR A PRESS�O M�DIA DO G�S ABAIXO DO PIST�O DA ITERA��O ANTERIOR
					v.p_= v.save_PPt;
					//ATRIBUI � PRESS�O M�DIA ABAIXO DO PIST�O A PRESS�O NO FUNDO DO REVESTIMENTO
					v.PPt = f.varSaida.PcsgB;
					//LA�O for (SE O VALOR ABSOLUTO DA DIFEREN�A ENTRE AS PRESS�ES M�DIAS DO G�S ABAIXO DO PIST�O DAS ITERA��ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E AT� 100 ITERA��ES)
					for(v.u = 0; abs(v.PPt - v.p_) > 1.0 && v.u < 100; v.u++ ) {
						//ATUALIZA A PRESS�O M�DIA DO G�S ABAIXO DO PIST�O COM O VALOR ANTERIOR CALCULADO
						v.PPt = v.p_;
						//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O G�S ABAIXO DO PIST�O
						v.z = ue.Z(v.PPt/ c.Ppc, v.TTt/ c.Tpc);
						//CALCULA A PRESS�O M�DIA DO G�S ABAIXO DO PIST�O
						v.p_ = v.z*v.Nt*c.R*v.TTt/ v.V;
					}
					//ARMAZENA A PRESS�O M�DIA DO G�S ABAIXO DO PIST�O
					v.save_PPt = v.p_;
					//PRESS�O M�DIA NA BASE DA COLUNA DE G�S ABAIXO DO PIST�O
					v.Pbt = (v.p_ * 2)/(1 + exp(-(c.PM * c.G * (f.varSaida.Hplg - f.tempos.Ltbg))/(v.z * c.R * v.TTt)));
					//SOMA A PRESS�O DA COLUNA DE L�QUIDO NO FUNDO � PRESS�O CALCULADA ANTERIORMENTE
					v.Pbt += c.ROliq * c.G * f.tempos.Ltbg;
					//ARMAZENA A PRESS�O M�DIA DO REVESTIMENTO NA ITERA��O ANTERIOR NA VARI�VEL p_
					v.p_ = v.save_PPcsg;
					//A PRESS�O M�DIA DO REVESTIMENTO RECEBE A PRESS�O NA BASE DO REVESTIMENTO
					v.PPcsg = f.varSaida.PcsgB;
					//LA�O for (SE O VALOR ABSOLUTO DA DIFEREN�A ENTRE AS PRESS�ES M�DIAS DO REVESTIMENTO DAS ITERA��ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E AT� 100)
					for(v.u = 0; abs(v.PPcsg - v.p_) > 1.0 && v.u<100; v.u++){
						//ATUALIZA A PRESS�O M�DIA DO REVESTIMENTO CALCULADO ANTERIORMENTE COM O VALOR ARMAZENADO
						v.PPcsg = v.p_;
						//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O G�S NO REVESTIMENTO
						v.z = ue.Z(v.PPcsg/ c.Ppc, c.TTcsg/ c.Tpc);
						//CALCULA A PRESS�O M�DIA DO G�S NO REVESTIMENTO
						v.p_ = v.z*v.Na*c.R*c.TTcsg/ c.Vcsg;
					}
					//ARMAZENA A PRESS�O M�DIA DO REVESTIMENTO
					v.save_PPcsg = v.p_;
					//CALCULA A PRESS�O NA BASE DO ESPA�O ANULAR
					v.Pba = (v.p_ * 2)/(1 + exp(-( c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg)));
					//SE O VALOR ABSOLUTO DA DIFEREN�A ENTRE AS PRESS�ES NA BASE DO TUBING E NA BASE DO ESPA�O ANULAR � MENOR OU IGUAL A 1 ENT�O ENCERRA O PROGRAMA
					if(abs(v.Pbt - v.Pba)<=1.0){break;}
					//SEN�O,SE O CONTADOR Y ESTIVER NA PRIMEIRA ITERA��O
					else if(v.y == 0){
						//ARMAZENA NA VARI�VEL Nt_ O N�MERO DE MOLES DO G�S NO TUBING
						v.Nt_ = v.Nt;
						//ARMAZENA NA VARI�VEL F_ A DIFEREN�A ENTRE AS PRESS�ES NAS BASES DO ESPA�O ANULAR E DO TUBING
						v.F_ = v.Pbt - v.Pba;
						//SE A PRESS�O NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ESPA�O ANULAR,O N�MERO DE MOLES DO G�S NO TUBING � MULTIPLICADO POR 0,5. RECALCULA O N�MERO DE MOLES DO G�S NO ESPA�O ANULAR
						if(v.Pbt > v.Pba){
							v.Nt *= 0.5;
							v.Na = v.Ntotal - v.Nt;
						}
						//O N�MERO DE MOLES DO G�S NO TUBING � MULTIPLICADO POR 1,5
						else {
							v.Nt *= 1.5;
							v.Na = v.Ntotal - v.Nt;
						}
					}
					else{
						//A VARI�VEL Ntt RECEBE O N�MERO DE MOLES DE G�S NO TUBING
						v.Ntt = v.Nt;
						//RECALCULA O N�MERO DE MOLES DE G�S NO TUBING ATRAV�S DE UMA EXPRESS�O EMP�RICA
						v.Nt = v.Nt - (v.Pbt - v.Pba) * (v.Nt_ - v.Nt)/(v.F_ - (v.Pbt - v.Pba));
						//RECALCULA O N�MERO DE MOLES DO G�S NO ANULAR
						v.Na = v.Ntotal - v.Nt;
						//A VARI�VEL Nt_ RECEBE A VARI�VEL Ntt
						v.Nt_ = v.Ntt;
						//ARMAZENA NA VARI�VEL F_ A DIFEREN�A ENTRE AS PRESS�ES NAS BASES DO ESPA�O ANULAR E DO TUBING
						v.F_ = v.Pbt - v.Pba;
					}
					//INCREMENTA CONTADOR
					v.y++;
				} while( v.y < 150 );    /*   fim do DO - linha 1481  */
				//ARMAZENA O N�MERO DE MOLES DO G�S NO ESPA�O ANULAR
				v.save_Na = v.Na;
				//ATRIBUI � PRESS�O DE FLUXO NO FUNDO DO REVESTIMENTO A PRESS�O NA BASE DO REVESTIMENTO A QUAL RECEBE A PRESS�O NA BASE DO ANULAR
				v.Pwf = v.Pba;
				//CALCULA A PRESS�O NO TOPO DA COLUNA DE G�S, A PARTIR DO TOPO DA COLUNA DE L�QUIDO FORMADA NO FUNDO
				v.Pt= ue.GASOSTT(v.PtbgB/*v.Pba - c.ROliq * c.G * f.tempos.Ltbg*/, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), c.Tsup, f.tubing.Lcauda - f.tempos.Ltbg);
			} while( abs(v.Ptt - v.Pt) > 1.0 );   /* fim do DO */
			//ATRIBUI � PRESS�O NO TOPO DO TUBING A VARI�VEL Pt
			f.varSaida.PtbgT = v.Pt;
			//ATRIBUI � PRESS�O NA BASE DO TUBING A VARI�VEL Pa MENOS PRESS�O DA COLUNA DE L�QUIDO FORMADA NO FUNDO
			v.PtbgB = v.Pba - c.ROliq * c.G * f.tempos.Ltbg;
			//CALCULA A PRESS�O NO TOPO DO REVESTIMENTO
			f.tempos.PcsgT = (v.p_ * 2)/(1 + exp((c.PM * c.G * (f.tubing.Lcauda))/(v.z * c.R * c.TTcsg)));
			//ATRIBUI � PRESS�O NO FUNDO DO REVESTIMENTO A PRESS�O DE FLUXO NO FUNDO
			f.varSaida.PcsgB = v.Pwf;

			//criarMensagem(CycleStage.AFTERFLOW);
			addTempo();
		} /*   fim do FOR  AfterFlow  -  linha 1415    */
		//Colocando valor default para a alteracao da vari�vel
		this.alterarValvula = false;

		//FOR�ANDO O PLOTE DO ULTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
			//criarMensagem(CycleStage.AFTERFLOW);
		}

		//Enviando o tempo de dura��o do afterflow
		enviarVarCiclo(CycleStage.STAGE_AFTER_DURATION, v.k * c.step_aft);

		//INFORMA QUE A ETAPA QUE ACABOU FOI O AFTERFLOW
		c.estagio = c.AFTERFLOW;
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Modelo matem�tico da etapa de Build Up da simula��o.
	 * @param ChegouSup Informa se o pist�o, na etapa de subida e produ��o,
	 *									conseguiu chegar � superf�cie, se sim true ou false caso
	 *									contr�rio.
	 */
	public void OffBuildUp(boolean ChegouSup){

		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		//FOR�AR A PLOTAGEM DO PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}
		// Vari�vel para o tempo gasto no buildup
		double tpgasto = 0;
		/* Vari�vel que diz qual etapa do build up est� para modificar o passo de
		 *integra��o:
		 * 0 - Queda do pist�o no g�s
		 * 1 - Transi��o do pist�o do g�s para o l�quido
		 * 2 - Queda do pist�o no l�quido
		 * 3 - Pist�o no fundo do po�o
		 */
		int modo_passo = 0;

		// Passo de integra��o do pist�o caindo no g�s
		c._step = c._stepGas;

		//Pt RECEBE PRESS�O NO TOPO DO REVESTIMENTO
		v.Pt = f.tempos.PcsgT;
		//N RECEBE O N�MERO DE MOLES TOTAL
		v.N = v.Ntotal;
		//ARMAZENA A PRESS�O NA BASE DO REVESTIMENTO DIVIDIDA POR 1.5 EM Ptt
		v.save_PPt = f.varSaida.PcsgB/1.5;
		//ARMAZENA A PRESS�O NA BASE DO REVESTIMENTO DIVIDIDA POR 1.5 EM PPcsg
		v.save_PPcsg = f.varSaida.PcsgB/1.5;
		//LIMITE PARA QUE O BUILDUP SEJA FINALIZADO
		//Danielson: comentei por usar a variavel Vqpl e Vqpg que foram excluidas,
		//assim como comentei o if depois do bypasscontroller que usa limite.
//		v.limite = (f.tubing.Lcauda - f.tempos.Ltbg) * (60*3.2808)/ f.pistao.Vqpg + ((v.Ppart_csg - v.Ppart_tbg) * (60*3.2808)/(c.ROliq * c.G * f.pistao.Vqpl));
		//INICIA A ITERACAO COM M = 1 E AUMENTA A CADA ITERACAO
		//Enquanto (n�o tiver chegado no tempo de offtime ou
		//(estiver passando pelo controle e o pistao ainda nao chegou no fundo)) e
		//pedido de altera��o de v�lvula motora

		for( v.m = 1; ( tpgasto < f.tempos.Offtime ||
									 (!byPassController && f.varSaida.Hplg > 0.0) ) &&
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
			// C�lculos intermedi�rios para facilitar compreens�o das equa��es
			//double eqk = (3*M_PI*f.pistao.Dplg + 17600*M_PI*f.pistao.Dplg*f.pistao.Lplg)*0.0072;
			double eqt = c._step*v.m;
			double DplgEsf = pow(pow(f.pistao.Dplg,2) * f.pistao.Lplg * 1.5, 0.3333);
			//Se pist�o estiver no g�s
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
			//Se pist�o estiver no L�quido
			else if ( f.varSaida.Hplg < f.tempos.Ltbg && f.varSaida.Hplg > 0.0 ) {
				double visliq = ue.VISC(ue.TEMP(f.tempos.Ltbg));
				double eqk = 3 * M_PI * DplgEsf * visliq + 300;
				//double eqFliq = f.pistao.Mplg*c.G - c.ROliq*c.G*pow(f.pistao.Dplg,2)*(M_PI/4)*f.pistao.Lplg;
				double eqFliq = f.pistao.Mplg*c.G - c.ROliq*c.G*pow(DplgEsf,3)*(M_PI/6);
				v.v0 = -(eqFliq/eqk)*(1 - exp(-eqk*eqt/f.pistao.Mplg));
				modo_passo = 2;
			}
			//Se pist�o estiver no fundo
			else if ( f.varSaida.Hplg == 0 ) {
				v.v0 = 0;
				modo_passo = 2;
			}
			// Testa se o pist�o est� na interface do l�quido
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
				// muda passo de integra��o
				modo_passo = 1;
				// For�a a plotagem de pontos enquanto essa condi��o for verdadeira
				this.quantidadePontos = this.periodoAmostragem + 1;
			}

			if ( abs(f.tempos.Ltbg - f.varSaida.Hplg) < f.pistao.Lplg ) {
				// muda passo de integra��o
				modo_passo = 1;
				// For�a a plotagem de pontos enquanto essa condi��o for verdadeira
				this.quantidadePontos = this.periodoAmostragem + 1;
			}
			//CALCULA A DIST�NCIA ENTRE A SUPERF�CIE E O PIST�O
			v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
			v.LtbgX = f.tempos.Ltbg;
			/*
			// Fim de Parte modificada
			if((int)(v.m * c._step *10) % 10==0){
				// DANIELSON: Atribui��es da vari�vel v0(velocidade do pistao) dependendo
				//da localiza��o que ele est�.
				if ( f.varSaida.Hplg > f.tempos.Ltbg ) {
					v.v0 = - f.pistao.Vqpg/(60*3.2808);
				}
				else if ( f.varSaida.Hplg < f.tempos.Ltbg && f.varSaida.Hplg > 0 )

					v.v0 = -(f.pistao.Vqpl - (f.varSaida.Qlres /( c.AItbg*86400)))/(60*3.2808);
				else if ( f.varSaida.Hplg == 0 )
					v.v0 = 0;

				//CALCULA A DIST�NCIA ENTRE A SUPERF�CIE E O PIST�O
				v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
				v.LtbgX = f.tempos.Ltbg;
			} /* fim do IF BUILDUP - linha 1453 */

			v.temp = (v.flag == 0 ? f.tempos.Ltbg + f.tempos.Lslg : (v.flag == 1 ? v.LtbgZ : v.LtbgY) );
			//CALCULA O VOLUME DA COLUNA DE PRODU��O ACIMA DA COLUNA DE L�QUIDO DEPENDENDO DA VARI�VEL temp
			v.Vt = c.AItbg * (f.tubing.Lcauda - v.temp);
			//CALCULA A TEMPERATURA M�DIA NO TUBING BASEADA NA TEMPERATURA DA SUPERF�CIE
			v.TTt= (ue.TEMP(f.tubing.Lcauda - v.temp) + c.Tsup)/2.0;
			//INICIALIZA CONTADOR
			v.y=0;

			do{
				//ARMAZENA PPt DA ITERA��O ANTERIOR EM pp
				f.varSaida.pp = v.save_PPt;
				//ATUALIZA PPt PARA A PRESS�O NA BASE DO REVESTIMENTO
				v.PPt = f.varSaida.PcsgB;
				//LA�O FOR (SE O VALOR ABSOLUTO DA DIFEREN�A ENTRE AS PRESS�ES M�DIAS DO TUBING DAS ITERA��ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E AT� 20)
				for( v.u = 0; abs(v.PPt - f.varSaida.pp) > 1.0 && v.u < 20; v.u++ ) {
					//ATUALIZA A PRESS�O M�DIA DO TUBING
					v.PPt = f.varSaida.pp;
					//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O G�S NO TUBING
					v.z = ue.Z(v.PPt/ c.Ppc, v.TTt/ c.Tpc);
					//CALCULA A PRESS�O M�DIA DO G�S NO TUBING
					f.varSaida.pp = v.z * v.Nt * c.R * v.TTt/ v.Vt;
				}
				//ARMAZENA A PRESS�O M�DIA NO TUBING DESTA ITERA��O
				v.save_PPt = f.varSaida.pp;
				//CALCULA A PRESS�O NA BASE DO TUBING LEVANDO EM CONTA A COLUNA DE L�QUIDO DE COMPRIMENTO DEFINIDO PELA VARI�VEL temp
				v.Pbt = ( f.varSaida.pp * 2)/(1 + exp(-(c.PM * c.G * (f.tubing.Lcauda - v.temp))/(v.z * c.R * v.TTt)));
				//SOMA � PRESS�O NA BASE DO TUBING O PESO DA COLUNA DE L�QUIDO DE COMPRIMENTO DEFINIDO PELA VARI�VEL temp
				v.Pbt += c.ROliq * c.G * v.temp;
				//ARMAZENA EM pp A PRESS�O M�DIA DO REVESTIMENTO DA ITERA��O ANTERIOR
				f.varSaida.pp = v.save_PPcsg;
				//ATUALIZA A PRESS�O M�DIA NO REVESTIMENTO COM O VALOR DA PRESS�O NA BASE DO REVESTIMENTO
				v.PPcsg = f.varSaida.PcsgB;
				//LA�O for (SE O VALOR ABSOLUTO DA DIFEREN�A ENTRE AS PRESS�ES M�DIAS DO REVESTIMENTO DAS ITERA��ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E AT� 20)
				for(v.u = 0; abs(v.PPcsg - f.varSaida.pp) > 1.0 && v.u<20; v.u++){
					//ATUALIZA A PRESS�O M�DIA DO REVESTIMENTO
					v.PPcsg = f.varSaida.pp;
					//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O G�S NO REVESTIMENTO
					v.z = ue.Z(v.PPcsg/ c.Ppc, c.TTcsg/ c.Tpc);
					//PRESS�O M�DIA DO G�S NO REVESTIMENTO
					f.varSaida.pp = v.z*v.Na*c.R*c.TTcsg/ c.Vcsg;
				}
				//ARMAZENA A PRESS�O M�DIA DO REVESTIMENTO DESTA ITERA��O EM PPcsg
				v.save_PPcsg = f.varSaida.pp;
				//CALCULA A PRESS�O NA BASE DO ESPA�O ANULAR
				v.Pba = (f.varSaida.pp*2)/(1 + exp(-(c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg)));
				//SE O VALOR ABSOLUTO DA DIFEREN�A ENTRE AS PRESS�ES NA BASE DO TUBING E NA BASE DO ESPA�O ANUAR � MENOR OU IGUAL A 1 ENT�O ENCERRA O PROGRAMA
				if ( abs(v.Pbt - v.Pba) <= 1.0 ) {
					break;
				}
				//SEN�O,SE O CONTADOR Y ESTIVER NA PRIMEIRA ITERA��O
				else if(v.y == 0){
					//ARMAZENA NA VARI�VEL Nt_ O N�MERO DE MOLES DO G�S NO TUBING
					v.Nt_ = v.Nt;
					//ARMAZENA NA VARI�VEL F_ A DIFEREN�A ENTRE AS PRESS�ES NAS BASES DO ESPA�O ANULAR E DO TUBING
					v.F_ = v.Pbt - v.Pba;
					//SE A PRESS�O NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ESPA�O ANULAR O N�MERO DE MOLES DO G�S � MULTIPLICADO POR 0,5. RECALCULA O N�MERO DE MOLES DO G�S NO ESPA�O ANULAR
					if ( v.Pbt > v.Pba ) {
						v.Nt /= 0.5;
						v.Na = v.N - v.Nt;
					}
					//O N�MERO DE MOLES DO G�S NO TUBING � MULTIPLICADO POR 1,5
					else {
						v.Nt *= 1.5;
						v.Na = v.N - v.Nt;
					}

				}
				else {
					//A VARI�VEL Ntt RECEBE O N�MERO DE MOLES DE G�S NO TUBING
					v.Ntt = v.Nt;
					//RECALCULA O N�MERO DE MOLES DE G�S NO TUBING ATRAV�S DE UMA EXPRESS�O EMP�RICA
					v.Nt = v.Nt - (v.Pbt - v.Pba)*(v.Nt_ - v.Nt)/(v.F_ - (v.Pbt - v.Pba));
					//RECALCULA O N�MERO DE MOLES DO G�S NO ESPA�O ANULAR
					v.Na = v.N - v.Nt;
					//A VARI�VEL Nt_ RECEBE A VARI�VEL Ntt
					v.Nt_ = v.Ntt;
					//ARMAZENA NA VARI�VEL F_ A DIFEREN�A ENTRE AS PRESS�ES NAS BASES DO ESPA�O ANULAR E DO TUBING
					v.F_ = v.Pbt - v.Pba;
				}
				//INCREMENTA O CONTADOR
				v.y++;
			} while ( v.y < 150 );
			//ARMAZENA O N�MERO DE MOLES DO G�S NO ESPA�O ANULAR
			v.save_Na = v.Na;
			//ATRIBUI � PRESS�O DE FLUXO NO FUNDO A PRESS�O NA BASE DO REVESTIMENTO,QUE � IGUAL � PRESS�O NA BASE DO TUBING
			v.Pwf = f.varSaida.PcsgB = v.Pbt;
			//CALCULA A PRESS�O NO TOPO DO REVESTIMENTO
			f.tempos.PcsgT = (v.save_PPcsg * 2)/(1 + exp((c.PM * c.G * (f.tubing.Lcauda))/(v.z * c.R * c.TTcsg)));
			//CALCULA A PRESS�O NA BASE DA COLUNA DE PRODU��O,QUE � A PRESS�O NA BASE DO TUBING MENOS AS PRESS�ES DEVIDO AO PESO DA COLUNA DE L�QUIDO,CUJO COMPRIMENTO FOI DEFINIDO NA VARI�VEL temp, E AO PESO DO PIST�O
			v.PtbgB = v.Pbt - (c.ROliq * c.G * v.temp + f.pistao.Mplg * c.G/ c.AItbg);

			//CALCULA A PRESS�O NO TOPO DA COLUNA DE PRODU��O,QUE � PRESS�O NO TOPO DO REVESTIMENTO MENOS AS PRESS�ES DEVIDO AO PESO DA COLUNA DE L�QUIDO,CUJO COMPRIMENTO FOI DEFINIDO NA VARI�VEL temp, E AO PESO DO PIST�O
			// ALTER: Foi trocado PcsgT por PcsgB at� confirma��o de troca
			//f.varSaida.PtbgT = f.tempos.PcsgT - (c.ROliq * c.G * v.temp + f.pistao.Mplg * c.G/ c.AItbg);
			f.varSaida.PtbgT = ue.GASOSTT(v.PtbgB,ue.TEMP(f.tubing.Lcauda - v.temp), c.Tsup, f.tubing.Lcauda - v.temp );
			//CALCULA A VAZ�O DE L�QUIDO DO REVESTIMENTO
			f.varSaida.Qlres = c.Qmax * (1 - .2*(v.Pwf/ f.reservat.Pest) - .8*pow(v.Pwf/ f.reservat.Pest,2));
			//INCREMENTA O N�MERO DE MOLES TOTAL,ARMAZENADO NA VARI�VEL N
			v.N += c._step * (((f.varSaida.Qlres * f.reservat.RGL/86400)) * c.Pstd)/(c.R * c.Tstd);
			//INCREMENTA O COMPRIMENTO DA COLUNA DE L�QUIDO FORMADA NO FUNDO
			f.tempos.Ltbg += (c._step * f.varSaida.Qlres/86400)/(c.AItbg);// * 86400);
			if(v.flag == 1)
				v.LtbgZ += c._step * f.varSaida.Qlres/(c.AItbg * 86400);
			if(v.flag == 2)
				v.LtbgY += c._step * f.varSaida.Qlres/(c.AItbg * 86400);
			// PARTE MODIFICADA
			//SE O PIST�O N�O ESTIVER NO FUNDO
			//DANIELSON: Aqui que vai mudar alguma coisa
			// Como vem a vari�vel Hplg e voc� calculou delta_v, delta_v seria calculado e substituido
			//	 pelo f.pistao.VqpX?
			if(f.varSaida.Hplg > 0) {
				v.delta_h = v.save_v0*c._step + (v.delta_v/2)*c._step;
				f.varSaida.Hplg += v.delta_h;
			}
			// FIM DE PARTE MODIFICADA
			/* C�PIA DE SEGURAN�A
			//SE O PIST�O N�O ESTIVER NO FUNDO
			//DANIELSON: Aqui que vai mudar alguma coisa
			// Como vem a vari�vel Hplg e voc� calculou delta_v, delta_v seria calculado e substituido
			//	 pelo f.pistao.VqpX?
			if(f.varSaida.Hplg > 0){
				//SE O PIST�O ESTIVER ACIMA DO TOPO DA COLUNA DE L�QUIDO FORMADA NO FUNDO DECREMENTA A POSI��O DESTE FATOR
				if ( f.varSaida.Hplg > f.tempos.Ltbg ) {
					f.varSaida.Hplg -= (c._step * f.pistao.Vqpg/(60 * 3.2808));
				}
				//SE O PIST�O ESTIVER ABAIXO DO TOPO DA COLUNA DE L�QUIDO FORMADA NO FUNDO DECREMENTA A POSI��O DO PIST�O DESTE OUTRO FATOR
				if ( f.varSaida.Hplg < f.tempos.Ltbg ) {
					f.varSaida.Hplg -= (c._step * (f.pistao.Vqpl - (f.varSaida.Qlres/(c.AItbg * 86400)))/(60 * 3.2808));
				}
			}*/
			//EVITA QUE A POSI��O DO PIST�O ASSUMA VALORES NEGATIVOS
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
			addTempo();
		}/*  fim do FOR (shut-in) OFF: - linha 1429  */
		this.alterarValvula = false;
		//FOR�ANDO O PLOTE DO ULTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
			//criarMensagem(CycleStage.BUILDUP);
		}

		//ATUALIZA O N�MERO TOTAL DE MOLES SOMENTE COM O N�MERO DE MOLES DO ESPA�O
		// ANULAR
		v.Ntotal = v.Na;
		// PARTE DO CONTROLADOR (MUDA O TEMPO DE FECHAMENTO DA VALVULA)
		if ( !byPassController ) {
			//INCREMENTA temp_Offtime
			v.temp_Offtime++;

			//O TEMPO DE Offtime RECEBE temp_Offtime
			f.tempos.Offtime = v.temp_Offtime;
		}
		//O COMPRIMENTO DA GOLFADA � ATUALIZADO, MAS BASEADO NA FLAG
		// ALTER: alterar para uma condi��o IF correspondente
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
			// O pistao n�o chegou no liquido do fundo da coluna?
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

		//Enviando o tempo de dura��o do buildup
		enviarVarCiclo(CycleStage.STAGE_BUILDUP_DURATION, c._step * v.m);

		//Enviando o tempo de dura��o do ciclo atual(SUBIDA + PRODUCAO + AFTERFLOW
		// + BUILDUP)
		enviarFimCiclo( v.i * c.step +  v.j * c.step_ + v.k * c.step + v.m * c._step );
	}
	//---------------------------------------------------------------------------
	public void addTempo() {
		DataConstants c = DataConstants.getInstance();
		
		int stage = c.estagio + 1;		
		if (stage == 8) {
			stage = 2;
		}		
		
		switch (stage) {
			// Subida do Pistao
			case 2:
				this.tempo += c.step ;
				break;
			//Producao do Liquido
			case 3:
				this.tempo += c.step_;
				break;
			//Afterflow
			case 5:
				this.tempo += c.step_aft;
				break;
			//Build-Up
			case 6:
				this.tempo += c._step;
				break;
			default:
				break;
		}
	}
	/**
	 * @brief Seta a precis�o de um double na quantidade de casas decimais.
	 * @param x N�mero para ser ajustado.
	 * @param precisao Precis�o do double resultante.
	 * @return Retorna o valor X com PRECISAO casas decimais.
	 */
	public double setPrecision(double x, int precisao) {
		//Para pegar somente a precisao, multiplica o numero X por 10 elevado a
		//precisao e ent�o transforma em inteiro. Com o numero inteiro, divide por
		//10 elevado a precisao e depois da virgula tera somente precisao numeros.
		return (((int)(x * pow(10,precisao)))/pow(10,precisao));
	}
	/**
	 * @brief Fun��o que trata de enviar um dado para ser inserido no hist�rico
	 * 				mostrado em uma das abas na interface a cada ciclo.
	 * @param ciclovar est�gio do ciclo atual ou vari�vel de ciclo
	 * @param valor Valor que deve ser enviado caracterizando a ocorr�ncia.
	 */
	public void enviarVarCiclo(CycleStage ciclovar, double valor) {
		
	}
	/**
	 * @brief Fun��o que trata de informar a interface que chegou ao fim do ciclo
	 * 				em determinado tempo.
	 * @param cycle Tempo em que o ciclo foi finalizado.
	 */
	public void enviarFimCiclo(double cycle) {		

	}
	/**
	 * @brief Fun��o que recebe o pedido de altera��o no estado da v�lvula motora.
	 */
	public void alterValvula() {
		this.alterarValvula = true;
	}

}
