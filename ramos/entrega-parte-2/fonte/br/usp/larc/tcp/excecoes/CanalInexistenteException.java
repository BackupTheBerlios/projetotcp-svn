/*
 * @(#)CanalInexistenteException.java	1.0 31/03/2003
 *
 * Copyleft (L) 2003 Laboratório de Arquitetura e Redes de Computadores.
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

package br.usp.larc.tcp.excecoes;

/**
 * Exceção que representa uma referência nula ou inválida a um canal.
 * 
 * @author Laboratório de Arquitetura e Redes de Computadores.
 * @version 1.0 09 Maio 2003.
 */
public class CanalInexistenteException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>CanalInexistenteException</code> without detail message.
     */
    public CanalInexistenteException() {
    }
    
    
    /**
     * Constructs an instance of <code>CanalInexistenteException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CanalInexistenteException(String msg) {
        super(msg);
    }
}
