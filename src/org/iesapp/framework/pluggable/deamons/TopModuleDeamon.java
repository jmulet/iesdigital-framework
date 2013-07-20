/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.deamons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import javax.swing.Timer;
import org.iesapp.framework.util.CoreCfg;
 
/**
 *
 * @author Josep
 */
public abstract class TopModuleDeamon {
    
    public static final HashMap<String,TopModuleDeamon> deamons = new HashMap<String,TopModuleDeamon>();
    
    public static final byte STATUS_NORMAL = 1;
    public static final byte STATUS_SLEEPING = 0;
    public static final byte STATUS_AWAKE = 2;
    protected javax.swing.Timer timer;
    protected byte status = STATUS_NORMAL; //by-DEFAULT
    protected transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    protected BeanDeamon deamon;
    protected String moduleClassName="";
    protected CoreCfg coreCfg;
    private boolean manualCheck = false;
    
   
    /**
     * Get the value of status
     *
     * @return the value of status
     */
    
    public void initialize(CoreCfg coreCfg)
    {
        this.coreCfg = coreCfg;
    }
    
    public byte getStatus() {
        return status;
    }


    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }


    public void start(int milliseconds) {
        if(milliseconds>0)
        {
            timer = new Timer(milliseconds, new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                   checkStatusProcedure();
                }
            });
            timer.setInitialDelay(1000);
            
            timer.start();
        }
    }

   
    public void stop() {
        if(timer!=null)
        {
            timer.stop();
        }
    }
    

    protected abstract void checkStatusProcedure();
 
    public abstract String getMessage();
    
    public abstract HashMap getCurrentValues();
    
    public BeanDeamon getDeamon() {
        return deamon;
    }

    public void setDeamon(BeanDeamon deamon) {
        this.deamon = deamon;
    }

    public String getModuleClassName() {
        return moduleClassName;
    }

    public void setModuleClassName(String moduleClassName) {
        this.moduleClassName = moduleClassName;
    }
    
    public abstract void reset();
    
    
    public static HashMap<String,TopModuleDeamon> getActiveDeamons()
    {
        return deamons;
    }

    public void checkStatus() {
        manualCheck = true;
        checkStatusProcedure();
        manualCheck = false;
    }
}
