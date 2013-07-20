/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class HtmlLog extends javax.swing.JTextPane {
    
    public static final java.awt.Color DEFAULTCOLOR = java.awt.Color.BLACK; 
    public static final byte DEFAULTSIZE  = 0;
    public static final byte BIGSIZE = 1;
    
    public static final byte DEFAULTSTYLE = 0;
    public static final byte BOLD = 1;
    public static final byte ITALIC = 2;
    public static final byte UNDERLINE = 3;
    
    public static final byte ERRORTYPE = 0;
    public static final byte TITLETYPE = 1;
    public static final byte COMMENTTYPE = 2;
    public static final byte SUMMARYTYPE = 3;
    public static final byte WARNINGTYPE = 4;
    
    public static final String EMPTYTABLEMESSAGE = "No s'han trobat dades";
    
    private String body = "";
    private String table = "";
    private int rowCount = 0;
    private int columnCount;
    private JPopupMenu jPopupMenu1;
    private final HTMLDocument document;
    private final HashMap<String, ActionListener> eventsMap;
    private int fsize;
 
   
    
    public HtmlLog()
    {
        fsize = this.getFont().getSize();
        eventsMap = new HashMap<String,ActionListener>();
        
        this.setContentType("text/html");
        this.setEditable(false);
        HTMLEditorKit editorkit = new HTMLEditorKit();
        document = (HTMLDocument) editorkit.createDefaultDocument(); 
        this.setDocument(document);
        this.clear();
        
        jPopupMenu1 = new JPopupMenu();
        javax.swing.JMenuItem jMenuItem1 = new javax.swing.JMenuItem("Imprimir");
        jMenuItem1.setIcon(new ImageIcon(HtmlLog.class.getResource("/org/iesapp/framework/icons/print.gif")));
        javax.swing.JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    HtmlLog.this.print();
                } catch (PrinterException ex) {
                    Logger.getLogger(HtmlLog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
       
        
        jPopupMenu1.add(jMenuItem1);
        jPopupMenu1.add(jSeparator1);        
        
        javax.swing.JMenuItem jMenuItem2 = new javax.swing.JMenuItem("Esborra");
        jMenuItem2.setIcon(new ImageIcon(HtmlLog.class.getResource("/org/iesapp/framework/icons/delete.gif")));
        javax.swing.JPopupMenu.Separator jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                HtmlLog.this.clear();
                body = "";
                
            }
        });
        jPopupMenu1.add(jMenuItem2);
        jPopupMenu1.add(jSeparator2);   
        
        javax.swing.JMenuItem jMenuItem3 = new javax.swing.JMenuItem("Desa");
        jMenuItem3.setIcon(new ImageIcon(HtmlLog.class.getResource("/org/iesapp/framework/icons/save.gif")));
        javax.swing.JPopupMenu.Separator jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent e) {
                //Dialog de fitxer (escriura un doc pero realment es html)
                FileDialog fd = new FileDialog((java.awt.Dialog) null,
                        "Desa document...",
                        FileDialog.SAVE);
                fd.setLocationRelativeTo(null);
                fd.setVisible(true);
                String file = fd.getFile();
                String dir = fd.getDirectory();
                if (file == null || dir == null) {
                    return;
                }
                if (!StringUtils.AfterLast(file, ".").trim().equals("doc")) {
                    file += ".doc";
                }
 
             try {
                 java.io.FileWriter fw = new java.io.FileWriter(dir+file);
                 java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
                 java.io.PrintWriter salida = new java.io.PrintWriter(bw);
                 salida.println(HtmlLog.this.getText());
                 salida.close();
                 bw.close();
                 fw.close();
             }
             catch(java.io.IOException ex) 
             { 
                 //System.out.println("Exception: "+ex);
             }
                
            }
        });
        
        
        
        jPopupMenu1.add(jMenuItem3);
        jPopupMenu1.add(jSeparator3);   
        
