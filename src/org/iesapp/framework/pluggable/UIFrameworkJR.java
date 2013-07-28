/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import com.l2fprod.common.swing.JLinkButton;
import com.l2fprod.common.swing.StatusBar;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javar.JRTabbedPane;
import javar.JRTabbedPaneStation;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.iesapp.framework.pluggable.modulesAPI.BeanModule;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.updater.RemoteRepository;
import org.iesapp.updater.RemoteUpdater;
import org.iesapp.util.StringUtils;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Leaf;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.MultiSplitLayout.Split;
import org.jdesktop.swingx.MultiSplitPane;


/**
 *
 * @author Josep
 */
public class UIFrameworkJR implements UIFramework{
    private JFrame frame;
    private JToolBar jToolBar1;
    private StatusBar jStatusBar1;
    private String appDisplayName;
    private JRTabbedPane currentTabbedPane;
    private ArrayList<JRTabbedPane> jrTabbedPanes;
    public final static int MAXTabbedPanes = 5;
    private JPanel mainPanel;
    private JRTabbedPaneStation station;
    
    private MultiSplitPane multiSplitPane;
    private final CoreCfg coreCfg;
    private Stamp stamper;
    private ArrayList<JMenu> beforeMenu;
    private ArrayList<JMenu> afterMenu;
    private final SysTray stray;
    private final JToggleButton jToggleButton;
    private WindowManager windowManager;
    private Leaf left;
    private Leaf center;
    private Leaf right;
    private Leaf top;
    private Leaf bottom;
    private Split modelRoot;
    private Split centralSplits;
    private RemoteRepository repository;

    
 
    public UIFrameworkJR(CoreCfg coreCfg, Stamp stamper, SysTray stray, JToggleButton jToggleButton1)
    {
        this.coreCfg = coreCfg;
        this.stamper = stamper;
        this.stray = stray;
        this.jToggleButton = jToggleButton1;
        
    }
    
