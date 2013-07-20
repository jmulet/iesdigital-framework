/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.grantsystem;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;

/**
 *
 * @author Josep
 */
public class GrantEntryForm extends javax.swing.JPanel {
    private final GrantBean grantBean;
    private final ButtonGroup group1;

    /**
     * Creates new form GrantEntryForm
     */
    public GrantEntryForm(final GrantBean grantBean, int pos) {
        this.grantBean = grantBean;
        initComponents();
        
        group1 = new ButtonGroup();
        group1.add(jByte1);
        group1.add(jByte2);
        
        this.setOpaque(true);
        if(pos%2==0)
        {
            this.setBackground(new Color(255,225,100));
        }
        else
        {
            this.setBackground(Color.WHITE);
        }
        
        ActionListener disableModifiers = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                jByte3.setSelected(false);
                jByte4.setSelected(false);
                jByte5.setSelected(false);
                jComboBox1.setSelectedIndex(0);
                jComboBox2.setSelectedIndex(0);
            }
              
        };
        jByte1.addActionListener(disableModifiers);
        jByte2.addActionListener(disableModifiers);
          
        ActionListener disableBasic = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                jByte1.setSelected(false);
                jByte2.setSelected(false);  
                group1.clearSelection();
            }
              
        };
        jByte3.addActionListener(disableBasic);
        jByte4.addActionListener(disableBasic);
        jByte5.addActionListener(disableBasic);
        
        jByte1.setVisible( (grantBean.getAcceptedOptions() & GrantBean.NONE) == GrantBean.NONE);
        jByte2.setVisible( (grantBean.getAcceptedOptions() & GrantBean.ALL) == GrantBean.ALL);
        jByte3.setVisible( (grantBean.getAcceptedOptions() & GrantBean.BELONGS) == GrantBean.BELONGS);
        boolean visible4 = (grantBean.getAcceptedOptions() & GrantBean.NESE) == GrantBean.NESE;
        jByte4.setVisible( visible4 );
        jComboBox1.setVisible(visible4);
        boolean visible5 = (grantBean.getAcceptedOptions() & GrantBean.REPETIDOR) == GrantBean.REPETIDOR;
        jByte5.setVisible( visible5 );
        jComboBox2.setVisible(visible5);
//        if((grantBean.getAcceptedOptions() & GrantBean.BASIC_CONFIG) == GrantBean.BASIC_CONFIG)
//        {
//            jLabel2.setVisible(false);
//            jPanel2.setVisible(false);
//        }
        
         
        jByte1.setSelected( jByte1.isVisible() && (grantBean.value & GrantBean.NONE) == GrantBean.NONE);
        jByte2.setSelected( jByte2.isVisible() && (grantBean.value & GrantBean.ALL) == GrantBean.ALL);
        jByte3.setSelected( jByte3.isVisible() && (grantBean.value & GrantBean.BELONGS) == GrantBean.BELONGS);
        jByte4.setSelected( jByte4.isVisible() && (grantBean.value & GrantBean.NESE) == GrantBean.NESE);
        jByte5.setSelected( jByte5.isVisible() && (grantBean.value & GrantBean.REPETIDOR) == GrantBean.REPETIDOR);
        
        ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int value = generateValue();
                grantBean.setValue(value);
                grantBean.update();
            }
        };
        jByte1.addActionListener(listener);
        jByte2.addActionListener(listener);
        jByte3.addActionListener(listener);
        jByte4.addActionListener(listener);
        jByte5.addActionListener(listener);
        jComboBox1.addActionListener(listener);
        jComboBox2.addActionListener(listener);
        
        jLabel1.setText(grantBean.description);
        jLabel1.setToolTipText("key: "+grantBean.key);
               
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
        jPanel1 = new javax.swing.JPanel();
        jByte1 = new javax.swing.JCheckBox();
        jByte2 = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jByte3 = new javax.swing.JCheckBox();
        jComboBox2 = new javax.swing.JComboBox();
        jByte5 = new javax.swing.JCheckBox();
        jByte4 = new javax.swing.JCheckBox();
        jComboBox1 = new javax.swing.JComboBox();

        setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("jLabel1");

        jPanel1.setOpaque(false);

        jByte1.setText("NONE");
        jByte1.setOpaque(false);

        jByte2.setText("ALL");
        jByte2.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jByte1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jByte2)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jByte1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jByte2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel2.setText("Modifiers");

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jPanel2.setOpaque(false);

        jByte3.setText("BELONGS");
        jByte3.setOpaque(false);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));

        jByte5.setText("REPETIDORS");
        jByte5.setOpaque(false);

        jByte4.setText("NESE");
        jByte4.setOpaque(false);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jByte3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jByte4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jByte5)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jByte3)
                        .addComponent(jByte4)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jByte5)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jByte1;
    private javax.swing.JCheckBox jByte2;
    private javax.swing.JCheckBox jByte3;
    private javax.swing.JCheckBox jByte4;
    private javax.swing.JCheckBox jByte5;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables


    private int generateValue()
    {
        int value = (jByte1.isVisible() && jByte1.isSelected())? GrantBean.NONE : 0;
        value |= (jByte2.isVisible() && jByte2.isSelected())? GrantBean.ALL : 0;
        value |= (jByte3.isVisible() && jByte3.isSelected())? GrantBean.BELONGS : 0;
        value |= (jByte4.isVisible() && jByte4.isSelected())? GrantBean.NESE : 0;
        value |= (jByte5.isVisible() && jByte5.isSelected())? GrantBean.REPETIDOR : 0;
        value |= (jComboBox1.isVisible() && jComboBox1.getSelectedIndex()==1)? GrantBean.MODIFIER_NESE : 0;
        value |= (jComboBox2.isVisible() && jComboBox2.getSelectedIndex()==1)? GrantBean.MODIFIER_REPETIDOR : 0;
        return value;
    }
}