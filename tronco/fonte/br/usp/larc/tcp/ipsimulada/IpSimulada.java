/*
 * @(#)IpSimulada.java	1.0 31/03/2003
 *
 * Copyleft (L) 2003 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

package br.usp.larc.tcp.ipsimulada;

import br.usp.larc.tcp.excecoes.*;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Classe que implementa os serviços forncecidos pela interface IpSimuladaIF.
 * Ela abstrai a comunicação com uma camada (canal) UDP através do envio e
 * recebimento de datagramas UDP, simulando uma camada IP.
 *
 *
 * @author	Laboratório de Arquitetura e Redes de Computadores.
 * @version	1.0 09 Maio 2003.
 */

public class IpSimulada implements IpSimuladaIF {
    
    /**
     * Atributo que representa o DatagramaSocket que vai ser manipulado pelo
     * Canal.
     */
    private DatagramSocket socket;
    
    /**
     * Atributo que contém o IP real do Canal.
     */
    private String ipReal;
    
    /**
     * Atributo que contém o a porta (UDP) do Canal.
     */
    private int porta;
    
    
    /** Constante que contém um valor máximo para o tamanho dos buffers de
     * saída e entrada do canal.
     */    
    private final static int TAMANHO_MAXIMO_BUFFER = 8192;
    
    /** Constante que contém um valor default para o tamanho dos buffers de
     * saída e entrada do canal.
     */    
    private final static int TAMANHO_DEFAULT_BUFFER = 1024;
    
    /** Constante que contém um valor default para o timeout para o buffer de
     * recebimento do canal.
     */    
    private final static int TIMEOUT_DEFAULT = 1000;
    
    /** Construtor da Classe */    
    public IpSimulada() {
        this.porta = 0;
    }
    
    /** Método acessador para o atributo ipReal.
     * @return String  O IP real do canal.
     */    
    public String getIpReal() {
        return this.ipReal;
    }
    
    /** Método acessador para o atributo porta.
     * @return int A porta (UDP) do canal.
     */    
    public int getPorta() {
        return this.porta;
    }
    
    /** Método acessador para o tamanho do buffer de entrada.
     * @return int O tamanho do buffer de entrada.
     * @throws CanalInexistenteException Exceção gerada se acontecer algum erro.
     */    
    public int getTamanhoBufferRx() throws CanalInexistenteException {
        try {
            return this.socket.getReceiveBufferSize();
        }catch (SocketException ex) {
            throw new CanalInexistenteException("IpSimulada: " + 
                "getTamanhoBufferRx()" + ex.getMessage());
        }
    }
    
    /** Método modificador para o tamanho do buffer de entrada.
     * @param _tamanhoBufferCanal O novo tamanho do buffer de entrada para o canal.
     * @throws BufferOverflowException Exceção gerada se estourar valor máximo permitido para o buffer.
     * @throws CanalInexistenteException Exceção gerada se acontecer algum erro.
     */    
    public void setTamanhoBufferRx(int _tamanhoBufferCanal) throws
        CanalInexistenteException, BufferOverflowException {
        try {
            if (_tamanhoBufferCanal > 8192) {
                throw new BufferOverflowException("IpSimulada.setTamanhoBufferRx(): " +
                    "Erro: Estouro de tamanho máximo(8192) do Buffer");
            } else {
                this.socket.setReceiveBufferSize(_tamanhoBufferCanal);
            }
        }catch (SocketException ex) {
            throw new CanalInexistenteException("IpSimulada.setTamanhoBufferRx(): " + 
            ex.getMessage());
        }
    }
    
