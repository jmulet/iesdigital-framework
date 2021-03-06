/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.modulesAPI;

import org.iesapp.framework.pluggable.daemons.BeanDaemon;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Josep
 */
public class ModulesDeamonsManager extends javar.JRDialog {
    private DefaultTableModel modelTable1;
    private DefaultListModel modelList1;
    private final ArrayList<BeanDaemon> deamons;
    private final GenericFactory genericFactory;
    private boolean listening = false;

    /**
     * Creates new form ModulesDeamonsManager
     */
    public ModulesDeamonsManager(java.awt.Frame parent, boolean modal, final GenericFactory genericFactory, final ArrayList<BeanDaemon> deamons) {
        super(parent, modal);
        initComponents();
        this.deamons = deamons;
        this.genericFactory = genericFactory;
        for(BeanDaemon bd: deamons)
        {
            modelList1.addElement(bd.getDeamonClassName());
        }
        
        modelTable1.addTableModelListener(new TableModelListener(){

            @Override
            public void tableChanged(TableModelEvent e) {
                int row = jTable1.getSelectedRow();
                if(!listening || row <0)
                {
                    return;
                }
//            
                int row2 = jList1.getSelectedIndex();
                String deamonClass = (String) jList1.getSelectedValue();
                String key = (String) jTable1.getValueAt(row, 0);
                Object value = jTable1.getValueAt(row, 1);
                
                //System.out.println("key/value/class:"+key+" "+value+" "+value.getClass());
                if (value instanceof String) {
                    String txtvalue = (String) value;
                    genericFactory.setModuleDeamonAttribute(deamonClass, key, txtvalue);
                    //We must also update arrayList
                    if(key.equalsIgnoreCase("enabled"))
                    {
                        deamons.get(row2).setEnabled(txtvalue.equalsIgnoreCase("yes"));
                    }
                    else if(key.equalsIgnoreCase("activateIcon"))
                    {
                        deamons.get(row2).setActivateIcon(txtvalue.equalsIgnoreCase("yes"));
                    }
                    else if(key.equalsIgnoreCase("activateModule"))
                    {
                        deamons.get(row2).setActivateModule(txtvalue.equalsIgnoreCase("yes"));
                    }
                    else if(key.equalsIgnoreCase("showMessage"))
                    {
                        deamons.get(row2).setShowMessage(txtvalue.equalsIgnoreCase("yes"));
                    }
                    else if(key.equalsIgnoreCase("timeInMillis"))
                    {
                        deamons.get(row2).setTimeInMillis(Integer.parseInt(txtvalue));
                    }
                    
                            
                }
                else  
                {
                   //System.out.println("key/value/class:"+key+" "+value+" "+value.getClass());
                }

                        
                
            }
        
        });
        
        if(modelList1.getSize()>0)
        {
            jList1.setSelectedIndex(0);
            fillTable();
        }
        
        
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
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int row, int col)
            {
                return col==1;
            }
        };

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Deamons manager");

        jLabel1.setText("Deamons for this module");

        jLabel2.setText("Configuration parameters");

        modelList1 = new DefaultListModel();
        jList1.setModel(modelList1);
        jScrollPane1.setViewportView(jList1);

        modelTable1 = new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Key", "Value", "Description"
            }
        );
        jTable1.setModel(modelTable1);
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel2)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    private void fillTable() {
        
       
       int row = jList1.getSelectedIndex();
       if(row<0)
       {
           return;
       }
       listening = false;
       while(jTable1.getRowCount()>0)
       {
           modelTable1.removeRow(0);
       }
       
       BeanDaemon bd = deamons.get(row);
       modelTable1.addRow(new Object[]{"enabled", bd.isEnabled()?"yes":"no", ""});
       modelTable1.addRow(new Object[]{"timeInMillis", bd.getTimeInMillis()+"", ""});
       modelTable1.addRow(new Object[]{"showMessage", bd.isShowMessage()?"yes":"no", ""});
       modelTable1.addRow(new Object[]{"activateIcon", bd.isActivateIcon()?"yes":"no", ""});
       modelTable1.addRow(new Object[]{"activateModule", bd.isActivateModule()?"yes":"no", ""});
       
      listening = true;
    }
}
