/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.pluggable;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author Josep
 */
public class SysTray {
    protected final JFrame frame;
    protected final TrayIcon trayIcon;
    protected String last_message;
    protected PopupMenu popupMenu;
    private final ImageIcon icona;
    private final String description;
   
   
   public SysTray(JFrame frame, ImageIcon icona, String description){
      this.icona = icona;
      this.description = description;
      this.frame = frame;
      last_message = "";      
    
      trayIcon = new TrayIcon(icona.getImage(), description, createPopupMenu());
        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException ex) {
            //Logger.getLogger(SysTray.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   
   
   public void dispose()
   {
       if(trayIcon!=null)
       {
            SystemTray.getSystemTray().remove(trayIcon);
       }
   }

   public void displayMessage(String missatge, MessageType type)
   {
       getTrayIcon().displayMessage(description, missatge, type);
       last_message = missatge;
   }
   
   private PopupMenu createPopupMenu(){
      popupMenu = new PopupMenu();
      return popupMenu;
   }

    public TrayIcon getTrayIcon() {
     
        return trayIcon;
    }

    public JFrame getFrame() {
        return frame;
    }

    public PopupMenu getPopupMenu() {
        return popupMenu;
    }

    public String getLast_message() {
        return last_message;
    }

    public void addActionListener(ActionListener actionListener) {
        trayIcon.addActionListener(actionListener);
    }

   
    
  
  
}