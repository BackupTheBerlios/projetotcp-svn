/*
 * @(#)CanalInexistenteException.java	1.0 31/03/2003
 *
 * Copyleft (L) 2003 Laborat�rio de Arquitetura e Redes de Computadores.
 * Escola Polit�cnica da Universidade de S�o Paulo.
 *
 */

package br.usp.larc.tcp.excecoes;

/**
 * Exce��o que representa uma refer�ncia nula ou inv�lida a um canal.
 * 
 * @author Laborat�rio de Arquitetura e Redes de Computadores.
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
