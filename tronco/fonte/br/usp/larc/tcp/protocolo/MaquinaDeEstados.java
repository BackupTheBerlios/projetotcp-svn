package br.usp.larc.tcp.protocolo;

/*
 * @(#)MaquinaDeEstados.java 1.0 31/04/2004 Copyleft (L) 2004 Laborat�rio de
 * Arquitetura e Redes de Computadores Escola Polit�cnica da Universidade de S�o
 * Paulo
 */

import java.util.Timer;
import java.util.TimerTask;

import br.usp.larc.tcp.aplicacao.MaquinaDeEstadosFrame;
import br.usp.larc.tcp.excecoes.EstadoInvalidoException;
import br.usp.larc.tcp.excecoes.PrimitivaInvalidaException;
import br.usp.larc.tcp.excecoes.SegmentoInvalidoException;
import br.usp.larc.tcp.ipsimulada.IpSimulada;

/**
 * Classe que representa a M�quina de Estado do seu Protocolo (que pode ter n).
 * Detalhes e dicas de implementa��o podem ser consultadas nas Apostilas.
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a
 * criatividade na implementa��o do seu projeto.
 * 
 * @author Laborat�rio de Arquitetura e Redes de Computadores
 * @version 1.0 Agosto 2003
 */
public class MaquinaDeEstados
{
    /**
     * Atributo que representa o frame associado a M�quina de Estados
     */
    private MaquinaDeEstadosFrame meFrame;

    /**
     * Atributo que representa o monitor associado a M�quina de Estados
     */
    private Monitor               monitor;

    /**
     * Atributo que representa o id da Conex�o associado a M�quina de Estados
     */
    private int                   idConexao;

    /**
     * IP Simulado Local associado a M�quina de Estados
     */
    private String                ipSimuladoLocal;

    /**
     * IP Simulado Destino associado a M�quina de Estados
     */
    private String                ipSimuladoDestino;

    /**
     * Nome da esta��o destino associado a M�quina de Estados
     */
    private String                nomeEstacaoDestino;

    /**
     * Porta TCP local associada a M�quina de Estados
     */
    private int                   portaLocal;

    /**
     * Porta TCP remota associada a M�quina de Estados
     */
    private int                   portaDestino;

    /**
     * O estado atual da m�quina de conex�o e desconex�o
     */
    private byte                  estadoMEConexao;
    private byte                  estadoMETX;
    private byte                  estadoMERX;

    /**
     * N�mero de retransmiss�es de um segmeto TCP com timestamp expirado.
     */
    private int                   numRetransmissoes = 0;

    /**
     * Pr�ximo n�mero de seq��ncia a ser enviado
     */
    private int                   proximoNS      = 0;
    
    /**
     * N�mero de seq��ncia esperado
     */
    private int                   esperadoNS      = 0;
    
    /**
     * �ltimo n�mero de seq��ncia n�o confirmado
     */
    private int                   ultimoNS      = 0;

    /**
     * Constante que guarda o tempo (em milesegundos) para expirar o timestamp
     * de um segmento TCP enviado.
     */
    private int                   tempoTimeout      = TCP.T_ESTOURO_RETRANSMISSOES;

    /**
     * Segmento TCP para ser enviado
     */
    private PacoteTCP             pacoteDeEnvio;

    /**
     * Segmento TCP recebido
     */
    private PacoteTCP             pacoteRecebido;

    /**
     * Tamanho da Janela
     */
    private int                   tamanhoJanela;

    private Timer                 retransmissao     = new Timer ();

    class RetransmissaoTask extends TimerTask
    {
        /*
         * (non-Javadoc)
         * 
         * @see this.java.util.TimerTask#run()
         */
        public void run ()
        {
            try
            {
                String[] arg = {""};
                MaquinaDeEstados.this.incNumRetransmissoes();
                MaquinaDeEstados.this.recebePrimitiva(TCP.P_TIMEOUT, arg);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /*
            byte segmento = TCP.S_NENHUM;
            try
            {
//                MaquinaDeEstados.this.numRetransmissoes
                switch (MaquinaDeEstados.this.estadoMEConAtual)
                {
                    case TCP.SYNRCVD:
                        segmento = TCP.S_SYN_ACK;
                        break;
                    case TCP.SYNSENT:
                        segmento = TCP.S_SYN_ACK;
                        break;
                    case TCP.FINWAIT1:
                        segmento = TCP.S_SYN_ACK;
                        break;
                    case TCP.CLOSING:
                        segmento = TCP.S_SYN_ACK;
                        break;
                    case TCP.LASTACK:
                        segmento = TCP.S_SYN_ACK;
                        break;
                    case TCP.TIMEWAIT:
                        segmento = TCP.S_SYN_ACK;
                        break;
                    default:
                        throw new EstadoInvalidoException();
                }
                
                MaquinaDeEstados.this.pacoteDeEnvio.setControle(segmento);

            }
            catch (Exception ex)
            {
                System.err.println ("ME.RetransmissaoTask.run: erro: " + ex.getMessage ());
                System.err.flush ();
            }
*/
        }
    }

