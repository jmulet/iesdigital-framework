/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GenContrassenyes.java
 *
 * Created on 21-jun-2011, 20:20:12
 */

package org.iesapp.framework.admin;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import org.iesapp.clients.iesdigital.alumnat.GeneratePwds;
import org.iesapp.framework.util.CoreCfg;
/**
 *
 * @author Josep
 */
public class GenContrassenyes extends javar.JRDialog {
    private final String anyAcademic;
    private final CoreCfg coreCfg;
   

    /** Creates new form GenContrassenyes */
    public GenContrassenyes(java.awt.Frame parent, boolean modal, String anyAcademic, CoreCfg coreCfg) {
        super(parent, modal);
        this.coreCfg = coreCfg;
        initComponents();
     
        this.anyAcademic = anyAcademic;
        String cmd = "*PER DEFECTE:\n";
        cmd += "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne SET ultim8 = RIGHT (MD5(Exp2),8);\n";
        cmd += "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne SET pwd = LEFT(CONV(ultim8,16,10),8);";
        jTextArea1.setText(cmd);

    }
    
   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jProgressBar1 = new javax.swing.JProgressBar();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Genera contrasenyes d'alumnes");
        setForeground(java.awt.Color.white);

        jLabel1.setText("Generation by default: It can be overwrite using associated JS script");
        jLabel1.setName("jLabel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        jLabel2.setText("Log");
        jLabel2.setName("jLabel2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextArea2.setEditable(false);
        jTextArea2.setBackground(new java.awt.Color(204, 204, 204));
        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setName("jTextArea2"); // NOI18N
        jScrollPane2.setViewportView(jTextArea2);

        jProgressBar1.setName("jProgressBar1"); // NOI18N

        jCheckBox1.setSelected(true);
        jCheckBox1.setText(" Update students without password only");
        jCheckBox1.setName("jCheckBox1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 60, 5));

        jButton3.setText("Generate");
        jButton3.setToolTipText("Genera contrassenyes per tots els alumnes");
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);

        jButton5.setText("Check");
        jButton5.setToolTipText("Genera contrassenyes per tots els alumnes");
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5);

        jButton4.setText("Close");
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 491, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jTextArea2.removeAll();
        GeneratePwds.generaContrasenyes(!jCheckBox1.isSelected(), coreCfg.getMysql());
        jProgressBar1.setValue(100);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
       
        
        
        jTextArea2.setText("");
        jTextArea2.append("* Alumnes sense contrassenya:\n");
        jTextArea2.append("  ---------------------------\n\n");
            
           String SQL1=" SELECT  "+
                 " xes.Exp2, CONCAT(llinatge1,', ', llinatge2,' ', nom1) AS nom, CONCAT(xh.Estudis,' ', xh.Grup) AS grupo  "+
               " FROM  `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh  "+
                 " INNER JOIN  `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes   "+
                 " ON xes.Exp2=xh.Exp2 AND xh.AnyAcademic='"+anyAcademic+"'  "+
               " WHERE xes.pwd is null or xes.pwd='' "+
               " ORDER BY grupo, llinatge1, llinatge2, nom1  ";
      
           
        try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1,st); 
              while(rs1!=null && rs1.next())
              {
                 String msg = "["+rs1.getString("Exp2")+"] "+rs1.getString("nom")+" : "+ rs1.getString("grupo")+"\n";
                 jTextArea2.append(msg);
              }
              if(rs1!=null) {
                rs1.close();
                st.close();
            }
                  
        } catch (SQLException ex) {
            Logger.getLogger(GenContrassenyes.class.getName()).log(Level.SEVERE, null, ex);
        }
          jTextArea2.append("Fi");
    }//GEN-LAST:event_jButton5ActionPerformed

 @Override
    protected JRootPane createRootPane() {
    JRootPane rootPane2 = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
    Action actionListener = new AbstractAction() {
      @Override
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
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private static javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables

}
