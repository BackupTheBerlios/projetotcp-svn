/*
 * @(#)BindException.java	1.0 31/04/2003
 *
 * Copyleft (L) 2004 Laborat�rio de Arquitetura e Redes de Computadores
 * Escola Polit�cnica da Universidade de S�o Paulo.
 *
 */

package br.usp.larc.tcp.excecoes;

/**
 * Classe para exemplificar uma cria�ao de exce��o pr�pria.
 * Voc� pode seguir esse modelo para criar suas pr�prias exce��es para o
 * projeto (por exemplo, exce��es para timeouts)
 * 
 * @author Laborat�rio de Arquitetura e Redes de Computadores.
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