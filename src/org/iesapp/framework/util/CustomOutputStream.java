/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.iesapp.database.HtmlEscape;
import org.iesapp.database.MyDatabase;
 
/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 * @author www.codejava.net
 *
 */
public class CustomOutputStream extends OutputStream {
    protected final HTMLDocument document;
    private Element body;
     
    public CustomOutputStream() {
            HTMLEditorKit editorkit = new HTMLEditorKit();
            document = (HTMLDocument) editorkit.createDefaultDocument(); 
            Element root = document.getDefaultRootElement();
            try {
            document.insertAfterStart(root, " <html> "
                            + "<head>"
                            + "<title>An example HTMLDocument</title>"
                            + "<style type=\"text/css\">"
                            + "  div {text-align:left; text-indent:2px; font-family:arial; font-size:10px}"
                            + "  ul { color: grey; }"
                            + "  .especial{ text-align:left; max-width: 140px;min-width:140;}"
                            + "  .select{ text-indent:12px; text-align:left; color:black; font-family:arial; font-size:12px}"
                            + "  .update{ text-indent:12px; text-align:left; color:blue; font-family:arial; font-size:10px}"
                            + "  .update2{ text-indent:20px; text-align:left; color:blue; font-family:arial; font-size:10px}"
                            + "  .insert{ text-indent:12px;text-align:left; color:rgb(0,100,255); font-family:arial; font-size:10px}"
                            + "  .delete{ text-indent:12px;text-align:left; color:rgb(200,100,0); font-family:arial; font-size:10px}"
                            + "  .error{ text-indent:2px;text-align:left; color:red; font-family:arial; font-size:12px}"                                  
                            + "  .date{text-align:left; color:grey; font-family:courier; font-size:8px; max-width: 140px;min-width:140;}" 
                            + "  .title{text-align:left; color:blue; font-family:arial; font-weight:bold; font-size:14px}"
                            + "  .comment{text-align:left; color:grey; font-family:arial;  font-style:italic; font-size:10px}"
                            + "  .summary{text-align:left; color:green; font-family:arial; font-size:10px}"
                            + "  .warning{text-align:left; color:black; background-color:yellow;font-weight:bold; font-family: Courier New,Courier,monospace; font-size:10px}"
                            + "</style>"
                            + "</head>"
                            + "<body id='body'>"                        
                            + "</body>"
                            + "</html>");
            body = document.getElement("body");
        } catch (BadLocationException ex) {
            Logger.getLogger(MyDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    @Override
    public void write(int b) throws IOException {
        try {
            // redirects data to the text area
            String msg = String.valueOf((char)b);
            document.insertBeforeEnd(body, msg);
        } catch (BadLocationException ex) {
            Logger.getLogger(CustomOutputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            // redirects data to the text area
            String msg = new String(b, off, len);
            if(msg == null)
            {
                return;
            }
            
            if(!msg.startsWith("<div"))
            {
                msg = HtmlEscape.escape(msg);
            }
            String renderClass = "select";
            if(msg.toUpperCase().contains("WARN") || msg.toUpperCase().contains("GRAVE:"))
            {
                renderClass = "error";
            }
            document.insertBeforeEnd(body, "<div class='"+renderClass+"'><code>"+msg+"</code></div>");
        } catch (BadLocationException ex) {
            Logger.getLogger(CustomOutputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
       try {
             write(b, 0, b.length);
        } catch (Exception ex) {
            Logger.getLogger(CustomOutputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public HTMLDocument getDocument() {
        return document;
    }
}