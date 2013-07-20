/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.modulesAPI;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.iesapp.framework.pluggable.DockingFrameworkApp;
import org.iesapp.framework.pluggable.TopModuleWindow;
import org.iesapp.framework.pluggable.deamons.BeanDeamon;
import org.iesapp.framework.pluggable.pluginsAPI.BeanAnchorPoint;
import org.iesapp.framework.pluggable.pluginsAPI.BeanDisplayPoint;
import org.iesapp.framework.util.CoreCfg;
import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author Josep
 */
public class BeanModule {

    public static final byte NO = 0;
    public static final byte YES = 1;
    public static final byte ONDEMAND = 2;
    public static final byte MODULETYPE_STD = 3;
    public static final byte MODULETYPE_DESKTOP_BROWSE = 4;
    public static final byte MODULETYPE_DESKTOP_OPEN = 5;
    protected boolean plugin = false;
    protected boolean enabled = true;
    protected String roles = "*";
    protected String users = "";
    protected String jar;
    protected String forModule = "";
    protected String className;
    protected byte autoStart = 0;
    protected boolean multipleInstance = false;
    protected boolean closable = false;
    protected ArrayList<BeanAnchorPoint> listAnchorPoints;
    protected BeanDisplayPoint displayPoint;
    protected final HashMap<Byte, ImageIcon> moduleIcons24x24;
    protected final HashMap<Byte, ImageIcon> moduleIcons16x16;
    private HashMap<String, String> moduleNameBundle;  //Module display name with internationalization support
    protected ArrayList<String> requiredLibs;
    protected ArrayList<BeanModule> installedPlugins;
    protected ArrayList<BeanDeamon> deamons;
    protected final BeanMetaInf metaINF;
    protected HashMap<String, Object> iniParameters;
    protected HashMap<String, String> iniParametersDescription;
    protected String shortcut = "";
    protected String helpSetJar = null;
    protected String helpSet = null;
    protected ImageIcon MODULE_ICON = new ImageIcon(BeanModule.class.getResource("/org/iesapp/framework/icons/module_icon.gif"));
    protected ImageIcon PLUGIN_ICON = new ImageIcon(BeanModule.class.getResource("/org/iesapp/framework/icons/plugin.gif"));

