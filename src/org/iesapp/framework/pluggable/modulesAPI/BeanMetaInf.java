/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.modulesAPI;

import java.util.HashMap;

/**
 *
 * @author Josep
 */
public class BeanMetaInf {
    protected String author="";
    protected String version="";
    protected String dependencies="";
    protected HashMap<String, String> descriptionMap;

    public BeanMetaInf()
    {
        descriptionMap = new HashMap<String, String>();
    }
    
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    /*
     * Gets description from bundle
     */
    public String getDescription(String lang) {
        
        String desc = null;
        if(descriptionMap.containsKey(lang))
        {
           desc = descriptionMap.get(lang);
        }
        else
        {
            desc = descriptionMap.get("default");
        }
        if(desc==null)
        {
            desc = "";
        }
            
        return desc;
    }

    public HashMap<String, String> getDescriptionMap() {
        return descriptionMap;
    }
}
