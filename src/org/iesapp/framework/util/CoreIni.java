/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.iesapp.framework.pluggable.Closable;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 *
 * This has to be separate from CoreCfg because clients have not been added to
 * classpath yet.
 *
 */
public class CoreIni {
    public static final String COREINI = "config/core.ini";
    public static final String APPINI = "config/iesapp.ini";

    protected boolean debug = false;
    protected boolean core_anuncisOnStartup = false;
    protected boolean core_accessBlocked = false;
    protected String  core_PRODUCTID = "DEFAULT";
    protected String  core_sgdClientJar = "";
    protected String  core_iesdigitalClientJar = "";
    protected String  contextRoot = "";
    protected String  core_lang = "ca";
    private static CoreIni instance = null;

    private CoreIni(final String[] args, final Closable closable) {

        //Determine if we are in debug mode
        for (String s : args) {
            if (s.startsWith("-debug")) {
                debug = true;
                break;
            }
        }

        try {
            File file = new File(".");
            if (debug) {
                String tmp = file.getCanonicalPath();
                contextRoot = StringUtils.BeforeLast(tmp, File.separator);
                contextRoot = StringUtils.BeforeLast(contextRoot, File.separator);
            } else {
                contextRoot = file.getCanonicalPath();
            }
        } catch (IOException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        //System.out.println("debug & contextRoot "+debug+" "+contextRoot);

        File propfile = new File(contextRoot + File.separator + COREINI);
        if (!propfile.exists()) {
            saveCoreIni();
        }

        Properties props = new Properties();
        //try retrieve data from file
        try {
            FileInputStream filestream = new FileInputStream(contextRoot+File.separator+COREINI);
            props.load(filestream);

            String txt = props.getProperty("core.anuncisOnStartup", "1");
            core_anuncisOnStartup = Integer.parseInt(txt) > 0;

            txt = props.getProperty("core.accessBlocked", "0");
            core_accessBlocked = Integer.parseInt(txt) > 0;

            core_PRODUCTID = props.getProperty("core.productID", "");

            core_sgdClientJar = props.getProperty("core.sgdClientJar", "modules/sgd7.jar");
            core_iesdigitalClientJar = props.getProperty("core.iesdigitalClientJar", "modules/iesdigitalclient.jar");

            props.getProperty("core.lang", Locale.getDefault().getLanguage());
            filestream.close();
        } catch (IOException e) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, e);
        }
        
        //System.out.println("aaa");
        //Add clients to classpath
        File file1 = new File(contextRoot+File.separator+core_sgdClientJar);
        if(!file1.exists())
        {
            JOptionPane.showMessageDialog(null, "Can't find client specified in config/core.ini: \n"+file1.getAbsolutePath(), "GRAVE", JOptionPane.ERROR_MESSAGE);
            closable.quitApp();
        }
        //System.out.println("bbb");
        File file2 = new File(contextRoot+File.separator+core_iesdigitalClientJar);
        if(!file2.exists())
        {
            JOptionPane.showMessageDialog(null, "Can't find client specified in config/core.ini: \n"+file2.getAbsolutePath(), "GRAVE", JOptionPane.ERROR_MESSAGE);
            closable.quitApp();
        }        
        //System.out.println("cccc");
        JarClassLoader.getInstance().addJarToClasspath(file1);
        JarClassLoader.getInstance().addJarToClasspath(file2);
        //System.out.println("ddd");
        
        //Now CoreCfg is accesible for static members since libs are loaded
        CoreCfg.contextRoot = contextRoot;
        CoreCfg.core_PRODUCTID = core_PRODUCTID;
       // CoreCfg.core_lang = core_lang;
        CoreCfg.debug = debug;
        CoreCfg.core_accessBlocked = core_accessBlocked;
        CoreCfg.core_anuncisOnStartup = core_anuncisOnStartup;
         
    }

    private void saveCoreIni() {

        Properties props = new Properties();
        try {
            props.setProperty("core.accesBlocked", core_accessBlocked ? "1" : "0");
            props.setProperty("core.anuncisOnStartup", core_anuncisOnStartup ? "1" : "0");
            props.setProperty("core.productID", core_PRODUCTID);
            props.setProperty("core.lang", core_lang);
            props.setProperty("core.sgdClientJar", "modules/org-iesapp-clients-sgd7.jar");
            props.setProperty("core.iesdigitalClientJar", "modules/org-iesapp-clients-iesdigitalclient.jar");

            FileOutputStream filestream = new FileOutputStream(contextRoot+File.separator+COREINI);
            props.store(filestream, null);
            filestream.close();

        } catch (IOException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static CoreIni getInstance(final String[] args, final Closable closable)
    {
        //System.out.println("in instance is"+instance);
        if(instance==null)
        {
            instance = new CoreIni(args, closable);
            //System.out.println(" created new instance "+instance);
        }
        return instance;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isCore_anuncisOnStartup() {
        return core_anuncisOnStartup;
    }

    public boolean isCore_accessBlocked() {
        return core_accessBlocked;
    }

    public String getCore_PRODUCTID() {
        return core_PRODUCTID;
    }

    public String getCore_sgdClientJar() {
        return core_sgdClientJar;
    }

    public String getCore_iesdigitalClientJar() {
        return core_iesdigitalClientJar;
    }

    public String getContextRoot() {
        return contextRoot;
    }

    public String getCore_lang() {
        return core_lang;
    }
//    
//    public static void main(String[] args)
//    {
//        CoreIni instance1 = CoreIni.getInstance(new String[]{"-debug"}, null);
//        CoreIni instance2 = CoreIni.getInstance(new String[]{"-debug"}, null);
//    }

}
