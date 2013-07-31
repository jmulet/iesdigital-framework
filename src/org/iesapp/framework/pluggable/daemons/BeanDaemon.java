/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.daemons;

import org.iesapp.util.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Josep
 */
public class BeanDaemon {
    //Deamons must be in the same jar than modules
    //no need for specifier jarFile name
    
    //must specify, however, class name
    protected String deamonClassName = "";
    protected int timeInMillis = 0; //0-inactive
    protected boolean activateIcon = true;
    protected boolean showMessage = true;
    protected boolean enabled = true;
    protected boolean activateModule = false;
    protected String roles = "*";

    public String getDeamonClassName() {
        return deamonClassName;
    }

    public void setDeamonClassName(String deamonClassName) {
        this.deamonClassName = deamonClassName;
    }

    public int getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(int timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public boolean isActivateIcon() {
        return activateIcon;
    }

    public void setActivateIcon(boolean activateIcon) {
        this.activateIcon = activateIcon;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    public void setAttributes(NamedNodeMap attributes) {
        if (attributes == null) {
            return;
        }
        for(int i=0; i<attributes.getLength(); i++)
        {
            Node item = attributes.item(i);
            if(item.getNodeName().equalsIgnoreCase("class"))
            {
                this.deamonClassName = StringUtils.noNull(item.getNodeValue());
            }
            else if(item.getNodeName().equalsIgnoreCase("timeInMillis"))
            {
                String str = item.getNodeValue();
                if(str==null)
                {
                    str = "0";
                }
                this.timeInMillis = Integer.parseInt(str);
            }
            else if(item.getNodeName().equalsIgnoreCase("activateIcon"))
            {
                this.activateIcon = StringUtils.noNull(item.getNodeValue()).equalsIgnoreCase("yes");
            }
            else if(item.getNodeName().equalsIgnoreCase("showMessage"))
            {
                this.showMessage = StringUtils.noNull(item.getNodeValue()).equalsIgnoreCase("yes");
            }
            else if(item.getNodeName().equalsIgnoreCase("enabled"))
            {
                this.enabled = StringUtils.noNull(item.getNodeValue()).equalsIgnoreCase("yes");
            }
            else if(item.getNodeName().equalsIgnoreCase("activateModule"))
            {
                this.activateModule = StringUtils.noNull(item.getNodeValue()).equalsIgnoreCase("yes");
            }
            else if(item.getNodeName().equalsIgnoreCase("roles"))
            {
                this.roles = StringUtils.noNull(item.getNodeValue());
            }
            
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isActivateModule() {
        return activateModule;
    }

    public void setActivateModule(boolean activateModule) {
        this.activateModule = activateModule;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
