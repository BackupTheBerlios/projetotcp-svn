/*
 * @(#)BindException.java	1.0 31/04/2003
 *
 * Copyleft (L) 2004 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

package br.usp.larc.tcp.excecoes;

/**
 * Classe para exemplificar uma criaçao de exceção própria.
 * Você pode seguir esse modelo para criar suas próprias exceções para o
 * projeto (por exemplo, exceções para timeouts)
 * 
 * @author Laboratório de Arquitetura e Redes de Computadores.
 * @version 1.0 01 Junho 2004.
 */
public class PrimitivaInvalidaException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>PrimitivaInvalidaException</code> without detail message.
     */
    public PrimitivaInvalidaException()
    {
        // XXX: Construtora da PrimitivaInvalidaException
    }
    
    
    /**
     * Constructs an instance of <code>ExemploException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PrimitivaInvalidaException(String msg) {
        super(msg);
    }
}