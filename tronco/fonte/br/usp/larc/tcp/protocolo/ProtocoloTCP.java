package br.usp.larc.tcp.protocolo;

/*
 * @(#)ProtocoloTCP.java 1.0 31/04/2004 Copyleft (L) 2004 Laborat�rio de
 * Arquitetura e Redes de Computadores. Escola Polit�cnica da Universidade de
 * S�o Paulo.
 */

import br.usp.larc.tcp.ipsimulada.IpSimulada;
import java.lang.Exception;

/**
 * Classe que encapasula de modo global todos as classes do Protocolo TCP
 * Simulado. � nessa classe que voc� voc� vai implementar as a��es que os
 * eventos que a Interfaces Monitor gera, fazendo com que as classes se
 * comuniquem entre si de acordo com cada a��o/evento. Procure sempre usar o
 * paradigma Orientado a Objeto, a simplicidade e a criatividade na
 * implementa��o do seu projeto.
 * 
 * @author Laborat�rio de Arquitetura e Redes de Computadores.
 * @version 1.0 Agosto 2003.
 */

public class ProtocoloTCP extends TCP
{

    /**
     * Atributo que representa a camada IpSimulada.
     */
    private IpSimulada camadaIpSimulada;

    /**
     * Atributo que representa se o canal IP esta aberto.
     */
    private boolean    camadaIPSimuladaAberta;

    /**
     * Objeto Monitor.
     */
    private Monitor    monitor;

    //adicione aqui outros atributos importantes que voc� julgar necess�rio

    /**
     * Construtor da classe ProtocoloTCP.
     */
    public ProtocoloTCP ()
    {
        this.init ();
    }

    /**
     * M�todo que inicializa os atributos do Protocolo TCP.
     */
    public void init ()
    {
        this.camadaIpSimulada = new IpSimulada ();
        this.camadaIPSimuladaAberta = false;
        this.monitor = new Monitor (this);
    }

    /**
     * M�todo que recebe primitivas da camada de aplica��o e executa as
     * opera��es para atender a a��o. As primitivas est�o definidas na interface
     * TCP.
     * 
     * @param _primitiva
     *        A primitiva que a aplica��o enviou.
     * @param args[]
     *        Um array de argumentos que a aplica��o pode enviar.
     * @exception Exception
     *            Caso ocorra algum erro ou exce��o, lan�a (throw) para quem
     *            chamou o m�todo.
     */
    public void recebePrimitivaAplicacao (int _primitiva, String args[]) throws Exception
    {
        System.out.println ("ProtocoloTCP.recebePrimitivaAplicacao: " + TCP.nomePrimitivaAplicacao[_primitiva]);
        
        try
        {
            switch (_primitiva)
            {
                case TCP.P_TCP_OPEN:
                    inicializaTCP ();
                    break;
                case TCP.P_TCP_CLOSE:
                    finalizaTCP ();
                    break;
                case TCP.P_TCP_OPEN_ME:
                    int portaME = Integer.parseInt (args[0]);
                    criaMaquinaEstado (portaME);
                    break;
                case TCP.P_TCP_CLOSE_ME:
                    int idConexao = Integer.parseInt (args[0]);
                    fechaMaquinaEstado (idConexao);
                    break;
                case TCP.P_TCP_RESET:
                    reinicializaTCP ();
                    break;
            }
        }
        catch (Exception e)
        {
            System.err.println ("recebePrimitivaAplicacao: erro em  "
                    + TCP.nomePrimitivaAplicacao[_primitiva]);
            System.err.flush ();
            throw new Exception ("Erro no recebimento de primitiva: "
                    + TCP.nomePrimitivaAplicacao[_primitiva] + "\n" + e.getMessage ());
        }

    }

