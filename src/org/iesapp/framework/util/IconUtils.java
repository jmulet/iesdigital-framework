/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Josep
 */
public class IconUtils {
    
    //Define static default icons (lazy creation)
    private static Icon deleteIcon;
    private static Icon exitIcon;
    private static Icon saveIcon;
    private static Icon attentionIcon;
    private static Icon backIcon;
    private static Icon forwardIcon;
    private static Icon downIcon;
    private static Icon upIcon;
    private static Icon configIcon;
    private static Icon sendIcon;
    private static Icon runIcon;
    private static Icon printIcon;
    private static Icon moduleIcon;
    private static Icon pluginIcon;
    private static Icon insertIcon;
    private static ImageIcon blankIcon;
    
    public static Icon getDeleteIcon()
    {
        if(deleteIcon==null)
        {
            deleteIcon = getIconResource(null, "org/iesapp/framework/delete.gif");
        }
        return deleteIcon;
    }
    
    public static Icon getExitIcon()
    {
        if(exitIcon==null)
        {
            exitIcon = getIconResource(null, "org/iesapp/framework/exit.gif");
        }
        return exitIcon;
    }
    
    public static Icon getSaveIcon()
    {
        if(saveIcon==null)
        {
            saveIcon = getIconResource(null, "org/iesapp/framework/save.gif");
        }
        return saveIcon;
    }
    
    public static Icon getAttentionIcon()
    {
        if(attentionIcon==null)
        {
            attentionIcon = getIconResource(null, "org/iesapp/framework/attention.png");
        }
        return attentionIcon;
    }
    
    public static Icon getBackIcon()
    {
        if(backIcon==null)
        {
            backIcon = getIconResource(null, "org/iesapp/framework/back.gif");
        }
        return backIcon;
    }
    
    public static Icon getForwardIcon()
    {
        if(forwardIcon==null)
        {
            forwardIcon = getIconResource(null, "org/iesapp/framework/forward.gif");
        }
        return forwardIcon;
    }
    
    public static Icon getUpIcon()
    {
        if(upIcon==null)
        {
            upIcon = getIconResource(null, "org/iesapp/framework/up.gif");
        }
        return upIcon;
    }
    
    public static Icon getDownIcon()
    {
        if(downIcon==null)
        {
            downIcon = getIconResource(null, "org/iesapp/framework/down.gif");
        }
        return downIcon;
    }
    
    public static Icon getConfigIcon()
    {
        if(configIcon==null)
        {
            configIcon = getIconResource(null, "org/iesapp/framework/configIcon.gif");
        }
        return configIcon;
    }
    
    public static Icon getSendIcon()
    {
        if(sendIcon==null)
        {
            sendIcon = getIconResource(null, "org/iesapp/framework/envia.gif");
        }
        return sendIcon;
    }
    
    public static Icon getRunIcon()
    {
        if(runIcon==null)
        {
            runIcon = getIconResource(null, "org/iesapp/framework/run.gif");
        }
        return runIcon;
    }
    
    public static Icon getPrintIcon()
    {
        if(printIcon==null)
        {
            printIcon = getIconResource(null, "org/iesapp/framework/print.gif");
        }
        return printIcon;
    }
    
    public static Icon getModuleIcon()
    {
        if(moduleIcon==null)
        {
            moduleIcon = getIconResource(null, "org/iesapp/framework/module_icon.gif");
        }
        return moduleIcon;
    }
    
    public static Icon getPluginIcon()
    {
        if(pluginIcon==null)
        {
            pluginIcon = getIconResource(null, "org/iesapp/framework/plugin.gif");
        }
        return pluginIcon;
    }
    
    public static Icon getInsertIcon()
    {
        if(insertIcon==null)
        {
            insertIcon = getIconResource(null, "org/iesapp/framework/insert.gif");
        }
        return insertIcon;
    }
    
    public static Icon getBlankIcon()
    {
        if(blankIcon==null)
        {
            blankIcon = new ImageIcon();
        }
        return blankIcon;
    }
    public static Icon getFrameworkIcon(String iconName)
    {
        return getIconResource(null, "org/iesapp/framework/"+iconName);         
    }
    
    /**
     * This method avoids nullpointer exception when creating icons which resources
     * are not found...
     * @param classLoader
     * @param resource
     * @return 
     */
    public static Icon getIconResource(final ClassLoader classLoader, final String resource)
    {
        ImageIcon icon ;
        URL url;
        String resource2 = resource;
        
        if(resource.startsWith("/"))
        {
            resource2 = resource.substring(1);
        }
        if(classLoader!=null)
        {
            url = classLoader.getResource(resource2);
        }
        else
        {
            url = JarClassLoader.getInstance().getResource(resource2);
        }
        
        if(url!=null)
        {
            icon = new ImageIcon(url);
        }
        else
        {
            icon = new ImageIcon();
        }
        return icon;
    }
     
    
    
}
