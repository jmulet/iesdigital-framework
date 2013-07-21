/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import com.l2fprod.common.swing.StatusBar;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.StringViewMap;

import net.infonode.util.Direction;
import org.iesapp.framework.pluggable.modulesAPI.BeanModule;
import org.iesapp.framework.util.CoreCfg;


/**
 *
 * @author Josep
 */
public class UIFrameworkIN implements UIFramework{
    private JFrame frame;
    private JToolBar jToolBar1;
    private StatusBar jStatusBar1;
    private String appDisplayName;
    private JPanel mainPanel;
   
    private final CoreCfg coreCfg;
    private Stamp stamper;
    private ArrayList<JMenu> beforeMenu;
    private ArrayList<JMenu> afterMenu;
    private final SysTray stray;
    private final JToggleButton jToggleButton;
    private WindowManager windowManager;
    private StringViewMap viewMap;
    private RootWindow rootWindow;
    private RootWindowProperties properties = new RootWindowProperties();
    private DockingWindowsTheme currentTheme = new ShapedGradientDockingTheme();
    private TopModuleWindow lastFocusedTopModuleWindow;
     

    public UIFrameworkIN(CoreCfg coreCfg, Stamp stamper, SysTray stray, JToggleButton jToggleButton1)
    {
        this.coreCfg = coreCfg;
        this.stamper = stamper;
        this.stray = stray;
        this.jToggleButton = jToggleButton1;  
    }
    
    @Override
    public void initialize(final WindowManager windowManager, final String appDisplayName,
                        final ArrayList<JMenu> beforeMenu, final ArrayList<JMenu> afterMenu ) {
        this.windowManager = windowManager;
        this.frame = windowManager.getFrame();
        this.beforeMenu = beforeMenu;
        this.afterMenu = afterMenu;
        this.jToolBar1 = windowManager.getjToolBar1();
        this.jStatusBar1 = windowManager.getjStatusBar1();
        this.appDisplayName = appDisplayName;
        this.mainPanel = windowManager.getMainPanel();
    
        //Layout
        setRootWindow();
       
      
    }

     
    