    @Override
    public void initialize(final WindowManager windowManager, final String appDisplayName,
                        final ArrayList<JMenu> beforeMenu, final ArrayList<JMenu> afterMenu, final String currentAppName ) {
        this.windowManager = windowManager;
        this.frame = windowManager.getFrame();
        this.beforeMenu = beforeMenu;
        this.afterMenu = afterMenu;
        this.jToolBar1 = windowManager.getjToolBar1();
        this.jStatusBar1 = windowManager.getjStatusBar1();
        this.appDisplayName = appDisplayName;
        this.mainPanel = windowManager.getMainPanel();
       
        station = new JRTabbedPaneStation();
        station.addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals("selectedJRTabbedPane"))
                {
                    currentTabbedPane = (JRTabbedPane) evt.getNewValue();
                    //Update menus
                    TopModuleWindow win = currentTabbedPane.getSelectedTopModuleWindow();
                    if(win!=null)
                    {
                        setMenus(win);
                        frame.setTitle("iesDigital " + CoreCfg.VERSION + "   ·   " + appDisplayName+" / " + win.getModuleDisplayName()+" "+win.getModuleVersion()+"   ·   "+coreCfg.anyAcademic+"-"+(coreCfg.anyAcademic+1));
                    
                    }
                    else
                    {
                       frame.setTitle("iesDigital " + CoreCfg.VERSION + "   ·   " + appDisplayName + "   ·   "+coreCfg.anyAcademic+"-"+(coreCfg.anyAcademic+1));
                     
                    }
                 
                     
                }
            }
        });
        jrTabbedPanes = new ArrayList<JRTabbedPane>();
        
         
        for (int i = 0; i < MAXTabbedPanes; i++) {
            JRTabbedPane jrTabbedPane1 = new JRTabbedPane() {
                @Override
                public void aboutToClose(ActionEvent e, Component c) {
                    String id = c.getName();
                    //TopModuleWindow win = TopModuleRegistry.findId(id);
                    //System.out.println("About to close win  with id "+id+" win ..");
                    windowManager.adjustFrameworkLayout(-1);
                    TopModuleRegistry.remove(id);
               }
            };
            station.registerJRTabbedPane(jrTabbedPane1);
            jrTabbedPane1.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    TopModuleWindow win = currentTabbedPane.getSelectedTopModuleWindow();

                    if (win != null) {
                        frame.setTitle("iesDigital " + CoreCfg.VERSION + "   ·   " + appDisplayName + " / " + win.getModuleDisplayName() + " " + win.getModuleVersion() + "   ·   " + coreCfg.anyAcademic + "-" + (coreCfg.anyAcademic + 1));

                    } else {
                        frame.setTitle("iesDigital " + CoreCfg.VERSION + "   ·   " + appDisplayName+"   ·   "+coreCfg.anyAcademic+"-"+(coreCfg.anyAcademic+1));
                    }

                      
                    setMenus(win);
                    adjustStationWeights();
        
                }
            });
            jrTabbedPanes.add(jrTabbedPane1);
        }
        currentTabbedPane = jrTabbedPanes.get(0);
        currentTabbedPane.setFocus(true);
     
        //Layout
        setDistribution(1);
       
      
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
                        for(JRTabbedPane jrTabbedPane1: jrTabbedPanes)
                        {
                            for(int i=0; i<jrTabbedPane1.getTabCount(); i++)
                            {
                                 
                                if(jrTabbedPane1.getTopModuleWindowAt(i) == findwin)
                                {
                                    station.setSelectedJRTabbedPane(jrTabbedPane1);
                                    jrTabbedPane1.setSelectedTopModuleWindow(findwin);
                                    windowManager.adjustFrameworkLayout(0);  
                                    break;
                                }
                                
                            }
                        }
                        //currentTabbedPane.setSelectedComponent(findwin);
                    }
            }
            
            DebugLogger.getInstance().addText("module ismultipleinstance --> "+module.isMultipleInstance());
            if(!alreadyInstanced || (alreadyInstanced && module.isMultipleInstance()) )
            {
                DebugLogger.getInstance().addText("CREATING::::");
                //ModuleClassLoader moduleClassLoader = new ModuleClassLoader(ClassLoader.getSystemClassLoader());
                org.iesapp.framework.util.JarClassLoader.getInstance().addToClasspath(new File(CoreCfg.contextRoot+"\\modules\\"+module.getJar() ));
                //Include any other required lib
                //Adds to classpath any other required lib for this module
                for (String s : module.getRequiredLibs()) {
                    org.iesapp.framework.util.JarClassLoader.getInstance().addToClasspath(new File(CoreCfg.contextRoot + "\\" + s));
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
                        
                        JRTabbedPane target = currentTabbedPane;

                        if (module.getDisplayPoint().getParentId().equalsIgnoreCase("left")) {
                            target = jrTabbedPanes.get(2);
                        } 
                        else if (module.getDisplayPoint().getParentId().equalsIgnoreCase("center")) {
                            target = jrTabbedPanes.get(0);
                        }
                        else if (module.getDisplayPoint().getParentId().equalsIgnoreCase("right")) {
                            target = jrTabbedPanes.get(1);
                        }
                        target.addTab(win, displayname, module.getModuleIcon16x16(), module.isClosable());
                        //Li passam el tabComponent
//                        Component tabComponentAt = target.getTabComponentAt(target.getTabCount()-1);                        
//                        win.tabComponent = (JRCustomTab) tabComponentAt;
                        windowManager.adjustFrameworkLayout(0); 
                       
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
        this.adjustStationWeights();
        return id;
   
    }
   
    @Override
    public TopModuleWindow getSelectedTopModuleWindow() {        
         Component comp = currentTabbedPane.getSelectedComponent();
         TopModuleWindow win = null;
         if(comp!=null)
         {
            JScrollPane jscp = (JScrollPane) comp;
            win = (TopModuleWindow) jscp.getViewport().getView();
         }
         return win;
    }

    @Override
    public void closeAll() {
        for(JRTabbedPane jrTabbedPane1: jrTabbedPanes)
        {
            for(int i=0; i<jrTabbedPane1.getTabCount(); i++)
            {
                 Component comp = jrTabbedPane1.getComponentAt(i);
                 JScrollPane jscp = (JScrollPane) comp;
                 TopModuleWindow win = (TopModuleWindow) jscp.getViewport().getView();
                 win.dispose();
            }
            jrTabbedPane1.removeAll();
        }
        System.gc();
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
       if(repository!=null  && win!= null)
       {
           BeanModule beanModule = win.getBeanModule();
           if(beanModule!=null)
           {
                String className = StringUtils.noNull(beanModule.getClassName());
                String lastVersionForModule = repository.getLastVersionForModule(className);
                if(StringUtils.compare(lastVersionForModule, beanModule.getBeanMetaINF().getVersion())>0)
                {
                    JLinkButton lbutton = new JLinkButton("New "+win.moduleDisplayName+" "+ lastVersionForModule);
                    lbutton.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/bubble1.png")));
                    zone.addComponent(lbutton);
                    lbutton.addMouseListener(new MouseAdapter(){
                        
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            System.out.println("DO something");
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
        windowManager.adjustFrameworkLayout(1);
        String id = TopModuleRegistry.register(win.getClass().getName(), win);
        win.setName(id);
        JRTabbedPane target = currentTabbedPane;
        if(locationId.equalsIgnoreCase("left"))
        {
            target = jrTabbedPanes.get(2);
        }
        else if(locationId.equalsIgnoreCase("center"))
        {
            target = jrTabbedPanes.get(0);
        }
        else if(locationId.equalsIgnoreCase("right"))
        {
            target = jrTabbedPanes.get(1);
        }
        else if(locationId.equalsIgnoreCase("top"))
        {
            target = jrTabbedPanes.get(3);
        }
         else if(locationId.equalsIgnoreCase("bottom"))
        {
            target = jrTabbedPanes.get(4);
        }
        
        target.addTab(win, win.getModuleDisplayName(), null, closable);
        //Li passam el tabComponent
//        Component tabComponentAt = target.getTabComponentAt(target.getTabCount() - 1);
//        win.tabComponent = (JRCustomTab) tabComponentAt;
        
        //Determine in which leaf is currentTabbedPane and make sure it is visible
        multiSplitPane.revalidate();
        this.adjustStationWeights();
        return id;
    }

    

    @Override
    public void setSelectedTopWindow(String identifier) {
        for(JRTabbedPane jrTabbedPane1: jrTabbedPanes)
        {
            for(Component comp: jrTabbedPane1.getComponents())
            {
                if(comp.getName()!=null && comp.getName().equals(identifier))
                {
                    jrTabbedPane1.setSelectedComponent(comp);
                    break;
                }
            }
        }
    }

    @Override
    public void setDistribution(int i) {
        mainPanel.removeAll();      
//        
////        Divider divider1 = new Divider();
////        Divider divider2 = new Divider();
////        Divider divider3 = new Divider();
////        Divider divider4 = new Divider();
////        
//////        List childrenH = Arrays.asList(left, divider1, center, divider2, right);
//////        
//////        List childrenV = Arrays.asList(top, divider3, childrenH, divider4, bottom);
////          modelRoot = new MultiSplitLayout.Split();
////        modelRoot.setChildren(childrenV);
//        String layoutDef = "(COLUMN top (ROW left weight=1 center right) bottom)";
//        modelRoot = MultiSplitLayout.parseModel(layoutDef);
        
        
        
        createLayout();
        
        multiSplitPane = new MultiSplitPane();
        multiSplitPane.getMultiSplitLayout().setModel(modelRoot);
        multiSplitPane.setDividerSize(1);
    
        centralSplits.setWeight(1.0);
        center.setWeight(1.0);
        right.setWeight(0.0);
        left.setWeight(0.0);
        top.setWeight(0.0);
        bottom.setWeight(0.0);
        
        //Register the position in every tabbedPane -
        jrTabbedPanes.get(0).setName("center");
        jrTabbedPanes.get(1).setName("right");
        jrTabbedPanes.get(2).setName("left");
        jrTabbedPanes.get(3).setName("top");
        jrTabbedPanes.get(4).setName("bottom");
                
        multiSplitPane.add(jrTabbedPanes.get(0), "center");
        multiSplitPane.add(jrTabbedPanes.get(1), "right");
        multiSplitPane.add(jrTabbedPanes.get(2), "left");
        multiSplitPane.add(jrTabbedPanes.get(3), "top");
        multiSplitPane.add(jrTabbedPanes.get(4), "bottom");
        mainPanel.add(multiSplitPane);  
        
        currentTabbedPane = jrTabbedPanes.get(0);
        //mainPanel.revalidate();
        multiSplitPane.revalidate();  
        multiSplitPane.repaint();  
    }

    private void adjustStationWeights()
    {
        createLayout();
        multiSplitPane.setModel(modelRoot);
      
        int nc = jrTabbedPanes.get(0).getTabCount();
        int nr = jrTabbedPanes.get(1).getTabCount();
        int nl = jrTabbedPanes.get(2).getTabCount();
        int ncentral  = nc + nr + nl;
        int nt = jrTabbedPanes.get(3).getTabCount();
        int nb = jrTabbedPanes.get(4).getTabCount();
        
        System.out.println("Adjusting station weights"+nc+" "+nr+" "+nl+" "+nt+" "+nb);
        System.out.println("Before weights "+left.getWeight()+" "+center.getWeight()+" "+right.getWeight());
        
        //Go for center stations
        if(nc+nl+nr>0)
        {
            int bc = nc>0?1:0;
            int br = nr>0?1:0;
            int bl = nl>0?1:0;
            double total = bc + br + bl;
            center.setWeight(bc/total);
            left.setWeight(bl/total);
            right.setWeight(br/total);
            
        }
        else
        {
            center.setWeight(1.0);
            left.setWeight(0.0);
            right.setWeight(0.0);
        }
        
         
        int bt = nt>0?1:0;
        int bcentral = ncentral>0?1:0;
        int bb = nb>0?1:0;
        double total = bt+bcentral+bb;
        
        if(total>0)
        {
            top.setWeight(bt/total);
            centralSplits.setWeight(bcentral/total);
            bottom.setWeight(bb/total);
            
        }
        else
        {
            top.setWeight(0.0);
            centralSplits.setWeight(1.0);
            bottom.setWeight(0.0);
        }
        
        multiSplitPane.revalidate();
        multiSplitPane.repaint();
        multiSplitPane.updateUI();
         
        
    }

    private void createLayout() {
        modelRoot = new Split();
        modelRoot.setRowLayout(false);
        
        top = new Leaf("top");
        centralSplits = new Split();
        centralSplits.setRowLayout(true);
        
        bottom = new Leaf("bottom");
        List children = Arrays.asList(new Node[]{top, new Divider(), centralSplits, new Divider(), bottom});
        modelRoot.setChildren(children);
        
        left = new Leaf("left");
        center = new Leaf("center");
        right = new Leaf("right");
       
     
        List children2 = Arrays.asList(new Node[]{left, new Divider(), center, new Divider(), right});
        centralSplits.setChildren(children2);
    }
 

    @Override
    public void removeModuleUpdater(String moduleClassName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRemoteUpdater(RemoteUpdater remoteUpdater) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
}