    /** Construtor da classe MaquinaDeEstados */
    //    public MaquinaDeEstados() {
    //    }
    /**
     * Construtor da classe MaquinaDeEstados
     * 
     * @param _monitor
     * @param _porta
     * @param _idConexao
     */
    public MaquinaDeEstados (Monitor _monitor, int _porta, int _idConexao)
    {
        this.monitor = _monitor;
        this.estadoMEConexao = TCP.CLOSED;
        this.ipSimuladoLocal = _monitor.getIpSimuladoLocal ();
        this.portaLocal = _porta;
        this.ipSimuladoDestino = "";
        this.portaDestino = 0;
        this.idConexao = _idConexao;
        this.meFrame = new MaquinaDeEstadosFrame (this);
        this.meFrame.atualizaInfoConexao (this.estadoMEConexao, this
                .getIpSimuladoLocalBytePonto (), Integer.toString (this.getPortaLocal ()), "null",
                "null");
    }

    /**
     * M�todo que recebe primitivas e executa as opera��es para atender a a��o.
     * As primitivas est�o definidas na interface TCP.
     * 
     * @param _primitiva
     *        A primitiva que enviada.
     * @param args[]
     *        Um array de argumentos que voc� pode receber adicionalmente
     * @throws Exception
     */
    public void recebePrimitiva (byte _primitiva, String args[]) throws Exception
    {
        System.out.println ("ME.recebePrimitiva: " + TCP.nomePrimitiva[_primitiva]);

        byte novaPrimitiva = TCP.P_NENHUMA;
        byte novoSegmento = TCP.S_NENHUM;
        byte proximoEstado = TCP.NENHUM;
        PacoteTCP novoPacote = null;
        
        if (_primitiva != TCP.P_TIMEOUT)
            clearNumRetransmissoes();
        
        if ( (_primitiva == TCP.P_TIMEOUT) && (getNumRetransmissoes () == TCP.MAX_RETRANSMISSOES))
        {
            System.out.println ("ME.recebePrimitiva: estouro de retransmiss�es");
            proximoEstado = TCP.CLOSED;
            novoSegmento = TCP.S_RST;
            novaPrimitiva = TCP.P_ERROR;
        }
        else
        {
            switch (this.estadoMEConexao)
            {
                case TCP.CLOSED:
                    this.setTempoTimeout (Integer.parseInt (args[3]));
                    this.setTamanhoJanela (Integer.parseInt (args[4]));

                    switch (_primitiva)
                    {
                        case TCP.P_PASSIVEOPEN:
                            proximoEstado = TCP.LISTEN;
                            novaPrimitiva = TCP.P_OPENID;
                            break;
                        case TCP.P_ACTIVEOPEN:
                            proximoEstado = TCP.SYNSENT;
                            novoSegmento = TCP.S_SYN;
                            this.setIpSimuladoDestino (args[0]);
                            this.setPortaDestino (Integer.parseInt (args[1]));
                            break;
                        default:
                            throw new PrimitivaInvalidaException ();
                    }
                    break;
                case TCP.LISTEN:
                    switch (_primitiva)
                    {
                        case TCP.P_CLOSE:
                            proximoEstado = TCP.CLOSED;
                            break;
                        case TCP.P_SEND:
                            proximoEstado = TCP.SYNSENT;
                            novoSegmento = TCP.S_SYN;
                            String ip_origem = Decoder.bytePontoToIpSimulado (this.pacoteRecebido
                                    .getIpSimuladoLocal ());
                            int porta_origem = this.pacoteRecebido.getPortaLocal ();

                            this.setIpSimuladoDestino (ip_origem);
                            this.setPortaDestino (porta_origem);
                            break;
                        default:
                            throw new PrimitivaInvalidaException ();
                    }
                    break;
                case TCP.SYNRCVD:
                    switch (_primitiva)
                    {
                        case TCP.P_CLOSE:
                            proximoEstado = TCP.FINWAIT1;
                            novoSegmento = TCP.S_FIN;
                            break;
                        case TCP.P_TIMEOUT:
                            novoSegmento = TCP.S_SYN_ACK;
                            break;
                        default:
                            throw new PrimitivaInvalidaException ();
                    }
                    break;
                case TCP.SYNSENT:
                    switch (_primitiva)
                    {
                        case TCP.P_CLOSE:
                            proximoEstado = TCP.CLOSED;
                            novoSegmento = TCP.S_RST;
                            break;
                        case TCP.P_TIMEOUT:
                            novoSegmento = TCP.S_SYN;
                            break;
                        default:
                            throw new PrimitivaInvalidaException ();
                    }
                    break;
                case TCP.ESTABLISHED:
                    switch (_primitiva)
                    {
                        case TCP.P_CLOSE:
                            proximoEstado = TCP.FINWAIT1;
                            novoSegmento = TCP.S_FIN;
                            break;
                        default:
                            throw new PrimitivaInvalidaException ();
                    }
                    break;
                case TCP.CLOSEWAIT:
                    switch (_primitiva)
                    {
                        case TCP.P_CLOSE:
                            proximoEstado = TCP.LASTACK;
                            novoSegmento = TCP.S_FIN;
                            break;
                        default:
                            throw new PrimitivaInvalidaException ();
                    }
                    break;
                case TCP.FINWAIT1:
                    switch (_primitiva)
                    {
                        case TCP.P_TIMEOUT:
                            novoSegmento = TCP.S_FIN;
                            break;
                        default:
                            throw new PrimitivaInvalidaException ();
                    }
                    break;
                /*
                case TCP.FINWAIT2:
                    switch (_primitiva)
                    { 
                        default: throw new
                        PrimitivaInvalidaException ();
                    } 
                    break;
                */
                case TCP.CLOSING:
                    switch (_primitiva)
                    {
                        case TCP.P_TIMEOUT:
                            novoSegmento = TCP.S_FIN;
                            break;
                        default:
                            throw new PrimitivaInvalidaException ();
                    }
                    break;

                case TCP.LASTACK:
                    switch (_primitiva)
                    {
                        case TCP.P_TIMEOUT:
                            novoSegmento = TCP.S_FIN;
                            break;
                        default:
                            throw new PrimitivaInvalidaException ();
                    }
                    break;
                case TCP.TIMEWAIT:
                    switch (_primitiva)
                    {
                        case TCP.P_TIMEOUT:
                            proximoEstado = TCP.CLOSED;
                            novaPrimitiva = TCP.P_TERMINATE;
                            break;
                        default:
                            throw new PrimitivaInvalidaException ();
                    }
                    break;
                default:
                    throw new EstadoInvalidoException ();
            }
        }
        
        String func = TCP.nomeSegmento (novoSegmento);
        String seta;
        
        if (_primitiva == TCP.P_TIMEOUT)
            seta = TCP.SETA_NENHUMA_PRIM;
        else
            seta = TCP.SETA_RECEBE_PRIM;

        // atualiza exibi��o do estado atual com a primitiva recebida e o
        // segmento criado
        if (novoSegmento != TCP.S_NENHUM)
        {
            if (_primitiva == TCP.P_TIMEOUT)
            {
                novoPacote = this.pacoteDeEnvio;
                novoPacote.setControle(novoSegmento);
            }
            else
            {
                novoPacote = new PacoteTCP (
                        getIpSimuladoLocalBytePonto (),
                        getIpSimuladoDestinoBytePonto (),
                        new CampoTCP (2, getPortaLocal ()),
                        new CampoTCP (2, getPortaDestino ()),
                        new CampoTCP (4, getProximoNS()),
                        new CampoTCP (4, getEsperadoNS()),
                        new CampoTCP (1, (short) 0),            // Offset
                        new CampoTCP (1, novoSegmento),
                        new CampoTCP (2, getTamanhoJanela ()),
                        new CampoTCP (2, 0),                    // checksum
                        new CampoTCP (2, 0),                    // ponteiro
                        "");
                
            }   
            
            func = TCP.nomeSegmento (novoPacote.getControle ()) + "("
                    + novoPacote.getNumSequencia () + ","
                    + novoPacote.getTamanho () + "," + novoPacote.getNumAck ()
                    + "," + novoPacote.getJanela () + ")";
            seta += TCP.SETA_ENVIA_SEG;
        }
        this.meFrame.atualizaDadosEstado (TCP.nomeEstado[getEstadoMEConexao()],
                TCP.nomePrimitiva[_primitiva], seta, func);

        if (proximoEstado != TCP.NENHUM)
            setEstadoMEConexao (proximoEstado);

        if ((_primitiva != TCP.P_TIMEOUT)
                || (novaPrimitiva != TCP.P_NENHUMA))
            enviaPrimitiva (novaPrimitiva, new String[0]);
             
        if (novoSegmento != TCP.S_NENHUM)
            enviaSegmentoTCP (novoPacote);

        System.out.println ("recebePrimitiva: fim");
    } // recebePrimitiva

