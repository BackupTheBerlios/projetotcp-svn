package br.usp.larc.tcp.protocolo;

/*
 * @(#)PacoteTCP.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

import java.util.StringTokenizer;

/**  
 * Esta classe é responsável por montar o pacote TCP com as informações 
 * necessárias para a implementação do TCP Simulado. Note que seguimos a
 * especificação da Apostila II da experiência.
 *
 * Para entender mais detalhes sobre o processo de conversão de campos para se 
 * ajustar a especificação do nosso segmento TCP adaptado, consulte as classes
 * CampoTCP e Decoder.
 *
 * @author Laboratório de Arquitetura e Redes de Computadores.
 * @version	1.0 Agosto 2003.
 */
public class PacoteTCP {

    /** Atributo que representa o campo de ipSimulado remoto do header do pacote.
     */        
    private String ipSimuladoRemoto  = "";    

    /** Atributo que representa o campo de ipSimulado local do header do pacote.
     */        
    private String ipSimuladoLocal   = "";

    /** Atributo que representa o campo de porta remota do header do pacote.
     */        
    private CampoTCP portaRemota;

    /** Atributo que representa o campo de porta local do header do pacote.
     */        
    private CampoTCP portaLocal;

    /** Atributo que representa o campo de campo de sequencia do header do pacote.
     */        
    private CampoTCP numSequencia;

    /** Atributo que representa o campo de campo de Ack do header do pacote.
     */        
    private CampoTCP numAck;

    /** Atributo que representa o campo de offset do header do pacote.
     */        
    private CampoTCP offset;

    /** Atributo que representa o campo de controle do header do pacote.
     */        
    private CampoTCP controle;

    /** Atributo que representa o campo de checksum do header do pacote.
    */    
    private CampoTCP checksum;

    /** Atributo que representa o campo de janela do header do pacote.
     */        
    private CampoTCP janela;

    /** Atributo que representa o campo de ponteiro urgente do header do pacote.
     */        
    private CampoTCP ponteiroUrgente;

    /** Atributo que representa o campo de opcoes do header do pacote.
     */        
    private CampoTCP opcoes;

    /** Atributo que representa o campo de dados do pacote.
     */        
    private String dados    =   "";

    /** 
     * Construtor do pacote.
     */
    public PacoteTCP() {
        this.portaRemota        = new CampoTCP(2);
        this.portaLocal         = new CampoTCP(2);
        this.numSequencia       = new CampoTCP(4);
        this.numAck             = new CampoTCP(4);
        this.offset             = new CampoTCP(1);
        this.controle           = new CampoTCP(1);
        this.checksum           = new CampoTCP(2);
        this.janela             = new CampoTCP(2);
        this.ponteiroUrgente    = new CampoTCP(2);
        this.opcoes             = new CampoTCP(4);
    }

    /** Construtor do pacote.
     *
     * @param _ipSimuladoLocal Valor do atributo ipSimulado local.
     * @param _ipSimuladoRemoto Valor do atributo ipSimulado remoto.
     * @param _portaLocal Valor do atributo porta local.
     * @param _portaRemota Valor do atributo porta remota.
     * @param _numSequencia Valor do atributo campo numSequencia.
     * @param _numAck Valor do atributo campo numAck.
     * @param _offset Valor do atributo offset.
     * @param _controle Valor do atributo controle.
     * @param _janela Valor do atributo janela.
     * @param _checksum Valor do atributo checksum.
     * @param _ponteiroUrgente Valor do atributo ponteiroUrgente.
     * @param _opcoes Valor do atributo opcoes.
     * @param _dados Valor do atributo Parameter.
     */
    public PacoteTCP(String _ipSimuladoLocal, String _ipSimuladoRemoto, 
        CampoTCP _portaLocal, CampoTCP _portaRemota, CampoTCP _numSequencia, 
        CampoTCP _numAck, CampoTCP _offset, CampoTCP _controle, 
        CampoTCP _janela, CampoTCP _checksum, CampoTCP _ponteiroUrgente, 
        CampoTCP _opcoes, String _dados) {
        
        this.ipSimuladoRemoto   = _ipSimuladoRemoto;
        this.ipSimuladoLocal    = _ipSimuladoLocal;
        this.portaRemota        = _portaRemota;
        this.portaLocal         = _portaLocal;
        this.numSequencia       = _numSequencia;
        this.numAck             = _numAck;
        this.offset             = _offset;
        this.controle           = _controle;
        this.janela             = _janela;
        this.checksum           = _checksum;
        this.ponteiroUrgente    = _ponteiroUrgente;
        this.opcoes             = _opcoes;
        this.dados              = _dados;
    }

