package br.usp.larc.tcp.protocolo;

/*
 * @(#)ProtocoloTCP.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laborat�rio de Arquitetura e Redes de Computadores.
 * Escola Polit�cnica da Universidade de S�o Paulo.
 *
 */

import br.usp.larc.tcp.ipsimulada.IpSimulada;
import java.lang.Exception;

/** 
 * Classe que encapasula de modo global todos as classes do Protocolo TCP 
 * Simulado. � nessa classe que voc� voc� vai implementar as a��es que os
 * eventos que a Interfaces Monitor gera, fazendo com que as classes se 
 * comuniquem entre si de acordo com cada a��o/evento. 
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a 
 * criatividade na implementa��o do seu projeto.
 *  
 *
 * @author	Laborat�rio de Arquitetura e Redes de Computadores.
 * @version	1.0 Agosto 2003.
 */

public class ProtocoloTCP implements TCPIF {

    /** 
     * Atributo que representa a camada IpSimulada.
     */        
    private IpSimulada camadaIpSimulada;
   
    /**
     * Atributo que representa se o canal IP esta aberto.
     */        
    private boolean camadaIPSimuladaAberta;
    
    /**
     * Objeto Monitor.
     */
    private Monitor monitor;
   
    //adicione aqui outros atributos importantes que voc� julgar necess�rio
        
    /** 
     * Construtor da classe ProtocoloTCP.
     */
    public ProtocoloTCP() {
    	this.init();
    }
    
    /** 
     * M�todo que inicializa os atributos do Protocolo TCP.
     */
    public void init() {
    	this.camadaIpSimulada = new IpSimulada();
    	this.camadaIPSimuladaAberta = false;
    	this.monitor = new Monitor(this);
    }
    
    /** 
     * M�todo que recebe primitivas da camada de aplica��o e executa as opera��es
     * para atender a a��o. As primitivas est�o definidas na interface TCPIF.
     *
     * @param _primitiva A primitiva que a aplica��o enviou.
     * @param args[] Um array de argumentos que a aplica��o pode enviar.
     * @exception Exception  Caso ocorra algum erro ou exce��o, lan�a (throw) 
     * para quem chamou o m�todo.
     */
    public void recebePrimitivaAplicacao(int _primitiva, String args[])
    throws Exception
	{
    	
    	switch (_primitiva) {
    	case ProtocoloTCP.P_TCP_OPEN:
                try{
                    this.inicializaTcp();
                    break;
                } catch(Exception e) {
                    System.out.println("ProtocoloTCP: Erro " +
                        "recebePrimitivaAplicacao(P_TCP_OPEN)");
                    throw new Exception("Erro no recebimento de primitiva: " + 
                    		_primitiva + "\n" + e.getMessage());
                }
            case ProtocoloTCP.P_TCP_CLOSE:
                try{
                    this.finalizaTCP();
                    break;
                } catch(Exception e) {
                    System.out.println("ProtocoloTCP: Erro " +
                        "recebePrimitivaAplicacao(P_TCP_CLOSE_ME)");
                    throw new Exception("Erro no recebimento de primitiva: " + 
                        _primitiva + "\n" + e.getMessage());
                }
            case ProtocoloTCP.P_TCP_OPEN_ME:
                try{
                    int portaME = Integer.parseInt(args[0]);
                    this.criaMaquinaEstado(portaME);
                    break;
                } catch(Exception e) {
                    System.out.println("ProtocoloTCP: Erro " +
                        "recebePrimitivaAplicacao(P_TCP_OPEN_ME)");
                    throw new Exception("Erro no recebimento de primitiva: " + 
                        _primitiva + "\n" + e.getMessage());
                }
            case ProtocoloTCP.P_TCP_CLOSE_ME:
                try{
                    int idConexao = Integer.parseInt(args[0]);
                    this.fechaMaquinaEstado(idConexao);
                    break;
                } catch(Exception e) {
                    System.out.println("ProtocoloTCP: Erro " +
                        "recebePrimitivaAplicacao(P_TCP_OPEN_ME)");
                    throw new Exception("Erro no recebimento de primitiva: " + 
                        _primitiva + "\n" + e.getMessage());
                }
            case ProtocoloTCP.P_TCP_RESET:
            	this.reinicializaTcp();
            // XXX Reinicializa TCP
                //adicione aqui o c�digo que trata a primitiva que reseta o
                //o protocolo TCP.
        }
    }
    
