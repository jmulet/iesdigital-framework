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

import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.StringUtils;



/**
 *
 * @author Josep
 */
public class JRMultiDateChooser extends javax.swing.JPanel implements JRComponent {
    private String id = "";
    private boolean required = false;
    private boolean selectable;
    private String selectionFieldName;
    protected int numberOfDays=2;
    private DefaultTableModel modelTable1;
    private HashMap<String, Object> inimap;
    private boolean addtomap;
    private String initialValue;
    private boolean initialSelection;
    private CoreCfg coreCfg;
     
    
    /** Creates new form XmlTextField */
    public JRMultiDateChooser() {
        
        initComponents();
        jRadioButton1.setVisible(selectable);
        createModel();
        jTable1.getColumnModel().getColumn(0).setCellEditor(new DateCellEditor());
        jTable1.getColumnModel().getColumn(0).setCellRenderer(new DateCellRenderer());
        jTable1.setTableHeader(null);
        //jTable1.setPreferredScrollableViewportSize(500);
    }
   

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setOpaque(false);

        jLabel1.setText("jLabel1");
        jLabel1.setName("jLabel1"); // NOI18N

        jRadioButton1.setName("jRadioButton1"); // NOI18N
        jRadioButton1.setOpaque(false);
        jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButton1StateChanged(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N
        jScrollPane2.setOpaque(false);

        modelTable1 = new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Dies"
            }
        );
        jTable1.setModel(modelTable1);
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane2.setViewportView(jTable1);
        jTable1.setRowHeight(24);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jRadioButton1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton1)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton1StateChanged
         jTable1.setVisible(jRadioButton1.isSelected());
    }//GEN-LAST:event_jRadioButton1StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    public void setValue(String value) {
        initialValue = value;
        ArrayList<String> parsed = StringUtils.parseStringToArray(value, ",", StringUtils.CASE_INSENSITIVE);
        int n = 0;
        for(String s: parsed)
        {
            if(n<jTable1.getRowCount())
            {
                 ( (JDateChooser) jTable1.getValueAt(n, 0)).setDate(toDate(s));
            }
            n += 1;
        }
          
    }

    public String getValue() {
        String s = toString( ((JDateChooser) jTable1.getValueAt(0,0)).getDate());
        for(int i=1; i<modelTable1.getRowCount(); i++)
        {
            s+=","+toString( ((JDateChooser) jTable1.getValueAt(i,0)).getDate());
        }
        return s;
    }

    public boolean check() {
        boolean check = true;        
        
        if(required)
        {
            for(int i=0; i<jTable1.getRowCount(); i++)
            {
                java.util.Date date1 = ((JDateChooser) jTable1.getValueAt(i, 0)).getDate();
                if(date1==null)
                {
                    check = false;
                    break;
                }
                for(int j=i+1; j<jTable1.getRowCount(); j++)
                {
                    java.util.Date date2 = ((JDateChooser) jTable1.getValueAt(j, 0)).getDate();   
                    if(date2==null || date2.equals(date1))
                    {
                        check = false;
                        break;
                    }
                }
            }
        }
        
        if(!check)
        {
            jLabel1.setBackground(Color.red);
            jLabel1.setOpaque(true);
        }
        else
        {
            jLabel1.setOpaque(false);
        }
        return check;
    }
    
    
    private java.util.Date toDate(String sdate)
    {
        DateFormat formatter = null;
        if(sdate.contains("/")) {
            formatter = new SimpleDateFormat("dd/MM/yyyy");
        }
        else if(sdate.contains("-")) {
            formatter = new SimpleDateFormat("dd-MM-yyyy");
        }
            
        java.util.Date date = null;
        
        if(formatter == null) {
            return null;
        }
        
        try {
            date = (java.util.Date) formatter.parse(sdate);
        } catch (ParseException ex) {
            Logger.getLogger(JRMultiDateChooser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }
    
    private String toString(java.util.Date date)
    {
        String str="";
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        if(date!=null)
        {    
           str = df.format(date);
        }
        return str;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEnable(boolean enabled) {
        jTable1.setEnabled(enabled);
        jRadioButton1.setEnabled(enabled);
    }

    public void focus() {
        jTable1.requestFocusInWindow();
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }

    public void setLabel(String label) {
        this.jLabel1.setText(label);
    }
    

    public void setSelectable(boolean selectable, String selectionFieldName) {
        this.selectable = selectable;
        this.selectionFieldName = selectionFieldName;
        jRadioButton1.setVisible(selectable);
         if(selectable)
        {
            jTable1.setVisible(jRadioButton1.isSelected());
        }
    }

    public boolean isSelectable() {
       return selectable;
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
        initialSelection = selected;
        jRadioButton1.setSelected(selected);
        jTable1.setVisible(selected);
    }    

    /**
     * @return the numberOfDays
     */
    public int getNumberOfDays() {
        return numberOfDays;
    }

    /**
     * @param numberOfDays the numberOfDays to set
     */
    public void setNumberOfDays(int numberOfDays) {
        
       this.numberOfDays = numberOfDays;
       createModel();
      
            
    }

    
    
    private void createModel()
    {
        while (jTable1.getRowCount() > 0) {
            modelTable1.removeRow(0);
        }
        for (int i = 0; i < numberOfDays; i++) {
            JDateChooser jdatechooser = new JDateChooser();

            if (inimap!=null && inimap.containsKey("allowdays")) {
                String allowdays = (String) inimap.get("allowdays");
                jdatechooser.getJCalendar().getDayChooser().addDateEvaluator(new org.iesapp.framework.util.FestiusDateEvaluator(null, null, allowdays, coreCfg));

            } else {
                jdatechooser.getJCalendar().getDayChooser().addDateEvaluator(new org.iesapp.framework.util.FestiusDateEvaluator(null, null, coreCfg));
            }
            modelTable1.addRow(new Object[]{jdatechooser});      
    }
    }

    public boolean hasChanged() {
       return (!(initialValue.equals(this.getValue())) || (initialSelection!=this.isSelected()));
    }

    
    

      

    private static class DateCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Object value;
            
            public Object getCellEditorValue() {
               return value;
            }

            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                this.value = value;
                return (Component) value;
            }
        
    }

    private static class DateCellRenderer implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
           return (Component) value;
        }
    }
    
    public void setIniParams(HashMap<String,Object> params) {
        this.inimap = params; 
        if(params.containsKey("ndays"))
        {
            numberOfDays = Integer.parseInt( (String) params.get("ndays"));
            this.setPreferredSize(new Dimension(this.getWidth(), 45+24*numberOfDays));
        }
       
        createModel();
    }
    
   public boolean isAddToMap() {
        return addtomap;
    }

    public void setAddToMap(boolean settomap) {
        addtomap = settomap;
    }
    
     @Override
    public void setCoreCfg(CoreCfg coreCfg) {
        this.coreCfg = coreCfg;
    }
}