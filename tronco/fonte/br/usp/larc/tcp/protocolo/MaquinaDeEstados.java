package br.usp.larc.tcp.protocolo;

/*
 * @(#)MaquinaDeEstados.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laborat�rio de Arquitetura e Redes de Computadores
 * Escola Polit�cnica da Universidade de S�o Paulo
 *
 */

import br.usp.larc.tcp.aplicacao.MaquinaDeEstadosFrame;
import br.usp.larc.tcp.ipsimulada.IpSimulada;
import br.usp.larc.tcp.protocolo.ProtocoloTCP;

/** 
 * Classe que representa a M�quina de Estado do seu Protocolo (que pode ter n).
 * Detalhes e dicas de implementa��o podem ser consultadas nas Apostilas.
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a 
 * criatividade na implementa��o do seu projeto.
 *  
 *
 * @author	Laborat�rio de Arquitetura e Redes de Computadores
 * @version	1.0 Agosto 2003
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
    private Monitor monitor;

    /** 
     * Atributo que representa o id da Conex�o associado a M�quina de Estados
     */
    private int idConexao;

    /** 
     * IP Simulado Local associado a M�quina de Estados
     */
    private String ipSimuladoLocal;

    /** 
     * IP Simulado Destino associado a M�quina de Estados
     */
    private String ipSimuladoDestino;

    /** 
     * Nome da esta��o destino associado a M�quina de Estados
     */
    private String nomeEstacaoDestino;
    
    /** 
     * Porta TCP local associada a M�quina de Estados
     */
    private int portaLocal;

    /** 
     * Porta TCP remota associada a M�quina de Estados
     */
    private int portaDestino;

    /** 
     * O estado atual da m�quina de conex�o e desconex�o
     */
    private byte estadoMEConAtual;
    
    /** 
     * Constante que guarda o n�mero de retransmiss�es de um segmeto TCP com
     * timestamp expirado.
     */
    private static final int numRetransmissoes = TCPIF.MAX_RETRANSMISSOES;
    
    /** 
     * Constante que guarda o tempo (em milesegundos) para expirar o timestamp
     * de um segmento TCP enviado.
     */
    private int tempoTimeout = TCPIF.T_ESTOURO_RETRANSMISSOES;


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
     *
     */
    private int tamanhoJanela;
    
    /** Construtor da classe MaquinaDeEstados */
