/*
 * @(#)IpSimuladaIF.java	1.0 31/03/2003
 *
 * Copyleft (L) 2003 Laborat�rio de Arquitetura e Redes de Computadores
 * Escola Polit�cnica da Universidade de S�o Paulo.
 *
 */

package br.usp.larc.tcp.ipsimulada;

import br.usp.larc.tcp.excecoes.*;
//import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;

/**
 * Interface que fornece servi�os (m�todos) de acesso a uma camada IP Simulada.
 * 
 * @author Laborat�rio de Arquitetura e Redes de Computadores.
 * @version 1.0 09 Maio 2003.
 */

public interface IpSimuladaIF {
       
    /** M�todo que inicializa um canal na camada IPSimulada.
     * @param _tamanhoBuffer O tamanho do buffer usado pelo canal.
     * @throws BindException Exce��o gerada quando ocorre algum erro na inicializa��o do canal.
     * @throws BufferOverflowException Exce��o gerada se estourar valor m�ximo permitido para o buffer.
     */
    public void inicializaCanal(int tamanhoBuffer) throws BufferOverflowException, br.usp.larc.tcp.excecoes.BindException;
    
    /** M�todo que transmite dados atrav�s de um canal inicializado.
     * @param _ipMaquinaDestino O IP real (sem os :portaTCP) da m�quina destino para qual ser�o transmitidos os dados.
     * @param _bufferSaida O conte�do dos dados que ser�o enviados.
     * @param _tamanhoBuffer O tamanho do buffer que ser� usada para a transmiss�o.
     * @param _porta A porta (UDP) da m�quina destino para qual ser�o entregues os dados.
     * @throws CanalInexistenteException Exce��o gerada se ocorrer erro ao tentar.
     *  transmitir atrav�s de canal que n�o existe (ou n�o inicializado) ou se acontecer.
     *  algum erro na interface de I/O .
     */
    public void transmite(String ipMaquinaDestino, String bufferSaida, int tamanhoBuffer, int porta) throws IOException, CanalInexistenteException, InvalidArgumentException;
    
    /** M�todo que transmite dados atrav�s de um canal inicializado.
     * @param _nomeMaquinaDestino Nome (hostname:porta) da m�quina destino.
     * @param _bufferSaida O conte�do dos dados que ser�o enviados.
     * @param _tamanhoBuffer O tamanho do buffer que ser� usada para a transmiss�o.
     * @throws InvalidArgumentException Exce��o gerada se receber argumentos inv�lidos de entrada.
     * @throws CanalInexistenteException Exce��o gerada se ocorrer erro ao tentar.
     *  transmitir atrav�s de canal que n�o existe (ou n�o inicializado) ou se acontecer.
     *  algum erro na interface de I/O .
     */
    public void transmite(String nomeCanalMaquinaDestino, String bufferSaida, int tamanhoBuffer) throws IOException, CanalInexistenteException, InvalidArgumentException;
    
    /** M�todo que recebe dados atrav�s de um canal inicializado.
     * @return String Os dados recebidos no buffer de entrada do canal.
     * @param _tamanhoBuffer O tamanho do buffer que ser� usado para a recep��o.
     * @throws TimeOutException Exce��o gerada se o timeout do buffer de entrada expira.
     * @throws CanalInexistenteException Exce�ao gerada se algum erro na interface de I/O acontece.
     */
    public String recebe(int tamanhoBuffer) throws IOException, TimeOutException, CanalInexistenteException;
    
    /** M�todo que retorna nome da esta��o local onde o canal foi inicializado.
     * @return String O nome da esta��o local (hostname).
     * @throws UnknownHostException Exece��o gerada se n�o conseguir resolver o nome da esta��o local
     */
    public String descobreNomeEstacaoLocal() throws UnknownHostException;
    
    /** M�todo que retorna nome da esta��o local com o seu dom�nio de rede onde o canal foi inicializado.
     * @return String O nome da esta��o (hostname + dom�nio) local.
     * @throws UnknownHostException Exece��o gerada se n�o conseguir resolver o nome da esta��o/dom�nio da esta��o.
     */
    public String descobreNomeDominioEstacaoLocal() throws UnknownHostException;
    
    /** M�todo que retorna o nome do canal.
     * @return String O nome do canal (hostname:PortaUDP).
     * @throws CanalInexistenteException Exce��o gerada se ocorrer erro ao tentar
     *  transmitir atrav�s de canal que n�o existe (ou n�o inicializado).
     */
    public String descobreCanalNomeSimulado() throws CanalInexistenteException;
    
    /** M�todo que retorna o IPSimulado (IpReal:PortaUDP) do canal.
     * @return String O IPSimulado (IpReal:PortaUDP) do canal.
     * @throws CanalInexistenteException Exce��o gerada se ocorrer erro ao tentar
     *  transmitir atrav�s de canal que n�o existe (ou n�o inicializado).
     */
    public String descobreCanalIPSimulado() throws CanalInexistenteException;
    
    /** M�todo que o descobre o IP simulado de uma esta��o dado o enderecoSimulado.
     * @return String O IPSimulado da esta��o.
     * @param _nomeEstacao O nome da esta��o (hostname[dom�nio]:PortaUDP.
     * @throws InvalidArgumentException Exce��o gerada se receber argumentos inv�lidos de entrada.
     */
    public String descobreIPSimulado(String nomeEstacao)throws InvalidArgumentException;
    
    /** M�todo que finaliza o canal
     * @throws CanalInexistenteException gerada se ocorrer erro ao tentar
     *  transmitir atrav�s de canal que n�o existe (ou n�o inicializado).
     */
    public void finalizaCanal() throws CanalInexistenteException;
}