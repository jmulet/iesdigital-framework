/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.widgets.menubutton;

import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Josep
 */
public class MenuButton extends javax.swing.JButton{
    
    public static final byte POS_ABOVE=0;
    public static final byte POS_BELOW=1;
    public static final byte POS_LEFT=2;
    protected byte position = POS_BELOW;
    protected JPopupMenu popup;
   
    public MenuButton()
    {
        
        popup = new JPopupMenu();    
        this.setText("Menu");
        this.setIcon(new ImageIcon(MenuButton.class.getResource("/org/iesapp/framework/icons/down.gif")));
        this.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {                       
                       Dimension size = MenuButton.this.getSize();
                       switch(position)
                       {
                           case POS_BELOW: 
                                getPopup().show(e.getComponent(), 0, size.height);
                                break;
                               
                           case POS_ABOVE: 
                                getPopup().show(e.getComponent(), 0, -popup.getHeight());
                                break;
                               
                           case POS_LEFT:
                                getPopup().show(e.getComponent(), size.width, 0);
                                break;
                           
                       }
                      
                    }

   
                });
    }
    
    public void addMenuItem(JMenuItem item)
    {
        getPopup().add(item);
    }

    /**
     * @return the popup
     */
    public JPopupMenu getPopup() {
        return popup;
    }

    /**
     * @param popup the popup to set
     */
    public void setPopup(JPopupMenu popup) {
        this.popup = popup;
    }

    /**
     * @return the position
     */
    public byte getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(byte position) {
        this.position = position;
    }
     
           
}