//    public MaquinaDeEstados() {
//    }
    
    /** Construtor da classe MaquinaDeEstados */
    public MaquinaDeEstados(Monitor _monitor, int _porta, int _idConexao) {
        this.monitor = _monitor;
        this.estadoMEConAtual = TCPIF.CLOSED;
        this.ipSimuladoLocal = _monitor.getIpSimuladoLocal();
        this.portaLocal = _porta;
        this.ipSimuladoDestino = "";
        this.portaDestino = -1;
        this.idConexao = _idConexao;
        this.meFrame = new MaquinaDeEstadosFrame(this);
        this.meFrame.atualizaInfoConexao(
                this.estadoMEConAtual, 
                this.getIpSimuladoLocalBytePonto(),
                Integer.toString(this.getPortaLocal()),  "null",  "null");
    }

    /**
     * M�todo que recebe primitivas e executa as opera��es para atender a a��o.
     * As primitivas est�o definidas na interface TCPIF.
     * 
     * @param _primitiva
     *        A primitiva que enviada.
     * @param args[]
     *        Um array de argumentos que voc� pode receber adicionalmente
     * @exception Exception
     *            Caso ocorra algum erro ou exce��o, lan�a (throw) para quem
     *            chamou o m�todo.
     */
    public void recebePrimitiva (byte _primitiva, String args[]) throws Exception
    {
    	System.out.println("recebePrimitiva: in�cio");
    	
        // atualiza exibi��o do estado atual com a primitiva recebida
        this.meFrame.atualizaDadosEstado (TCPIF.nomeEstado[this.estadoMEConAtual],
                TCPIF.nomePrimitiva[_primitiva], "->|", "");

        byte novaPrimitiva = TCPIF.P_NENHUM;
        byte novoSegmento = TCPIF.S_NENHUM;
        byte proximoEstado = TCPIF.NENHUM;

        switch (this.estadoMEConAtual)
        {
            case TCPIF.CLOSED:
                switch (_primitiva)
                {
                    case TCPIF.P_PASSIVEOPEN:
                        proximoEstado = TCPIF.LISTEN;
                        novaPrimitiva = TCPIF.P_OPENID;
                        //    			this.setIpSimuladoDestino(args[0]);
                        //    			this.setPortaDestino(Integer.parseInt(args[1]));
                        break;
                    case TCPIF.P_ACTIVEOPEN:
                        proximoEstado = TCPIF.SYNSENT;
                        novoSegmento = TCPIF.S_SYN;
                        this.setIpSimuladoDestino (args[0]);
                        this.setPortaDestino (Integer.parseInt (args[1]));
                        break;
                    default:
                        throw new Exception ();
                }
                break;
            case TCPIF.LISTEN:
                switch (_primitiva)
                {
                    case TCPIF.P_CLOSE:
                        proximoEstado = TCPIF.CLOSED;
                        break;
                    case TCPIF.P_SEND:
                        proximoEstado = TCPIF.SYNSENT;
                        novoSegmento = TCPIF.S_SYN;
                        break;
                    default:
                        throw new Exception ();
                }
                break;
            case TCPIF.SYNRCVD:
                switch (_primitiva)
                {
                    case TCPIF.P_CLOSE:
                        proximoEstado = TCPIF.FINWAIT1;
                        novoSegmento = TCPIF.S_FIN;
                        break;
                    default:
                        throw new Exception ();
                }
                break;
    	case TCPIF.SYNSENT:
    		switch (_primitiva)
			{
    		case TCPIF.P_CLOSE:
    			proximoEstado = TCPIF.CLOSED; 
    			novoSegmento = TCPIF.S_RST;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.ESTABLISHED:
    		switch (_primitiva)
			{
    		case TCPIF.P_CLOSE:
    			proximoEstado = TCPIF.FINWAIT1; 
    			novoSegmento = TCPIF.S_FIN;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.CLOSEWAIT:
    		switch (_primitiva)
			{
    		case TCPIF.P_CLOSE:
    			proximoEstado = TCPIF.LASTACK; 
    			novoSegmento = TCPIF.S_FIN;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
/*    		
    	case TCPIF.FINWAIT1:
    		switch (_primitiva)
			{
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.FINWAIT2:
    		switch (_primitiva)
			{
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.CLOSING:
    		switch (_primitiva)
			{
    		default:
    			throw new Exception();
			}
    		break;
    		*/
    	case TCPIF.LASTACK:
    		switch (_primitiva)
			{
    		case TCPIF.P_TIMEOUT:
    			proximoEstado = TCPIF.CLOSED; 
    			novoSegmento = TCPIF.P_TERMINATE;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.TIMEWAIT:
    		switch (_primitiva)
			{
    		case TCPIF.P_TIMEOUT:
    			proximoEstado = TCPIF.CLOSED; 
    			novoSegmento = TCPIF.P_TERMINATE;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	default:
			throw new Exception();
		}


    	this.estadoMEConAtual = proximoEstado;

    	
    	if (novoSegmento != TCPIF.S_NENHUM)
    	{
        	System.out.println("recebePrimitiva: novo segmento");
        	
    		this.pacoteDeEnvio = new PacoteTCP (
    				this.getIpSimuladoLocalBytePonto(),
					this.getIpSimuladoDestinoBytePonto(),
					new CampoTCP(2, this.getPortaLocal()),
					new CampoTCP(2, this.getPortaDestino()),
					new CampoTCP(4, 0L),
					new CampoTCP(4, 0L),
					new CampoTCP(1, (short) 0),
					new CampoTCP(1, novoSegmento),
					new CampoTCP(2, this.getTamanhoJanela()),
					new CampoTCP(2, 0),
					new CampoTCP(2, 0),
					new CampoTCP(4, 0L),							// Op��es
					args[2]);
    		
    		this.setTempoTimeout  (Integer.parseInt(args[3]));
    		this.setTamanhoJanela (Integer.parseInt(args[4]));
    		
        	System.out.println("recebePrimitiva: envia segmento");
    		enviaSegmentoTCP(this.pacoteDeEnvio);
    	}
        
    	if (novaPrimitiva != TCPIF.P_NENHUM)
    	{
        	System.out.println("recebePrimitiva: nova primitiva");
        	
    		enviaPrimitiva(novaPrimitiva, args);
    	}

    	System.out.println("recebePrimitiva: fim");
	} // recebePrimitiva
    
    /** 
     * M�todo que envia primitivas
     *
     * @param _primitiva A primitiva que est� sendo enviada.
     * @param args[] Um array de argumentos que a aplica��o pode enviar.
     * @exception Exception  Caso ocorra algum erro ou exce��o, lan�a (throw) 
     * para quem chamou o m�todo.
     */
    public void enviaPrimitiva(int _primitiva, String args[]) throws Exception
	{   
       //implemente aqui o envio de primitivas para sua M�quinaDeEstadosFrame
    	this.meFrame.atualizaDadosEstado(TCPIF.nomeEstado[this.estadoMEConAtual],
    			TCPIF.nomePrimitiva[_primitiva],
    			"<-|",
    			"");
    }
    
    /** 
     * M�todo que recebe segmentos TCP e faz o tratamento desse pacote
     *
     * @param _pacoteTCP O segmento TCP recebido
     * @exception Exception  Caso ocorra algum erro ou exce��o, lan�a (throw) 
     * para quem chamou o m�todo.
     */
    public void recebeSegmentoTCP(PacoteTCP _pacoteTCP)
    throws Exception
	{        
        String func = ProtocoloTCP.nomeSegmento(_pacoteTCP) + "(" + 
    	_pacoteTCP.getNumSequencia() + "," + 
    	_pacoteTCP.getTamanho() + "," +
    	_pacoteTCP.getNumAck() + "," +
    	this.getTamanhoJanela() + ")";
        
    	// atualiza exibi��o do estado atual com o segmento recebido
    	this.meFrame.atualizaDadosEstado(
    	        TCPIF.nomeEstado[this.estadoMEConAtual],
    			TCPIF.nomePrimitiva[TCPIF.P_NENHUM],
    			"  |<-",
    			func);

        this.pacoteRecebido = _pacoteTCP;
    	
    	byte novaPrimitiva = TCPIF.P_NENHUM;
    	byte novoSegmento  = TCPIF.S_NENHUM;
    	byte proximoEstado = TCPIF.NENHUM;
    	
    	switch (this.estadoMEConAtual)
		{
    	case TCPIF.LISTEN:
    		switch (this.pacoteRecebido.getControle())
			{
    		case TCPIF.S_SYN:
    			proximoEstado = TCPIF.SYNRCVD; 
    			novoSegmento = TCPIF.S_SYN_ACK;
    			this.setIpSimuladoDestino(Decoder.bytePontoToIpSimulado(this.pacoteRecebido.getIpSimuladoLocal()));
    			this.setPortaDestino(this.pacoteRecebido.getPortaLocal());
/*
            this.maquinaDeEstados.getMonitor().getTabelaDeConexoes().alteraDestino(
                    this.maquinaDeEstados.getIdConexao(),
                    this.maquinaDeEstados.getIpSimuladoDestino(),
                    Integer.toString(this.maquinaDeEstados.getPortaDestino()));

            
            this.atualizaInfoConexao(
                    this.maquinaDeEstados.getEstadoMEConAtual(),
                    this.maquinaDeEstados.getIpSimuladoLocalBytePonto(),
                    Integer.toString(this.maquinaDeEstados.getPortaLocal()), 
                    "null",
                    "null");
*/
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.SYNRCVD:
    		switch (this.pacoteRecebido.getControle())
			{
    		case TCPIF.S_SYN:
    			proximoEstado = TCPIF.SYNRCVD; 
    			novoSegmento = TCPIF.S_SYN_ACK;
    			break;
    		case TCPIF.S_ACK:
    			proximoEstado = TCPIF.ESTABLISHED; 
    			novaPrimitiva = TCPIF.P_OPENSUCCESS;
    			break;
    		case TCPIF.S_RST:
    			proximoEstado = TCPIF.LISTEN; 
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.SYNSENT:
    		switch (this.pacoteRecebido.getControle())
			{
    		case TCPIF.S_SYN:
    			proximoEstado = TCPIF.SYNRCVD; 
    			novoSegmento = TCPIF.S_ACK;
    			break;
    		case TCPIF.S_SYN_ACK:
    			proximoEstado = TCPIF.ESTABLISHED; 
    			novoSegmento = TCPIF.S_ACK;
    			novaPrimitiva = TCPIF.P_OPENSUCCESS;
    			break;
    		case TCPIF.S_RST:
    			proximoEstado = TCPIF.CLOSED; 
    			novaPrimitiva = TCPIF.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.ESTABLISHED:
    		switch (this.pacoteRecebido.getControle())
			{
    		case TCPIF.S_FIN:
    			proximoEstado = TCPIF.CLOSEWAIT; 
    			novoSegmento = TCPIF.S_ACK;
    			break;
    		case TCPIF.S_SYN_ACK:
    			proximoEstado = TCPIF.ESTABLISHED; 
    			novoSegmento = TCPIF.S_ACK;
    			break;
    		case TCPIF.S_RST:
    			proximoEstado = TCPIF.CLOSED; 
    			novaPrimitiva = TCPIF.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.CLOSEWAIT:
    		switch (this.pacoteRecebido.getControle())
			{
    		case TCPIF.S_FIN:
    			proximoEstado = TCPIF.CLOSEWAIT; 
    			novoSegmento = TCPIF.S_ACK;
    			break;
    		case TCPIF.S_RST:
    			proximoEstado = TCPIF.CLOSED; 
    			novaPrimitiva = TCPIF.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.FINWAIT1:
    		switch (this.pacoteRecebido.getControle())
			{
    		case TCPIF.S_ACK:
    			proximoEstado = TCPIF.FINWAIT2; 
    			break;
    		case TCPIF.S_FIN:
    			proximoEstado = TCPIF.CLOSING; 
    			novoSegmento = TCPIF.S_ACK;
    			break;
    		case TCPIF.S_RST:
    			proximoEstado = TCPIF.CLOSED; 
    			novaPrimitiva = TCPIF.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.FINWAIT2:
    		switch (this.pacoteRecebido.getControle())
			{
    		case TCPIF.S_FIN:
    			proximoEstado = TCPIF.TIMEWAIT; 
    			novoSegmento = TCPIF.S_ACK;
    			break;
    		case TCPIF.S_RST:
    			proximoEstado = TCPIF.CLOSED; 
    			novaPrimitiva = TCPIF.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.CLOSING:
    		switch (this.pacoteRecebido.getControle())
			{
    		case TCPIF.S_ACK:
    			proximoEstado = TCPIF.TIMEWAIT; 
    			break;
    		case TCPIF.S_FIN:
    			proximoEstado = TCPIF.CLOSING; 
    			novoSegmento = TCPIF.S_ACK;
    			break;
    		case TCPIF.S_RST:
    			proximoEstado = TCPIF.CLOSED; 
    			novaPrimitiva = TCPIF.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.LASTACK:
    		switch (this.pacoteRecebido.getControle())
			{
    		case TCPIF.S_ACK:
    			proximoEstado = TCPIF.CLOSED; 
    			novaPrimitiva = TCPIF.P_TERMINATE;
    			break;
    		case TCPIF.S_FIN:
    			proximoEstado = TCPIF.LASTACK; 
    			novoSegmento = TCPIF.S_ACK;
    			break;
    		case TCPIF.S_RST:
    			proximoEstado = TCPIF.CLOSED; 
    			novaPrimitiva = TCPIF.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case TCPIF.TIMEWAIT:
    		switch (this.pacoteRecebido.getControle())
			{
    		case TCPIF.S_FIN:
    			proximoEstado = TCPIF.TIMEWAIT; 
    			novoSegmento = TCPIF.S_ACK;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	default:
			throw new Exception();
		}
    	
    	if (this.pacoteRecebido.getJanela() < this.getTamanhoJanela())
    	    this.setTamanhoJanela(this.pacoteRecebido.getJanela());

    	this.estadoMEConAtual = proximoEstado;        

    	if (novoSegmento != TCPIF.S_NENHUM)
    	{
    		this.pacoteDeEnvio = new PacoteTCP (
    				this.getIpSimuladoLocalBytePonto(),
					this.getIpSimuladoDestinoBytePonto(),
					new CampoTCP(2, this.getPortaLocal()),
					new CampoTCP(2, this.getPortaDestino()),
					new CampoTCP(4, 0L),
					new CampoTCP(4, 0L),
					new CampoTCP(1, (short) 0),
					new CampoTCP(1, novoSegmento),
					new CampoTCP(2, this.getTamanhoJanela()),
					new CampoTCP(2, 0),
					new CampoTCP(2, 0),
					new CampoTCP(4, 0L),							// Op��es
					this.pacoteRecebido.getDados());
    		
    		enviaSegmentoTCP(this.pacoteDeEnvio);
    	}
        
    	if (novaPrimitiva != TCPIF.P_NENHUM)
    	{
    	    String args[] = {""};
    		enviaPrimitiva(novaPrimitiva, args);
    	}

    } // recebeSegmentoTCP
    
    /** 
     * M�todo que envia segmento TCP
     *
     * @param _pacoteTCP O segmento TCP enviado
     * @exception Exception  Caso ocorra algum erro ou exce��o, lan�a (throw) 
     * para quem chamou o m�todo.
     */
    public void enviaSegmentoTCP(PacoteTCP _pacoteTCP)
    throws Exception
	{
    	System.out.println("enviaSegmentoTCP: in�cio");
        _pacoteTCP.geraOpcoes();
        
        String func = ProtocoloTCP.nomeSegmento(_pacoteTCP) + "(" + 
        	_pacoteTCP.getNumSequencia() + "," + 
        	_pacoteTCP.getTamanho() + "," +
        	_pacoteTCP.getNumAck() + "," +
        	this.getTamanhoJanela() + ")";
        
    	System.out.println("enviaSegmentoTCP: pega ip e porta");
        String ip = IpSimulada.descobreNomeIPSimulado(_pacoteTCP.getIpSimuladoRemoto());
        int porta = Integer.parseInt(IpSimulada.descobrePortaIPSimulado(_pacoteTCP.getIpSimuladoRemoto()));
    	
    	System.out.println("enviaSegmentoTCP: envia segmento � camada IP");
        // Envia segmento � camada IP simulada
    	this.monitor.getProtocoloTCP().getCamadaIpSimulada().transmite(
    			ip, _pacoteTCP.toString(), _pacoteTCP.toString().length(), porta);

    	System.out.println("enviaSegmentoTCP: atualiza mostrador");
    	// Atualiza mostrador de estados
    	this.meFrame.atualizaDadosEstado(
    			TCPIF.nomeEstado[this.estadoMEConAtual],
    			TCPIF.nomePrimitiva[TCPIF.P_NENHUM],
    			"  |->",
				func);
    	
    	System.out.println("enviaSegmentoTCP: fim");
	}
    
    /** M�todo acessador para o atributo monitor.
     * @return A refer�ncia para o atributo monitor.
     *
     */
    public Monitor getMonitor() {
        return this.monitor;
    }
    
    /** M�todo modificador para o atributo monitor.
     * @param monitor Novo valor para o atributo monitor.
     *
     */
    public void setMonitor(Monitor _monitor) {
        this.monitor = _monitor;
    }
    
    /** M�todo modificador para o atributo meFrame.
     * @param meFrame Novo valor para o atributo meFrame.
     *
     */
    public void setMeFrame(MaquinaDeEstadosFrame _meFrame) {
        this.meFrame = _meFrame;
    }
    
    /** M�todo acessador para o atributo meFrame.
     * @return A refer�ncia para o atributo meFrame.
     *
     */
    public MaquinaDeEstadosFrame getMeFrame() {
        return this.meFrame;
    }    

    /** M�todo acessador para o atributo idConexao.
     * @return A refer�ncia para o atributo idConexao.
     *
     */
    public int getIdConexao() {
        return this.idConexao;
    }
    
    /** M�todo modificador para o atributo idConexao.
     * @param id Novo valor para o atributo idConexao.
     *
     */
    public void setIdConexao(int id) {
        this.idConexao = id;
    }
    
    /** M�todo acessador para o atributo ipSimuladoLocal.
     * @return A refer�ncia para o atributo ipSimuladoLocal.
     *
     */
    public String getIpSimuladoLocal() {
        return this.ipSimuladoLocal;
    }
    
    /** M�todo modificador para o atributo ipSimuladoLocal.
     * @param ipSimuladoLocal Novo valor para o atributo ipSimuladoLocal.
     *
     */
    public void setIpSimuladoLocal(String _ipSimuladoLocal) {
        this.ipSimuladoLocal = _ipSimuladoLocal;
    }
    
    /** M�todo acessador para o atributo ipSimuladoLocal.
     * @return A refer�ncia para o atributo ipSimuladoLocal.
     *
     */
    public String getIpSimuladoLocalBytePonto()
    {
        return Decoder.ipSimuladoToBytePonto(this.ipSimuladoLocal);
    }
    
    /** M�todo modificador para o atributo ipSimuladoLocal.
     * @param ipSimuladoLocal Novo valor para o atributo ipSimuladoLocal.
     *
     */
    public void setIpSimuladoLocalBytePonto(String _ipSimuladoLocal) {
        this.ipSimuladoLocal = Decoder.bytePontoToIpSimulado(_ipSimuladoLocal);
    }
    
        /** M�todo acessador para o atributo portaLocal.
     * @return A refer�ncia para o atributo portaLocal.
     *
     */
    public int getPortaLocal() {
        return this.portaLocal;
    }
    
    /** M�todo modificador para o atributo portaLocal.
     * @param porta Novo valor para o atributo portaLocal.
     *
     */
    public void setPortaLocal(int porta) {
        this.portaLocal = porta;
    }
    
    /** M�todo acessador para o atributo ipSimuladoDestino.
     * @return A refer�ncia para o atributo ipSimuladoDestino.
     *
     */
    public String getIpSimuladoDestino() {
        return this.ipSimuladoDestino;
    }

    /** M�todo acessador para o atributo ipSimuladoDestino.
     * @return A refer�ncia para o atributo ipSimuladoDestino.
     *
     */
    public String getIpSimuladoDestinoBytePonto()
    {
        return Decoder.ipSimuladoToBytePonto(this.ipSimuladoDestino);
    }
    
    /** M�todo modificador para o atributo ipSimuladoDestino.
     * @param ipSimuladoDestino Novo valor para o atributo ipSimuladoDestino.
     *
     */
    public void setIpSimuladoDestino(String _ipSimuladoDestino) {
        this.ipSimuladoDestino = _ipSimuladoDestino;
    }
    
    /** M�todo acessador para o atributo nomeEstacaoDestino.
     * @return A refer�ncia para o atributo nomeEstacaoDestino.
     *
     */
    public String getNomeEstacaoDestino() {
        return this.nomeEstacaoDestino;
    }
    
    /** M�todo modificador para o atributo nomeEstacaoDestino.
     * @param nomeEstacaoDestino Novo valor para o atributo nomeEstacaoDestino.
     *
     */
    public void setNomeEstacaoDestino(String _nomeEstacaoDestino) {
        this.nomeEstacaoDestino = _nomeEstacaoDestino;
    }
    
    /** M�todo acessador para o atributo portaDestino.
     * @return A refer�ncia para o atributo portaDestino.
     *
     */
    public int getPortaDestino() {
        return this.portaDestino;
    }
    
    /** M�todo modificador para o atributo portaDestino.
     * @param portaDestino Novo valor para o atributo portaDestino.
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
	 * @param Ajusta o tamanho da janela a ser utilizada.
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