/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class TopModuleRegistry {
    private static final LinkedHashMap<String, TopModuleWindow> mapWindows = new LinkedHashMap<String, TopModuleWindow>();
    private static final HashMap<String, Integer> mapCurrentInstances = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> mapGlobalInstances = new HashMap<String, Integer>();
    
    public static void reset()
    {
        mapWindows.clear();
        mapCurrentInstances.clear();
        //mapGlobalInstances.clear();
    }
    
    public static String register(String className, TopModuleWindow win)
    {
        if(mapCurrentInstances.containsKey(className))
        {
            mapCurrentInstances.put(className, mapCurrentInstances.get(className)+1);
        }
        else
        {
            mapCurrentInstances.put(className, 1);
        }
        
        if(mapGlobalInstances.containsKey(className))
        {
            mapGlobalInstances.put(className, mapGlobalInstances.get(className)+1);
        }
        else
        {
            mapGlobalInstances.put(className, 1);
        }
        
        String identifier = className+"$"+mapGlobalInstances.get(className);
        mapWindows.put(identifier, win);
       
        //System.out.println("after creating new instance: targeting "+className);
        //System.out.println("global:"+mapGlobalInstances);
        //System.out.println("current:"+mapCurrentInstances);
        
        return identifier;
    }
    
    public static TopModuleWindow findId(String id)
    {
        return mapWindows.get(id);
    }

    public static boolean containsWin(String uniqueId) {
        return mapWindows.containsKey(uniqueId);
    }

    public static void remove(String uniqueId) {
        
        //System.out.println("before removing the situation is: targeting "+uniqueId);
        //System.out.println("global:"+mapGlobalInstances);
        //System.out.println("current:"+mapCurrentInstances);
        
        String prefixId = StringUtils.BeforeLast(uniqueId,"$");
        if(mapCurrentInstances.containsKey(prefixId))
        {
            if(containsWin(uniqueId))
            {
                mapCurrentInstances.put(prefixId, mapCurrentInstances.get(prefixId)-1);
                mapWindows.get(uniqueId).dispose();
                mapWindows.remove(uniqueId);
            }
        }
        
        //System.out.println("after removing the situation is:");
        //System.out.println("global:"+mapGlobalInstances);
        //System.out.println("current:"+mapCurrentInstances);
        
    }
    
    public static ArrayList<String> getCurrentInstancesOf(String classname) {
        ArrayList<String> list = new ArrayList<String>();

        for (String ky : mapWindows.keySet()) {
            if (ky.startsWith(classname)) {
                list.add(ky);
            }
        }

        return list;
    }

    public static int getCount() {
        return mapWindows.size();
    }

    public static Collection<TopModuleWindow> listWindows() {
         return mapWindows.values();
    }
    
}
