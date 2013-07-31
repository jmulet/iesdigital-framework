package org.iesapp.framework.util;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.help.DefaultHelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import org.iesapp.clients.iesdigital.DefaultConfigTableEntry;
import org.iesapp.clients.sgd7.SgdClient;
import org.iesapp.clients.sgd7.base.SgdBase;
import org.iesapp.clients.sgd7.profesores.Profesores;
import org.iesapp.database.MyDatabase;
import org.iesapp.framework.data.User;
import org.iesapp.framework.pluggable.Closable;
import org.iesapp.framework.pluggable.TopModuleWindow;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class CoreCfg extends org.iesapp.clients.iesdigital.ICoreData{

    //Constants
    public static final String VERSION = "4.5";
    public static final String BUILD = "20130610";
    public static String APPINI = "config/iesapp.ini";
//    public static String COREINI = "config/core.ini";
//    public static String FITXESINI = "config/fitxes.ini";
//    public static String GUARDIESINI = "config/guardies.ini";
//    public static String RESERVESINI = "config/reserves.ini";
//    public static String SYNCSGDINI = "config/syncsgd.ini";
    public static String LOGININI = ".iesDigitalLogin";
    public static String SCRIPTMAINDB = "config/iesdigital_blank.sql";
    public static String SCRIPTXXXXDB = "config/iesdigitalxxxx_blank.sql";
    public static int DBTYPE = 2; //1=access(NO LONGER SUPPORTED); 2=mysql
    //Must be obtained from configTableMap
    public static String cloudBaseURL = "http://localhost:8080/pdaweb/webresources/cloud/";
    public static String prefix = "sig_";
    public static final String dl = "'";
    public static boolean admin;
    //Stuff from .iesapp.ini
    public static String core_pwdSU = "";
    public static String core_mysqlUser = "root";
    public static String core_mysqlPasswd = "";
    public static String core_mysqlHost = "localhost:3306";
    public static String core_mysqlDB = ""; //IMPORTANT --> MUST BE EMPTY FOR CONNECTIONS TO SUCCED
    //public static String core_mysqlDBPrefix = "iesdigital"; //--> IT IS DEFINED IN ANCESTOR ICOREDATA
    public static String core_accessFile = "";
    //Stuff from core.ini
    public static boolean core_anuncisOnStartup = false;
    public static boolean core_accessBlocked = false;
    //Stuff from DATABASE CONFIG TABLE
    
    public static boolean coreDB_copiaLocal;
    public static boolean coreDB_copiaExterna;
    public static String coreDB_sgdPasswd = "";
    public static String coreDB_sgdUser = "root";
    public static int coreDB_autorefresc = 0;
    public static String coreDB_doc = "";
    public static int coreDB_guardiaAntelacio = 0;
    public static int coreDB_tardaAntelacio = 0;
    public static int coreDB_EditFitxes = 1;
    public static String coreDB_FitxesDataPath = "";
    public static int coreDB_timeoutSU = 0;
    public static String coreDB_sgdHost = "localhost:3306";
    public static String coreDB_sgdDB = "curso";
    public static int coreDB_reservesAntelacio = 15;
    public static String tipPasswd = "";
    public static ArrayList<String> lastLogins;
    //elements de la base de dades
    //Encapsulated Lazy-initialization
    public static String coreDB_sgdParam = "zeroDateTimeBehavior=convertToNull";
    public static String coreDB_mysqlParam = "zeroDateTimeBehavior=convertToNull";
    
    private MyDatabase mysql;
    private MyDatabase sgd;
    private User userInfo;
    public static String core_pwdPREF = "";
    public static String core_pwdGUARD = "";
    public static String osName;
    //public static int anyAcademic;  //as determined in configTable
    //Two common databases
  
    //public static String contextRoot = "";
    public static boolean debug = false;
    public String core_lang = "ca";
    protected Properties iniProperties;
    protected SgdClient sgdClient;
    private String abrev;
    protected IesClient iesClient;
    
    protected javax.help.HelpBroker mainHelpBroker;
    protected javax.help.HelpSet mainHelpSet;
    private final long startTime;
    

    //Default constructor
    public CoreCfg(final String[] args, Closable closable) {
        //get preInitialization
        //initialize();
        startTime = System.currentTimeMillis();
    }

    public int initialize() {
        
        osName = System.getProperty("os.name");
        try {
            InetAddress ownIP = InetAddress.getLocalHost();
            ip = ownIP.getHostAddress();
            netbios = ownIP.getCanonicalHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        //Redefineix el user.home per a que concideixi amb %USERPROFILE%
        System.setProperty("user.home", System.getenv().get("USERPROFILE"));

        //Initialize main database it is initialized with the PREFIX database name
        //Then we switch to the current year using setCatalog(dbThisYear);
        boolean result = startFitxesDB();
        if (!result) {
            JScrollPane scrollPane = new JScrollPane();
            JTextArea area = new JTextArea();
            area.setWrapStyleWord(true);
            area.setLineWrap(true);
            area.setAutoscrolls(true);
            area.setSize(300, 200);
            scrollPane.setViewportView(area);
            String msg = "L'aplicació no pot engegar-se perquè la "
                        + "conexió amb la base de dades iesDigital ha fallat. Comproveu el funcionament "
                        + "del servidor i la configuració amb l'opció d'engegada -c\n";
            try {
                msg +=  mysql.getDocument().getText(0, mysql.getDocument().getLength()-1);
            } catch (BadLocationException ex) {
                Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
            }
            area.setText(msg);
            JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), scrollPane, "GRAVE", JOptionPane.ERROR_MESSAGE);
     
            return -1;
        }

        //And constructs the configMap from database
        //This has been updated since now reads info from the PREFIX database
        //and not from year to year
        readDatabaseCfg();
 
//       Link creation is deprecated since 4.3    
//        //Gestiona el funcionament del panell d'anuncis
//        if(CoreCfg.core_anuncisOnStartup) {
//            CoreCfg.createLink(false);
//        }

        lastLogins = new ArrayList<String>();
        readLoginIni();


        //Initialize sgd databases
        getMysql().setCatalog(CoreCfg.core_mysqlDB);

        sgd = new MyDatabase(CoreCfg.coreDB_sgdHost, CoreCfg.coreDB_sgdDB,
                CoreCfg.coreDB_sgdUser, CoreCfg.coreDB_sgdPasswd, CoreCfg.coreDB_sgdParam);

        boolean q2 = getSgd().connect();
        //Check connection
        if (!q2) {
            JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), "Alerta. No hi ha connexió amb la base SGD.");
        }

        //Check version
        if (StringUtils.compare(VERSION, (String) configTableMap.get("minVersion")) < 0) {
            JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), "Ho sentim. Estau emprant una versió massa antiga\n"
                    + "del programa. Assegureu-vos d'actualitzar-lo.");
            return -1;
        }
        iesClient = new IesClient(this);
        //Writes in javaversion table, which java is using this machine
        //if the parameter enableRegSystemInfo is yes in configtablemap 
        //Only if enabled
        registerSystemInfo();

        //Initialize databases loggers
        getSgd().setMaxLogLines((Integer) configTableMap.get("mysqlLoggerMaxLines"));
        getMysql().setMaxLogLines((Integer) configTableMap.get("mysqlLoggerMaxLines"));