    /** Contrutor do pacote.
     *
     * @param texto Texto codificado em bytes, após ser decodificado
     * altera os atributos do pacote.
     */
    public PacoteTCP(String _texto) {
        if (_texto.length() >= 36) {

            this.ipSimuladoRemoto   = new String(
                this.decodificarIp( _texto.substring(0, 6)));
            this.ipSimuladoLocal    = new String(
                this.decodificarIp( _texto.substring(6, 12)));
            this.portaRemota        = new CampoTCP(2, _texto.substring(12, 14));
            this.portaLocal         = new CampoTCP(2, _texto.substring(14, 16));
            this.numSequencia       = new CampoTCP(4, _texto.substring(16, 20));
            this.numAck             = new CampoTCP(4, _texto.substring(20, 24));
            this.offset             = new CampoTCP(1, _texto.substring(24, 25));
            this.controle           = new CampoTCP(1, _texto.substring(25, 26));
            this.janela             = new CampoTCP(2, _texto.substring(26, 28));
            this.checksum           = new CampoTCP(2, _texto.substring(28, 30));
            this.ponteiroUrgente    = new CampoTCP(2, _texto.substring(30, 32));
            this.opcoes             = new CampoTCP(4, _texto.substring(32, 36));
            this.dados              = new String(_texto.substring(36));
            
        }
    }

    /**
     * Método acessador para o atibuto checksum.
     *
     * @return    O valor para o atributo checksum.
     */
    public int getChecksum() {
        return (int) this.checksum.getValue();
    }

    /**
     * Método acessador para o atibuto controle.
     *
     * @return    O valor para o atributo controle.
     */
    public short getControle() {
        return (short) this.controle.getValue();
    }

    /**
     * Método acessador para o atibuto dados.
     *
     * @return    O valor para o atributo dados.
     */
    public String getDados() {
        return this.dados;
    }

    /**
     * Método acessador para o atibuto ipSimuladoLocal.
     *
     * @return    O valor para o atributo ipSimuladoLocal.
     */
    public String getIpSimuladoLocal() {
        return this.ipSimuladoLocal;
    }

    /**
     * Método acessador para o atibuto ipSimuladoRemoto.
     *
     * @return    O valor para o atributo ipSimuladoRemoto.
     */
    public String getIpSimuladoRemoto() {
        return this.ipSimuladoRemoto;
    }

    /**
     * Método acessador para o atibuto janela.
     *
     * @return    O valor para o atributo janela.
     */
    public int getJanela() {
        return (int) this.janela.getValue();
    }

    /**
     * Método acessador para o atibuto numAck.
     *
     * @return    O valor para o atributo numAck.
     */
    public long getNumAck() {
        return this.numAck.getValue();
    }

    /**
     * Método acessador para o atibuto numSequencia.
     *
     * @return    O valor para o atributo numSequencia.
     */
    public long getNumSequencia() {
        return this.numSequencia.getValue();
    }

    /**
     * Método acessador para o atibuto offset.
     *
     * @return    O valor para o atributo offset.
     */
    public short getOffsetValue() {
        return (short) this.offset.getValue();
    }

    /**
     * Método acessador para o atibuto opcoes.
     *
     * @return    O valor para o atributo opcoes.
     */
    public long getOpcoes() {
        return this.opcoes.getValue();
    }

    /**
     * Método acessador para o atibuto ponteiroUrgente.
     *
     * @return    O valor para o atributo ponteiroUrgente.
     */
    public int getPonteiroUrgente() {
        return (int) this.ponteiroUrgente.getValue();
    }

    /**
     * Método acessador para o atibuto portaLocal.
     *
     * @return    O valor para o atributo portaLocal.
     */
    public int getPortaLocal() {
        return (int) this.portaLocal.getValue();
    }


    /**
     * Método acessador para o atibuto portaRemota.
     *
     * @return    O valor para o atributo portaRemota.
     */
    public int getPortaRemota() {
        return (int) this.portaRemota.getValue();
    }

    /**
     * Método acessador para o atibuto tamanho
     *
     * @return    O tamanho do Pacote TCP
     */
    public long getTamanho() {
//        return (this.opcoes.getValue() - 2 * 16777216 - 4 * 65536 - 36);
        return ((this.opcoes.getValue() & 0x0000FFFF) - 36);
    }

    /**
     * Método modificador do atributo checksum.
     *
     * @param  _checksum  O novo valor para o atributo checksum.
     */
    public void setChecksum(int _checksum) {
        try {
            this.checksum.setValue((int) _checksum);
        } catch (Exception e) {
        }
    }

    /**
     * Método modificador do atributo controle.
     *
     * @param  _controle  O novo valor para o atributo controle.
     */
    public void setControle(short _controle) {
        try {
            this.controle.setValue((short) _controle);
        } catch (Exception e) {
        }
    }

    /**
     * Método modificador do atributo controle.
     *
     *@param  _controle  O novo valor para o atributo controle.
     */
    public void setControle(byte _controle) {
        try {
            this.controle.setValue((short) _controle);
        } catch (Exception e) {
        }
    }

