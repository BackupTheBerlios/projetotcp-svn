package br.usp.larc.tcp.protocolo;

/*
 * @(#)TCP.java 1.0 31/04/2004 Copyleft (L) 2004 Laboratório de Arquitetura e
 * Redes de Computadores Escola Politécnica da Universidade de São Paulo.
 */

/**
 * Classe abstrata que contém os principais eventos/estados do Protocolo TCP
 * Você deve adicionar/remover atributos nessa interface, se necessário.
 * 
 * @author Laboratório de Arquitetura e Redes de Computadores.
 * @version 1.0 Agosto 2003.
 */
public abstract class TCP
{
    // Estados da conexão TCP 0 a 9
    public static final byte     CLOSED                     = 0;
    public static final byte     LISTEN                     = 1;
    public static final byte     SYNRCVD                    = 2;
    public static final byte     SYNSENT                    = 3;
    public static final byte     ESTABLISHED                = 4;
    public static final byte     CLOSEWAIT                  = 5;
    public static final byte     FINWAIT1                   = 6;
    public static final byte     FINWAIT2                   = 7;
    public static final byte     CLOSING                    = 8;
    public static final byte     LASTACK                    = 9;
    public static final byte     TIMEWAIT                   = 10;
    public static final byte     NENHUM                     = 100;

    //Primitivas de 001 até 004 (relacionadas com o frame Monitor)
    public static final int      P_TCP_OPEN                 = 0;
    public static final int      P_TCP_CLOSE                = 1;
    public static final int      P_TCP_RESET                = 2;
    public static final int      P_TCP_OPEN_ME              = 3;
    public static final int      P_TCP_CLOSE_ME             = 4;

    // Primitivas (relacionadas com o frame Máquina de Estado)
    /** <code>P_NENHUMA</code>: Nenhuma primitiva recebida/enviada */
    public static final byte     P_NENHUMA                  = 0;
    /** <code>P_PASSIVEOPEN</code>: Pede que ME espere conexão (Listen) */
    public static final byte     P_PASSIVEOPEN              = 1;
    /** <code>P_CLOSE</code>: Pede finalização de conexão */
    public static final byte     P_CLOSE                    = 2;
    /** <code>P_ACTIVEOPEN</code>: Pede abertura de conexão */
    public static final byte     P_ACTIVEOPEN               = 3;
    /** <code>P_SEND</code>: Envia dados */
    public static final byte     P_SEND                     = 4;
    /** <code>P_OPENSUCCESS</code>: Informa que conexão foi aberta com sucesso */
    public static final byte     P_OPENSUCCESS              = 5;
    /** <code>P_OPENID</code>: Informa que ME está esperando conexão */
    public static final byte     P_OPENID                   = 6;
    /** <code>P_ERROR</code>: Informa que houve um erro */
    public static final byte     P_ERROR                    = 7;
    public static final byte     P_CLOSING                  = 8;
    public static final byte     P_TERMINATE                = 9;
    public static final byte     P_TIMEOUT                  = 10;

    // Campos de controle dos Segmentos (ver tabela da apostila 1) (PDU)
    public static final byte     S_NENHUM                   = 0x00;
    public static final byte     S_FIN                      = 0x01;
    public static final byte     S_SYN                      = 0x02;
    public static final byte     S_RST                      = 0x04;
    public static final byte     S_PSH                      = 0x08;
    public static final byte     S_ACK                      = 0x10;
    public static final byte     S_URG                      = 0x20;
    public static final byte     S_FIN_ACK                  = S_FIN | S_ACK;
    public static final byte     S_SYN_ACK                  = S_SYN | S_ACK;

    // TimeOuts
    public final static int      MAX_RETRANSMISSOES         = 10;
    public static final int      T_TIMEOUT                  = 150;
    public static final int      T_ESTOURO_RETRANSMISSOES   = 151;

    /** <code>T_TIMEOUT_TX</code>: Maximum Segment Lifetime */
    public static final int      T_TIMEOUT_TX               = 2000;

    /**
     * <code>BUFFER_DEFAULT_IP_SIMULADA</code>: Tmanho do segmento (no caso,
     * um datagrama UDP) default que camada IP Simulada recebe e envia por vez
     */
    public static final int      BUFFER_DEFAULT_IP_SIMULADA = 1024;

    /** <code>nomeEstado</code>: Array com os nomes dos estados. */
    public static final String[] nomeEstado                 = {
            "Closed",
            "Listen",
            "SynRcvd",
            "SynSent",
            "Estab.",
            "CloseWait",
            "FinWait1",
            "FinWait2",
            "Closing",
            "LastAck",
            "TimeWait"                                      };

    public static final String[] nomePrimitiva              = {
            "",
            "PassiveOpen",
            "Close",
            "ActiveOpen",
            "Send",
            "OpenSuccess",
            "OpenID",
            "Error",
            "Closing",
            "Terminate",
            "TimeOut"                                       };

    public static final String   SETA_RECEBE_PRIM           = "->|";
    public static final String   SETA_ENVIA_PRIM            = "<-|";
    public static final String   SETA_NENHUMA_PRIM          = "  |";
    public static final String   SETA_RECEBE_SEG            =    "<=";
    public static final String   SETA_ENVIA_SEG             =    "=>";

    /**
     * Dado um campo de controle do segmento TCP, retorna o nome do tipo do
     * segmento.
     * 
     * @param controle
     *        Campo de controle do segmento TCP
     * @return Nome do tipo de segmento.
     */
    public static String nomeSegmento (int controle)
    {
        int flag, i;
        String nomes[] = {"", "FIN", "SYN", "RST", "PSH", "ACK", "URG"};
        String nome = "";

        for (flag = 0x01, i = 1; flag < 0x30; flag = flag << 1, i++)
        {
            if ( (controle & flag) != 0)
                nome += ( (nome.length () > 0)? "+": "") + nomes[i];
        }

        if (nome.length () == 0)
            nome = nomes[0];

        return nome;
    } // nomeSegmento

} // fim da classe
