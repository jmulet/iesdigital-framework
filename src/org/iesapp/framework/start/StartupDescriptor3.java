/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.iesapp.framework.util.wizard.WizardPanelDescriptor;


public class StartupDescriptor3 extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "STEP3";
    private final StartupStep3 panel2;
    
    public StartupDescriptor3() {
       panel2= new StartupStep3();
    
       panel2.registerListenerRadioButtons(new ActionListener() {

           @Override
            public void actionPerformed(ActionEvent e) {
               panel2.updateComponents();
            }
        });
        
        
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel2);
    }
    
     @Override
    public void displayingPanel() {
       panel2.onShow();
    }
        
   @Override
    public void aboutToHidePanel() {
       panel2.doSave();
    }
 
    @Override
    public Object getBackPanelDescriptor() {
        
         return StartupDescriptor2.IDENTIFIER;
       
    }  

    @Override
     public Object getNextPanelDescriptor() {
         return StartupDescriptor4.IDENTIFIER;
    }
    
    
}
