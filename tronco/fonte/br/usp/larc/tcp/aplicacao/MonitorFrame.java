package br.usp.larc.tcp.aplicacao;

/*
 * @(#)MonitorFrame.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laborat�rio de Arquitetura e Redes de Computadores
 * Escola Polit�cnica da Universidade de S�o Paulo.
 *
 */

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import br.usp.larc.tcp.protocolo.MaquinaDeEstados;
import br.usp.larc.tcp.protocolo.ProtocoloTCP;
import br.usp.larc.tcp.protocolo.TCP;

/** 
 * Classe que representa a Interface HM Monitor. Note que usamos a classe
 * Timer e TimerTask para atualizar a textArea que mostra a tabela de xonex�o.
 * Voc� tamb�m poder� utilizar essas classes para implementar mecanismos de
 * timeout (temporariza��o e timestamp de pacotes). 
 *
 * Mais detalhes e dicas de implementa��o podem ser consultadas nas Apostilas.
 * 
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a 
 * criatividade na implementa��o do seu projeto.
 *  
 *
 * @author	Laborat�rio de Arquitetura e Redes de Computadores.
 * @version	1.0 Agosto 2003.
 */
public class MonitorFrame extends JFrame {
    
   /** Creates new form Monitor */
    public MonitorFrame() {
        initComponents();
//        this.setSize(460,365);
        this.setVisible(true);
    }
    
    /** Creates new form Monitor 
     * @param _protocoloTCP*/
    public MonitorFrame(ProtocoloTCP _protocoloTCP)
    {
        this.initComponents();
//        this.setSize(460,365);
        this.setVisible(true);
        this.protocoloTCP = _protocoloTCP;
        this.timerAtualizaTabelaDeConexoes.schedule (new RemindTask(), 2 * 1000, 1 * 1000);
        this.habilitaInterface(false);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents()
    {
        GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        this.jpTitulo = new javax.swing.JPanel();
        this.jpGerenciador = new javax.swing.JPanel();
        this.jpInfo = new javax.swing.JPanel();
        this.jpComandos = new javax.swing.JPanel();
        this.jlTitulo1 = new JLabel();
        this.jlTitulo2 = new JLabel();
		this.jlPorta = new JLabel();
        this.jlIdDeConexao = new JLabel();
        this.jtfPortaTCPNovaMaquina = new JTextField();
        this.jtfIdDeConexao = new JTextField();
        this.jbCriarNovaMaquina = new JButton();
        this.jbFecharMaquina = new JButton();
        this.jbIniciarTCP = new JButton();
        this.jbFechar = new JButton();
        this.jbReset = new JButton();
        this.jpTabela = new JPanel();
        this.jspTabela = new JScrollPane();
        this.jtaTabelaDeConexoes = new JTextArea();

        // T�tulo
        jpTitulo.setLayout(new BoxLayout(jpTitulo, BoxLayout.X_AXIS));

        jlTitulo1.setFont(new Font("Dialog", Font.BOLD, 18));
        jlTitulo1.setText("Gerenciador de M�quinas de Estados: ");
        jpTitulo.add(jlTitulo1, null);

        jlTitulo2.setFont(new Font("Dialog", Font.PLAIN, 18));
        jlTitulo2.setText("Desativado");
		jpTitulo.add(jlTitulo2, null);
		
        // Gerenciador
        jpGerenciador.setLayout(new BoxLayout(jpGerenciador, BoxLayout.X_AXIS));
        jpGerenciador.setBorder(new javax.swing.border.TitledBorder("Gerenciador de M�quinas de Estado"));

        jpInfo.setLayout(new GridBagLayout());

		jlPorta.setText("Porta TCP:");        
        jtfPortaTCPNovaMaquina.setColumns(3);
        jtfPortaTCPNovaMaquina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPortaTCPNovaMaquinaActionPerformed(evt);
            }
        });
        
        jbCriarNovaMaquina.setText("Criar Nova M�quina");
        jbCriarNovaMaquina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCriarNovaMaquinaActionPerformed(evt);
            }
        });
        
        jlIdDeConexao.setText("Id de Conex�o:");
        jtfIdDeConexao.setColumns(3);
        jtfIdDeConexao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIdDeConexaoActionPerformed(evt);
            }
        });
        jbFecharMaquina.setText("Fechar M�quina");
        jbFecharMaquina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbFecharMaquinaActionPerformed(evt);
            }
        });
        
		jpGerenciador.add(jpInfo, null);

        jpComandos.setLayout(new BoxLayout(jpComandos, BoxLayout.Y_AXIS));

        jbIniciarTCP.setText("Iniciar TCP");
        jbIniciarTCP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbIniciarTCPActionPerformed(evt);
            }
        });
		jpComandos.add(jbIniciarTCP, null);

        jbFechar.setText("Fechar");
        jbFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbFecharActionPerformed(evt);
            }
        });
		jpComandos.add(jbFechar, null);

        jbReset.setText("Reset");
        jbReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetActionPerformed(evt);
            }
        });
		jpComandos.add(jbReset, null);

		jpGerenciador.add(jpComandos, null);

        // Tabela
        jpTabela.setLayout(new BoxLayout(jpTabela, BoxLayout.Y_AXIS));
        jpTabela.setBorder(new javax.swing.border.TitledBorder("Tabela de Conex�es"));
        jtaTabelaDeConexoes.setRows(10);
        jspTabela.setViewportView(jtaTabelaDeConexoes);
		jpTabela.add(jspTabela, null);

        // Janela
        setContentPane(getJpPrincipal());
        setTitle("Monitor de M�quinas de Estado");
        setResizable(true);
