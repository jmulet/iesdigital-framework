/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.start;

import org.iesapp.framework.util.wizard.WizardPanelDescriptor;


public class StartupDescriptor5 extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "STEP5";
    private final StartupStep5 panel2;
    
    public StartupDescriptor5() {
       panel2= new StartupStep5();
       panel2.registerKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                doCheck();
            }

           
        });
        
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel2);
    }
    
   private void doCheck() {
         getWizard().setNextFinishButtonEnabled(panel2.doCheck());
    }
   
   
     @Override
    public void displayingPanel() {
         getWizard().setNextFinishButtonEnabled(false);
    }
        
   @Override
    public void aboutToHidePanel() {
      panel2.doSave();
    }
  
    
    @Override
     public Object getNextPanelDescriptor() {
        return FINISH;
    }

    @Override
    public Object getBackPanelDescriptor() {
        return StartupDescriptor4.IDENTIFIER;
 
    }  
    
}
