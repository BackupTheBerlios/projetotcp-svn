package br.usp.larc.tcp.protocolo;

/*
 * @(#)MaquinaDeEstados.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo
 *
 */

import br.usp.larc.tcp.aplicacao.MaquinaDeEstadosFrame;
import br.usp.larc.tcp.excecoes.PrimitivaInvalidaException;
import br.usp.larc.tcp.excecoes.SegmentoInvalidoException;
import br.usp.larc.tcp.ipsimulada.IpSimulada;

/** 
 * Classe que representa a Máquina de Estado do seu Protocolo (que pode ter n).
 * Detalhes e dicas de implementação podem ser consultadas nas Apostilas.
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a 
 * criatividade na implementação do seu projeto.
 *  
 *
 * @author	Laboratório de Arquitetura e Redes de Computadores
 * @version	1.0 Agosto 2003
 */
public class MaquinaDeEstados
{    
    /** 
     * Atributo que representa o frame associado a Máquina de Estados
     */
    private MaquinaDeEstadosFrame meFrame;

    /** 
     * Atributo que representa o monitor associado a Máquina de Estados
     */
    private Monitor monitor;

    /** 
     * Atributo que representa o id da Conexão associado a Máquina de Estados
     */
    private int idConexao;

    /** 
     * IP Simulado Local associado a Máquina de Estados
     */
    private String ipSimuladoLocal;

    /** 
     * IP Simulado Destino associado a Máquina de Estados
     */
    private String ipSimuladoDestino;

    /** 
     * Nome da estação destino associado a Máquina de Estados
     */
    private String nomeEstacaoDestino;
    
    /** 
     * Porta TCP local associada a Máquina de Estados
     */
    private int portaLocal;

    /** 
     * Porta TCP remota associada a Máquina de Estados
     */
    private int portaDestino;

    /** 
     * O estado atual da máquina de conexão e desconexão
     */
    private byte estadoMEConAtual;
    
    /** 
     * Constante que guarda o número de retransmissões de um segmeto TCP com
     * timestamp expirado.
     */
    private static final int numRetransmissoes = TCP.MAX_RETRANSMISSOES;
    
    /** 
     * Constante que guarda o tempo (em milesegundos) para expirar o timestamp
     * de um segmento TCP enviado.
     */
    private int tempoTimeout = TCP.T_ESTOURO_RETRANSMISSOES;


    /** 
     * Segmento TCP para ser enviado
     */
    private PacoteTCP pacoteDeEnvio;
    
    /** 
     * Segmento TCP recebido
     */
    private PacoteTCP pacoteRecebido;
    
    /**
     * Tamanho da Janela
     */
    private int tamanhoJanela;
    
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
    public MaquinaDeEstados(Monitor _monitor, int _porta, int _idConexao) {
        this.monitor = _monitor;
        this.estadoMEConAtual = TCP.CLOSED;
        this.ipSimuladoLocal = _monitor.getIpSimuladoLocal();
        this.portaLocal = _porta;
        this.ipSimuladoDestino = "";
        this.portaDestino = 0;
        this.idConexao = _idConexao;
        this.meFrame = new MaquinaDeEstadosFrame(this);
        this.meFrame.atualizaInfoConexao(
                this.estadoMEConAtual, 
                this.getIpSimuladoLocalBytePonto(),
                Integer.toString(this.getPortaLocal()),  "null",  "null");
    }

