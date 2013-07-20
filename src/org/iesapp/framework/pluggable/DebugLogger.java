/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;

/**
 *
 * @author Josep
 */
public class DebugLogger extends javax.swing.JTextArea {
   
    private static DebugLogger instance;
    
    private DebugLogger()
    {
        this.setWrapStyleWord(true);
        this.setLineWrap(true);
    }
    
    public static DebugLogger getInstance()
    {
        if(instance==null)
        {
            instance = new DebugLogger();
        }
        return instance;
    }
    
    public void addText(String txt)
    {
        int offset = 0;
        if(this.getDocument().getLength()>0)
        {
            offset = this.getDocument().getLength()-1;
        }
        try {
            this.getDocument().insertString(offset, txt+"\n", null);
        } catch (BadLocationException ex) {
            Logger.getLogger(DebugLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
