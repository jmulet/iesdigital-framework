/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.actuacions;

import com.l2fprod.common.swing.JTaskPaneGroup;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javar.JRComponent;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import org.iesapp.clients.iesdigital.actuacions.Actuacio;
import org.iesapp.clients.iesdigital.actuacions.BeanCategory;
import org.iesapp.clients.iesdigital.actuacions.BeanFieldSet;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class CreateUI {
    protected HashMap<String, JRComponent> uiComponentsMap;
    
    public CreateUI(Container mainPane, Actuacio act, CoreCfg coreCfg) {
        uiComponentsMap = new HashMap<String,JRComponent>();
        Container currentPane = mainPane;
        for(BeanFieldSet bean: act.beanRule.getFields())
        {            
            try {
                if(bean.getFieldRenderClass()!=null && !bean.getFieldRenderClass().isEmpty())
                {
                 Class<?> loadClass = Class.forName(bean.getFieldRenderClass());
                 Object newInstance = loadClass.newInstance();
                 //Determina si és un component de javar.JR
                 //Els components de swing es poden emprar però no es posaran dins el mapa uiComponentsMap
                 if(bean.getFieldRenderClass().startsWith("javar.JR"))
                 {
                 //Crea una instancia i passa els parametres d'iniciacio
                 JRComponent jrComponent1 = (JRComponent) newInstance;
                 jrComponent1.setCoreCfg(coreCfg);
                 if(bean.getFieldRenderClassParams()!=null) {
                        jrComponent1.setIniParams(bean.getFieldRenderClassParams());
                  }
                 jrComponent1.setValue((String) act.map.get(bean.getFieldName()));
                 jrComponent1.setLabel(bean.getFieldDescription());
                 jrComponent1.setEnable(!act.locked && (bean.getEditable().equals("S") || (bean.getEditable().equals("P") && act.admin)));
                 jrComponent1.setRequired(bean.isRequired());
                 jrComponent1.setId(bean.getFieldName());
                 jrComponent1.setAddToMap(bean.isAddToMap());
                  
                 if(bean.getSelectableField()!=null && !bean.getSelectableField().isEmpty())
                 {
                     
                    jrComponent1.setSelectable(true, bean.getSelectableField());
                    if(jrComponent1.isSelectable())
                    {
                        jrComponent1.setSelected(act.map.get(bean.getSelectableField())!=null &&
                                              !((String) act.map.get(bean.getSelectableField())).trim().isEmpty());
                    }    
                 }
                 
                 uiComponentsMap.put(bean.getFieldName(), jrComponent1);
                 }
                 
                 JComponent jcomponent = (JComponent) newInstance;
                 
                 if(bean.isVisible())
                 {
                    //System.out.println("Component "+bean.fieldName+" goes to "+bean.category);
                    if(bean.getCategory().isEmpty())
                    {
                        mainPane.add(jcomponent);
                    }
                    else
                    {
                        JTaskPaneGroup jtk1 = null;
                        for(Component comp: mainPane.getComponents())
                        {                 
                            if(comp.getClass().equals(JTaskPaneGroup.class) && comp.getName().equals(bean.getCategory()) )
                            {
                                        jtk1 = (JTaskPaneGroup) comp;
                                        break;
                            }
                        }
                        BeanCategory bcat = act.beanRule.getListCategories().get(bean.getCategory());
                        if(jtk1 == null)
                        {
                            jtk1 = new JTaskPaneGroup();                            
                            jtk1.setTitle(bcat.getTitle());
                            jtk1.setAnimated(false);
                            jtk1.setCollapsable(bcat.isCollapsable());
                            jtk1.setExpanded(!bcat.isCollapsed());
                            jtk1.setName(bean.getCategory());
                           
                            
                        }
                        jtk1.add(jcomponent);
                        mainPane.add(jtk1);
                        
                        
                    }
                 }
                }
            } catch (Exception ex) {
                Logger.getLogger(BasicForm.class.getName()).log(Level.SEVERE, null, ex);
            }   
            
        }
        
        //Comprova si hi ha components selectables que apareixen en group
        for(String g: act.beanRule.getSelectableGroups())
        {
            ArrayList<String> parsed = StringUtils.parseStringToArray(g, ",", StringUtils.CASE_INSENSITIVE);
            if(parsed.size()>1)
            {
                ButtonGroup bg = new ButtonGroup();
                for(String s2: parsed)
                {
                    //Cerca la id del component
//                    String selection = uiComponentsMap.get(s2).getSelectionFieldName();
//                    String selectedValue = (String) act.map.get(selection);
                     JRComponent comp = uiComponentsMap.get(s2);
                    if(comp!=null)
                    {
                        AbstractButton radioButton = comp.getRadioButton();
                        bg.add(radioButton);
                        //bg.setSelected(radioButton.getModel(), selectedValue!=null && !selectedValue.trim().isEmpty());
                    }
                    }
              
            }
        }
    }

    public HashMap<String, JRComponent> getUiComponentsMap() {
        return uiComponentsMap;
    }
 
}
