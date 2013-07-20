/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.data;

import org.iesapp.clients.iesdigital.alumnat.Grup;
import org.iesapp.clients.sgd7.profesores.Profesores;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class User extends org.iesapp.clients.iesdigital.professorat.IUser {

    protected Profesores profesorSGD;
    private static User OFFLINE_USER;
     

    public User(CoreCfg coreCfg)
    {
        super(coreCfg.getIesClient());
    }
    
    public User(String abrev, CoreCfg coreCfg)
    {
        super(abrev, coreCfg.getIesClient());
        
        //---------------------------------------------------------
        if(this.idSGD>0)
        {
            //System.out.println("idSGD="+user.idSGD);
            this.profesorSGD = coreCfg.getSgdClient().getProfesores();
            this.profesorSGD.loadById(this.idSGD+"");
           
            //Li toca tutoria
            if( this.profesorSGD.isTutor() )
            {
                this.setGrant(TUTOR);
            }
            this.systemUser = this.profesorSGD.getSystemUser();
            this.claveUP = this.profesorSGD.getClaveUP();
        }
    }
    
    public static User getOffLineUser(CoreCfg coreCfg)
    {
        if(OFFLINE_USER==null)
        {
            OFFLINE_USER = new User(coreCfg);
            OFFLINE_USER.abrev = "";
            OFFLINE_USER.carrec = "";
            OFFLINE_USER.claveUP = "";
            OFFLINE_USER.grant = 0;
            OFFLINE_USER.grup = new Grup(coreCfg.getIesClient());
            OFFLINE_USER.idSGD = 0;
            OFFLINE_USER.name = "";
            OFFLINE_USER.profesorSGD = null;
            OFFLINE_USER.role = "";
            OFFLINE_USER.systemUser = "";
        }
        return OFFLINE_USER;
    }

    public static User getUser(String abrev, CoreCfg coreCfg) {
        return new User(abrev, coreCfg);
    }
   
     
}
