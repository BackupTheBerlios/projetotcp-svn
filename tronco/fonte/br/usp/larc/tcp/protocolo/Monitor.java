package br.usp.larc.tcp.protocolo;

/**
 * @(#)Monitor.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

import java.util.Iterator;

import br.usp.larc.tcp.aplicacao.MonitorFrame;

/**
 * Classe que representa o monitor do seu protocolo. Detalhes e dicas de
 * implementação podem ser consultadas nas Apostilas.
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a
 * criatividade na implementação do seu projeto.
 *
 *
 * @author	Laboratório de Arquitetura e Redes de Computadores
 * @version	1.0 Agosto 2003
 */
public class Monitor {

    /**
     * O Protocolo TCP que o monitor está vinculado
     */
    private ProtocoloTCP protocoloTCP;

    /**
     * Atributo que representa o frame associado ao ProtocoloTCP
     */
    private MonitorFrame monitorFrame;

    /**
     * Thread que vai monitorar o buffer de entrada da camdada IPSimulada e
     * entregar os dados recebidos para o monitor
     */
    private MonitorThread monitorThread;

    /**
     * Coleção com objetos MaquinaDeEstados
     */
    private MaquinasDeEstados maquinasDeEstados;

    /**
     * Objeto que representa a Tabela de Conexao
     */
    private TabelaDeConexoes tabelaDeConexoes;

    /**
     * Atributo com o IpLocal do Monitor
     */
    private String ipSimuladoLocal;

    /**
     * Atributo que representa o contador de Id de Conexões
     */
    private int countIdConexao;

    /** Construtor da classe Monitor */
//    public Monitor() {
//    }

    /**
     * Construtor da classe Monitor
     * 
     * @param _protocoloTCP
     *        Objeto da classe ProtocoloTCP que será associado ao monitor.
     */
    public Monitor (ProtocoloTCP _protocoloTCP)
    {
        this.protocoloTCP = _protocoloTCP;
        //inicia o Frame do Monitor
        this.monitorFrame = new MonitorFrame(_protocoloTCP);
        this.tabelaDeConexoes = new TabelaDeConexoes();
        this.maquinasDeEstados = new MaquinasDeEstados();

        //inicia o contador de id de conexão em 0 (zero)
        this.countIdConexao = 0;
    }

    /**
    * Método utilizado para entregar o próximo id do contador para cada 
conexão.
    *
    * @return O id da próxima Conexão
    */
    public synchronized int getNextID() 
    {
        return this.countIdConexao++;
    }

    /**
     * Método que procura uma maquina de estados dado a porta local associada
     * a essa máquina de estados e retorna uma referência para a máquina de
     * estados encontrada. Caso não encontre a máquina, retorna null.
     *
     * @param  _portaME  porta local da maquina de estados
     * @return MaquinaDeEstados associada a porta local dada
     */
    public MaquinaDeEstados findMEPorPortaLocal(int _portaME) 
    {
        Iterator i = this.maquinasDeEstados.maquinas();
        
        while (i.hasNext()) 
        {
            MaquinaDeEstados meq = (MaquinaDeEstados) i.next();
            if (meq.getPortaLocal() == _portaME) 
                return meq;
        }
        return null;
    }

    /**
     * Cria uma máquina de estados com a porta passada como parâmetro (uma
     * porta TCP local). Retorna false se já existir uma máquina de estados já
     * associada àquela porta.
     *
     * @param _portaTCP a porta que será atribuida a nova Máquina de Estados
     * @return Indica se houve sucesso.
     */
    public boolean criaMaquinaDeEstados(int _portaTCP)
    {
        //verifica se já existe uma máquina de estados associada àquela porta
        if (findMEPorPortaLocal(_portaTCP) == null)
        {

            //sincroniza esse trecho do código para evitar inconsistência na
            //ordem de inserção da coleçao de máquinas de estados e da tabela
            //de conexões.
            synchronized (this)
            {
                int idConexao = this.getNextID();

                //cria o objeto com a nova máquina de estados associando a porta
                // passada como parâmetro e o id da conexão
                MaquinaDeEstados maquinaME = new MaquinaDeEstados(this, _portaTCP, idConexao);
                this.adicionaMaquina(Integer.toString(idConexao), maquinaME);

                //cria uma nova conexão
                ConexaoTCP conexao = new ConexaoTCP();
                conexao.setIpSimuladoLocal(this.ipSimuladoLocal);
                conexao.setPortaLocal(Integer.toString(_portaTCP));
                conexao.setIpSimuladoRemoto("");
                conexao.setPortaRemota("");
                conexao.setIdConexao(idConexao);

                //adiciona a nova conexão na Tabela de Conexões do Monitor
                this.abreConexao(conexao);
            }
            return true;
        }
        return false;
    }

