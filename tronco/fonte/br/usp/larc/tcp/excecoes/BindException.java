/*
 * @(#)BindException.java	1.0 31/03/2003
 *
 * Copyleft (L) 2003 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

package br.usp.larc.tcp.excecoes;

/**
 * Exceção que representa erro de bind.
 * 
 * @author Laboratório de Arquitetura e Redes de Computadores.
 * @version 1.0 09 Maio 2003.
 */
public class BindException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>BindException</code> without detail message.
     */
    public BindException() {
    }
    
    
    /**
     * Constructs an instance of <code>BindException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BindException(String msg) {
        super(msg);
    }
}