    /**
     * M�todo que envia primitivas
     * 
     * @param _primitiva
     *        A primitiva que est� sendo enviada.
     * @param args[]
     *        Um array de argumentos que a aplica��o pode enviar.
     * @exception PrimitivaInvalidaException
     *            Caso ocorra algum erro ou exce��o, lan�a (throw) para quem
     *            chamou o m�todo.
     */
    public void enviaPrimitiva (int _primitiva, String args[])
    {
        System.out.println ("enviaPrimitiva: " + TCP.nomePrimitiva[_primitiva]);

        String seta;

        // atualiza exibi��o do pr�ximo estado e nova primitiva
        if (_primitiva == TCP.P_NENHUMA)
            seta = "";
        else
            seta = TCP.SETA_ENVIA_PRIM;

        this.meFrame.atualizaDadosEstado (TCP.nomeEstado[this.estadoMEConexao],
                TCP.nomePrimitiva[_primitiva], seta, TCP.nomeSegmento (TCP.S_NENHUM));

        if (this.estadoMEConexao == TCP.CLOSED)
        {
            this.meFrame.habilitaNovaConexao (true);
            this.ipSimuladoDestino = "";
            this.portaDestino = 0;
            this.getMonitor ().getTabelaDeConexoes ().alteraDestino (this.ipSimuladoLocal,
                    Integer.toString (this.portaLocal), "", "");
        }

        this.meFrame.atualizaInfoConexao (this.estadoMEConexao, this
                .getIpSimuladoLocalBytePonto (), Integer.toString (this.getPortaLocal ()), this
                .getIpSimuladoDestinoBytePonto (), Integer.toString (this.getPortaDestino ()));

        System.out.println ("enviaPrimitiva: fim");
    }

