/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import java.util.ArrayList;
import java.util.HashMap;
import org.iesapp.framework.util.CoreCfg;


/**
 *
 * @author Josep
 */
public class Stamp {
    protected String appNameId;
    protected String abrev;
    protected int pid=0;
    protected HashMap<String,ArrayList<String>> mapModuleActions;
    private  CoreCfg coreCfg;
    protected boolean initialized;
     
    /**
     * 
     * @param appNameId
     * @param abrev 
     */
    public Stamp()
    {
      //Default constructor
    }
    
    public void initialize(String appNameId, String abrev, CoreCfg coreCfg)
    {
        this.coreCfg = coreCfg;
        this.appNameId = appNameId;
        this.abrev = abrev;
        mapModuleActions = new HashMap<String,ArrayList<String>>();
        initialized = true;
    }
    
    public void inStamp()
    { 
        if(!initialized)
        {
            return;
        }
              
        if(getPid()>0) {
            return;
        } //assegura que només es crida un pic a la subrutina

      
        String SQL1 = "INSERT INTO `"+CoreCfg.core_mysqlDB+"`.sig_log (usua, ip, netbios, domain, tasca, inici) VALUES(?,"
                + "'"+CoreCfg.ip+"','"+CoreCfg.netbios+"','"+CoreCfg.core_PRODUCTID+"',?,NOW())";
        Object[] obj = new Object[]{abrev, appNameId};
        this.pid=coreCfg.getMysql().preparedUpdateID(SQL1, obj);
    }


   public int outStamp()
   {
        if(getPid()==0) {
           return -1;
       } 
        StringBuilder builder = new StringBuilder();
        for(String key: mapModuleActions.keySet())
        {
            builder.append(key).append(" : ").append(mapModuleActions.get(key).toString()).append("\n");
            mapModuleActions.get(key).clear();
        }
                
        String SQL1 = "UPDATE `"+CoreCfg.core_mysqlDB+"`.sig_log SET fi=NOW(), resultat=? where id="+getPid();
        int nup = coreCfg.getMysql().preparedUpdate(SQL1, new Object[]{builder.toString()});
        //System.out.println("outstamp "+nup+" ");
        setPid(-1);
        mapModuleActions.clear();
        initialized = false;
        return nup;
        //id=0;//assegura que només es crida un pic a la subrutina
    }

   /**
    * Add an action to the given module name
    * @param module
    * @param string 
    */
    public void addAction(String module, String string) {
       if(!mapModuleActions.containsKey(module))
       {
           addModule(module);
       }
         
        if(!mapModuleActions.get(module).contains(string)) {
            mapModuleActions.get(module).add(string);
        }
    }

    /**
     * Add action to the current module pointer
     * @param string 
     */
    public void addAction(String string) {
        addAction("global",string);
    }
    
    public int getPid() {        
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getAppNameId() {
        return appNameId;
    }

    public String getAbrev() {
        return abrev;
    }

    /**
     * registers a new module section for the stamper
     * @param moduleName 
     */
    public void addModule(String moduleName) {
        if(!mapModuleActions.containsKey(moduleName))
        {
            mapModuleActions.put(moduleName, new ArrayList<String>());
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
            
}