    /**
     * Método que recebe primitivas e executa as operações para atender a ação.
     * As primitivas estão definidas na interface TCP.
     * 
     * @param _primitiva
     *        A primitiva que enviada.
     * @param args[]
     *        Um array de argumentos que você pode receber adicionalmente
     * @throws Exception
     */
    public void recebePrimitiva (byte _primitiva, String args[])
        throws Exception
    {
        System.out.println ("recebePrimitiva: " + TCP.nomePrimitiva[_primitiva]);
    	
        byte novaPrimitiva = TCP.P_NENHUMA;
        byte novoSegmento = TCP.S_NENHUM;
        byte proximoEstado = TCP.NENHUM;

        switch (this.estadoMEConAtual)
        {
            case TCP.CLOSED:
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
                        this.setPortaDestino (porta_origem);                        break;
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
    			throw new PrimitivaInvalidaException();
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
    			throw new PrimitivaInvalidaException();
			}
    		break;
/*    		
    	case TCP.FINWAIT1:
    		switch (_primitiva)
			{
    		default:
    			throw new PrimitivaInvalidaException();
			}
    		break;
    	case TCP.FINWAIT2:
    		switch (_primitiva)
			{
    		default:
    			throw new PrimitivaInvalidaException();
			}
    		break;
    	case TCP.CLOSING:
    		switch (_primitiva)
			{
    		default:
    			throw new PrimitivaInvalidaException();
			}
    		break;
    		*/
    	case TCP.LASTACK:
    		switch (_primitiva)
			{
    		case TCP.P_TIMEOUT:
    			proximoEstado = TCP.CLOSED; 
    			novoSegmento = TCP.P_TERMINATE;
    			break;
    		default:
    			throw new PrimitivaInvalidaException();
			}
    		break;
    	case TCP.TIMEWAIT:
    		switch (_primitiva)
			{
    		case TCP.P_TIMEOUT:
    			proximoEstado = TCP.CLOSED; 
    			novoSegmento = TCP.P_TERMINATE;
    			break;
    		default:
    			throw new PrimitivaInvalidaException();
			}
    		break;
    	default:
			throw new Exception("Estado desconhecido");
		}
        
        String func = TCP.nomeSegmento (novoSegmento);
        String seta = TCP.SETA_RECEBE_PRIM;
       
        // atualiza exibição do estado atual com a primitiva recebida e o
        // segmento criado
        if (novoSegmento != TCP.S_NENHUM)
        {
            func += "(" + "0" + "," + "0" + "," + "0" + "," + this.getTamanhoJanela ()
                    + ")";
            seta += TCP.SETA_ENVIA_SEG;
        }        
        this.meFrame.atualizaDadosEstado (TCP.nomeEstado[this.estadoMEConAtual],
                TCP.nomePrimitiva[_primitiva], seta, func);
                        
        this.meFrame.atualizaInfoConexao (proximoEstado,
                this.getIpSimuladoLocalBytePonto (),
                Integer.toString (this.getPortaLocal ()),       
                this.getIpSimuladoDestinoBytePonto (),
                Integer.toString (this.getPortaDestino ()));       

        this.estadoMEConAtual = proximoEstado;
                
        enviaPrimitiva(novaPrimitiva, new String[0]);

    	if (novoSegmento != TCP.S_NENHUM)
    	{
        	System.out.println("recebePrimitiva: novo segmento");
        	
        	this.setTempoTimeout  (Integer.parseInt(args[3]));
        	this.setTamanhoJanela (Integer.parseInt(args[4]));
        	
        	this.pacoteDeEnvio = new PacoteTCP (
        	        this.getIpSimuladoLocalBytePonto(),
        	        this.getIpSimuladoDestinoBytePonto(),
        	        new CampoTCP(2, this.getPortaLocal()),
        	        new CampoTCP(2, this.getPortaDestino()),
        	        new CampoTCP(4, 0L),
        	        new CampoTCP(4, 0L),
        	        new CampoTCP(1, (short) 0),
        	        new CampoTCP(1, novoSegmento),             // tipo
        	        new CampoTCP(2, this.getTamanhoJanela()),  // tam. janela
        	        new CampoTCP(2, 0),
        	        new CampoTCP(2, 0),
        	        new CampoTCP(4, 0L),							// Opções
        	        args[2]);
        	
        	System.out.println("recebePrimitiva: envia segmento");
        	enviaSegmentoTCP(this.pacoteDeEnvio);
    	}