    /**
     * M�todo que recebe segmentos TCP e faz o tratamento desse pacote
     * 
     * @param _pacoteTCP
     *        O segmento TCP recebido
     * @exception Exception
     *            Caso ocorra algum erro ou exce��o, lan�a (throw) para quem
     *            chamou o m�todo.
     */
    public void recebeSegmentoTCP (PacoteTCP _pacoteTCP) throws Exception
    {
        this.pacoteRecebido = _pacoteTCP;

        System.out.println ("recebeSegmentoTCP: "
                + TCP.nomeSegmento (this.pacoteRecebido.getControle ()));

        byte novaPrimitiva = TCP.P_NENHUMA;
        byte novoSegmento = TCP.S_NENHUM;
        byte proximoEstado = TCP.NENHUM;

        switch (this.estadoMEConexao)
        {
            case TCP.LISTEN:
                switch (this.pacoteRecebido.getControle ())
                {
                    case TCP.S_SYN:
                        proximoEstado = TCP.SYNRCVD;
                        novoSegmento = TCP.S_SYN_ACK;
                        String ip_origem = Decoder.bytePontoToIpSimulado (this.pacoteRecebido
                                .getIpSimuladoLocal ());
                        int porta_origem = this.pacoteRecebido.getPortaLocal ();

                        this.setIpSimuladoDestino (ip_origem);
                        this.setPortaDestino (porta_origem);
                        break;
                    default:
                        throw new SegmentoInvalidoException ();
                }
                break;
            case TCP.SYNRCVD:
                switch (this.pacoteRecebido.getControle ())
                {
                    case TCP.S_SYN:
                        proximoEstado = TCP.SYNRCVD;
                        novoSegmento = TCP.S_SYN_ACK;
                        break;
                    case TCP.S_ACK:
                        proximoEstado = TCP.ESTABLISHED;
                        novaPrimitiva = TCP.P_OPENSUCCESS;
                        break;
                    case TCP.S_RST:
                        proximoEstado = TCP.LISTEN;
                        break;
                    default:
                        throw new SegmentoInvalidoException ();
                }
                break;
            case TCP.SYNSENT:
                switch (this.pacoteRecebido.getControle ())
                {
                    case TCP.S_SYN:
                        proximoEstado = TCP.SYNRCVD;
                        novoSegmento = TCP.S_ACK;
                        break;
                    case TCP.S_SYN_ACK:
                        proximoEstado = TCP.ESTABLISHED;
                        novoSegmento = TCP.S_ACK;
                        novaPrimitiva = TCP.P_OPENSUCCESS;
                        break;
                    case TCP.S_RST:
                        proximoEstado = TCP.CLOSED;
                        novaPrimitiva = TCP.P_ERROR;
                        break;
                    default:
                        throw new SegmentoInvalidoException ();
                }
                break;
            case TCP.ESTABLISHED:
                switch (this.pacoteRecebido.getControle ())
                {
                    case TCP.S_FIN:
                        proximoEstado = TCP.CLOSEWAIT;
                        novoSegmento = TCP.S_ACK;
                        break;
                    case TCP.S_SYN_ACK:
                        proximoEstado = TCP.ESTABLISHED;
                        novoSegmento = TCP.S_ACK;
                        break;
                    case TCP.S_RST:
                        proximoEstado = TCP.CLOSED;
                        novaPrimitiva = TCP.P_ERROR;
                        break;
                    default:
                        throw new SegmentoInvalidoException ();
                }
                break;
            case TCP.CLOSEWAIT:
                switch (this.pacoteRecebido.getControle ())
                {
                    case TCP.S_FIN:
                        proximoEstado = TCP.CLOSEWAIT;
                        novoSegmento = TCP.S_ACK;
                        break;
                    case TCP.S_RST:
                        proximoEstado = TCP.CLOSED;
                        novaPrimitiva = TCP.P_ERROR;
                        break;
                    default:
                        throw new SegmentoInvalidoException ();
                }
                break;
            case TCP.FINWAIT1:
                switch (this.pacoteRecebido.getControle ())
                {
                    case TCP.S_ACK:
                        proximoEstado = TCP.FINWAIT2;
                        break;
                    case TCP.S_FIN:
                        proximoEstado = TCP.CLOSING;
                        novoSegmento = TCP.S_ACK;
                        break;
                    case TCP.S_RST:
                        proximoEstado = TCP.CLOSED;
                        novaPrimitiva = TCP.P_ERROR;
                        break;
                    default:
                        throw new SegmentoInvalidoException ();
                }
                break;
            case TCP.FINWAIT2:
                switch (this.pacoteRecebido.getControle ())
                {
                    case TCP.S_FIN:
                        proximoEstado = TCP.TIMEWAIT;
                        novoSegmento = TCP.S_ACK;
                        break;
                    case TCP.S_RST:
                        proximoEstado = TCP.CLOSED;
                        novaPrimitiva = TCP.P_ERROR;
                        break;
                    default:
                        throw new SegmentoInvalidoException ();
                }
                break;
            case TCP.CLOSING:
                switch (this.pacoteRecebido.getControle ())
                {
                    case TCP.S_ACK:
                        proximoEstado = TCP.TIMEWAIT;
                        break;
                    case TCP.S_FIN:
                        proximoEstado = TCP.CLOSING;
                        novoSegmento = TCP.S_ACK;
                        break;
                    case TCP.S_RST:
                        proximoEstado = TCP.CLOSED;
                        novaPrimitiva = TCP.P_ERROR;
                        break;
                    default:
                        throw new SegmentoInvalidoException ();
                }
                break;
            case TCP.LASTACK:
                switch (this.pacoteRecebido.getControle ())
                {
                    case TCP.S_ACK:
                        proximoEstado = TCP.CLOSED;
                        novaPrimitiva = TCP.P_TERMINATE;
                        break;
                    case TCP.S_FIN:
                        proximoEstado = TCP.LASTACK;
                        novoSegmento = TCP.S_ACK;
                        break;
                    case TCP.S_RST:
                        proximoEstado = TCP.CLOSED;
                        novaPrimitiva = TCP.P_ERROR;
                        break;
                    default:
                        throw new SegmentoInvalidoException ();
                }
                break;
            case TCP.TIMEWAIT:
                switch (this.pacoteRecebido.getControle ())
                {
                    case TCP.S_FIN:
                        proximoEstado = TCP.TIMEWAIT;
                        novoSegmento = TCP.S_ACK;
                        break;
                    default:
                        throw new SegmentoInvalidoException ();
                }
                break;
            default:
                throw new EstadoInvalidoException ();
        }

        String seta;
        String func = TCP.nomeSegmento (this.pacoteRecebido.getControle ()) + "("
                + this.pacoteRecebido.getNumSequencia () + "," + this.pacoteRecebido.getTamanho ()
                + "," + this.pacoteRecebido.getNumAck () + "," + this.pacoteRecebido.getJanela ()
                + ")";

        // atualiza exibi��o do estado atual com o segmento recebido
        seta = TCP.SETA_NENHUMA_PRIM + TCP.SETA_RECEBE_SEG;
        this.meFrame.atualizaDadosEstado (TCP.nomeEstado[this.estadoMEConexao],
                TCP.nomePrimitiva[TCP.P_NENHUMA], seta, func);

        // Ajusta tamanho da janela
        if (this.pacoteRecebido.getJanela () < this.getTamanhoJanela ())
            this.setTamanhoJanela (this.pacoteRecebido.getJanela ());

        // atualiza exibi��o do estado atual e novo segmento
        func = TCP.nomeSegmento (novoSegmento);
        seta = "";
        if (novoSegmento != TCP.S_NENHUM)
        {
            func += "(" + "0" + "," + "0" + "," + "0" + "," + this.getTamanhoJanela () + ")";
            seta = TCP.SETA_NENHUMA_PRIM + TCP.SETA_ENVIA_SEG;
            this.meFrame.atualizaDadosEstado (TCP.nomeEstado[this.estadoMEConexao],
                    TCP.nomePrimitiva[TCP.P_NENHUMA], seta, func);
        }

        if (proximoEstado != TCP.NENHUM)
            this.estadoMEConexao = proximoEstado;

        String args[] = {""};
        enviaPrimitiva (novaPrimitiva, args);

        if (novoSegmento != TCP.S_NENHUM)
        {
            setPacoteDeEnvio (new PacoteTCP (
                    this.getIpSimuladoLocalBytePonto (),
                    this
                    .getIpSimuladoDestinoBytePonto (),
                    new CampoTCP (2, this.getPortaLocal ()),
                    new CampoTCP (2, this.getPortaDestino ()),
                    new CampoTCP (4, 0L),
                    new CampoTCP (4, 0L),
                    new CampoTCP (1, (short) 0), 
                    new CampoTCP (1, novoSegmento),
                    new CampoTCP (2, this.getTamanhoJanela ()), 
                    new CampoTCP (2, 0), 
                    new CampoTCP (2, 0), 
                    ""));

            enviaSegmentoTCP (this.pacoteDeEnvio);
        }

        System.out.println ("recebeSegmentoTCP: fim");
    } // recebeSegmentoTCP

