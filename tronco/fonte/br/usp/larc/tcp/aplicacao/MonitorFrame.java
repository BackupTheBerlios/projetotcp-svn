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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import br.usp.larc.tcp.protocolo.ProtocoloTCP;
import br.usp.larc.tcp.protocolo.TCPIF;

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
    
    /** Creates new form Monitor */
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
        jpTitulo = new javax.swing.JPanel();
        jpGerenciador = new javax.swing.JPanel();
        jpInfo = new javax.swing.JPanel();
        jpComandos = new javax.swing.JPanel();
        jlTitulo1 = new JLabel();
        jlTitulo2 = new JLabel();
		jlPorta = new JLabel();
        jlIdDeConexao = new JLabel();
        jtfPortaTCPNovaMaquina = new javax.swing.JTextField();
        jtfIdDeConexao = new javax.swing.JTextField();
        jbCriarNovaMaquina = new javax.swing.JButton();
        jbFecharMaquina = new javax.swing.JButton();
        jbIniciarTCP = new javax.swing.JButton();
        jbFechar = new javax.swing.JButton();
        jbReset = new javax.swing.JButton();
        jpTabela = new javax.swing.JPanel();
        jspTabela = new javax.swing.JScrollPane();
        jtaTabelaDeConexoes = new javax.swing.JTextArea();

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
                jButtonCriarNovaMaquinaActionPerformed(evt);
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
                jButtonFecharMaquinaActionPerformed(evt);
            }
        });
		jpGerenciador.add(jpInfo, null);

        jpComandos.setLayout(new BoxLayout(jpComandos, BoxLayout.Y_AXIS));

        jbIniciarTCP.setText("Iniciar TCP");
        jbIniciarTCP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIniciarTCPActionPerformed(evt);
            }
        });
		jpComandos.add(jbIniciarTCP, null);

        jbFechar.setText("Fechar");
        jbFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFecharActionPerformed(evt);
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

    private void jButtonFecharMaquinaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFecharMaquinaActionPerformed
    	// Add your handling code here:
    	try
		{
    		String argumentos[]  = {""};
    		argumentos[0] = (String) this.jtfIdDeConexao.getText();
    		this.protocoloTCP.recebePrimitivaAplicacao (ProtocoloTCP.P_TCP_CLOSE_ME, argumentos);
    		this.jtfIdDeConexao.setText("");
		}
    	catch(Exception e)
		{
    		JOptionPane.showMessageDialog(null,"jButtonFecharMaquinaActionPerformed: "+e.getMessage());
		}        
    }//GEN-LAST:event_jButtonFecharMaquinaActionPerformed

    private void jButtonFecharActionPerformed(java.awt.event.ActionEvent evt)
    {
        try
        {
            String argumentos[]  = {""};
            this.protocoloTCP.recebePrimitivaAplicacao (TCPIF.P_TCP_CLOSE, argumentos);
            this.habilitaInterface(false);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,"jButtonFecharActionPerformed: " + e.getMessage());
        }
    }

    private void jButtonCriarNovaMaquinaActionPerformed(java.awt.event.ActionEvent evt)
    {
        try
		{
            String argumentos[]  = {""};
            argumentos[0] = this.jtfPortaTCPNovaMaquina.getText();
            this.protocoloTCP.recebePrimitivaAplicacao (TCPIF.P_TCP_OPEN_ME, argumentos);
            this.jtfPortaTCPNovaMaquina.setText("");
        }
        catch(Exception e)
		{
            JOptionPane.showMessageDialog(null, "jButtonCriarNovaMaquinaActionPerformed: " + e.getMessage());
        }
    }
    
    private void jTextFieldIdDeConexaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIdDeConexaoActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jTextFieldIdDeConexaoActionPerformed
    
    private void jTextFieldPortaTCPNovaMaquinaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPortaTCPNovaMaquinaActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jTextFieldPortaTCPNovaMaquinaActionPerformed
    
    private void jButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jButtonResetActionPerformed
    
    private void jButtonIniciarTCPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIniciarTCPActionPerformed
        // Add your handling code here:
        try {
            String argumentos [] = null;
            this.protocoloTCP.recebePrimitivaAplicacao            
                (TCPIF.P_TCP_OPEN, argumentos);
            this.habilitaInterface(true);
        } catch(Exception e){
            JOptionPane.showMessageDialog(null,"jButtonIniciarTCPActionPerformed: " + e.getMessage());
        }
    }//GEN-LAST:event_jButtonIniciarTCPActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    private void habilitaInterface(boolean _flag) {
            if (_flag) {
                this.jlTitulo2.setForeground(new java.awt.Color(0, 153, 0));
                this.jlTitulo2.setHorizontalAlignment(
                    javax.swing.SwingConstants.CENTER);
                this.jlTitulo2.setText("Ativado");
            } else {
                this.jlTitulo2.setForeground(new java.awt.Color(204, 0, 0));
                this.jlTitulo2.setHorizontalAlignment(
                    javax.swing.SwingConstants.CENTER);
                this.jlTitulo2.setText("Desativado");
            }
            
            this.jbFechar.setEnabled(_flag);
            this.jbFecharMaquina.setEnabled(_flag);
            this.jbReset.setEnabled(_flag);
            this.jbCriarNovaMaquina.setEnabled(_flag);
            this.jtfPortaTCPNovaMaquina.setEditable(_flag);
            this.jbIniciarTCP.setEnabled(!_flag);

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
    private Timer timerAtualizaTabelaDeConexoes = new Timer();;
    
	private JPanel jpPrincipal = null;
	private JLabel jlPorta = null;
	
    class RemindTask extends TimerTask
	{	
    	public void run()
    	{
    		try 
			{
    			jtaTabelaDeConexoes.setText(protocoloTCP.getMonitor().getTabelaDeConexoes().toString());
    			boolean existeMaquinas = !protocoloTCP.getMonitor().getTabelaDeConexoes().isEmpty();
    			jbFecharMaquina.setEnabled(existeMaquinas);
    			jtfIdDeConexao.setEditable(existeMaquinas);
    		} 
    		catch (Exception ex)
			{
    			JOptionPane.showMessageDialog(null,"RemindTask.run: " + ex.getMessage());
    		}
    		
    	}
    }
    
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJpPrincipal() {
		if (jpPrincipal == null) {
			jpPrincipal = new JPanel();
			jpPrincipal.setLayout(new BoxLayout(jpPrincipal, BoxLayout.Y_AXIS));
			jpPrincipal.add(jpTitulo, null);
			jpPrincipal.add(jpGerenciador, null);
			jpPrincipal.add(jpTabela, null);
		}
		return jpPrincipal;
	}
 }  //  @jve:decl-index=0:visual-constraint="10,10"//fim da classe MonitorFrame
