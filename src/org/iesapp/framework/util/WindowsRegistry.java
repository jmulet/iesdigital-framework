/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class WindowsRegistry {
   /**
     * 
     * @param location path in the registry
     * @param key registry key
     * @return registry value or null if not found
     */
    
    public static final String USERFOLDERS ="HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders";
    
    
    public static String readRegistry(String location, String key)
    {
        String output = null;
        try{
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("reg query \""+
                location + "\" /v "+ key);
            } catch (IOException ex) {
                Logger.getLogger(WindowsRegistry.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            InputStream is = process.getInputStream();
            StringBuilder sw = new StringBuilder();
            
               int c;
                while((c = is.read()) !=-1) {
                sw.append((char) c);
            }
           
              output = sw.toString();
              if(output!=null && !output.isEmpty()) {
                output = StringUtils.AfterFirst(output, "REG_EXPAND_SZ").trim();
            }
              
        }
        catch(IOException e){
                
                
        }
            
        return output;
    }
    
    
//    /**
//    * @param args the command line arguments
//    */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//               String kk = readRegistry(WindowsRegistry.USERFOLDERS,"Startup") ;
//               //System.out.println(kk);
//               kk = kk.replace("%USERPROFILE%", System.getProperty("user.home"));
//               //System.out.println(kk);
//            }
//        });
//    }
    
}