    /**
     * M�todo que envia segmento TCP
     * 
     * @param _pacoteTCP
     *        O segmento TCP enviado
     * @exception Exception
     *            Caso ocorra algum erro ou exce��o, lan�a (throw) para quem
     *            chamou o m�todo.
     */
    public void enviaSegmentoTCP (PacoteTCP _pacoteTCP) throws Exception
    {
        if (_pacoteTCP.getControle () == TCP.S_NENHUM)
            return;

        setPacoteDeEnvio (_pacoteTCP);
        this.pacoteDeEnvio.geraOpcoes ();

        System.out.println ("enviaSegmentoTCP: "
                + TCP.nomeSegmento (this.pacoteDeEnvio.getControle ()));

        String ip = IpSimulada.descobreNomeIPSimulado (this.pacoteDeEnvio.getIpSimuladoRemoto ());
        int porta = Integer.parseInt (IpSimulada.descobrePortaIPSimulado (this.pacoteDeEnvio
                .getIpSimuladoRemoto ()));

        System.out.println ("enviaSegmentoTCP: destino " + ip + " : " + porta);

        // Envia segmento � camada IP simulada
        this.monitor.getProtocoloTCP ().getCamadaIpSimulada ().transmite (ip,
                this.pacoteDeEnvio.toString (), this.pacoteDeEnvio.toString ().length (), porta);

        // agenda timeout se n�o for �ltima retransmiss�o
        if (getNumRetransmissoes () < TCP.MAX_RETRANSMISSOES)
        {
            int timeout;
            
            if (this.estadoMEConexao == TCP.TIMEWAIT)
                timeout = 2 * TCP.T_TIMEOUT_MSL;
            else
                timeout = this.getTempoTimeout();
                
            this.retransmissao.schedule (new RetransmissaoTask (), timeout);
        }
        

        System.out.println ("enviaSegmentoTCP: fim");
    }

