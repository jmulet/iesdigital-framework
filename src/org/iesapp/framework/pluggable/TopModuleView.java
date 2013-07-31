/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.View;
import net.infonode.docking.WindowPopupMenuFactory;
import org.iesapp.framework.util.IconUtils;

/**
 *
 * @author Josep
 */
public class TopModuleView extends View {

    protected final TopModuleWindow topModuleWindow;
    protected final String displayPoint;

    public TopModuleView(String displayName, Icon icon, boolean closable, String displayPoint, final TopModuleWindow component) {
        super(displayName, icon, new JScrollPane(component));
        ((DockingWindow) this).getWindowProperties().setCloseEnabled(closable);
        ((DockingWindow) this).setPopupMenuFactory(new WindowPopupMenuFactory() {
            @Override
            public JPopupMenu createPopupMenu(final DockingWindow window) {
                JPopupMenu menu = new JPopupMenu();
                // Check that the window is a View
                if (window instanceof View) {
                    JMenuItem item = new JMenuItem("Refresh", IconUtils.getFrameworkIcon("refresh.png"));
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            component.refreshUI();
                        }
                    });
                    menu.add(item);
                }
                return menu;
            }
        });
        
        this.topModuleWindow = component;
        this.displayPoint = displayPoint;
        
        //Add a refresh Button to the view
        //this.getViewProperties().getViewTitleBarProperties().setVisible(true);
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
