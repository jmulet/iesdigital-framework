/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.preferences;

import com.l2fprod.common.swing.JTaskPaneGroup;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.iesapp.framework.pluggable.TopModuleRegistry;
import org.iesapp.framework.pluggable.TopModuleWindow;
import org.iesapp.util.StringUtils;
 
/**
 *
 * @author Josep
 */
public class UserPreferencesModule extends TopModuleWindow {

    /**
     * Creates new form UserPreferencesModule
     */
    public UserPreferencesModule() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/iesapp/framework/pluggable/pluggable"); // NOI18N
        initComponents();
        this.moduleName = "userPreferences";
        this.moduleDisplayName = bundle.getString("userPreferences");
        this.moduleDescription = "Allows users to customize modules";     
    }
    
    @Override
    public void postInitialize() {
    
          ActionListener defaultAction = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String key = e.getActionCommand();
                Object source = e.getSource();
                if(source instanceof JComboBox)
                {
                    String value = ((JComboBox) source).getSelectedItem().toString();
                    if(value.startsWith("["))
                    {
                        value = StringUtils.AfterFirst(value, "[");
                        value = StringUtils.BeforeFirst(value, "]");
                    }
                    coreCfg.setUserPreferences(key, value); 
                    coreCfg.saveIniProperties();
                }
                else if(source instanceof JCheckBox)
                {
                    String value = ((JCheckBox) source).isSelected()?"yes":"no";
                    coreCfg.setUserPreferences(key, value); 
                    coreCfg.saveIniProperties();
                }
            }
        };
        
        //These are items from the framework section
        jComboBox1.addActionListener(defaultAction);
        jComboBox2.addActionListener(defaultAction);
        jComboBox3.addActionListener(defaultAction);
       
         //However we must set initial values for them
        jComboBox1.setSelectedItem(coreCfg.getUserPreferences().getProperty(jComboBox1.getActionCommand()));
        jComboBox2.setSelectedItem(coreCfg.getUserPreferences().getProperty(jComboBox2.getActionCommand()));
        jComboBox3.setSelectedItem(coreCfg.getUserPreferences().getProperty(jComboBox3.getActionCommand()));
        
        //Append further module preferences
        //Check for all openened modules, registered in TopModuleRegistry
        Collection<TopModuleWindow> listWindows = TopModuleRegistry.listWindows();
        Iterator<TopModuleWindow> iterator = listWindows.iterator();
        while(iterator.hasNext())
        {
            TopModuleWindow next = iterator.next();
            
            //Add here content from module itself
            ArrayList<UserPreferencesBean> prefs = next.getUserModulePreferences();
            if(prefs!=null)
            {
                JTaskPaneGroup jTaskPaneGroup = new JTaskPaneGroup();
                jTaskPaneGroup.setTitle(next.getModuleDisplayName());
                jTaskPaneGroup.setAnimated(false);
                jTaskPaneGroup.setExpanded(false);
                jTaskPane1.add(jTaskPaneGroup);
            
                for(UserPreferencesBean bean: prefs)
                {
                    JPanel panel = new JPanel();
                    panel.setOpaque(false);
                    panel.setLayout(new BorderLayout());
                    JLabel label = new JLabel(bean.getName());
                    label.setToolTipText(bean.getDescription());
                    panel.add(label,BorderLayout.WEST);
                    Component comp = bean.getComponent();
                    if(comp instanceof JComboBox)
                    {
                        ((JComboBox) comp).addActionListener(defaultAction);
                    }
                    else if(comp instanceof JCheckBox)
                    {
                        ((JCheckBox) comp).addActionListener(defaultAction);
                    }
                    panel.add(comp, BorderLayout.EAST);
                    jTaskPaneGroup.add(panel);
                }
            }
            
        }
        listWindows = null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTaskPane1 = new com.l2fprod.common.swing.JTaskPane();
        jTaskPaneGroup1 = new com.l2fprod.common.swing.JTaskPaneGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        getContentContainer().setLayout(new java.awt.BorderLayout());

        jTaskPaneGroup1.setAnimated(false);
        jTaskPaneGroup1.setTitle("General");

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Idioma");
        jPanel1.add(jLabel1, java.awt.BorderLayout.WEST);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CA", "ES", "EN" }));
        jComboBox1.setActionCommand("lang");
        jPanel1.add(jComboBox1, java.awt.BorderLayout.EAST);

        jTaskPaneGroup1.getContentPane().add(jPanel1);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel2.setText("Auxiliar toolbar position");
        jPanel2.add(jLabel2, java.awt.BorderLayout.WEST);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EAST", "WEST", "NORTH", "SOUTH" }));
        jComboBox2.setActionCommand("framework.vtoolbar.location");
        jPanel2.add(jComboBox2, java.awt.BorderLayout.EAST);

        jTaskPaneGroup1.getContentPane().add(jPanel2);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel3.setText("Comportament d'inici");
        jPanel3.add(jLabel3, java.awt.BorderLayout.WEST);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AUTO", "TOOLBAR", "NORMAL", "EXTENDED" }));
        jComboBox3.setActionCommand("framework.startupPolicy");
        jPanel3.add(jComboBox3, java.awt.BorderLayout.EAST);

        jTaskPaneGroup1.getContentPane().add(jPanel3);

        jTaskPane1.add(jTaskPaneGroup1);

        getContentContainer().add(jTaskPane1, java.awt.BorderLayout.CENTER);

        jButton1.setText("Aplica");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        getContentContainer().add(jPanel4, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    //Apply properties to different modules (invoke refresh method)
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       refresh();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private com.l2fprod.common.swing.JTaskPane jTaskPane1;
    private com.l2fprod.common.swing.JTaskPaneGroup jTaskPaneGroup1;
    // End of variables declaration//GEN-END:variables

    private void refresh() {
        Collection<TopModuleWindow> listWindows = TopModuleRegistry.listWindows();
        Iterator<TopModuleWindow> iterator = listWindows.iterator();
        while(iterator.hasNext())
        {
            TopModuleWindow next = iterator.next();
            next.refreshUI();
        }
        listWindows = null;
    }

    @Override
    public void dispose() {
        super.dispose();
        this.refresh();
    }
}
    
    

