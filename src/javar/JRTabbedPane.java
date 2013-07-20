/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javar;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import org.iesapp.framework.pluggable.TopModuleRegistry;
import org.iesapp.framework.pluggable.TopModuleWindow;

/**
 *
 * @author Josep
 */
public class JRTabbedPane extends DnDTabbedPane {

    private ArrayList<JRTabbedPane> registeredTabbedPanes;
    protected boolean hasFocus;
    protected String locationId;
    /**
     * Creates new form JRTabbedPane
     */
    public JRTabbedPane() {
        this.locationId = "center";
        initComponents();
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                tabClicked(e);
            }
        });
        
        registeredTabbedPanes = new ArrayList<JRTabbedPane>();
        registeredTabbedPanes.add(this);
    }
  
 
    
 
    public void setRegisteredTabbedPanes(ArrayList<JRTabbedPane> list)
    {
        this.registeredTabbedPanes = list;
    }
    

    @Override
    public void updateUI() {
        //super.updateUI();
          this.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI(){
          @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
//                if(tabPlacement<0 || JRTabbedPane.this.getTabCount()==1)
//                {
//                    return 0;
//                }
                return super.calculateTabAreaHeight(tabPlacement, horizRunCount, maxTabHeight);
            }
            
        }); 
    }
    
    
    protected void tabClicked(MouseEvent e)
    {
        if(e.getButton()!= MouseEvent.BUTTON1 && e.getClickCount()==1)
        {
            int pos = this.getSelectedIndex();
            if(pos<0){
                return;
            }
            JRCustomTab ct = (JRCustomTab) this.getTabComponentAt(pos);
            jMenuItem1.setEnabled(ct.closable);
            jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
        }
        
        //Trigger an event saying that this tabbedpane has recieved focus
        hasFocus = true;
        for(JRTabbedPane jrTabbedPane1: this.registeredTabbedPanes)
        {
            if(!jrTabbedPane1.equals(this))
            {
                jrTabbedPane1.setFocus(false);
            }
        }
    }
   
    
    public Container getContentContainer()
    {
        return this;
    }
    
    public void addTab(final JComponent c, final String title, final Icon icon, boolean closable) {
    // Add the tab to the pane without any label
    final JScrollPane jscp = new JScrollPane(c);
    this.addTab(null, jscp);
    int pos = this.indexOfComponent(jscp);

  
    // Make a small JPanel with the layout and make it non-opaque
    JRCustomTab pnlTab = new JRCustomTab(c, jscp, title, icon, closable);
    // Add the listener that removes the tab
    ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                aboutToClose(e, c);
                // The component parameter must be declared "final" so that it can be
                // referenced in the anonymous listener class like this.
                if (c instanceof TopModuleWindow) {
                    ((TopModuleWindow) c).dispose();
                }
                //Ara ha de cercar quin dels panels registrats conte aquest component
                for (JRTabbedPane jrTabbedPane1 : JRTabbedPane.this.registeredTabbedPanes) {
                    for (int i = 0; i < jrTabbedPane1.getTabCount(); i++) {
                        if (jrTabbedPane1.getComponentAt(i).equals(jscp)) {
                            jrTabbedPane1.remove(jscp);
                            break;
                        }
                    }

                }
            }
        };
        pnlTab.addActionListener(listener);
    // Now assign the component for the tab
    this.setTabComponentAt(pos, pnlTab);

    // Optionally bring the new tab to the front
    this.setSelectedComponent(jscp);

    //-------------------------------------------------------------
    // Bonus: Adding a <Ctrl-W> keystroke binding to close the tab
    //-------------------------------------------------------------
    AbstractAction closeTabAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            aboutToClose(e, c);
            if(c instanceof TopModuleWindow)
            {
                ((TopModuleWindow) c).dispose();
            }
            
            //Ara ha de cercar quin dels panels registrats conte aquest component
            for(JRTabbedPane jrTabbedPane1: JRTabbedPane.this.registeredTabbedPanes)
            {
                for(int i=0; i<jrTabbedPane1.getTabCount(); i++)
                {
                    if(jrTabbedPane1.getComponentAt(i).equals(jscp))
                    {
                        jrTabbedPane1.remove(jscp);
                        break;
                    }
                }
                 
            }
           
        }

            
    };

    
    // Create a keystroke
    KeyStroke controlW = KeyStroke.getKeyStroke("control W");

    // Get the appropriate input map using the JComponent constants.
    // This one works well when the component is a container. 
    InputMap inputMap = c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    // Add the key binding for the keystroke to the action name
    inputMap.put(controlW, "closeTab");

   // Now add a single binding for the action name to the anonymous action
    c.getActionMap().put("closeTab", closeTabAction);
  
  
 }

