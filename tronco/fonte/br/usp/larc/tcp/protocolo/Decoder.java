package br.usp.larc.tcp.protocolo;

/**
 * @(#)Decoder.java 1.0 31/04/2004 Copyleft (L) 2004 Laboratório de Arquitetura
 *                  e Redes de Computadores Escola Politécnica da Universidade
 *                  de São Paulo.
 */

/**
 * Classe que auxilia na conversão de tipos. Ela será util na manipulação do
 * segmento TCP (fases de envio/montagem e recebimento/desmontagem dos
 * segmentos). Como todos os seus métodos são estáticos você não precisa criar
 * uma instância desse objeto para usá-lo. Um estudo sobre tipos primitivos de
 * Java pode ajudar numa melhor compreensão dessa classe.
 * 
 * @author Laboratório de Arquitetura e Redes de Computadores.
 * @version 1.0 Agosto 2003.
 */
public abstract class Decoder
{

    /**
     * Construtor da classe Decoder
     */
    /*
    public Decoder ()
    {
        // Construtora já veio vazia
    }*/

    /**
     * Método que tem funcionalidade de converter uma String com 16 caracteres
     * (ex.: XXXxxxXXXxxxN) que representa um IP Simulado para uma String no
     * formato byte ponto tradicional (ex.: xxx.xxx.xxx.xxx:XXXX).
     * 
     * Exemplos:
     * decoder.ipSimuladoToBytePonto("1501650750211024");
     * retorna 150.165.75.21:1024
     * 
     * decoder.ipSimuladoToBytePonto("1921680010010900");
     * retorna 192.168.001.001:0900
     * 
     * decoder.ipSimuladoToBytePonto("0100000000010021");
     * retorna 010.000.000.001:0021
     * 
     * decoder.ipSimuladoToBytePonto("0100000010021");
     * retorna null (tamanho do parâmetro diferente de 16)
     * 
     * @param _oIPSimulado
     *        O IP Simulado sem formato.
     * @return O IP Simulado em formato byte ponto. tradicional. Retorna null,
     *         se o tamanho do String for diferente de 16.
     */
    public static String ipSimuladoToBytePonto (String _oIPSimulado)
    {
        if (_oIPSimulado.length () < 13)
            return "";

        String retornoIPSimulado = new String ();
        int cursor = 0;

        for (int i = 0; i < 4; i++)
        {
            retornoIPSimulado += Integer.parseInt (_oIPSimulado.substring (cursor,
                    cursor + 3));
            cursor = cursor + 3;
            if (i != 3)
                retornoIPSimulado += ".";
        }

        retornoIPSimulado += ":" + Integer.parseInt (_oIPSimulado.substring (cursor));

        return retornoIPSimulado;
    }

    /**
     * Método que tem funcionalidade de converter uma String no formato byte
     * ponto tradicional (ex.: xxx.xxx.xxx.xxx:xxxx) para uma String com 16
     * caracteres (ex.: xxxxxxxxxxxxxxxx).
     * 
     * @param _bytePonto
     *        String O IP Simulado em formato byte ponto tradicional.
     * @return O IP Simulado sem formato.
     */
    public static String bytePontoToIpSimulado(String _bytePonto) {
		String ip    = new String();
		String temp  = new String();
		int cursor   = 0;

		for (int i = 0; i < 3; i++) 
        {
			temp = _bytePonto.substring(cursor, _bytePonto.indexOf('.', cursor));

            while (temp.length() < 3)
				temp = "0" + temp;
           
			cursor = _bytePonto.indexOf('.', cursor) + 1;
			ip += temp;
        }
        temp = _bytePonto.substring (cursor, _bytePonto.indexOf (':', cursor));

        while (temp.length() < 3)
            temp = "0" + temp;
       
		cursor = _bytePonto.indexOf(':', cursor) + 1;
		ip = ip + temp;
		temp = _bytePonto.substring(cursor);
        
        while (temp.length() < 5)
            temp = "0" + temp;
       
		ip = ip + temp;

		return ip;
	}
    /**
     * Método que tem funcionalidade de converter uma string num byte (8 bits).
     * 
     * @param _palavra
     *        A String que será convertida.
     * @return byte O byte (8 bits) convertido.
     */
    public static byte stringToByte (String _palavra)
    {
        return Byte.parseByte (_palavra);
    }

