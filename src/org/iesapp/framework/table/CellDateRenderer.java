/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.table;


import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Josep
 */
public class CellDateRenderer extends DefaultTableCellRenderer {

   protected java.awt.Color highLightColor = null;
  /*
   * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
   */
    private ArrayList<Integer> rows;

  


  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus,
                                                 int row, int column) {



   JLabel comp = new JLabel();

   Calendar cal = Calendar.getInstance();

   if (value instanceof java.sql.Date)
   {
       java.sql.Date date = (java.sql.Date) value;
       cal.setTime(date);
   }
   else if(value instanceof java.util.Date)
   {
       java.util.Date date = (java.util.Date) value;
       cal.setTime(date);
   }
   else if(value instanceof Calendar)
   {
       cal = (Calendar) value;
   }

   if(value==null)
   {
       comp.setText("");
   }
   else
   {
        SimpleDateFormat sdf = new SimpleDateFormat(("dd-MM-yyyy"));
        comp.setText(sdf.format(cal.getTime()));
   }
   comp.setOpaque(true);
   if(isSelected)
   {
       comp.setBackground(table.getSelectionBackground());
   }
   else
   {
       if(rows!=null && rows.contains(row))
       {
            if(highLightColor!=null) {
                 comp.setBackground(highLightColor);
             }
       }
   }

   
   
   return comp;
  }

    /**
     * @return the highLightColor
     */
    public java.awt.Color getHighLightColor() {
        return highLightColor;
    }

    /**
     * @param highLightColor the highLightColor to set
     */
    public void setHighLightColor(java.awt.Color highLightColor) {
        this.highLightColor = highLightColor;
    }

    public void setHighLightRows(ArrayList<Integer> rows) {
        this.rows = rows;
    }

}
