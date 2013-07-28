/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.style.INotificationStyle;
import ch.swingfx.twinkle.style.theme.DarkDefaultNotification;
import ch.swingfx.twinkle.window.Positions;
import com.jgoodies.looks.FontPolicies;
import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.FontSet;
import com.jgoodies.looks.FontSets;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.l2fprod.common.swing.JLinkButton;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.help.CSH;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import org.iesapp.clients.sgd7.profesores.Profesores;
import org.iesapp.framework.data.User;
import org.iesapp.framework.dialogs.Login;
import org.iesapp.framework.dialogs.LoginAdmin;
import org.iesapp.framework.dialogs.LoginInterface;
import org.iesapp.framework.dialogs.LoginValidation;
import org.iesapp.framework.pluggable.deamons.BeanDeamon;
import org.iesapp.framework.pluggable.deamons.TopModuleDeamon;
import org.iesapp.framework.pluggable.modulesAPI.BeanModule;
import org.iesapp.framework.pluggable.modulesAPI.ErrorDisplay;
import org.iesapp.framework.pluggable.modulesAPI.GenericFactory;
import org.iesapp.framework.pluggable.modulesAPI.ModulesManager;
import org.iesapp.framework.pluggable.modulesAPI.Property;
import org.iesapp.framework.pluggable.modulesAPI.UtilsFactory;
import org.iesapp.framework.pluggable.pluginsAPI.BeanAnchorPoint;
import org.iesapp.framework.pluggable.preferences.UserPreferencesModule;
import org.iesapp.framework.start.Start;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.CoreIni;
import org.iesapp.framework.util.JarClassLoader;
import org.iesapp.updater.RemoteUpdater;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class DockingFrameworkApp extends JFrame implements Closable{

    protected static String[] supportedLangDesc = new String[]{"Català","Español","English"};
    protected static String[] supportedLang = new String[]{"ca","es","en"};
    public static String moduleLoadPref = "all";  //none, required, all
    public static String pluginLoadPref = "all";
    protected java.util.ResourceBundle bundle;    
    protected String appDisplayName = "iesDigital";
    protected String appNameId = "App";
    protected String appDescription = "A basic pluggable application by J. Mulet";
    
    protected boolean maximum = true;
    protected boolean administrativeApp = false;
    
    protected String user = "";
    protected String pwd = "";
    protected String role;
    protected ImageIcon MODULE_ICON = new ImageIcon(DockingFrameworkApp.class.getResource("/org/iesapp/framework/icons/module_icon.gif"));
    protected Class appClass;
    protected String requiredJar;
    protected String requiredModuleName;
    protected Loading wait;
    protected UIFramework uiFramework;  //my own implementation of the ui-framework
    protected GenericFactory genericFactory;
    protected boolean showMenuBar;

    public final Stamp stamper;
    protected SysTray stray;
    public final ArrayList<JMenu> beforeMenu = new ArrayList<JMenu>();
    public final ArrayList<JMenu> afterMenu = new ArrayList<JMenu>();
    protected Thread hook;
    protected CoreCfg coreCfg;
    protected boolean iWasAdmin;
    protected final WindowManager windowManager;
    protected final String anuncisClassName = "org.iesapp.apps.anuncis.AnuncisApp";
    protected final VToolbar vtoolbar;
    
    
    /**
     * Creates new form NewJFrame
     * @param args
     */
    public DockingFrameworkApp(String[] args) {
       //Initialize core and creates a new istance of coreCfg 
       //Toolkit.getDefaultToolkit().setDynamicLayout(true);
       initializeCore(args);
       
       stamper = new Stamp();
       
       //Create a verticaltoolbar
       vtoolbar = new VToolbar(coreCfg);
       Locale locale = new Locale(coreCfg.core_lang);
       Locale.setDefault(locale); 
       this.setLocale(locale);
       
       coreCfg.getMainHelpBroker().enableHelpKey(this.getRootPane(), "org-iesapp-modules", null);
       
        //Hook for windows logoff
        hook = new Thread()
        {
            @Override
            public void run()
            {
                uiFramework.closeAll();
                if(stamper!=null)
                {
                    stamper.addAction("KILLED");
                    stamper.outStamp();
                }
                coreCfg.close();
                DockingFrameworkApp.this.dispose();
            }
        };
        
       Runtime.getRuntime().addShutdownHook(hook);
      
       bundle = java.util.ResourceBundle.getBundle("org/iesapp/framework/pluggable/pluggable");
       
       
       initComponents();
       
       //register close behavior
       this.addWindowListener( new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing(e);
            }
           
       });
       
       //Help track and menu
       jButtonHT.addActionListener(new CSH.DisplayHelpAfterTracking(coreCfg.getMainHelpBroker()));
       jMenuItem2.addActionListener(new CSH.DisplayHelpFromSource(coreCfg.getMainHelpBroker()));  
       
       //View-toggle
       jToggleButton1.setSelectedIcon(new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/back.gif")));
       jToggleButton1.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/forward.gif")));
        
       
       beforeMenu.add(jMenuFitxer);
       beforeMenu.add(jMenuModules);
       
       afterMenu.add(jMenuAdministrador);
       afterMenu.add(jMenuWindow);     
       afterMenu.add(jMenuAjuda);
       
       //3 zones
       //1st Login Zone
       //2nd Module displayable component zone, e.g. clock, users online, etc.
       //3rd For Module specific purposes
       String[] zoneNames = new String[]{"first","second","third"};
       String[] contrains = new String[]{"15%","50%","35%"};
               
       StatusBarZone[] zonePanels = new StatusBarZone[zoneNames.length];
       for(int i=0; i<zoneNames.length; i++)
       {
           StatusBarZone panel = new StatusBarZone();          
           zonePanels[i] = panel;
       }
       
       
        jStatusBar.setZones(zoneNames, zonePanels, contrains);
        ((StatusBarZone) jStatusBar.getZone("first")).addComponent(switchLabel);
        ((StatusBarZone) jStatusBar.getZone("first")).addComponent(jLabelUser);
      
       
       
       ActionListener langListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String language = e.getActionCommand();
                coreCfg.setUserPreferences("lang", language);
                coreCfg.core_lang = language;
                coreCfg.saveIniProperties();
                Locale.setDefault(new Locale(language));
                ResourceBundle.clearCache();
                bundle = java.util.ResourceBundle.getBundle("org/iesapp/framework/pluggable/pluggable"); // NOI18N
       
                //Perque tingui efecte cal reiniciar
                JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), bundle.getString("restartForModule"));
                
            }

           
        };
       
       String currentLang = coreCfg.core_lang;
       ButtonGroup groupLang = new ButtonGroup();        
       for(int i=0; i<supportedLang.length; i++)
       {
           JCheckBoxMenuItem item = new JCheckBoxMenuItem();
           item.setText(supportedLangDesc[i]);
           item.setActionCommand(supportedLang[i]);
           if(supportedLang[i].equals(currentLang))
           {
               item.setSelected(true);
           }
           item.addActionListener(langListener);
           groupLang.add(item);
           jMenuIdioma.add(item);
       }
       
       
        switchLabel.setToolTipText(bundle.getString("changeUser"));
        switchLabel.addMouseListener(new MouseAdapter() {
           
            @Override
            public void mouseClicked(MouseEvent e) {
                DockingFrameworkApp.this.onSwitchUser(true);
            }
            
        });
        
        //Define here switchButton
        switchButton.setIcon(switchLabel.getIcon());
        switchButton.setToolTipText(switchLabel.getToolTipText());
        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DockingFrameworkApp.this.onSwitchUser(true);

            }

        });
       
        windowManager = new WindowManager((JFrame) this, jPanel1, jToolBar1, jStatusBar, jButtonHT, 
                jToggleButton1, switchButton, vtoolbar);
        this.setLocationRelativeTo(null);         
      
           
    }
    
    public Container getContentContainer()
    {
        return jPanel1;
    }
    
    
    
    private void initializeCore(String[] args)
    {        
         
        boolean mustShow = false;
        boolean debug = false;
        if(args!=null)
        {
        for (String s : args) {
            if (s.startsWith("-user=")) {
                user = StringUtils.AfterFirst(s, "=").trim();
            }
            if (s.startsWith("-pwd=")) {
                pwd = StringUtils.AfterFirst(s, "=").trim();
            }
            if (s.startsWith("-c")) {
                mustShow = true;
            }
            if (s.startsWith("-debug")) {
                debug = true;
            }
            if (s.startsWith("-modules=")) {
                moduleLoadPref = StringUtils.AfterFirst(s, "=").trim();
            }
            if (s.startsWith("-plugins=")) {
                pluginLoadPref = StringUtils.AfterFirst(s, "=").trim();
            }
        }
        }
       
        //Add
        CoreIni coreini = CoreIni.getInstance(args, (Closable) this);
        Start.startup(mustShow, (Closable) this);
        coreCfg = new CoreCfg(args, (Closable) this);  //Created new instance of coreCfg
        int initialize = coreCfg.initialize();
        if(initialize<0)
        {
            this.exitApp();
        }
    }

   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelUser = new javax.swing.JLabel();
        switchLabel = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jToolBarModules = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        jStatusBar = new com.l2fprod.common.swing.StatusBar();
        jButtonHT = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        switchButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFitxer = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuModules = new javax.swing.JMenu();
        jMenuAdministrador = new javax.swing.JMenu();
        jMenuItemStartAdmin = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuWindow = new javax.swing.JMenu();
        jMenuLookandfeel = new javax.swing.JMenu();
        jMenuDistribucio = new javax.swing.JMenu();
        jMenuIdioma = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFullscreen = new javax.swing.JCheckBoxMenuItem();
        jMenuAjuda = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuAbout = new javax.swing.JMenuItem();

        jLabelUser.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelUser.setText("                                              ");
        jLabelUser.setPreferredSize(new java.awt.Dimension(150, 14));

        switchLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/framework/icons/switchUser.gif"))); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setFocusable(false);

        jToolBarModules.setFloatable(false);
        jToolBarModules.setBorderPainted(false);
        jToolBarModules.setFocusable(false);
        jToolBarModules.setName("jToolBarModules"); // NOI18N
        jToolBar1.add(jToolBarModules);

        jPanel1.setLayout(new java.awt.BorderLayout());

        com.l2fprod.common.swing.PercentLayout percentLayout1 = new com.l2fprod.common.swing.PercentLayout();
        percentLayout1.setGap(1);
        jStatusBar.setLayout(percentLayout1);

        jButtonHT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/framework/icons/helptrack.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/iesapp/framework/pluggable/pluggable"); // NOI18N
        jButtonHT.setToolTipText(bundle.getString("helpTrack")); // NOI18N
        jButtonHT.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonHT.setContentAreaFilled(false);
        jButtonHT.setFocusable(false);
        jButtonHT.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonHT.setPreferredSize(new java.awt.Dimension(31, 26));
        jButtonHT.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jToggleButton1.setToolTipText(bundle.getString("collapseToolbar")); // NOI18N
        jToggleButton1.setFocusable(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        switchButton.setFocusable(false);
        switchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        switchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenuFitxer.setText(bundle.getString("Fitxer")); // NOI18N
        jMenuFitxer.setName("jMenuFitxer"); // NOI18N

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/framework/icons/switchUser.gif"))); // NOI18N
        jMenuItem4.setText(bundle.getString("changeUser")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenuFitxer.add(jMenuItem4);
        jMenuFitxer.add(jSeparator4);

        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/framework/icons/configIcon.gif"))); // NOI18N
        jMenuItem6.setText(bundle.getString("userPreferences")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenuFitxer.add(jMenuItem6);
        jMenuFitxer.add(jSeparator3);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/framework/icons/exit.gif"))); // NOI18N
        jMenuItem3.setText(bundle.getString("exit")); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenuFitxer.add(jMenuItem3);

        jMenuBar1.add(jMenuFitxer);

        jMenuModules.setText(bundle.getString("moduls")); // NOI18N
        jMenuModules.setName("jMenuModules"); // NOI18N
        jMenuBar1.add(jMenuModules);

        jMenuAdministrador.setText(bundle.getString("Administracio")); // NOI18N
        jMenuAdministrador.setName("jMenuAdministracio"); // NOI18N

        jMenuItemStartAdmin.setText(bundle.getString("LlanzaAdministrador")); // NOI18N
        jMenuItemStartAdmin.setName("jMenuItemStartAdmin"); // NOI18N
        jMenuItemStartAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStartAdminActionPerformed(evt);
            }
        });
        jMenuAdministrador.add(jMenuItemStartAdmin);
        jMenuAdministrador.add(jSeparator1);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setMnemonic('C');
        jMenuItem5.setText(bundle.getString("moduleManager")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenuAdministrador.add(jMenuItem5);

        jMenuBar1.add(jMenuAdministrador);

        jMenuWindow.setText(bundle.getString("window")); // NOI18N
        jMenuWindow.setName("jMenuWindow"); // NOI18N

        jMenuLookandfeel.setText(bundle.getString("lookandfeel")); // NOI18N
        jMenuLookandfeel.setName("jMenuLookandfeel"); // NOI18N
        jMenuWindow.add(jMenuLookandfeel);

        jMenuDistribucio.setText(bundle.getString("distribution")); // NOI18N
        jMenuDistribucio.setEnabled(false);
        jMenuDistribucio.setName("jMenuDistribucio"); // NOI18N
        jMenuWindow.add(jMenuDistribucio);

        jMenuIdioma.setText(bundle.getString("language")); // NOI18N
        jMenuWindow.add(jMenuIdioma);

        jMenuItem1.setText(bundle.getString("logger")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenuWindow.add(jMenuItem1);
        jMenuWindow.add(jSeparator2);

        jMenuItemFullscreen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemFullscreen.setText(bundle.getString("fullScreen")); // NOI18N
        jMenuItemFullscreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFullscreenActionPerformed(evt);
            }
        });
        jMenuWindow.add(jMenuItemFullscreen);

        jMenuBar1.add(jMenuWindow);

        jMenuAjuda.setText(bundle.getString("help")); // NOI18N
        jMenuAjuda.setName("jMenuAjuda"); // NOI18N

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/framework/icons/support.png"))); // NOI18N
        jMenuItem2.setText(bundle.getString("helpcontents")); // NOI18N
        jMenuAjuda.add(jMenuItem2);

        jMenuAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.CTRL_MASK));
        jMenuAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/framework/icons/help.gif"))); // NOI18N
        jMenuAbout.setText(bundle.getString("about")); // NOI18N
        jMenuAbout.setActionCommand("about");
        jMenuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAboutActionPerformed(evt);
            }
        });
        jMenuAjuda.add(jMenuAbout);

        jMenuBar1.add(jMenuAjuda);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jStatusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jButtonHT, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(switchButton)
                .addGap(0, 0, 0)
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(switchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonHT, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(1, 1, 1)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jStatusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        this.exitApp();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    //Initialize App
    public void initializeFramework()
    {
        
        //Set Lookandfeel from the very begining
        //By default set systemLookandFeel
        String lookandfeel="Nimbus";
        boolean nimbus = false;
        ActionListener lookandfeelListener = new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel(e.getActionCommand());
                    SwingUtilities.updateComponentTreeUI(DockingFrameworkApp.this);
                   
                
                } catch (Exception ex) {
                    Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
            
        };
        
        ButtonGroup buttonGroup1 = new ButtonGroup();
        initializeLookAndFeels();
        
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(info.getName());
                item.setActionCommand(info.getClassName());
                item.addActionListener(lookandfeelListener);
              
                buttonGroup1.add(item);
                jMenuLookandfeel.add(item);
                if (lookandfeel.equals(info.getName())) {
                    nimbus = true;
                    item.setSelected(true);
                }                
            }
       
        } catch (Exception ex) {
            DebugLogger.getInstance().addText(ex.toString());
        }
        
        
        
        ActionListener distribucioListener = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = Integer.parseInt(e.getActionCommand());
                uiFramework.setDistribution(i);
            }
        };
                
        ButtonGroup bg2 = new ButtonGroup();
        for (int i = 0; i < UIFrameworkJR.MAXTabbedPanes; i++) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem((i+1)+" finestres");
            item.setActionCommand((i+1)+"");
            item.addActionListener(distribucioListener);
            KeyStroke keyStroke = KeyStroke.getKeyStroke("alt " + (i+1));
            if (keyStroke != null) {
                item.setAccelerator(keyStroke);
            }
            bg2.add(item);
            if (i == 0) {
                item.setSelected(true);
            }
            jMenuDistribucio.add(item);
       }
        
       this.setTitle("iesDigital " + CoreCfg.VERSION + "   ·   " + appDisplayName +"   ·   "+coreCfg.anyAcademic+"-"+(coreCfg.anyAcademic+1));
      
       
       try {
            genericFactory = new GenericFactory(appClass.getName(), requiredModuleName, requiredJar);
        } catch (Exception ex) {
            DebugLogger.getInstance().addText(ex.toString());
        }
        windowManager.setProperties(genericFactory.getApplicationInitParameters());
      
         //Initialize your desired framework here
       uiFramework = (UIFramework) new UIFrameworkIN(coreCfg, stamper, stray, jToggleButton1);
       uiFramework.initialize(windowManager, appDisplayName, beforeMenu, afterMenu, this.appClass.getName());
       
        //// set application display parameters
           
        //Apply application attributes
        if(genericFactory.getApplicationAttribute("showToolBar").contains("no"))
        {
           jToolBar1.setVisible(false);
           this.validate();
        } 
        
        if(genericFactory.getApplicationAttribute("showStatusBar").contains("no"))
        {
           jStatusBar.setVisible(false);
           this.validate();
        }   
        
        showMenuBar = true;
        if(genericFactory.getApplicationAttribute("showMenuBar").contains("no"))
        {
           jMenuBar1.setVisible(false);
           showMenuBar = false;
           this.validate();
        }   
        
        if(!appClass.getName().equals(anuncisClassName))
        {
            if(genericFactory.getApplicationAttribute("display").contains("extended"))
            {
               windowManager.setDisplayMode(WindowManager.MODE_MAXIMIZED);
            }

            if(genericFactory.getApplicationAttribute("display").contains("normal"))
            {
                windowManager.setDisplayMode(WindowManager.MODE_NORMAL);
            }

            if(genericFactory.getApplicationAttribute("display").contains("fullscreen"))
            {
                windowManager.setDisplayMode(WindowManager.MODE_FULLSCREEN);
                jMenuItemFullscreen.setSelected(true);
            }
        }
               
        HashMap<String, Property> map = genericFactory.getApplicationInitParameters();
       
        if(map.containsKey("useSystemUser") && map.get("useSystemUser").getValue().equalsIgnoreCase("yes"))
        {
            String windowsUserName = System.getProperty("user.name");
            Profesores profe = coreCfg.getSgdClient().getProfesores();
            profe.loadByWinUser(windowsUserName);

            if(profe.getIdUnidadesPersonales()>0)
            {
                this.pwd = profe.getClaveUP();
                this.user = profe.getAbrev();
                //vtoolbar.setSystemUser(profe.getSystemUser(), profe.getClaveUP());                
            }
        }
        
         onSwitchUser(false);
         //Overridable method
         onWindowOpened();  
       
    }
    
   
    //Override this method if you wish another close behaviour for your application
    public void formWindowClosing(java.awt.event.WindowEvent evt) {                                   
           this.exitApp();           
    }                                  

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        onSwitchUser(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    //Launch administrador
    private void jMenuItemStartAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStartAdminActionPerformed
        try {
            String cmd = CoreCfg.contextRoot+"\\bin\\administrador.exe -user="+user+" -pwd="+pwd;
            Runtime.getRuntime().exec(cmd);
        } catch (Exception ex) {
            Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItemStartAdminActionPerformed

    private void jMenuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAboutActionPerformed
                String name = appDisplayName;
                String desc = appDescription;
                String version = CoreCfg.VERSION;
       
                TopModuleWindow win = uiFramework.getSelectedTopModuleWindow();
                BeanModule beanModule = null;
                if(win!=null)
                {
                    name = win.getModuleDisplayName();
                    desc = win.getModuleDescription();
                    if(win.getBeanModule()!=null)
                    {
                        version = win.getBeanModule().getBeanMetaINF().getVersion();
                        beanModule = win.getBeanModule();
                    }
                    
                }
                org.iesapp.framework.dialogs.AboutDlg dlg = new org.iesapp.framework.dialogs.AboutDlg(DockingFrameworkApp.this, true, name, desc, version, beanModule, coreCfg);
                dlg.setLocationRelativeTo(null);
                dlg.setVisible(true);
        
    }//GEN-LAST:event_jMenuAboutActionPerformed

    private void jMenuItemFullscreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFullscreenActionPerformed
      if(jMenuItemFullscreen.isSelected())
      {
          windowManager.setDisplayMode(WindowManager.MODE_FULLSCREEN);
      }
      else
      {
          windowManager.setLastDisplayMode();          
      }
    }//GEN-LAST:event_jMenuItemFullscreenActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        ModulesManager dlg = new ModulesManager(this, true, appClass, requiredModuleName, requiredJar, coreCfg);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);       
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        //opens a logger
        if (coreCfg.getUserInfo().getGrant() != User.ADMIN) {
            LoginAdmin dlg = new LoginAdmin(this, true);
            dlg.setLocationRelativeTo(null);
            dlg.setVisible(true);
            if (dlg.getValidation() != 1) {
                return;
            }
        }

        //First check if logger is already instantiated
        ArrayList<String> instances = TopModuleRegistry.getCurrentInstancesOf(LoggerModule.class.getName());
        if (instances.isEmpty()) {
            LoggerModule loggerModule = new LoggerModule(coreCfg);
            loggerModule.setName("logger");
            uiFramework.addTopModuleWindow(loggerModule, "bottom", true, false);
        }


    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        
        //Add module
        //Check if already exists
        //First check if logger is already instantiated
        ArrayList<String> instances = TopModuleRegistry.getCurrentInstancesOf(UserPreferencesModule.class.getName());
        if (instances.isEmpty()) {
             TopModuleWindow win = new UserPreferencesModule();
             win.initialize(stamper, stray, coreCfg, uiFramework);
             uiFramework.addTopModuleWindow(win, "left", true, false);
        }
       
    }//GEN-LAST:event_jMenuItem6ActionPerformed


 
 
    public void exitApp() {
      
        if(stamper!=null)
        {
            stamper.outStamp();
        }
        
        if(genericFactory!=null && genericFactory.getApplicationAttribute("validateLogout").equalsIgnoreCase("yes"))
        {
            if(jMenuItemFullscreen.isSelected())
            {
                windowManager.setLastDisplayMode();
                jMenuItemFullscreen.setSelected(false);
            }
            ValidacioDlg dlg = new ValidacioDlg(javar.JRDialog.getActiveFrame(), true, "ADMIN", coreCfg);
            dlg.setLocationRelativeTo(null);
            dlg.setAlwaysOnTop(true);
            dlg.setVisible(true);
            
            if(!dlg.isValidated)
            {
                return;
            }
            dlg.dispose();
        }
        
        //Tanca tots els moduls
        if(uiFramework!=null)
        {
            uiFramework.closeAll();
        }
        coreCfg.close();
        this.dispose();
        if(hook!=null)
        {
            //Before exit must remove hookthread
            Runtime.getRuntime().removeShutdownHook(hook);
        }
        System.exit(0);
    }
    
    
 
     /**
      * Adds new Module 
      * Make sure that class is in the classpath
      * @param className className of the module which must extend TopModuleWindow 
      */
  
    public void loadModules()
    {   
        //Stop all deamons
        DebugLogger.getInstance().addText("Start loadModules...");
        jToolBarModules.removeAll();
         
        jToolBarModules.add(switchButton);
        DebugLogger.getInstance().addText("TopModuleRegistry.reset()...");
        TopModuleRegistry.reset();
        
        if(iWasAdmin)
        {
            try {
                if (genericFactory == null || iWasAdmin) {
                    genericFactory = new GenericFactory(appClass.getName(), requiredModuleName, requiredJar);
                }
            } catch(Exception ex)
            {
                ErrorDisplay.showMsg(ex.toString());
                this.quitApp();
            }
             
        }
        
        
     
        
        boolean preventSwitchUser = genericFactory.getApplicationAttribute("preventSwitchUser").equalsIgnoreCase("yes");
        jMenuItem4.setEnabled(!preventSwitchUser && !administrativeApp);
        switchLabel.setEnabled(!preventSwitchUser && !administrativeApp);
        
        
        if(moduleLoadPref.equals("none"))
        {
            //System.out.println("-modules=none: no modules are loaded");
            uiFramework.setMenus(null);
            return;
        }
        
        
        for(BeanModule module: genericFactory.loadModulesCache())
        {
            if (module.getModuleType() == BeanModule.MODULETYPE_STD) {
                loadSTDModule(module);
            } 
            else if (module.getModuleType() == BeanModule.MODULETYPE_DESKTOP_BROWSE) {
                loadDesktopBrowse(module);
            }
             else if (module.getModuleType() == BeanModule.MODULETYPE_DESKTOP_OPEN) {
                loadDesktopOpen(module);
            }
            
            //Deamons (activate them)
            if(user!=null && !user.isEmpty())
            {
            for(BeanDeamon beandeamon: module.getDeamons())
            {
                 if(!beandeamon.isEnabled() || (!beandeamon.getRoles().contains("*") &&
                         !beandeamon.getRoles().contains(coreCfg.getUserInfo().getRole())))
                {
                    continue;
                }

                JarClassLoader deamonClassLoader = JarClassLoader.getInstance().getSubInstance(module);
                try {
                    TopModuleDeamon deamon = null;
                    if(TopModuleDeamon.getActiveDeamons().containsKey(beandeamon.getDeamonClassName()+"@"+user))
                    {
                        deamon = TopModuleDeamon.getActiveDeamons().get(beandeamon.getDeamonClassName()+"@"+user);
                    }
                    else
                    {
                        Class<?> forName =deamonClassLoader.loadClass(beandeamon.getDeamonClassName());
                        deamon = (TopModuleDeamon) forName.newInstance();
                        deamon.initialize(coreCfg);

                        TopModuleDeamon.getActiveDeamons().put(forName.getName()+"@"+user, deamon);

                        deamon.setDeamon(beandeamon);
                        deamon.setModuleClassName(module.getClassName());
                        deamon.start(beandeamon.getTimeInMillis());
                        //System.out.println("ACTIVATED DEAMON "+deamon.getDeamon().getDeamonClassName());
                    }
                    deamon.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            System.out.println(evt);
                            TopModuleDeamon deamon = (TopModuleDeamon) evt.getSource();
                            String cn = deamon.getModuleClassName(); 
                            if(deamon.getDeamon().isShowMessage())
                            {
                                String str = deamon.getMessage();
                                if(str!=null && !str.isEmpty())
                                {
                                    INotificationStyle style = new DarkDefaultNotification().withWindowCornerRadius(8).withWidth(300).withAlpha(0.86f);
                                  // Now lets build the notification
                                  new NotificationBuilder().withStyle(style) // Required. here we set the previously set style
                                    .withTitle("Informació") // Required.
                                    .withMessage(str) // Optional
                                    .withIcon(new ImageIcon(NotificationBuilder.ICON_INFO)) // Optional. You could also use a String path
                                    .withDisplayTime(5000) // Optional
                                    .withPosition(Positions.SOUTH_EAST) // Optional. Show it at the center of the screen
                                    .showNotification(); 
                                  
                                }
                                vtoolbar.addNotificationFor(cn, str);
                            }
                            if(deamon.getDeamon().isActivateIcon())
                            {
                                
                                //Find module by class name
                                BeanModule realMod = null;
                                for(BeanModule mod: genericFactory.loadModulesCache())
                                {
                                    if(mod.getClassName().equals(cn))
                                    {
                                        realMod = mod;
                                        break;
                                    }
                                }
                                if(realMod!=null)
                                {
                                    ////System.out.println("found module");
                                    ImageIcon newicon = realMod.getModuleIcon24x24(deamon.getStatus());
                                    ////System.out.println("icon with status "+deamon.getStatus());
                                    JButton linkButton = findLinkButtonByName(jToolBar1, cn);
                                    if(linkButton!=null)
                                    {
                                         linkButton.setIcon(newicon);
                                    }
                                    //Apply changes to Vtoolbar as well
                                    JButton btn = vtoolbar.findButtonByModuleClassName(cn);
                                    if(btn!=null)
                                    {
                                        btn.setIcon(newicon);
                                    }
                                }
                                
                                
                            }
                            
                        }

                       
                    });
                } catch (Exception ex) {
                    Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            }
            
        }
       
    }
     
    /**
     * Every module icon must be named after its module name
     * in this way, it can be reachable in order to change
     * the status of the icon
     * @param modulename
     * @return E
     */
    private JButton findLinkButtonByName(Container container, String modulename)
    {
        System.out.println("trying to find "+modulename);
        JButton button= null;
        
        for (Component comp : container.getComponents()) {
             
            if (comp instanceof JButton) {
                
               String name = ((JButton) comp).getActionCommand();
               if (name != null && name.equals(modulename)) {
                    button = ((JButton) comp);
                    break;
                }
            }
            else if (comp instanceof JToolBar) {
                 button = findLinkButtonByName((JToolBar) comp,  modulename);
                 if(button!=null)
                 {
                     return button;
                 }
            }
        }
        return button;
    }
    
    ///////////////////////////////////////////// INSTALL A STD MODULE
    private void loadSTDModule(BeanModule module)
    {
        //Add these modules to classpath and loadThem if required
        ActionListener listenerNewModule = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String classname = e.getActionCommand();
                int idx = -1;
                for (int i = 0; i < genericFactory.loadModulesCache().size(); i++) {
                    if (genericFactory.loadModulesCache().get(i).getClassName().equals(classname)) {
                        idx = i;
                        break;
                    }
                }
                if (idx >= 0) {
                    DockingFrameworkApp.this.uiFramework.addTopModuleWindow(genericFactory.loadModulesCache().get(idx), true, true);   
                }
            }
        };

        DebugLogger.getInstance().addText(" @ Loading modules....");
        DebugLogger.getInstance().addText(" @ Dealing with.... module " + module.getClassName());
        boolean mustskip = moduleLoadPref.equals("required") && !module.getClassName().equals(requiredModuleName);
        if (mustskip) {
            return;
        }

        //Neglect modules which are disabled or modules which are not within the role category
        boolean isEnabled = UtilsFactory.isModuleEnabled(module, coreCfg.getUserInfo().getRole(), coreCfg.getUserInfo().getAbrev());
        if (!isEnabled) {
            //System.out.println(module.getClassName() + " is not enabled acording to role system...dismiss");
            return;
        }

        if (module.getAutoStart() != BeanModule.NO) {
            DebugLogger.getInstance().addText(" @ Trying to open via uiFramework.addTopModuleWindow " + module.getClassName());
            
            //Problem after module is added it calls adjustframework
            //and after that checks if ui must be minimized... 
            //solution: add third parameter boolean true/false if adjustFramework must be called or not
            String winID = this.uiFramework.addTopModuleWindow(module, false, false);
            TopModuleWindow win = null;
            //Make sure required module is not is sleep mode otherwise minimize ui
            if(winID!=null && module.getClassName().equals(requiredModuleName))
            {
                //Check if user has specified preferred startup policy
               win = TopModuleRegistry.findId(winID);
               String property = coreCfg.getUserPreferences().getProperty("framework.startupPolicy", "NORMAL"); //TOOLBAR
               if (!property.equalsIgnoreCase("TOOLBAR")) {
                    if (win != null && win.getModuleStatus() == TopModuleWindow.STATUS_SLEEPING) {
                        jToggleButton1.setSelected(true);
                        windowManager.setDisplayMode(WindowManager.MODE_TOOLBAR);
                    } else {
                        if (appClass.getName().equals(anuncisClassName)) {
                            windowManager.setDisplayMode(WindowManager.MODE_SIMPLE);
                        }
                    }
                } else {
                    jToggleButton1.setSelected(true);
                    windowManager.setDisplayMode(WindowManager.MODE_TOOLBAR);
                }
            }

        }

        //Add link to respective anchor points
        for (BeanAnchorPoint bap : module.getListAnchorPoints()) {
            if (bap.getLocation().equals("toolbar")) {
                JToolBar target = null;

                int pos = WindowManager.findToolBarItemPos(jToolBar1, bap.getParentId());
                if (pos >= 0) {
                    target = (JToolBar) jToolBar1.getComponent(pos);
                } else {
                    target = new JToolBar();
                    target.setName(bap.getParentId());
                    jToolBar1.add(bap.getParentId(), target);
                }

                JButton button = new JButton();
                button.setIcon(module.getModuleIcon24x24());
                button.setActionCommand(module.getClassName());
                button.setToolTipText(module.getNameForLocale(coreCfg.core_lang));
                button.addActionListener(listenerNewModule);
                target.add(button);
                
                vtoolbar.addModuleIcon(cloneButton(button));
                
            } else if (bap.getLocation().equals("menu")) {
                JMenu target = jMenuModules;//(JMenu) WindowManager.getContainerObject(jMenuBar1, bap.getParentId());
                JMenuItem menuitem = new JMenuItem();
                menuitem.setText(module.getNameForLocale(coreCfg.core_lang));
                if (bap.isShowIcon()) {
                    menuitem.setIcon(module.getModuleIcon24x24());
                }

                if (!module.getShortcut().isEmpty()) {
                    String mask = "";
                    String key = module.getShortcut();

                    KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
                    if (keyStroke != null) {
                        menuitem.setAccelerator(keyStroke);
                    }
                }

                menuitem.setActionCommand(module.getClassName());
                menuitem.addActionListener(listenerNewModule);
                if (target != null) {
                    if (bap.getPos() < 0 || bap.getPos() >= target.getItemCount()) {
                        target.add(menuitem);
                    } else {
                        target.add(menuitem, bap.getPos());
                    }
                } else {
                    //System.out.println("menu:: problem target is null");
                }

            } else {
                //System.out.println("Unsupported anchor point");
            }

        }
    
    }

    /////////////////////////////////////////////
    private void loadDesktopBrowse(BeanModule module)
    {
           if( !UtilsFactory.isModuleEnabled(module, coreCfg.getUserInfo().getRole(), coreCfg.getUserInfo().getAbrev()))
            {
                return;
            }
           
            ArrayList<BeanAnchorPoint> listAnchorPoints = module.getListAnchorPoints();
            for (BeanAnchorPoint bap : listAnchorPoints) {
                if (bap.getLocation().equalsIgnoreCase("toolbar")) {
                    JButton linkButton = new JButton();
                    linkButton.setIcon(module.getModuleIcon24x24());
                    linkButton.setToolTipText(module.getNameForLocale(coreCfg.core_lang));
                    linkButton.setActionCommand(module.getClassName());


                    linkButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //Retrieve which is the actual beanModule
                            BeanModule beanModule = null;
                            for (BeanModule mod : genericFactory.loadModulesCache()) {
                                if (mod.getClassName().equals(e.getActionCommand())) {
                                    beanModule = mod;
                                    break;
                                }
                            }

                            if (beanModule == null) {
                                return; // Error : Not found
                            }
                            if(beanModule.getIniParameters().containsKey("url"))
                            {
                                String url = (String) beanModule.getIniParameters().get("url");
                                if(!url.startsWith("http:"))
                                {
                                    url = "http:"+url;
                                }
                                if(beanModule.getIniParameters().containsKey("urlparameters") && coreCfg.getUserInfo().getIdSGD()>0)
                                {
                                    String parsedurlparams = (String) beanModule.getIniParameters().get("urlparameters");
                                    while(parsedurlparams.contains("$(abrev)"))
                                    {
                                        parsedurlparams = parsedurlparams.replace("$(abrev)", coreCfg.getUserInfo().getAbrev());
                                    }
                                    while(parsedurlparams.contains("$(winuser)"))
                                    {                                
                                         parsedurlparams = parsedurlparams.replace("$(winuser)", coreCfg.getUserInfo().getSystemUser());
                                    }
                                    url += "?"+parsedurlparams;
                                }
                                URI uri = java.net.URI.create(url);
                                try {
                                    Desktop.getDesktop().browse(uri);
                                } catch (IOException ex) {
                                    Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            
                        }
                    });

                JToolBar target = null;

                int pos = WindowManager.findToolBarItemPos(jToolBar1, bap.getParentId());
                if (pos >= 0) {
                    target = (JToolBar) jToolBar1.getComponent(pos);
                } else {
                    target = new JToolBar();
                    target.setName(bap.getParentId());
                    jToolBar1.add(bap.getParentId(), target);
                }
                target.add(linkButton);
                /////////
                vtoolbar.addModuleIcon(cloneButton(linkButton));
                }
            }
    }
    
    /////////////////////////////////////////////
    private void loadDesktopOpen(BeanModule module)
    {
        //Make sure that it is allowed to load this module
        if (!UtilsFactory.isModuleEnabled(module, coreCfg.getUserInfo().getRole(), coreCfg.getUserInfo().getAbrev())) {
            return;
        }
        ArrayList<BeanAnchorPoint> listAnchorPoints = module.getListAnchorPoints();
        for (BeanAnchorPoint bap : listAnchorPoints) {
            if (bap.getLocation().equalsIgnoreCase("toolbar")) {
                JButton linkButton = new JButton();
                linkButton.setIcon(module.getModuleIcon24x24());
                linkButton.setToolTipText(module.getNameForLocale(coreCfg.core_lang));
                linkButton.setActionCommand(module.getClassName());


                linkButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //Retrieve which is the actual beanModule
                        BeanModule beanModule = null;
                        for (BeanModule mod : genericFactory.loadModulesCache()) {
                            if (mod.getClassName().equals(e.getActionCommand())) {
                                beanModule = mod;
                                break;
                            }
                        }

                        if (beanModule == null) {
                            return; // Error : Not found
                        }
                        if (beanModule.getIniParameters().containsKey("file")) {
                            File file = new File((String) beanModule.getIniParameters().get("file"));
                            if (file.exists()) {
                                try {
                                    Desktop.getDesktop().open(file);
                                } catch (IOException ex) {
                                    Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                });

                JToolBar target = null;

                int pos = WindowManager.findToolBarItemPos(jToolBar1, bap.getParentId());
                if (pos >= 0) {
                    target = (JToolBar) jToolBar1.getComponent(pos);
                } else {
                    target = new JToolBar();
                    target.setName(bap.getParentId());
                    jToolBar1.add(bap.getParentId(), target);
                }
                target.add(linkButton);
                 
                /////////
                vtoolbar.addModuleIcon(cloneButton(linkButton));
            }
        }
    }

    protected void onSwitchUser(boolean mustShow) {

        //System.out.println("OnSwithUSer = " + user);
        //Checks if the user is valid if not require validation login
        boolean validated = false;

        if (!mustShow && LoginValidation.isValidated(user, pwd, coreCfg.getMysql())) {
            validated = true;
        }
    
        LoginInterface dlg = null;
        if(!validated || mustShow)
        {
       
        if(!administrativeApp)
        {        
            dlg = (LoginInterface) new Login(this, false, this.getAppDisplayName(), "", coreCfg);
            dlg.display();          
        }
        else
        {
            dlg = (LoginInterface) new LoginAdmin(this, true);            
            dlg.display();          
        }
            
            int code = dlg.getValidation();
            validated = code==1;
            if(code<0)
            {
                this.quitApp();
            }
            
            iWasAdmin = user!=null && user.equals("ADMIN");
            //System.out.println("OnSwithUSer & iwasadmin= "+user+" "+iWasAdmin);
         
            if (user != null && !user.isEmpty()) {
                ArrayList<String> tobeRemoved = new ArrayList<String>();
                for (String key : TopModuleDeamon.getActiveDeamons().keySet()) {
                    if (key.endsWith("@" + user)) {
                        tobeRemoved.add(key);
                        TopModuleDeamon.getActiveDeamons().get(key).stop();
                        //This yields to concurrent exception
                        //TopModuleDeamon.getActiveDeamons().remove(key);
                    }
                }
                for(String key: tobeRemoved)
                {
                    TopModuleDeamon.getActiveDeamons().remove(key);
                }
                tobeRemoved.clear();
                    
            }
            
            user = dlg.getAbrev();
            pwd = dlg.getPwd();
            role = dlg.getRole();
        }
        
         
        
        if(validated)
        {
            vtoolbar.clear();
            vtoolbar.addModuleIcon(this.cloneButton(switchButton));
            //Must create a new instance of stamper
            if(stamper!=null)
            {
                stamper.outStamp();
            }
           
            stamper.initialize(this.appNameId, user, coreCfg);
            stamper.inStamp();
            clearToolBar();
            jMenuModules.removeAll();
            ((StatusBarZone) jStatusBar.getZone("second")).clear();
      
            //Cal tancar tots els moduls
            uiFramework.closeAll();
            
            //Delega al nucli iesDigital l'usuari amb el sistema de roles
            coreCfg.setUser(user);
            jMenuAdministrador.setVisible(coreCfg.getUserInfo().getGrant()==User.ADMIN);
     
            //Look for new updates of system (only for certain users)
            if (coreCfg.getUserInfo().getAbrev().equalsIgnoreCase("ADMIN") || coreCfg.getUserInfo().getRole().equalsIgnoreCase("ADMIN")
                    || coreCfg.getUserInfo().getGrant() == User.ADMIN || coreCfg.getUserInfo().getGrant() == User.PREF) {
                new UpdateTask().execute();
            }

     
             //Delega al nucli del sgd, el mateix usuari (si l'usuari no té associat
            //una idSGD, aleshores l'usuari és null)
            int idSGD = coreCfg.getUserInfo().getIdSGD();
            
            try{
            Profesores profesor = null;
            if(idSGD>=0)
            {
                profesor = coreCfg.getSgdClient().getProfesores(idSGD+"");
                boolean enableLogger = profesor!=null && (Boolean) CoreCfg.configTableMap.get("sgdEnableLogger");
                coreCfg.getSgdClient().setUser(profesor, enableLogger);
                vtoolbar.setSystemUser(profesor.getSystemUser(), profesor.getClaveUP());
            }
            else if(user.equalsIgnoreCase("ADMIN"))
            {
                vtoolbar.setSystemUser(user, CoreCfg.core_pwdSU);
            }
           
            }
            catch(Exception ex)
            {
                Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            CoreCfg.admin = coreCfg.getUserInfo().getGrant()==User.ADMIN || coreCfg.getUserInfo().getGrant()==User.PREF;
            
            if(user.equalsIgnoreCase("ADMIN"))
            {
                jLabelUser.setText(bundle.getString("administrador"));
                jLabelUser.setToolTipText("ADMIN");
                role = "ADMIN";
            }
            else if(user.equalsIgnoreCase("GUARD"))
            {
                jLabelUser.setText(bundle.getString("usuariGuardies"));
                jLabelUser.setToolTipText("GUARD");
                role = "GUARD";
            }
            else
            {
                role = coreCfg.getUserInfo().getRole();
                jLabelUser.setText(coreCfg.getUserInfo().getName());
                jLabelUser.setToolTipText(" ("+role+") ");
                
            }
            if(dlg!=null)
            {
                dlg.dispose();
            }
            
           
           wait = new Loading();
           wait.setVisible(true);
           
           new LongTask().execute();
           
        }
        
        
    }

  
   
  
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonHT;
    private javax.swing.JLabel jLabelUser;
    private javax.swing.JMenuItem jMenuAbout;
    private javax.swing.JMenu jMenuAdministrador;
    private javax.swing.JMenu jMenuAjuda;
    protected javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuDistribucio;
    private javax.swing.JMenu jMenuFitxer;
    private javax.swing.JMenu jMenuIdioma;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JCheckBoxMenuItem jMenuItemFullscreen;
    private javax.swing.JMenuItem jMenuItemStartAdmin;
    private javax.swing.JMenu jMenuLookandfeel;
    private javax.swing.JMenu jMenuModules;
    private javax.swing.JMenu jMenuWindow;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    protected com.l2fprod.common.swing.StatusBar jStatusBar;
    protected javax.swing.JToggleButton jToggleButton1;
    protected javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBarModules;
    private javax.swing.JButton switchButton;
    private javax.swing.JLabel switchLabel;
    // End of variables declaration//GEN-END:variables

    public String getAppDisplayName() {
        return appDisplayName;
    }

    public void setAppDisplayName(String appDisplayName) {
        this.appDisplayName = appDisplayName;
        this.setTitle("iesDigital " + CoreCfg.VERSION + "   ·   " + appDisplayName+"   ·   "+coreCfg.anyAcademic+"-"+(coreCfg.anyAcademic+1));
    }

    public String getAppNameId() {
        return appNameId;
    }

    public void setAppNameId(String appNameId) {
        this.appNameId = appNameId;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    
    //Override these methods
    
    public void onWindowOpened() {    
    }

    public void onSwitchUserValid(){   
       
        loadModules();
    }

    public boolean isMaximum() {
        return maximum;
    }

    public void setMaximum(boolean maximum) {
        this.maximum = maximum;
        if(maximum)
        {
            this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
        }
        else
        {
            this.setExtendedState(JFrame.NORMAL);
        }
    }

    public boolean isAdministrativeApp() {
        return administrativeApp;
    }

    public void setAdministrativeApp(boolean administrativeApp) {
        this.administrativeApp = administrativeApp;
    }

    
    
    //Remove all components except the one named jToolBarModules
    protected void clearToolBar() {
        
        for(Component c: jToolBar1.getComponents())
        {
            if(c.getName()==null || !c.getName().equals("jToolBarModules"))
            {
                jToolBar1.remove(c);
            }
        }
         
    }
   
    public void enableLaunchAdministrador(boolean enable) {
        jMenuItemStartAdmin.setEnabled(enable);
    }

    
    @Override
     public void quitApp() {
        if(hook!=null)
        {
            Runtime.getRuntime().removeShutdownHook(hook);
        }
        System.exit(0);
    }


     
	/**
	 * Installs the JGoodies Look & Feels, if available, in classpath.
	 */
     public final void initializeLookAndFeels() {
		// if in classpath thry to load JGoodies Plastic Look & Feel
		try {
			LookAndFeelInfo[] lnfs = UIManager.getInstalledLookAndFeels();
			boolean found = false;
                        boolean foundNimbus = false;
			for (int i = 0; i < lnfs.length; i++) {
				if (lnfs[i].getName().equals("JGoodies Plastic 3D")) {
					found = true;
				}
                                else if (lnfs[i].getName().equals("Nimbus")) {
					foundNimbus = true;
				}
			}
			if (!found) {
				UIManager.installLookAndFeel("JGoodies Plastic 3D",
						"com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
			}
			String os = System.getProperty("os.name");
			FontSet fontSet = null;
			if (os.startsWith("Windows")) {
				fontSet = FontSets.createDefaultFontSet(new Font(
						"arial unicode MS", Font.PLAIN, 12));
			} else {
				fontSet = FontSets.createDefaultFontSet(new Font(
						"arial unicode", Font.PLAIN, 12));				
			}
			FontPolicy fixedPolicy = FontPolicies.createFixedPolicy(fontSet);
			PlasticLookAndFeel.setFontPolicy(fixedPolicy);

                        
                        if(foundNimbus)
                        {
                            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                    UIManager.setLookAndFeel(info.getClassName());
                                    break;
                                }
                            }
                        }
                        else
                        {
                            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
                        }
                   } catch (Throwable t) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
			}
		}
        
                
                UIManager.put("nimbusDisabledText", new java.awt.Color(100, 70, 60));
                SwingUtilities.updateComponentTreeUI(DockingFrameworkApp.this);        
                LookAndFeel laf = UIManager.getLookAndFeel();               
                if ( laf != null ) {
                     UIDefaults def = laf.getDefaults();
                    def.put("Button.contentMargins", new Insets(4,4,4,4));
                }
	}
     
     /**
      * Clone button
      * @param button
      * @return 
      */
     private JButton cloneButton(JButton button)
     {
        JButton newButton = new JButton();
        String text = button.getToolTipText();
       
        if(text.length()>10)
        {
            text = text.substring(0,9);
        }
        
        //newButton.setText(text.toUpperCase());
        newButton.setIcon(button.getIcon());
        newButton.setFont(new Font("arial unicode", Font.PLAIN, 8));
        newButton.setVerticalTextPosition(JLabel.BOTTOM);
        newButton.setHorizontalTextPosition(JLabel.CENTER);
        newButton.setToolTipText(button.getToolTipText());
        newButton.setActionCommand(button.getActionCommand());
        ActionListener[] actionListeners = button.getActionListeners();
        if(actionListeners.length>0)
        {
            newButton.addActionListener(actionListeners[0]);
        }
        return newButton;
     }
     
     
    protected class UpdateTask extends javax.swing.SwingWorker<Void,Void>
    {
    
        @Override
        protected Void doInBackground() throws Exception {
        
        try {
            //Do updater check in background
            RemoteUpdater updater = new RemoteUpdater(CoreIni.getCore_repoURLs());
            String latestVersion = updater.getLatestVersion();
            
            if(StringUtils.compare(latestVersion, CoreCfg.VERSION)>0)
            {
                JLinkButton label = new JLinkButton();
                label.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/bubble1.png")));
                label.setText("New version "+latestVersion);      
                ((StatusBarZone) DockingFrameworkApp.this.jStatusBar.getZone("second")).addComponent(label);
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("DO SOMETHING");
                    }

                });
            }
            uiFramework.setRemoteUpdater( updater );
            
            
        } catch (Exception ex) { 
            //Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
        }  catch (Throwable ex) {
           // Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    }
    
    protected class LongTask extends javax.swing.SwingWorker<Void,Void>
    {
        
        @Override
        protected Void doInBackground() throws Exception {
            //Must be implemented by load modules
            onSwitchUserValid();    
            wait.dispose();
            return null;
        }
        
    }
    
}
