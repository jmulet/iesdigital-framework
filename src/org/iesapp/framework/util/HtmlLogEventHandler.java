/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 *
 * @author Josep
 */
public abstract class HtmlLogEventHandler implements ActionListener {

    private String Idreg;
    private HashMap<String,Object> parameters = new HashMap<String,Object>();

    /**
     * @return the Idreg
     */
    public String getIdreg() {
        return Idreg;
    }

    /**
     * @param Idreg the Idreg to set
     */
    public void setIdreg(String Idreg) {
        this.Idreg = Idreg;
    }

    /**
     * @return the parameters
     */
    public HashMap<String,Object> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(HashMap<String,Object> parameters) {
        this.parameters = parameters;
    }
    
}