    /**
     * Método modificador do atributo dados.
     *
     * @param  _dados  O novo valor para o atributo dados.
     */
    public void setDados(String _dados) {
        this.dados = _dados;
    }

    /**
     * Método modificador do atributo ipSimuladoLocal.
     *
     * @param  _ipSimuladoLocal  O novo valor para o atributo ipSimuladoLocal.
     */
    public void setIpSimuladoLocal(String _ipSimuladoLocal) {
        this.ipSimuladoLocal = _ipSimuladoLocal;
    }

    /**
     * Método modificador do atributo ipSimuladoRemoto.
     *
     * @param  _ipSimuladoRemoto  O novo valor para o atributo ipSimuladoRemoto. 
     */
    public void setIpSimuladoRemoto(String _ipSimuladoRemoto) {
        this.ipSimuladoRemoto = _ipSimuladoRemoto;
    }

    /**
     * Método modificador do atributo janela.
     *
     * @param  _janela  O novo valor para o atributo janela.
     */
    public void setJanela(int _janela) {
        try {
            this.janela.setValue((int) _janela);
        } catch (Exception e) {
        }
    }

    /**
     * Método modificador do atributo numAck.
     *
     * @param  _numAck  O novo valor para o atributo numAck.
     */
    public void setNumAck(long _numAck) {
        try {
            this.numAck.setValue((long) _numAck);
        } catch (Exception e) {
        }
    }

    /**
     * Método modificador do atributo numSequencia.
     *
     * @param  _numSequencia  O novo valor para o atributo numSequencia.
     */
    public void setNumSequencia(long _numSequencia) {
        try {
            this.numSequencia.setValue((long) _numSequencia);
        } catch (Exception e) {
        }
    }

    /**
     * Método modificador do atributo offset.
     *
     * @param  _offset  O novo valor para o atributo offset.
     */
    public void setOffset(short _offset) {
        try {
            this.offset.setValue((short) _offset);
        } catch (Exception e) {
        }
    }

    /**
     *  Método modificador do atributo opcoes.
     *
     *@param  _opcoes  O novo valor para o atributo opcoes.
     */
    public void setOpcoes(long _opcoes) {
        try {
            this.opcoes.setValue((long) _opcoes);
        } catch (Exception e) {
        }
    }

    /**
     * Método modificador do atributo ponteiroUrgente.
     *
     * @param  _ponteiroUrgente  O novo valor para o atributo ponteiroUrgente.
     */
    public void setPonteiroUrgente(int _ponteiroUrgente) {
        try {
            this.ponteiroUrgente.setValue((int) _ponteiroUrgente);
        } catch (Exception e) {
        }
    }

    /**
     * Método modificador do atributo portaLocal.
     *
     * @param  _portaLocal  O novo valor para o atributo portaLocal.
     */
    public void setPortaLocal(String _portaLocal) {
        try {
            this.portaLocal.setValue((int) Integer.parseInt(_portaLocal));
        } catch (Exception e) {
        }
    }


    /**
     * Método modificador do atributo portaRemota.
     *
     * @param  _portaRemota  O novo valor para o atributo portaRemota .
     */
    public void setPortaRemota(String _portaRemota) {
        try {
            this.portaRemota.setValue((int) Integer.parseInt(_portaRemota));
        } catch (Exception e) {
        }
    }

    /** Método que codifica o ipSimulado no tamanho certo das especificações
     *  para o header do pacote.
     *
     * @param _ip String que contém o ipSimulado a ser codificado.
     * @return String que contém o ipSimulado já codificado.
     */
    private String codificarIp(String _ip) {
        
        StringTokenizer str  = new StringTokenizer(_ip, ".");
        String ipCodificado  = "";
        int i                = 0;
        while (str.hasMoreTokens())
        {
            if (i == 3) {
                StringTokenizer str2 = new StringTokenizer(str.nextToken(), ":");
                
                ipCodificado = ipCodificado + 
                Decoder.shortToString(Short.parseShort(str2.nextToken()));
                
                //System.out.println("Ic" + i + ": " +  Integer.parseInt(ipCodificado));
                
                ipCodificado = ipCodificado + 
                Decoder.intToString(Integer.parseInt(str2.nextToken()));
                //System.out.println("Ic" + i + ": " +  Integer.parseInt(ipCodificado));
            }
            else
            {
                ipCodificado = ipCodificado + Decoder.shortToString(
                        Short.parseShort(str.nextToken()));
//                System.out.println("Ic" + i + ": " +  Integer.parseInt(ipCodificado));
            }
            i++;
        }
//        System.out.println("TamanhoIc: " +  ipCodificado.length());
        return new String(ipCodificado);
    }


