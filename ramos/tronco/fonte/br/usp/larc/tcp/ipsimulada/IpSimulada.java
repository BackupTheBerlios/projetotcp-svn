/*
 * @(#)IpSimulada.java	1.0 31/03/2003
 *
 * Copyleft (L) 2003 Laborat�rio de Arquitetura e Redes de Computadores
 * Escola Polit�cnica da Universidade de S�o Paulo.
 *
 */

package br.usp.larc.tcp.ipsimulada;

import br.usp.larc.tcp.excecoes.*;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Classe que implementa os servi�os forncecidos pela interface IpSimuladaIF.
 * Ela abstrai a comunica��o com uma camada (canal) UDP atrav�s do envio e
 * recebimento de datagramas UDP, simulando uma camada IP.
 *
 *
 * @author	Laborat�rio de Arquitetura e Redes de Computadores.
 * @version	1.0 09 Maio 2003.
 */

public class IpSimulada implements IpSimuladaIF {
    
    /**
     * Atributo que representa o DatagramaSocket que vai ser manipulado pelo
     * Canal.
     */
    private DatagramSocket socket;
    
    /**
     * Atributo que cont�m o IP real do Canal.
     */
    private String ipReal;
    
    /**
     * Atributo que cont�m o a porta (UDP) do Canal.
     */
    private int porta;
    
    
    /** Constante que cont�m um valor m�ximo para o tamanho dos buffers de
     * sa�da e entrada do canal.
     */    
    private final static int TAMANHO_MAXIMO_BUFFER = 8192;
    
    /** Constante que cont�m um valor default para o tamanho dos buffers de
     * sa�da e entrada do canal.
     */    
    private final static int TAMANHO_DEFAULT_BUFFER = 1024;
    
    /** Constante que cont�m um valor default para o timeout para o buffer de
     * recebimento do canal.
     */    
    private final static int TIMEOUT_DEFAULT = 1000;
    
    /** Construtor da Classe */    
    public IpSimulada() {
        this.porta = 0;
    }
    
    /** M�todo acessador para o atributo ipReal.
     * @return String  O IP real do canal.
     */    
    public String getIpReal() {
        return this.ipReal;
    }
    
    /** M�todo acessador para o atributo porta.
     * @return int A porta (UDP) do canal.
     */    
    public int getPorta() {
        return this.porta;
    }
    
    /** M�todo acessador para o tamanho do buffer de entrada.
     * @return int O tamanho do buffer de entrada.
     * @throws CanalInexistenteException Exce��o gerada se acontecer algum erro.
     */    
    public int getTamanhoBufferRx() throws CanalInexistenteException {
        try {
            return this.socket.getReceiveBufferSize();
        }catch (SocketException ex) {
            throw new CanalInexistenteException("IpSimulada: " + 
                "getTamanhoBufferRx()" + ex.getMessage());
        }
    }
    
    /** M�todo modificador para o tamanho do buffer de entrada.
     * @param _tamanhoBufferCanal O novo tamanho do buffer de entrada para o canal.
     * @throws BufferOverflowException Exce��o gerada se estourar valor m�ximo permitido para o buffer.
     * @throws CanalInexistenteException Exce��o gerada se acontecer algum erro.
     */    
    public void setTamanhoBufferRx(int _tamanhoBufferCanal) throws
        CanalInexistenteException, BufferOverflowException {
        try {
            if (_tamanhoBufferCanal > 8192) {
                throw new BufferOverflowException("IpSimulada.setTamanhoBufferRx(): " +
                    "Erro: Estouro de tamanho m�ximo(8192) do Buffer");
            } else {
                this.socket.setReceiveBufferSize(_tamanhoBufferCanal);
            }
        }catch (SocketException ex) {
            throw new CanalInexistenteException("IpSimulada.setTamanhoBufferRx(): " + 
            ex.getMessage());
        }
    }
    
