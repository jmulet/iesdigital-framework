/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import org.iesapp.clients.iesdigital.ICoreData;
import org.iesapp.clients.iesdigital.IesDigitalClient;

/**
 *
 * @author Josep
 * 
 * This is extended in order to allow generalization of this client
 */
public class IesClient extends IesDigitalClient {
      
    public IesClient(CoreCfg coreCfg)
    {
        super(coreCfg.getMysql(), coreCfg.getSgd(), (ICoreData) coreCfg);
    }
    
}
