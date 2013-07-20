/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author Josep
 */
public class JRDataTable extends javax.swing.JTable {

    protected String emptyMessage = "No records found";
    protected List data;
    protected byte selectionMode;
    protected Object selection;
    private DefaultTableModel tableModel;
    private ArrayList<Boolean> editable = new ArrayList<Boolean>();
 
    public JRDataTable()
    {
         this.setAutoCreateRowSorter(true);
         this.setRowHeight(32);
         this.getTableHeader().setReorderingAllowed(false);
         
         tableModel = new javax.swing.table.DefaultTableModel();
         this.setFillsViewportHeight( getRowCount()==0 );
         this.setModel(tableModel);
         tableModel.addColumn("a");
         tableModel.addColumn("b");
         tableModel.addColumn("c");
         tableModel.addRow(new Object[]{1,2,34});
    }
    
    public void addColumn(String columnName, boolean editable, Object[] columData)
    {
        tableModel.addColumn(columnName, columData);
        this.editable.add(editable);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int colIndex) {
        return editable.get(colIndex);   //Disallow the editing of any cell
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        // We want renderer component to be
        // transparent so background image is visible
        if (c instanceof JComponent) {
            ((JComponent) c).setOpaque(getRowCount()!=0);
        }
        return c;
    }
    
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        if(getRowCount()==0)
        {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            g2d.drawString(getEmptyMessage(), 10, 20);
        }
    }
 
   
    
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        JScrollPane scrollp = new JScrollPane();
        JRDataTable dataTable = new JRDataTable();
        scrollp.setViewportView(dataTable);
        dataTable.setData(null);
        frame.add(scrollp);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
       
        
        for(int i=0; i<getColumnCount(); i++)
        {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JTextField textField = new JTextField();
            JLabel label = new JLabel("AAAA");
            panel.add(textField, BorderLayout.NORTH);
            panel.add(label, BorderLayout.CENTER);
        TableColumn column = this.getColumnModel().getColumn(i);
        column.setHeaderValue(panel);//.setHeaderRenderer(new JRTableHeaderRenderer());
        }
    }

    public String getEmptyMessage() {
        return emptyMessage;
    }

    public void setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
    }
    
    
    private class JRTableHeaderRenderer extends JPanel implements TableCellRenderer, TableCellEditor{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.setLayout(new BorderLayout());
            JTextField textField = new JTextField();
            JLabel label = new JLabel(value.toString());
            this.add(textField, BorderLayout.NORTH);
            this.add(label, BorderLayout.CENTER);
            return this;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.setLayout(new BorderLayout());
            JTextField textField = new JTextField();
            JLabel label = new JLabel(value.toString());
            this.add(textField, BorderLayout.NORTH);
            this.add(label, BorderLayout.CENTER);
            return this;
        }

        @Override
        public Object getCellEditorValue() {
           return "Aaa";
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean stopCellEditing() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void cancelCellEditing() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
