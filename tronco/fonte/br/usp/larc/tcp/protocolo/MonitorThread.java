package br.usp.larc.tcp.protocolo;

/*
 * @(#)MonitorThread.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

//import br.usp.larc.tcp.excecoes.CanalInexistenteException;
//import br.usp.larc.tcp.excecoes.TimeOutException;
import br.usp.larc.tcp.ipsimulada.IpSimulada;

/** 
 * Classe que monitora o buffer a espera de pacotes e entrega para o Objeto
 * Monitor.
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a 
 * criatividade na implementação do seu projeto.
 *
 * @author	Laboratório de Arquitetura e Redes de Computadores.
 * @version	1.0 Agosto 2003.
 */
public class MonitorThread extends Thread {
	
    /*
     * Monitor associado ao thread
     */
    private Monitor monitor;

    /*
     * Camada IP Simulada que a thread vai monitorar
     */
    private IpSimulada camadaIPSimulada;

    /*
     * Atributo que controla se a a thread deve continuar a monitorar a
     * camada IP Simulada
     */
    private boolean isRunning;


    /** 
     * Construtor da classe InputThread
     *
     * @param _monitor Monitor que o thread vai servir
     * @param _camadaIPSimulada Camada IP Simulada que a thread vai 
     * monitorar o buffer de entrada
     */ 
    public MonitorThread(Monitor _monitor, IpSimulada _camadaIPSimulada)
    {
        this.monitor = _monitor;
        this.camadaIPSimulada = _camadaIPSimulada;
        this.isRunning = true;
    }

    /*
     * Método de execução da thread
     */
    public void run()
    {
        String bufferEntrada = null;
        // indica se o buffer recebeu algo
        boolean bufferReceived = false;

        while(isRunning)
        {
            try
			{
                //verifica se tem dados no buffer de entrada,
                //se não tiver, gera exceção
                bufferEntrada = camadaIPSimulada.recebe(ProtocoloTCP.BUFFER_DEFAULT_IP_SIMULADA);
                bufferReceived = true;
            }
            catch(Exception e)
			{
                // Nada recebido
                //System.out.println(e.getMessage());
            }
            
            if(bufferReceived)
            {
                //recebe dados e entrega para o monitor analisar
                monitor.analisaDados(bufferEntrada);
            }
        }
    }

    /*
     * Indica se a thread está rodando
     *
     * @return boolean O estado do thread (true = monitorando e
     * false = não monitorando
     */

    public boolean isRunning()
    {
        return this.isRunning;
    }

    /*
     * Pára a thread
     */
    public void paraThread()
    {
        System.out.println("MonitorThread Parando...");
        this.isRunning = false;
    }
    
}//fim da classe MonitorThread