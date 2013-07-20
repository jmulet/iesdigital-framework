/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * XmlTextField.java
 *
 * Created on 01-abr-2012, 8:55:27
 */
package javar;

import java.util.HashMap;
import javax.swing.JRadioButton;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class JRRadioButton extends javax.swing.JPanel implements JRComponent {
    private String id="";
    private boolean required = false;
    private boolean selectable = false;
    private String selectionFieldName;
    private boolean addtomap;
    private String initialValue;
    private CoreCfg coreCfg;
    /** Creates new form XmlTextField */
    public JRRadioButton() {
        
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

        jRadioButton1 = new javax.swing.JRadioButton();

        setOpaque(false);

        jRadioButton1.setName("jRadioButton1"); // NOI18N
        jRadioButton1.setOpaque(false);
        jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButton1StateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jRadioButton1)
                .addGap(1, 1, 1))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton1StateChanged
        
    }//GEN-LAST:event_jRadioButton1StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton jRadioButton1;
    // End of variables declaration//GEN-END:variables

    public void setValue(String value) {
        jRadioButton1.setSelected(value!=null && !value.isEmpty());
        initialValue = jRadioButton1.isSelected()?"X":"";
    }

    public String getValue() {
        return jRadioButton1.isSelected()?"X":"";
    }

    public boolean check() {        
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public void setEnable(boolean enabled) {
        jRadioButton1.setEnabled(enabled);
    }

    public void focus() {
        jRadioButton1.requestFocusInWindow();
    }

    public void setRequired(boolean required) {
        this.required=required;
    }

    public boolean isRequired() {
       return this.required;
    }

    public void setLabel(String label) {
       jRadioButton1.setText(label);
    }

    public void setSelectable(boolean selectable, String selectionFieldName) {
        this.selectable = false;
        this.selectionFieldName = null;
    }

    public boolean isSelectable() {
       return false;
    }

    public JRadioButton getRadioButton() {
        return jRadioButton1;
    }

    public boolean isSelected() {
        return jRadioButton1.isSelected();
    }

    public String getSelectionFieldName() {
        return selectionFieldName;
    }

    public void setSelectionFieldName(String name) {
        this.selectionFieldName = name;
    }
    
    public void setSelected(boolean selected) {
        jRadioButton1.setSelected(selected);
         
    }

   public void setIniParams(HashMap<String,Object> params) {
        //
    }
   
     public boolean isAddToMap() {
        return addtomap;
    }

    public void setAddToMap(boolean settomap) {
        addtomap = settomap;
    }

    public boolean hasChanged() {
       return !(initialValue.equals(this.getValue())) ;
    }
    
    @Override
    public void setCoreCfg(CoreCfg coreCfg) {
        this.coreCfg = coreCfg;
    }
}
