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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.table.DefaultTableCellRenderer;

public class MyIconButtonRenderer extends DefaultTableCellRenderer {

   public MyIconButtonRenderer(String[] items, String[] resources) {
       mode = 1;
       opcions = items;
       icons = new ImageIcon[items.length];
       for(int i=0; i<items.length; i++)
       {
            icons[i] = new ImageIcon(getClass().getResource(resources[i]));
       }

    }

   public MyIconButtonRenderer(String[] resources) {
       mode = 2;
       icons = new ImageIcon[resources.length];
       for(int i=0; i<resources.length; i++)
       {
            icons[i] = new ImageIcon(getClass().getResource(resources[i]));
       }
        
   }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus,
                                                 int row, int column) {


//    cfg_colorGuardies = new Color(255,200,100);
//    cfg_colorParell= new Color(255,255,255);
//    cfg_colorSenar = new Color(240,240,255);

    JLabel comp = null;
  

    if(mode == 1)
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
    else if(mode==2)
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
        comp.setHorizontalTextPosition(JLabel.LEFT);
        comp.setToolTipText(ttip);
        String etc = "";
        if(code>=0) {
                etc += code;
            }

        comp.setText(txt+" "+etc);
    }

    comp.setOpaque(true);

//    if(isSelected)
    comp.setBackground(table.getSelectionBackground());

  
   JButton butt = new javax.swing.JButton();
//   {
//          @Override
//            public JToolTip createToolTip() {
//                 MultiLineToolTip tip = new MultiLineToolTip();
//                 tip.setComponent(this);
//                 return tip;
//          }
//        };
   
   butt.setText(comp.getText());
   butt.setIcon(comp.getIcon());
   butt.setToolTipText(comp.getToolTipText());
   butt.setOpaque(true);

   //if(hasFocus) butt.setBackground(java.awt.Color.red);
    
   return butt;
  }

//
//  public static Color cfg_colorGuardies;
//  public static Color cfg_colorParell;
//  public static Color cfg_colorSenar;

  private String[] opcions;
  private ImageIcon[] icons;
  private int mode;

}
