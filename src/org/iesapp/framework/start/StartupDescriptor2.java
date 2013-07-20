/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.iesapp.framework.util.wizard.WizardPanelDescriptor;


public class StartupDescriptor2 extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "STEP2";
    private final StartupStep2 panel2;
    
    public StartupDescriptor2() {
       panel2= new StartupStep2();
       panel2.registerActionButton1(new ActionListener() {

           @Override
            public void actionPerformed(ActionEvent e) {
                doCheck();
            }
        });
       
       panel2.registerActionList1( new ListSelectionListener() {

           @Override
            public void valueChanged(ListSelectionEvent e) {
                doCheck();
            }
        });
        
       
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
        getWizard().setNextFinishButtonEnabled(panel2.isValidDatabase());
        panel2.updateComponents();
     }
    
    @Override
    public void displayingPanel() {
       panel2.loadDBsInHost();
       doCheck();
    }
    
   @Override
    public void aboutToHidePanel() {
       panel2.saveToFile();
    }
         
    @Override
    public Object getBackPanelDescriptor() {
        return StartupDescriptor1.IDENTIFIER;
    }  
    
    @Override
     public Object getNextPanelDescriptor() {
       return StartupDescriptor3.IDENTIFIER;
    }

    
}
