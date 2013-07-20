/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.start;

import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.wizard.Wizard;
import org.iesapp.framework.util.wizard.WizardPanelDescriptor;

/**
 *
 * @author Josep
 */
public class StartupWiz {
    private static Wizard wizard;
    protected static CoreCfg tmpClient;
    public static int configType = StartupStep0.DEDICATED_MYSQL;

    public static CoreCfg getTmpClient() {
        return tmpClient;
    }
  
    public StartupWiz()       
    {
        tmpClient = new CoreCfg(new String[]{}, null);
        CoreCfg.readEncryptedFile();
        
        wizard = new org.iesapp.framework.util.wizard.Wizard();
        wizard.getDialog().setTitle("Configuració de iesDigital "+CoreCfg.VERSION);
        
        WizardPanelDescriptor descriptor0 = new StartupDescriptor0();
        wizard.registerWizardPanel(StartupDescriptor0.IDENTIFIER,"Tipus configuració", descriptor0);
        
        WizardPanelDescriptor descriptor1 = new StartupDescriptor1();
        wizard.registerWizardPanel(StartupDescriptor1.IDENTIFIER,"Connexió a mysql", descriptor1);

        WizardPanelDescriptor descriptor2 = new StartupDescriptor2();
        wizard.registerWizardPanel(StartupDescriptor2.IDENTIFIER, "Base de dades", descriptor2);
        
        WizardPanelDescriptor descriptor3 = new StartupDescriptor3();
        wizard.registerWizardPanel(StartupDescriptor3.IDENTIFIER, "Curs acadèmic", descriptor3);

        WizardPanelDescriptor descriptor4 = new StartupDescriptor4();
        wizard.registerWizardPanel(StartupDescriptor4.IDENTIFIER, "Connexió a SGD", descriptor4);
 
        WizardPanelDescriptor descriptor5 = new StartupDescriptor5();
        wizard.registerWizardPanel(StartupDescriptor5.IDENTIFIER, "Contrasenya administrador", descriptor5);
        
       
        wizard.setCurrentPanel(StartupDescriptor0.IDENTIFIER);
        
    }
    
    public static org.iesapp.framework.util.wizard.Wizard getWizard()
    {
        return wizard;
    }
    

    public int showModalDialog() {
        int showModalDialog = wizard.showModalDialog();
        tmpClient.close();
        tmpClient = null;
        return showModalDialog;
    }
}
