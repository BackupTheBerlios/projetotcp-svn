package br.usp.larc.tcp.protocolo;

/*
 * @(#)MaquinasDeEstados.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

//import java.text.DecimalFormat;
//import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Iterator;

/** 
 * Classe que representa as Máquinas de Conexões que receberão pacotes do objeto
 * Monitor.
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

public class MaquinasDeEstados extends Hashtable {
    
    /** Construtor da classe MaquinasDeEstados */
    public MaquinasDeEstados() {
        super();
    }
    
    /**
     * Método que adiciona na tabela uma conexão. No caso de adição de conexões
     * com o mesmo ID, o método sobrepõe os objetos e permanece na tabela a
     * última conexão inserida.
     *
     * @param _idConexao O id da Conexao que será chave do o objeto máquina
     * @param _maquinaDeEstados O objeto máquina que será inserido
     */    
    public void put(String _idConexao, MaquinaDeEstados _maquinaDeEstados) {
        super.put(_idConexao, _maquinaDeEstados);
    }
    
    /**
     * Método que retorna uma máquina da tabela com a Porta TCP (local) passada, 
     * caso não encontre a porta na tabela, retorna null.
     *
     * @param _idConexao O idConexao da máquina que você quer obter,  nulo se
     * não existir a máquina com o id passado
     * @return MaquinaDeEstados A máquina desejada
     */    
    public MaquinaDeEstados get(int _idConexao) {
        return (MaquinaDeEstados) super.get(Integer.toString(_idConexao));
    }
    
    /**
     * Método que remove uma máquina da tabela com o id da conexão passado, caso
     * não encontre o id na tabela, gera exceção.     
     * 
     * @param _idConexao O id da conexão da máquina que se quer remover da tabela
     * @throws NullPointerException Caso não encontre o objeto com o id passado
     */    
    public void remove(int _idConexao) throws NullPointerException {
        if (super.remove((Integer.toString(_idConexao))) == null) {
                throw new NullPointerException("Elemento não encontrado");
        }
    }
            
    /**
     * Método que retorna o tamanho atual da tabela de máquinas baseado no 
     * número de objetos inseridos.
     * 
     * @return int O tamanho da tabela de máquinas
     */    
    public int size() {
        return super.size();
    }
    
    /**
     * Método que retorna um iterador com todos os objetos MaquinaDeEstados
     * da HashTable
     *
     * Exemplo:
     *  
     * //cria um iterador para percorrer um objeto do tipo TabelaDeConexoes
     * Iterator  iteratorTabela = (Iterator) tabelaDeMaquinas.maquinas();
     * MaquinaDeEstados me = new MaquinaDeEstados();
     *
     * 
     * //percorre todo o iterador
     * while (iteratorTabela.hasNext()) {
     *      //imprime todos os ID's das conexões que estão na tabela
     *      System.out.println((
                (MaquinaDeEstados)iteratorTabela.next()).getIdConexao());
     *  }
     *   
     * @return Iterator O iterador com os objetos Máquinas da tabela
     */    
    public Iterator maquinas() {
        return (super.values()).iterator();
    }
    
    /**
     * Método que retorna um iterador com as chaves dos objetos MaquinaDeEstados
     * da HashTable
     *
     *   
     * @return Iterator O iterador com as chaves dos objetos da Hashtable
     */    
    public Iterator maquinasKeySet() {
        return (super.keySet()).iterator();
    }

}//fim da classe MaquinasDeEstados