    public BeanModule() {

        //When trying to access to these maps, we are going to 
        //perform a lazy loading of the icons
        moduleIcons24x24 = new HashMap<Byte, ImageIcon>();
        moduleIcons16x16 = new HashMap<Byte, ImageIcon>();

        listAnchorPoints = new ArrayList<BeanAnchorPoint>();
        moduleNameBundle = new HashMap<String, String>();
        requiredLibs = new ArrayList<String>();
        installedPlugins = new ArrayList<BeanModule>();
        metaINF = new BeanMetaInf();
        iniParameters = new HashMap<String, Object>();
        iniParametersDescription = new HashMap<String, String>();
        displayPoint = new BeanDisplayPoint();
        //Default display
        displayPoint.setLocation("topwindow");
        displayPoint.setParentId("");
        //
        deamons = new ArrayList<BeanDeamon>();
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isMultipleInstance() {
        return multipleInstance;
    }

    public void setMultipleInstance(boolean multipleInstance) {
        this.multipleInstance = multipleInstance;
    }

    public ArrayList<BeanAnchorPoint> getListAnchorPoints() {
        return listAnchorPoints;
    }

    public void setListAnchorPoints(ArrayList<BeanAnchorPoint> listAnchorPoints) {
        this.listAnchorPoints = listAnchorPoints;
    }

    public void setAttributes(NamedNodeMap attributes) {

        if (attributes == null) {
            return;
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            String nodeName = attributes.item(i).getNodeName();
            // ////System.out.println("Loaded Atributte "+nodeName);
            //////System.out.println("Nodename & value:"+nodeName+" "+attributes.item(i).getNodeValue());
            if (nodeName.equalsIgnoreCase("class")) {
                this.className = attributes.item(i).getNodeValue();
            } else if (nodeName.equalsIgnoreCase("jar")) {
                this.jar = attributes.item(i).getNodeValue();
            } else if (nodeName.equalsIgnoreCase("autoStart")) {
                byte b = NO;
                if (attributes.item(i).getNodeValue().equalsIgnoreCase("yes")) {
                    b = YES;
                } else if (attributes.item(i).getNodeValue().equalsIgnoreCase("ondemand")) {
                    b = ONDEMAND;
                }
                this.setAutoStart(b);
            } else if (nodeName.equalsIgnoreCase("multipleInstances")) {
                this.multipleInstance = attributes.item(i).getNodeValue().equalsIgnoreCase("yes");
            } else if (nodeName.equalsIgnoreCase("closable")) {
                this.closable = attributes.item(i).getNodeValue().equalsIgnoreCase("yes");
            } else if (nodeName.equalsIgnoreCase("enabled")) {
                this.enabled = attributes.item(i).getNodeValue().equalsIgnoreCase("yes");
            } else if (nodeName.equalsIgnoreCase("roles")) {
                this.roles = attributes.item(i).getNodeValue();
            } else if (nodeName.equalsIgnoreCase("users")) {
                this.users = attributes.item(i).getNodeValue();
            } else if (nodeName.equalsIgnoreCase("shortcut")) {
                this.shortcut = attributes.item(i).getNodeValue();
            } else if (nodeName.equalsIgnoreCase("forModule")) {
                this.forModule = attributes.item(i).getNodeValue();
            } else if (nodeName.equalsIgnoreCase("helpSetJar")) {
                this.setHelpSetJar(attributes.item(i).getNodeValue());
            } else if (nodeName.equalsIgnoreCase("helpSet")) {
                this.setHelpSet(attributes.item(i).getNodeValue());
            }
        }
    }

    public boolean isClosable() {
        return this.closable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    public byte getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(byte autoStart) {
        this.autoStart = autoStart;
    }

    private static ImageIcon scaleIconToHeight(ImageIcon moduleIcon, int px) {

        if (px > 16 && moduleIcon.getIconHeight() > 22 && moduleIcon.getIconHeight() < 27) {
            return moduleIcon; //no scale
        }
        Image img = moduleIcon.getImage();
        Image newimg = img.getScaledInstance(-1, px, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    public HashMap<String, String> getModuleNameBundle() {
        return moduleNameBundle;
    }

    public ArrayList<String> getRequiredLibs() {
        return requiredLibs;
    }

    public void setRequiredLibs(ArrayList<String> requiredLibs) {
        this.requiredLibs = requiredLibs;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public BeanMetaInf getBeanMetaINF() {
        return metaINF;
    }

    public ArrayList<BeanModule> getInstalledPlugins() {
        return installedPlugins;
    }

    public void setInstalledPlugins(ArrayList<BeanModule> installedPlugins) {
        this.installedPlugins = installedPlugins;
    }

    public BeanDisplayPoint getDisplayPoint() {
        return displayPoint;
    }

    public void setDisplayPoint(BeanDisplayPoint displayPoint) {
        this.displayPoint = displayPoint;
    }

    public HashMap<String, Object> getIniParameters() {
        return iniParameters;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public ImageIcon getModuleIcon24x24() {
        return getModuleIcon24x24(TopModuleWindow.STATUS_NORMAL);
    }

    public ImageIcon getModuleIcon24x24(byte status) {
        ImageIcon pointer;
        if (moduleIcons24x24.containsKey(status)) {
            pointer = moduleIcons24x24.get(status);
        } else {
            ImageIcon loadIt;
            if (this.getModuleType() == BeanModule.MODULETYPE_STD) {
                loadIt = loadIt(status);

            } else {
                loadIt = this.loadResourceModuleIcon();
            }
            pointer = scaleIconToHeight(loadIt, 24);
            moduleIcons24x24.put(status, pointer);
            moduleIcons16x16.put(status, scaleIconToHeight(loadIt, 12));
        }
        return pointer;
    }

    public ImageIcon getModuleIcon16x16() {
        return getModuleIcon16x16(TopModuleWindow.STATUS_NORMAL);
    }

    public ImageIcon getModuleIcon16x16(byte status) {
        ImageIcon pointer;
        if (moduleIcons16x16.containsKey(status)) {
            pointer = moduleIcons16x16.get(status);
        } else {
            ImageIcon loadIt;
            if (this.getModuleType() == MODULETYPE_STD) {
                loadIt = loadIt(status);
            } else {
                loadIt = this.loadResourceModuleIcon();
            }
            pointer = scaleIconToHeight(loadIt, 12);
            moduleIcons16x16.put(status, pointer);
            moduleIcons24x24.put(status, scaleIconToHeight(loadIt, 24));

        }
        return pointer;
    }

    /**
     * Loads image icon for a given status from the module.jar
     *
     * @param status
     * @return
     */
    private ImageIcon loadIt(byte status) {
        ////System.out.println(" IN LOADIT STAUS"+status);
        ImageIcon moduleIcon;
        String tag;
        if (plugin) {
            moduleIcon = PLUGIN_ICON;
            tag = "plugin";
        } else {
            moduleIcon = MODULE_ICON;
            tag = "module";
        }


        File jarfile = new File(CoreCfg.contextRoot + File.separator + "modules" + File.separator + getJar());

        try {

            ZipFile jarf = new ZipFile(jarfile);
            ZipEntry entry = null;
            if (status == TopModuleWindow.STATUS_NORMAL) {
                entry = jarf.getEntry("META-INF/" + tag + ".gif");
                ////System.out.println("META-INF/"+tag+".gif"  );
            } else if (status == TopModuleWindow.STATUS_SLEEPING) {
                entry = jarf.getEntry("META-INF/" + tag + "_0.gif");
                ////System.out.println("META-INF/"+tag+"_0.gif"  );
            } else if (status == TopModuleWindow.STATUS_AWAKE) {
                entry = jarf.getEntry("META-INF/" + tag + "_2.gif");
                ////System.out.println("META-INF/"+tag+"_2.gif"  );
            } else {
                entry = jarf.getEntry("META-INF/" + tag + "_" + status + ".gif");
                ////System.out.println("META-INF/"+tag+"_"+status+".gif"  );
            }

            //By default uses the standard icon if not found
            if (entry == null) {
                entry = jarf.getEntry("META-INF/" + tag + ".gif");
                ////System.out.println("not found getting standard META-INF/"+tag+".gif"  );
            }

            if (entry != null) {
                InputStream inputStream = jarf.getInputStream(entry);
                //Problem- animated gif !!!
                Image image = Toolkit.getDefaultToolkit().createImage(org.apache.commons.io.IOUtils.toByteArray(inputStream));
                moduleIcon = new ImageIcon(image);
                //moduleIcon = new ImageIcon(ImageIO.read(inputStream));
                //inputStream.close();                   
            }
            jarf.close();
        } catch (IOException ex) {
            Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return moduleIcon;
    }

    public String getForModule() {
        return forModule;
    }

    public void setForModule(String forModule) {
        this.forModule = forModule;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<tag ").append("jar=").append(jar).append(" class=").append(className).append(" roles").append(roles);
        return builder.toString();
    }

    public ImageIcon loadExtraModuleIcon(String ext) {
        File jarfile = new File(CoreCfg.contextRoot + File.separator + "modules" + File.separator + this.getJar());
        ImageIcon icon = null;
        try {

            ZipFile zjar = new ZipFile(jarfile);
            ZipEntry entry = zjar.getEntry("META-INF/module" + ext + ".gif");
            if (entry != null) {
                InputStream inputStream = zjar.getInputStream(entry);
                icon = new ImageIcon(ImageIO.read(inputStream));
                inputStream.close();
            }
            zjar.close();
        } catch (IOException ex) {
            Logger.getLogger(DockingFrameworkApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return icon;
    }

    public byte getModuleType() {
        byte type = MODULETYPE_STD;
        if (className == null) {
            return type;
        }

        if (className.startsWith("desktopBrowse")) {
            type = MODULETYPE_DESKTOP_BROWSE;
        } else if (className.startsWith("desktopOpen")) {
            type = MODULETYPE_DESKTOP_OPEN;
        }

        return type;
    }

    /**
     * Load a icon from the resource file not from the jar module
     */
    private ImageIcon loadResourceModuleIcon() {

        ImageIcon icon = MODULE_ICON;
        if (this.getIniParameters().containsKey("icon")) {
            File f = new File(CoreCfg.contextRoot + File.separator + this.getIniParameters().get("icon"));
            if (f.exists()) {
                icon = new ImageIcon(CoreCfg.contextRoot + File.separator + this.getIniParameters().get("icon"));
            }
        }

        return icon;
    }

    public boolean isPlugin() {
        return plugin;
    }

    public void setPlugin(boolean plugin) {
        this.plugin = plugin;
    }

    public ArrayList<BeanDeamon> getDeamons() {
        return deamons;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public HashMap<String, String> getIniParametersDescription() {
        return iniParametersDescription;
    }

    public String getNameForLocale(String locale) {
        String name = "";
        if (moduleNameBundle.containsKey(locale)) {
            name = this.moduleNameBundle.get(locale);
        } else if (moduleNameBundle.containsKey("default")) {
            name = this.moduleNameBundle.get("default");
        }
        return name;
    }

    public String getHelpSetJar() {
        return helpSetJar;
    }

    public void setHelpSetJar(String helpSetJar) {
        this.helpSetJar = helpSetJar;
    }

    public String getHelpSet() {
        return helpSet;
    }

    public void setHelpSet(String helpSet) {
        this.helpSet = helpSet;
    }
}
