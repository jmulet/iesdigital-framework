/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.pluginsAPI;

import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author Josep
 */
public class BeanAnchorPoint {
    //protected String location="menubar";
    //protected String parentId="jMenuBarModules";
    protected String location="toolbar";
    protected String parentId="jToolBarModules";
    
    protected int pos=-1;
    protected boolean showIcon = true;

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setAttributes(NamedNodeMap attributes) {
        if(attributes==null) {
            return;
        }
        for(int i=0; i<attributes.getLength(); i++)
        {
            String nodeName = attributes.item(i).getNodeName();
            if(nodeName.equals("location"))
            {
                this.location = attributes.item(i).getNodeValue();
            }
            else if(nodeName.equals("parentId"))
            {
                this.parentId = attributes.item(i).getNodeValue();
            }
             else if(nodeName.equals("pos"))
            {
                this.pos = Integer.parseInt(attributes.item(i).getNodeValue());
            }
             else if(nodeName.equals("showIcon"))
            {
                this.showIcon = attributes.item(i).getNodeValue().equalsIgnoreCase("yes");
            }
        }
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public boolean isShowIcon() {
        return showIcon;
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }
}