//        SgdBase.setMysql(mysql);
//        SgdBase.setSgd(sgd);
//        SgdBase.setCurrentDBPrefix((String) CoreCfg.configTableMap.get("sgdDBPrefix"));
//        SgdBase.setCurrentYear(anyAcademic);
        //SgdBase.initialize();

        //Use sgdClient facade
        sgdClient = new SgdClient(mysql, sgd, anyAcademic, 
                (String) CoreCfg.configTableMap.get("sgdDBPrefix"),
                "config", ip, netbios);


        return 0;
    }

    public static void readEncryptedFile() {
        //Try to read and decrypt
        FileInputStream fis = null;
        try {
            Encryption enc = new Encryption();
            fis = new FileInputStream(contextRoot+File.separator+APPINI);
            DataInputStream in = new DataInputStream(fis);

            String readUTF = in.readUTF();
            core_mysqlUser = enc.decrypt(readUTF).trim();

            readUTF = in.readUTF();
            core_mysqlPasswd = enc.decrypt(readUTF).trim();
            if (core_mysqlPasswd == null) {
                core_mysqlPasswd = "";
            }

            readUTF = in.readUTF();
            core_mysqlHost = enc.decrypt(readUTF).trim();

            readUTF = in.readUTF();
            core_mysqlDBPrefix = enc.decrypt(readUTF).trim();    //This is only the prefix

            readUTF = in.readUTF();
            coreDB_mysqlParam = enc.decrypt(readUTF).trim();  
            
            fis.close();
            in.close();


        } catch (GeneralSecurityException ex) {
            //System.out.println(ex);
        } catch (IOException ex) {
            //System.out.println(ex);
        }

    }

    public static void saveEncryptedFile() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(contextRoot+File.separator+APPINI);
            DataOutputStream out = new DataOutputStream(fos);

            Encryption enc = new Encryption();
            out.writeUTF(enc.encrypt(CoreCfg.core_mysqlUser));
            out.writeUTF(enc.encrypt(CoreCfg.core_mysqlPasswd));
            out.writeUTF(enc.encrypt(CoreCfg.core_mysqlHost));
            out.writeUTF(enc.encrypt(CoreCfg.core_mysqlDBPrefix));
            out.writeUTF(enc.encrypt(CoreCfg.coreDB_mysqlParam));
            fos.close();
            out.close();

        } catch (GeneralSecurityException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * updated to read from the new common database Creates the map containing
     * values of the configuration table in db
     */
    public void readDatabaseCfg() {

        //defaultConfigTableMap is defined and populated in ICoreData
        if (getMysql().isClosed()) {
            return;
        }  //do nothing if there is no connection

        String SQL = "SELECT property, value, castTo FROM `" + CoreCfg.core_mysqlDBPrefix + "`.configuracio";

        try {
            Statement st = getMysql().createStatement();
            ResultSet rs = getMysql().getResultSet(SQL, st);
            while (rs != null && rs.next()) {
                String str = StringUtils.noNull(rs.getString("value")).trim();
                Object obj = null;
                String type = rs.getString("castTo");
                if (type.equalsIgnoreCase("int")) {

                    obj = Integer.parseInt(str);
                } else if (type.equalsIgnoreCase("Boolean")) {
                    if (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("y")) {
                        obj = true;
                    } else {
                        obj = false;
                    }
                } else {
                    obj = str;
                }
                configTableMap.put(rs.getString("property"), obj);

            }
            if (rs != null) {
                rs.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Compare both maps and perform inserts
        for (String key : defaultConfigTableMap.keySet()) {
            if (!configTableMap.containsKey(key)) {
                //This new key must be saved into the database
                DefaultConfigTableEntry entry = (DefaultConfigTableEntry) defaultConfigTableMap.get(key);
                String SQL1 = "INSERT INTO `" + CoreCfg.core_mysqlDBPrefix + "`.configuracio (category,property,"
                        + "value,castTo,description) VALUES('" + entry.getCategory() + "','" + key
                        + "','" + entry.getValue() + "','" + entry.getCastTo() + "',?)";
                getMysql().preparedUpdate(SQL1, new Object[]{entry.getDescription()});
                Object obj = entry.getValue();
                configTableMap.put(key, obj);
            }
        }
        defaultConfigTableMap.clear();


        //From now on choose the correct database as default
        //Create Alias for the most commonly used variables
         if (configTableMap != null && configTableMap.containsKey("anyIniciCurs")) {
            coreDB_sgdHost = (String) configTableMap.get("sgdHost");
            coreDB_sgdUser = (String) configTableMap.get("sgdUser");
            coreDB_sgdPasswd = (String) configTableMap.get("sgdPasswd");
            coreDB_sgdDB = (String) configTableMap.get("sgdDBPrefix") + configTableMap.get("anyIniciCurs");
            core_pwdSU = (String) configTableMap.get("adminPwd");
            cloudBaseURL = (String) configTableMap.get("cloudBaseURL");
            core_pwdPREF = (String) configTableMap.get("prefPwd");
            core_pwdGUARD = (String) configTableMap.get("guardPwd");

            String any = "" + configTableMap.get("anyIniciCurs");
            core_mysqlDB = core_mysqlDBPrefix + any;

            getMysql().setCatalog(core_mysqlDB);
            anyAcademic = (int) Double.parseDouble(any);
        }
    }

    
    public void updateDatabaseCfg(String property, Object value) {
        String SQL1 = "UPDATE `" + core_mysqlDBPrefix + "`.`configuracio` SET value=? WHERE property=?";
        getMysql().preparedUpdate(SQL1, new Object[]{value, property});
    }

    public void updateDatabaseCfg() {
        for (String property : configTableMap.keySet()) {
            updateDatabaseCfg(property, configTableMap.get(property));
        }
    }

    private void readLoginIni() {
        
        String realLOGIN = System.getProperty("user.home") + "\\" + LOGININI;

        File propfile = new File(realLOGIN);
        if (!propfile.exists()) {
            saveLoginIni();
        }


        Properties props = new Properties();
        //try retrieve data from file
        try {
            FileInputStream filestream = new FileInputStream(realLOGIN);
            props.load(filestream);
            tipPasswd = props.getProperty("passwdHelp");
            String aux = props.getProperty("lastLogin");
            lastLogins = StringUtils.parseStringToArray(aux, ",", StringUtils.CASE_UPPER);
            core_lang = props.getProperty("lang", core_lang);
            filestream.close();
        } catch (IOException e) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Loads the iesDigital.ini configuration file Devoted to store
     * user-dependent information
     *
     * @return
     */
    public synchronized Properties getUserPreferences() {
        if (iniProperties == null) {
            String realLOGIN = System.getProperty("user.home") + "\\" + LOGININI;

            File propfile = new File(realLOGIN);
            if (!propfile.exists()) {
                saveLoginIni();
            }

            iniProperties = new Properties();
            //try retrieve data from file
            try {
                FileInputStream filestream = new FileInputStream(realLOGIN);
                iniProperties.load(filestream);
                filestream.close();
            } catch (IOException e) {
                Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return iniProperties;
    }

    public synchronized void saveIniProperties() {
        String realLOGIN = System.getProperty("user.home") + "\\" + LOGININI;

        try {
            if (iniProperties != null) {
                FileOutputStream filestream = new FileOutputStream(realLOGIN);
                iniProperties.store(filestream, null);
                filestream.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void setUserPreferences(String field, String value) {

        if (iniProperties == null) {
            iniProperties = getUserPreferences();
        }
        iniProperties.setProperty(field, value);

    }

    private void saveLoginIni() {
        String realLOGIN = System.getProperty("user.home") + "\\" + LOGININI;
        Properties props = new Properties();
        try {
            props.setProperty("passwdHelp", tipPasswd);

            String aux = "";
            if (lastLogins != null) {
                int nmax = lastLogins.size() < 6 ? lastLogins.size() : 5;
                for (int i = 0; i < nmax; i++) {
                    aux += lastLogins.get(i) + ",";
                }

                aux = StringUtils.BeforeLast(aux, ",");
            }
            props.setProperty("lastLogin", aux);

            props.setProperty("lang", core_lang);

            FileOutputStream filestream = new FileOutputStream(realLOGIN);
            props.store(filestream, null);
            filestream.close();

        } catch (IOException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

   
    public void close() {
        //Release databases
        if (getMysql() != null) {
            getMysql().close();
        }
        if (getSgd() != null) {
            getSgd().close();
        }
        //Release sgd
        SgdBase.close();
    }

    public void resetConnection() {

        if (getMysql() != null) {
            getMysql().close();
        }
        if (getSgd() != null) {
            getSgd().close();
        }

        //Initialize main database
        mysql = new MyDatabase(CoreCfg.core_mysqlHost, CoreCfg.core_mysqlDB,
                CoreCfg.core_mysqlUser, CoreCfg.core_mysqlPasswd, CoreCfg.coreDB_mysqlParam);
        boolean q1 = getMysql().connect();
        
        readDatabaseCfg();


        //Initialize sgd databases
        sgd = new MyDatabase(CoreCfg.coreDB_sgdHost, CoreCfg.coreDB_sgdDB,
                CoreCfg.coreDB_sgdUser, CoreCfg.coreDB_sgdPasswd, CoreCfg.coreDB_sgdParam);
        boolean q2 = getSgd().connect();

        //Initialize sgd module
//        org.iesapp.clients.sgd7.base.SgdBase.setMysql((MyDatabase) getMysql());
//        org.iesapp.clients.sgd7.base.SgdBase.setSgd((MyDatabase) getSgd());
//        org.iesapp.clients.sgd7.base.SgdBase.initialize();
    }

    /**
     * Crea un link perque s'executi el tauler d'anuncis a l'inici de la sessio
     * deprecated since 4.3
     *
     * @param force
     */
//    public static void createLink(boolean force) {
//        //Determina  menu inicio de la versio de windows
//        if(startupPath==null)
//        {
//            startupPath = WindowsRegistry.readRegistry(WindowsRegistry.USERFOLDERS, "Startup");
//            startupPath = startupPath.replace("%USERPROFILE%", System.getProperty("user.home"));
//        }
//        
//        java.io.File fpath = new java.io.File(startupPath);
//    
//        String propietat = CoreCfg.getIniProperties().getProperty("anuncisStartup", "UNDEFINED");
//       
//        if (fpath.exists() && (force || propietat.contains("UNDEFINED"))) {
//            java.io.File fpath2 = new java.io.File(startupPath + "\\anuncis.lnk");
//          
//            if (!fpath2.exists()) {
//                String root = "";
//                try {
//                    root = new File("./").getCanonicalPath();
//                } catch (IOException ex) {
//                    Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
//                }
////               try{
////                JShellLink link = new JShellLink();
////                link.setFolder(startupPath + "\\anuncis.lnk");
////                link.setName("anuncis");
////                link.setPath(root + "\\bin\\anuncis.exe");
////                link.setWorkingDirectory(root);
////                
////                /* Aquesta creació de links no funciona per a sistemes 64 bits */
////                
////                    link.save();               
////                    
////                }
////                catch(java.lang.UnsatisfiedLinkError e)                    
//                {
//                    //TODO: For 64 bits an alternative method should be divised.
//                    //WE PROVIDE ALONG anuncis.exe a file anuncis.lnk and we perform a copy
//                    CopyFile.copyfile(root + "\\bin\\anuncis.lnk", startupPath + "\\anuncis.lnk");    
//                }
////                catch(java.lang.RuntimeException e)
////                {
////                    //TODO: For 64 bits an alternative method should be divised.
////                    //WE PROVIDE ALONG anuncis.exe a file anuncis.lnk and we perform a copy
////                    CopyFile.copyfile(root + "\\bin\\anuncis.lnk", startupPath + "\\anuncis.lnk");    
////                }
//                if(new File(startupPath + "\\anuncis.lnk").exists())
//                {
//                        CoreCfg.setIniProperty("anuncisStartup", "AUTOMATIC");
//                        CoreCfg.saveIniProperties();
//                        
//                }
//
//            }
//        }
//    
//    }
//
//     public static void removeLink() {
//          //Determina  menu inicio de la versio de windows
//        if(startupPath==null)
//        {
//            startupPath = WindowsRegistry.readRegistry(WindowsRegistry.USERFOLDERS, "Startup");
//            startupPath = startupPath.replace("%USERPROFILE%", System.getProperty("user.home"));
//        }
//        String path = startupPath + "\\anuncis.lnk";
//        File file = new java.io.File(path);
//        if(!path.isEmpty()) 
//        {
//            file.delete();
//          
//            if(!file.exists()) 
//            {
//                 
//                CoreCfg.setIniProperty("anuncisStartup", "BLOCKED");
//                CoreCfg.saveIniProperties();
//            }
//        }
//    }
    public boolean startFitxesDB() {
        mysql = new MyDatabase(CoreCfg.core_mysqlHost, CoreCfg.core_mysqlDBPrefix,
                CoreCfg.core_mysqlUser, CoreCfg.core_mysqlPasswd, CoreCfg.coreDB_mysqlParam);
        return mysql.connect();
    }

    public void createEmptyDB(String dbName) {

        if (dbName.isEmpty()) {
            return;
        }
        //Si la base de dades ja existeix retorna


        boolean exists = false;
        try {
            Statement st = getMysql().createStatement();
            ResultSet rs1 = getMysql().getResultSet("SHOW DATABASES", st);
            while (rs1 != null && rs1.next()) {
                if (dbName.equalsIgnoreCase(rs1.getString(1))) {
                    exists = true;

                    break;
                }

            }
            rs1.close();
            st.close();

        } catch (SQLException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (exists) {
            return;
        }
        int nup = getMysql().executeUpdate("CREATE DATABASE if not exists " + dbName);
        if (nup <= 0) {
            return;
        }
        getMysql().setCatalog(dbName);

        //Crea taules amb l'script runner

        org.iesapp.database.ScriptRunner runner = new org.iesapp.database.ScriptRunner(getMysql().getConnection(), false, true);
        try {

            runner.runScript(new BufferedReader(new FileReader(contextRoot+File.separator+SCRIPTMAINDB)));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createEmptyxxxxDB(String dbName) {

        //Si la base de dades ja existeix retorna


        boolean exists = false;
        try {
            Statement st = getMysql().createStatement();
            ResultSet rs1 = getMysql().getResultSet("SHOW DATABASES", st);
            while (rs1 != null && rs1.next()) {
                if (dbName.equalsIgnoreCase(rs1.getString(1))) {
                    exists = true;

                    break;
                }

            }
            rs1.close();
            st.close();

        } catch (SQLException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (exists) {
            return;
        }

        int nup = getMysql().executeUpdate("CREATE DATABASE if not exists " + dbName);
        getMysql().setCatalog(dbName);

        //Crea taules amb l'script runner

        org.iesapp.database.ScriptRunner runner = new org.iesapp.database.ScriptRunner(getMysql().getConnection(), false, true);
        try {

            runner.runScript(new BufferedReader(new FileReader(contextRoot+File.separator+SCRIPTXXXXDB)));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public static void onAccessBlocked(Closable closable) {
//        TimeoutDialog cd = new TimeoutDialog(javar.JRDialog.getActiveFrame(), null);
//        cd = null;
//        //If Runtime.getRuntime().getShutdownHook is specified, the operations are done before closing...
//        closable.quitApp();
//    }
//
//    public void checkKillSignal(String whoami, String program, int pid, Closable closable) {
//        if (whoami == null || program == null) {
//            return;
//        }
//
//        String SQL1 = "SELECT * FROM `" + CoreCfg.core_mysqlDB + "`.sig_log WHERE usua='ADMIN' and tasca='SIGKILL' and"
//                + " (resultat LIKE '%CONTEXT={" + core_PRODUCTID + "}%' OR resultat LIKE '%CONTEXT={ALL}%' "
//                + " ) and (resultat LIKE '%PROGRAM={%" + program.toUpperCase() + "%}%' OR "
//                + " resultat LIKE '%PROGRAM={ALL}%') and "
//                + " (resultat LIKE '%IP={%" + ip + "%}%' OR "
//                + " resultat LIKE '%IP={ALL}%') and (resultat LIKE '%PID={%" + pid + "%}%' OR "
//                + " resultat LIKE '%PID={ALL}%') and (resultat LIKE '%USERS={%" + whoami + "%}%' OR "
//                + " resultat LIKE '%USERS={ALL}%') "
//                + " and inici<=now() and now()<=fi";
//        //System.out.println(SQL1);
//
//
//        try {
//            Statement st = getMysql().createStatement();
//            ResultSet rs1 = getMysql().getResultSet(SQL1, st);
//            if (rs1 != null && rs1.next()) {
//                onAccessBlocked(closable);
//            }
//            rs1.close();
//            st.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void checkKillSignal(String program, Closable closable) {
//        String SQL1 = "SELECT * FROM `" + CoreCfg.core_mysqlDB + "`.sig_log WHERE usua='ADMIN' and tasca='SIGKILL' and"
//                + " (resultat LIKE '%CONTEXT={" + core_PRODUCTID + "}%' OR resultat LIKE '%CONTEXT={ALL}%' "
//                + " ) and (resultat LIKE '%PROGRAM={%" + program.toUpperCase() + "%}%' OR "
//                + " resultat LIKE '%PROGRAM={ALL}%') and "
//                + " (resultat LIKE '%IP={}%' OR "
//                + " resultat LIKE '%IP={ALL}%') and (resultat LIKE '%PID={}%' OR resultat LIKE '%PID={ALL}%') "
//                + " and inici<=now() and now()<=fi";
//        //System.out.println("checkKillSignal: " +SQL1);
//
//
//
//        try {
//            Statement st = getMysql().createStatement();
//            ResultSet rs1 = getMysql().getResultSet(SQL1, st);
//            if (rs1 != null && rs1.next()) {
//                onAccessBlocked(closable);
//            }
//            rs1.close();
//            st.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(CoreCfg.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    /**
     * saves to javaversion table information about this computer if enabled in
     * the configtablemap
     */
    private void registerSystemInfo() {
        if (configTableMap.containsKey("enableRegSystemInfo")
                && configTableMap.get("enableRegSystemInfo").equals("yes")) {
            com.sun.management.OperatingSystemMXBean mxbean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            String SQL1 = "UPDATE `" + CoreCfg.core_mysqlDBPrefix + "`.javaversion SET netbios='" + netbios + "', java='"
                    + System.getProperty("java.version") + "', os='" + System.getProperty("os.name")
                    + " " + System.getProperty("os.version") + "', arch='" + mxbean.getArch() + "',"
                    + " processors='" + mxbean.getAvailableProcessors() + "', totalMemory='"
                    + mxbean.getTotalPhysicalMemorySize() * 9.5367431640625e-07 + "' WHERE ip='" + ip + "'";

            int nup = getMysql().executeUpdate(SQL1);
            if (nup <= 0) {
                SQL1 = "INSERT INTO `" + CoreCfg.core_mysqlDBPrefix + "`.javaversion (ip, netbios, java, os, arch, processors, totalMemory) VALUES('"
                        + ip + "', '" + netbios + "', '" + System.getProperty("java.version") + "','" + System.getProperty("os.name")
                        + " " + System.getProperty("os.version") + "','" + mxbean.getArch() + "','"
                        + mxbean.getAvailableProcessors() + "','" + mxbean.getTotalPhysicalMemorySize() * 9.5367431640625e-07 + "')";
                getMysql().executeUpdate(SQL1);
            }
        }
    }

    public MyDatabase getMysql() {
        return mysql;
    }

    public MyDatabase getSgd() {
        return sgd;
    }

    public User getUserInfo() {
        return userInfo;
    }

    public void setUser(String abrev) {
        
        this.abrev = abrev;
        
        if(abrev!=null && !abrev.isEmpty())
        {
            userInfo = User.getUser(abrev, this);
            //System.out.println("1");
            Profesores p = getSgdClient().getProfesores();
            //System.out.println("2");
            p.loadByAbrev(abrev);
            //System.out.println("3");
            boolean enableLogger = (Boolean) configTableMap.get("sgdEnableLogger");
            getSgdClient().setUser(p, enableLogger);
            getIesClient().setIuser(userInfo);
            //System.out.println("4");
        }
        else
        {
           userInfo = User.getOffLineUser(this);
           getSgdClient().setUser(null, false);
           getIesClient().setIuser(userInfo);
        }
        
    }

    public IesClient getIesClient() {
        return iesClient;
    }

    public SgdClient getSgdClient() {
        return sgdClient;
    }

    public javax.help.HelpBroker getMainHelpBroker() {
        if(mainHelpBroker==null)
        {
            //mainHelpBroker = getMainHelpSet().createHelpBroker();
            mainHelpBroker = new DefaultHelpBroker();
            mainHelpBroker.setHelpSet(getMainHelpSet());
            mainHelpBroker.setCurrentID("org-iesapp-modules");
        }
        return mainHelpBroker;
    }

    public javax.help.HelpSet getMainHelpSet() {
       
        if(mainHelpSet == null)
        {
            try {
                JarClassLoader systemClassLoader = JarClassLoader.getInstance();
                File file = new File(CoreCfg.contextRoot+File.separator+"modules"+File.separator+"org-iesapp-modules-helpset.jar");
                //System.out.println("File added is file->"+file);
                
                systemClassLoader.addToClasspath(file);
                URL url = HelpSet.findHelpSet(systemClassLoader, "org/iesapp/modules/help/module.hs", Locale.getDefault());
                //URL url = systemClassLoader.getResource("org/iesapp/modules/help/module.hs");
               // URL url = mainHelpSet.findmainHelpSet(systemClassLoader, "/org/iesapp/modules/reserves/help/module.hs");
                //System.out.println("URL->"+url);
                if(url!=null)
                {
                    mainHelpSet = new HelpSet(systemClassLoader, url);
                    //System.out.println("mainHelpSet->"+mainHelpSet);
                }
            } catch (HelpSetException ex) {
                Logger.getLogger(TopModuleWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return mainHelpSet;
    }

    public long getUpTime() {
        return System.currentTimeMillis() - startTime;
    }
 
}
