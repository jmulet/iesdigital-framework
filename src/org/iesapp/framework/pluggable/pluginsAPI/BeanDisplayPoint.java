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
public class BeanDisplayPoint {
    protected String location="topwindow";
    protected String parentId="";
    
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
            
        }
    }
 
}
