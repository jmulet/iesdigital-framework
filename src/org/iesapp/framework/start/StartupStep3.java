/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.start;

import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class StartupStep3 extends javax.swing.JPanel {
    private DefaultListModel model1, model2;
    private DefaultComboBoxModel combomodel1;
    private HashMap<String, String> taules;
    private final CoreCfg tmpClient;

    /**
     * Creates new form StartupStep3
     */
    public StartupStep3() {
        initComponents();
        tmpClient = StartupWiz.getTmpClient();
        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);  
        
        
    }
    
    public void registerListenerRadioButtons(ActionListener listener)
    {
        jRadioButton1.addActionListener(listener);
        jRadioButton2.addActionListener(listener);
        jComboBox1.addActionListener(listener);
    }
            
    
    
    public boolean doSave()
    {
        boolean q = false;
        String value = "";
        if(jRadioButton1.isSelected())
        {
            //Create a new curs
                  
            int selyear = (Integer) jComboBox1.getSelectedItem();
            value = selyear +"";
            
            
            //Crea la base en blanc per curs seleccionat
            tmpClient.createEmptyxxxxDB( CoreCfg.core_mysqlDBPrefix+value ); 
            //System.out.println("created new db="+tmpClient.core_mysqlDBPrefix+value );
            
            //Copia les bases de dades que interesen
            if(jCheckBox1.isSelected())
            {
                Object[] selectedValues = jList2.getSelectedValues();
                
                for(int i=0; i<selectedValues.length; i++)
                {
                    String str = (String) selectedValues[i];
                    
                    String tableName = taules.get(str);
                    
                    String from =CoreCfg.core_mysqlDBPrefix+(selyear-1);
                    String to = CoreCfg.core_mysqlDBPrefix+selyear;
                    if(tableName!=null)
                    {
                    tmpClient.getMysql().copyDataTableBetweenDBs(tableName, from, to);
                    }
                    //System.out.println("copying "+tableName+" from  "+from+" to "+ to );
                }
            }
            
        }
        else
        {
            
              value = (String) jList1.getSelectedValue();
        }
        
        //Carrega la configuracio per defecte
        tmpClient.readDatabaseCfg();
        //Desa la configuracio a la taula.
        CoreCfg.configTableMap.put("anyIniciCurs", value);
        tmpClient.updateDatabaseCfg("anyIniciCurs", value);
    
        return q;        
    }
    
    
    public void onShow()
    {
        if(StartupWiz.configType==StartupStep0.PORTABLE_MYSQL)
        {
            tmpClient.startFitxesDB();
        }
        
        model1 = new DefaultListModel();
        jList1.setModel(model1);
                
        model2 = new DefaultListModel();
        jList2.setModel(model2);

        //Llista de taules a mantenir dades
        taules = new HashMap<String,String>();
        taules.put("Aules","sig_espais");
        taules.put("Actuacions de tutoria","tuta_actuacions");
        taules.put("Hores de classe","sig_hores_classe");
        taules.put("Zones de guàrdia","sig_guardies_zones");
        taules.put("Camps sense guàrdia","sig_senseguardia");
        taules.put("Materials reservables","sig_reserves_material");
        taules.put("Items d'info tutors","sig_missatgeria_items");
        taules.put("Tasques programades","sig_progtasques");
        taules.put("Programes PIE","fitxa_programes");
        taules.put("Permisos grups d'usuaris","fitxa_permisos");
        
        for(String desc: taules.keySet()) {
            model2.addElement(desc);
        }
        
        jList2.setSelectionInterval(0, (model2.getSize()-1) );
        
        combomodel1 = new DefaultComboBoxModel();
        jComboBox1.setModel(combomodel1);
        
         //determina els cursos que existeixen
        tmpClient.getMysql().setCatalog(CoreCfg.core_mysqlDBPrefix);
        String SQL1 = "Show databases";
        try {
            Statement st = tmpClient.getMysql().createStatement();
            ResultSet rs1 = tmpClient.getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())   
            {
                String txt = rs1.getString(1);
                if(txt.startsWith(CoreCfg.core_mysqlDBPrefix))
                {
                    model1.addElement(txt.replaceAll(CoreCfg.core_mysqlDBPrefix, ""));
                }
            }
            if(rs1!=null)
            {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(StartupStep3.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int thisyear = StringUtils.anyAcademic_primer_int();
        for(int i=thisyear-3; i<thisyear+4; i++)
        {
            if(!model1.contains(i+"")) {
                combomodel1.addElement(i);
            }
        }
        if(model1.contains( (thisyear+"") ) )
        {
            jRadioButton2.setSelected(true);
            jList1.setSelectedValue((thisyear+""), true);
        }
        else
        {
             jComboBox1.setSelectedItem(thisyear);
        }
               
      
        updateComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jRadioButton2 = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();

        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Crea un nou curs");

        jLabel2.setText("Any inici del curs");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jRadioButton2.setText("Selecciona un curs existent");

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Manté algunes dades del curs anterior");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Aules", "Actuacions de tutoria", "Hores de classe", "Zones de guàrdia", "Camps sense guàrdia", "Materials reservables", "Items d'info tutors", "Tasques programades", "Programes PIE", "Grups de permisos" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton2)
                            .addComponent(jRadioButton1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, 0, 259, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        jList2.setEnabled(jCheckBox1.isSelected());
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    public void updateComponents() {
        if(jRadioButton1.isSelected())
        {
            jComboBox1.setEnabled(true);
            jList2.setEnabled(true);
            jCheckBox1.setEnabled(true);
            jList1.setEnabled(false);
        }
        else
        {
            jComboBox1.setEnabled(false);
            jList2.setEnabled(false);
            jCheckBox1.setEnabled(false);
            jList1.setEnabled(true);
        }
        
          //Existeix un curs anterior?
        boolean q = model1.contains(( (Integer) jComboBox1.getSelectedItem()-1)+"");
        jCheckBox1.setEnabled(q);
        jList2.setEnabled(q);
 
    }
}
