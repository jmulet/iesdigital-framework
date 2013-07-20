/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.grantsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class BeanRole {
    protected int id;
    protected String role;
    protected String description;
    private final CoreCfg coreCfg;
    
    public BeanRole(final CoreCfg coreCfg)
    {
        this.coreCfg = coreCfg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    public void save()
    {
        if(this.id<=0)
        {
            insert();
        }
        else
        {
            update();
        }
    }

    private void insert() {
        String SQL1 = "INSERT INTO sig_grant_roles (role,description) VALUES('"+role+"','')";
        this.id = coreCfg.getMysql().executeUpdateID(SQL1);
    }

    private void update() {
        String SQL1 = "UPDATE sig_grant_roles SET role=?,description=? WHERE id="+this.id;
        coreCfg.getMysql().preparedUpdate(SQL1,new Object[]{role,description});
    }
    
    
    public void delete() {
        String SQL1 = "DELETE FROM sig_grant_roles WHERE id="+this.id;
        coreCfg.getMysql().executeUpdate(SQL1);
        this.id = -1;
    }
    
    public ArrayList<BeanRole> getAllRoles()
    {
        ArrayList<BeanRole> list = new ArrayList<BeanRole>();
        String SQL1 = "SELECT  * FROM sig_grant_roles";
        try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                BeanRole br = new BeanRole(coreCfg);
                br.id = rs1.getInt("id");
                br.role = rs1.getString("role");
                br.description = rs1.getString("description");
                list.add(br);
            }
            if(rs1!=null)
            {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(BeanRole.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    public ArrayList<String> getAllModules()
    {
        ArrayList<String> list = new ArrayList<String>();
        String SQL1 = "SELECT DISTINCT moduleId FROM sig_grant order by moduleId";
         try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {              
                list.add(rs1.getString(1));
            }
            if(rs1!=null)
            {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(BeanRole.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

   public BeanRole duplicate() {
        BeanRole newRole = new BeanRole(coreCfg);
        newRole.setId(-1);
        newRole.setDescription(description);
        newRole.setRole(role+"_2");
        newRole.save();
        int newId = newRole.getId();
        cloneEntries(newId);
        
        return newRole;
    }

    private void cloneEntries(int newId) {
        try {
            String SQL1 = "SELECT * FROM sig_grant_values WHERE idRole="+id;
            Statement st =  coreCfg.getMysql().createStatement(); 
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1, st);
            while(rs1!=null && rs1.next())
            {
               String SQL2 = "INSERT INTO sig_grant_values (idGrant,idRole,value) VALUES('"+
                       rs1.getInt("idGrant") +"','"+newId+"','"+rs1.getInt("value") +"')";
               coreCfg.getMysql().executeUpdate(SQL2);
            }
            rs1.close();
            st.close();
                    
        } catch (SQLException ex) {
            Logger.getLogger(BeanRole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public boolean exists(String roleName) {
        boolean exists = false;
        try {
            String SQL1 = "SELECT * FROM sig_grant_roles WHERE role='"+roleName+"'";
            Statement st =  coreCfg.getMysql().createStatement(); 
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1, st);
            if(rs1!=null && rs1.next())
            {
              exists = true;
            }
            rs1.close();
            st.close();
                    
        } catch (SQLException ex) {
            Logger.getLogger(BeanRole.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exists;
     }
    
     public void rename(String roleName){
         //rename users entries
         String SQL1 = "UPDATE usu_usuari SET GrupFitxes='"+roleName+"' WHERE GrupFitxes='"+role+"'";
         coreCfg.getMysql().executeUpdate(SQL1);
                 
         this.role = roleName;
         this.save();
         
         
     }
}