    /**
     * M�todo que inicializa o protocolo TCP.
     *
     * @exception Exception  Caso ocorra algum erro ou exce��o, lan�a (throw) 
     * para quem chamou o m�todo.
     */
    public void inicializaTcp()
    throws Exception
	{
    	if (!camadaIPSimuladaAberta)
    	{
    		try
			{
    			System.out.println("Iniciando Protocolo TCP...");
    			this.inicializaIpSimulada(ProtocoloTCP.BUFFER_DEFAULT_IP_SIMULADA);
    			String ipBytePonto = this.camadaIpSimulada.descobreCanalIPSimulado();
    			
    			this.monitor.setIpSimuladoLocal(Decoder.bytePontoToIpSimulado(ipBytePonto));
    			this.camadaIPSimuladaAberta = true;
    			System.out.println("Iniciado:");
    			System.out.println("\tIP Simulado do MONITOR  : " + ipBytePonto);
    			this.monitor.monitoraCamadaIP();
    		} catch (Exception e)
			{
    			System.out.println("ProtocoloTCP.inicializaTcp(): " +
    					e.getMessage());
    			throw e;
    		}
    	} else {
    		System.out.println("Protocolo TCP j� inicializado.");
    	}
    }

    /**
     * M�todo que finaliza o protocoloTCP e consequentemente o projeto.
     *
     * @exception Exception  Caso ocorra algum erro ou exce��o, joga (throw) 
     * para quem chamou o m�todo.
     */
    public void finalizaTCP()
    throws Exception
	{
    	if (camadaIPSimuladaAberta)
    	{
    		try
			{
    			if (this.camadaIpSimulada != null)
    			{
    				this.monitor.terminaMonitoramentoCamadaIP();
    				this.monitor.fechar();
    				this.monitor = null;
    				this.finalizaIpSimulada();
    				this.camadaIpSimulada = null;
    				System.out.println("Protocolo TCP finalizado.");
    				System.exit(0);
    			}
    			else
    			{
    				System.out.println("Protocolo TCP n�o foi inicializado.");
    			}
			}
    		catch (Exception e)
			{
    			System.out.println("ProtocoloTCP.finalizaTCP(): " + e.getMessage());
    			throw e;
			}
    	}
    	else
    	{
    		System.out.println("Protocolo TCP n�o foi inicializado.");
    	}
	}
    
    /**
     * M�todo que abre uma nova M�quina de Estados associada a uma porta TCP
     * recebida  como par�metro.
     *
     * @param _portaME A porta TCP que ser� associada a m�quina de estados.
     * @exception Exception  Caso ocorra algum erro ou exce��o, lan�a (throw) 
     * para quem chamou o m�todo.
     */
    public void criaMaquinaEstado(int _portaME)
    throws Exception
	{
    	if (camadaIPSimuladaAberta)
    	{
    		try 
			{
    			if (!monitor.criaMaquinaDeEstados(_portaME))
    			{
    				throw new Exception("Porta: " + _portaME + " j� usada.");
    			}
			} 
    		catch (Exception e)
			{
    			System.out.println("ProtocoloTCP.inicializaTcp(): " + e.getMessage());
    			throw e;
			}
    	} 
    	else 
    	{
    		throw new Exception("Protocolo TCP n�o inicializado.");
    	}
	}
    
    /**
     * M�todo que fecha m�quina de estados com id de Conex�o passada como 
     * par�metro.
     *
     * @param _idConexao O id da Conex�o da m�quina que voc� quer fechar.
     * @exception Exception  Caso ocorra algum erro ou exce��o, lan�a (throw) 
     * para quem chamou o m�todo.
     */
    public void fechaMaquinaEstado(int _idConexao)
    throws Exception
	{
    	if (camadaIPSimuladaAberta)
    	{
    		try
			{
    			if (!monitor.fechaMaquinaDeEstados(_idConexao))
    			{
    				throw new Exception("Id: " + _idConexao + " n�o existe.");
    			}
			} 
    		catch (Exception e)
			{
    			System.out.println("ProtocoloTCP.fechaMaquinaEstado(): " + 
    					e.getMessage());
    			throw e;
			}
    	} 
    	else 
    	{
    		throw new Exception("Protocolo TCP n�o inicializado.");
    	}
	}
    