//		setMinimumSize(new Dimension(300,100));
		jbFechar.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
		jbFechar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 0;
		gridBagConstraints12.gridx = 1;
		gridBagConstraints12.gridy = 0;
		gridBagConstraints12.weightx = 1.0;
		gridBagConstraints12.ipadx = 30;
		gridBagConstraints13.gridx = 2;
		gridBagConstraints13.gridy = 0;
		gridBagConstraints14.gridx = 0;
		gridBagConstraints14.gridy = 1;
		gridBagConstraints15.gridx = 1;
		gridBagConstraints15.gridy = 1;
		gridBagConstraints15.ipadx = 30;
		gridBagConstraints16.gridx = 2;
		gridBagConstraints16.gridy = 1;
		jpInfo.add(jlPorta, gridBagConstraints11);
		jpInfo.add(jtfPortaTCPNovaMaquina, gridBagConstraints12);
		jpInfo.add(jbCriarNovaMaquina, gridBagConstraints13);
		jpInfo.add(jlIdDeConexao, gridBagConstraints14);
		jpInfo.add(jtfIdDeConexao, gridBagConstraints15);
		jpInfo.add(jbFecharMaquina, gridBagConstraints16);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });

        pack();
    }

    private void jbFecharMaquinaActionPerformed(ActionEvent evt)
    {
    	try
		{
    		String argumentos[]  = {""};
    		argumentos[0] = this.jtfIdDeConexao.getText();
    		this.protocoloTCP.recebePrimitivaAplicacao (TCP.P_TCP_CLOSE_ME, argumentos);

            Iterator tab = this.protocoloTCP.getMonitor().getMaquinasDeEstados().maquinasKeySet();
            String id = (String) tab.next();
            this.jtfIdDeConexao.setText(id);
		}
    	catch(Exception e)
		{
    		JOptionPane.showMessageDialog(null,"jbFecharMaquinaActionPerformed: "+e.getMessage());
		}        
    }

    private void jbFecharActionPerformed(ActionEvent evt)
    {
        try
        {
            String argumentos[]  = {""};
            this.protocoloTCP.recebePrimitivaAplicacao (TCP.P_TCP_CLOSE, argumentos);
            this.habilitaInterface(false);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,"jbFecharActionPerformed: " + e.getMessage());
        }
    }

    private void jbCriarNovaMaquinaActionPerformed(ActionEvent evt)
    {
        try
		{
            String argumentos[]  = {""};
            argumentos[0] = this.jtfPortaTCPNovaMaquina.getText();
            this.protocoloTCP.recebePrimitivaAplicacao (TCP.P_TCP_OPEN_ME, argumentos);
            
            int porta = Integer.parseInt (this.jtfPortaTCPNovaMaquina.getText());
            this.jtfPortaTCPNovaMaquina.setText(Integer.toString(porta+1));
            
            Iterator tab = this.protocoloTCP.getMonitor().getMaquinasDeEstados().maquinasKeySet();
            String id = (String) tab.next();
            this.jtfIdDeConexao.setText(id);
        }
        catch(Exception e)
		{
            JOptionPane.showMessageDialog(null, "jButtonCriarNovaMaquinaActionPerformed: " + e.getMessage());
        }
    }
    
    private void jTextFieldIdDeConexaoActionPerformed(ActionEvent evt)
    {
    }
    
    private void jTextFieldPortaTCPNovaMaquinaActionPerformed(ActionEvent evt)
    {
    }
    
    private void jButtonResetActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jButtonResetActionPerformed
    
    private void jbIniciarTCPActionPerformed (ActionEvent evt)
    {
        try
        {
            String argumentos[] = null;
            this.protocoloTCP.recebePrimitivaAplicacao (TCP.P_TCP_OPEN, argumentos);
            this.jtfPortaTCPNovaMaquina.setText("0");
            this.habilitaInterface (true);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog (null, "jButtonIniciarTCPActionPerformed: "
                                                 + e.getMessage ());
        }
    }
    
    /** Exit the Application 
     * @param evt*/
    private void exitForm(java.awt.event.WindowEvent evt)
    {
        System.exit(0);
    }
    
    private void habilitaInterface (boolean _flag)
    {
        if (_flag)
        {
            this.jlTitulo2.setForeground (new java.awt.Color (0, 153, 0));
            this.jlTitulo2.setHorizontalAlignment (javax.swing.SwingConstants.CENTER);
            this.jlTitulo2.setText ("Ativado");
        }
        else
        {
            this.jlTitulo2.setForeground (new java.awt.Color (204, 0, 0));
            this.jlTitulo2.setHorizontalAlignment (javax.swing.SwingConstants.CENTER);
            this.jlTitulo2.setText ("Desativado");
        }

        this.jbFechar.setEnabled (_flag);
        this.jbFecharMaquina.setEnabled (_flag);
        this.jbReset.setEnabled (_flag);
        this.jbCriarNovaMaquina.setEnabled (_flag);
        this.jtfPortaTCPNovaMaquina.setEditable (_flag);
        this.jtfIdDeConexao.setEditable (_flag);
        this.jbIniciarTCP.setEnabled (!_flag);
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jspTabela;
    private javax.swing.JPanel jpInfo;
    private javax.swing.JButton jbFechar;
    private javax.swing.JButton jbReset;
    private javax.swing.JPanel jpTabela;
    private javax.swing.JLabel jlIdDeConexao;
    private javax.swing.JLabel jlTitulo2;
    private javax.swing.JPanel jpGerenciador;
    private javax.swing.JTextArea jtaTabelaDeConexoes;
    private javax.swing.JTextField jtfIdDeConexao;
    private javax.swing.JPanel jpComandos;
    private javax.swing.JPanel jpTitulo;
    private javax.swing.JLabel jlTitulo1;
    private javax.swing.JButton jbIniciarTCP;
    private javax.swing.JButton jbCriarNovaMaquina;
    private javax.swing.JButton jbFecharMaquina;
    private javax.swing.JTextField jtfPortaTCPNovaMaquina;
    // End of variables declaration//GEN-END:variables
    private ProtocoloTCP protocoloTCP;
    private Timer timerAtualizaTabelaDeConexoes = new Timer();
    
	private JPanel jpPrincipal = null;
	private JLabel jlPorta = null;
	
    class RemindTask extends TimerTask
    {
        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        public void run ()
        {
            try
            {
                MonitorFrame.this.jtaTabelaDeConexoes
                        .setText (MonitorFrame.this.protocoloTCP.getMonitor ()
                                .getTabelaDeConexoes ().toString ());
                boolean existeMaquinas = !MonitorFrame.this.protocoloTCP.getMonitor ()
                        .getTabelaDeConexoes ().isEmpty ();
                MonitorFrame.this.jbFecharMaquina.setEnabled (existeMaquinas);
                MonitorFrame.this.jtfIdDeConexao.setEditable (existeMaquinas);
            }
            catch (Exception ex)
            {
                System.err.println ("MonitorFrame.RemindTask.run: erro: " + ex.getMessage ());
                System.err.flush();
            }

        }
    }
    
	/**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJpPrincipal ()
    {
        if (this.jpPrincipal == null)
        {
            this.jpPrincipal = new JPanel ();
            this.jpPrincipal.setLayout (new BoxLayout (this.jpPrincipal, BoxLayout.Y_AXIS));
            this.jpPrincipal.add (this.jpTitulo, null);
            this.jpPrincipal.add (this.jpGerenciador, null);
            this.jpPrincipal.add (this.jpTabela, null);
        }
        return this.jpPrincipal;
    }
    
} //  
