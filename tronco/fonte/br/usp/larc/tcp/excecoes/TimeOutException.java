/*
 * @(#)TimeOutException.java	1.0 31/03/2003
 *
 * Copyleft (L) 2003 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

package br.usp.larc.tcp.excecoes;

/**
 * Exceção que representa expiração de um algum timeout.
 * 
 * @author Laboratório de Arquitetura e Redes de Computadores.
 * @version 1.0 09 Maio 2003.
 */
public class TimeOutException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>TimeOutException</code> without detail message.
     */
    public TimeOutException() {
    }
    
    
    /**
     * Constructs an instance of <code>TimeOutException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TimeOutException(String msg) {
        super(msg);
    }
}
