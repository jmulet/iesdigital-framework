/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import com.l2fprod.common.swing.JLinkButton;
import com.l2fprod.common.swing.StatusBar;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.StringViewMap;

import net.infonode.util.Direction;
import org.iesapp.framework.dialogs.ModuleUpdaterDlg;
import org.iesapp.framework.pluggable.modulesAPI.BeanModule;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.JarClassLoader;
import org.iesapp.updater.RemoteUpdater;
import org.iesapp.util.StringUtils;


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
    private final Stamp stamper;
    private ArrayList<JMenu> beforeMenu;
    private ArrayList<JMenu> afterMenu;
    private final SysTray stray;
    private final JToggleButton jToggleButton;
    private WindowManager windowManager;
    private StringViewMap viewMap;
    private RootWindow rootWindow;
    private final RootWindowProperties properties = new RootWindowProperties();
    private final DockingWindowsTheme currentTheme = new ShapedGradientDockingTheme();
    private TopModuleWindow lastFocusedTopModuleWindow;
    private RemoteUpdater remoteUpdater;
    private final HashMap<String, ModuleUpdaterDlg> updatersMap;
    private String currentAppClass;
     
    public UIFrameworkIN(CoreCfg coreCfg, Stamp stamper, SysTray stray, JToggleButton jToggleButton1)
    {
        this.coreCfg = coreCfg;
        this.stamper = stamper;
        this.stray = stray;
        this.jToggleButton = jToggleButton1;  
        updatersMap = new HashMap<String, ModuleUpdaterDlg>();
    }
    
    @Override
    public void initialize(final WindowManager windowManager, final String appDisplayName,
                        final ArrayList<JMenu> beforeMenu, final ArrayList<JMenu> afterMenu, 
                        final String currentAppClass) {
        this.windowManager = windowManager;
        this.frame = windowManager.getFrame();
        this.beforeMenu = beforeMenu;
        this.afterMenu = afterMenu;
        this.jToolBar1 = windowManager.getjToolBar1();
        this.jStatusBar1 = windowManager.getjStatusBar1();
        this.appDisplayName = appDisplayName;
        this.mainPanel = windowManager.getMainPanel();
        this.currentAppClass = currentAppClass;
    
        //Layout
        setRootWindow();
       
      
    }

     
    
    @Override
    public String addTopModuleWindow(BeanModule module, boolean mustShow, boolean adjust) {
        
        
        String id = null;
        try {
  
            boolean alreadyInstanced = false;
            ArrayList<String> ninstances = TopModuleRegistry.getCurrentInstancesOf(module.getClassName());
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
                            TopModuleWindow tmw = ((TopModuleView) viewtmp).getTopModuleWindow();
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
            
            if(!alreadyInstanced || (alreadyInstanced && module.isMultipleInstance()) )
            {
                JarClassLoader moduleClassLoader = JarClassLoader.getInstance().getSubInstance(module);
                Class<?> forName = moduleClassLoader.loadClass(module.getClassName());
                DebugLogger.getInstance().addText("\tCreating a new instance of module :: "+module.getClassName());
                TopModuleWindow win = (TopModuleWindow) forName.newInstance();
                
                //Pass bean & ini parameters & initialize with moduleClassLoader
                win.setBeanModule(module);
                win.setModuleClassLoader(moduleClassLoader);
                DebugLogger.getInstance().addText("\tModule "+module.getClassName()+" contains "+module.getInstalledPlugins().size()+" plugins");
                win.iniParameters.setMap( module.getIniParameters() );
                win.initialize(stamper, stray, coreCfg, (UIFramework) this);
                DebugLogger.getInstance().addText("\tModule "+module.getClassName()+" initialized");
                
                win.setMultipleInstance(module.isMultipleInstance());
                
                if(mustShow || module.getAutoStart()==BeanModule.YES || (module.getAutoStart()==BeanModule.ONDEMAND && win.isOpeningRequired()))
                {                     
                    
                    String displayname = module.getNameForLocale(coreCfg.core_lang);
                    
                    if (module.getDisplayPoint().getLocation().equals("statusbar")) {
                        ((StatusBarZone) jStatusBar1.getZone("second")).addComponent(win);
                    } else if (module.getDisplayPoint().getLocation().equals("topwindow")) {
                        id = TopModuleRegistry.register(forName.getName(), win);
                        win.setName(id);
                        TopModuleView createNewView = new TopModuleView(displayname, module.getModuleIcon16x16(), module.isClosable(), module.getDisplayPoint().getParentId(), win);
                        viewMap.addView(id, createNewView);
                        createNewView.makeVisible();
                        //DockingUtil.addWindow(createNewView, rootWindow);
                        createLayoutViewMap(createNewView);
                        createNewView.requestFocus();
                       
                        //createNewView.getViewProperties().getViewTitleBarProperties().getFocusedProperties().getCloseButtonProperties().setVisible(module.isClosable());
                        //createNewView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(module.isClosable());
                        if(adjust)
                        {
                            windowManager.adjustFrameworkLayout(0); 
                        }
                    }

                    win.refreshUI();
                     
                } else {
                    win.dispose();
                }
            }
           
        } catch (Exception ex) {
            Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), "Problem loading module: "+module.getClassName()+"\n"+ex);
        }
        
        return id;
   
    }
   
    @Override
    public TopModuleWindow getSelectedTopModuleWindow() {  
        return this.lastFocusedTopModuleWindow;
    }

    @Override
    public void closeAll() {
        
        if(updatersMap!=null)
        {
            for(ModuleUpdaterDlg mu: updatersMap.values())
            {
                mu.dispose();
            }
            updatersMap.clear();
        }
        
        for(int i=0; i<viewMap.getViewCount(); i++)
        {
            View view = viewMap.getViewAtIndex(i);
            if(view!=null)
            {
                TopModuleWindow win = ((TopModuleView) view).getTopModuleWindow();
                if(win!=null)
                {
                    view.close();
                    viewMap.removeView(win.getName());
                    rootWindow.remove(view.getParent());
                    TopModuleRegistry.remove(win.getName());
                    //win.dispose();      
                    win = null;
                }    
            }
            
        }
     
//        for(int i=0; i<rootWindow.getChildWindowCount(); i++)
//        {
//            DockingWindow childWindow = rootWindow.getChildWindow(i);
//            childWindow.close();
//        }
        
        //Reset rootWindow
        viewMap = new StringViewMap();
        rootWindow.setWindow(new TabWindow());
        
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
          
        StatusBarZone zone = ((StatusBarZone) jStatusBar1.getZone("third"));
        zone.clear();
        
       //Add information about module versioning (if available)
       if(remoteUpdater!=null  && win!= null)
       {
           BeanModule beanModule = win.getBeanModule();
           if(beanModule!=null)
           {
                String className = StringUtils.noNull(beanModule.getClassName());
                String lastVersionForModule = remoteUpdater.getModulesRepo().getLastVersionForModule(className);
                
                if(lastVersionForModule!=null && StringUtils.compare(lastVersionForModule, beanModule.getBeanMetaINF().getVersion())>0)
                {
                    final JLinkButton lbutton = new JLinkButton("New "+win.moduleDisplayName+" "+ lastVersionForModule);
                    lbutton.setActionCommand(className);
                    lbutton.setName("moduleUpdater:"+className);
                    lbutton.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/bubble2.png")));
                    zone.addComponent(lbutton);
                    lbutton.addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String className = e.getActionCommand();
                            if(updatersMap.containsKey(className))
                            {
                                updatersMap.get(className).setVisible(true);
                            }
                            else
                            {
                                //Try to get current installed version of this module
                                String installedVersion = "";
                                ArrayList<String> instancesIDs = TopModuleRegistry.getCurrentInstancesOf(className);
                                if(!instancesIDs.isEmpty())
                                {
                                    TopModuleWindow win = TopModuleRegistry.findId(instancesIDs.get(0));
                                    if(win.getBeanModule()!=null)
                                    {
                                        installedVersion = win.getBeanModule().getBeanMetaINF().getVersion();
                                    }
                                }
                                
                                ModuleUpdaterDlg dlg = new ModuleUpdaterDlg(javar.JRDialog.getActiveFrame(), false,
                                         installedVersion, remoteUpdater.getModulesRepo().getRepoForModule(className), currentAppClass, UIFrameworkIN.this);
                                Point loc = lbutton.getLocationOnScreen();
                                dlg.setLocation(loc.x, loc.y-dlg.getHeight()-5);
                                dlg.setVisible(true);
                                updatersMap.put(className, dlg);
                            }
                        }
 
                    });
                }
           }
       }
       
        
       
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
        TopModuleView createNewView = new TopModuleView(win.getModuleDisplayName(), null, closable, locationId, win);
         
        viewMap.addView(id, createNewView);
