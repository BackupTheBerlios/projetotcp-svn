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
    private static final int numRetransmissoes = ProtocoloTCP.MAX_RETRANSMISSOES;
    
    /** 
     * Constante que guarda o tempo (em milesegundos) para expirar o timestamp
     * de um segmento TCP enviado.
     */
    private int tempoTimeout = ProtocoloTCP.T_ESTOURO_RETRANSMISSOES;


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
        this.estadoMEConAtual = ProtocoloTCP.CLOSED;
        this.ipSimuladoLocal = _monitor.getIpSimuladoLocal();
        this.portaLocal = _porta;
        this.idConexao = _idConexao;
        this.meFrame = new MaquinaDeEstadosFrame(this);
        this.meFrame.atualizaInfoConexao(
                this.estadoMEConAtual, 
                this.getIpSimuladoLocalBytePonto(),
                Integer.toString(this.getPortaLocal()),  "null",  "null");
    }

    /** 
     * M�todo que recebe primitivas  e executa as opera��es para atender a a��o.
     * As primitivas est�o definidas na interface TCPIF. 
     *
     * @param _primitiva A primitiva que enviada.
     * @param args[] Um array de argumentos que voc� pode receber adicionalmente
     * @exception Exception  Caso ocorra algum erro ou exce��o, lan�a (throw) 
     * para quem chamou o m�todo.
     */
    public void recebePrimitiva(byte _primitiva, String args[]) throws Exception
	{
    	// atualiza exibi��o do estado atual com a primitiva recebida
    	this.meFrame.atualizaDadosEstado(ProtocoloTCP.nomeEstado[this.estadoMEConAtual],
    			ProtocoloTCP.nomePrimitiva[_primitiva], "->|", "");

    	byte novaPrimitiva = ProtocoloTCP.P_NENHUM;
    	byte novoSegmento  = ProtocoloTCP.S_NENHUM;
    	byte proximoEstado = ProtocoloTCP.NENHUM;
    	
    	
    	switch (this.estadoMEConAtual)
		{
    	case ProtocoloTCP.CLOSED:
    		switch (_primitiva)
			{
    		case ProtocoloTCP.P_PASSIVEOPEN:
    			proximoEstado = ProtocoloTCP.LISTEN; 
    			novaPrimitiva = ProtocoloTCP.P_OPENID;
//    			this.setIpSimuladoDestino(args[0]);
//    			this.setPortaDestino(Integer.parseInt(args[1]));
    			break;
    		case ProtocoloTCP.P_ACTIVEOPEN:
    			proximoEstado = ProtocoloTCP.SYNSENT; 
    			novoSegmento = ProtocoloTCP.S_SYN;
    			this.setIpSimuladoDestino(args[0]);
    			this.setPortaDestino(Integer.parseInt(args[1]));
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.LISTEN:
    		switch (_primitiva)
			{
    		case ProtocoloTCP.P_CLOSE:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			break;
    		case ProtocoloTCP.P_SEND:
    			proximoEstado = ProtocoloTCP.SYNSENT; 
    			novoSegmento = ProtocoloTCP.S_SYN;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.SYNRCVD:
    		switch (_primitiva)
			{
    		case ProtocoloTCP.P_CLOSE:
    			proximoEstado = ProtocoloTCP.FINWAIT1; 
    			novoSegmento = ProtocoloTCP.S_FIN;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.SYNSENT:
    		switch (_primitiva)
			{
    		case ProtocoloTCP.P_CLOSE:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novoSegmento = ProtocoloTCP.S_RST;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.ESTABLISHED:
    		switch (_primitiva)
			{
    		case ProtocoloTCP.P_CLOSE:
    			proximoEstado = ProtocoloTCP.FINWAIT1; 
    			novoSegmento = ProtocoloTCP.S_FIN;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.CLOSEWAIT:
    		switch (_primitiva)
			{
    		case ProtocoloTCP.P_CLOSE:
    			proximoEstado = ProtocoloTCP.LASTACK; 
    			novoSegmento = ProtocoloTCP.S_FIN;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
/*    		
    	case ProtocoloTCP.FINWAIT1:
    		switch (_primitiva)
			{
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.FINWAIT2:
    		switch (_primitiva)
			{
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.CLOSING:
    		switch (_primitiva)
			{
    		default:
    			throw new Exception();
			}
    		break;
    		*/
    	case ProtocoloTCP.LASTACK:
    		switch (_primitiva)
			{
    		case ProtocoloTCP.P_TIMEOUT:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novoSegmento = ProtocoloTCP.P_TERMINATE;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.TIMEWAIT:
    		switch (_primitiva)
			{
    		case ProtocoloTCP.P_TIMEOUT:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novoSegmento = ProtocoloTCP.P_TERMINATE;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	default:
			throw new Exception();
		}


    	this.estadoMEConAtual = proximoEstado;

    	
    	if (novoSegmento != ProtocoloTCP.S_NENHUM)
    	{
    		pacoteDeEnvio = new PacoteTCP (
    				this.getIpSimuladoLocalBytePonto(),
					this.getIpSimuladoDestinoBytePonto(),
					new CampoTCP(2, (int)   this.getPortaLocal()),
					new CampoTCP(2, (int)   this.getPortaDestino()),
					new CampoTCP(4, (long)  0),
					new CampoTCP(4, (long)  0),
					new CampoTCP(1, (short) 0),
					new CampoTCP(1, (short) novoSegmento),
					new CampoTCP(2, (int)   this.getTamanhoJanela()),
					new CampoTCP(2, (int)   0),
					new CampoTCP(2, (int)   0),
					new CampoTCP(4, (long)  0),							// Op��es
					args[2]);
    		
    		this.setTempoTimeout  (Integer.parseInt(args[3]));
    		this.setTamanhoJanela (Integer.parseInt(args[4]));
    		
    		enviaSegmentoTCP(pacoteDeEnvio);
    	}
        
    	if (novaPrimitiva != ProtocoloTCP.P_NENHUM)
    	{
    		enviaPrimitiva(novaPrimitiva, args);
    	}

	}
    
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
    	this.meFrame.atualizaDadosEstado(ProtocoloTCP.nomeEstado[this.estadoMEConAtual],
    			ProtocoloTCP.nomePrimitiva[_primitiva],
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
    	        ProtocoloTCP.nomeEstado[this.estadoMEConAtual],
    			ProtocoloTCP.nomePrimitiva[ProtocoloTCP.P_NENHUM],
    			"  |<-",
    			func);

        pacoteRecebido = _pacoteTCP;
    	
    	byte novaPrimitiva = ProtocoloTCP.P_NENHUM;
    	byte novoSegmento  = ProtocoloTCP.S_NENHUM;
    	byte proximoEstado = ProtocoloTCP.NENHUM;
    	
    	switch (this.estadoMEConAtual)
		{
    	case ProtocoloTCP.LISTEN:
    		switch (pacoteRecebido.getControle())
			{
    		case ProtocoloTCP.S_SYN:
    			proximoEstado = ProtocoloTCP.SYNRCVD; 
    			novoSegmento = ProtocoloTCP.S_SYN_ACK;
    			this.setIpSimuladoDestino(Decoder.bytePontoToIpSimulado(pacoteRecebido.getIpSimuladoLocal()));
    			this.setPortaDestino(pacoteRecebido.getPortaLocal());
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
    	case ProtocoloTCP.SYNRCVD:
    		switch (pacoteRecebido.getControle())
			{
    		case ProtocoloTCP.S_SYN:
    			proximoEstado = ProtocoloTCP.SYNRCVD; 
    			novoSegmento = ProtocoloTCP.S_SYN_ACK;
    			break;
    		case ProtocoloTCP.S_ACK:
    			proximoEstado = ProtocoloTCP.ESTABLISHED; 
    			novaPrimitiva = ProtocoloTCP.P_OPENSUCCESS;
    			break;
    		case ProtocoloTCP.S_RST:
    			proximoEstado = ProtocoloTCP.LISTEN; 
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.SYNSENT:
    		switch (pacoteRecebido.getControle())
			{
    		case ProtocoloTCP.S_SYN:
    			proximoEstado = ProtocoloTCP.SYNRCVD; 
    			novoSegmento = ProtocoloTCP.S_ACK;
    			break;
    		case ProtocoloTCP.S_SYN_ACK:
    			proximoEstado = ProtocoloTCP.ESTABLISHED; 
    			novoSegmento = ProtocoloTCP.S_ACK;
    			novaPrimitiva = ProtocoloTCP.P_OPENSUCCESS;
    			break;
    		case ProtocoloTCP.S_RST:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novaPrimitiva = ProtocoloTCP.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.ESTABLISHED:
    		switch (pacoteRecebido.getControle())
			{
    		case ProtocoloTCP.S_FIN:
    			proximoEstado = ProtocoloTCP.CLOSEWAIT; 
    			novoSegmento = ProtocoloTCP.S_ACK;
    			break;
    		case ProtocoloTCP.S_SYN_ACK:
    			proximoEstado = ProtocoloTCP.ESTABLISHED; 
    			novoSegmento = ProtocoloTCP.S_ACK;
    			break;
    		case ProtocoloTCP.S_RST:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novaPrimitiva = ProtocoloTCP.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.CLOSEWAIT:
    		switch (pacoteRecebido.getControle())
			{
    		case ProtocoloTCP.S_FIN:
    			proximoEstado = ProtocoloTCP.CLOSEWAIT; 
    			novoSegmento = ProtocoloTCP.S_ACK;
    			break;
    		case ProtocoloTCP.S_RST:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novaPrimitiva = ProtocoloTCP.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.FINWAIT1:
    		switch (pacoteRecebido.getControle())
			{
    		case ProtocoloTCP.S_ACK:
    			proximoEstado = ProtocoloTCP.FINWAIT2; 
    			break;
    		case ProtocoloTCP.S_FIN:
    			proximoEstado = ProtocoloTCP.CLOSING; 
    			novoSegmento = ProtocoloTCP.S_ACK;
    			break;
    		case ProtocoloTCP.S_RST:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novaPrimitiva = ProtocoloTCP.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.FINWAIT2:
    		switch (pacoteRecebido.getControle())
			{
    		case ProtocoloTCP.S_FIN:
    			proximoEstado = ProtocoloTCP.TIMEWAIT; 
    			novoSegmento = ProtocoloTCP.S_ACK;
    			break;
    		case ProtocoloTCP.S_RST:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novaPrimitiva = ProtocoloTCP.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.CLOSING:
    		switch (pacoteRecebido.getControle())
			{
    		case ProtocoloTCP.S_ACK:
    			proximoEstado = ProtocoloTCP.TIMEWAIT; 
    			break;
    		case ProtocoloTCP.S_FIN:
    			proximoEstado = ProtocoloTCP.CLOSING; 
    			novoSegmento = ProtocoloTCP.S_ACK;
    			break;
    		case ProtocoloTCP.S_RST:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novaPrimitiva = ProtocoloTCP.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.LASTACK:
    		switch (pacoteRecebido.getControle())
			{
    		case ProtocoloTCP.S_ACK:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novaPrimitiva = ProtocoloTCP.P_TERMINATE;
    			break;
    		case ProtocoloTCP.S_FIN:
    			proximoEstado = ProtocoloTCP.LASTACK; 
    			novoSegmento = ProtocoloTCP.S_ACK;
    			break;
    		case ProtocoloTCP.S_RST:
    			proximoEstado = ProtocoloTCP.CLOSED; 
    			novaPrimitiva = ProtocoloTCP.P_ERROR;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	case ProtocoloTCP.TIMEWAIT:
    		switch (pacoteRecebido.getControle())
			{
    		case ProtocoloTCP.S_FIN:
    			proximoEstado = ProtocoloTCP.TIMEWAIT; 
    			novoSegmento = ProtocoloTCP.S_ACK;
    			break;
    		default:
    			throw new Exception();
			}
    		break;
    	default:
			throw new Exception();
		}
    	
    	if (pacoteRecebido.getJanela() < this.getTamanhoJanela())
    	    this.setTamanhoJanela(pacoteRecebido.getJanela());

    	this.estadoMEConAtual = proximoEstado;        

    	if (novoSegmento != ProtocoloTCP.S_NENHUM)
    	{
    		pacoteDeEnvio = new PacoteTCP (
    				this.getIpSimuladoLocalBytePonto(),
					this.getIpSimuladoDestinoBytePonto(),
					new CampoTCP(2, (int)   this.getPortaLocal()),
					new CampoTCP(2, (int)   this.getPortaDestino()),
					new CampoTCP(4, (long)  0),
					new CampoTCP(4, (long)  0),
					new CampoTCP(1, (short) 0),
					new CampoTCP(1, (short) novoSegmento),
					new CampoTCP(2, (int)   this.getTamanhoJanela()),
					new CampoTCP(2, (int)   0),
					new CampoTCP(2, (int)   0),
					new CampoTCP(4, (long)  0),							// Op��es
					pacoteRecebido.getDados());
    		
    		enviaSegmentoTCP(pacoteDeEnvio);
    	}
        
    	if (novaPrimitiva != ProtocoloTCP.P_NENHUM)
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
        _pacoteTCP.geraOpcoes();
        
        String func = ProtocoloTCP.nomeSegmento(_pacoteTCP) + "(" + 
        	_pacoteTCP.getNumSequencia() + "," + 
        	_pacoteTCP.getTamanho() + "," +
        	_pacoteTCP.getNumAck() + "," +
        	this.getTamanhoJanela() + ")";
        
        String ip = IpSimulada.descobreNomeIPSimulado(_pacoteTCP.getIpSimuladoRemoto());
        int porta = Integer.parseInt(IpSimulada.descobrePortaIPSimulado(_pacoteTCP.getIpSimuladoRemoto()));
    	
        // Envia segmento � camada IP simulada
    	monitor.getProtocoloTCP().getCamadaIpSimulada().transmite(
    			ip, _pacoteTCP.toString(), _pacoteTCP.toString().length(), porta);

    	// Atualiza mostrador de estados
    	this.meFrame.atualizaDadosEstado(
    			ProtocoloTCP.nomeEstado[this.estadoMEConAtual],
    			ProtocoloTCP.nomePrimitiva[ProtocoloTCP.P_NENHUM],
    			"  |->",
				func);
	}
    
    /** M�todo acessador para o atributo monitor.
     * @return A refer�ncia para o atributo monitor.
     *
     */
    public Monitor getMonitor() {
        return monitor;
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
        return meFrame;
    }    

    /** M�todo acessador para o atributo idConexao.
     * @return A refer�ncia para o atributo idConexao.
     *
     */
    public int getIdConexao() {
        return idConexao;
    }
    
    /** M�todo modificador para o atributo idConexao.
     * @param idConexao Novo valor para o atributo idConexao.
     *
     */
    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }
    
    /** M�todo acessador para o atributo ipSimuladoLocal.
     * @return A refer�ncia para o atributo ipSimuladoLocal.
     *
     */
    public String getIpSimuladoLocal() {
        return ipSimuladoLocal;
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
        return portaLocal;
    }
    
    /** M�todo modificador para o atributo portaLocal.
     * @param portaLocal Novo valor para o atributo portaLocal.
     *
     */
    public void setPortaLocal(int portaLocal) {
        this.portaLocal = portaLocal;
    }
    
    /** M�todo acessador para o atributo ipSimuladoDestino.
     * @return A refer�ncia para o atributo ipSimuladoDestino.
     *
     */
    public String getIpSimuladoDestino() {
        return ipSimuladoDestino;
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
        return nomeEstacaoDestino;
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
        return portaDestino;
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
		return tamanhoJanela;
	}
	/**
	 * @param Ajusta o tamanho da janela a ser utilizada.
	 */
	public void setTamanhoJanela(int tamanhoJanela) {
		this.tamanhoJanela = tamanhoJanela;
	}
    /**
     * @return Returns the estadoMEConAtual.
     */
    public byte getEstadoMEConAtual()
    {
        return estadoMEConAtual;
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
        return tempoTimeout;
    }
    
    /**
     * @param tempoTimeout The tempoTimeout to set.
     */
    public void setTempoTimeout(int tempoTimeout)
    {
        this.tempoTimeout = tempoTimeout;
    }    
}//fim da classe MaquinaDeEstados