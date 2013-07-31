/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import java.awt.Cursor;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLDocument;
import org.iesapp.database.HtmlEscape;

/**
 *
 * @author Josep
 */
public class DebugLogger extends javax.swing.JTextPane {
   
    private static DebugLogger instance;
    private final HTMLDocument document;
    
    private DebugLogger()
    {
        this.setContentType("text/html");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        document = DockingFrameworkApp.customOutputPrinter.getDocument();
        this.setDocument(document);
        document.addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                int loc = document.getLength()-1;
                DebugLogger.this.setCaretPosition(loc>=0? loc:0);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                 int loc = document.getLength()-1;
                DebugLogger.this.setCaretPosition(loc>=0? loc:0);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                 int loc = document.getLength()-1;
                DebugLogger.this.setCaretPosition(loc>=0? loc:0);
            }
        });
        
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
       try {        
            String renderClass = "update";
            if(txt.startsWith("\t"))
            {
                renderClass = "update2";
            }
            String msg = "<div class='"+renderClass+"'>"+HtmlEscape.escape(txt)+"</div>";
            DockingFrameworkApp.customOutputPrinter.write(msg.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(DebugLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