    /**
     * M�todo que reinicializa o protocolo TCP fazendo com que o protocolo volte
     * ao seu estado inicial .
     *
     * @exception Exception  Caso ocorra algum erro ou exce��o, lan�a (throw) 
     * para quem chamou o m�todo.
     */
    public void reinicializaTcp() 
    throws Exception 
	{
        System.out.println("Reiniciando Protocolo TCP...");
        //implemente aqui o m�todo que reinicializa o Protocolo TCP.
    }
    
    /**
     * M�todo que inicializa a camada IpSimulada.
     * 
     * @param buffer tamanho do buffer.
     * @exception Exception excecao jogada quando inicializa a camada IpSimulada.
     */
    public void inicializaIpSimulada(int buffer) throws Exception 
	{
    	try 
		{
    		this.camadaIpSimulada.inicializaCanal(buffer);
    		this.camadaIPSimuladaAberta = true;
		} 
    	catch (Exception e) 
		{
    		System.out.println("ProtocoloTCP.inicializaIpSimulada(): "  +
    				e.getMessage());
    		throw e;
		}
    }
    
    /** 
     * M�todo que finaliza a camada ipSimulada.
     * @exception Exception excecao joagada quando a ipSimulada � fechada.
     */
    public void finalizaIpSimulada() 
    throws Exception 
	{
    	try 
		{
    		this.camadaIpSimulada.finalizaCanal();
    		this.camadaIPSimuladaAberta = false;
		} 
    	catch (Exception e) 
		{
    		System.out.println("ProtocoloTCP.finalizaIpSimulada(): " + 
    				e.getMessage());
    		throw e;
		}
    }
    
    /** M�todo acessador para o atributo camadaIpSimulada.
     * @return A refer�ncia para o atributo camadaIpSimulada.
     *
     */
    public IpSimulada getCamadaIpSimulada() {
        return camadaIpSimulada;
    }
    
    /** M�todo modificador para o atributo camadaIpSimulada.
     * @param camadaIpSimulada Novo valor para o atributo camadaIpSimulada.
     *
     */
    public void setCamadaIpSimulada(IpSimulada _camadaIpSimulada) {
        this.camadaIpSimulada = _camadaIpSimulada;
    }

    /** 
     * M�todo acessador para o atributo camadaIPSimuladaAberta que verifica
     * se a camada IP Simulada j� est� aberta para esse protocolo.
     * 
     * @return Valor do atributo camadaIPSimuladaAberta.
     */
    public boolean getCamadaIPSimuladaAberta() {
        return camadaIPSimuladaAberta;
    }    
    
    /** 
     * M�todo modificador para o atributo camadaIPSimuladaAberta.
     * 
     * @param isCanalIpAberto Novo valor para o atributo camadaIPSimuladaAberta.
     */
    public void setCamadaIPSimuladaAberta(boolean _camadaIPSimuladaAberta) {
        this.camadaIPSimuladaAberta = _camadaIPSimuladaAberta;
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

	public static String nomeSegmento (PacoteTCP _pacote)
	{
		int controle = _pacote.getControle();
		int flag, i;
		String nomes[] = { "", "FIN", "SYN", "RST", "PSH", "ACK", "URG" };
		String nome="";
		
		for (flag=0x01, i=1; flag < 0x30; flag = flag << 1, i++)
		{
			if ((controle & flag) != 0)
				nome += ((nome.length()>0)? "+": "") + nomes[i];
		}
		
		if (nome.length()==0)
			nome = nomes[0];
		
		return nome;
	}
    
 
    /*
     * M�todo que executa o projeto.
     */
    public static void main(String args[]) {
    	// Cria uma inst�ncia do simulador TCP
        ProtocoloTCP protocoloTCP = new ProtocoloTCP();
        System.out.println("Iniciando Projeto...");
    }

}//fim da classe ProtocoloTCP