    /**
     * M�todo que inicializa o protocolo TCP.
     * 
     * @exception Exception
     *            Caso ocorra algum erro ou exce��o, lan�a (throw) para quem
     *            chamou o m�todo.
     */
    public void inicializaTCP () throws Exception
    {
        if (!this.camadaIPSimuladaAberta)
        {
            try
            {
                System.out.println ("inicializaTcp: iniciando");
                this.inicializaIPSimulada (TCP.BUFFER_DEFAULT_IP_SIMULADA);
                String ipBytePonto = this.camadaIpSimulada.descobreCanalIPSimulado ();

                this.monitor.setIpSimuladoLocal (Decoder
                        .bytePontoToIpSimulado (ipBytePonto));
                this.camadaIPSimuladaAberta = true;
                System.out.println ("inicializaTcp: iniciado");
                System.out.println ("inicializaTcp: IP Simulado do MONITOR  : "
                                    + ipBytePonto);
                this.monitor.monitoraCamadaIP ();
            }
            catch (Exception e)
            {
                System.out.println ("ProtocoloTCP.inicializaTcp(): " + e.getMessage ());
                throw e;
            }
        }
        else
        {
            System.out.println ("Protocolo TCP j� inicializado.");
        }
    }

    /**
     * M�todo que finaliza o protocoloTCP e consequentemente o projeto.
     * 
     * @exception Exception
     *            Caso ocorra algum erro ou exce��o, joga (throw) para quem
     *            chamou o m�todo.
     */
    public void finalizaTCP () throws Exception
    {
        if (this.camadaIPSimuladaAberta)
        {
            try
            {
                if (this.camadaIpSimulada != null)
                {
                    this.monitor.terminaMonitoramentoCamadaIP ();
                    this.monitor.fechar ();
                    this.monitor = null;
                    this.finalizaIpSimulada ();
                    this.camadaIpSimulada = null;
                    System.out.println ("Protocolo TCP finalizado.");
                    System.exit (0);
                }
                else
                {
                    System.out.println ("Protocolo TCP n�o foi inicializado.");
                }
            }
            catch (Exception e)
            {
                System.out.println ("ProtocoloTCP.finalizaTCP(): " + e.getMessage ());
                throw e;
            }
        }
        else
        {
            System.out.println ("Protocolo TCP n�o foi inicializado.");
        }
    }

    /**
     * M�todo que abre uma nova M�quina de Estados associada a uma porta TCP
     * recebida como par�metro.
     * 
     * @param _portaME
     *        A porta TCP que ser� associada a m�quina de estados.
     * @exception Exception
     *            Caso ocorra algum erro ou exce��o, lan�a (throw) para quem
     *            chamou o m�todo.
     */
    public void criaMaquinaEstado (int _portaME) throws Exception
    {
        if (this.camadaIPSimuladaAberta)
        {
            try
            {
                if (!this.monitor.criaMaquinaDeEstados (_portaME))
                    throw new Exception ("Porta: " + _portaME + " j� usada.");
            }
            catch (Exception e)
            {
                System.out.println ("ProtocoloTCP.inicializaTcp(): " + e.getMessage ());
                throw e;
            }
        }
        else
            throw new Exception ("Protocolo TCP n�o inicializado.");
    }

    /**
     * M�todo que fecha m�quina de estados com id de Conex�o passada como
     * par�metro.
     * 
     * @param _idConexao
     *        O id da Conex�o da m�quina que voc� quer fechar.
     * @exception Exception
     *            Caso ocorra algum erro ou exce��o, lan�a (throw) para quem
     *            chamou o m�todo.
     */
    public void fechaMaquinaEstado (int _idConexao) throws Exception
    {
        if (this.camadaIPSimuladaAberta)
        {
            try
            {
                if (!this.monitor.fechaMaquinaDeEstados (_idConexao))
                {
                    throw new Exception ("Id: " + _idConexao + " n�o existe.");
                }
            }
            catch (Exception e)
            {
                System.out.println ("ProtocoloTCP.fechaMaquinaEstado(): "
                                    + e.getMessage ());
                throw e;
            }
        }
        else
        {
            throw new Exception ("Protocolo TCP n�o inicializado.");
        }
    }

