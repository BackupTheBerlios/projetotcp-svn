/*
 * @(#)IpSimuladaIF.java	1.0 31/03/2003
 *
 * Copyleft (L) 2003 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

package br.usp.larc.tcp.ipsimulada;

import br.usp.larc.tcp.excecoes.*;
//import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;

/**
 * Interface que fornece serviços (métodos) de acesso a uma camada IP Simulada.
 * 
 * @author Laboratório de Arquitetura e Redes de Computadores.
 * @version 1.0 09 Maio 2003.
 */

public interface IpSimuladaIF {
       
    /** Método que inicializa um canal na camada IPSimulada.
     * @param _tamanhoBuffer O tamanho do buffer usado pelo canal.
     * @throws BindException Exceção gerada quando ocorre algum erro na inicialização do canal.
     * @throws BufferOverflowException Exceção gerada se estourar valor máximo permitido para o buffer.
     */
    public void inicializaCanal(int tamanhoBuffer) throws BufferOverflowException, br.usp.larc.tcp.excecoes.BindException;
    
    /** Método que transmite dados através de um canal inicializado.
     * @param _ipMaquinaDestino O IP real (sem os :portaTCP) da máquina destino para qual serão transmitidos os dados.
     * @param _bufferSaida O conteúdo dos dados que serão enviados.
     * @param _tamanhoBuffer O tamanho do buffer que será usada para a transmissão.
     * @param _porta A porta (UDP) da máquina destino para qual serão entregues os dados.
     * @throws CanalInexistenteException Exceção gerada se ocorrer erro ao tentar.
     *  transmitir através de canal que não existe (ou não inicializado) ou se acontecer.
     *  algum erro na interface de I/O .
     */
    public void transmite(String ipMaquinaDestino, String bufferSaida, int tamanhoBuffer, int porta) throws IOException, CanalInexistenteException, InvalidArgumentException;
    
    /** Método que transmite dados através de um canal inicializado.
     * @param _nomeMaquinaDestino Nome (hostname:porta) da máquina destino.
     * @param _bufferSaida O conteúdo dos dados que serão enviados.
     * @param _tamanhoBuffer O tamanho do buffer que será usada para a transmissão.
     * @throws InvalidArgumentException Exceção gerada se receber argumentos inválidos de entrada.
     * @throws CanalInexistenteException Exceção gerada se ocorrer erro ao tentar.
     *  transmitir através de canal que não existe (ou não inicializado) ou se acontecer.
     *  algum erro na interface de I/O .
     */
    public void transmite(String nomeCanalMaquinaDestino, String bufferSaida, int tamanhoBuffer) throws IOException, CanalInexistenteException, InvalidArgumentException;
    
    /** Método que recebe dados através de um canal inicializado.
     * @return String Os dados recebidos no buffer de entrada do canal.
     * @param _tamanhoBuffer O tamanho do buffer que será usado para a recepção.
     * @throws TimeOutException Exceção gerada se o timeout do buffer de entrada expira.
     * @throws CanalInexistenteException Exceçao gerada se algum erro na interface de I/O acontece.
     */
    public String recebe(int tamanhoBuffer) throws IOException, TimeOutException, CanalInexistenteException;
    
    /** Método que retorna nome da estação local onde o canal foi inicializado.
     * @return String O nome da estação local (hostname).
     * @throws UnknownHostException Execeção gerada se não conseguir resolver o nome da estação local
     */
    public String descobreNomeEstacaoLocal() throws UnknownHostException;
    
    /** Método que retorna nome da estação local com o seu domínio de rede onde o canal foi inicializado.
     * @return String O nome da estação (hostname + domínio) local.
     * @throws UnknownHostException Execeção gerada se não conseguir resolver o nome da estação/domínio da estação.
     */
    public String descobreNomeDominioEstacaoLocal() throws UnknownHostException;
    
    /** Método que retorna o nome do canal.
     * @return String O nome do canal (hostname:PortaUDP).
     * @throws CanalInexistenteException Exceção gerada se ocorrer erro ao tentar
     *  transmitir através de canal que não existe (ou não inicializado).
     */
    public String descobreCanalNomeSimulado() throws CanalInexistenteException;
    
    /** Método que retorna o IPSimulado (IpReal:PortaUDP) do canal.
     * @return String O IPSimulado (IpReal:PortaUDP) do canal.
     * @throws CanalInexistenteException Exceção gerada se ocorrer erro ao tentar
     *  transmitir através de canal que não existe (ou não inicializado).
     */
    public String descobreCanalIPSimulado() throws CanalInexistenteException;
    
    /** Método que o descobre o IP simulado de uma estação dado o enderecoSimulado.
     * @return String O IPSimulado da estação.
     * @param _nomeEstacao O nome da estação (hostname[domínio]:PortaUDP.
     * @throws InvalidArgumentException Exceção gerada se receber argumentos inválidos de entrada.
     */
    public String descobreIPSimulado(String nomeEstacao)throws InvalidArgumentException;
    
    /** Método que finaliza o canal
     * @throws CanalInexistenteException gerada se ocorrer erro ao tentar
     *  transmitir através de canal que não existe (ou não inicializado).
     */
    public void finalizaCanal() throws CanalInexistenteException;
}