//        TabWindow target = tabWindowCenter;
//        if(locationId.equals("right")) {
//            //target = tabWindowRight;
//        }
//        target.addTab(createNewView);
       
        createNewView.makeVisible();
        //target.makeVisible();
        //DockingUtil.addWindow(createNewView, rootWindow);
        this.createLayoutViewMap(createNewView);
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
        
       // DeveloperUtil.createWindowLayoutFrame("My Main RootWindow", rootWindow).setVisible(true);
       
//        //Create a suitable layout center-left
//        tabWindowCenter = new TabWindow();
//        tabWindowCenter.getWindowProperties().setCloseEnabled(false);
//        
//        rootWindow.setWindow( tabWindowCenter );
        //DockingUtil.addWindow(tabWindowCenter, rootWindow);
        properties.addSuperObject(currentTheme.getRootWindowProperties());
        properties.getDockingWindowProperties().setDockEnabled(false);
        properties.getDockingWindowProperties().setUndockEnabled(false);
        rootWindow.getRootWindowProperties().setRecursiveTabsEnabled(false);
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
                TopModuleWindow win = ((TopModuleView) view1).getTopModuleWindow();
                
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
                        TopModuleWindow win = ((TopModuleView) view).getTopModuleWindow();
                        if(win!=null)
                        {
                            String id = win.getName();
                            System.out.println("About to close win  with id " + id + " win .." + win);
                            windowManager.adjustFrameworkLayout(-1);
                            TopModuleRegistry.remove(id);
                            win = null;
                            //Remove this view from map
                            viewMap.removeView(id);
                        }
                      
                        
                    }
                    
                }
                //Close the window itself
                if (window instanceof View) {
                        View view = (View) window;
                        TopModuleWindow win = ((TopModuleView) view).getTopModuleWindow();
                        if(win!=null)
                        {
                            String id = win.getName();
                            windowManager.adjustFrameworkLayout(-1);
                            TopModuleRegistry.remove(id);
                            viewMap.removeView(id);
                            win = null;
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


//    private TopModuleWindow extractTopModuleWindowFromView(View view) {
//        TopModuleWindow win = null;
//        Component component = view.getComponent();
//        if (component instanceof JScrollPane) {
//            Component comp = ((JScrollPane) component).getViewport().getView();
//            if (comp instanceof TopModuleWindow) {
//                win = (TopModuleWindow) comp;
//            }
//        }
//        return win;
//    }

    private void createLayoutViewMap(TopModuleView view) {
        
        if(view==null || viewMap.getViewCount()<=1)
        {
            TabWindow tabbedWin = new TabWindow();
            rootWindow.setWindow(tabbedWin);
            if(view!=null)
            {
                tabbedWin.addTab(view);
            }
            return;
        }
        
        
        String displayPoint = view.getDisplayPoint();
        if(displayPoint.equalsIgnoreCase("bar"))
        {
            rootWindow.getWindowBar(Direction.LEFT).addTab(view);
            view.requestFocusInWindow();
            return;
        }
        
        //try to see if current layout already contains a view in the same displayPoint
        //it returns its container TabWindow
        //if TabWindow!=null No change in layout is required
        TabWindow pointer = findTabWindow(displayPoint);
        if(pointer!=null)
        {
            pointer.makeVisible();
            pointer.addTab(view);
            return;
        }
        //Layout must be updated in order to support new displayPoint
        DockingWindow window_old = rootWindow.getWindow();
        if(window_old==null) //something strange happened... fix it
        {
            TabWindow tabbedWin = new TabWindow();
            rootWindow.setWindow(tabbedWin);
            window_old = tabbedWin;
        }
        DockingWindow window_new = null;
        if(displayPoint.equalsIgnoreCase("left") || displayPoint.equalsIgnoreCase("center"))
        {
            float weight = 0.66f;
            //in some ocasions 0.66f is to much and must be set to 0.33f
            
            if(isSplit(window_old, true))
            {
                weight = 0.23f;
            }
            window_new = new SplitWindow(true, weight, new TabWindow(view), window_old);
        }
        else if(displayPoint.equalsIgnoreCase("right") )
        {
            float weight = 0.66f;
            if(isSplit(window_old, true))
            {
                weight = 0.23f;
            }
            window_new = new SplitWindow(true, 0.66f, window_old, new TabWindow(view));
        }
        else if(displayPoint.equalsIgnoreCase("top"))
        {
            float weight = 0.66f;
            if(isSplit(window_old,false))
            {
                weight = 0.23f;
            }
            window_new = new SplitWindow(false, weight, new TabWindow(view), window_old);
        }
        else if(displayPoint.equalsIgnoreCase("bottom"))
        {
            float weight = 0.66f;
            if(isSplit(window_old,false))
            {
                weight = 0.23f;
            }
            window_new = new SplitWindow(false, weight, window_old, new TabWindow(view));
        }
         
        rootWindow.setWindow(window_new);
       
    }

    /**
     * Returns the first found tabWindow containing a view with a given
     * displayPoint. Returns null if no view with such a displayPoint exists
     * @param displayPoint
     * @return 
     */
    private TabWindow findTabWindow(String displayPoint) {
        TabWindow tw = null;
        for(int i=0; i<viewMap.getViewCount(); i++)
        {
            TopModuleView tmv = (TopModuleView) viewMap.getViewAtIndex(i);
            String displayPoint1 = tmv.getDisplayPoint();
            if(displayPoint1!=null && displayPoint1.equals(displayPoint))
            {
                Container parent = tmv.getWindowParent();
                
                if(parent !=null && parent instanceof TabWindow)
                {
                    return (TabWindow) parent;
                }
            }
            
        }
        return tw;
    }

    /**
     * Lookup in the layout tree splitwindows which are vertical
     * @param dw
     * @return 
     */
    private boolean isSplit(DockingWindow dw, boolean horizontal) {
        boolean is = false;
        if(dw == null)
        {
            return false;
        }
        
        if (dw instanceof SplitWindow) {
            if (((SplitWindow) dw).isHorizontal() == horizontal) {
                return true;
            }
        }
        
        for(int i=0; i<dw.getChildWindowCount(); i++)
        {
                DockingWindow childWindow = dw.getChildWindow(i);
                
                if(childWindow instanceof SplitWindow)
                {
                    if(((SplitWindow) childWindow).isHorizontal() == horizontal )
                    {
                        return true;
                    }
                }
                is = isSplit(childWindow, horizontal);
            }
        return is;
    }
 

    @Override
    public void removeModuleUpdater(String moduleClassName) {
        //Make it disappear from statusbar
         StatusBarZone zone = ((StatusBarZone) jStatusBar1.getZone("third"));
         zone.removeComponentsBy("moduleUpdater:"+moduleClassName, JLinkButton.class);
         this.updatersMap.remove(moduleClassName);
        
         JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), 
                 moduleClassName+"\nhas been updated.\nChanges will take place after restart.");
         
         //Prevent update message from appearing again for this module
          ArrayList<String> instanceIDs = TopModuleRegistry.getCurrentInstancesOf(moduleClassName);
          for(String id: instanceIDs)
          {
             TopModuleWindow win = TopModuleRegistry.findId(id);
             win.getBeanModule().getBeanMetaINF().setVersion(remoteUpdater.getModulesRepo().getLastVersionForModule(moduleClassName));
          }
       
    }

    @Override
    public void setRemoteUpdater(RemoteUpdater remoteUpdater) {
        this.remoteUpdater = remoteUpdater;
    }

    
   }
