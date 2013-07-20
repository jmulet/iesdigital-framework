/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.awt.Graphics;
 

/**
 * Panel with background image
 * @author Josep
 */
public class JPanelImage extends javax.swing.JPanel {
    private java.awt.Image img=null;
    
    public JPanelImage()
    {
        //Default constructor
    }
    
    public JPanelImage(String resource)
    {
        img = new javax.swing.ImageIcon(getClass().getResource(resource)).getImage();
    }
    
    public void setResource(String resource)
    {
          img = new javax.swing.ImageIcon(getClass().getResource(resource)).getImage();
          this.updateUI();
    }
    
    
    @Override
    public void paintComponent(Graphics g)
    {
        if(img!=null)
        {
//            Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
//            //Dimension size = new Dimension(1000,1000);
//            g.clearRect(0, 0, 1000, 1000);
//            setPreferredSize(size);
//            setMinimumSize(size);
//            setMaximumSize(size);
//            setSize(size);
            //setLayout(null);
            g.drawImage(img, 0, 0, null);
        }
        else
        {
//            Dimension size = new Dimension(550, 297);
//            setPreferredSize(size);
//            setMinimumSize(size);
//            setMaximumSize(size);
//            setSize(size);
//            
            g.setColor(java.awt.Color.WHITE);
            g.drawRect(0, 0, 550, 297);
        }
    }

    @Override
    public void update(Graphics g){
        paint(g);
    }

}