    /** Método acessador para o tamanho do buffer de saída.
     * @return int O tamanho do buffer de saída.
     * @throws CanalInexistenteException Exceção gerada se acontecer algum erro.
     */    
    public int getTamanhoBufferTx() throws CanalInexistenteException {
        try{
            return this.socket.getSendBufferSize();
        } catch (SocketException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IpSimulada.getTamanhoBufferTx(): " +
                ex.getMessage());
        }        
    }
    
    /** Método modificador para o tamanho do buffer de saída.
     * @param _tamanhoBufferCanal O novo tamanho do buffer de saída.
     * @throws BufferOverflowException Exceção gerada se estourar valor máximo permitido para o buffer.
     * @throws CanalInexistenteException Exceção gerada se acontecer algum erro.
     */    
    public void setTamanhoBufferTx(int _tamanhoBufferCanal) 
        throws CanalInexistenteException, BufferOverflowException {
        try{
            if (_tamanhoBufferCanal > 8192) {
                throw new BufferOverflowException("IpSimulada.setTamanhoBufferTx(): " +
                    "Erro: Estouro de tamanho máximo(8192) do Buffer");
            } else {
                this.socket.setSendBufferSize(_tamanhoBufferCanal);
            }
        } catch (SocketException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IpSimulada.setTamanhoBufferTx(): " + 
                ex.getMessage());
        }        
    }
    
    /** Método que altera o valor de timeout (em ms) do buffer de entrada do canal.
     * @param _timeout O novo timeout do buffer de entrada do canal.
     * @throws CanalInexistenteException Exceção gerada se acontecer algum erro.
     */    
    public void setTimeout(int _timeout) throws CanalInexistenteException {
        try {
            this.socket.setSoTimeout(_timeout);
        } catch (SocketException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IpSimulada.setTimeout(): " + 
                ex.getMessage());
        }   
    }
    
    /** Método que inicializa um canal na camada IPSimulada.
     * @param _tamanhoBuffer O tamanho do buffer usado pelo canal.
     * @throws BindException Exceção gerada quando ocorre algum erro na inicialização do canal.
     * @throws BufferOverflowException Exceção gerada se estourar valor máximo permitido para o buffer.
     */
    public void inicializaCanal(int _tamanhoBuffer) throws br.usp.larc.tcp.excecoes.BufferOverflowException, br.usp.larc.tcp.excecoes.BindException {
        // Criar socket datagrama
        if (_tamanhoBuffer > 8192) {
            throw new BufferOverflowException
            ("IpSimulada.incializaCanal(): Erro: Estouro de tamanho máximo(8192) do Buffer");
        } else {
            try{
                this.socket = new DatagramSocket();
                this.socket.setReceiveBufferSize(_tamanhoBuffer);
                this.socket.setSendBufferSize(_tamanhoBuffer);
                this.porta = socket.getLocalPort();
                this.socket.setSoTimeout(TIMEOUT_DEFAULT);
                this.ipReal = (String) (InetAddress.getLocalHost()).getHostAddress();
            } catch (Exception ex) {
                throw new br.usp.larc.tcp.excecoes.BindException("IpSimulada.inicializaCanal(): " + 
                    ex.getMessage());
            }
        }
    }
    
    /** Método que recebe dados através de um canal inicializado.
     * @return String Os dados recebidos no buffer de entrada do canal.
     * @param _tamanhoBuffer O tamanho do buffer que será usado para a recepção.
     * @throws TimeOutException Exceção gerada se o timeout do buffer de entrada expira.
     * @throws CanalInexistenteException Exceçao gerada se algum erro na interface de I/O acontece.
     */
    public String recebe(int _tamanhoBuffer) throws br.usp.larc.tcp.excecoes.CanalInexistenteException, br.usp.larc.tcp.excecoes.TimeOutException {
        try {
            this.setTamanhoBufferRx(_tamanhoBuffer);
            byte[] buffer = new byte[_tamanhoBuffer];
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            socket.receive(dp);
            String recebidos = new String(dp.getData(),0,dp.getLength(),"ISO-8859-1");
            return recebidos;
        } catch (SocketTimeoutException ex) {
            throw new  br.usp.larc.tcp.excecoes.TimeOutException("IPSimulada.recebe():" +
            "Buffer de Recepção Vazio");
        } catch (IOException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.recebe(): Erro de I/O:" 
                + ex.getMessage());
        } catch (Exception ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.recebe(): Erro :" 
                + ex.getMessage());
        }
        
    }
    
    /** Método que transmite dados através de um canal inicializado.
     * @param _nomeMaquinaDestino Nome (hostname:porta) da máquina destino.
     * @param _bufferSaida O conteúdo dos dados que serão enviados.
     * @param _tamanhoBuffer O tamanho do buffer que será usada para a transmissão.
     * @throws InvalidArgumentException Exceção gerada se receber argumentos inválidos de entrada.
     * @throws CanalInexistenteException Exceção gerada se ocorrer erro ao tentar.
     *  transmitir através de canal que não existe (ou não inicializado) ou se acontecer.
     *  algum erro na interface de I/O .
     */
    public void transmite(String _nomeMaquinaDestino, String _bufferSaida, int _tamanhoBuffer) throws br.usp.larc.tcp.excecoes.CanalInexistenteException, InvalidArgumentException {
       try { 
            this.setTamanhoBufferTx(_tamanhoBuffer); 
            byte[] data = _bufferSaida.getBytes("ISO-8859-1");
            String hostname = descobreNomeIPSimulado(_nomeMaquinaDestino);
            int porta = Integer.parseInt(descobrePortaIPSimulado(_nomeMaquinaDestino));
            DatagramPacket theOutput = new DatagramPacket(data, data.length, InetAddress.getByName(hostname), porta);
            socket.send(theOutput);
        } catch (InvalidArgumentException ex) {
            throw new InvalidArgumentException("IPSimulada.transmite():" +
            "Argumentos de entrada inválidos");
        } catch (IOException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.transmite(): Erro de I/O:"
                + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("IPSimulada.transmite() Erro: " + ex.getMessage());
        }
       
    }
    
    /** Método que transmite dados através de um canal inicializado.
     * @param _ipMaquinaDestino O IP real (sem os :portaTCP) da máquina destino para qual serão transmitidos os dados.
     * @param _bufferSaida O conteúdo dos dados que serão enviados.
     * @param _tamanhoBuffer O tamanho do buffer que será usada para a transmissão.
     * @param _porta A porta (UDP) da máquina destino para qual serão entregues os dados.
     * @throws CanalInexistenteException Exceção gerada se ocorrer erro ao tentar.
     *  transmitir através de canal que não existe (ou não inicializado) ou se acontecer.
     *  algum erro na interface de I/O .
     */
    public void transmite(String _ipMaquinaDestino, String _bufferSaida, int _tamanhoBuffer, int _porta) throws CanalInexistenteException {
        try{
            this.setTamanhoBufferTx(_tamanhoBuffer); 
            byte[] data = _bufferSaida.getBytes("ISO-8859-1");
            DatagramPacket theOutput = new DatagramPacket(data, data.length, InetAddress.getByName(_ipMaquinaDestino), _porta);
            socket.send(theOutput);
        } catch (IOException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.transmite(): Erro de I/O:"
                + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("IPSimulada.transmite() Erro: " + ex.getMessage());
        }
    }
    
    /** Método que retorna nome da estação local onde o canal foi inicializado.
     * @return String O nome da estação local (hostname).
     * @throws UnknownHostException Execeção gerada se não conseguir resolver o nome da estação local
     */
    public String descobreNomeEstacaoLocal() throws UnknownHostException {
        try {
            return (String) (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("IPSimulada.descobreNomeEstacaoLocal() - Erro: " + ex.getMessage());
        }        
    }
    
    /** Método que retorna nome da estação local com o seu domínio de rede onde o canal foi inicializado.
     * @return String O nome da estação (hostname + domínio) local.
     * @throws UnknownHostException Execeção gerada se não conseguir resolver o nome da estação/domínio da estação.
     */
    public String descobreNomeDominioEstacaoLocal() throws UnknownHostException {
        try {
            return (String) (InetAddress.getLocalHost()).getCanonicalHostName();
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("IPSimulada.descobreNomeDominioEstacaoLocal() - Erro: " + ex.getMessage());
        }   
    }
    
    /** Método que retorna o nome do canal.
     * @return String O nome do canal (hostname:PortaUDP).
     * @throws CanalInexistenteException Exceção gerada se ocorrer erro ao tentar
     *  transmitir através de canal que não existe (ou não inicializado).
     */
    public String descobreCanalNomeSimulado() throws br.usp.larc.tcp.excecoes.CanalInexistenteException {
        try {
            return (String) this.descobreNomeEstacaoLocal() + ":" + (this.getPorta());
        } catch (UnknownHostException ex) {
            System.out.println("IPSimulada.descobreCanalNomeSimulado() Erro: " + ex.getMessage());
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.descobreCanalNomeSimulado(): Canal Inexistente");
        }
    }
    
    /** Método que retorna o IPSimulado (IpReal:PortaUDP) do canal.
     * @return String O IPSimulado (IpReal:PortaUDP) do canal.
     * @throws CanalInexistenteException Exceção gerada se ocorrer erro ao tentar
     *  transmitir através de canal que não existe (ou não inicializado).
     */
    public String descobreCanalIPSimulado() throws CanalInexistenteException {
        if ( (this.ipReal == null) || (this.porta == 0) ) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.descobreCanalIPSimulado(): Canal Inexistente");
        } else {
            return (String) this.ipReal + ":" + (this.porta);
        }
        
    }
    
    /** Método que retorna o nome do IPSimulado (elemento da parte esquerda) do canal.
     * @return String O nome do IPSimulado do canal.
     * @param _enderecoSimulado O endereço IP Simulado (IpReal:PortaUDP ou hostname:PortaUDP) do canal.
     * @throws InvalidArgumentException Exceção gerada se receber argumentos inválidos de entrada.
     */
    public static String descobreNomeIPSimulado(String _enderecoSimulado)
    throws InvalidArgumentException
    {
        if ( (_enderecoSimulado == null) || (_enderecoSimulado.equals("")) )
        {
            throw new InvalidArgumentException("IPSimulada.descobreNomeIPSimulado(): Argumento Inválido: " 
                    + _enderecoSimulado);
        }
        else
        {
            StringTokenizer stringTokenizer = new StringTokenizer(_enderecoSimulado ,":");
            return (String)stringTokenizer.nextToken();
        }
        
        
    }
    
    /** Método que retorna a porta do IPSimulado (elemento da parte direita) do canal.
     * @return String A porta do IPSimulado do canal.
     * @param _enderecoSimulado O endereço IP Simulado (IpReal:PortaUDP ou hostname:PortaUDP) do canal.
     * @throws InvalidArgumentException Exceção gerada se receber argumentos inválidos de entrada.
     */
    public static String descobrePortaIPSimulado(String _enderecoSimulado) throws InvalidArgumentException {
        if ( (_enderecoSimulado == null) || (_enderecoSimulado.equals("")) ) {
            throw new InvalidArgumentException("IPSimulada.descobreNomeIPSimulado(): Argumento Inválido: " 
                + _enderecoSimulado);
        } else {
            StringTokenizer stringTokenizer = new StringTokenizer(_enderecoSimulado,":");
            stringTokenizer.nextToken();
            return ((String)stringTokenizer.nextToken());
        }
    }
    
    /** Método que o descobre o IP simulado de uma estação dado o enderecoSimulado.
     * @return String O IPSimulado da estação.
     * @param _nomeEstacao O nome da estação (hostname[domínio]:PortaUDP.
     * @throws InvalidArgumentException Exceção gerada se receber argumentos inválidos de entrada.
     */
    public String descobreIPSimulado(String _nomeEstacao) throws InvalidArgumentException {
        try {
            String hostname = (InetAddress.getByName(descobreNomeIPSimulado(_nomeEstacao))).getHostAddress();
            int porta = Integer.parseInt(descobrePortaIPSimulado(_nomeEstacao));
            return (String) hostname + ":" + Integer.toString(porta);
        } catch (Exception ex) {
            throw new InvalidArgumentException("IPSimulada.descobreIPSimulado(): Argumento Inválido: " 
                + _nomeEstacao);
        }
    }
    
    /** Método que descobre o nome simulado de uma estação a partir do seu IP simulado
     * @return String O nome da estação (hostname[domínio]:PortaUDP.
     * @param _enderecoIp O endereço IP Simulado (IpReal:PortaUDP ou hostname:PortaUDP) do canal.
     * @throws InvalidArgumentException Exceção gerada se receber argumentos inválidos de entrada.
     */
    public static String descobreNomeEstacao(String _enderecoIp) throws InvalidArgumentException{
        try {
            int porta = Integer.parseInt(descobrePortaIPSimulado(_enderecoIp));
            return (InetAddress.getByName(descobreNomeIPSimulado(_enderecoIp))).getHostName() + ":" + Integer.toString(porta);
        } catch (Exception ex) {
            throw new InvalidArgumentException("IPSimulada.descobreNomeEstacao(): Argumento Inválido: " 
                + _enderecoIp);
        }
    }
    
    /** Método que finaliza o canal
     * @throws CanalInexistenteException gerada se ocorrer erro ao tentar
     *  transmitir através de canal que não existe (ou não inicializado).
     */
    public void finalizaCanal() throws br.usp.larc.tcp.excecoes.CanalInexistenteException {
        try {
            socket.disconnect();
            socket.close();
        } catch (Exception ex) {
            System.out.println("IPSimulada.finalizaCanal() Erro: " + ex.getMessage());
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.finalizaCanal(): Canal Inexistente");

        }
    }
}
