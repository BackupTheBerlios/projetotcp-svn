package br.usp.larc.tcp.protocolo;

/*
 * @(#)TabelaDeConexoes.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo
 *
 */

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Iterator;

/** 
 * Classe que representa a Tabela de Conexão que vai ajudar o objeto Monitor no 
 * chaveamento de pacotes para as máquinas de estados correspondentes.
 * 
 * Note que essa classe estende a classe java.util.Hashtable (ela herda todos
 * os atributos e métodos) e sobrecarrega/sobrepõe alguns métodos da mesma. 
 * Estamos usando o conceito de Polimorfismo.
 *
 * Detalhes e dicas de implementação dessa classe podem ser consultadas na API
 * da classe java.util.Hashtable.
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a 
 * criatividade na implementação do seu projeto.
 *  
 *
 * @author	Laboratório de Arquitetura e Redes de Computadores
 * @version	1.0 Agosto 2003
 */
public class TabelaDeConexoes extends Hashtable
{
    
    /** Construtor da classe TabelaDeConexoes */
    public TabelaDeConexoes() {
        super();
    }
    
    /**
     * Método que adiciona na tabela uma conexão. No caso de adição de conexões
     * com o mesmo ID, o método sobrepõe os objetos e permanece na tabela a
     * última conexão inserida.
     *
     * @param aConexaoTCP O objeto conexão que será inserido
     */    
    public void put(ConexaoTCP _conexaoTCP) {
        super.put(Integer.toString(_conexaoTCP.getIdConexao()),  _conexaoTCP);
    }
    
    /**
     * Método que retorna uma conexão da tabela com o ID (de conexão) passado, 
     * caso não encontre o ID na tabela, retorna null.
     *
     * @param idConexao O ID da conexão que você quer obter, nulo se não existir
     * a conexão com o ID passado
     * @return ConexaoTCP A conexão desejada
     */    
    public ConexaoTCP get(int _idConexao) {
        return (ConexaoTCP) super.get(Integer.toString(_idConexao));
    }
    
    /**
     * Método que remove uma conexão da tabela com o ID (de conexão) passado, 
     * caso não encontre o ID na tabela, gera exceção.     
     * 
     * @param idConexao o ID da conexão que se quer remover da tabela
     * @throws NullPointerException Caso não encontre o objeto com o ID passado
     */    
    public void remove(int _idConexao) throws NullPointerException {
        if (super.remove((Integer.toString(_idConexao))) == null) {
                throw new NullPointerException("Elemento não encontrado");
        }
    }
            
    /**
     * Método que retorna o tamanho atual da tabela de conexões baseado no 
     * número de conexões inseridas.
     * 
     * @return int O tamanho da tabela de conexões
     */    
    public int size() {
        return super.size();
    }
    
    /**
     *  Método que retorna um iterador com todos os objetos Conexão da tabela
     *
     *  Exemplo:
     *  
     *  //cria um iterador para percorrer um objeto do tipo TabelaDeConexoes
     *  Iterator  iteratorTabela = (Iterator) tabelaDeConexoes.conexoes();
     *  ConexaoTCP conexao = new Conexao();
     *
     * 
     *  //percorre todo o iterador
     *  while (iteratorTabela.hasNext()) {
     *      //imprime todos ID's das conexões que estão na tabela
     *      System.out.println(((ConexaoTCP)iteratorTabela.next()).getIdConexao());
     *  }
     *   
     * @return Iterator O iterador com os objetos Conexão da tabela
     */    
    public Iterator conexoes() {
        return (Iterator) (super.values()).iterator();
    }
    
    /**
     * Método que retorna um iterador com as chaves dos objetos ConexaoTCP
     * da HashTable
     *
     *   
     * @return Iterator O iterador com as chaves dos objetos da Hashtable
     */    
    public Iterator conexoesKeySet() {
        return (Iterator) (super.keySet()).iterator();
    }
    
    /**
     * Método para visualizar a tabela de conexões
     * 
     * Imprime linha por linha o conteúdo da tabela de conexão com a seguinte
     * sintaxe:
     * 
     * ID: NumId [ IPSimuladoLocal : PortaTCP] [ IPSimuladoRemoto : PortaTCP]
     * Um exemplo:
     * ID: 1 [ 143.107.111.087:4593 : 1024 ] [ 143.107.111.087:4593 : 1025 ]
     * ID: 2 [ 143.107.111.087:4593 : 1026 ] [ 143.107.111.087:4593 : 1027 ]
     *
     * @return String O String com cada da tabela de conexão
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        MessageFormat mf = new MessageFormat("ID: {0} [ {1} : {2} ] [ {3} : {4} ]\n");
        DecimalFormat df = new DecimalFormat("0");
        
        Iterator i = this.conexoes();
        
        while (i.hasNext())
        {
            ConexaoTCP conexao = (ConexaoTCP) i.next();
            Object[] argumentos = {df.format(conexao.getIdConexao()),
                    Decoder.ipSimuladoToBytePonto(conexao.getIpSimuladoLocal()),
                    conexao.getPortaLocal(),
                    Decoder.ipSimuladoToBytePonto(conexao.getIpSimuladoRemoto()),
                    conexao.getPortaRemota()};
            buffer.append(mf.format(argumentos));
        }
        return buffer.toString();
    }
    
    /**
     * 
     * 
     * @author acdcp
     *
     * TODO To change the template for this generated type comment go to
     * Window - Preferences - Java - Code Style - Code Templates
     */
    public void alteraDestino (int id, String ips, String porta)
    {
        Iterator  tabela = (Iterator) this.conexoes();
        ConexaoTCP conexao = new ConexaoTCP();

        //percorre todo o iterador
        while (tabela.hasNext())
        {
            //imprime todos ID's das conexões que estão na tabela
           conexao = (ConexaoTCP) tabela.next();
           if (conexao.getIdConexao() == id)
           {
               conexao.setIpSimuladoRemoto(ips);
               conexao.setPortaRemota(porta);
               return;
           }
        }
    }
    
}//fim da classe TabelaDeConexões