    /**
     * M�todo que reinicializa o protocolo TCP fazendo com que o protocolo volte
     * ao seu estado inicial .
     * 
     * @exception Exception
     *            Caso ocorra algum erro ou exce��o, lan�a (throw) para quem
     *            chamou o m�todo.
     */
    public void reinicializaTCP () throws Exception
    {
        System.out.println ("ProtocoloTCP.reinicializaTCP:");
        if (this.camadaIPSimuladaAberta)
        {
            try
            {
                if (this.camadaIpSimulada != null)
                {
                    System.out.println ("ProtocoloTCP.reinicializaTCP: terminaMonitoramentoCamadaIP");
                    this.monitor.terminaMonitoramentoCamadaIP ();
                    System.out.println ("ProtocoloTCP.reinicializaTCP: reinicia");
                    this.monitor.reinicia();
                    System.out.println ("ProtocoloTCP.reinicializaTCP: finalizaIpSimulada");
                    this.finalizaIpSimulada ();
                    System.out.println ("ProtocoloTCP.reinicializaTCP: fim?");
                }
                else
                {
                    System.out.println ("Protocolo TCP n�o foi reinicializado.");
                }
            }
            catch (Exception e)
            {
                System.out.println ("ProtocoloTCP.reinicializaTCP: " + e.getMessage ());
                throw e;
            }
        }
        else
        {
            System.out.println ("Protocolo TCP n�o foi reinicializado.");
        }
    }

    /**
     * M�todo que inicializa a camada IpSimulada.
     * 
     * @param buffer
     *        tamanho do buffer.
     * @exception Exception
     *            excecao jogada quando inicializa a camada IpSimulada.
     */
    public void inicializaIPSimulada (int buffer) throws Exception
    {
        try
        {
            this.camadaIpSimulada.inicializaCanal (buffer);
            this.camadaIPSimuladaAberta = true;
        }
        catch (Exception e)
        {
            System.out.println ("ProtocoloTCP.inicializaIpSimulada: " + e.getMessage ());
            throw e;
        }
    }

    /**
     * M�todo que finaliza a camada ipSimulada.
     * 
     * @exception Exception
     *            excecao joagada quando a ipSimulada � fechada.
     */
    public void finalizaIpSimulada () throws Exception
    {
        System.out.println ("ProtocoloTCP.finalizaIpSimulada:");
        try
        {
            System.out.println ("ProtocoloTCP.finalizaIpSimulada: finalizaCanal");
            this.camadaIpSimulada.finalizaCanal ();
            System.out.println ("ProtocoloTCP.finalizaIpSimulada: fecha");
            this.camadaIPSimuladaAberta = false;
        }
        catch (Exception e)
        {
            System.err.println ("ProtocoloTCP.finalizaIpSimulada(): " + e.getMessage ());
            System.err.flush();
            throw e;
        }
        System.out.println ("ProtocoloTCP.finalizaIpSimulada: fim");
    }

    /**
     * M�todo acessador para o atributo camadaIpSimulada.
     * 
     * @return A refer�ncia para o atributo camadaIpSimulada.
     */
    public IpSimulada getCamadaIpSimulada ()
    {
        return this.camadaIpSimulada;
    }

    /**
     * M�todo modificador para o atributo camadaIpSimulada.
     * 
     * @param _camadaIpSimulada
     *        Novo valor para o atributo camadaIpSimulada.
     */
    public void setCamadaIpSimulada (IpSimulada _camadaIpSimulada)
    {
        this.camadaIpSimulada = _camadaIpSimulada;
    }

    /**
     * M�todo acessador para o atributo camadaIPSimuladaAberta que verifica se a
     * camada IP Simulada j� est� aberta para esse protocolo.
     * 
     * @return Valor do atributo camadaIPSimuladaAberta.
     */
    public boolean getCamadaIPSimuladaAberta ()
    {
        return this.camadaIPSimuladaAberta;
    }

    /**
     * M�todo modificador para o atributo camadaIPSimuladaAberta.
     * 
     * @param _camadaIPSimuladaAberta
     *        Novo valor para o atributo camadaIPSimuladaAberta.
     */
    public void setCamadaIPSimuladaAberta (boolean _camadaIPSimuladaAberta)
    {
        this.camadaIPSimuladaAberta = _camadaIPSimuladaAberta;
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
     * M�todo que executa o projeto.
     * 
     * @param args
     *        Par�metros da linha de comando
     */
    public static void main (String args[])
    {
        // Cria uma inst�ncia do simulador TCP
        ProtocoloTCP protocoloTCP = new ProtocoloTCP ();
        System.out.println ("ProtocoloTCP.main: Iniciando projeto: "
                            + protocoloTCP.toString ());
    }

} //fim da classe ProtocoloTCP
