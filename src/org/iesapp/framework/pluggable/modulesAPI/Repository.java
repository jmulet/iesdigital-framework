/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.modulesAPI;

/**
 *
 * @author Josep
 */
public class Repository {
    
    protected String extensionFileName;
    protected String ModuleClassName;
    protected String version;
    protected String jar;

    public String getExtensionFileName() {
        return extensionFileName;
    }

    public void setExtensionFileName(String extensionFileName) {
        this.extensionFileName = extensionFileName;
    }

    public String getModuleClassName() {
        return ModuleClassName;
    }

    public void setModuleClassName(String ModuleClassName) {
        this.ModuleClassName = ModuleClassName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }
 
  
}
