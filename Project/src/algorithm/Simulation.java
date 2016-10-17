package algorithm;

import static java.lang.Math.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import controller.Controller;

/**
 * Simulation Methods
 * @author Danielson Flávio Xavier da Silva (danielson_fx@yahoo.com.br)
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
	/** VariÃ¡vel para informar se o pistÃ£o chegou ou nÃ£o Ã  superfÃ­cie */
	public boolean ChegouSup;
	/** Variï¿½vel que iria para desenho do processo */
	public double M_PI;
	/** Parâmetro que verifica se o pedido de alteração da válvula motora foi
			feita ou não */
	public boolean changeStateValve;
	/** Arquivo de impressão de variáveis de ciclo */
	public PrintWriter arquivo;
	/** Armazena o número do ciclo da execução corrente */
	public int cyclenumber;
	/** Controller of the current simulation */
	private Controller controller;
	
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
		ChegouSup         = false;
		byPassController  = true;
		forcarPontosI     = true;
		forcarPontosF     = true;
		changeStateValve	  = false;
		M_PI              = 3.14159265;
		cyclenumber       = 1;
		try {
			arquivo  = new PrintWriter("variaveis_de_ciclo.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			arquivo.close();
		}
	}
	/**
	 * Sets the controller used for control of the method
	 */
	public void setController(Controller ctrl) {
		this.controller = ctrl;
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
	 * Inicia os parï¿½metros para poder comeï¿½ar a simulaï¿½ï¿½o.
	 */
	public void iniciarSimulacao() {
		
		//System.out.println("Estagio: Iniciar Simulação");

		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		// Variï¿½veis de controle de passos de integraï¿½ï¿½o
		//c->_stepGas2Liq = 0.01;//valor antigo = 0.001
		//c->_stepGas = 0.1;//valor antigo = 0.01
		//c->_stepLiq = 0.1;//valor antigo = 0.005

		v.CP   = 30;   //Controle de Pressï¿½o (Pressï¿½o crï¿½tica)
		c.Fast = 5.5;  //Velocidade do pistao para ser considerada rï¿½pida
		c.Slow = 4.5;  //Velocidade do pistao para ser considerada lenta

		//Razï¿½o de ï¿½gua no lï¿½quido produzido
		c.FW = f.fluido.BSW/100;

		/*
		 * Specific Gravity do ï¿½leo retirada a partir da API
		 * a fï¿½rmula pode ser obtida em
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

		//Casing - ï¿½rea Interna
		c.AIcsg = 3.14*(pow(f.casing.DIcsg,2)-pow(f.tubing.DOtbg,2))/4;

		//Casing - Volume interno
		c.Vcsg = c.AIcsg * f.tubing.Lcauda;

		//Pressï¿½o na base do Anular
		f.varSaida.PcsgB = ue.GASOSTB(f.tempos.PcsgT, c.Tsup,ue.TEMP(f.tubing.Lcauda),f.tubing.Lcauda);

		//Temperatura mï¿½dia do Anular
		c.TTcsg=( ue.TEMP ( f.tubing.Lcauda ) + c.Tsup)/2;

		//Inicializa nï¿½mero de moles do gï¿½s
		v.Ntotal = 0;

		//Iniciando Contadores
		v.cont = 0;
		v.count = 0;
		v.contador = 1;

		//Iniciar as variï¿½veis em 0 (zero)
		f.varSaida.Qlres = 0;
		f.varSaida.PtbgT = 0;
		v.PtbgB = 0;
		f.varSaida.Hplg = 0;
		v.v0 = 0;
		f.tempos.Ltbg = 0;

		//Pressao na base do Tubing = Pressao na base do casing -
		//( densidade*gravidade*comprimento da golfada +
		//a massa do pistao pela razï¿½o entre a gravidade e a ï¿½rea interna do tubing
		v.PtbgB = f.varSaida.PcsgB - ( c.ROliq * c.G * f.tempos.Lslg + f.pistao.Mplg * c.G/c.AItbg);

		//Pressao no topo do tubing pela equacao GASOSTT
		f.varSaida.PtbgT = ue.GASOSTT(v.PtbgB,ue.TEMP(v.H), c.Tsup, v.H);

		//Passando o valor da entidade para variaveis auxiliares
		v.temp_Offtime = f.tempos.Offtime;

		//Opï¿½ï¿½o de esperar o pistao
		byPassController = !(f.tempos.Controller);

		//O Estagio e setado como BuildUp para que o inicioCiclo() seja iniciado
		c.estagio = c.OFF_BUILD_UP;
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Funï¿½ï¿½o que inicia o ciclo ajustando os parï¿½metros para comeï¿½ar um
	 *				novo ciclo.
	 * @return True se ocorreu a funï¿½ï¿½o com sucesso, false caso contrï¿½rio.
	 */
	public boolean inicioCiclo(){
		
		//System.out.println("Estagio: Início Ciclo");

		//Objetos de manipulacao de variaveis do poco
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		enviarVarCiclo(CycleStage.CYCLE_START, this.cyclenumber);
		this.cyclenumber = this.cyclenumber + 1;
		
		v.Ppart_csg = f.tempos.PcsgT;
		v.Ppart_tbg = f.varSaida.PtbgT;
		v.cont++;
		//ARMAZENAR A VARIAVEL Lslg
		v.LslgX = f.tempos.Lslg;
		//Volume do Tubing
		v.V = c.AItbg * f.tubing.Lcauda;
		//PPcsg - Pressï¿½o mï¿½dia no casing
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
		//Posiï¿½ï¿½o (altura) inicial do pistï¿½o (m)
		//f.varSaida.Hplg = 0.0;
		//H profundidade do topo do slug (m)
		v.H = f.tubing.Lcauda - f.varSaida.Hplg - f.pistao.Lplg - f.tempos.Lslg;

		// Se o poco afogou acaba funcao
		if( v.PtbgB <= 0 ) {
			return false;
		}

		//PP - pressao media no tubing (Pa)
		v.PP = (f.varSaida.PtbgT + v.PtbgB)/2;
		//TT temperatura media (K)
		v.TT = (ue.TEMP(v.H) + c.Tsup)/2.0;
		//Z Fator de compressibilidade mï¿½dio
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

		//Pressï¿½o mï¿½dia do gï¿½s acima da slug, com z=0.98
		f.varSaida.pp = 0.98 * v.nn * c.R * v.TT/v.V;

		//Atualiza a pressï¿½o mï¿½dia e fator de compressibilidade para o gï¿½s acima do slug
		do{
			v.PP = f.varSaida.pp;
			v.z = ue.Z(v.PP/c.Ppc, v.TT/c.Tpc);
			f.varSaida.pp = v.z * v.nn * c.R * v.TT/v.V;
		} while ( abs(v.PP - f.varSaida.pp) > 1.0 );

		//Inicializa pressï¿½o no topo do tubing descontando a influï¿½ncia do pistï¿½o
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
	 * @brief Modelo matemï¿½tico da parte de subida do pistï¿½o.
	 */
	public void subidaPistao() {
		
		System.out.println("Subida do pistao");
		
		//System.out.println("Estagio: Subida do PistÃ£o");
		//CRIACAO DE VARIAVEIS
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();
		
		//FORÃ‡AR A PLOTAGEM DO PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		//ALTER: Afirmando que delta h antes da subida Ã© sempre igual a zero
		v.delta_h = 0;
		/* Abertura da Valvula Motora */
		for( v.d = 0, v.i = 1; (v.H - v.delta_h) > 0 &&
				(f.tempos.Ontime - v.i * c.step - v.Transient) > 0 &&
				(!this.changeStateValve); v.i++ ){
			if( (int)(v.i * c.step * 10) % 10 == 0) {
				v.templ = 0.0 /*Lcauda-Hplg*/;
				v.LtbgX = (v.flag == 0 ? f.tempos.Ltbg : v.LtbgZ);
			}
			v.qqq = ue.QSC(f.varSaida.PtbgT/1000.0, f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);
			//ATRIBUI Ã€ VARIÃ�VEL N O NÂº MÃ‰DIO DE MOLES DO GÃ�S ACIMA DA GOLFADA
			v.n = v.nn;
			//SE O NÂº DE MOLES MÃ‰DIO(nn) FOR MENOR DO QUE O VALOR DO NÂº DE MOLES
			//CALCULADO (COM TEMPERATURA MÃ‰DIA (tt) NA SUPERFÃ�CIE) O VALOR DE N Ã‰
			//ATUALIZADO COM A TEMPERATURA.
			if( v.n < (v.temp = f.linhaPro.Psep * v.V/(v.z * c.R * v.TT)) )
				v.n = v.temp;
			//CONTADOR PARA O DO-WHILE
			v.y = 0;

			//LAÃ‡O DO-WHILE (SE A DIFERENÃ‡A ENTRE AS PRESSÃ•ES NO TUBING NA SUPERFÃ�CIE DA
			// ITERAÃ‡ÃƒO ATUAL E PASSADA DOR MAIOR QUE 1 E ATINGIR 50 ITERAÃ‡Ã•ES)
			do{
				//PRESSÃƒO DO TUBING NA SUPERFÃ�CIE
				v.Ptt = f.varSaida.PtbgT;
				//VAZÃƒO DE GÃ�S NA LINHA DE SURGÃŠNCIA
				v.q = ue.QSC(f.varSaida.PtbgT/1000.0,f.linhaPro.Psep/1000.0,f.valvula.Dab,c.Tsup);
				if(v.q == 0.0) {
					break;
				}
				//VAZÃƒO MÃ‰DIA NA LINHA DE SURGÃŠNCIA(O VALOR DE q MUDA A CADA PASSO)
				v.qq = (v.qqq + v.q)/2.0;
				//CÃ�LCULO DO NÃšMERO DE MOLES MÃ‰DIO CALCULADO NA PRESSÃƒO E TEMPERATURA PADRÃ•ES
				v.nn = v.n - c.step * ((v.qq/86400) * c.Pstd)/(c.R * c.Tstd);
				//SE O NÂº DE MOLES MÃ‰DIO CALCULADO ANTERIORMENTE FOR MENOR QUE O NÂº DE MOLES DO GÃ�S ACIMA DA GOLFADA,LEVANDO EM CONTA A PRESSÃƒO DO SEPARADOR, ENTÃƒO O NÂº DE MOLES RECEBE O VALOR DE TEMP
				if( v.nn < (v.temp = f.linhaPro.Psep * v.V/(v.z * c.R * v.TT)) )
					v.nn=v.temp;
				v.ppp = f.varSaida.pp;
				//RECALCULA PRESSÃƒO MÃ‰DIA(pp)DO GÃ�S ACIMA DO TOPO DA GOLFADA USANDO Z=0.98
				f.varSaida.pp = 0.98 * v.nn * c.R * v.TT/v.V;
				//LAÃ‡O DO-WHILE (SE A DIFERENÃ‡A ENTRE OS pp DAS ITERAÃ‡Ã•ES PRESENTE E PASSADA Ã‰ MAIOR QUE 1)
				do{
					//SALVA A PRESSÃƒO MÃ‰DIA DO GÃ�S ACIMA DA GOLFADA NA VARIÃ�VEL PP
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
				//CALCULA A PRESSÃƒO NO TUBING NA SUPERFÃ�CIE
				f.varSaida.PtbgT = v.Ptt - (v.ppp - f.varSaida.pp) * 2 * v.fatorT/(v.fatorT + v.fatorB);
				//System.out.println("Diferença PtbgT = " + (f.varSaida.PtbgT - v.save_PPt));
			}while( abs( v.Ptt - f.varSaida.PtbgT) > 1.0 && v.y++ < 50);
			//CALCULA A PRESSÃƒO NA BASE DA COLUNA
			v.PtbgB = (f.varSaida.pp * 2)/(1 + exp(- (c.PM * c.G * (f.tubing.Lcauda - f.tempos.Lslg - f.varSaida.Hplg))/(v.z * c.R * v.TT)) );
			//FATOR T:DIFERENÃ‡A ENTRE AS PRESSÃ•ES DO TOPO DA COLUNA E DO SEPARADOR
			v.fatorT = f.varSaida.PtbgT - f.linhaPro.Psep;
			//FATOR B:DIFERENÃ‡A ENTRE AS PRESSÃ•ES NA BASE DA COLUNA E DA BASE DA COLUNA DE GÃ�S DE COMPRIMENTO H (DO TOPO TUBING AO TOPO DA GOLFADA)LEVANDO EM CONTA A PRESSÃƒO EM CONTA A PRESSÃƒO DO SEPARADOR A TEMPERATURA DA SUPERFÃ�CIE
			v.fatorB = v.PtbgB - ue.GASOSTB(f.linhaPro.Psep, c.Tsup, ue.TEMP(v.H),v.H);

			//SE O PISTÃƒO NÃƒO ESTÃ� NO FUNDO DO POÃ‡O
			if(f.varSaida.Hplg >= 0){
				//PERDA DE PRESSÃƒO A JUSANTE: LEVA EM CONTA A PRESSÃƒO NA BASE DO TUBING,A PERDA POR PESO DA COLUNA DE LÃ�QUIDO E A PERDA DEÂ´PRESSÃƒO NO PISTÃƒO
				v.PplgJ = v.PtbgB + f.tempos.Lslg * c.ROliq * c.G + f.pistao.Mplg * c.G/c.AItbg;
				//ARMAZENA A POSIÃ‡ÃƒO DA BASE DO PISTÃƒO
				v.save_Hplg = f.varSaida.Hplg;
				//ARMAZENA A VELOCIDADE DA GOLFADA
				v.save_v0 = v.v0;
				//SE FOR A PRIMEIRA ITERACAO
				if( v.i == 1 ){
					//PRESSÃƒO NO PLUNGER A MONTANTE,ACIMA DA COLUNA DE LÃ�QUIDO FORMADA NO FUNDO OU NO TOPO DA COLUNA DE GÃ�S ABAIXO DO PISTÃƒO
					v.PplgM = ue.GASOSTT(f.varSaida.PcsgB - c.ROliq * c.G * f.tempos.Ltbg, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg), f.varSaida.Hplg - f.tempos.Ltbg);
					//ARMAZENA A PRESSÃƒO DO PLUNGER A MONTANTE MENOS PERDA POR FRICÃ‡ÃƒO
					v.save_PplgM = v.PplgM -= v.Pfric;
					//CALCULA A VARIAÃ‡ÃƒO DE VELOCIDADE DO PISTÃƒO
					v.delta_v = c.step * (((v.PplgM - v.PplgJ) * c.AItbg)/(c.ROliq * f.tempos.Lslg * c.AItbg + f.pistao.Mplg)- c.G);
					//CALCULA A VARIAÃ‡ÃƒO DE POSIÃ‡ÃƒO DO PISTÃƒO
					v.delta_h = v.v0 * c.step + c.step * v.delta_v/2;
					//CALCULA A VELOCIDADE DA GOLFADA
					v.v0 = v.save_v0 + v.delta_v;
					//CALCULA A POSIÃ‡ÃƒO DO PISTÃƒO
					f.varSaida.Hplg = v.save_Hplg + v.delta_h;
					//CALCULA A VISCOSIDADE
					v.Visc   = ue.VISC ( ue.TEMP( f.tubing.Lcauda - f.varSaida.Hplg - f.tempos.Lslg/2 ) );
					//CALCULA O NÂº DE REYNOLDS
					v.Rey    = c.ROliq * abs(v.v0) * f.tubing.DItbg/v.Visc;
					//CALCULA O FATOR DE FRICÃ‡ÃƒO
					v.Fric   = ue.FRIC(v.Rey, f.tubing.E, f.tubing.DItbg);
					//CALCULA A PERDA POR FRICÃ‡ÃƒO
					v.Pfric  = c.ROliq * v.Fric * f.tempos.Lslg * abs(v.v0) * v.v0/(2 * f.tubing.DItbg);
				}
				for( v.o = 0; v.o < 4; v.o++ ){ //PQ REPETE 4 VEZES
					//PRESSÃƒO NO PLUNGER A MONTANTE, ACIMA DA COLUNA DE LÃ�QUIDO FORMADA NO FUNDO
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
				f.varSaida.Qlres = c.Qmax * (1 - 0.2*(v.Pwf/f.reservat.Pest) - 0.8*pow(v.Pwf/f.reservat.Pest, 2));
				//CALCULO DO NUMERO DE MOLES DO RESERVATORIO BASEADO NO FATOR I, CALCULADO NA INICIALIZACAO, COM UMA VAZAO EM mÂ³/d
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
					/*Calcula a temperatura mÃ©dia da seguinte forma:
					Encontra a temperatura entre a base do pistÃ£o e o topo do tubing
					Encontra a temperatura entre o topo da coluna de lÃ­quido no fundo e o topo do tubing
					Essa diferenÃ§a corresponde Ã  temperatura da coluna de gÃ¡s abaixo do pistÃ£o*/
					v.TTt=(ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg) + ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg))/2.0;
					//SE O CONTADOR i ESTIVER UMA ITERAÃ‡ÃƒO Ã€ FRENTE DO CONTADOR d
					if ( v.i == v.d + 1) {
						//CALCULA O NÂº DE MOLES DO GÃ�S ABAIXO DO PISTÃƒO
						v.Nt=f.varSaida.PcsgB*v.Vt/(c.R*v.TTt);
						//CALCULA E ARMAZENA O NÂº DE MOLES DE GÃ�S NO ESPAÃ‡O ANULAR
						v.save_Na= v.Na = v.Ntotal - v.Nt;
						//AMAZENA 0 NA PRESSÃƒO MÃ‰DIA DE GÃ�S ABAIXO DO PISTÃƒO
						v.save_PPt=0;
						//AMAZENA 0 NA PRESSÃƒO MÃ‰DIA DO REVESTIMENTO
						v.save_PPcsg=0;
					}
					else {
						//ATRIBUI AO NÂº DE MOLES DO GÃ�S NO ESPAÃ‡O ANULAR O Na DA ITERAÃ‡ÃƒO ANTERIOR
						v.Na=v.save_Na;
						//CALCULLA O NÂº DE MOLES DO GÃ�S ABAIXO DO PISTÃƒO BASADO EM Na E NO NÂº DE MOLES DE GÃ�S DE SAÃ�DA DO RESERVATÃ“IO
						v.Nt=v.Ntotal-v.Na;
					}
					//INICIALIZA O CONTADOR Y COM 0
					v.y=0;
					do{
						//CRIA VARIÃ�VEL PARA ARMAZENAR A PRSSÃƒO MÃ‰DIA DO GÃ�S ABAIXO DO PISTÃƒO DA ITERAÃ‡ÃƒO ANTERIOR
						v.p_=v.save_PPt;
						//ATRIBUI Ã€ PRESSÃƒO MÃ‰DIA ABAIXO DO PISTÃƒO A PRESSÃƒO NO FUNDO DO REVESTIMENTO
						v.PPt=f.varSaida.PcsgB;
						//LAÃ‡O for (SE O VALOR ABSOLUTO DADIFERENÃ‡A ENTRE AS PRESSÃ•ES MÃ‰DIAS DO GÃ�S ABAIXO DO PISTÃƒO DAS ITERAÃ‡Ã•ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATÃ‰ 100 ITERAÃ‡Ã•ES)
						for(v.u=0; abs(v.PPt - v.p_) > 1.0 && v.u < 100; v.u++){
							//ATUALIZA A PRESSÃƒO MÃ‰DIA DO GÃ�S ABAIXO DO PISTÃƒO
							v.PPt=v.p_;
							//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O GÃ�S ABAIXO DO PISTÃƒO
							v.z=ue.Z(v.PPt/c.Ppc,v.TTt/c.Tpc);
							//CALCULA A PRESSÃƒO MÃ‰DIA DO GÃ�S ABAIXO DO PISTÃƒO
							v.p_ = v.z * v.Nt * c.R * v.TTt/v.Vt;
						}
						//ARMAZENA A PRESSÃƒO MÃ‰DIA DO GÃ�S ABAIXO DO PISTÃƒO
						v.save_PPt=v.p_;
						//CALCULA A PRESSÃƒO MÃ‰DIA NA BASE DA COLUNA DE GÃ�S ABAIXO DO PISTÃƒO
						v.Pbt=(v.p_*2)/(1+exp(-(c.PM*c.G*(f.varSaida.Hplg-f.tempos.Ltbg))/(v.z*c.R*v.TTt)));
						//SOMA A PRESSÃƒO DA COLUNA D LÃ�QUIDO NO FUNDO Ã€ PRESSÃƒO CALCULADA ANTERIOMENTE
						v.Pbt += c.ROliq * c.G * f.tempos.Ltbg;
						//ARMAZENA A PRESSÃƒO MÃ‰DIA DO REVESTIMENTO NA ITERAÃ‡ÃƒO ANTERIOR NA VARIÃ�VEL p
						v.p_ = v.save_PPcsg;
						//A PRESSÃƒO MÃ‰DIA DO REVESTIMENTO RECEBE A PRESSÃƒO NA BASE DO REVESTIMENTO
						v.PPcsg = f.varSaida.PcsgB;
						//LAÃ‡O for (SE O VALOR ABSOLUTO DA DIFERENÃ‡A ENTRE AS PRESÃ•ES MÃ‰DIAS DO REVESTIMENTO DAS ITERAÃ‡Ã•ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATÃ‰ 100 ITERAÃ‡Ã•ES)
						for(v.u=0; abs(v.PPcsg - v.p_) > 1.0 && v.u < 100; v.u++){
							//ATUALIZA A PRESSÃƒO MÃ‰DIA DO REVESTIMENTO
							v.PPcsg = v.p_;
							//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O GÃ�S NO REVESTIMENTO
							v.z=ue.Z(v.PPcsg/c.Ppc, c.TTcsg/c.Tpc);
							//CACULA A PRESSÃƒO MÃ‰DIA DO GÃ�S NO REVESTIMENTO
							v.p_ = v.z * v.Na * c.R * c.TTcsg/c.Vcsg;
						}
						//ARMAZENA A PRESSÃƒO MÃ‰DIA DO REVESTIMENTO
						v.save_PPcsg = v.p_;
						//CALCULA A PRESSÃƒO NA BASE DO ESPAÃ‡O ANULAR
						v.Pba = (v.p_ * 2)/(1 + exp(-(c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg)));
						//SE O VALOR ABSOLUTO DA DIFERENÃ‡A ENTRE AS PRESSÃ•ES NA BASE DO TUBING E NA BASE DO ESPAÃ‡O ANULAR Ã‰ MENOR OU IGUAL A 1 ENTÃƒO ENCERRA O PROGRAMA
						if ( abs(v.Pbt - v.Pba) <= 1.0 ) {
	                        break;
						}
						//SENÃƒO,SE O CONTADOR Y ESTIVER NA PRIMEIRA ITERAÃ‡ÃƒO
						else if( v.y == 0 ){
							//ARMAZENA NA VARIÃ�VEL Nt O NÃšMERO DE MOLES DO GÃ�S NO TUBING
							v.Nt_ = v.Nt;
							//ARMAZENA NA VARIÃ�VEL F_ A DIFERENÃ‡A ENTRE AS PRESSÃ•ES NAS BASES DO ESPAÃ‡O ANULAR E DO TUBING
							v.F_ = v.Pbt - v.Pba;
							//SE A PRESSÃƒO NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ESPAÃ‡O ANULAR O NÃšMERO DE MOLES DE GÃ�S NO TUBING Ã‰ MULTIPLICADO POR 0,5 E RECALCULA O NÃšMERO DE MOLES DO GÃ�S NO ESPAÃ‡O ANULAR
							if( v.Pbt > v.Pba ) {
								v.Nt *= 0.5;
								v.Na  = v.Ntotal - v.Nt;
							}
							//O NÂº DE MOLES DO GÃ�S NO TUBING Ã‰ MULTIPLICADO POR 1,5 E RECALCULA O NÃšMERO DE MOLES DO GÃ�S NO ESPAÃ‡O ANULAR
							else {
								v.Nt *= 1.5;
								v.Na = v.Ntotal - v.Nt;
							}
						}
						else{
							//A VARIÃ�VEL Ntt RECEBE O NÃšMERO DE MOLES DE GÃ�S NO TUBING
							v.Ntt = v.Nt;
							//RECALCULA O NÃšMERO DE MOLES DO GÃ�S NO TUBING ATRAVÃ‰S DE UM EXPRESSÃƒO EMPÃ�RICA
							v.Nt = v.Nt - (v.Pbt-v.Pba) * (v.Nt_-v.Nt) / (v.F_ - (v.Pbt-v.Pba));
							//RECALCULA O NÃšMERO DE MOLES DO GÃ�S NO ESPAÃ‡O ANULAR
							v.Na = v.Ntotal - v.Nt;
							//A VARIÃ�VEL Nt_ RECEBE O VALOR DE Ntt
							v.Nt_ = v.Ntt;
							//ARMAZENA NA VARIÃ�VEL F_ A DIFERENÃ‡A ENTRE AS PRESSÃ•ES NAS BASES DO ESPAÃ‡O ANULAR E DO TUBING
							v.F_ = v.Pbt - v.Pba;
						}
						//INCREMENTA CONTADOR
						v.y++;
					}while(v.y<150);
					//ARMAZENA O NÃšMERO DE MOLES DO GÃ�S NO ESPAÃ‡O ANULAR
					v.save_Na = v.Na;
					//ATRIBUI Ã€ PRESSÃƒO DE FLUXO NO FUNDO DO REVESTIMENTO A PRESSÃƒO NA BASE DO REVESTIMENTO A QUAL RECEBE A PRESSÃƒO NA BASE DO ESPAÃ‡O ANULAR
					v.Pwf = f.varSaida.PcsgB = v.Pba;
					//CALCULA A PRESSÃƒO NO TOPO DO REVESTIMENTO
					f.tempos.PcsgT = (v.p_*2)/(1+exp((c.PM*c.G*f.tubing.Lcauda)/(v.z*c.R*c.TTcsg)));
				}
				//ATRIBUI Ã€ VARIAVEL N O NÃšMERO DE MOLES NO TUBING,QUE Ã‰ O MESMO PRODUZIDO PELO RESERVATÃ“RIO
				v.N=v.Ntotal;
				//CALCULA A TEMPERATURA MÃ‰DIA ENTRE A TEMPERATURA DA COLUNA DE GÃ�S ACIMA DA GOLFADA E A TEMPERATURA NA SUPERFÃ�CIE
				v.TT=(ue.TEMP(v.H)+c.Tsup)/2.0;
			}
			else{
				//INICIALIZA A POSIÃ‡ÃƒO DO PLUNGER
				f.varSaida.Hplg=0;
				//PRESSÃƒO DE FLUXO NO FUNDO DO POÃ‡O IGUAL Ã€ PRESSÃƒO NA BASE DO REVESTIMENTO
				v.Pwf=f.varSaida.PcsgB;
				//CALCULA A VAZÃƒO DE LÃ�QUIDO NO RESERVATÃ“RIO
				f.varSaida.Qlres = c.Qmax*(1-0.2*(v.Pwf/f.reservat.Pest)-0.8*pow(v.Pwf/f.reservat.Pest,2));
				//EQ.4.1 AUMENTO DA COLUNA DE LIQUIDO NO FUNDO DO TUBING
				f.tempos.Ltbg+=(c.step*f.varSaida.Qlres/86400)/c.AItbg;
				//INCREMENTA O NÃšMERO DE MOLES DE GÃ�S NO RESERVATÃ“RIO
				v.Ntotal+=((c.step*f.varSaida.Qlres*f.reservat.RGL*c.Pstd/86400)/(c.R*c.Tstd)-c.step*v.I);
				//nORMALIZA A PRESSÃƒO NA BASE DO REVESTIMENTO PEO NÃšMERO DE MOLES
				f.varSaida.PcsgB=f.varSaida.PcsgB*v.Ntotal/v.N;
				//nORMALIZA A PRESSÃƒO NO TOPO DO REVESTIMENTO PELO NÃšMERO DE MOLES
				f.tempos.PcsgT=f.tempos.PcsgT*v.Ntotal/v.N;
				//ATRIBUI A N O NÃšMERO DE MOLES DO GÃ�S NO RESERVATÃ“RIO
				v.N=v.Ntotal;

			}
			//criarMensagem(PLUNGER_RISE);
			addTempo();
			//Checks the controller
			this.controller.check();
		}/* fim do FOR Abrir Valv Motora */

		//FORÃ‡ANDO O PLOTE DO ÃšLTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		//ENVIANDO MENSAGEM COM O TEMPO DE DURACAO DA SUBIDA PISTAO
		this.enviarVarCiclo(CycleStage.STAGE_RISE_DURATION, v.i * c.step);
		//Se a válvula foi alterada, então chamar buildup
		if ( this.changeStateValve ) {
			this.changeStateValve = false;
			c.estagio = c.AFTERFLOW;
			this.ChegouSup = false;
		}
		else {
			//ETAPA QUE ACABOU FOI A SUBIDA DO PISTAO
			c.estagio = c.SUBIDA_PISTAO;
		}
		
		//NÃO CONSEGUIU CHEGAR NA SUPERFÃ�CIE
		if (f.tempos.Ontime - (v.i*c.step + v.Transient) <= 0) {
			System.out.println("Chegou na condicao de nao chegada na superficie");
			v.j = 0;
			v.Ntotal += v.nn;
			Controle();
		}
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Modelo matemático da etapa de produção de líquido.
	 */
	public void producaoLiquido(){

		//System.out.println("Estagio: Produção de Líquido");
		
		//CRIACAO DE VARIAVEIS PARA A SIMULACAO
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		//FORï¿½ANDO PLOTAR O PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		//CALCULA AS PERDAS POR FRICCAO
		v.Pfric += v.delta_h * c.ROliq * v.v0 * (pow(f.tubing.DItbg, 2)/pow(f.valvula.Dab/1000, 2) - 1)/c.step; //CUIDADO! , SEM EFEITO
		//ENQUANTO O PISTAO NAO CHEGAR TOTALMENTE NA SUPERFICIE E O TEMPO DA VALVULA ABERTA NAO TIVER TERMINADO
		for(v.j = 0; f.tubing.Lcauda > f.varSaida.Hplg &&
					f.tempos.Ontime - (v.i*c.step + v.j*c.step_ + v.Transient) > 0 &&
					(!this.changeStateValve); v.j++){//for1
			/* A CADA INTERVALO CONSTANTE DE TEMPO, ï¿½ ATUALIZADO A VARIAVEL TEMPL(MAIS ABAIXO)
			 * j ï¿½ O CONTADOR DO FOR(VARIAVEL) E step_ ï¿½ UM DOS PASSOS DE INTEGRACAO(CONSTANTE)
			 * QUANDO A MULTIPLICACAO DELES FOR X.0X, A CONDICAO ï¿½ COMPLETA
			 */
			if( (int)(v.j * c.step_ * 10) % 10 == 0) {
				//TEMPL RECEBE A DISTANCIA ENTRE A BASE DO PISTAO E A SUPERFICIE
				v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
				v.LtbgX = f.tempos.Ltbg;
			}
			//PRESSAO DE FLUXO DO UNFOD RECEBE A PRESSAO NA BASE DO ANULAR
			v.Pwf     = f.varSaida.PcsgB;
			//VAZï¿½O DE Lï¿½QUIDO E Gï¿½S NO RESERVATï¿½RIO
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
			//A PRESSAO NA BASE DO TUBING ï¿½ A PRESSAO MEDIA NA BASE DA COLUNA DE GAS ABAIXO DO PISTAO SUBTRAIDA DA PRESSAO DA COLUNA DE LIQUIDO NO FUNDO DO POCO
			v.PtbgB   = v.Pbt - c.ROliq * c.G * f.tempos.Ltbg;
			//PRESSAO DO PISTAO A MONTANTE, NO TOPO DA COLUNA DE GAS ABAIXO DO PISTAO
			v.PplgM   = ue.GASOSTT(v.PtbgB, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg),ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg), f.varSaida.Hplg - f.tempos.Ltbg);
			/* CALCULA A PRESSAO DO TUBING NA SUPERFICIE
			 * ï¿½ calculada pela pressao no pistao ï¿½ montante retirando a perda
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
			if( f.varSaida.Hplg >= f.tubing.Lcauda ){
				//POSICAO DO PISTAO RECEBE O VALOR DA ALTURA DO TUBING
				f.varSaida.Hplg = f.tubing.Lcauda;
			}
			//O COMPRIMENTO DA GOLFADA ï¿½ SUBTRAIDO DE delta_h
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
			//Checks the controller
			this.controller.check();
		}//fim for1

		//FORï¿½ANDO O PLOTE DO ULTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
			//criarMensagem(CycleStage.PRODUCTION);
		}

		//Enviando o tempo de duraï¿½ï¿½o da etapa de produï¿½ï¿½o
		enviarVarCiclo(CycleStage.STAGE_PRODUC_DURATION, v.j * c.step_);
		//Se a vï¿½lvula foi alterada de estado
		if ( this.changeStateValve ) {
			this.changeStateValve = false;
			this.ChegouSup = false;
			c.estagio = c.AFTERFLOW;
		}
		else {
			//ATRIBUI O ESTAGIO QUE FOI TERMINADO COMO PRODUCAO_LIQUIDO
			c.estagio = c.PRODUCAO_LIQUIDO;
		}
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Parte de controle da simulaï¿½ï¿½o.
	 */
	public void Controle() {
		//System.out.println("Estagio: Controle");

		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		/*CALCULA A VARIACAO DE PRESSAO COMO SENDO A DIFERENCA ENTRE AS PRESSOES
		 *NO TOPO DO ANULAR DO INICIO DO PROGRAMA E DA ITERACAO ATUAL
		 */
		v.delta_P = v.Ppart_csg - f.tempos.PcsgT;
		//SE O PISTAO ESTIVER ACIMA DA SUPERFICIE OU NO TOPO DO TUBING
		if( f.varSaida.Hplg == f.tubing.Lcauda ){
			//System.out.println("Condição atingida superficie");
			//TEMPO DE CHEGADA DO PISTAO RECEBE A SOMA  DOS TEMPOS DE OCORRENCIA
			//DAS ETAPAS ANTERIORES MAIS O TEMPO QUE A ONDA ACUSTICA ATRAVESSA
			//O GAS NO TUBING ATE ATINGIR A GOLFADA
			v.piston_arrival = (int)(v.j*c.step_ + v.i*c.step + v.Transient);
		}
		//SE O PISTAO NAO CHEGOU NA SUPERFICIE
		else{
			//System.out.println("Condição NAO atingida superficie");
			//TEMPO DE CHEGADA DO PISTAO E ZERO
			v.piston_arrival = 0;
		}

		//CALCULA A PRODUCAO
		v.production = (float)((v.LslgX - f.tempos.Lslg)*c.AItbg*6.2848352758387*(1- c.FW));
	    //PRODUï¿½ï¿½O ACUMULADA
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
		boolean buildupgone = false;
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
			//VELOCIDADE MEDIA DO PISTAO ï¿½ ZERO
			v.velocity = 0;
			// ??????????????
			if( v.contador == 1) {
				f.tempos.Ontime    += 0;
				f.tempos.Afterflow += 0;
				f.tempos.Offtime   += 0;
			}
			//VAI PARA A ETAPA DE BUILDUP
			c.estagio = c.AFTERFLOW;
			this.OffBuildUp(false);
			buildupgone = true;
		}
		this.controller.check();
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
			//SE CONTADOR ï¿½ ZERO, O CONTROLE FUZZY ATUA(CP Nï¿½O ï¿½ MODIFICADO POIS O CONTROLE FUZZY ESTï¿½ VAZIO)
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
		if ( !buildupgone ) {
			//ESTAGIO RECEBE A ETAPA QUE FOI FINALIZADA - CONTROLE
			c.estagio = c.CONTROLE;
		}
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Parte do modelo matemï¿½tico da etapa de Afterflow.
	 */
	public void Afterflow() {
		
		//System.out.println("Estagio: Afterflow");

		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();

		//FORï¿½AR A PLOTAGEM DO PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}

		//O PISTï¿½O SE ENCONTRA NA SUPERFï¿½CIE
		f.varSaida.Hplg = f.tubing.Lcauda;
		//ANULA A VELOCIDADE DA GOLFADA
		v.v0 = 0;
		//A VARIï¿½VEL Pt RECEBE A PRESSï¿½O NO TOPO DO REVESTIMENTO SUBTRAï¿½DA DA PRESSï¿½O NO TOPO DA COLUNA DE Lï¿½QUIDO FORMADA NO FUNDO DO TUBING
		//PRESSï¿½O NA COLUNA DE Gï¿½S ABAIXO DO PISTï¿½O
		v.Pt = f.tempos.PcsgT - c.ROliq * c.G * f.tempos.Ltbg;
		//PARA UM TEMPO MENOR QUE O DE AFTERFLOW
		for(v.k = 1; v.k*c.step_aft < f.tempos.Afterflow && (!this.changeStateValve); v.k++) {
			/*****/
			if((int)(v.j*c.step_aft*10) % 10 == 0){
				v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
				v.LtbgX = f.tempos.Ltbg;
			}

			//INCREMENTA O COMPRIMENTO DA GOLFADA QUE ESTï¿½ SENDO FORMADA NO FUNDO
			f.tempos.Ltbg += (c.step_aft * f.varSaida.Qlres/86400)/ c.AItbg;
			//CALCULA O VOLUME ENTRE O TOPO DA COLUNA DE Lï¿½QUIDO FORMADA NO FUNDO E O TOPO DE TUBING
			v.V = c.AItbg * (f.tubing.Lcauda - f.tempos.Ltbg);
			//CALCULA A VAZï¿½O DE Gï¿½S NA LINHA DE SURGï¿½NCIA(CONSIDERA PRESSï¿½O A MONTANTE,PRESSï¿½O A JUSANTE,DIï¿½METRO DE ABERTURA E TEMPERATURA DE SUPERFï¿½CIE)
			v.qqq = ue.QSC( v.Pt/1000.0 , f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);
			//ARMAZENA O Nï¿½MERO TOTAL DE MOLES TOTAL(Gï¿½S NO TUBING E ANULAR)
			v.n = v.Ntotal;

			do{
				//VAZï¿½O DE Lï¿½QUIDO DO RESERVATï¿½RIO
				f.varSaida.Qlres = c.Qmax * ( 1- .2 * (v.Pwf/ f.reservat.Pest) - .8*pow(v.Pwf/ f.reservat.Pest,2));
				//Pt ï¿½ ATRIBUï¿½DA ï¿½ VARIï¿½VEL Ptt(PRESSï¿½O NA COLUNA DE Gï¿½S ABAIXO DO PISTï¿½O)
				v.Ptt=v.Pt;
				//CALCULA A NOVA VAZï¿½O NA LINHA DE SURGï¿½NCIA
				v.q=ue.QSC(v.Pt/1000.0, f.linhaPro.Psep/1000.0, f.valvula.Dab, c.Tsup);
				//SE A VAZï¿½O FOR NULA ENCERRA O PROGRAMA
				if(v.q == 0.0){break;}
				//CALCULA A VAZï¿½O Mï¿½DIA
				v.qq=(v.qqq + v.q)/2.0;
				//CALCULA O Nï¿½MERO TOTAL DE MOLES RESTANTES APï¿½S A VAZï¿½O COM OS PROVENIENTES DO RESERVATï¿½RIO
				v.Ntotal = v.n - c.step_aft*( (v.qq/86400) * c.Pstd)/(c.R * c.Tstd) + (c.step_aft * f.varSaida.Qlres * f.reservat.RGL * c.Pstd/86400)/(c.R * c.Tstd);
				//CALCULA A TEMPERATURA Mï¿½DIA DA COLUNA ABAIXO DO PISTï¿½O
				v.TTt=(ue.TEMP(f.tubing.Lcauda - f.varSaida.Hplg) + ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg))/2.0;
				//ATUALIZA O Nï¿½MERO DE MOLES NO ANULAR COM O VALOR DA ITERAï¿½ï¿½O ANTERIOR
				v.Na = v.save_Na;
				//ATUALIZA O Nï¿½MERO DE MOLES NO TUBING
				v.Nt = v.Ntotal - v.Na;
				//INICIALIZA O CONTADOR
				v.y = 0;

				do{
					//CRIA VARIï¿½VEL PARA ARMAZENAR A PRESSï¿½O Mï¿½DIA DO Gï¿½S ABAIXO DO PISTï¿½O DA ITERAï¿½ï¿½O ANTERIOR
					v.p_= v.save_PPt;
					//ATRIBUI ï¿½ PRESSï¿½O Mï¿½DIA ABAIXO DO PISTï¿½O A PRESSï¿½O NO FUNDO DO REVESTIMENTO
					v.PPt = f.varSaida.PcsgB;
					//LAï¿½O for (SE O VALOR ABSOLUTO DA DIFERENï¿½A ENTRE AS PRESSï¿½ES Mï¿½DIAS DO Gï¿½S ABAIXO DO PISTï¿½O DAS ITERAï¿½ï¿½ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATï¿½ 100 ITERAï¿½ï¿½ES)
					for(v.u = 0; abs(v.PPt - v.p_) > 1.0 && v.u < 100; v.u++ ) {
						//ATUALIZA A PRESSï¿½O Mï¿½DIA DO Gï¿½S ABAIXO DO PISTï¿½O COM O VALOR ANTERIOR CALCULADO
						v.PPt = v.p_;
						//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O Gï¿½S ABAIXO DO PISTï¿½O
						v.z = ue.Z(v.PPt/ c.Ppc, v.TTt/ c.Tpc);
						//CALCULA A PRESSï¿½O Mï¿½DIA DO Gï¿½S ABAIXO DO PISTï¿½O
						v.p_ = v.z*v.Nt*c.R*v.TTt/ v.V;
					}
					//ARMAZENA A PRESSï¿½O Mï¿½DIA DO Gï¿½S ABAIXO DO PISTï¿½O
					v.save_PPt = v.p_;
					//PRESSï¿½O Mï¿½DIA NA BASE DA COLUNA DE Gï¿½S ABAIXO DO PISTï¿½O
					v.Pbt = (v.p_ * 2)/(1 + exp(-(c.PM * c.G * (f.varSaida.Hplg - f.tempos.Ltbg))/(v.z * c.R * v.TTt)));
					//SOMA A PRESSï¿½O DA COLUNA DE Lï¿½QUIDO NO FUNDO ï¿½ PRESSï¿½O CALCULADA ANTERIORMENTE
					v.Pbt += c.ROliq * c.G * f.tempos.Ltbg;
					//ARMAZENA A PRESSï¿½O Mï¿½DIA DO REVESTIMENTO NA ITERAï¿½ï¿½O ANTERIOR NA VARIï¿½VEL p_
					v.p_ = v.save_PPcsg;
					//A PRESSï¿½O Mï¿½DIA DO REVESTIMENTO RECEBE A PRESSï¿½O NA BASE DO REVESTIMENTO
					v.PPcsg = f.varSaida.PcsgB;
					//LAï¿½O for (SE O VALOR ABSOLUTO DA DIFERENï¿½A ENTRE AS PRESSï¿½ES Mï¿½DIAS DO REVESTIMENTO DAS ITERAï¿½ï¿½ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATï¿½ 100)
					for(v.u = 0; abs(v.PPcsg - v.p_) > 1.0 && v.u<100; v.u++){
						//ATUALIZA A PRESSï¿½O Mï¿½DIA DO REVESTIMENTO CALCULADO ANTERIORMENTE COM O VALOR ARMAZENADO
						v.PPcsg = v.p_;
						//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O Gï¿½S NO REVESTIMENTO
						v.z = ue.Z(v.PPcsg/ c.Ppc, c.TTcsg/ c.Tpc);
						//CALCULA A PRESSï¿½O Mï¿½DIA DO Gï¿½S NO REVESTIMENTO
						v.p_ = v.z*v.Na*c.R*c.TTcsg/ c.Vcsg;
					}
					//ARMAZENA A PRESSï¿½O Mï¿½DIA DO REVESTIMENTO
					v.save_PPcsg = v.p_;
					//CALCULA A PRESSï¿½O NA BASE DO ESPAï¿½O ANULAR
					v.Pba = (v.p_ * 2)/(1 + exp(-( c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg)));
					//SE O VALOR ABSOLUTO DA DIFERENï¿½A ENTRE AS PRESSï¿½ES NA BASE DO TUBING E NA BASE DO ESPAï¿½O ANULAR ï¿½ MENOR OU IGUAL A 1 ENTï¿½O ENCERRA O PROGRAMA
					if(abs(v.Pbt - v.Pba)<=1.0){break;}
					//SENï¿½O,SE O CONTADOR Y ESTIVER NA PRIMEIRA ITERAï¿½ï¿½O
					else if(v.y == 0){
						//ARMAZENA NA VARIï¿½VEL Nt_ O Nï¿½MERO DE MOLES DO Gï¿½S NO TUBING
						v.Nt_ = v.Nt;
						//ARMAZENA NA VARIï¿½VEL F_ A DIFERENï¿½A ENTRE AS PRESSï¿½ES NAS BASES DO ESPAï¿½O ANULAR E DO TUBING
						v.F_ = v.Pbt - v.Pba;
						//SE A PRESSï¿½O NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ESPAï¿½O ANULAR,O Nï¿½MERO DE MOLES DO Gï¿½S NO TUBING ï¿½ MULTIPLICADO POR 0,5. RECALCULA O Nï¿½MERO DE MOLES DO Gï¿½S NO ESPAï¿½O ANULAR
						if(v.Pbt > v.Pba){
							v.Nt *= 0.5;
							v.Na = v.Ntotal - v.Nt;
						}
						//O Nï¿½MERO DE MOLES DO Gï¿½S NO TUBING ï¿½ MULTIPLICADO POR 1,5
						else {
							v.Nt *= 1.5;
							v.Na = v.Ntotal - v.Nt;
						}
					}
					else{
						//A VARIï¿½VEL Ntt RECEBE O Nï¿½MERO DE MOLES DE Gï¿½S NO TUBING
						v.Ntt = v.Nt;
						//RECALCULA O Nï¿½MERO DE MOLES DE Gï¿½S NO TUBING ATRAVï¿½S DE UMA EXPRESSï¿½O EMPï¿½RICA
						v.Nt = v.Nt - (v.Pbt - v.Pba) * (v.Nt_ - v.Nt)/(v.F_ - (v.Pbt - v.Pba));
						//RECALCULA O Nï¿½MERO DE MOLES DO Gï¿½S NO ANULAR
						v.Na = v.Ntotal - v.Nt;
						//A VARIï¿½VEL Nt_ RECEBE A VARIï¿½VEL Ntt
						v.Nt_ = v.Ntt;
						//ARMAZENA NA VARIï¿½VEL F_ A DIFERENï¿½A ENTRE AS PRESSï¿½ES NAS BASES DO ESPAï¿½O ANULAR E DO TUBING
						v.F_ = v.Pbt - v.Pba;
					}
					//INCREMENTA CONTADOR
					v.y++;
				} while( v.y < 150 );    /*   fim do DO - linha 1481  */
				//ARMAZENA O Nï¿½MERO DE MOLES DO Gï¿½S NO ESPAï¿½O ANULAR
				v.save_Na = v.Na;
				//ATRIBUI ï¿½ PRESSï¿½O DE FLUXO NO FUNDO DO REVESTIMENTO A PRESSï¿½O NA BASE DO REVESTIMENTO A QUAL RECEBE A PRESSï¿½O NA BASE DO ANULAR
				v.Pwf = v.Pba;
				//CALCULA A PRESSï¿½O NO TOPO DA COLUNA DE Gï¿½S, A PARTIR DO TOPO DA COLUNA DE Lï¿½QUIDO FORMADA NO FUNDO
				v.Pt= ue.GASOSTT(v.PtbgB/*v.Pba - c.ROliq * c.G * f.tempos.Ltbg*/, ue.TEMP(f.tubing.Lcauda - f.tempos.Ltbg), c.Tsup, f.tubing.Lcauda - f.tempos.Ltbg);
			} while( abs(v.Ptt - v.Pt) > 1.0 );   /* fim do DO */
			//ATRIBUI ï¿½ PRESSï¿½O NO TOPO DO TUBING A VARIï¿½VEL Pt
			f.varSaida.PtbgT = v.Pt;
			//ATRIBUI ï¿½ PRESSï¿½O NA BASE DO TUBING A VARIï¿½VEL Pa MENOS PRESSï¿½O DA COLUNA DE Lï¿½QUIDO FORMADA NO FUNDO
			v.PtbgB = v.Pba - c.ROliq * c.G * f.tempos.Ltbg;
			//CALCULA A PRESSï¿½O NO TOPO DO REVESTIMENTO
			f.tempos.PcsgT = (v.p_ * 2)/(1 + exp((c.PM * c.G * (f.tubing.Lcauda))/(v.z * c.R * c.TTcsg)));
			//ATRIBUI ï¿½ PRESSï¿½O NO FUNDO DO REVESTIMENTO A PRESSï¿½O DE FLUXO NO FUNDO
			f.varSaida.PcsgB = v.Pwf;

			//criarMensagem(CycleStage.AFTERFLOW);
			addTempo();
			//Checks the controller
			this.controller.check();
		} /*   fim do FOR  AfterFlow  -  linha 1415    */
		//Colocando valor default para a alteracao da variï¿½vel
		this.changeStateValve = false;

		//FORï¿½ANDO O PLOTE DO ULTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
			//criarMensagem(CycleStage.AFTERFLOW);
		}

		//Enviando o tempo de duraï¿½ï¿½o do afterflow
		enviarVarCiclo(CycleStage.STAGE_AFTER_DURATION, v.k * c.step_aft);

		//INFORMA QUE A ETAPA QUE ACABOU FOI O AFTERFLOW
		c.estagio = c.AFTERFLOW;
	}
	//---------------------------------------------------------------------------
	/**
	 * @brief Modelo matemático da etapa de Build Up da simulação.
	 * @param ChegouSup Informa se o pistão, na etapa de subida e produção,
	 *						conseguiu chegar à superfície, se sim true ou false caso contrário.
	 */
	public void OffBuildUp(boolean ChegouSup){
		
		Entities            f = Entities.getInstance();
		DataConstants       c = DataConstants.getInstance();
		SimulationVariables v = SimulationVariables.getInstance();
		UtilEquations       ue= new UtilEquations();
		
		//System.out.println("Estagio: BuildUp | " + c.estagio);
		//System.out.println("Chegou na iteracao do Buildup");
		
		this.ChegouSup = false;

		//FORÃ‡AR A PLOTAGEM DO PRIMEIRO PONTO DA ETAPA
		if ( forcarPontosI ) {
			quantidadePontos = periodoAmostragem + 1;
		}
		// VariÃ¡vel para o tempo gasto no buildup
		double tpgasto = 0;
		/* VariÃ¡vel que diz qual etapa do build up estÃ¡ para modificar o passo de
		 *integraÃ§Ã£o:
		 * 0 - Queda do pistÃ£o no gÃ¡s
		 * 1 - TransiÃ§Ã£o do pistÃ£o do gÃ¡s para o lÃ¬quido
		 * 2 - Queda do pistÃ£o no lÃ­quido
		 * 3 - PistÃ£o no fundo do poÃ§o
		 */
		int modo_passo = 0;

		// Passo de integraÃ§Ã£o do pistÃ£o caindo no gÃ¡s
		c._step = c._stepGas;

		//Pt RECEBE PRESSÃƒO NO TOPO DO REVESTIMENTO
		v.Pt = f.tempos.PcsgT;
		//n RECEBE O NÃšMERO DE MOLES TOTAL
		v.N = v.Ntotal;
		//ARMAZENA A PRESSÃƒO NA BASE DO REVESTIMENTO DIVIDIDA POR 1.5 EM Ptt
		v.save_PPt = f.varSaida.PcsgB/1.5;
		//ARMAZENA A PRESSÃƒO NA BASE DO REVESTIMENTO DIVIDIDA POR 1.5 EM PPcsg
		v.save_PPcsg = f.varSaida.PcsgB/1.5;
		//LIMITE PARA QUE O BUILDUP SEJA FINALIZADO
		//Danielson: comentei por usar a variavel Vqpl e Vqpg que foram excluidas,
		//assim como comentei o if depois do bypasscontroller que usa limite.
//		v.limite = (f.tubing.Lcauda - f.tempos.Ltbg) * (60*3.2808)/ f.pistao.Vqpg + ((v.Ppart_csg - v.Ppart_tbg) * (60*3.2808)/(c.ROliq * c.G * f.pistao.Vqpl));
		//INICIA A ITERACAO COM M = 1 E AUMENTA A CADA ITERACAO
		//Enquanto (nÃ£o tiver chegado no tempo de offtime ou
		//(estiver passando pelo controle e o pistao ainda nao chegou no fundo)) e
		//pedido de alteraÃ§Ã£o de vÃ¡lvula motora

		for( v.m = 1; ( tpgasto < f.tempos.Offtime /*|| f.varSaida.Hplg > 0*/ ) && (!this.changeStateValve); v.m++ ) { //System.out.println("Iteracao do buildup com tp gasto = " + tpgasto);
			///////////////////////////////////////////////////
			if ( v.m >= 100) {
				periodoAmostragem = 120;
			}
			///////////////////////////////////////////////////
			v.save_v0 = v.v0;
			// Parte modificada
			// CÃ¡lculos intermediÃ¡rios para facilitar compreensÃ£o das equaÃ§Ãµes
			//double eqk = (3*M_PI*f.pistao.Dplg + 17600*M_PI*f.pistao.Dplg*f.pistao.Lplg)*0.0072;
			double eqt = c._step*v.m;
			double DplgEsf = pow(pow(f.pistao.Dplg,2) * f.pistao.Lplg * 1.5, 0.3333);
			//Se pistÃ£o estiver no gÃ¡s
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
			//Se pistÃ£o estiver no LÃ­quido
			else if ( f.varSaida.Hplg < f.tempos.Ltbg && f.varSaida.Hplg > 0 ) {
				double visliq = ue.VISC(ue.TEMP(f.tempos.Ltbg));
				double eqk = 3 * M_PI * DplgEsf * visliq + 300;
				//double eqFliq = f.pistao.Mplg*c.G - c.ROliq*c.G*pow(f.pistao.Dplg,2)*(M_PI/4)*f.pistao.Lplg;
				double eqFliq = f.pistao.Mplg*c.G - c.ROliq*c.G*pow(DplgEsf,3)*(M_PI/6);
				v.v0 = -(eqFliq/eqk)*(1 - exp(-eqk*eqt/f.pistao.Mplg));
				modo_passo = 2;
			}
			//Se pistÃ£o estiver no fundo
			else if ( f.varSaida.Hplg == 0 ) {
				v.v0 = 0;
				modo_passo = 2;
			}
			// Testa se o pistÃ£o estÃ¡ na interface do lÃ­quido
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
				// muda passo de integraÃ§Ã£o
				modo_passo = 1;
				// ForÃ§a a plotagem de pontos enquanto essa condiÃ§Ã£o for verdadeira
				this.quantidadePontos = this.periodoAmostragem + 1;
			}

			if ( abs(f.tempos.Ltbg - f.varSaida.Hplg) < f.pistao.Lplg ) {
				// muda passo de integraÃ§Ã£o
				modo_passo = 1;
				// ForÃ§a a plotagem de pontos enquanto essa condiÃ§Ã£o for verdadeira
				this.quantidadePontos = this.periodoAmostragem + 1;
			}
			//CALCULA A DISTÃ‚NCIA ENTRE A SUPERFÃ�CIE E O PISTÃƒO
			v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
			v.LtbgX = f.tempos.Ltbg;
			/*
			// Fim de Parte modificada
			if((int)(v.m * c._step *10) % 10==0){
				// DANIELSON: AtribuiÃ§Ãµes da variÃ¡vel v0(velocidade do pistao) dependendo
				//da localizaÃ§Ã£o que ele estÃ¡.
				if ( f.varSaida.Hplg > f.tempos.Ltbg ) {
					v.v0 = - f.pistao.Vqpg/(60*3.2808);
				}
				else if ( f.varSaida.Hplg < f.tempos.Ltbg && f.varSaida.Hplg > 0 )

					v.v0 = -(f.pistao.Vqpl - (f.varSaida.Qlres /( c.AItbg*86400)))/(60*3.2808);
				else if ( f.varSaida.Hplg == 0 )
					v.v0 = 0;

				//CALCULA A DISTÃ‚NCIA ENTRE A SUPERFÃ�CIE E O PISTÃƒO
				v.templ = f.tubing.Lcauda - f.varSaida.Hplg;
				v.LtbgX = f.tempos.Ltbg;
			} /* fim do IF BUILDUP - linha 1453 */

			v.temp = (v.flag == 0 ? f.tempos.Ltbg + f.tempos.Lslg : (v.flag == 1 ? v.LtbgZ : v.LtbgY) );
			//CALCULA O VOLUME DA COLUNA DE PRODUÃ‡ÃƒO ACIMA DA COLUNA DE LÃ�QUIDO DEPENDENDO DA VARIÃ�VEL temp
			v.Vt = c.AItbg * (f.tubing.Lcauda - v.temp);
			//CALCULA A TEMPERATURA MÃ‰DIA NO TUBING BASEADA NA TEMPERATURA DA SUPERFÃ�CIE
			v.TTt= (ue.TEMP(f.tubing.Lcauda - v.temp) + c.Tsup)/2.0;
			//INICIALIZA CONTADOR
			v.y=0;

			do{
				//ARMAZENA PPt DA ITERAÃ‡ÃƒO ANTERIOR EM pp
				f.varSaida.pp = v.save_PPt;
				//ATUALIZA PPt PARA A PRESSÃƒO NA BASE DO REVESTIMENTO
				v.PPt = f.varSaida.PcsgB;
				//LAÃ‡O FOR (SE O VALOR ABSOLUTO DA DIFERENÃ‡A ENTRE AS PRESSÃ•ES MÃ‰DIAS DO TUBING DAS ITERAÃ‡Ã•ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATÃ‰ 20)
				for( v.u = 0; abs(v.PPt - f.varSaida.pp) > 1.0 && v.u < 20; v.u++ ) {
					//ATUALIZA A PRESSÃƒO MÃ‰DIA DO TUBING
					v.PPt = f.varSaida.pp;
					//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O GÃ�S NO TUBING
					v.z = ue.Z(v.PPt/ c.Ppc, v.TTt/ c.Tpc);
					//CALCULA A PRESSÃƒO MÃ‰DIA DO GÃ�S NO TUBING
					f.varSaida.pp = v.z * v.Nt * c.R * v.TTt/ v.Vt;
				}
				//ARMAZENA A PRESSÃƒO MÃ‰DIA NO TUBING DESTA ITERAÃ‡ÃƒO
				v.save_PPt = f.varSaida.pp;
				//CALCULA A PRESSÃƒO NA BASE DO TUBING LEVANDO EM CONTA A COLUNA DE LÃ�QUIDO DE COMPRIMENTO DEFINIDO PELA VARIÃ�VEL temp
				v.Pbt = ( f.varSaida.pp * 2)/(1 + exp(-(c.PM * c.G * (f.tubing.Lcauda - v.temp))/(v.z * c.R * v.TTt)));
				//SOMA Ã€ PRESSÃƒO NA BASE DO TUBING O PESO DA COLUNA DE LÃ�QUIDO DE COMPRIMENTO DEFINIDO PELA VARIÃ�VEL temp
				v.Pbt += c.ROliq * c.G * v.temp;
				//ARMAZENA EM pp A PRESSÃƒO MÃ‰DIA DO REVESTIMENTO DA ITERAÃ‡ÃƒO ANTERIOR
				f.varSaida.pp = v.save_PPcsg;
				//ATUALIZA A PRESSÃƒO MÃ‰DIA NO REVESTIMENTO COM O VALOR DA PRESSÃƒO NA BASE DO REVESTIMENTO
				v.PPcsg = f.varSaida.PcsgB;
				//LAÃ‡O for (SE O VALOR ABSOLUTO DA DIFERENÃ‡A ENTRE AS PRESSÃ•ES MÃ‰DIAS DO REVESTIMENTO DAS ITERAÃ‡Ã•ES PRESENTE E ANTERIOR FOR MAIOR QUE 1 E ATÃ‰ 20)
				for(v.u = 0; abs(v.PPcsg - f.varSaida.pp) > 1.0 && v.u<20; v.u++){
					//ATUALIZA A PRESSÃƒO MÃ‰DIA DO REVESTIMENTO
					v.PPcsg = f.varSaida.pp;
					//CALCULA O FATOR DE COMPRESSIBILIDADE PARA O GÃ�S NO REVESTIMENTO
					v.z = ue.Z(v.PPcsg/ c.Ppc, c.TTcsg/ c.Tpc);
					//PRESSÃƒO MÃ‰DIA DO GÃ�S NO REVESTIMENTO
					f.varSaida.pp = v.z*v.Na*c.R*c.TTcsg/ c.Vcsg;
				}
				//ARMAZENA A PRESSÃƒO MÃ‰DIA DO REVESTIMENTO DESTA ITERAÃ‡ÃƒO EM PPcsg
				v.save_PPcsg = f.varSaida.pp;
				//CALCULA A PRESSÃƒO NA BASE DO ESPAÃ‡O ANULAR
				v.Pba = (f.varSaida.pp*2)/(1 + exp(-(c.PM * c.G * f.tubing.Lcauda)/(v.z * c.R * c.TTcsg)));
				//SE O VALOR ABSOLUTO DA DIFERENÃ‡A ENTRE AS PRESSÃ•ES NA BASE DO TUBING E NA BASE DO ESPAÃ‡O ANUAR Ã‰ MENOR OU IGUAL A 1 ENTÃƒO ENCERRA O PROGRAMA
				if ( abs(v.Pbt - v.Pba) <= 1.0 ) {
					break;
				}
				//SENÃƒO,SE O CONTADOR Y ESTIVER NA PRIMEIRA ITERAÃ‡ÃƒO
				else if(v.y == 0){
					//ARMAZENA NA VARIÃ�VEL Nt_ O NÃšMERO DE MOLES DO GÃ�S NO TUBING
					v.Nt_ = v.Nt;
					//ARMAZENA NA VARIÃ�VEL F_ A DIFERENÃ‡A ENTRE AS PRESSÃ•ES NAS BASES DO ESPAÃ‡O ANULAR E DO TUBING
					v.F_ = v.Pbt - v.Pba;
					//SE A PRESSÃƒO NA BASE DO TUBING FOR MAIOR QUE NA BASE DO ESPAÃ‡O ANULAR O NÃšMERO DE MOLES DO GÃ�S Ã‰ MULTIPLICADO POR 0,5. RECALCULA O NÃšMERO DE MOLES DO GÃ�S NO ESPAÃ‡O ANULAR
					if ( v.Pbt > v.Pba ) {
						v.Nt /= 0.5;
						v.Na = v.N - v.Nt;
					}
					//O NÃšMERO DE MOLES DO GÃ�S NO TUBING Ã‰ MULTIPLICADO POR 1,5
					else {
						v.Nt *= 1.5;
						v.Na = v.N - v.Nt;
					}

				}
				else {
					//A VARIÃ�VEL Ntt RECEBE O NÃšMERO DE MOLES DE GÃ�S NO TUBING
					v.Ntt = v.Nt;
					//RECALCULA O NÃšMERO DE MOLES DE GÃ�S NO TUBING ATRAVÃ‰S DE UMA EXPRESSÃƒO EMPÃ�RICA
					v.Nt = v.Nt - (v.Pbt - v.Pba)*(v.Nt_ - v.Nt)/(v.F_ - (v.Pbt - v.Pba));
					//RECALCULA O NÃšMERO DE MOLES DO GÃ�S NO ESPAÃ‡O ANULAR
					v.Na = v.N - v.Nt;
					//A VARIÃ�VEL Nt_ RECEBE A VARIÃ�VEL Ntt
					v.Nt_ = v.Ntt;
					//ARMAZENA NA VARIÃ�VEL F_ A DIFERENÃ‡A ENTRE AS PRESSÃ•ES NAS BASES DO ESPAÃ‡O ANULAR E DO TUBING
					v.F_ = v.Pbt - v.Pba;
				}
				//INCREMENTA O CONTADOR
				v.y++;
			} while ( v.y < 150 );
			//ARMAZENA O NÃšMERO DE MOLES DO GÃ�S NO ESPAÃ‡O ANULAR
			v.save_Na = v.Na;
			//ATRIBUI Ã€ PRESSÃƒO DE FLUXO NO FUNDO A PRESSÃƒO NA BASE DO REVESTIMENTO,QUE Ã‰ IGUAL Ã€ PRESSÃƒO NA BASE DO TUBING
			v.Pwf = f.varSaida.PcsgB = v.Pbt;
			//CALCULA A PRESSÃƒO NO TOPO DO REVESTIMENTO
			f.tempos.PcsgT = (v.save_PPcsg * 2)/(1 + exp((c.PM * c.G * (f.tubing.Lcauda))/(v.z * c.R * c.TTcsg)));
			//CALCULA A PRESSÃƒO NA BASE DA COLUNA DE PRODUÃ‡ÃƒO,QUE Ã‰ A PRESSÃƒO NA BASE DO TUBING MENOS AS PRESSÃ•ES DEVIDO AO PESO DA COLUNA DE LÃ�QUIDO,CUJO COMPRIMENTO FOI DEFINIDO NA VARIÃ�VEL temp, E AO PESO DO PISTÃƒO
			v.PtbgB = v.Pbt - (c.ROliq * c.G * v.temp + f.pistao.Mplg * c.G/ c.AItbg);

			//CALCULA A PRESSÃƒO NO TOPO DA COLUNA DE PRODUÃ‡ÃƒO,QUE Ã‰ PRESSÃƒO NO TOPO DO REVESTIMENTO MENOS AS PRESSÃ•ES DEVIDO AO PESO DA COLUNA DE LÃ�QUIDO,CUJO COMPRIMENTO FOI DEFINIDO NA VARIÃ�VEL temp, E AO PESO DO PISTÃƒO
			// ALTER: Foi trocado PcsgT por PcsgB atÃ© confirmaÃ§Ã£o de troca
			//f.varSaida.PtbgT = f.tempos.PcsgT - (c.ROliq * c.G * v.temp + f.pistao.Mplg * c.G/ c.AItbg);
			f.varSaida.PtbgT = ue.GASOSTT(v.PtbgB,ue.TEMP(f.tubing.Lcauda - v.temp), c.Tsup, f.tubing.Lcauda - v.temp );
			//CALCULA A VAZÃƒO DE LÃ�QUIDO DO REVESTIMENTO
			f.varSaida.Qlres = c.Qmax * (1 - .2*(v.Pwf/ f.reservat.Pest) - .8*pow(v.Pwf/ f.reservat.Pest,2));
			//INCREMENTA O NÃšMERO DE MOLES TOTAL,ARMAZENADO NA VARIÃ�VEL N
			v.N += c._step * (((f.varSaida.Qlres * f.reservat.RGL/86400)) * c.Pstd)/(c.R * c.Tstd);
			//INCREMENTA O COMPRIMENTO DA COLUNA DE LÃ�QUIDO FORMADA NO FUNDO
			f.tempos.Ltbg += (c._step * f.varSaida.Qlres/86400)/(c.AItbg);// * 86400);
			if(v.flag == 1)
				v.LtbgZ += c._step * f.varSaida.Qlres/(c.AItbg * 86400);
			if(v.flag == 2)
				v.LtbgY += c._step * f.varSaida.Qlres/(c.AItbg * 86400);
			// PARTE MODIFICADA
			//SE O PISTÃƒO NÃƒO ESTIVER NO FUNDO
			//DANIELSON: Aqui que vai mudar alguma coisa
			// Como vem a variÃ¡vel Hplg e vocÃª calculou delta_v, delta_v seria calculado e substituido
			//	 pelo f.pistao.VqpX?
			if(f.varSaida.Hplg > 0) {
				v.delta_h = v.save_v0*c._step + (v.delta_v/2)*c._step;
				f.varSaida.Hplg += v.delta_h;
			}
			// FIM DE PARTE MODIFICADA
			/* CÃ“PIA DE SEGURANÃ‡A
			//SE O PISTÃƒO NÃƒO ESTIVER NO FUNDO
			//DANIELSON: Aqui que vai mudar alguma coisa
			// Como vem a variÃ¡vel Hplg e vocÃª calculou delta_v, delta_v seria calculado e substituido
			//	 pelo f.pistao.VqpX?
			if(f.varSaida.Hplg > 0){
				//SE O PISTÃƒO ESTIVER ACIMA DO TOPO DA COLUNA DE LÃ�QUIDO FORMADA NO FUNDO DECREMENTA A POSIÃ‡ÃƒO DESTE FATOR
				if ( f.varSaida.Hplg > f.tempos.Ltbg ) {
					f.varSaida.Hplg -= (c._step * f.pistao.Vqpg/(60 * 3.2808));
				}
				//SE O PISTÃƒO ESTIVER ABAIXO DO TOPO DA COLUNA DE LÃ�QUIDO FORMADA NO FUNDO DECREMENTA A POSIÃ‡ÃƒO DO PISTÃƒO DESTE OUTRO FATOR
				if ( f.varSaida.Hplg < f.tempos.Ltbg ) {
					f.varSaida.Hplg -= (c._step * (f.pistao.Vqpl - (f.varSaida.Qlres/(c.AItbg * 86400)))/(60 * 3.2808));
				}
			}*/
			//EVITA QUE A POSIÃ‡ÃƒO DO PISTÃƒO ASSUMA VALORES NEGATIVOS
			if ( f.varSaida.Hplg < 0 ) {
				f.varSaida.Hplg = 0;
			}
			if ( v.flag == 0 && (f.tempos.Ltbg - f.varSaida.Hplg) >= 0) {
				v.LtbgY += f.tempos.Ltbg;
				v.flag = 2;
			}
			
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
			// Adds the step in time
			addTempo();
			//Checks the controller
			this.controller.check();
		}/*  fim do FOR (shut-in) OFF: - linha 1429  */
		this.changeStateValve = false;
		//FORÃ‡ANDO O PLOTE DO ULTIMO PONTO DA ETAPA
		if ( forcarPontosF ) {
			quantidadePontos = periodoAmostragem + 1;
			//criarMensagem(BUILDUP);
		}

		//ATUALIZA O NÃšMERO TOTAL DE MOLES SOMENTE COM O NÃšMERO DE MOLES DO ESPAÃ‡O
		// ANULAR
		v.Ntotal = v.Na;
		// PARTE DO CONTROLADOR (MUDA O TEMPO DE FECHAMENTO DA VALVULA)
		if ( !byPassController ) {
			//INCREMENTA temp_Offtime
			v.temp_Offtime++;

			//O TEMPO DE Offtime RECEBE temp_Offtime
			f.tempos.Offtime = v.temp_Offtime;
		}
		//O COMPRIMENTO DA GOLFADA Ã‰ ATUALIZADO, MAS BASEADO NA FLAG
		// ALTER: alterar para uma condiÃ§Ã£o IF correspondente
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
			// O pistao nÃ£o chegou no liquido do fundo da coluna?
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

		//Enviando o tempo de duraÃ§Ã£o do buildup
		//Enviando o tempo de duraï¿½ï¿½o do buildup
		this.enviarVarCiclo(CycleStage.STAGE_BUILDUP_DURATION, c._step * v.m);

		//Enviando o tempo de duraÃ§Ã£o do ciclo atual(SUBIDA + PRODUCAO + AFTERFLOW
		// + BUILDUP)
		this.enviarFimCiclo( v.i * c.step +  v.j * c.step_ + v.k * c.step + v.m * c._step );
	}
	//---------------------------------------------------------------------------
	public void addTempo() {
		DataConstants c = DataConstants.getInstance();
		
		//int stage = c.estagio + 1;
		//if (stage == 8) {
		//	stage = 2;
		//}		
		
		switch (c.estagio) {
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
	 * @brief Seta a precisï¿½o de um double na quantidade de casas decimais.
	 * @param x Nï¿½mero para ser ajustado.
	 * @param precisao Precisï¿½o do double resultante.
	 * @return Retorna o valor X com PRECISAO casas decimais.
	 */
	public double setPrecision(double x, int precisao) {
		//Para pegar somente a precisao, multiplica o numero X por 10 elevado a
		//precisao e entï¿½o transforma em inteiro. Com o numero inteiro, divide por
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
		try {
			arquivo  = new PrintWriter(new FileWriter("variaveis_de_ciclo.txt", true));
			switch (ciclovar) {
			case STAGE_BUILDUP_DURATION:
				arquivo.println("Duração da etapa Build-up:       " + valor);
				break;
			case STAGE_AFTER_DURATION:
				arquivo.println("Duração da etapa Afterflow:      " + valor);
				break;
			case PRODUCTION_VOLUME:
				arquivo.println("Volume de produção no Ciclo:     " + valor);
				break;
			case TOTAL_PRODUCTION:
				arquivo.println("Total produzido pelo poço:       " + valor);
				break;
			case PL_RISE_TIME:
				arquivo.println("Tempo de viagem do pistão:       " + valor);
				break;
			case AVERAGE_PL_VELOCITY:
				arquivo.println("Média de velocidade do pistão:   " + valor);
				break;
			case IMPACT_VEL:
				arquivo.println("Velocidade de impacto do pistão: " + valor);
				break;
			case STAGE_PRODUC_DURATION:
				arquivo.println("Duração da etapa Produção:       " + valor);
				break;
			case STAGE_RISE_DURATION:
				arquivo.println("Duração da etapa Subida:         " + valor);
				break;
			case CYCLE_START:
				arquivo.println("Características do Ciclo nº" + (int)valor);
				break;
			default:
				break;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			arquivo.close();
		}
	}
	/**
	 * @brief Função que trata de informar a interface que chegou ao fim do ciclo
	 * 				em determinado tempo.
	 * @param valor Tempo em que o ciclo foi finalizado.
	 */
	public void enviarFimCiclo(double valor) {
		try {
			arquivo  = new PrintWriter(new FileWriter("variaveis_de_ciclo.txt", true));
			arquivo.println("Duração do ciclo:                " + valor);
			arquivo.println();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			arquivo.close();
		}
			
	}
	/**
	 * @brief Função que recebe o pedido de alteração no estado da válvula motora.
	 */
	public void changeStateValve() {
		this.changeStateValve = true;
	}

}
