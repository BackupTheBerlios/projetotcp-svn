package br.usp.larc.tcp.protocolo;

/*
 * @(#)ProtocoloTCP.java 1.0 31/04/2004 Copyleft (L) 2004 Laboratório de
 * Arquitetura e Redes de Computadores. Escola Politécnica da Universidade de
 * São Paulo.
 */

import br.usp.larc.tcp.ipsimulada.IpSimulada;
import java.lang.Exception;

/**
 * Classe que encapasula de modo global todos as classes do Protocolo TCP
 * Simulado. Ë nessa classe que você você vai implementar as ações que os
 * eventos que a Interfaces Monitor gera, fazendo com que as classes se
 * comuniquem entre si de acordo com cada ação/evento. Procure sempre usar o
 * paradigma Orientado a Objeto, a simplicidade e a criatividade na
 * implementação do seu projeto.
 * 
 * @author Laboratório de Arquitetura e Redes de Computadores.
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

    //adicione aqui outros atributos importantes que você julgar necessário

    /**
     * Construtor da classe ProtocoloTCP.
     */
    public ProtocoloTCP ()
    {
        this.init ();
    }

    /**
     * Método que inicializa os atributos do Protocolo TCP.
     */
    public void init ()
    {
        this.camadaIpSimulada = new IpSimulada ();
        this.camadaIPSimuladaAberta = false;
        this.monitor = new Monitor (this);
    }

    /**
     * Método que recebe primitivas da camada de aplicação e executa as
     * operações para atender a ação. As primitivas estão definidas na interface
     * TCP.
     * 
     * @param _primitiva
     *        A primitiva que a aplicação enviou.
     * @param args[]
     *        Um array de argumentos que a aplicação pode enviar.
     * @exception Exception
     *            Caso ocorra algum erro ou exceção, lança (throw) para quem
     *            chamou o método.
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
     * Método que inicializa o protocolo TCP.
     * 
     * @exception Exception
     *            Caso ocorra algum erro ou exceção, lança (throw) para quem
     *            chamou o método.
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
            System.out.println ("Protocolo TCP já inicializado.");
        }
    }

    /**
     * Método que finaliza o protocoloTCP e consequentemente o projeto.
     * 
     * @exception Exception
     *            Caso ocorra algum erro ou exceção, joga (throw) para quem
     *            chamou o método.
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
                    System.out.println ("Protocolo TCP não foi inicializado.");
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
            System.out.println ("Protocolo TCP não foi inicializado.");
        }
    }

    /**
     * Método que abre uma nova Máquina de Estados associada a uma porta TCP
     * recebida como parâmetro.
     * 
     * @param _portaME
     *        A porta TCP que será associada a máquina de estados.
     * @exception Exception
     *            Caso ocorra algum erro ou exceção, lança (throw) para quem
     *            chamou o método.
     */
    public void criaMaquinaEstado (int _portaME) throws Exception
    {
        if (this.camadaIPSimuladaAberta)
        {
            try
            {
                if (!this.monitor.criaMaquinaDeEstados (_portaME))
                    throw new Exception ("Porta: " + _portaME + " já usada.");
            }
            catch (Exception e)
            {
                System.out.println ("ProtocoloTCP.inicializaTcp(): " + e.getMessage ());
                throw e;
            }
        }
        else
            throw new Exception ("Protocolo TCP não inicializado.");
    }

    /**
     * Método que fecha máquina de estados com id de Conexão passada como
     * parâmetro.
     * 
     * @param _idConexao
     *        O id da Conexão da máquina que você quer fechar.
     * @exception Exception
     *            Caso ocorra algum erro ou exceção, lança (throw) para quem
     *            chamou o método.
     */
    public void fechaMaquinaEstado (int _idConexao) throws Exception
    {
        if (this.camadaIPSimuladaAberta)
        {
            try
            {
                if (!this.monitor.fechaMaquinaDeEstados (_idConexao))
                {
                    throw new Exception ("Id: " + _idConexao + " não existe.");
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
            throw new Exception ("Protocolo TCP não inicializado.");
        }
    }

    /**
     * Método que reinicializa o protocolo TCP fazendo com que o protocolo volte
     * ao seu estado inicial .
     * 
     * @exception Exception
     *            Caso ocorra algum erro ou exceção, lança (throw) para quem
     *            chamou o método.
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
                    System.out.println ("Protocolo TCP não foi reinicializado.");
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
            System.out.println ("Protocolo TCP não foi reinicializado.");
        }
    }

    /**
     * Método que inicializa a camada IpSimulada.
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
     * Método que finaliza a camada ipSimulada.
     * 
     * @exception Exception
     *            excecao joagada quando a ipSimulada é fechada.
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
     * Método acessador para o atributo camadaIpSimulada.
     * 
     * @return A referência para o atributo camadaIpSimulada.
     */
    public IpSimulada getCamadaIpSimulada ()
    {
        return this.camadaIpSimulada;
    }

    /**
     * Método modificador para o atributo camadaIpSimulada.
     * 
     * @param _camadaIpSimulada
     *        Novo valor para o atributo camadaIpSimulada.
     */
    public void setCamadaIpSimulada (IpSimulada _camadaIpSimulada)
    {
        this.camadaIpSimulada = _camadaIpSimulada;
    }

    /**
     * Método acessador para o atributo camadaIPSimuladaAberta que verifica se a
     * camada IP Simulada já está aberta para esse protocolo.
     * 
     * @return Valor do atributo camadaIPSimuladaAberta.
     */
    public boolean getCamadaIPSimuladaAberta ()
    {
        return this.camadaIPSimuladaAberta;
    }

    /**
     * Método modificador para o atributo camadaIPSimuladaAberta.
     * 
     * @param _camadaIPSimuladaAberta
     *        Novo valor para o atributo camadaIPSimuladaAberta.
     */
    public void setCamadaIPSimuladaAberta (boolean _camadaIPSimuladaAberta)
    {
        this.camadaIPSimuladaAberta = _camadaIPSimuladaAberta;
    }

    /**
     * Método acessador para o atributo monitor.
     * 
     * @return A referência para o atributo monitor.
     */
    public Monitor getMonitor ()
    {
        return this.monitor;
    }

    /**
     * Método modificador para o atributo monitor.
     * 
     * @param _monitor
     *        Novo valor para o atributo monitor.
     */
    public void setMonitor (Monitor _monitor)
    {
        this.monitor = _monitor;
    }

    /**
     * Método que executa o projeto.
     * 
     * @param args
     *        Parâmetros da linha de comando
     */
    public static void main (String args[])
    {
        // Cria uma instância do simulador TCP
        ProtocoloTCP protocoloTCP = new ProtocoloTCP ();
        System.out.println ("ProtocoloTCP.main: Iniciando projeto: "
                            + protocoloTCP.toString ());
    }

} //fim da classe ProtocoloTCP
