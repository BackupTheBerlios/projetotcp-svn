package br.usp.larc.tcp.protocolo;

/*
 * @(#)ConexaoTCP.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

/** 
 * Classe que representa uma conexão TCPSimulado. Conexões TCP são representadas
 * por uma quádrupla composta por um ID da Conexão, Endereço ipSimuladoLocal, 
 * Endereço IPSimuladoOrigem, Porta Local e Porta Remota. Aqui, incluímos o ID 
 * da conexão vai representar uma chave única de que identifica a conexao na 
 * tabela de conexões.
 * 
 * Note que todos os atributos dessa classe são "private" e só podem ser
 * acessados e modificados pelos métodos "get" e "set", respectivamente. Isso é
 * uma forma de encapsulamento.
 *
 * @author	Laboratório de Arquitetura e Redes de Computadores.
 * @version	1.0 Agosto 2003.
 */
public class ConexaoTCP {
    
    /**
     * Atributo que identifica a conexão de forma única.
     */
    private int idConexao;
    
    /**
     * Atributo que representa o IP Simulado (IP REAL Local+ porta UDP) Local.
     */
    private String ipSimuladoLocal;

    /**
     * Atributo que representa o IP Simulado (IP REAL Remoto + porta UDP) Remoto.
     */
    private String ipSimuladoRemoto;

    /**
     * Atributo que representa a Porta TCP Local.
     */
    private String portaLocal;
    
    /**
     * Atributo que representa a Porta TCP Remota.
     */
    private String portaRemota;
    
    /** Construtor default da classe ConexãoTCP */
    public ConexaoTCP() {
        this.idConexao = 0;
        this.ipSimuladoLocal = "";
        this.ipSimuladoRemoto = "";
        this.portaLocal = "";
        this.portaRemota = ""; 
    }
    
   /** Construtor com parâmetros da classe ConexãoTCP */
    public ConexaoTCP(int oIdConexao, String oIpSimuladoLocal, String aPortaLocal, String oIpSimuladoRemoto, String aPortaRemota) {
        this.idConexao = oIdConexao;
        this.ipSimuladoLocal = oIpSimuladoLocal;
        this.portaLocal = aPortaLocal;
        this.ipSimuladoRemoto = oIpSimuladoRemoto;
        this.portaRemota = aPortaRemota; 
    }
    
    /** 
     * Método acessador para o atributo idConexao.
     * @return int Valor do atributo idConexao.
     *
     */
    public int getIdConexao() {
        return this.idConexao;
    }
    
    /** 
     * Método modificador para o atributo idConexao.
     * @param aIdConexao Novo valor para o atributo idConexao.
     *
     */
    public void setIdConexao(int _idConexao) {
        this.idConexao = _idConexao;
    }
    
    /**
     * Método acessador para o atributo ipSimuladoLocal.
     * @return String Valor do atributo ipSimuladoLocal.
     *
     */
    public String getIpSimuladoLocal() {
        return this.ipSimuladoLocal;
    }
    
    /** Método modificador para o atributo ipSimuladoLocal.
     * @param oIpSimuladoLocal Novo valor para o atributo ipSimuladoLocal.
     *
     */
    public void setIpSimuladoLocal(String _IPSimuladoLocal) {
        this.ipSimuladoLocal = _IPSimuladoLocal;
    }
    
    /** Método acessador para o atributo ipSimuladoRemoto
     * @return String Valor do atributo ipSimuladoLocal.
     *
     */
    public String getIpSimuladoRemoto() {
        return this.ipSimuladoRemoto;
    }
    
    /** Método modificador para o atributo ipSimuladoRemoto.
     * @param oIpSimuladoRemoto Novo valor para o atributo ipSimuladoRemoto.
     *
     */
    public void setIpSimuladoRemoto(String _IPSimuladoRemoto) {
        this.ipSimuladoRemoto = _IPSimuladoRemoto;
    }
    
    /** Método acessador para o atributo portaLocal.
     * @return String Valor do atributo portaLocal.
     *
     */
    public String getPortaLocal() {
        return this.portaLocal;
    }
    
    /** Método modificador para o atributo portaLocal.
     * @param aPortaLocal Novo valor para o atributo portaLocal.
     *
     */
    public void setPortaLocal(String _portaLocal) {
        this.portaLocal = _portaLocal;
    }
    
    /** Método acessador para o atributo portaRemota.
     * @return String Valor do atributo portaRemota.
     *
     */
    public String getPortaRemota() {
        return this.portaRemota;
    }
    
    /** Método modificador para o atributo portaRemota.
     * @param aPortaRemota Novo valor para o atributo portaRemota.
     *
     */
    public void setPortaRemota(String _portaRemota) {
        this.portaRemota = _portaRemota;
    }
    
}//fim da classe ConexaoTCP