//        javax.swing.JMenuItem jMenuItem4 = new javax.swing.JMenuItem("-Redueix lletra");
//        javax.swing.JMenuItem jMenuItem5 = new javax.swing.JMenuItem("+Augmenta lletra");
//        
//       jMenuItem4.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                if(fsize>6) fsize -=1;
//                java.awt.Font font = HtmlLog.this.getFont();
//                HtmlLog.this.setFont(font.deriveFont(1f*fsize));
//                //System.out.println("now has been reduced to "+HtmlLog.this.getFont().getSize());
//                String text = HtmlLog.this.getText();
//                HtmlLog.this.setText(text);
//           }
//        }); 
//       jMenuItem5.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                if(fsize<20) fsize +=1;
//                java.awt.Font font = HtmlLog.this.getFont();
//                HtmlLog.this.setFont(font.deriveFont(1f*fsize));
//                HtmlLog.this.validate();
//            }
//        });
//        jPopupMenu1.add(jMenuItem4);
//        jPopupMenu1.add(jMenuItem5);
        
        this.addMouseListener( new org.iesapp.framework.util.JPopupMenuShower(jPopupMenu1) );
        
        this.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                //System.out.println("He pitjat un hiperlink "+e.getDescription()+"; "+e.getEventType()+"; "+e.getURL());
                
                if(e.getEventType().equals(EventType.ACTIVATED))
                {
                    //System.out.println("he pitjat un link");
                    ActionListener al = eventsMap.get(e.getDescription());
                    if(al!=null)
                    {
                        al.actionPerformed(null);
                    }
                }   
        }});
        
        
    }
    
     public void append(String htmlText)             
     {
         append(htmlText, "body");
     }
             
    
    public void append(String htmlText, String id)
    {
       
        try {
            Element el = document.getElement(id);
            //document.getElement(document.getDefaultRootElement(), StyleConstants.NameAttribute, HTML.Tag.DIV)
          if( el==null) 
           {
               append("internal log error >> element with id "+id+" not found.","body",HtmlLog.ERRORTYPE);
           }
           else
           {
               document.insertBeforeEnd(el, htmlText);
           }

        } catch (IOException ex) {
           
            Logger.getLogger(HtmlLog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            
            Logger.getLogger(HtmlLog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void append(String htmlText, java.awt.Color color, int size, byte style)
    {
        append(htmlText, "body", color, size, style);
    }
    
    public void append(String htmlText, String id, java.awt.Color color, int size, byte style)
    {
        String rgb = Integer.toHexString(color.getRGB());
        rgb = rgb.substring(2, rgb.length());
        
        String tagStartSize = "";
        String tagEndSize = "";
        if(size==BIGSIZE)
        {
            tagStartSize = "<big>";
            tagEndSize = "</big>";
        }
       
        String fontweight= "";
        if(style==BOLD)
        {
            fontweight = "font-weight: bold;";
        }
        else if(style==ITALIC)
        {
            fontweight = "font-weight: italic;";
        }
        
        String decoration= "";
        if(style==UNDERLINE)
        {
            fontweight = "text-decoration: underline;";
        }
       
       String txt = tagStartSize+"<span style=\""+fontweight+" "+decoration+" color: #"
                +rgb+";\">"+htmlText+"</span>"+tagEndSize+"<br>";
         
        
        try {
            Element el = document.getElement(id);
            //document.getElement(document.getDefaultRootElement(), StyleConstants.NameAttribute, HTML.Tag.DIV)
           if( el==null) 
           {
                 append("internal log error >> element with id "+id+" not found.","body",HtmlLog.ERRORTYPE);
           }
           else
           {
               document.insertBeforeEnd(el, txt);
           }

        } catch (IOException ex) {
          
            Logger.getLogger(HtmlLog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
    
            Logger.getLogger(HtmlLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    public void append(String txt, byte type)
    {
        append(txt,"body",type);
    }
    
    public void append(String txt, String id, byte type)
    {
        if(type==ERRORTYPE)
        {
            append("<div class='error'>"+txt+"</div>", id);
        }
        else if(type==TITLETYPE)
        {
            append("<div class='title'>"+txt+"</div>"+"<hr style=\"width: 100%; height: 2px;\">", id);
        }
        else if(type==COMMENTTYPE)
        {
            append("<div class='comment'>"+txt+"</div>", id);
        }
        else if(type==SUMMARYTYPE)
        {
            append("<div class='summary'>"+txt+"</div>", id);              
        }
        else if(type==WARNINGTYPE)
        {
            append("<div class='warning'>"+txt+"</div>", id);
        }
        else
        {
            append(txt,id);
        }
       
    }
    

    

    public void beginTable(String tableid, String[] headers, String id)
    {
        rowCount = 0;
        columnCount = headers.length;
        String tabla = "<table id=\""+tableid+"\" style=\"text-align: center; width:90%;\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\">"+
                " <tbody> "+                
                " <tr> ";
        
        for(int i=0; i<headers.length; i++)
        {
            tabla += "<td style=\"font-weight: bold; text-align: center; background-color: silver;\">"+headers[i]+"</td>";
        }
        
        tabla += "</tr>";
        tabla +="</tbody>";
        tabla +="</table>";
        
        append(tabla,id);
    }   
      
    public void addRowTable(Object[] line, String tableid)
    {
            String tabla = "<tr>";
                for(int i=0; i<line.length; i++)
                {
                    tabla +="<td>";
                        tabla += line[i];
                    tabla +="</td>";
                }
            tabla += "</tr>";
            
            append(tabla, tableid);
            
            rowCount += 1;
    }
      
    
    
    
    public void clear()
    {
         this.eventsMap.clear();
         this.setText(" <html> "
                + "<head>"
                + "<title>An example HTMLDocument</title>"
                + "<style type=\"text/css\">"
                + "  div {text-align:left; text-indent:10px; font-family:arial; font-size:10px}"
                + "  ul { color: grey; }"
                + "  .error{text-align:left; color:red; font-family:arial; font-size:10px}"
                + "  .title{text-align:left; color:blue; font-family:arial; font-weight:bold; font-size:14px}"
                + "  .comment{text-align:left; color:grey; font-family:arial;  font-style:italic; font-size:10px}"
                + "  .summary{text-align:left; color:green; font-family:arial; font-size:10px}"
                + "  .warning{text-align:left; color:black; background-color:yellow;font-weight:bold; font-family: Courier New,Courier,monospace; font-size:10px}"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div id='body'>"
                +"</div>"
                + "</body>"
                + "</html>");
       
    }
 

        

        /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                javax.swing.JFrame frame = new javax.swing.JFrame();
                HtmlLog htmlog = new HtmlLog();
                frame.add(htmlog);
                frame.pack();

                htmlog.append("<div align=\"left\" id=\"div1\"></div>");
                htmlog.append("<div id=\"div2\"><a href=\"regevnt001\">zzzzz</a></div>");
                htmlog.append("<h1>hola</h1>","div1");
                htmlog.append("<h3>adios</h3>","div1");
                
               
                htmlog.append("goodbye","div2",HtmlLog.ERRORTYPE);
                htmlog.append("goodbye","div2",HtmlLog.COMMENTTYPE);
                htmlog.append("goodbye","div2",HtmlLog.WARNINGTYPE);
                htmlog.append("goodbye","div2",HtmlLog.TITLETYPE);
                htmlog.append("goodbye","div2",HtmlLog.SUMMARYTYPE);
                
                htmlog.beginTable("table1",new String[]{"aaa","bbb","ccc"},"div2");
                htmlog.addRowTable(new Object[]{"12","perico","a<<<"},"table1");

                htmlog.registerLinkEvent("regevnt001", new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        //System.out.println("Something to be done here!");
                    }
                });
                
                frame.setVisible(true);
                frame.pack();
            }
        });
    }

    public void registerLinkEvent(String id, ActionListener actionListener) {
        eventsMap.put(id, actionListener);
    }
}
