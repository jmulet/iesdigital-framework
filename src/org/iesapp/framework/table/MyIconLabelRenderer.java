/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.table;

/**
 *
 * @author Josep
*/
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


//
//  public static Color cfg_colorGuardies;
//  public static Color cfg_colorParell;
//  public static Color cfg_colorSenar;

public class MyIconLabelRenderer extends DefaultTableCellRenderer {
  private String[] opcions;
  private Icon[] icons;
  private int mode;
  protected java.awt.Color highLightColor;
  protected ArrayList<Integer> rows;

    public MyIconLabelRenderer() {
       mode = 0;
    }
    
   public MyIconLabelRenderer(String[] items, Icon[] icons) {
       mode = 1;
       opcions = items;
       this.icons = icons;
    }

   public MyIconLabelRenderer(Icon[] icons) {
       mode = 2;
       this.icons = icons;        
   }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus,
                                                 int row, int column) {


//    cfg_colorGuardies = new Color(255,200,100);
//    cfg_colorParell= new Color(255,255,255);
//    cfg_colorSenar = new Color(240,240,255);

    JLabel comp = new JLabel();
     
    if(mode==1 || value.getClass().equals(java.lang.Number.class) || value.getClass().equals(java.lang.Integer.class))
    {
        int code = ((Number) value).intValue();
        if(code <0 ||code>3) {
            code = 0;
        }

        //decide which icon to set
        if(column > 0) {
            comp = new JLabel(icons[code],JLabel.RIGHT);
        }
        else {
            comp = new JLabel(icons[code],JLabel.LEFT);
        }

        comp.setHorizontalTextPosition(JLabel.LEFT);
        comp.setText(opcions[code]);
    }
    else if(value.getClass().equals(CellTableState.class))
    {
        CellTableState cts = (CellTableState) value;
        int index = 0;
        int code = -1;
        String txt = "";
        String ttip = null;
        if(cts!=null)
        {
           index = cts.getState();
           code = cts.getCode();
           txt = cts.getText();
           ttip = cts.getTooltip();
        }
        comp = new JLabel(icons[index],JLabel.LEFT);
//        {
//                @Override
//            public JToolTip createToolTip() {
//                 MultiLineToolTip tip = new MultiLineToolTip();
//                 tip.setComponent(this);
//                 return tip;
//          }
//        }
        comp.setHorizontalTextPosition(JLabel.RIGHT);
        comp.setToolTipText(ttip);
        String etc = "";
        if(code>=0) {
            etc += code;
        }

        comp.setText(txt+" "+etc);
    }
    else if(value.getClass().equals(javax.swing.JLabel.class) && value!=null)
    {
        //Make sure that the object is not null!!!!
        comp = (JLabel) value;
    }

  

    if(isSelected)
    {
        comp.setOpaque(true);
        comp.setBackground(table.getSelectionBackground());
        comp.setForeground(table.getSelectionForeground());
    }
    else
    {
        comp.setOpaque(true);
        comp.setBackground(table.getBackground());
        comp.setForeground(table.getForeground());
        
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

    /**
     * @param rows the rows to set
     */
    public void setRows(ArrayList<Integer> rows) {
        this.rows = rows;
    }

}
