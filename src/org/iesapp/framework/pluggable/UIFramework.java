/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import java.util.ArrayList;
import javax.swing.JMenu;
import org.iesapp.framework.pluggable.modulesAPI.BeanModule;

/**
 *
 * @author Josep
 */
public interface UIFramework {
    public void initialize(WindowManager windowManager, String appDisplayName, final ArrayList<JMenu> beforeMenu, final ArrayList<JMenu> afterMenu);    
    public String addTopModuleWindow(BeanModule module, boolean mustShow, boolean adjust);    
    public String addTopModuleWindow(TopModuleWindow win, boolean closable, boolean adjust);  
    public String addTopModuleWindow(TopModuleWindow win, String locationId, boolean closable, boolean adjust);  
    public TopModuleWindow getSelectedTopModuleWindow();    
    public void closeAll();
    public void setMenus(TopModuleWindow win);
    public void setSelectedTopWindow(String identifier);
    public void setDistribution(int i);
    public String printLayout();
}
