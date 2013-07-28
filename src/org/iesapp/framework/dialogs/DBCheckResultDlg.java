/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.dialogs;

import java.awt.Cursor;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class DBCheckResultDlg extends javar.JRDialog {
    public static final byte CHECK_BOTH = 2;
    public static final byte CHECK_IESDIGITAL = 0;
    public static final byte CHECK_SGD = 1;
   
    private final CoreCfg coreCfg;
    protected boolean fixed;
    private final byte checkType;

    /**
     * Creates new form DBCheckResultDlg
     * @param parent
     * @param modal
     * @param coreCfg
     * @param nouAny
     * @param checkType
     */
    public DBCheckResultDlg(java.awt.Frame parent, boolean modal, final CoreCfg coreCfg, int nouAny,
            final byte checkType) {
        super(parent, modal);
        this.coreCfg = coreCfg;
        this.checkType = checkType;
        initComponents();
        
        String checkDatabases1 = "";
        String checkDatabases2 = "";
        
        if(checkType==CHECK_BOTH || checkType==CHECK_IESDIGITAL)
        {
            checkDatabases1 = coreCfg.getIesClient().checkDatabases(nouAny);
        }
        
        if(checkType==CHECK_BOTH || checkType==CHECK_SGD)
        {
            checkDatabases2 = coreCfg.getSgdClient().checkDatabases(nouAny);
        }
        
        if(!checkDatabases1.isEmpty() || !checkDatabases2.isEmpty())
        {
            jTextPane1.setText(checkDatabases1+"\n--\n"+checkDatabases2);
            this.setLocationRelativeTo(null);
            
            this.setVisible(true);
        }
        else
        {
            fixed = true;
            this.setVisible(false);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }
     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();

        setTitle("Database integrity checks");

        jPanel1.setLayout(new java.awt.BorderLayout());

        jButton2.setText("Fix them");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, java.awt.BorderLayout.EAST);

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, java.awt.BorderLayout.WEST);

        jTextPane1.setEditable(false);
        jScrollPane1.setViewportView(jTextPane1);

        jLabel1.setText("Client database integrity checks found these incompatibilities");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 254, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
         
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String fixDatabases1 =""; 
        String fixDatabases2 ="";
        if (checkType == CHECK_BOTH || checkType == CHECK_IESDIGITAL) {
            fixDatabases1 = coreCfg.getIesClient().fixDatabases();
        }
        if (checkType == CHECK_BOTH || checkType == CHECK_SGD) {
            fixDatabases2 = coreCfg.getSgdClient().fixDatabases();
        }
        String result = fixDatabases1 + fixDatabases2;
        if(result.isEmpty())
        {
            fixed = true;
            result = " * FIXED: OK ";
        }
        else
        {
            result = " * FIXED: FAILED. Check these incompatibilities manually\n"+result;
        }
               
        this.setCursor(Cursor.getDefaultCursor());
        jTextPane1.setText(result);
        jButton2.setEnabled(false);
    }//GEN-LAST:event_jButton2ActionPerformed

     

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables

    
    public boolean isFixed() {
        return fixed;
    }

}