    /**
     * M�todo acessador para o atributo monitor.
     * 
     * @return A refer�ncia para o atributo monitor.
     */
    public Monitor getMonitor ()
    {
        return this.monitor;
    }

    /**
     * M�todo modificador para o atributo monitor.
     * 
     * @param _monitor
     *        Novo valor para o atributo monitor.
     */
    public void setMonitor (Monitor _monitor)
    {
        this.monitor = _monitor;
    }

    /**
     * M�todo modificador para o atributo meFrame.
     * 
     * @param _meFrame
     *        Novo valor para o atributo meFrame.
     */
    public void setMeFrame (MaquinaDeEstadosFrame _meFrame)
    {
        this.meFrame = _meFrame;
    }

    /**
     * M�todo acessador para o atributo meFrame.
     * 
     * @return A refer�ncia para o atributo meFrame.
     */
    public MaquinaDeEstadosFrame getMeFrame ()
    {
        return this.meFrame;
    }

    /**
     * M�todo acessador para o atributo idConexao.
     * 
     * @return A refer�ncia para o atributo idConexao.
     */
    public int getIdConexao ()
    {
        return this.idConexao;
    }

    /**
     * M�todo modificador para o atributo idConexao.
     * 
     * @param id
     *        Novo valor para o atributo idConexao.
     */
    public void setIdConexao (int id)
    {
        this.idConexao = id;
    }

