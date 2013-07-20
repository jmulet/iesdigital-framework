/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.start;

import java.io.File;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.wizard.WizardPanelDescriptor;


public class StartupDescriptor0 extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "STEP0";
    private final StartupStep0 panel1;
    private boolean isMysqlInitiated = false;
    
    public StartupDescriptor0() {
        panel1 = new StartupStep0();
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel1);
    }
    
   
    @Override
    public void displayingPanel() {
       super.displayingPanel();      
       getWizard().setNextFinishButtonEnabled(true);
    }
    
    @Override
    public void aboutToHidePanel() {
        
    }
    
    
 
    @Override
    public Object getBackPanelDescriptor() {
          return  null;
    }  
    
    @Override
    public Object getNextPanelDescriptor() {
        
         if(panel1.getConfigurationType()==StartupStep0.DEDICATED_MYSQL)
         {
            CoreCfg.core_mysqlDBPrefix = ""; //not yet specified
            CoreCfg.core_mysqlHost = "localhost:3306";
            CoreCfg.core_mysqlUser = "root";
            CoreCfg.core_mysqlPasswd = "";
            CoreCfg.coreDB_mysqlParam = "zeroDateTimeBehavior=convertToNull";
            return StartupDescriptor1.IDENTIFIER;
         }
         else
         {
             
                     //Set data manually
                      CoreCfg.core_mysqlDB = ""; //not yet specified
                      CoreCfg.core_mysqlHost = "localhost:3307";
                      CoreCfg.core_mysqlUser = "root";
                      CoreCfg.core_mysqlPasswd = "";
                      CoreCfg.coreDB_mysqlParam = "zeroDateTimeBehavior=convertToNull";
                      //CoreCfg.saveEncryptedFile();
                      
                      return StartupDescriptor1.IDENTIFIER;
                  
         
         }
    }  
}
