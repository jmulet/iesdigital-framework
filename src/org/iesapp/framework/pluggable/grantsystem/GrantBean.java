/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.grantsystem;

import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */


public class GrantBean {
    public static final int NONE = 1;
    public static final int ALL = 2;
    public static final int BELONGS = 4;
    public static final int NESE = 8;
    public static final int REPETIDOR = 16;
    
    public static final int MODIFIER_NESE = 32;
    public static final int MODIFIER_REPETIDOR = 64;
    
    
    public static final int BASIC_CONFIG = NONE | ALL; //accepts yes/no
    public static final int EXTENDED_CONFIG = BASIC_CONFIG | BELONGS;  //accepts yes/no/belongs
    public static final int FULL_CONFIG = EXTENDED_CONFIG | NESE | REPETIDOR; //all options enabled
    private final CoreCfg coreCfg;

    public GrantBean(CoreCfg coreCfg)
    {
        this.coreCfg = coreCfg;
    }
    
    public boolean isNone()
    {
        return value==0 || ((value & NONE) == NONE);
    } 
    
    public boolean isAll()
    {
        return (value & ALL) == ALL;
    }
    
    public boolean isBelongs()
    {
        return (value & BELONGS) == BELONGS;
    }
    
    public boolean isNese()
    {
        return (value & NESE) == NESE;
    }
    
    public boolean isRepetidor()
    {
        return (value & REPETIDOR) == REPETIDOR;
    }
    
    public String getModifierNESE()
    {
        String modifier = "OR";
        if((value & MODIFIER_NESE) == MODIFIER_NESE)
        {
            modifier = "AND";
        }
        return modifier;
    }
    
     public String getModifierREPETIDOR()
    {
        String modifier = "OR";
        if((value & MODIFIER_REPETIDOR) == MODIFIER_REPETIDOR)
        {
            modifier = "AND";
        }
        return modifier;
    }
    
    public static String valueToString(int value) {
        String str = "";
        if( ((value & NONE) == NONE) || value==0 )
        {
            str += "NONE ";
        }
         if( (value & ALL) == ALL)
        {
            str += "ALL ";
        }
        if( (value & BELONGS) == BELONGS)
        {
            str += "BELONGS | ";
        }
        if( (value & MODIFIER_NESE) == MODIFIER_NESE)
        {
            str += " AND ";
        }
        else
        {
            str += " OR ";
        }
           if( (value & NESE) == NESE)
        {
            str += "NESE | ";
        }
        if( (value & MODIFIER_REPETIDOR) == MODIFIER_REPETIDOR)
        {
            str += " AND ";
        }
        else
        {
            str +=" OR ";
        }
        if( (value & REPETIDOR) == REPETIDOR)
        {
            str += "REPETIDOR";
        }
   
        return str;
    }
    
    //Works as a binary system
    //int kk = BELONGS | REPETIDOR;
    
    protected String key;
    protected int value; //use one of the above
    protected String description;
    protected int acceptedOptions;
    protected int id=-1;

    public GrantBean(String key, int value, String description, int acceptedOptions, CoreCfg coreCfg) {
        this.coreCfg = coreCfg;
        this.key = key;
        this.value = value;
        this.description = description;
        this.acceptedOptions = acceptedOptions;
    }
   
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAcceptedOptions() {
        return acceptedOptions;
    }

    public void setAcceptedOptions(int acceptedOptions) {
        this.acceptedOptions = acceptedOptions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void update() {
        if(this.id<=0)
        {
            return;
        }
        
        String SQL1 = "UPDATE sig_grant_values SET value='"+value+"' WHERE id="+id;
        //System.out.println("UPDATE "+SQL1);
        coreCfg.getMysql().executeUpdate(SQL1);
    }

    @Override
    public String toString()
    {
       return "key: "+key+" -> "+ valueToString(value);
    }
   
}