    /**
     * M�todo acessador para o atributo ipSimuladoLocal.
     * 
     * @return A refer�ncia para o atributo ipSimuladoLocal.
     */
    public String getIpSimuladoLocal ()
    {
        return this.ipSimuladoLocal;
    }

    /**
     * M�todo modificador para o atributo ipSimuladoLocal.
     * 
     * @param _ipSimuladoLocal
     *        Novo valor para o atributo ipSimuladoLocal.
     */
    public void setIpSimuladoLocal (String _ipSimuladoLocal)
    {
        this.ipSimuladoLocal = _ipSimuladoLocal;
    }

    /**
     * M�todo acessador para o atributo ipSimuladoLocal.
     * 
     * @return A refer�ncia para o atributo ipSimuladoLocal.
     */
    public String getIpSimuladoLocalBytePonto ()
    {
        return Decoder.ipSimuladoToBytePonto (this.ipSimuladoLocal);
    }

    /**
     * M�todo modificador para o atributo ipSimuladoLocal.
     * 
     * @param _ipSimuladoLocal
     *        Novo valor para o atributo ipSimuladoLocal.
     */
    public void setIpSimuladoLocalBytePonto (String _ipSimuladoLocal)
    {
        this.ipSimuladoLocal = Decoder.bytePontoToIpSimulado (_ipSimuladoLocal);
    }

    /**
     * M�todo acessador para o atributo portaLocal.
     * 
     * @return A refer�ncia para o atributo portaLocal.
     */
    public int getPortaLocal ()
    {
        return this.portaLocal;
    }

    /**
     * M�todo modificador para o atributo portaLocal.
     * 
     * @param porta
     *        Novo valor para o atributo portaLocal.
     */
    public void setPortaLocal (int porta)
    {
        this.portaLocal = porta;
    }

    /**
     * M�todo acessador para o atributo ipSimuladoDestino.
     * 
     * @return A refer�ncia para o atributo ipSimuladoDestino.
     */
    public String getIpSimuladoDestino ()
    {
        return this.ipSimuladoDestino;
    }

    /**
     * M�todo acessador para o atributo ipSimuladoDestino.
     * 
     * @return A refer�ncia para o atributo ipSimuladoDestino.
     */
    public String getIpSimuladoDestinoBytePonto ()
    {
        return Decoder.ipSimuladoToBytePonto (this.ipSimuladoDestino);
    }

    /**
     * M�todo modificador para o atributo ipSimuladoDestino.
     * 
     * @param _ipSimuladoDestino
     *        Novo valor para o atributo ipSimuladoDestino.
     */
    public void setIpSimuladoDestino (String _ipSimuladoDestino)
    {
        this.ipSimuladoDestino = _ipSimuladoDestino;
    }

    /**
     * M�todo acessador para o atributo nomeEstacaoDestino.
     * 
     * @return A refer�ncia para o atributo nomeEstacaoDestino.
     */
    public String getNomeEstacaoDestino ()
    {
        return this.nomeEstacaoDestino;
    }