    @Override
    public String addTopModuleWindow(BeanModule module, boolean mustShow, boolean adjust) {
        
        
        String id = null;
        try {
  
            boolean alreadyInstanced = false;
            ArrayList<String> ninstances = TopModuleRegistry.getCurrentInstancesOf(module.getClassName());
            DebugLogger.getInstance().addText("NUM instances of class "+module.getClassName()+" = "+ninstances.size());
            if(ninstances.size()>0)            
            {
                    alreadyInstanced = true;                    
                    TopModuleWindow findwin = TopModuleRegistry.findId(ninstances.get(0));
                    if(findwin!=null)
                    {
                        //Aquesta finestra estarà en algun tabbedpane pero primer l'hem de cercar i seleccionar
                        
                        for(int i=0; i<viewMap.getViewCount(); i++)
                        {
                            View viewtmp = viewMap.getViewAtIndex(i);
                            TopModuleWindow tmw = extractTopModuleWindowFromView(viewtmp);
                            if(tmw!=null && tmw.equals(findwin))
                            {
                                viewtmp.makeVisible();
                                //viewtmp.maximize();
                                viewtmp.requestFocus();
                                viewtmp.restore();
                                if(adjust)
                                {
                                    windowManager.adjustFrameworkLayout(0);  
                                }
                                break;
                            }
                        }
//                        for(JRTabbedPane jrTabbedPane1: jrTabbedPanes)
//                        {
//                            for(int i=0; i<jrTabbedPane1.getTabCount(); i++)
//                            {
//                                 
//                                if(jrTabbedPane1.getTopModuleWindowAt(i) == findwin)
//                                {
//                                    station.setSelectedJRTabbedPane(jrTabbedPane1);
//                                    jrTabbedPane1.setSelectedTopModuleWindow(findwin);
//                                    windowManager.adjustFrameworkLayout(0);  
//                                    break;
//                                }
//                                
//                            }
//                        }
                        //currentTabbedPane.setSelectedComponent(findwin);
                    }
            }
            
            DebugLogger.getInstance().addText("module ismultipleinstance --> "+module.isMultipleInstance());
            if(!alreadyInstanced || (alreadyInstanced && module.isMultipleInstance()) )
            {
                DebugLogger.getInstance().addText("CREATING::::");
                //ModuleClassLoader moduleClassLoader = new ModuleClassLoader(ClassLoader.getSystemClassLoader());
                org.iesapp.framework.util.JarClassLoader.getInstance().addJarToClasspath(new File(CoreCfg.contextRoot+"\\modules\\"+module.getJar() ));
                //Include any other required lib
                //Adds to classpath any other required lib for this module
                for (String s : module.getRequiredLibs()) {
                    org.iesapp.framework.util.JarClassLoader.getInstance().addJarToClasspath(new File(CoreCfg.contextRoot + "\\" + s));
                }
                DebugLogger.getInstance().addText("Trying to find class for module :: "+module.getClassName());
                Class<?> forName = Class.forName(module.getClassName());
                 //org.openide.util.lookup.Lookups.forPath("modules").lookupAll(forName);            
                DebugLogger.getInstance().addText("Creating a new instance of module :: "+module.getClassName());
                TopModuleWindow win = (TopModuleWindow) forName.newInstance();
                
                //Pass bean & ini parameters & initialize with moduleClassLoader
                win.setBeanModule(module);
                DebugLogger.getInstance().addText("@@@@@ The module "+module.getClassName()+" contains #plugins "+module.getInstalledPlugins().size());
                win.iniParameters.setMap( module.getIniParameters() );
                DebugLogger.getInstance().addText("@@@@@ initializing The module "+module.getClassName());
                win.initialize(stamper, stray, coreCfg, (UIFramework) this);
                DebugLogger.getInstance().addText("...Done");
                DebugLogger.getInstance().addText("...is opening required? "+win.isOpeningRequired());
                
                win.setMultipleInstance(module.isMultipleInstance());
                
                if(mustShow || module.getAutoStart()==BeanModule.YES || (module.getAutoStart()==BeanModule.ONDEMAND && win.isOpeningRequired()))
                {                     
                    
                    String displayname = module.getNameForLocale(coreCfg.core_lang);
                    DebugLogger.getInstance().addText("Process win.refreshUI()...");
                    

                    if (module.getDisplayPoint().getLocation().equals("statusbar")) {
                        ((StatusBarZone) jStatusBar1.getZone("second")).addComponent(win);
                    } else if (module.getDisplayPoint().getLocation().equals("topwindow")) {
                        id = TopModuleRegistry.register(forName.getName(), win);
                        win.setName(id);
                        View createNewView = createNewView(win, displayname, module.getModuleIcon16x16(), module.isClosable());
                       
                        viewMap.addView(id, createNewView);
//                        TabWindow target = tabWindowCenter;
//                        if(module.getDisplayPoint().getParentId().equals("right")) {
//                           // target = tabWindowRight;
//                        }
//                        target.addTab(createNewView);
                        createNewView.makeVisible();
                        //target.makeVisible();
                        DockingUtil.addWindow(createNewView, rootWindow);
                        createNewView.requestFocus();
                       
                        //createNewView.getViewProperties().getViewTitleBarProperties().getFocusedProperties().getCloseButtonProperties().setVisible(module.isClosable());
                        //createNewView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(module.isClosable());
                        if(adjust)
                        {
                            windowManager.adjustFrameworkLayout(0); 
                        }
                    }

                    win.refreshUI();
                    DebugLogger.getInstance().addText("...done win.refreshUI()!");
                    
                } else {
                    win.dispose();
                }
            }
           
        } catch (Exception ex) {
            Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), "Problem loading module: "+module.getClassName()+"\n"+ex);
        }
        DebugLogger.getInstance().addText(">>>>> quit UIFrameworkJR.addTopModuleWindow");
        return id;
   
    }
   
    @Override
    public TopModuleWindow getSelectedTopModuleWindow() {  
        return this.lastFocusedTopModuleWindow;
    }

    @Override
    public void closeAll() {
        
//        for(int i=0; i<rootWindow.getChildWindowCount(); i++)
//        {
//            DockingWindow childWindow = rootWindow.getChildWindow(i);
//            recursivelyClose(childWindow);
//        }
        
        for(int i=0; i<viewMap.getViewCount(); i++)
        {
            View view = viewMap.getViewAtIndex(i);
            if(view!=null)
            {
                TopModuleWindow win = this.extractTopModuleWindowFromView(view);
                if(win!=null)
                {
                    view.close();
                    viewMap.removeView(win.getName());
                    rootWindow.remove(view.getParent());
                    TopModuleRegistry.remove(win.getName());
                    win.dispose();                    
                }    
            }
            
        }
     
        for(int i=0; i<rootWindow.getChildWindowCount(); i++)
        {
            DockingWindow childWindow = rootWindow.getChildWindow(i);
            childWindow.close();
        }
        //System.gc();
    }

    private void recursivelyClose(DockingWindow dwindow){
        for(int i=0; i<dwindow.getChildWindowCount(); i++)
        {
            DockingWindow childWindow = dwindow.getChildWindow(i);
            recursivelyClose(childWindow);
        }
    }
       
    @Override
    public void setMenus(final TopModuleWindow win)
    {
        //Remove All except the first component if exits which is the module toolbar
        clearToolBar();
        
        JMenuBar menuBar = frame.getJMenuBar();        
//        int pos = WindowManager.findMenuItemPos(menuBar, "jMenuFitxer");
//        JMenu jMenuFitxer = menuBar.getMenu(pos);
//        
//        pos = WindowManager.findMenuItemPos(menuBar, "jMenuModules");
//        JMenu jMenuModules = menuBar.getMenu(pos);
//        
//        pos = WindowManager.findMenuItemPos(menuBar, "jMenuAdministracio");
//        JMenu jMenuAdministracio = menuBar.getMenu(pos);
//        //Eliminar lo que hagin pogut posar les altres aplicacions;
//        
//        pos = WindowManager.findMenuItemPos(menuBar, "jMenuWindow");
//        JMenu jMenuWindow = menuBar.getMenu(pos);
//        
//        pos = WindowManager.findMenuItemPos(menuBar, "jMenuAjuda");
//        JMenu jMenuAjuda = menuBar.getMenu(pos);
//        
       ((StatusBarZone) jStatusBar1.getZone("third")).clear();
        
       
        //create a new menubar
        JMenuBar jMenuBar2 = new JMenuBar();
        jMenuBar2.setVisible(menuBar.isVisible());
        for(JMenu jmenu: beforeMenu)
        {
            jMenuBar2.add(jmenu);
        }
               
        //Aqui els diferents mòduls populen
        if(win!=null)
        {
            win.setMenuEntries(jMenuBar2, jToolBar1, jStatusBar1);
        }
        
        for(JMenu jmenu: afterMenu)
        {
            jMenuBar2.add(jmenu);
        }
        frame.setJMenuBar(jMenuBar2);
        
//        if(moduleLoadPref.equals("none"))
//        {
//            ((StatusBarZone)jStatusBar.getZone("second")).addComponent(new JLabel("-module:none | No modules have been loaded"));
//        }
//        else if(moduleLoadPref.equals("required"))
//        {
//            ((StatusBarZone)jStatusBar.getZone("second")).addComponent(new JLabel("-module:required | Only required module has been loaded"));
//        }
//              
//        jToolBar1.validate();
//        jToolBar1.repaint();
        
        jToolBar1.validate();
        jToolBar1.repaint();
        jMenuBar2.validate();
        jMenuBar2.repaint();
    }
    
    private void clearToolBar() {
        
        for(Component c: jToolBar1.getComponents())
        {
            if(c.getName()==null || !c.getName().equals("jToolBarModules"))
            {
                jToolBar1.remove(c);
            }
        }
         
    }

    
   

     
    @Override
    public String addTopModuleWindow(TopModuleWindow win, boolean closable, boolean adjust) {
      return addTopModuleWindow(win,"",closable, adjust);
    }

    @Override
    public String addTopModuleWindow(TopModuleWindow win, String locationId, boolean closable, boolean adjust) {
        if(adjust)
        {
            windowManager.adjustFrameworkLayout(1);
        }
        
        String id = TopModuleRegistry.register(win.getClass().getName(), win);
        win.setName(id);
        View createNewView = createNewView(win, win.getModuleDisplayName(), null, closable);
         
        viewMap.addView(id, createNewView);
//        TabWindow target = tabWindowCenter;
//        if(locationId.equals("right")) {
//            //target = tabWindowRight;
//        }
//        target.addTab(createNewView);
       
        createNewView.makeVisible();
        //target.makeVisible();
        DockingUtil.addWindow(createNewView, rootWindow);
        createNewView.requestFocus();
        //Li passam el tabComponent
        
        return id;
    }

    

    @Override
    public void setSelectedTopWindow(String identifier) {
       
    }

    @Override
    public void setDistribution(int i) {
    }

    private void setRootWindow() {
        viewMap = new StringViewMap();
        rootWindow = DockingUtil.createRootWindow(viewMap, null, true);
//        //Create a suitable layout center-left
//        tabWindowCenter = new TabWindow();
//        tabWindowCenter.getWindowProperties().setCloseEnabled(false);
//        
//        rootWindow.setWindow( tabWindowCenter );
        //DockingUtil.addWindow(tabWindowCenter, rootWindow);
        properties.addSuperObject(currentTheme.getRootWindowProperties());
        properties.getDockingWindowProperties().setDockEnabled(false);
        properties.getDockingWindowProperties().setUndockEnabled(false);
        properties.getDockingWindowProperties().setCloseEnabled(false); //Disable massive close
        rootWindow.getRootWindowProperties().addSuperObject(properties);

        // Enable the bottom window bar
        rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);

        // Add a listener which shows dialogs when a window is closing or closed.
        rootWindow.addListener(new DockingWindowAdapter() {
           
            @Override
            public void viewFocusChanged(View view, View view1) {
                //We have a problem when view1 is null (So get the defaultView)
//                if(view1==null)
//                {
//                    DockingWindow lastFocusedChildWindow = rootWindow.getLastFocusedChildWindow();
//                    if(lastFocusedChildWindow instanceof View)
//                    {
//                        view1 = (View) lastFocusedChildWindow;
//                    }
//                    
//                }
                if(view1==null)
                {
                    return;
                }
                TopModuleWindow win = extractTopModuleWindowFromView(view1);
                
                if (win != null) {
                    lastFocusedTopModuleWindow = win;
                    frame.setTitle("iesDigital " + CoreCfg.VERSION + "   ·   " + appDisplayName + " / " + win.getModuleDisplayName() + " " + win.getModuleVersion() + "   ·   " + coreCfg.anyAcademic + "-" + (coreCfg.anyAcademic + 1));

                } else {
                    frame.setTitle("iesDigital " + CoreCfg.VERSION + "   ·   " + appDisplayName + "   ·   " + coreCfg.anyAcademic + "-" + (coreCfg.anyAcademic + 1));
                }
                setMenus(win);
            }

            ;
     
      

            @Override
            public void windowClosing(DockingWindow window) throws OperationAbortedException {
                // Confirm close operation
                System.out.println("window" + window + " window.getChildWindowCount() " + window.getChildWindowCount());
                for (int i = 0; i < window.getChildWindowCount(); i++) {
                    DockingWindow child = window.getChildWindow(i);
                    if (child instanceof View) {
                        View view = (View) child;
                        TopModuleWindow win = extractTopModuleWindowFromView(view);
                        if(win!=null)
                        {
                            String id = win.getName();
                            System.out.println("About to close win  with id " + id + " win .." + win);
                            windowManager.adjustFrameworkLayout(-1);
                            TopModuleRegistry.remove(id);
                            win.dispose();
                            //Remove this view from map
                            viewMap.removeView(id);
                        }
                      
                        
                    }
                    
                }
                //Close the window itself
                if (window instanceof View) {
                        View view = (View) window;
                        TopModuleWindow win = extractTopModuleWindowFromView(view);
                        if(win!=null)
                        {
                            String id = win.getName();
                            System.out.println("About to close win  with id " + id + " win .." + win);
                            windowManager.adjustFrameworkLayout(-1);
                            TopModuleRegistry.remove(id);
                            win.dispose();
                            viewMap.removeView(id);
                        }

                    }
            }

            @Override
            public void windowDocking(DockingWindow window) throws OperationAbortedException {
                // Confirm dock operation
                if (JOptionPane.showConfirmDialog(frame, "Really dock window '" + window + "'?") != JOptionPane.YES_OPTION) {
                    throw new OperationAbortedException("Window dock was aborted!");
                }
            }

            @Override
            public void windowUndocking(DockingWindow window) throws OperationAbortedException {
                // Confirm undock operation 
                if (JOptionPane.showConfirmDialog(frame, "Really undock window '" + window + "'?") != JOptionPane.YES_OPTION) {
                    throw new OperationAbortedException("Window undock was aborted!");
                }
            }
        });

    // Add a mouse button listener that closes a window when it's clicked with the middle mouse button.
    rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
    mainPanel.add(rootWindow, BorderLayout.CENTER);  
    }

    private View createNewView(TopModuleWindow win, String displayname, ImageIcon moduleIcon16x16, boolean closable) {

        View view = new View(displayname, moduleIcon16x16, new JScrollPane(win));
       ((DockingWindow) view).getWindowProperties().setCloseEnabled(closable);
        return view;
    }

    private TopModuleWindow extractTopModuleWindowFromView(View view) {
        TopModuleWindow win = null;
        Component component = view.getComponent();
        if (component instanceof JScrollPane) {
            Component comp = ((JScrollPane) component).getViewport().getView();
            if (comp instanceof TopModuleWindow) {
                win = (TopModuleWindow) comp;
            }
        }
        return win;
    }

    public String printLayout()
    {
        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            rootWindow.write(out);
            out.close();
            return bos.toString();
        } catch (IOException ex) {
            Logger.getLogger(UIFrameworkIN.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(UIFrameworkIN.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
