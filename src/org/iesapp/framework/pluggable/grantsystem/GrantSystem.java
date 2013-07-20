/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.grantsystem;

import java.util.HashMap;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class GrantSystem{
    
    private static HashMap<String,GrantModule> systemGrant = new HashMap<String,GrantModule>();
    
    private GrantSystem()
    {
        //disable direct instance of this multiplon
    }
    
    public static GrantModule getInstance(String moduleId, CoreCfg coreCfg)
    {
        if(systemGrant.containsKey(moduleId))
        {
            return systemGrant.get(moduleId);
        }
        else{
            GrantModule newinstance = new GrantModule(moduleId, coreCfg);           
            systemGrant.put(moduleId, newinstance);
            return newinstance;
        }
    }
    
 
}