    /**
     * M�todo modificador para o atributo nomeEstacaoDestino.
     * 
     * @param _nomeEstacaoDestino
     *        Novo valor para o atributo nomeEstacaoDestino.
     */
    public void setNomeEstacaoDestino (String _nomeEstacaoDestino)
    {
        this.nomeEstacaoDestino = _nomeEstacaoDestino;
    }

    /**
     * M�todo acessador para o atributo portaDestino.
     * 
     * @return A refer�ncia para o atributo portaDestino.
     */
    public int getPortaDestino ()
    {
        return this.portaDestino;
    }

    /**
     * M�todo modificador para o atributo portaDestino.
     * 
     * @param _portaDestino
     *        Novo valor para o atributo portaDestino.
     */
    public void setPortaDestino (int _portaDestino)
    {
        this.portaDestino = _portaDestino;
    }

    /**
     * @return Retorna o tamanho da janela atual.
     */
    public int getTamanhoJanela ()
    {
        return this.tamanhoJanela;
    }

    /**
     * @param tamanho
     *        Ajusta o tamanho da janela a ser utilizada.
     */
    public void setTamanhoJanela (int tamanho)
    {
        this.tamanhoJanela = tamanho;
    }

    /**
     * @return Returns the estadoMEConAtual.
     */
    public byte getEstadoMEConexao ()
    {
        return this.estadoMEConexao;
    }

    /**
     * @return Returns the numRetransmissoes.
     */
    public int getNumRetransmissoes ()
    {
        return this.numRetransmissoes;
    }

    /**
     * Incrementa a contagem de retransmiss�es
     */
    public void incNumRetransmissoes ()
    {
        this.numRetransmissoes ++;
    }

    /**
     * Zera a contagem de retransmiss�es
     */
    public void clearNumRetransmissoes ()
    {
        this.numRetransmissoes = 0;
    }
    
    /**
     * @return Returns the tempoTimeout.
     */
    public int getTempoTimeout ()
    {
        return this.tempoTimeout;
    }

    /**
     * @param tempo
     *        The tempoTimeout to set.
     */
    public void setTempoTimeout (int tempo)
    {
        this.tempoTimeout = tempo;
    }
    
    /**
     * @return Returns the proximoNS.
     */
    public int getProximoNS ()
    {
        return this.proximoNS;
    }
    
    /**
     * @param _numSequencia The proximoNS to set.
     */
    public void setProximoNS (int _numSequencia)
    {
        this.proximoNS = _numSequencia;
    }
    /**
     * @return Returns the esperadoNS.
     */
    public int getEsperadoNS ()
    {
        return this.esperadoNS;
    }
    /**
     * @param _esperadoNS The esperadoNS to set.
     */
    public void setEsperadoNS (int _esperadoNS)
    {
        this.esperadoNS = _esperadoNS;
    }
    /**
     * @return Returns the pacoteDeEnvio.
     */
    public PacoteTCP getPacoteDeEnvio ()
    {
        return this.pacoteDeEnvio;
    }
    /**
     * @param _pacoteDeEnvio The pacoteDeEnvio to set.
     */
    public void setPacoteDeEnvio (PacoteTCP _pacoteDeEnvio)
    {
        this.pacoteDeEnvio = _pacoteDeEnvio;
    }
    /**
     * @return Returns the pacoteRecebido.
     */
    public PacoteTCP getPacoteRecebido ()
    {
        return this.pacoteRecebido;
    }
    /**
     * @param _pacoteRecebido The pacoteRecebido to set.
     */
    public void setPacoteRecebido (PacoteTCP _pacoteRecebido)
    {
        this.pacoteRecebido = _pacoteRecebido;
    }
    /**
     * @return Returns the retransmissao.
     */
    public Timer getRetransmissao ()
    {
        return this.retransmissao;
    }
    /**
     * @param _retransmissao The retransmissao to set.
     */
    public void setRetransmissao (Timer _retransmissao)
    {
        this.retransmissao = _retransmissao;
    }
    /**
     * @return Returns the ultimoNS.
     */
    public int getUltimoNS ()
    {
        return this.ultimoNS;
    }
    /**
     * @param _ultimoNS The ultimoNS to set.
     */
    public void setUltimoNS (int _ultimoNS)
    {
        this.ultimoNS = _ultimoNS;
    }
    /**
     * @param _estadoMEConAtual The estadoMEConAtual to set.
     */
    public void setEstadoMEConexao (byte _estadoMEConAtual)
    {
        this.estadoMEConexao = _estadoMEConAtual;
    }
}//fim da classe MaquinaDeEstados
