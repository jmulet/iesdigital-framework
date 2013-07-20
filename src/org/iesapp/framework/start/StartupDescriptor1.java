/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.iesapp.framework.util.wizard.WizardPanelDescriptor;


public class StartupDescriptor1 extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "STEP1";
    private final StartupStep1 panel1;
    
    public StartupDescriptor1() {
        panel1 = new StartupStep1();
        panel1.addListenerConectaButton(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                 boolean q = panel1.tryConnection();
                 if(q) 
                 {
                     panel1.saveConnection();
                     getWizard().setNextFinishButtonEnabled(true);
                     
                 }
            }
        });
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel1);
    }
    
   
    @Override
    public void displayingPanel() {
       super.displayingPanel();
       panel1.updateValues();
       getWizard().setNextFinishButtonEnabled(false);
    }
    
    @Override
    public void aboutToHidePanel() {
        
    }
    
    
 
    @Override
    public Object getBackPanelDescriptor() {
          return  StartupDescriptor0.IDENTIFIER;
    }  
    
    @Override
    public Object getNextPanelDescriptor() {
        
         return StartupDescriptor2.IDENTIFIER;
    }  
}
