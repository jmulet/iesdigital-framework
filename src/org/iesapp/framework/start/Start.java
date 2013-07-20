/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.start;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.iesapp.framework.pluggable.Closable;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.CoreIni;

/**
 *
 * @author Josep
 */
public final class Start {

    public static void startup(boolean mustShow, Closable closable) {
        
        //CoreCfg.preInitialize();
        File file = new File(CoreCfg.contextRoot+File.separator+CoreIni.APPINI);

        //System.out.println("i am in startup and file is"+file.getAbsolutePath());
        //Reads the encrypted file with connection information   
        if (file.exists()) {
            CoreCfg.readEncryptedFile();
            //System.out.println("i read the encrypted file");
        }

        if (mustShow || (!file.exists() || (file.exists() && CoreCfg.core_mysqlDBPrefix.isEmpty()))) {

            String lookandfeel = "Nimbus";

            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if (lookandfeel.equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        UIManager.put("nimbusDisabledText", new java.awt.Color(100, 70, 60));
                        break;
                    }
                }
            } catch (Exception e) {
                // handle exception
            }  

            //System.out.println("openining wizard");
            StartupWiz dlg = new StartupWiz();
            int ret = dlg.showModalDialog();
   
            //check again
            if (!file.exists()) {
                JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), "El programa no està configurat i es tancarà.");
                closable.quitApp();
            }
        }





    }
}
