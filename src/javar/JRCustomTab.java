/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javar;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import org.iesapp.framework.pluggable.TopModuleWindow;

/**
 *
 * @author Josep
 */
  public class JRCustomTab extends JPanel {
        public final boolean closable;
        public final String title;
        private JButton btnClose;
        private static final Icon CLOSE_TAB_ICON2 = new ImageIcon(JRTabbedPane.class.getResource("/org/iesapp/framework/icons/close2.gif"));
        private static final Icon CLOSE_TAB_ICON = new ImageIcon(JRTabbedPane.class.getResource("/org/iesapp/framework/icons/close.gif"));
        private final JLabel lblTitle;
        private final Color defaultBackground;
        private Timer timer;
  

      public JRCustomTab(final Component c, final JScrollPane jscp, String title, Icon icon, boolean closable) {
            this.title = title;
            this.closable = closable;
            // Create a FlowLayout that will space things 5px apart
            FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 0);
            this.setLayout(f);
            this.setOpaque(true);
            defaultBackground = this.getBackground();

            // Add a JLabel with title and the left-side tab icon

            lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("arial unicode", Font.PLAIN, 10));
            lblTitle.setOpaque(false);
            lblTitle.setIcon(icon);
            this.add(lblTitle);


            if (closable) {
                // Create a JButton for the close tab button    
                btnClose = new JButton(){

                    @Override
                    public void updateUI() {
                        this.setUI(new javax.swing.plaf.basic.BasicButtonUI());
                    }
                    
                };
                btnClose.setOpaque(false);
                btnClose.setToolTipText("Close");

                // Configure icon and rollover icon for button
                btnClose.setRolloverIcon(CLOSE_TAB_ICON);
                btnClose.setRolloverEnabled(true);
                btnClose.setIcon(CLOSE_TAB_ICON2);

                // Set border null so the button doesn't make the tab too big
                btnClose.setBorder(null);

                // Make sure the button can't get focus, otherwise it looks funny
                btnClose.setFocusable(false);

                // Put the panel together
                this.add(btnClose);

              
            }



            // Add a thin border to keep the image below the top edge of the tab
            // when the tab is selected
            this.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        }

    public void addActionListener(ActionListener listener) {
        if(btnClose!=null)
        {
            btnClose.addActionListener(listener);
        }
    }
    
    public void setIcon(ImageIcon icon)
    {
       lblTitle.setIcon(icon);
    }

    public void setStatus(byte status) {
        
        if(status==TopModuleWindow.STATUS_AWAKE)
        {
            //Flashing background
            
            timer = new Timer(100, new ActionListener(){
                private int cicles;

                @Override
                public void actionPerformed(ActionEvent e) {
                    cicles += 1;
                    int wave = (int) Math.round( 20*Math.sin(2*Math.PI*cicles/5) );
                    int wave2 = (int) Math.round( 30*Math.sin(2*Math.PI*cicles/5) );
                    int r = defaultBackground.getRed() + wave;
                    int g = defaultBackground.getGreen() + wave;
                    int b = defaultBackground.getBlue() + wave2;
                    Color currentBackground = new Color(r,g,b);
                    JRCustomTab.this.setBackground(currentBackground);
                }
                
            });
            timer.start();
            lblTitle.setForeground(Color.BLACK);
        }
        else if(status==TopModuleWindow.STATUS_NORMAL)
        {
            if(timer!=null)
            {
                timer.stop();
            }
            this.setBackground(defaultBackground);
            lblTitle.setForeground(Color.BLACK);
        }
        else if(status==TopModuleWindow.STATUS_SLEEPING)
        {
            if(timer!=null)
            {
                timer.stop();
            }
            this.setBackground(defaultBackground);
            lblTitle.setForeground(Color.GRAY);
        }
    }
 }