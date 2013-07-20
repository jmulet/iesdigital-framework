/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.table;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
/**
 *
 * @author Josep
 */

public class MyCheckBoxRenderer extends JCheckBox implements TableCellRenderer {

  public MyCheckBoxRenderer() {
    setHorizontalAlignment(JLabel.CENTER);
    setOpaque(true);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }
    setSelected((value != null && ((Boolean) value).booleanValue()));
    return this;
  }

}



//public class MyCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
//
//    public MyCheckBoxRenderer() {
//        super();
//    }
//
//    public Component getTableCellRendererComponent(JTable table, Object value,
//            boolean isSelected, boolean hasFocus, int row, int column) {
//
//        // Select the current value
//        this.setOpaque(false);
//        this.setHorizontalTextPosition(CENTER);
//        this.setHorizontalAlignment(CENTER);
//
//        //String strNumber = (String) value;
//        //int val = ((Number) value).intValue();
//
//        if (isSelected) {
//            setForeground(table.getSelectionForeground());
//            setBackground(table.getSelectionBackground());
//        } else {
//            setForeground(table.getForeground());
//            setBackground(table.getBackground());
//        }
//
//        boolean q = (Boolean) value;
//
//        this.setSelected(q);
//
//        return this;
//    }
//
//}
