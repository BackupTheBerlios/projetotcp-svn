package br.usp.larc.tcp.protocolo;

/*
 * @(#)TCPIF.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

/** 
 * Interface que contém os principais eventos/estados do Protocolo TCP
 * Você deve adicionar/remover atributos nessa interface, se necessário.
 * 
 *
 * @author	Laboratório de Arquitetura e Redes de Computadores.
 * @version	1.0 Agosto 2003.
 */
public interface TCPIF 
{
	
	// Estados da conexão TCP 0 a 9
	public static final byte CLOSED 		= 0;
	public static final byte LISTEN 		= 1;
	public static final byte SYNRCVD 		= 2;
	public static final byte SYNSENT 		= 3;
	public static final byte ESTABLISHED 	= 4;
	public static final byte CLOSEWAIT 		= 5;
	public static final byte FINWAIT1 		= 6;
	public static final byte FINWAIT2 		= 7;
	public static final byte CLOSING 		= 8;
	public static final byte LASTACK 		= 9;
	public static final byte TIMEWAIT 		= 10;
	public static final byte NENHUM			= 100;
	
	//Primitivas de 001 até 004 (relacionadas com o frame Monitor)
	public static final int P_TCP_OPEN                  = 000;
	public static final int P_TCP_CLOSE                 = 001;
	public static final int P_TCP_RESET                 = 002;
	public static final int P_TCP_OPEN_ME               = 003;
	public static final int P_TCP_CLOSE_ME              = 004;
	
	
	// Primitivas (relacionadas com o frame Máquina de Estado)
	public static final byte P_NENHUM                   = 0;
	public static final byte P_PASSIVEOPEN              = 1;
	public static final byte P_CLOSE                    = 2;
	public static final byte P_ACTIVEOPEN               = 3;
	public static final byte P_SEND                     = 4;
	public static final byte P_OPENSUCCESS              = 5;
	public static final byte P_OPENID                   = 6;
	public static final byte P_ERROR                    = 7;
	public static final byte P_CLOSING                  = 8;
	public static final byte P_TERMINATE                = 9;
	public static final byte P_TIMEOUT                  = 10;

	// Campos de controle dos Segmentos (ver tabela da apostila 1) (PDU)
	public static final byte S_NENHUM                   = 0x00;
	public static final byte S_FIN                      = 0x01;
	public static final byte S_SYN                      = 0x02;
	public static final byte S_RST                      = 0x04;
	public static final byte S_PSH                      = 0x08;
	public static final byte S_ACK                      = 0x10;
	public static final byte S_URG                      = 0x20;
	public static final byte S_FIN_ACK                  = S_FIN|S_ACK;
	public static final byte S_SYN_ACK                  = S_SYN|S_ACK;

	// TimeOuts
	public final static int MAX_RETRANSMISSOES          = 10;
	public static final int T_TIMEOUT                   = 150;
	public static final int T_ESTOURO_RETRANSMISSOES    = 151;
	// Maximum Segment Lifetime
	public static final int T_TIMEOUT_TX                = 2000;
	
	// Tmanho do segmento (no caso, um datagrama UDP) default que camada 
	// IP Simulada recebe e envia por vez	
	public static final int BUFFER_DEFAULT_IP_SIMULADA  = 1024;

	// Nomes
	public static final String[] nomeEstado = {
			"Closed", "Listen", "SynRcvd", "SynSent", "Estab.", "CloseWait",
			"FinWait1", "FinWait2", "Closing", "TimeWait", "LastAck" };
	
	public static final String[] nomePrimitiva = {
			"", "PassiveOpen", "Close", "ActiveOpen", "Send", "OpenSuccess",
			"OpenID", "Error", "Closing", "Terminate", "TimeOut" };
	
	public static final String SETA_DIR = "==>";
	public static final String SETA_ESQ = "<==";
	
//	public static String nomeSegmento (PacoteTCP _pacote);

	
}//fim da interface