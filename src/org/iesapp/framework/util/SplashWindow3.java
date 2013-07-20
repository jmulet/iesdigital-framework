/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 *
 * @author Josep
 */
public class SplashWindow3 extends JWindow
{
    
    
    public void setOpacity(float opac)
    {
         try {
           Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
           Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
           mSetWindowOpacity.invoke(null, this, Float.valueOf(opac));
        } catch (NoSuchMethodException ex) {
           ex.printStackTrace();
        } catch (SecurityException ex) {
           ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
           ex.printStackTrace();
        } catch (IllegalAccessException ex) {
           ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
           ex.printStackTrace();
        } catch (InvocationTargetException ex) {
           ex.printStackTrace();
        }
    }
    
    public SplashWindow3(String filename, JFrame f, int waitTime, java.awt.Point p)
    {
        super(f);
        JTextArea l = new JTextArea();
        l.setColumns(15);
        l.setRows(5);
        l.setText(filename);
        l.setCaretPosition(0);
        l.setBackground(Color.YELLOW);
        l.setForeground(new java.awt.Color(0,0,0));
        l.setOpaque(true);
        l.setFont(new Font("Arial",Font.BOLD,16));
        l.setWrapStyleWord(true); 
        l.setLineWrap(true);
        
            
        JScrollPane scrollpane1=new JScrollPane(l);    
        scrollpane1.setBounds(10,50,400,300);
      
        getContentPane().add(scrollpane1, BorderLayout.CENTER);
        pack();
        Dimension screenSize =
          Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = scrollpane1.getPreferredSize();
        
        if(p==null)
        {
            setLocation(screenSize.width/2 - (labelSize.width/2),
                    screenSize.height/2 - (labelSize.height/2));
            
        }
        else
        {
            java.awt.Point p2 = new java.awt.Point(p);
            p2.y -=labelSize.height;
            setLocation(p2);
        }
        
        l.addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    setVisible(false);
                    dispose();
                }
            });
        final int pause = waitTime;
        final Runnable closerRunner = new Runnable()
            {
                public void run()
                {
                    setVisible(false);
                    dispose();
                }
            };
        Runnable waitRunner = new Runnable()
            {
                public void run()
                {
                    try
                        {
                            Thread.sleep(pause);
                            SwingUtilities.invokeAndWait(closerRunner);
                        }
                    catch(Exception e)
                        {
                            // can catch InterruptedException
                        }
                }
            };
        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();
    }
}