/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.admin;

import java.awt.Frame;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class CanviExpedientDlg extends javar.JRDialog {
    private final int oldExp;
    private final CoreCfg coreCfg;
     
    /**
     * Creates new form CanviExpedientDlg
     */
    public CanviExpedientDlg(Frame frame, boolean modal, int oldExp, CoreCfg coreCfg) {
       super(frame,modal);
       this.oldExp = oldExp;
       this.coreCfg = coreCfg;
       initComponents();
       this.jSpinField1.setValue(oldExp);
      }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSpinField1 = new com.toedter.components.JSpinField();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Canvi de número d'expedient");

        jLabel1.setText("New expedient number");

        jLabel2.setText("Log");

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Change");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Change in SGD too");

        jScrollPane1.setViewportView(jEditorPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSpinField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jCheckBox1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(6, 6, 6)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        boolean existsIESDIGITAL = false;
        boolean existsSGD = false;
        int newExp = jSpinField1.getValue();   
        if(newExp<=0)
        {
            JOptionPane.showMessageDialog(this, "El número d'expedient "+newExp+" és invàlid.");
            return;
        }
        
        try {
            //comprova si ja existeix en alguna base
            String SQL1 = "SELECT * FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne WHERE exp2="+newExp;
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1);
            if(rs1!=null && rs1.next())
            {
                existsIESDIGITAL = true;
            }
            SQL1 = "SELECT * FROM alumnos WHERE expediente="+newExp;
            rs1 = coreCfg.getSgd().getResultSet(SQL1);
            if(rs1!=null && rs1.next())
            {
                existsSGD = true;
           }
                
            rs1.close();
        } catch (SQLException ex) {
            Logger.getLogger(CanviExpedientDlg.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(existsIESDIGITAL || existsSGD)
        {
            JOptionPane.showMessageDialog(this, "El número d'expedient "+newExp+" ja existeix en les bases de dades.");
            return;
        }
        String text = "Converting ...\n\n";
        //OK: proceed
        //Canvis a base config
        String[] batch1 = new String[]{
        "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne SET exp2="+newExp+" WHERE exp2="+oldExp,
        "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alta SET Exp2="+newExp+" WHERE Exp2="+oldExp,
        "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_detall SET EXP_FK_ID="+newExp+" WHERE EXP_FK_ID="+oldExp,
        "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic SET Exp2="+newExp+" WHERE Exp2="+oldExp,
        "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.xes_dades_pares SET Exp2="+newExp+" WHERE Exp2="+oldExp,
        "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.sig_medicaments_alumnes SET Exp2="+newExp+" WHERE Exp2="+oldExp,
        "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.sig_medicaments_observ SET Exp2="+newExp+" WHERE Exp2="+oldExp,
        
        "UPDATE `"+CoreCfg.core_mysqlDB+"`.sig_medicaments_reg SET Exp2="+newExp+" WHERE Exp2="+oldExp,
        "UPDATE `"+CoreCfg.core_mysqlDB+"`.tuta_dies_sancions SET Exp2="+newExp+" WHERE Exp2="+oldExp,
        "UPDATE `"+CoreCfg.core_mysqlDB+"`.tuta_entrevistes SET Exp2="+newExp+" WHERE Exp2="+oldExp,
        "UPDATE `"+CoreCfg.core_mysqlDB+"`.tuta_reg_actuacions SET Exp2="+newExp+" WHERE Exp2="+oldExp,
        "UPDATE `"+CoreCfg.core_mysqlDB+"`.tuta_reg_actuacions_deleted SET Exp2="+newExp+" WHERE Exp2="+oldExp
        };
        
        for(String sql: batch1)
        {
                int nup = coreCfg.getMysql().executeUpdate(sql);
                text += "\nUpdated="+nup+" :  "+ sql;
                jEditorPane1.setText(text);
        }
        
        if(jCheckBox1.isSelected())
        {
        //OK: proceed
        //Canvis a base sgd
        String[] batch2 = new String[]{
        "UPDATE `"+CoreCfg.coreDB_sgdDB+"`.alumnos SET expediente="+newExp+" WHERE expediente="+oldExp
        };
        
        
        for(String sql: batch2)
        {
                int nup = coreCfg.getSgd().executeUpdate(sql);
                text += "\nUpdated="+nup+" :  "+ sql;
                jEditorPane1.setText(text);         
        }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.components.JSpinField jSpinField1;
    // End of variables declaration//GEN-END:variables
}