    /**
     * Método que tem funcionalidade de converter um byte(8 bits) para String.
     * 
     * @param _obyte
     *        O byte que será convertido.
     * @return String A String convertida.
     */
    public static String byteToString (byte _obyte)
    {
        return Byte.toString (_obyte);
    }

    /**
     * Método que converte um int (32 bits/4 bytes) em uma String de tamanho 2
     * (dois bytes). Usamos esse artifício para o campo porta do IPSimulado.
     * 
     * @param _numero
     *        O inteiro que será convertido.
     * @return A String de tamanho 2 convertida. Caso o parâmetro número seja
     *         menor que zero, retorna null.
     */
    public static String intToString (int _numero)
    {
        String retorno = null;

        if (_numero >= 0)
        {
            //separa as duas partes do int.
            Short lsb = new Short ((short) (_numero % 256));
            Short msb = new Short ((short) (_numero / 256));

            //cria dois caracteres (1 bytes cada)
            Character c1 = new Character ((char) lsb.shortValue ());
            Character c2 = new Character ((char) msb.shortValue ());

            //concatena os caracteres na String de retorno
            retorno = c1.toString () + c2.toString ();
        }
        return retorno;
    }

    /**
     * Método que converte uma String de dois caracteres (2 bytes) em um int.
     * 
     * @param _texto
     *        A String que será convertida.
     * @return O valor em int. Caso o tamanho da String seja diferente de 2
     *         (dois), retorna um int = 0.
     */
    public static int stringToInt (String _texto)
    {
        if (_texto.length () == 2)
        {
            //faz o processo inverso do intToString
            int i = (new Integer (_texto.charAt (0))).intValue ()
                    + ( (new Integer (_texto.charAt (1))).intValue () * 256);
            return i;
        }
        return 0;
    }

    /**
     * Método que coverte um short menor que 256 (usado para os campos de do
     * endereço IP) em um Caracter de 8 bits (1 byte) e retorna o valor em
     * String.
     * 
     * @param _numero
     *        Número que se quer converter.
     * @return String com o valor do número convertido. Caso o valor do número
     *         passado como parâmetro seja maior que 256, retorna null.
     */
    public static String shortToString (short _numero)
    {
        Character c = null;
        String retorno = null;
        if (_numero >= 0)
        {
            if (_numero < 256)
            {
                c = new Character ((char) _numero);
                retorno = c.toString ();
            }
        }
        return retorno;
    }

    /**
     * Método que coverte uma String que tem o valor de um caracter de 8 bits
     * para um short (usado para os campos de do endereço IP) .
     * 
     * @param _texto
     *        A String com um caracter de 8 bits
     * @return O short representando o valor de um caracter de 8 bits. Caso a
     *         String passada com parâmetro tenha tamanho diferente de 1 (um)
     *         retorna 0 (zero).
     */
    public static short stringToShort (String _texto)
    {
        if (_texto.length () == 1)
        {
            short i = ((short) _texto.charAt (0));
            return i;
        }
        return 0;
    }

    /**
     * Método que ilustra funcionamento da classe Decoder.
     * 
     * @param args
     */
    public static void main (String args[])
    {

        for (int n = 0; n < 65536; n++)
        {
            System.out.println (n + " " + Decoder.stringToInt (Decoder.intToString (n)));
        }
        for (long m = (long) Math.pow (2, 31); m < (long) Math.pow (2, 32); m++)
        {

            Integer lsw = new Integer ((int) (m % 65536));
            Integer msw = new Integer ((int) (m / 65536));
            String str = Decoder.intToString (lsw.intValue ()) + ""
                         + Decoder.intToString (msw.intValue ());
            long ms = Decoder.stringToInt (str.substring (0, 2))
                      + (Decoder.stringToInt (str.substring (2, 4)) * 65536);

            System.out.println (m + " " + ms);
        }
    }

}//fim da classe Decoder