    /**
     * Fecha uma máquina de estados com o id passado como parâmetro
     * 
     * @param _idConexao
     *        O id da Máquina de Estados que será fechada.
     * @return Indica se foi possível fechar a máquina de estado.
     */
    public boolean fechaMaquinaDeEstados(int _idConexao)
    {
    	MaquinaDeEstados maquina = null;
    	
    	//faz uma varredura na coleção de chaves da coleção de máquina
    	//de estados
    	Iterator i = this.maquinasDeEstados.maquinasKeySet(); 
    	while (i.hasNext())
    	{
    		
    		//pega chave por chave da coleção de máquina de estados
    		int chaveIdConexao = Integer.parseInt((String) (i.next()));
    		
    		//verifica se chave recuperada da coleção é igual a chave
    		//passada como parâmetro
    		if (chaveIdConexao == _idConexao) 
    		{
    			//recupera a referência da máquina de estados
    			//com o id da conexão passada como parâmetro
    			maquina = this.maquinasDeEstados.get(_idConexao);
    			break;
    		}
    	}

    	//se a referência recuperada não for nula a apaga a máquina da
    	//coleção de máquinas de estados do monitor e também a conexão
    	//com aquele id da Tabela de Conexões do Monitor
    	if (maquina != null)
    	{
    		this.fechaMaquina(_idConexao);
    		this.fechaConexao(_idConexao);
    		this.fechaMaquinaDeEstadosFrame(maquina);
    		return true;
    	}
    	return false;
    }

    /**
     * Finaliza o frame da máquina de estados passada com parâmetro
     *
     * @param _maquina O id da Máquina de Estados que será fechada
     */
    public void fechaMaquinaDeEstadosFrame(MaquinaDeEstados _maquina)
    {
        if (_maquina.getMeFrame() != null)
        {
            _maquina.getMeFrame().dispose();
            _maquina.setMeFrame(null);
            _maquina = null;
        }
    }

    /**
     *  Fecha o monitor
     */
    public void fechar()
    {
        this.monitorFrame.dispose();
        this.monitorThread = null;
        this.tabelaDeConexoes = null;
        this.maquinasDeEstados = null;
    }

    /**
     *  Reinicia o monitor
     */
    public void reinicia()
    {
        //implemente aqui o método que reinicia o monitor ao seu estado inicial

        try
		{
            this.protocoloTCP.reinicializaTCP();
        }
        catch (Exception e)
		{
        	System.out.println("Monitor.reinicia()" + e.getMessage());
		}

        //cria um iterador para percorrer um objeto do tipo TabelaDeConexoes
        Iterator  iteratorTabela = this.tabelaDeConexoes.conexoes();
        //percorre todo o iterador
        while (iteratorTabela.hasNext())
        {
        	//remove as conexoes
        	
        	this.tabelaDeConexoes.remove(((ConexaoTCP)iteratorTabela.next()).getIdConexao());
        	//imprime todos ID's das conexões que estão na tabela
        	
        	System.out.println(((ConexaoTCP)iteratorTabela.next()).getIdConexao());
        }
        
        //cria um iterador para percorrer um objeto do tipo Maquinas de Estado
        Iterator  iteratorMaquinas = this.maquinasDeEstados.maquinas();
        //percorre todo o iterador
        while (iteratorMaquinas.hasNext())
        {
        	//remove as maquinas
        	
        	this.maquinasDeEstados.remove(((MaquinaDeEstados)iteratorMaquinas.next()).getIdConexao());
        	//imprime todos ID's das conexões que estão na tabela
        	
        	System.out.println(((MaquinaDeEstados)iteratorMaquinas.next()).getIdConexao());
        }
        //reinicia o contador de id de conexão em 0 (zero)
        //this.countIdConexao = 0;
    }
    
    /**
     * Monitora a camada IP Simulada
     */
    public synchronized void monitoraCamadaIP()
    {
    	this.monitorThread = new MonitorThread(this, this.protocoloTCP.getCamadaIpSimulada());
    	this.monitorThread.start();
    }

    /**
     * Termina monitoramento da camada IP Simulada
     */
    public synchronized void terminaMonitoramentoCamadaIP()
    {
        this.monitorThread.paraThread();
    }

