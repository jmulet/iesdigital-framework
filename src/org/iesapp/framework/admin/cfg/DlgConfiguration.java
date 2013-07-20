/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgConfiguration.java
 *
 * Created on 29-abr-2011, 12:21:09
 */

package org.iesapp.framework.admin.cfg;
import java.awt.Frame;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

/**
 *
 * @author Josep
 */
public class DlgConfiguration extends javar.JRDialog {
    private final DlgCfgDatabase dlgCfgDatabase1;
    private final ResourceBundle bundle;

    /** Creates new form DlgConfiguration */
    public DlgConfiguration(Frame par, boolean modal) {
        super(par, modal);

        bundle = ResourceBundle.getBundle("org/iesapp/framework/admin/cfg/bundle");
        initComponents();

        dlgCfgDatabase1 = new DlgCfgDatabase();
        //DlgCfgTables dlgCfgTables1 = new DlgCfgTables();

        dlgCfgDatabase1.startUp();
        //dlgCfgTables1.startUp();

        jTabbedPane1.insertTab(bundle.getString("connection"), null, dlgCfgDatabase1, null, 0);
        //jTabbedPane1.insertTab("Taules", null, dlgCfgTables1, null, 1);

        jTabbedPane1.setSelectedIndex(0);

        this.setIconImage(new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/configIcon.gif")).getImage());
        initComponents();


    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configuració");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    public void setPassword(String pwd) {
        dlgCfgDatabase1.setPassword(pwd);
    }

    
}