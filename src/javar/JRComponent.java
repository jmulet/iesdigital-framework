/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javar;

import java.util.HashMap;
import javax.swing.AbstractButton;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public interface JRComponent {
    
    public void setValue(String value);
    public String getValue();
    public boolean check();
    public String getId();
    public void setId(String id);
    public void setEnable(boolean enabled);
    public void setRequired(boolean required);
    public boolean isRequired();
    public void focus();
    public void setLabel(String label);
    public void    setSelectable(boolean selectable, String selectionFieldName);
    public boolean isSelectable();
    public boolean isSelected();
    public void setSelected(boolean selected);
    public AbstractButton getRadioButton();
    public String getSelectionFieldName();
    public void setSelectionFieldName(String name);     
    public void setIniParams(HashMap<String,Object> params);
    public boolean isAddToMap();
    public void setAddToMap(boolean settomap);
    public boolean hasChanged();
    public void setCoreCfg(CoreCfg coreCfg);
}
