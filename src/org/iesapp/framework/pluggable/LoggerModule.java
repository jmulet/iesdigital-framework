/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class LoggerModule extends TopModuleWindow {
    private final CoreCfg coreCfg;

    /**
     * Creates new form LoggerModule
     */
    public LoggerModule(final CoreCfg coreCfg) {
        this.coreCfg = coreCfg;
        this.moduleName = "mysqlLogger";
        this.moduleDescription ="Mysql logger";
        this.moduleDisplayName = "Mysql Logger";
        
        initComponents();
        
        jPanel1.add(DebugLogger.getInstance());
              
        jTabbedPane1.setTitleAt(0, coreCfg.getMysql().getConBean().getHost()+":"+coreCfg.getMysql().getConBean().getDb());
        jTabbedPane1.setTitleAt(1, coreCfg.getSgd().getConBean().getHost()+":"+coreCfg.getSgd().getConBean().getDb());
   
        jTextPane1.setDocument(coreCfg.getMysql().getDocument());
        jTextPane2.setDocument(coreCfg.getSgd().getDocument());

        coreCfg.getMysql().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                int n = coreCfg.getMysql().getDocument().getLength();
                jTextPane1.setCaretPosition(n > 0 ? n - 1 : 0);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                int n = coreCfg.getMysql().getDocument().getLength();
                jTextPane1.setCaretPosition(n > 0 ? n - 1 : 0);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                int n = coreCfg.getMysql().getDocument().getLength();
                jTextPane1.setCaretPosition(n > 0 ? n - 1 : 0);
            }
        });
        
        
        coreCfg.getSgd().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                int n = coreCfg.getSgd().getDocument().getLength();
                jTextPane2.setCaretPosition(n > 0 ? n - 1 : 0);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                int n = coreCfg.getSgd().getDocument().getLength();
                jTextPane2.setCaretPosition(n > 0 ? n - 1 : 0);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                int n = coreCfg.getSgd().getDocument().getLength();
                jTextPane2.setCaretPosition(n > 0 ? n - 1 : 0);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N

        jTextPane1.setEditable(false);
        jTextPane1.setContentType("text/html"); // NOI18N
        jScrollPane1.setViewportView(jTextPane1);

        jTabbedPane1.addTab("tab1", jScrollPane1);

        jTextPane2.setEditable(false);
        jTextPane2.setContentType("text/html"); // NOI18N
        jScrollPane2.setViewportView(jTextPane2);

        jTabbedPane1.addTab("tab2", jScrollPane2);

        jPanel1.setLayout(new java.awt.BorderLayout());
        jScrollPane3.setViewportView(jPanel1);

        jTabbedPane1.addTab("Debugger", jScrollPane3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentContainer());
        getContentContainer().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    // End of variables declaration//GEN-END:variables
}