//Overide this method if necessary
    public void aboutToClose(ActionEvent e, Component c) {
             
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        jMenuItem1.setText("Close this");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setText("Close All");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        jMenuItem3.setText("Close other");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem3);
    }// </editor-fold>//GEN-END:initComponents

    
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        safeRemoveTab(this.getSelectedIndex());
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        //Component current = this.getSelectedComponent();
        int n = this.getTabCount();
        int totaldeleted = 0;
        for(int i=0; i<n; i++)
        {
            boolean deleted = safeRemoveTab(i-totaldeleted);
            if(deleted)
            {
                totaldeleted += 1;
                n -= 1;
            }
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        Component current = this.getSelectedComponent();
        //Component current = this.getSelectedComponent();
        int n = this.getTabCount();
        int totaldeleted = 0;
        for(int i=0; i<n; i++)
        {
            Component comp = this.getComponentAt(i);
            if(comp!=current)
            {
            boolean deleted = safeRemoveTab(i-totaldeleted);
            if(deleted)
            {
                totaldeleted += 1;
                n -= 1;
            }
            }
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPopupMenu jPopupMenu1;
    // End of variables declaration//GEN-END:variables

    private boolean safeRemoveTab(int i) {
        boolean deleted = false;
        this.setSelectedIndex(i);
        JRCustomTab tabComponentAt = (JRCustomTab) this.getTabComponentAt(i);
        if (tabComponentAt.closable) {
            Component comp = this.getSelectedComponent();
            JScrollPane jscrollpane = (JScrollPane) comp;
            Component selectedComponent = jscrollpane.getViewport().getView();
            if (selectedComponent instanceof TopModuleWindow) {
                //Make sure to remove it from registry before erasing
                 TopModuleRegistry.remove(((TopModuleWindow) selectedComponent).getName());
                ((TopModuleWindow) selectedComponent).dispose();
            }
            this.removeTabAt(i);
            deleted = true;
            
        }
        return deleted;
    }

    public boolean isFocus() {
        return hasFocus;
    }

    public void setFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
        if(hasFocus)
        {
            setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        }
        else
        {
            this.setBorder(null);
        }
    }

    
    public TopModuleWindow getTopModuleWindowAt(int i)
    {
        TopModuleWindow win= null;
        if(i<0 || i>=this.getTabCount())
        {
            return win;
        }
        
        JScrollPane jscrollpane = (JScrollPane) this.getComponentAt(i);
        if(jscrollpane!=null)
        {
            win = (TopModuleWindow) jscrollpane.getViewport().getView();
        }
        return win;
    }

    public void setSelectedTopModuleWindow(TopModuleWindow win)
    {
       for(int i=0; i<this.getTabCount(); i++)
       {
         if(this.getTopModuleWindowAt(i).equals(win))
         {
             this.setSelectedIndex(i);
             break;
         }
        
       }        
    }

    public TopModuleWindow getSelectedTopModuleWindow()
    {
       return this.getTopModuleWindowAt(this.getSelectedIndex());
    }
//    private static class JVertLabel extends JComponent {
//
//        private String text;
//
//        public JVertLabel(String s) {
//            text = s;
//        }
//
//        @Override
//        public void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            Graphics2D g2d = (Graphics2D) g;
//
//            g2d.rotate(Math.toRadians(270.0));
//            g2d.drawString(text, 0, 0);
//        }
//    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

}