    	System.out.println("recebePrimitiva: fim");
	} // recebePrimitiva
    
    /** 
     * Método que envia primitivas
     *
     * @param _primitiva A primitiva que está sendo enviada.
     * @param args[] Um array de argumentos que a aplicação pode enviar.
     * @exception PrimitivaInvalidaException  Caso ocorra algum erro ou exceção, lança (throw) 
     * para quem chamou o método.
     */
    public void enviaPrimitiva(int _primitiva, String args[]) throws PrimitivaInvalidaException
	{   
        // FIXME enviaPrimitiva: O que vai aqui?
        System.out.println ("enviaPrimitiva: " + TCP.nomePrimitiva[_primitiva]);
        
        String seta;
        
        // atualiza exibição do próximo estado e nova primitiva
        if (_primitiva == TCP.P_NENHUMA)
            seta = "";
        else
            seta = TCP.SETA_ENVIA_PRIM;
        this.meFrame.atualizaDadosEstado (TCP.nomeEstado[this.estadoMEConAtual],
                TCP.nomePrimitiva[_primitiva], seta, TCP.nomeSegmento (TCP.S_NENHUM));
        
        if (this.estadoMEConAtual == TCP.CLOSED)
            this.meFrame.habilitaNovaConexao(true);

        System.out.println("enviaPrimitiva: fim");
    }
    
    /**
     * Método que recebe segmentos TCP e faz o tratamento desse pacote
     * 
     * @param _pacoteTCP
     *        O segmento TCP recebido
     * @exception Exception
     *            Caso ocorra algum erro ou exceção, lança (throw) para quem
     *            chamou o método.
     */
    public void recebeSegmentoTCP (PacoteTCP _pacoteTCP) throws Exception
    {
        this.pacoteRecebido = _pacoteTCP;

        System.out.println ("recebeSegmentoTCP: " + TCP.nomeSegmento(this.pacoteRecebido.getControle()));

        byte novaPrimitiva = TCP.P_NENHUMA;
        byte novoSegmento = TCP.S_NENHUM;
        byte proximoEstado = TCP.NENHUM;
        
        switch (this.estadoMEConAtual)
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
                        this.setPortaDestino (porta_origem);                        break;
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
                throw new SegmentoInvalidoException ();
        }
        
        String seta;
        String func = TCP.nomeSegmento (this.pacoteRecebido.getControle ()) + "("
                      + this.pacoteRecebido.getNumSequencia () + ","
                      + this.pacoteRecebido.getTamanho () + ","
                      + this.pacoteRecebido.getNumAck () + ","
                      + this.pacoteRecebido.getJanela () + ")";
        
        // atualiza exibição do estado atual com o segmento recebido
        seta = TCP.SETA_NENHUMA_PRIM + TCP.SETA_RECEBE_SEG;
        this.meFrame.atualizaDadosEstado (TCP.nomeEstado[this.estadoMEConAtual],
                TCP.nomePrimitiva[TCP.P_NENHUMA], seta, func);

        // Ajusta tamanho da janela
        if (this.pacoteRecebido.getJanela () < this.getTamanhoJanela ())
            this.setTamanhoJanela (this.pacoteRecebido.getJanela ());
        
        // atualiza exibição do estado atual e novo segmento
        func = TCP.nomeSegmento (novoSegmento);
        seta = "";
        if (novoSegmento != TCP.S_NENHUM)
        {
            func += "(" + "0" + "," + "0" + "," + "0"
                   + "," + this.getTamanhoJanela () + ")";
            seta = TCP.SETA_NENHUMA_PRIM + TCP.SETA_ENVIA_SEG;
            this.meFrame.atualizaDadosEstado (TCP.nomeEstado[this.estadoMEConAtual],
                    TCP.nomePrimitiva[TCP.P_NENHUMA], seta, func);
        }

        this.estadoMEConAtual = proximoEstado;
        
        String args[] = {""};
        enviaPrimitiva(novaPrimitiva, args);         

        this.meFrame.atualizaInfoConexao (this.getEstadoMEConAtual (), this
                .getIpSimuladoLocalBytePonto (),
                Integer.toString (this.getPortaLocal ()),       
                this.getIpSimuladoDestinoBytePonto (),
                Integer.toString (this.getPortaDestino ()));       
        
        if (novoSegmento != TCP.S_NENHUM)
        {
            this.pacoteDeEnvio = new PacoteTCP (
                    this.getIpSimuladoLocalBytePonto (),
                    this.getIpSimuladoDestinoBytePonto (),
                    new CampoTCP (2, this.getPortaLocal ()),
                    new CampoTCP (2, this.getPortaDestino ()),
                    new CampoTCP (4, 0L),
                    new CampoTCP (4, 0L),
                    new CampoTCP (1, (short) 0),
                    new CampoTCP (1, novoSegmento),
                    new CampoTCP (2, this.getTamanhoJanela ()),
                    new CampoTCP (2, 0),
                    new CampoTCP (2, 0),
                    new CampoTCP (4, 0L), // Opções
                    this.pacoteRecebido.getDados ());

            enviaSegmentoTCP (this.pacoteDeEnvio);
        }

        
        System.out.println("recebeSegmentoTCP: fim");
    } // recebeSegmentoTCP
    
    /** 
     * Método que envia segmento TCP
     *
     * @param _pacoteTCP O segmento TCP enviado
     * @exception Exception  Caso ocorra algum erro ou exceção, lança (throw) 
     * para quem chamou o método.
     */
    public void enviaSegmentoTCP (PacoteTCP _pacoteTCP) throws Exception
    {
        if (_pacoteTCP.getControle() == TCP.S_NENHUM)
            return;
        
        this.pacoteDeEnvio = _pacoteTCP;
        this.pacoteDeEnvio.geraOpcoes ();

        System.out.println ("enviaSegmentoTCP: " + TCP.nomeSegmento(this.pacoteDeEnvio.getControle()));

        String ip = IpSimulada.descobreNomeIPSimulado (this.pacoteDeEnvio
                .getIpSimuladoRemoto ());
        int porta = Integer.parseInt (IpSimulada
                .descobrePortaIPSimulado (this.pacoteDeEnvio.getIpSimuladoRemoto ()));

        System.out.println ("enviaSegmentoTCP: destino " + ip + " : " + porta);

        // Envia segmento à camada IP simulada
        this.monitor.getProtocoloTCP ().getCamadaIpSimulada ().transmite (ip,
                this.pacoteDeEnvio.toString (), this.pacoteDeEnvio.toString ().length (),
                porta);

        System.out.println("enviaSegmentoTCP: fim");
    }
    
    /** Método acessador para o atributo monitor.
     * @return A referência para o atributo monitor.
     *
     */
    public Monitor getMonitor() {
        return this.monitor;
    }
    
    /** Método modificador para o atributo monitor.
     * @param _monitor Novo valor para o atributo monitor.
     *
     */
    public void setMonitor(Monitor _monitor) {
        this.monitor = _monitor;
    }
    
    /** Método modificador para o atributo meFrame.
     * @param _meFrame Novo valor para o atributo meFrame.
     *
     */
    public void setMeFrame(MaquinaDeEstadosFrame _meFrame) {
        this.meFrame = _meFrame;
    }
    
    /** Método acessador para o atributo meFrame.
     * @return A referência para o atributo meFrame.
     *
     */
    public MaquinaDeEstadosFrame getMeFrame() {
        return this.meFrame;
    }    

    /** Método acessador para o atributo idConexao.
     * @return A referência para o atributo idConexao.
     *
     */
    public int getIdConexao() {
        return this.idConexao;
    }
    
    /** Método modificador para o atributo idConexao.
     * @param id Novo valor para o atributo idConexao.
     *
     */
    public void setIdConexao(int id) {
        this.idConexao = id;
    }
    
    /** Método acessador para o atributo ipSimuladoLocal.
     * @return A referência para o atributo ipSimuladoLocal.
     *
     */
    public String getIpSimuladoLocal() {
        return this.ipSimuladoLocal;
    }
    
    /** Método modificador para o atributo ipSimuladoLocal.
     * @param _ipSimuladoLocal Novo valor para o atributo ipSimuladoLocal.
     *
     */
    public void setIpSimuladoLocal(String _ipSimuladoLocal) {
        this.ipSimuladoLocal = _ipSimuladoLocal;
    }
    
    /** Método acessador para o atributo ipSimuladoLocal.
     * @return A referência para o atributo ipSimuladoLocal.
     *
     */
    public String getIpSimuladoLocalBytePonto()
    {
        return Decoder.ipSimuladoToBytePonto(this.ipSimuladoLocal);
    }
    
    /** Método modificador para o atributo ipSimuladoLocal.
     * @param _ipSimuladoLocal Novo valor para o atributo ipSimuladoLocal.
     *
     */
    public void setIpSimuladoLocalBytePonto(String _ipSimuladoLocal) {
        this.ipSimuladoLocal = Decoder.bytePontoToIpSimulado(_ipSimuladoLocal);
    }
    
        /** Método acessador para o atributo portaLocal.
     * @return A referência para o atributo portaLocal.
     *
     */
    public int getPortaLocal() {
        return this.portaLocal;
    }
    
    /** Método modificador para o atributo portaLocal.
     * @param porta Novo valor para o atributo portaLocal.
     *
     */
    public void setPortaLocal(int porta) {
        this.portaLocal = porta;
    }
    
    /** Método acessador para o atributo ipSimuladoDestino.
     * @return A referência para o atributo ipSimuladoDestino.
     *
     */
    public String getIpSimuladoDestino() {
        return this.ipSimuladoDestino;
    }

    /** Método acessador para o atributo ipSimuladoDestino.
     * @return A referência para o atributo ipSimuladoDestino.
     *
     */
    public String getIpSimuladoDestinoBytePonto()
    {
        return Decoder.ipSimuladoToBytePonto(this.ipSimuladoDestino);
    }
    
    /** Método modificador para o atributo ipSimuladoDestino.
     * @param _ipSimuladoDestino Novo valor para o atributo ipSimuladoDestino.
     *
     */
    public void setIpSimuladoDestino(String _ipSimuladoDestino) {
        this.ipSimuladoDestino = _ipSimuladoDestino;
    }
    
    /** Método acessador para o atributo nomeEstacaoDestino.
     * @return A referência para o atributo nomeEstacaoDestino.
     *
     */
    public String getNomeEstacaoDestino() {
        return this.nomeEstacaoDestino;
    }
    
    /** Método modificador para o atributo nomeEstacaoDestino.
     * @param _nomeEstacaoDestino Novo valor para o atributo nomeEstacaoDestino.
     *
     */
    public void setNomeEstacaoDestino(String _nomeEstacaoDestino) {
        this.nomeEstacaoDestino = _nomeEstacaoDestino;
    }
    
    /** Método acessador para o atributo portaDestino.
     * @return A referência para o atributo portaDestino.
     *
     */
    public int getPortaDestino() {
        return this.portaDestino;
    }
    
    /** Método modificador para o atributo portaDestino.
     * @param _portaDestino Novo valor para o atributo portaDestino.
     *
     */
    public void setPortaDestino(int _portaDestino) {
        this.portaDestino = _portaDestino;
    }
    
	/**
	 * @return Retorna o tamanho da janela atual.
	 */
	public int getTamanhoJanela() {
		return this.tamanhoJanela;
	}
	/**
	 * @param tamanho Ajusta o tamanho da janela a ser utilizada.
	 */
	public void setTamanhoJanela(int tamanho) {
		this.tamanhoJanela = tamanho;
	}
    /**
     * @return Returns the estadoMEConAtual.
     */
    public byte getEstadoMEConAtual()
    {
        return this.estadoMEConAtual;
    }
    
    /**
     * @return Returns the numRetransmissoes.
     */
    public static int getNumRetransmissoes()
    {
        return numRetransmissoes;
    }
    /**
     * @return Returns the tempoTimeout.
     */
    public int getTempoTimeout()
    {
        return this.tempoTimeout;
    }
    
    /**
     * @param tempo The tempoTimeout to set.
     */
    public void setTempoTimeout(int tempo)
    {
        this.tempoTimeout = tempo;
    }    
}//fim da classe MaquinaDeEstados