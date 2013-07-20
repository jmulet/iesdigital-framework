/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.actuacions;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.iesapp.clients.iesdigital.actuacions.BeanFieldReport;

/**
 *
 * @author Josep
 */
class CustomCBRenderer extends javax.swing.JPanel implements ListCellRenderer {
    private final JLabel jLabel1;
    private final JLabel jLabel2;

    public CustomCBRenderer() {
        
        jLabel1 = new JLabel();        
        jLabel2 = new JLabel();
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) { 
        BeanFieldReport bfr = (BeanFieldReport) value;
        jLabel1.setText(bfr.getReportDescription());
        if(bfr.isImportant()) {
            jLabel1.setIcon(new ImageIcon(CustomCBRenderer.class.getResource("/org/iesapp/framework/actuacions/icons/dotred.gif")));
        }
        else {
            jLabel1.setIcon(new ImageIcon(CustomCBRenderer.class.getResource("/org/iesapp/framework/actuacions/icons/dotgrey.gif")));
        }
        
        jLabel2.setIcon(new ImageIcon(CustomCBRenderer.class.getResource("/org/iesapp/framework/actuacions/icons/"+bfr.getLang().toLowerCase()+".gif")));
        jLabel2.setToolTipText(bfr.getLang());
        return this;
    }
    
}
