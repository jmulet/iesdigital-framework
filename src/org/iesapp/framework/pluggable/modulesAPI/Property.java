/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.modulesAPI;

/**
 *
 * @author Josep
 */
public class Property {
    
    public static final byte STRING_TYPE = 0;
    public static final byte BOOLEAN_TYPE = 1;
    public static final byte INTEGER_TYPE = 2;
    public static final byte FLOAT_TYPE = 3;
    public static final byte DOUBLE_TYPE = 4;
    
    protected String key;
    protected String value;
    protected String type;
    protected String description;

    public Property()
    {
        //Default constructor
    }
    
    public Property(String key, String value, String type, String description)
    {
        this.value = value;
        this.key = key;
        this.type = type;
        this.description = description;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
