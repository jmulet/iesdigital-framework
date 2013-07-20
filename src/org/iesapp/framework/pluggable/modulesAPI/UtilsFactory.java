/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.modulesAPI;

import java.util.ArrayList;
import java.util.HashMap;
import org.iesapp.util.StringUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
             
            
/**
 *
 * @author Josep
 */
public class UtilsFactory {
    
    //parse: <whatsoever key1="value1" key2="value2" ..... into a map
    public static HashMap<String, String> attributesToMap(NamedNodeMap attributes) {
        HashMap<String, String> map = new HashMap<String, String>();
        
        for(int i=0; i<attributes.getLength(); i++)
        {
            Node item = attributes.item(i);
            map.put(item.getNodeName(), item.getNodeValue());
        }
        return map;
    }

    /**parse: <whatsoever>
               <param key="key1" value="value1"/>
               <param key="key2" value="value2"/>
               ....
               </whatsover>
               * into a map string-string
     **/
    public static HashMap<String, String> nodeListToStringMap(NodeList childNodes1) {
        
        HashMap<String, String> map = new HashMap<String, String>();
        for (int ki = 0; ki < childNodes1.getLength(); ki++) {
            NamedNodeMap attributes = childNodes1.item(ki).getAttributes();
            String key = "";
            String value = "";
            Node namedItem1 = attributes.getNamedItem("key");
            Node namedItem2 = attributes.getNamedItem("value");
            if (namedItem1 != null) {
                key = namedItem1.getNodeValue();
            }
            if (namedItem2 != null) {
                value = namedItem1.getNodeValue();
            }
            if (!key.isEmpty()) {
                map.put(key, value);
            }
        }
        return map;
    }
    
     /**parse: <whatsoever>
               <param key="key1" value="value1" type="type1"/>
               <param key="key2" value="value2" type="type2"/>
               ....
               </whatsover>
               * into a map string-object  (default type is string)
     **/
    public static HashMap<String, Object> nodeListToObjectMap(NodeList childNodes1, HashMap<String, String> descriptionMap) {
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (int ki = 0; ki < childNodes1.getLength(); ki++) {
            if(!childNodes1.item(ki).getNodeName().equals("parameter"))
            {
                continue;
            }
            NamedNodeMap attributes = childNodes1.item(ki).getAttributes();
            String key = "";
            String value = "";
            String type = "String";
            String description = null;
            Node namedItem1 = attributes.getNamedItem("key");
            Node namedItem2 = attributes.getNamedItem("value");
            Node namedItem3 = attributes.getNamedItem("type");
            Node namedItem4 = attributes.getNamedItem("description");
            if (namedItem1 != null) {
                key = namedItem1.getNodeValue();
            }
            if (namedItem2 != null)
            {
                value = namedItem2.getNodeValue();
            }
            if (namedItem4 != null) {
                description= namedItem4.getNodeValue();
            }
            //Add CDATA support
            NodeList valueList = ((Element) childNodes1.item(ki)).getElementsByTagName("value");
            NodeList descList = ((Element) childNodes1.item(ki)).getElementsByTagName("description");
            if(valueList.getLength()>0)
            {
               Node firstChild = valueList.item(0).getFirstChild();
               if (firstChild!=null && firstChild instanceof CharacterData) {
                    value = ((CharacterData) firstChild).getData();
                }
            }
            if(descList.getLength()>0)
            {
               Node firstChild = descList.item(0).getFirstChild();
               if (firstChild!=null && firstChild instanceof CharacterData) {
                    description = ((CharacterData) firstChild).getData();
                }
            }
            //END CDATA
            if (namedItem3 != null) {
                type= namedItem3.getNodeValue();
            }
          
            if (!key.isEmpty() && value!=null) {
                Object obj;
                if(type.equals("Integer"))
                {
                    obj = Integer.parseInt(value);
                }
                else if(type.equals("Boolean"))
                {
                    obj = value.equalsIgnoreCase("yes");
                }
                else if(type.equals("Float"))
                {
                    obj = Float.parseFloat(value);
                }
                else if(type.equals("Double"))
                {
                    obj = Double.parseDouble(value);
                }
                else                   
                {
                    obj = value;
                }
                
                map.put(key, obj);
                
            }
            else if (!key.isEmpty() && value==null) {
                map.put(key, null);
            }
            if(description!=null)
            {
                descriptionMap.put(key, description);
            }
        }
        return map;
    }


