/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;

/**
 *
 * @author Josep
 */
public class NotificationPanel extends JDialog {
    private final JTextPane jTextPane1;

    public NotificationPanel(JDialog parent) {
        super(parent, false);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setSize(100, 80);
        this.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new LineBorder(Color.GRAY, 3)); 
        jTextPane1 = new JTextPane();
        jTextPane1.setFont(new Font("arial unicode", Font.BOLD, 12));
        jTextPane1.setEditable(false);
        panel.add(jTextPane1);
        this.add(panel);
       
    }
    
    public void setText(String text)
    {
        jTextPane1.setText(text);
    }
    
}
