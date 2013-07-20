/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.grantsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class GrantModule {
    private Map<String,GrantBean> moduleGrant;
    private final String moduleId;
    private String role;
    private GrantModule defaultsGrant;
    private final CoreCfg coreCfg;
    
    public GrantModule(String moduleId, CoreCfg coreCfg) {
        this.coreCfg = coreCfg;
        this.moduleId = moduleId;       
        moduleGrant = Collections.synchronizedMap(new LinkedHashMap<String,GrantBean>());
    }
    
    public void put(String key, int value)
    {
        GrantBean gb= new GrantBean(key,value,"",GrantBean.BASIC_CONFIG, coreCfg);
        moduleGrant.put(key, gb);
    }
    
     public GrantBean get(String key)
     {
       GrantBean gb = moduleGrant.get(key);
       if(gb ==null)
       {
           //System.out.println("WARNING: Grant with key "+key+" not found. Returning empty bean.");
           gb = new GrantBean(key,GrantBean.NONE,"", GrantBean.BASIC_CONFIG, coreCfg);
       }
       return gb;
     }
     
     /**
      * Checks if a given grant labeled by key is granted or not
      * according to a student which belongs / nese / repetidor
      * @param key
      * @param belongs
      * @param nese
      * @param repetidor
      * @return 
      */
     public boolean isGranted(String key, Profile profile)
     {
         boolean isgranted = true;
         GrantBean gb = get(key);
         if(gb.isNone())    //profile indepenedent no
         {
             return false;
         }
         else if(gb.isAll()) //profile independent yes
         {
             return true;
         }
         else   //check upon profile modifiers
         {
             if(gb.isBelongs()){
                 isgranted &= profile.belongs;
             }
             if(gb.isNese()){
                 if(gb.getModifierNESE().equalsIgnoreCase("AND")){
                     isgranted &= profile.nese;
                 }
                 else
                 {
                     isgranted |= profile.nese;
                 }
                 
             }
              if(gb.isRepetidor()){
                 if(gb.getModifierREPETIDOR().equalsIgnoreCase("AND")){
                    isgranted &= profile.repetidor;
      
                 }
                 else
                 {
                    isgranted |= profile.repetidor;
                 }
             }
         
         }
        
         return isgranted;
     }
     
     public boolean isGranted(GrantBean gb, Profile profile)
     {
         return isGranted(gb.getKey(), profile);         
     }
     
     public GrantBean get(String key, GrantBean defaults)
     {
       GrantBean gb = moduleGrant.get(key);
       if(gb ==null)
       {
           gb = defaults;
           //Must be created for all roles
       }
       return gb;
     }
    
     public void loadGrantForRole(String role, GrantModule defaultsGrant)
     {
        this.role = role;
        this.defaultsGrant = defaultsGrant;
        
        //TODO: For undefined roles return NONE 
        String SQL0 = "SELECT * FROM sig_grant_roles WHERE role='"+role+"'";
        boolean existsRole = false;
        try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL0,st);
            if(rs1!=null && rs1.next())
            {
                existsRole = true;
            }
            if(rs1!=null)
            {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(GrantModule.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        if(!existsRole)
        {
            for(String key: defaultsGrant.getMap().keySet())
            {
                GrantBean gb = new GrantBean(coreCfg);
                gb.key = key;
                gb.value = GrantBean.NONE;
                gb.acceptedOptions = GrantBean.BASIC_CONFIG;
                gb.id = -1;
                moduleGrant.put(key, gb);
            }
            return;
        }
        moduleGrant.clear();
        String SQL1 = "SELECT sg.key, sg.description, sgv.value, sg.acceptedValues, sgv.id FROM  sig_grant AS sg "+
                     " INNER JOIN sig_grant_values sgv ON (sg.id = sgv.idGrant) "+
                     " INNER JOIN sig_grant_roles AS sgr "+
                     " ON (sgr.id = sgv.idRole) WHERE sg.moduleId='"+moduleId+"' AND sgr.role='"+role+"'";
       
        try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                String key = rs1.getString(1);
                GrantBean bean = new GrantBean(key,rs1.getInt(3),rs1.getString(2), rs1.getInt(4), coreCfg);
                bean.setId(rs1.getInt(5));
                moduleGrant.put(key, bean);
            }
            if(rs1!=null)
            {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(GrantModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(defaultsGrant==null)
        {
            return;
        }
        
        //Now check if currentGrant contains all defaultGrant's fields
        //if not store them into database
        //System.out.println("Comparem els mapes");
        //System.out.println("DefaultsMap:"+defaultsGrant.getMap());
        //System.out.println("LoadedMap:"+moduleGrant);
        for(String key: defaultsGrant.getMap().keySet())
        {
            if(!moduleGrant.containsKey(key))
            {
                GrantBean gb = defaultsGrant.get(key);
                //Check all roles
                String SQL = "INSERT INTO sig_grant (moduleId,sig_grant.key,description,defaultValue,acceptedValues) VALUES('"+moduleId+"','"+key+"',?,'"+gb.value+"','"+gb.acceptedOptions+"')";
                int idGrant = coreCfg.getMysql().preparedUpdateID(SQL, new Object[]{gb.description});
                if(idGrant>0)
                {
                SQL = "SELECT * FROM sig_grant_roles";
                try{
                Statement st2 = coreCfg.getMysql().createStatement();
                ResultSet rs1 = coreCfg.getMysql().getResultSet2(SQL,st2);
                 
                     while (rs1 != null && rs1.next()) {
                         
                         int idRole = rs1.getInt("id");
                         String lrole = rs1.getString("role");
                         int value = gb.getValue();
                         if(lrole.equalsIgnoreCase("ADMIN"))
                         {
                             value = GrantBean.ALL;
                         }
                         String SQL2 = "INSERT INTO sig_grant_values (idGrant,idRole,value) VALUES('"+idGrant+"','"+idRole+"','"+value+"')";
                         coreCfg.getMysql().executeUpdate(SQL2);
                     }
                     if (rs1 != null) {
                         rs1.close();
                         st2.close();
                     }
                 } catch (SQLException ex) {
                     Logger.getLogger(GrantModule.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }
            }
         }
     }

    public void register(String key, String description, int value, int acceptedValues) {
        GrantBean gb = new GrantBean(key,value,description,acceptedValues, coreCfg);
        moduleGrant.put(key, gb);
    }

   

    public Map<String,GrantBean> getMap() {
        return moduleGrant;
    }
    
}