    /**
     * Analiza dados recebidos da camada IP simulada e faz análise.
     * 
     * @param _bufferEntrada
     */
    public void analisaDados(String _bufferEntrada)
    {
    	//implemente aqui o tratamento dos segmentos que chegam.
    	Iterator conexoes = this.tabelaDeConexoes.conexoes();
    	Iterator maquinas = this.maquinasDeEstados.maquinas();
    	
    	PacoteTCP pacote = new PacoteTCP(_bufferEntrada);
    	ConexaoTCP conexao;

		String ip_destino = Decoder.bytePontoToIpSimulado (pacote.getIpSimuladoRemoto ());
        String ip_origem = Decoder.bytePontoToIpSimulado (pacote.getIpSimuladoLocal ());
        String porta_destino = Integer.toString (pacote.getPortaRemota ());
        String porta_origem = Integer.toString (pacote.getPortaLocal ());

    	while (conexoes.hasNext())
    	{
    		conexao = (ConexaoTCP) conexoes.next();
    		
    		// Se destino do pacote = endereço local da tabela
    		if (conexao.getIpSimuladoLocal().equals(ip_destino) &&
    				conexao.getPortaLocal().equals(porta_destino))
    		{
    			// Se endereço remoto da tabela for nulo, preenche com endereço
                // de origem do pacote (primeiro pacote recebido de uma ME em
                // Listen)
                if (conexao.getIpSimuladoRemoto ().equals (""))
                    conexao.setIpSimuladoRemoto (ip_origem);
                if (conexao.getPortaRemota ().equals (""))
                    conexao.setPortaRemota (porta_origem);

                // Se origem do pacote = endereço remoto da tabela
                if (conexao.getIpSimuladoRemoto ().equals (ip_origem)
                        && conexao.getPortaRemota ().equals (porta_origem))
    			{
    				int id = conexao.getIdConexao();
    				MaquinaDeEstados maquina ;
    				
    				// Procura ME da conexão encontrada
    				while (maquinas.hasNext())
    				{
    					maquina = (MaquinaDeEstados) maquinas.next();
    					if (id == maquina.getIdConexao())
    					{
    						try
							{
    							maquina.recebeSegmentoTCP(pacote);
    							System.out.println( "Monitor.analisaDados: Recebeu Segmento");
    							return;
							}
    						catch ( Exception e )
							{
    							System.err.println("Monitor.analisaDados erro na ME:" + e.getMessage());
                                e.printStackTrace();
                                System.err.flush();
							}
    					}
    				} // while (maquinas.hasNext())
    		    	System.out.println( "Monitor.analisaDados: ME não encontrada");
    			} // se origem do pacote = end. remoto da tabela
    		} // se destino do pacote = end. local da tabela
    	}
    	System.out.println( "Monitor.analisaDados: Descartou Segmento");
    }

    /**
     * Método que coloca uma nova Maquina na tabela de MaquinasDeEstados
     *
     * @param _idConexao        O Id da conexao da máquina a ser registrada
     * @param _maquinaDeEstados Nova máquina a ser registrada
     */
    public void adicionaMaquina(String _idConexao,
        MaquinaDeEstados _maquinaDeEstados) {
        try {
            this.maquinasDeEstados.put(_idConexao, _maquinaDeEstados);
        } catch (Exception e) {
            System.out.println("Monitor.adicionaMaquina(): "  +
                e.getMessage());
        }
    }

    /**
     * Método que fecha uma máquina de estados, dada a porta da conexão
     * 
     * @param _idConexao
     *        id da Máquina de Estado a ser excluída
     */
    public void fechaMaquina (int _idConexao)
    {
        try
        {
            this.maquinasDeEstados.remove (Integer.toString (_idConexao));
        }
        catch (Exception e)
        {
            System.out.println ("Monitor.fechaMaquina(): " + e.getMessage ());
        }
    }

    /**
     * Método que coloca uma nova conexaoTCP na tabela de conexões
     *
     * @param _conexaoTCP Nova conexão a ser registrada
     */
    public void abreConexao(ConexaoTCP _conexaoTCP) {
        try {
            this.tabelaDeConexoes.put(_conexaoTCP);
        } catch (Exception e) {
            System.out.println("Monitor.abreConexao(): "  +
                e.getMessage());
        }
    }

    /**
     * Método que fecha uma conexão TCP, dado o id da conexão
     * 
     * @param _idConexaoTCP
     *        id da conexaoTCP a ser fechada
     */
    public void fechaConexao (int _idConexaoTCP)
    {
        try
        {
            this.tabelaDeConexoes.remove (_idConexaoTCP);
        }
        catch (Exception e)
        {
            System.out.println ("Monitor.fechaConexao(): " + e.getMessage ());
        }
    }

    /**
     * Método acessador para o atributo protocoloTCP.
     * 
     * @return A referência para o atributo protocoloTCP.
     */
    public ProtocoloTCP getProtocoloTCP ()
    {
        return this.protocoloTCP;
    }

    /** Método modificador para o atributo protocoloTCP.
     * @param _protocoloTCP Novo valor para o atributo protocoloTCP.
     *
     */
    public void setProtocoloTCP(ProtocoloTCP _protocoloTCP) {
        this.protocoloTCP = _protocoloTCP;
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

    /** Método acessador para o atributo maquinasDeEstados.
     * @return A referência para o atributo maquinasDeEstados.
     *
     */
    public MaquinasDeEstados getMaquinasDeEstados() {
        return this.maquinasDeEstados;
    }

    /** Método modificador para o atributo maquinasDeEstados.
     * @param _maquinasDeEstados Novo valor para o atributo maquinasDeEstados.
     *
     */
    public void setMaquinasDeEstados(MaquinasDeEstados _maquinasDeEstados) {
        this.maquinasDeEstados = _maquinasDeEstados;
    }

    /** Método acessador para o atributo tabelaDeConexoes.
     * @return A referência para o atributo tabelaDeConexoes.
     *
     */
    public TabelaDeConexoes getTabelaDeConexoes() {
        return this.tabelaDeConexoes;
    }

    /** Método modificador para o atributo tabelaDeConexoes.
     * @param _tabelaDeConexoes Novo valor para o atributo tabelaDeConexoes.
     *
     */
    public void setTabelaDeConexoes(TabelaDeConexoes _tabelaDeConexoes) {
        this.tabelaDeConexoes = _tabelaDeConexoes;
    }

}//fim da classe Monitor