    /** M�todo acessador para o tamanho do buffer de sa�da.
     * @return int O tamanho do buffer de sa�da.
     * @throws CanalInexistenteException Exce��o gerada se acontecer algum erro.
     */    
    public int getTamanhoBufferTx() throws CanalInexistenteException {
        try{
            return this.socket.getSendBufferSize();
        } catch (SocketException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IpSimulada.getTamanhoBufferTx(): " +
                ex.getMessage());
        }        
    }
    
    /** M�todo modificador para o tamanho do buffer de sa�da.
     * @param _tamanhoBufferCanal O novo tamanho do buffer de sa�da.
     * @throws BufferOverflowException Exce��o gerada se estourar valor m�ximo permitido para o buffer.
     * @throws CanalInexistenteException Exce��o gerada se acontecer algum erro.
     */    
    public void setTamanhoBufferTx(int _tamanhoBufferCanal) 
        throws CanalInexistenteException, BufferOverflowException {
        try{
            if (_tamanhoBufferCanal > 8192) {
                throw new BufferOverflowException("IpSimulada.setTamanhoBufferTx(): " +
                    "Erro: Estouro de tamanho m�ximo(8192) do Buffer");
            } else {
                this.socket.setSendBufferSize(_tamanhoBufferCanal);
            }
        } catch (SocketException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IpSimulada.setTamanhoBufferTx(): " + 
                ex.getMessage());
        }        
    }
    
    /** M�todo que altera o valor de timeout (em ms) do buffer de entrada do canal.
     * @param _timeout O novo timeout do buffer de entrada do canal.
     * @throws CanalInexistenteException Exce��o gerada se acontecer algum erro.
     */    
    public void setTimeout(int _timeout) throws CanalInexistenteException {
        try {
            this.socket.setSoTimeout(_timeout);
        } catch (SocketException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IpSimulada.setTimeout(): " + 
                ex.getMessage());
        }   
    }
    
    /** M�todo que inicializa um canal na camada IPSimulada.
     * @param _tamanhoBuffer O tamanho do buffer usado pelo canal.
     * @throws BindException Exce��o gerada quando ocorre algum erro na inicializa��o do canal.
     * @throws BufferOverflowException Exce��o gerada se estourar valor m�ximo permitido para o buffer.
     */
    public void inicializaCanal(int _tamanhoBuffer) throws br.usp.larc.tcp.excecoes.BufferOverflowException, br.usp.larc.tcp.excecoes.BindException {
        // Criar socket datagrama
        if (_tamanhoBuffer > 8192) {
            throw new BufferOverflowException
            ("IpSimulada.incializaCanal(): Erro: Estouro de tamanho m�ximo(8192) do Buffer");
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
    
    /** M�todo que recebe dados atrav�s de um canal inicializado.
     * @return String Os dados recebidos no buffer de entrada do canal.
     * @param _tamanhoBuffer O tamanho do buffer que ser� usado para a recep��o.
     * @throws TimeOutException Exce��o gerada se o timeout do buffer de entrada expira.
     * @throws CanalInexistenteException Exce�ao gerada se algum erro na interface de I/O acontece.
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
            "Buffer de Recep��o Vazio");
        } catch (IOException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.recebe(): Erro de I/O:" 
                + ex.getMessage());
        } catch (Exception ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.recebe(): Erro :" 
                + ex.getMessage());
        }
        
    }
    
    /** M�todo que transmite dados atrav�s de um canal inicializado.
     * @param _nomeMaquinaDestino Nome (hostname:porta) da m�quina destino.
     * @param _bufferSaida O conte�do dos dados que ser�o enviados.
     * @param _tamanhoBuffer O tamanho do buffer que ser� usada para a transmiss�o.
     * @throws InvalidArgumentException Exce��o gerada se receber argumentos inv�lidos de entrada.
     * @throws CanalInexistenteException Exce��o gerada se ocorrer erro ao tentar.
     *  transmitir atrav�s de canal que n�o existe (ou n�o inicializado) ou se acontecer.
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
            "Argumentos de entrada inv�lidos");
        } catch (IOException ex) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.transmite(): Erro de I/O:"
                + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("IPSimulada.transmite() Erro: " + ex.getMessage());
        }
       
    }
    
    /** M�todo que transmite dados atrav�s de um canal inicializado.
     * @param _ipMaquinaDestino O IP real (sem os :portaTCP) da m�quina destino para qual ser�o transmitidos os dados.
     * @param _bufferSaida O conte�do dos dados que ser�o enviados.
     * @param _tamanhoBuffer O tamanho do buffer que ser� usada para a transmiss�o.
     * @param _porta A porta (UDP) da m�quina destino para qual ser�o entregues os dados.
     * @throws CanalInexistenteException Exce��o gerada se ocorrer erro ao tentar.
     *  transmitir atrav�s de canal que n�o existe (ou n�o inicializado) ou se acontecer.
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
    
    /** M�todo que retorna nome da esta��o local onde o canal foi inicializado.
     * @return String O nome da esta��o local (hostname).
     * @throws UnknownHostException Exece��o gerada se n�o conseguir resolver o nome da esta��o local
     */
    public String descobreNomeEstacaoLocal() throws UnknownHostException {
        try {
            return (String) (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("IPSimulada.descobreNomeEstacaoLocal() - Erro: " + ex.getMessage());
        }        
    }
    
    /** M�todo que retorna nome da esta��o local com o seu dom�nio de rede onde o canal foi inicializado.
     * @return String O nome da esta��o (hostname + dom�nio) local.
     * @throws UnknownHostException Exece��o gerada se n�o conseguir resolver o nome da esta��o/dom�nio da esta��o.
     */
    public String descobreNomeDominioEstacaoLocal() throws UnknownHostException {
        try {
            return (String) (InetAddress.getLocalHost()).getCanonicalHostName();
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("IPSimulada.descobreNomeDominioEstacaoLocal() - Erro: " + ex.getMessage());
        }   
    }
    
    /** M�todo que retorna o nome do canal.
     * @return String O nome do canal (hostname:PortaUDP).
     * @throws CanalInexistenteException Exce��o gerada se ocorrer erro ao tentar
     *  transmitir atrav�s de canal que n�o existe (ou n�o inicializado).
     */
    public String descobreCanalNomeSimulado() throws br.usp.larc.tcp.excecoes.CanalInexistenteException {
        try {
            return (String) this.descobreNomeEstacaoLocal() + ":" + (this.getPorta());
        } catch (UnknownHostException ex) {
            System.out.println("IPSimulada.descobreCanalNomeSimulado() Erro: " + ex.getMessage());
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.descobreCanalNomeSimulado(): Canal Inexistente");
        }
    }
    
    /** M�todo que retorna o IPSimulado (IpReal:PortaUDP) do canal.
     * @return String O IPSimulado (IpReal:PortaUDP) do canal.
     * @throws CanalInexistenteException Exce��o gerada se ocorrer erro ao tentar
     *  transmitir atrav�s de canal que n�o existe (ou n�o inicializado).
     */
    public String descobreCanalIPSimulado() throws CanalInexistenteException {
        if ( (this.ipReal == null) || (this.porta == 0) ) {
            throw new br.usp.larc.tcp.excecoes.CanalInexistenteException("IPSimulada.descobreCanalIPSimulado(): Canal Inexistente");
        } else {
            return (String) this.ipReal + ":" + (this.porta);
        }
        
    }
    
    /** M�todo que retorna o nome do IPSimulado (elemento da parte esquerda) do canal.
     * @return String O nome do IPSimulado do canal.
     * @param _enderecoSimulado O endere�o IP Simulado (IpReal:PortaUDP ou hostname:PortaUDP) do canal.
     * @throws InvalidArgumentException Exce��o gerada se receber argumentos inv�lidos de entrada.
     */
    public static String descobreNomeIPSimulado(String _enderecoSimulado)
    throws InvalidArgumentException
    {
        if ( (_enderecoSimulado == null) || (_enderecoSimulado.equals("")) )
        {
            throw new InvalidArgumentException("IPSimulada.descobreNomeIPSimulado(): Argumento Inv�lido: " 
                    + _enderecoSimulado);
        }
        else
        {
            StringTokenizer stringTokenizer = new StringTokenizer(_enderecoSimulado ,":");
            return (String)stringTokenizer.nextToken();
        }
        
        
    }
    
    /** M�todo que retorna a porta do IPSimulado (elemento da parte direita) do canal.
     * @return String A porta do IPSimulado do canal.
     * @param _enderecoSimulado O endere�o IP Simulado (IpReal:PortaUDP ou hostname:PortaUDP) do canal.
     * @throws InvalidArgumentException Exce��o gerada se receber argumentos inv�lidos de entrada.
     */
    public static String descobrePortaIPSimulado(String _enderecoSimulado) throws InvalidArgumentException {
        if ( (_enderecoSimulado == null) || (_enderecoSimulado.equals("")) ) {
            throw new InvalidArgumentException("IPSimulada.descobreNomeIPSimulado(): Argumento Inv�lido: " 
                + _enderecoSimulado);
        } else {
            StringTokenizer stringTokenizer = new StringTokenizer(_enderecoSimulado,":");
            stringTokenizer.nextToken();
            return ((String)stringTokenizer.nextToken());
        }
    }
    
    /** M�todo que o descobre o IP simulado de uma esta��o dado o enderecoSimulado.
     * @return String O IPSimulado da esta��o.
     * @param _nomeEstacao O nome da esta��o (hostname[dom�nio]:PortaUDP.
     * @throws InvalidArgumentException Exce��o gerada se receber argumentos inv�lidos de entrada.
     */
    public String descobreIPSimulado(String _nomeEstacao) throws InvalidArgumentException {
        try {
            String hostname = (InetAddress.getByName(descobreNomeIPSimulado(_nomeEstacao))).getHostAddress();
            int porta = Integer.parseInt(descobrePortaIPSimulado(_nomeEstacao));
            return (String) hostname + ":" + Integer.toString(porta);
        } catch (Exception ex) {
            throw new InvalidArgumentException("IPSimulada.descobreIPSimulado(): Argumento Inv�lido: " 
                + _nomeEstacao);
        }
    }
    
    /** M�todo que descobre o nome simulado de uma esta��o a partir do seu IP simulado
     * @return String O nome da esta��o (hostname[dom�nio]:PortaUDP.
     * @param _enderecoIp O endere�o IP Simulado (IpReal:PortaUDP ou hostname:PortaUDP) do canal.
     * @throws InvalidArgumentException Exce��o gerada se receber argumentos inv�lidos de entrada.
     */
    public static String descobreNomeEstacao(String _enderecoIp) throws InvalidArgumentException{
        try {
            int porta = Integer.parseInt(descobrePortaIPSimulado(_enderecoIp));
            return (InetAddress.getByName(descobreNomeIPSimulado(_enderecoIp))).getHostName() + ":" + Integer.toString(porta);
        } catch (Exception ex) {
            throw new InvalidArgumentException("IPSimulada.descobreNomeEstacao(): Argumento Inv�lido: " 
                + _enderecoIp);
        }
    }
    
    /** M�todo que finaliza o canal
     * @throws CanalInexistenteException gerada se ocorrer erro ao tentar
     *  transmitir atrav�s de canal que n�o existe (ou n�o inicializado).
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
