/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 *
 * @author Josep
 */
public class JRTabbedPaneStation {
    
    protected final ArrayList<JRTabbedPane> register = new ArrayList<JRTabbedPane>();
    private JRTabbedPane selectedJRTabbedPane;

  
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public JRTabbedPaneStation()
    {
//        mainPanel.addMouseListener(new MouseAdapter(){
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                //System.out.println("mouse is clicked on maiPanel "+e);
//               
//            }
//        
//        });
    }
    
    public void registerJRTabbedPane(JRTabbedPane tabbedPane)
    {
        tabbedPane.setRegisteredTabbedPanes(register);
        tabbedPane.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
              
                //System.out.println("mouse is entered in "+e);
                JRTabbedPane jrTabbedPane = (JRTabbedPane) e.getSource();
                setSelectedJRTabbedPane(jrTabbedPane);
                jrTabbedPane.requestFocusInWindow();
            }
        
        });
         tabbedPane.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //System.out.println("pname:"+evt.getPropertyName());
                if(evt.getPropertyName().equals("indexForTabComponent"))
                {
                JRTabbedPane jrTabbedPane = (JRTabbedPane) evt.getSource();
                setSelectedJRTabbedPane(jrTabbedPane);
                jrTabbedPane.requestFocusInWindow();
                }
            }
        });
         
        register.add(tabbedPane);
    }
     
    public void unregisterJRTabbedPane(JRTabbedPane tabbedPane)
    {
        register.remove(tabbedPane);
    }

   
    public void setSelectedJRTabbedPane(JRTabbedPane selectedJRTabbedPane) {
        
        if(!selectedJRTabbedPane.equals(this.selectedJRTabbedPane))
        {
            propertyChangeSupport.firePropertyChange("selectedJRTabbedPane", this.selectedJRTabbedPane, selectedJRTabbedPane);
        }
        
        this.selectedJRTabbedPane = selectedJRTabbedPane;
        for(JRTabbedPane pane: register)
        {
             if(pane.equals(selectedJRTabbedPane))
            {
                pane.setFocus(true);
             }
            else
            {
                pane.setFocus(false);
            }
        }
    }

    public ArrayList<JRTabbedPane> getRegisteredJRTabbedPanes()
    {
        return register;
    }

    public int getTabCount() {
       int count = 0;
       for(JRTabbedPane jrTabbedPane: register)
       {
           count += jrTabbedPane.getTabCount();
       }
       return count;
    }
    
    
    
}
