/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.table;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Josep
*/

public class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
    public MyComboBoxRenderer(String[] items) {
        super(items);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        if(value.getClass()==Integer.class)
        {
            // Select the current value (based on item)
            setSelectedIndex( ((Number) value).intValue() );
        }
        else 
        {
            setSelectedItem(value);
        }
        return this;
    }
}