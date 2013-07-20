/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.start;

import org.iesapp.framework.util.wizard.WizardPanelDescriptor;


public class StartupDescriptor4 extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "STEP4";
    private final StartupStep4 panel2;
    
    public StartupDescriptor4() {
       panel2= new StartupStep4();
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
  
    
    public Object StartupDescriptor2() {
         return StartupDescriptor4.IDENTIFIER;
    }
    
    @Override
     public Object getNextPanelDescriptor() {
        return StartupDescriptor5.IDENTIFIER;
    }

    @Override
    public Object getBackPanelDescriptor() {
        return StartupDescriptor3.IDENTIFIER;
 
    }  
    
}
