/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import java.util.HashMap;

/**
 *
 * @author Josep
 */
public class IniParameters {
    protected HashMap<String, Object> map;
 
    public IniParameters(HashMap<String,Object> map)
    {
        this.map = map;
    }

    
    public IniParameters() {
        this.map = new HashMap<String,Object>();
    }
    
    public int getInt(String key, int defecte)
    {
        int value = defecte;
        if(getMap().containsKey(key))
        {
            try{
                Object obj = getMap().get(key);
                if(obj instanceof Integer)
                {
                    value = (Integer) obj;
                }
                else if(obj instanceof Float)
                {
                    value = ((Float) obj).intValue();
                }
                else if(obj instanceof Double)
                {
                    value = ((Double) obj).intValue();
                }
                else if(obj instanceof Boolean)
                {
                    value = ((Boolean) obj)? 1 : 0;
                }
                else if(obj instanceof String)
                {
                    value = Integer.parseInt((String) obj);
                }
            }
            catch(Exception e)
            {
                //
            }
                   
        }
        return value;
    }
    
    public float getFloat(String key, float defecte)
    {
        float value = defecte;
        if(getMap().containsKey(key))
        {
            try{
                Object obj = getMap().get(key);
                if(obj instanceof Integer)
                {
                    value = 1f*((Integer) obj);
                }
                else if(obj instanceof Float)
                {
                    value = (Float) obj;
                }
                else if(obj instanceof Double)
                {
                    value = ((Double) obj).floatValue();
                }
                else if(obj instanceof Boolean)
                {
                    value = ((Boolean) obj)? 1f : 0f;
                }
                else if(obj instanceof String)
                {
                    value = Float.parseFloat((String) obj);
                }
            }
            catch(Exception e)
            {
                //
            }
                   
        }
        return value;
    }


    public double getDouble(String key, double defecte)
    {
        double value = defecte;
        if(getMap().containsKey(key))
        {
            try{
                Object obj = getMap().get(key);
                if(obj instanceof Integer)
                {
                    value = 1.0*((Integer) obj);
                }
                else if(obj instanceof Float)
                {
                    value = 1.0*((Float) obj);
                }
                else if(obj instanceof Double)
                {
                    value = ((Double) obj);
                }
                else if(obj instanceof Boolean)
                {
                    value = ((Boolean) obj)? 1.0 : 0.0;
                }
                else if(obj instanceof String)
                {
                    value = Double.parseDouble((String) obj);
                }
            }
            catch(Exception e)
            {
                //
            }
                   
        }
        return value;
    }
    
    public boolean getBoolean(String key, boolean defecte)
    {
        boolean value = defecte;
        if(getMap().containsKey(key))
        {
            try{
                Object obj = getMap().get(key);
                if(obj instanceof Integer)
                {
                    value = ((Integer) obj).equals(1);
                }
                else if(obj instanceof Float)
                {
                    value = ((Float) obj).equals(1f);
                }
                else if(obj instanceof Double)
                {
                    value = ((Double) obj).equals(1.0);
                }
                else if(obj instanceof Boolean)
                {
                    value = ((Boolean) obj);
                }
                else if(obj instanceof String)
                {
                    value =((String) obj).equalsIgnoreCase("yes") || ((String) obj).equalsIgnoreCase("true");
                }
            }
            catch(Exception e)
            {
                //
            }
                   
        }
        return value;
    }

    public String getBoolean(String key, String defecte)
    {
        String value = defecte;
        if(getMap().containsKey(key))
        {
            try{
                Object obj = getMap().get(key);
                value = obj.toString();
            }
            catch(Exception e)
            {
                //
            }
                   
        }
        return value;
    }

    public HashMap<String, Object> getMap() {
        return map;
    }
    
    public void setMap(HashMap<String, Object> map) {
        this.map = map;
    }
}
