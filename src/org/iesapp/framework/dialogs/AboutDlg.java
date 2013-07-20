/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AboutDlg.java
 *
 * Created on 05-abr-2011, 15:20:33
 */

package org.iesapp.framework.dialogs;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.iesapp.clients.iesdigital.IesDigitalClient;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class AboutDlg extends javar.JRDialog {
    private final CoreCfg coreCfg;

    /** Creates new form AboutDlg */
    
    public AboutDlg(java.awt.Frame parent, boolean modal, String appName, String description, String version, CoreCfg coreCfg) {
        super(parent, modal);
        this.coreCfg = coreCfg;
        initialize(appName, description, version);
    }
    
    public AboutDlg(java.awt.Frame parent, boolean modal, String appName, String description, CoreCfg coreCfg) {
        super(parent, modal);
        this.coreCfg = coreCfg;
        initialize(appName, description, CoreCfg.VERSION);        
    }

    
    private void initialize(String appName, String description, String version) {
        initComponents();
        jAppName.setText(appName + " " + version);
        
        String panel1 = "<html> "
                + "<body>"
                + "<div style=\"text-align: justify;\"><span style=\"color: rgb(0, 0, 153);\"> "
                + description + "</div></span><br><br>"
                + "<span style=\"font-weight: bold;\">Autor:</span> Josep "
                + "Mulet&nbsp; (pep.mulet@gmail.com)<br>"
                + "<br>"
                + "<span style=\"font-weight: bold;\">Agraïments: </span>Sergio "
                + "Forlano, Xim López (IES Alcúdia)<br>"
                + "<br>"
                + "<span style=\"font-weight: bold;\">Organització: </span>"
                + "IES Alcúdia, Conselleria d'Educació IB<br>"
                + "<br>"
                + "<span style=\"font-weight: bold;\">Llicència:</span><br>"
                + "<div class=\"INFORMALEXAMPLE\""
                + " style=\"text-align: justify;color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; background-color: rgb(255, 255, 255); font-size: medium;\">"
                + "<p>Copyright (C) 2011-2012&nbsp; Josep Mulet</p>"
                + "<p>Este programa es software libre. Puede redistribuirlo y/o "
                + "modificarlo bajo los términos de la Licencia Pública General de GNU "
                + "según es publicada por la Free Software Foundation.</p>"
                + "<p>Este programa se distribuye con la esperanza de que sea útil, "
                + "pero SIN NINGUNA GARANTÍA, incluso sin la garantía MERCANTIL implícita "
                + "o sin garantizar la CONVENIENCIA PARA UN PROPÓSITO PARTICULAR. Véase la "
                + "Licencia Pública General de GNU para más detalles.</p>"
                + "<p>Esta Licencia Pública General no permite que incluya sus "
                + "programas en programas propietarios.</p>"
                + "<span style=\"color: rgb(0, 0, 0); font-family: 'Times New Roman'; font-style: normal; font-variant: normal; font-weight: normal; letter-spacing: normal; line-height: normal; orphans: 2; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; background-color: rgb(255, 255, 255); font-size: medium; display: inline ! important; float: none;\"></span></div>"
                + "</body>"
                + "</html>";
        
        jDescription.setText(panel1);
        jDescription.setCaretPosition(0);
        
        String panel2 = "<html>"
                + "<body>"
                + "<span style=\"font-family: 'Times New Roman';\"><span"
                + " style=\"font-weight: bold;\">Versió:</span> " + CoreCfg.VERSION + "<br>"
                + "<span style=\"font-weight: bold;\">Build:</span> " + CoreCfg.BUILD + "</span><br>"
                + "<span style=\"font-weight: bold;\">Java version:</span> " + System.getProperty("java.version") + "</span><br>"
                + "<span style=\"font-weight: bold;\">System:</span> " + System.getProperty("os.name") +" "
                + System.getProperty("os.version")+ " " + System.getProperty("os.arch")+ "</span><br>"
                + "<span style=\"font-weight: bold;\">Databases:</span> " + CoreCfg.core_mysqlDB +", "
                    +CoreCfg.coreDB_sgdDB+ "</span><br>"
                + "<span style=\"font-weight: bold;\">Client versions:</span> iesDigitalClient: " 
                + coreCfg.getIesClient().getClientVersion()+ ",  sgdClient: "
                + coreCfg.getSgdClient().getClientVersion()
                + "</span>"
                + "</body>"
                + "</html>";
        
        jVersion.setText(panel2);
        jVersion.setCaretPosition(0);
        
        
        //Load (if exists) versions file
        File file = new File("config/versions.html");
        if(file.exists())
        {
            String content = "";
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader("config/versions.html"));

              
                String sCurrentLine;

                while ((sCurrentLine = br.readLine()) != null) {
                    content += sCurrentLine+"\n";

                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AboutDlg.class.getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                Logger.getLogger(AboutDlg.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            jTextPane1.setText(content);
            jTextPane1.setCaretPosition(0);
        }
    }
   

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jAppName = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jVersion = new javax.swing.JTextPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jDescription = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sobre l'aplicació");
        setBackground(new java.awt.Color(204, 204, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        jAppName.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jAppName.setForeground(new java.awt.Color(0, 0, 153));
        jAppName.setText(" ");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/framework/icons/about.jpg"))); // NOI18N
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jVersion.setEditable(false);
        jVersion.setContentType("text/html"); // NOI18N
        jScrollPane2.setViewportView(jVersion);

        jDescription.setEditable(false);
        jDescription.setBorder(null);
        jDescription.setContentType("text/html"); // NOI18N
        jDescription.setOpaque(false);
        jScrollPane1.setViewportView(jDescription);

        jTabbedPane1.addTab("Crèdits", jScrollPane1);

        jTextPane1.setEditable(false);
        jTextPane1.setContentType("text/html"); // NOI18N
        jScrollPane3.setViewportView(jTextPane1);

        jTabbedPane1.addTab("Versions", jScrollPane3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
                    .addComponent(jAppName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jAppName)
                .addGap(3, 3, 3)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


     @Override
    protected JRootPane createRootPane() {
    JRootPane rootPane2 = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
    Action actionListener = new AbstractAction() {
      public void actionPerformed(ActionEvent actionEvent) {
        setVisible(false);
      }
    } ;
    InputMap inputMap = rootPane2.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(stroke, "ESCAPE");
    rootPane2.getActionMap().put("ESCAPE", actionListener);

    return rootPane2;
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jAppName;
    private javax.swing.JTextPane jDescription;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jVersion;
    // End of variables declaration//GEN-END:variables


}