    /** Método que decodifica o ipSimulado.
     *
     * @param _ipCodificado String que contem o ipSimulado codificado.
     * @return String que contem o ipSimulado decodificado.
     */
    private String decodificarIp(String _ipCodificado) {
        String s               = _ipCodificado;
        String ipDecodificado  = "";

        for (int i = 0; i < 4; i++) {
            ipDecodificado = ipDecodificado +  Short.toString(Decoder.stringToShort(s.substring(i, i + 1)));
//            System.out.println("ID" + i + ": " +  ipDecodificado);
            if (i < 3) {
                    ipDecodificado = ipDecodificado + ".";
            } else {
                    ipDecodificado = ipDecodificado + ":";
            }
        }
        ipDecodificado = ipDecodificado + String.valueOf(
            Decoder.stringToInt(s.substring(4, 6)));
        System.out.println("ID: " +  ipDecodificado);
        return ipDecodificado;
    }

    /** 
     * Método que atribui os valores corretos ao atributo opções.
     */
    public void geraOpcoes()
    {
        //tamanho do cabeçalho do pacote
        int tamanho  = 36;

        tamanho += this.dados.length();
        // igual 2Hexa + 4Hexa + Tamanho do Pacote
        tamanho |= 0x02040000;
        this.setOpcoes(tamanho);
    }

    /** 
     * Método que ajusta para um novo valor o campo de sequência (1 + (mais)
     * o número de bytes do campo de dados do pacote. No caso do número de
     * sequêcia for maior que 2 elevado a 32 - 1 (igual ao máximo valor do
     * tipo long) ele zera o contador de número de sequência para 0.
     *
     * @param _num valor atual do campo de sequencia.
     * @return novo valor campo de sequencia incrementado de 1.
     */
    public long ajustaNumSeq(long _num) {
        long r  = _num + this.getDados().length() + 1;
        r = r % (long) Math.pow((double) 2, (double) 32);
        this.setNumSequencia(r);
        return r;
    }

    /**
     * Imprime o pacote codificado no formato de um String de bytes.
     *
     * @return    A representação do pacote como uma String.
     */
    public String toString() {
        return this.codificarIp(this.ipSimuladoRemoto) +
            this.codificarIp(this.ipSimuladoLocal) +
            this.portaRemota.toString() + this.portaLocal.toString() +
            this.numSequencia.toString() + this.numAck.toString() +
            this.offset.toString() + this.controle.toString() +
            this.janela.toString() + this.checksum.toString() +
            this.ponteiroUrgente.toString() + this.opcoes.toString() +
            this.getDados();
    }

    /**
     * Exemplo de "montagem" de um pacote e depois a "desmontagem" usando o
     * método toString().
     *
     */
    public static void main(String args[]) {
        PacoteTCP p   = new PacoteTCP();

        //p.setIpSimuladoLocal("192.168.0.2:1024");
        p.setIpSimuladoLocal("150.168.000.002:5000");
        //p.setIpSimuladoRemoto("255.255.255.255:65536");
        p.setIpSimuladoRemoto("10.0.0.2:5000");
        p.setPortaLocal("100");
        p.setPortaRemota("34000");
        // seta o número de sequência para o número máximo que ele pode
        // ter: 4294967295 
        p.setNumSequencia((long)(Math.pow((double) 2, (double) 32)-1));
        System.out.println("Num. Seq.: " + p.getNumSequencia());
        p.ajustaNumSeq(p.getNumSequencia());
        System.out.println("Num. Seq. Incrementado: " +
            p.getNumSequencia());
        p.setNumAck(0);
        p.setOffset((short) 0);
        p.setControle((short) 2);
        p.setJanela(0);
        p.setChecksum(0);
        p.setPonteiroUrgente(0);
        p.setOpcoes(0);
        p.setDados("0");
        p.geraOpcoes();


        System.out.println("Pacote1 Codificado: " + p.toString());
        System.out.println("Tamanho Pacote1: " + ((String)p.toString()).length());
        System.out.println("Tamanho dados Pacote1: " + p.getTamanho());

        PacoteTCP p2  = new PacoteTCP(p.toString());

        System.out.println("Pacote2 Codificado: " + p2.toString());
        System.out.println("Pacote1 IPL: " + p.getIpSimuladoLocal());
        System.out.println("Pacote2 IPL: " + p2.getIpSimuladoLocal());

        System.out.println("Pacote1 IPR: " + p.getIpSimuladoRemoto());
        System.out.println("Pacote2 IPR: " + p2.getIpSimuladoRemoto());
        
        System.out.println("Pacote1 IPL: " + p.getPortaLocal());
        System.out.println("Pacote2 IPL: " + p2.getPortaLocal());

        System.out.println("Pacote1: " + p.getNumSequencia());
        System.out.println("Pacote2: " + p2.getNumSequencia());
    }
        
}//fim da classe PacoteCP