/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import com.l2fprod.common.swing.StatusBar;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.iesapp.framework.pluggable.modulesAPI.Property;
import org.iesapp.util.StringUtils;
import org.openide.util.Lookup.Result;
import org.openide.util.Lookup.Template;


/**
 *
 * @author Josep
 */
public class WindowManager {
    
    protected boolean fullScreen;
    protected int displayMode = MODE_NORMAL;
    private int lastDisplayMode = 1;
    protected final JFrame frame;
    protected final JPanel mainPanel;
    protected final JToggleButton jToggleButton1;
    
    public static final int MODE_FULLSCREEN = 9;
    public static final int MODE_TOOLBAR = 0;
    public static final int MODE_SIMPLE = 1;
    public static final int MODE_NORMAL = 2;
    public static final int MODE_MAXIMIZED = 3;
    private final GraphicsDevice device;
    private final DisplayMode defaultDisplayMode;
    protected final JButton jButtonHT;
    protected final JToolBar jToolBar1;
    protected final StatusBar jStatusBar1;
    private final Dimension screenSize;
    private HashMap<String, Property> propertiesMap;
    private final VToolbar vtoolbar;
    private final JButton switchButton;
    
    //Create instance to hold changes in display
    public WindowManager(final JFrame frame, final JPanel mainPanel, final JToolBar jToolBar1,
            final StatusBar jStatusBar1, final JButton jButtonHT, final JToggleButton jToggleButton1,
            final JButton switchButton,
            final VToolbar vtoolbar)
    {
        this.frame = frame;
        this.mainPanel = mainPanel;
        this.jToggleButton1 = jToggleButton1;
        this.switchButton = switchButton;
        this.jButtonHT = jButtonHT;
        this.jToolBar1 = jToolBar1;
        this.jStatusBar1 = jStatusBar1;
        this.vtoolbar = vtoolbar;
        
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = env.getDefaultScreenDevice();
        defaultDisplayMode = device.getDisplayMode();
     
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        //ADD LISTENER TO TOGGLEBUTTON
        jToggleButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 //System.out.println("actionPerformed"+e+" kThd"+jToggleButton1.isSelected());
                 if(jToggleButton1.isSelected()){
                     setDisplayMode(MODE_TOOLBAR);
                 }
                 else
                 {
                     setDisplayMode(lastDisplayMode);
                 }
                 
                     
        }});      
    }
            
    
    public void setProperties(HashMap<String, Property> applicationInitParameters) {
        this.propertiesMap = applicationInitParameters;
    }
    
    
    public int getDisplayMode() {
        return displayMode;
    }
    
    public void setLastDisplayMode()
    {
        System.out.println("Cridada a lastdisplaymode" + lastDisplayMode) ;
        setDisplayMode(lastDisplayMode);
    }

    public void setDisplayMode(int displayMode) {
        setDisplayMode(displayMode, 0);
    }
    
    //int mode = 0 ; -1 (about to delete module); +1 (about to create module)
    public void setDisplayMode(int displayMode, int mode) {
        
        //System.out.println("ENTERING IN MODE "+displayMode+" being "+lastDisplayMode);
        if(this.displayMode==displayMode)
        {
          //  System.out.println("returning already in "+displayMode);
            return; //no change indeed
        }
        
        this.lastDisplayMode = this.displayMode;
        this.displayMode = displayMode;              
        
        if(lastDisplayMode==MODE_FULLSCREEN)
        {
            //ALL OTHER MODES ARE INCOMPATIBLE WITH FULL SCREEN, WE MUST RETURN
            //TO NORMAL DISPLAY
            
            //quit fullscreen mode

                device.setDisplayMode(defaultDisplayMode);
                //hide the frame so we can change it.
                frame.setVisible(false);
                //remove the frame from being displayable.
                frame.dispose();
                //put the borders back on the frame.
                frame.setUndecorated(false);
                //needed to unset this window as the fullscreen window.
		device.setFullScreenWindow(null);
		//make sure the size of the window is correct.
		frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true); 
                frame.repaint();
        }
        
        switch(displayMode)
        {
            case MODE_FULLSCREEN:
                jToggleButton1.setSelected(false);
                //change to fullscreen.
                frame.setVisible(false);
                //remove the frame from being displayable.
                frame.dispose();
                //remove borders around the frame
                frame.setUndecorated(true);
                //make the window fullscreen.
                device.setFullScreenWindow(getFrame());
                //attempt to change the screen resolution.
                device.setDisplayMode(defaultDisplayMode);
                //show the frame
                frame.setVisible(true);
                frame.repaint();
                break;
               
            case MODE_TOOLBAR:
                
                frame.setVisible(false);
                vtoolbar.setVisible(true);
//                jToggleButton1.setSelected(true);
//                jButtonHT.setVisible(false);
//                mainPanel.setVisible(false);
//                jStatusBar1.setVisible(false);
//                frame.setAlwaysOnTop(true);
//                frame.setResizable(true);
//                frame.setExtendedState(JFrame.NORMAL);
//                frame.getJMenuBar().setVisible(false);
//                frame.validate();
//                frame.pack();
                //Position
//                Dimension size = frame.getSize();
//                frame.setLocation(screenSize.width-size.width, 0);
//                this.positionFrame();
                break;
                
            case MODE_NORMAL:
                vtoolbar.setVisible(false);
                frame.setVisible(true);
                switchButton.setVisible(false);
                jToggleButton1.setSelected(false);
                frame.setAlwaysOnTop(false);
                mainPanel.setVisible(true);
                frame.setResizable(true);
                frame.getJMenuBar().setVisible(true);
                if(frame.getExtendedState()!=JFrame.NORMAL){
                 frame.pack();
                 this.positionFrame();
                }
                break;
                
            case MODE_SIMPLE:
                vtoolbar.setVisible(false);
                frame.setVisible(true);
                jToggleButton1.setSelected(false);
                switchButton.setVisible(true);
                frame.setAlwaysOnTop(false);
                mainPanel.setVisible(true);
                frame.setResizable(true);
                frame.getJMenuBar().setVisible(false);
                jStatusBar1.setVisible(false);
                
                //if(frame.getExtendedState()!=JFrame.NORMAL){
                frame.setExtendedState(JFrame.NORMAL);
                //}
                //frame.pack();
                //Position
                frame.setSize(500, screenSize.height-40);
                frame.setLocation(screenSize.width-frame.getWidth(), 0);
                break;
                
            case MODE_MAXIMIZED:
                vtoolbar.setVisible(false);
                frame.setVisible(true);
                switchButton.setVisible(false);
                jToggleButton1.setSelected(false);
                jButtonHT.setVisible(true);
                mainPanel.setVisible(true);
                jStatusBar1.setVisible(true);
                frame.setAlwaysOnTop(false);
                frame.setResizable(true);
                frame.getJMenuBar().setVisible(true);
                frame.pack();
                //if(frame.getExtendedState()!=JFrame.MAXIMIZED_BOTH){
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                  
                //}
                
                break;
        }
         
           
    }
    
    

     //mode =+1 creating, mode=-1 closing
     public void adjustFrameworkLayout(int mode) {
        
        int anuncis = TopModuleRegistry.getCurrentInstancesOf("org.iesapp.modules.anuncis.AnuncisModule").size();
        int n = TopModuleRegistry.getCount();
        System.out.println("RegisteredTabbedPanes size is " + n+ " for mode "+mode);
        if(n+mode<=0)
        {
            return;
        }
        
        if (anuncis > 0) {

            if (n + mode == 1) {
                setDisplayMode(MODE_SIMPLE);
            } else if (n + mode > 1) {
                setDisplayMode(MODE_MAXIMIZED);
            }
        } else {
            setDisplayMode(MODE_MAXIMIZED);
        }
    }

    
    //Coordinates are given in relative 0.0 to 1.0 both in x and y
    //this can be specified in config xml through application init parameters
    //rposx, rposy, rwidth, rheight
    
    private void positionFrame() {
        
        if(propertiesMap==null)
        {
            return;
        }
        
        Property rposx = propertiesMap.get("rposx");
        Property rposy = propertiesMap.get("rposy");
        Property rwidth = propertiesMap.get("rwidth");
        Property rheight = propertiesMap.get("rheight");
        
        int posx = 0;
        int posy = 0;
        int width = screenSize.width;
        int heigth = screenSize.height;
       
        
        if(rposx!=null)
        {
            double parseDouble = Double.parseDouble(rposx.getValue());
            posx = (int) (parseDouble*screenSize.width);
        }
        if(rposy!=null)
        {
            double parseDouble = Double.parseDouble(rposy.getValue());
            posy = (int) (parseDouble*screenSize.height);
        }
        if(rwidth!=null)
        {
            double parseDouble = Double.parseDouble(rwidth.getValue());
            if(parseDouble==-1)
            {
                width = screenSize.width;
            }
            else
            {
                width = (int) (parseDouble*screenSize.width);
            }
        }
        if(rheight!=null)
        {
            double parseDouble = Double.parseDouble(rheight.getValue());
            if(parseDouble==-1)
            {
                heigth = screenSize.height;
            }
            else
            {  
                heigth = (int) (parseDouble*screenSize.height);
            }
        }
        
        
        
        if(jToggleButton1.isSelected())
        {
            heigth = frame.getHeight();
        }
        
        frame.setSize(width, heigth);
        frame.setLocation(posx, posy);
    }
   
    
    /**
     * Look for TopModuleWindows through lookup
     * in such approach every module can inspect all modules
     * and retrieve their respective lookups
     * @param nameId
     * @return 
     */
    public static TopModuleWindow findTopModuleWindow(String nameId)
    {
        if(TopModuleWindow.globalLookup==null)
        {
            return null;
        }
                
        Result result = TopModuleWindow.globalLookup.lookup(new Template(TopModuleWindow.class));
        Collection<TopModuleWindow> allInstances = result.allInstances();
        
        for(TopModuleWindow win: allInstances)
        {
            if(win.getModuleName().equals(nameId))
            {
                return win;
            }
        }
        return null;
    }
    
    
    public static int findMenuItemPos(JMenuBar container, String id)
    {
        for(int i=0; i<container.getComponentCount(); i++)
        {
           String name = container.getComponent(i).getName();
           //System.out.println("\t"+name+" @ "+i);
           if( name!=null && name.equals(id) )
           {
               return i;
           }
           
        }
        return -1;
        
    }
    
    public static int findToolBarItemPos(JToolBar container, String id)
    {
        for(int i=0; i<container.getComponentCount(); i++)
        {
           //System.out.println(container.getComponent(i).getName()+" --> "+id);
            String name = container.getComponent(i).getName();
           if( name!=null && name.equals(id) )
           {
               return i;
           }
           
        }
        return -1; 
        
    }

    /**
     * Finds the position of a container with name id
     * which belongs to the container parent
     * returns -1 if not found
     * @param container
     * @param id
     * @return 
     */
    public static int findContainerPos(Container container, String id) {
       
        if (container instanceof JMenuBar) {
            JMenuBar jmenu = (JMenuBar) container;
            //System.out.println("JMENUBAR " + container.getName() + " has #child " + jmenu.getMenuCount());

            for (int i = 0; i < jmenu.getMenuCount(); i++) {
                Component menuComponent = jmenu.getMenu(i);
                String name = menuComponent.getName();
                //System.out.println("\t\t found " + menuComponent + " .... " + name + " @ " + i);
                if (name != null && name.equals(id)) {
                    return i;
                }

            }

        } else if (container instanceof JMenu) {

            JMenu jmenu = (JMenu) container;
            //System.out.println("JMENU " + container.getName() + " has #child " + jmenu.getMenuComponentCount());

            for (int i = 0; i < jmenu.getMenuComponentCount(); i++) {
                Component menuComponent = jmenu.getMenuComponent(i);
                String name = menuComponent.getName();
                //System.out.println("\t\t found " + menuComponent + " .... " + name + " @ " + i);
                if (name != null && name.equals(id)) {
                    return i;
                }

            }

        } else {
            //System.out.println("conainter " + container.getName() + " has #child " + container.getComponentCount());

            for (int i = 0; i < container.getComponentCount(); i++) {
                String name = container.getComponent(i).getName();
                //System.out.println("\t\t found " + container.getComponent(i) + " .... " + name + " @ " + i);
                if (name != null && name.equals(id)) {
                    return i;
                }

            }
        }
        return -1;
    }

    
    /**
     * Gets the container with name=id which belong to the parent container
     * Returns null if not found
     * @param container
     * @param id
     * @return 
     */
    public static Container getContainerObject(Container container, String id) {
        if(id.startsWith("\\"))
        {
            id = id.substring(1);
        }
        
        if(id.contains("\\"))
        {
            return getContainerRecursively(container, id);
        }
        else
        {
            return getContainerObjectSingle(container, id);
        }
        
    }
    
    
    public static Container getContainerObjectSingle(Container container, String id) {
        Container cont = null;
        
        int pos = findContainerPos(container, id);
        //System.out.println("single detection of "+id+" @ "+pos+ " from "+container);
        if(pos>=0)
        {
            if(container instanceof JMenuBar)
            {
                cont = (Container) ((JMenuBar) container).getMenu(pos);
            }
            else if(container instanceof JMenu)
            {
                cont = (Container) ((JMenu) container).getMenuComponent(pos);
            }
            else
            {
                cont = (Container) container.getComponent(pos);                
            }
        }
        return cont;
    }
   /**
     * Finds the position of a container with name id
     * which belongs to the container parent
     * returns -1 if not found
     * @param container
     * @param path is given like "jEdit\jMenu1" starting from container searching for jEdit
     * @return 
     */
    private static Container getContainerRecursively(Container container, String path) {
        ArrayList<String> parsed = StringUtils.parseStringToArray(path, "\\", StringUtils.CASE_INSENSITIVE);
        //System.out.println(parsed);
        Container lastParent = container;
        Container tmp = null;
        for(int i=0; i<parsed.size(); i++)
        {
            String newpath = parsed.get(i);
            HashMap<String,String> modifiersMap = null;
            if(newpath.contains("("))
            {
                String modi = StringUtils.AfterLast(newpath, "(").replace(")", "");
                modifiersMap = StringUtils.StringToHash(modi, ",");
                newpath = StringUtils.BeforeFirst(newpath, "(");
                //System.out.println("ATENCIO:::"+modi+"-->"+modifiersMap);
            }
            
            if(lastParent!=null)
            {
                tmp = lastParent;
                lastParent = getContainerObjectSingle(lastParent, newpath);
                if(lastParent==null && modifiersMap!=null && !modifiersMap.isEmpty())
                {
                    String text = modifiersMap.get("text");
                    if(text!=null && !text.isEmpty() && (
                        tmp instanceof JMenu || tmp instanceof JMenuBar))
                    {
                        JMenu jmenu = new JMenu();
                        jmenu.setText(modifiersMap.get("text"));
                        jmenu.setName(newpath);  
                        String sicon = modifiersMap.get("icon");
                        if(sicon!=null)
                        {
                            URL resource = WindowManager.class.getResource(sicon);
                            ImageIcon icon = null;
                            if(resource!=null)
                            {
                                icon = new ImageIcon(resource);
                            }
                            jmenu.setIcon(icon);
                        }
                        tmp.add(jmenu);
                        lastParent = jmenu;
                    }
                }
            }
            else
            {
                break;
            }
        }
        
            
        return lastParent;
    }
    
    
      
    public Component findMenuItem(Container container, String id)
    {         
        for(Component comp: container.getComponents())
        {
           if( comp.getName().equals(id) )
           {
               return comp;
           }
        }
        return null;
        
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JToggleButton getjToggleButton1() {
        return jToggleButton1;
    }

    public JButton getjButtonHT() {
        return jButtonHT;
    }

    public JToolBar getjToolBar1() {
        return jToolBar1;
    }

    public StatusBar getjStatusBar1() {
        return jStatusBar1;
    }


}