    public static Element objectMapToNodeList(Document doc, String elementName, HashMap<String, Object> iniParameters, HashMap<String, String> iniParametersDescription) {
        Element createElement = doc.createElement(elementName);

        for (String key : iniParameters.keySet()) {
            Element parameter = doc.createElement("parameter");
            parameter.setAttribute("key", key);
            Object obj = iniParameters.get(key);
            String value;
            if(obj==null)
            {
                value="null";
            }
            else
            {
                if(obj.getClass().equals(Boolean.class))
                {
                    value = ((Boolean) obj)?"yes":"no";
                }
                else 
                {
                    value = obj.toString();
                }
            }
            //parameter.setAttribute("value", value); -->Use CDATA
            parameter.setAttribute("type", obj.getClass().getSimpleName());
            Element elementValue = doc.createElement("value");
            elementValue.appendChild(doc.createCDATASection(value));
            parameter.appendChild(elementValue);
            
            if(iniParametersDescription.containsKey(key))
            {
               // parameter.setAttribute("description", iniParametersDescription.get(key)); --> Use CDATA
                 Element elementDesc = doc.createElement("description");
                 elementDesc.appendChild(doc.createCDATASection(iniParametersDescription.get(key)));
                 parameter.appendChild(elementDesc);
            }
            createElement.appendChild(parameter);
        }

        return createElement;
    }

    public static ArrayList<String> nodeListToArray(NodeList childNodes1, String lib, String path) {
        ArrayList<String> list = new ArrayList<String>();
        for (int r = 0; r < childNodes1.getLength(); r++) {
            if (childNodes1.item(r).getNodeName().equalsIgnoreCase(lib)) {
                Node namedItem = childNodes1.item(r).getAttributes().getNamedItem(path);
                if (namedItem != null) {
                    list.add(namedItem.getNodeValue());
                }
            }
        }
        return list;
    }

    /**
     * Determine if one module has to be loaded acording with
     * the roles system
     * @param module
     * @param role
     * @return 
     */
    public static boolean isModuleEnabled(BeanModule module, String role, String abrev) {
        boolean enabled = false;
        if(module==null || role==null || abrev==null || role.isEmpty() || abrev.isEmpty())
        {
            return false;
        }
        
        String currentRole = role.toUpperCase().trim();
        String currentAbrev = abrev.toUpperCase().trim();
         
        //Check the field role in the module bean
        
        //Enabled for everyone
        if(module.isEnabled())
        {
            byte mode = 1;  //+ include
            String validRoles = module.getRoles().trim();
            if(validRoles.startsWith("+"))
            {
                validRoles = validRoles.substring(1);
            }
            else if(validRoles.startsWith("-"))
            {
                validRoles = validRoles.substring(1);
                mode = 0;  //- exclude
            }
            ArrayList<String> parsed = StringUtils.parseStringToArray(validRoles, ",", StringUtils.CASE_UPPER);
            ////System.out.println(parsed);
            if(parsed.contains("*"))
            {
                enabled = true;
            }
            else                
            {
                boolean contains = parsed.contains(currentRole);
                enabled = (mode==1 && contains) || (mode==0 && !contains); 
            }
        }
        
        //Check in users field (if not empty)
        //allows to add/remove individual users without definining new roles
        if(module.getUsers()!=null && !module.getUsers().isEmpty())
        {
            byte mode = 1;  //+ include
            String users = module.getUsers().trim();
            if(users.startsWith("+"))
            {
                users = users.substring(1);
            }
            else if(users.startsWith("-"))
            {
                users = users.substring(1);
                mode = 0;  //- exclude
            }
            
            ArrayList<String> parsed = StringUtils.parseStringToArray(users, ",", StringUtils.CASE_UPPER);
            if(parsed.contains("*"))
            {
                enabled = true;
            }
            else                
            {
                boolean contains = parsed.contains(currentAbrev);
                if(mode==0) //exclude these users
                {
                    enabled &= !contains;
                }
                else //include these users
                {
                    enabled |= contains;
                }
                
            }
            
        }
        
        
        
        
        
        return enabled;
    }


}
