package br.usp.larc.tcp.aplicacao;

/*
 * @(#)MonitorFrame.java	1.0 31/04/2004
 *
 * Copyleft (L) 2004 Laboratório de Arquitetura e Redes de Computadores
 * Escola Politécnica da Universidade de São Paulo.
 *
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import br.usp.larc.tcp.protocolo.ProtocoloTCP;
import br.usp.larc.tcp.protocolo.TCP;

/** 
 * Classe que representa a Interface HM Monitor. Note que usamos a classe
 * Timer e TimerTask para atualizar a textArea que mostra a tabela de xonexão.
 * Você também poderá utilizar essas classes para implementar mecanismos de
 * timeout (temporarização e timestamp de pacotes). 
 *
 * Mais detalhes e dicas de implementação podem ser consultadas nas Apostilas.
 * 
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a 
 * criatividade na implementação do seu projeto.
 *  
 *
 * @author	Laboratório de Arquitetura e Redes de Computadores.
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
        this.jpTitulo = new JPanel();
        this.jpGerenciador = new JPanel();
        this.jpInfo = new JPanel();
        this.jpComandos = new JPanel();
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

        // Título
        this.jpTitulo.setLayout(new BoxLayout(this.jpTitulo, BoxLayout.X_AXIS));

        this.jlTitulo1.setFont(new Font("Dialog", Font.BOLD, 18));
        this.jlTitulo1.setText("Gerenciador de Máquinas de Estados: ");
        this.jpTitulo.add(this.jlTitulo1, null);

        this.jlTitulo2.setFont(new Font("Dialog", Font.PLAIN, 18));
        this.jlTitulo2.setText("Desativado");
		this.jpTitulo.add(this.jlTitulo2, null);
		
        // Gerenciador
        this.jpGerenciador.setLayout(new BoxLayout(this.jpGerenciador, BoxLayout.X_AXIS));
        this.jpGerenciador.setBorder(new TitledBorder("Gerenciador de Máquinas de Estado"));

        this.jpInfo.setLayout(new GridBagLayout());

		this.jlPorta.setText("Porta TCP:");        
        this.jtfPortaTCPNovaMaquina.setColumns(3);
        
        this.jbCriarNovaMaquina.setText("Criar Nova Máquina");
        this.jbCriarNovaMaquina.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbCriarNovaMaquinaActionPerformed(evt);
            }
        });
        
        this.jlIdDeConexao.setText("Id de Conexão:");
        this.jtfIdDeConexao.setColumns(3);

        this.jbFecharMaquina.setText("Fechar Máquina");
        this.jbFecharMaquina.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbFecharMaquinaActionPerformed(evt);
            }
        });
        
		this.jpGerenciador.add(this.jpInfo, null);

        this.jpComandos.setLayout(new BoxLayout(this.jpComandos, BoxLayout.Y_AXIS));

        this.jbIniciarTCP.setText("Iniciar TCP");
        this.jbIniciarTCP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbIniciarTCPActionPerformed(evt);
            }
        });
		this.jpComandos.add(this.jbIniciarTCP, null);

        this.jbFechar.setText("Fechar");
        this.jbFechar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbFecharActionPerformed(evt);
            }
        });
		this.jpComandos.add(this.jbFechar, null);

        this.jbReset.setText("Reset");
        this.jbReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbResetActionPerformed(evt);
            }
        });
		this.jpComandos.add(this.jbReset, null);

		this.jpGerenciador.add(this.jpComandos, null);

        // Tabela
        this.jpTabela.setLayout(new BoxLayout(this.jpTabela, BoxLayout.Y_AXIS));
        this.jpTabela.setBorder(new TitledBorder("Tabela de Conexões"));
        this.jtaTabelaDeConexoes.setRows(10);
        this.jspTabela.setViewportView(this.jtaTabelaDeConexoes);
		this.jpTabela.add(this.jspTabela, null);

        // Janela
        setContentPane(getJpPrincipal());
        setTitle("Monitor de Máquinas de Estado");
        setResizable(true);
//		setMinimumSize(new Dimension(300,100));
		this.jbFechar.setVerticalTextPosition(SwingConstants.CENTER);
		this.jbFechar.setHorizontalAlignment(SwingConstants.RIGHT);
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
		this.jpInfo.add(this.jlPorta, gridBagConstraints11);
		this.jpInfo.add(this.jtfPortaTCPNovaMaquina, gridBagConstraints12);
		this.jpInfo.add(this.jbCriarNovaMaquina, gridBagConstraints13);
		this.jpInfo.add(this.jlIdDeConexao, gridBagConstraints14);
		this.jpInfo.add(this.jtfIdDeConexao, gridBagConstraints15);
		this.jpInfo.add(this.jbFecharMaquina, gridBagConstraints16);
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
    
    private void jbResetActionPerformed(ActionEvent evt)
    {
        try
        {
            String argumentos[] = null;
            this.protocoloTCP.recebePrimitivaAplicacao (TCP.P_TCP_RESET, argumentos);
            this.habilitaInterface (false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
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
            JOptionPane.showMessageDialog (null, "jbIniciarTCP: " + e.getMessage ());
        }
    }
    
    /**
     * Exit the Application
     * 
     * @param evt
     */
    private void exitForm(WindowEvent evt)
    {
        System.exit(0);
    }
    
    private void habilitaInterface (boolean _flag)
    {
        if (_flag)
        {
            this.jlTitulo2.setForeground (new Color (0, 153, 0));
            this.jlTitulo2.setHorizontalAlignment (SwingConstants.CENTER);
            this.jlTitulo2.setText ("Ativado");
        }
        else
        {
            this.jlTitulo2.setForeground (new Color (204, 0, 0));
            this.jlTitulo2.setHorizontalAlignment (SwingConstants.CENTER);
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
    private JScrollPane jspTabela;
    private JPanel jpInfo;
    private JButton jbFechar;
    private JButton jbReset;
    private JPanel jpTabela;
    private JLabel jlIdDeConexao;
    private JLabel jlTitulo2;
    private JPanel jpGerenciador;
    private JTextArea jtaTabelaDeConexoes;
    private JTextField jtfIdDeConexao;
    private JPanel jpComandos;
    private JPanel jpTitulo;
    private JLabel jlTitulo1;
    private JButton jbIniciarTCP;
    private JButton jbCriarNovaMaquina;
    private JButton jbFecharMaquina;
    private JTextField jtfPortaTCPNovaMaquina;
    // End of variables declaration//GEN-END:variables
    private ProtocoloTCP protocoloTCP;
    
    private JPanel jpPrincipal = null;
    private JLabel jlPorta = null;
    
    private Timer timerAtualizaTabelaDeConexoes = new Timer();
    class RemindTask extends TimerTask
    {
        /*
         * (non-Javadoc)
         * 
         * @see this.java.util.TimerTask#run()
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
     * @return this.javax.swing.JPanel
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
