/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.dialogs;
 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.iesapp.database.MyDatabase;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class LoginValidation {
   
    public static boolean isValidated(String abrev, String pwd, MyDatabase mysql)
    {
      boolean result=false;

      String validpwd = "";
      if(abrev==null)
      {
          return false;
      }

      if(abrev.equals("ADMIN"))
      {
                validpwd = CoreCfg.core_pwdSU;
                result = (pwd.equals(validpwd));
      }
      else if(abrev.equals("PREF"))
      {
                validpwd = CoreCfg.core_pwdPREF;
                result = (pwd.equals(validpwd));
      }
      else if(abrev.equals("GUARD"))
      {
               validpwd = CoreCfg.core_pwdGUARD;
               result = (pwd.equals(validpwd));
      }
      else
      {

        String SQL1 = "SELECT CONTRASENYA FROM usu_usuari WHERE "
                      + "nom='" + abrev + "'";



         try {
            Statement st = mysql.createStatement();
            ResultSet rs1 = mysql.getResultSet(SQL1,st);
                if(rs1 != null && rs1.next()) {
                     validpwd = rs1.getString("contrasenya");
                     result = (pwd.equals(validpwd)) || pwd.equals(CoreCfg.core_pwdSU);
                }
               if(rs1 != null) {
                rs1.close();
                st.close();
            }
            }catch (SQLException ex) {
                 //System.out.println(ex);
            }

        }

       
        return result;
    }



}
