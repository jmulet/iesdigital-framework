/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.View;

/**
 *
 * @author Josep
 */
public class TopModuleView extends View {

    protected final TopModuleWindow topModuleWindow;
    protected final String displayPoint;

    public TopModuleView(String displayName, Icon icon, boolean closable, String displayPoint, TopModuleWindow component) {
        super(displayName, icon, new JScrollPane(component));
        ((DockingWindow) this).getWindowProperties().setCloseEnabled(closable);
        this.topModuleWindow = component;
        this.displayPoint = displayPoint;
    }

    public TopModuleWindow getTopModuleWindow() {
        return topModuleWindow;
    }
    
    public String getViewId()
    {
        return topModuleWindow.getName();
    }

    public String getDisplayPoint() {
        return displayPoint;
    }
}
