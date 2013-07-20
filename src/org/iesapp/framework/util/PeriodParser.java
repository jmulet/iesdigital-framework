/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iesapp.util.StringUtils;



/**
 *
 * @author Josep
 * 
 */

public class PeriodParser
{
    private ArrayList<String> listPeriods;
    protected ArrayList<java.util.Date> listIniDate;
    protected ArrayList<java.util.Date> listEndDate;
    private final CoreCfg coreCfg;
    
    public PeriodParser(String cmds, CoreCfg coreCfg)
    {
        this.coreCfg = coreCfg;
        listPeriods = new ArrayList<String>();
        listIniDate = new ArrayList<java.util.Date>();
        listEndDate = new ArrayList<java.util.Date>();
        
        if(cmds==null || cmds.trim().isEmpty()) 
        {
            //System.out.println("Warning: "+getClass().getName()+": invalid input string '"+cmds+"'");
            return;
        }
        
        ArrayList<String> list = StringUtils.parseStringToArray(cmds, ",", StringUtils.CASE_INSENSITIVE);
       
        
        for(int i=0; i<list.size(); i++)
        {
            String interval = list.get(i);
            String2DateSI s2d = new String2DateSI(interval);
            listIniDate.add(s2d.getInici());
            listEndDate.add(s2d.getFinal());
            listPeriods.add(interval);
        }
        
    }
    
    /*
     * MTIPUS = DIMECRES; EXPULSIO; ETC
     */
    public void doDBUpdate(int exp2, int idActuacio, String mtipus) 
    {
            if(exp2<=0 || idActuacio<=0 || listIniDate==null || listIniDate.isEmpty()) {
                 return;
            }
            
          
            for(int i=0; i<listIniDate.size(); i++)
            {
                String SQL2="INSERT INTO tuta_dies_sancions (exp2, idActuacio, desde, fins, tipus) VALUES (?,?,?,?,?)";
                Object[] obj = new Object[]{exp2, idActuacio, listIniDate.get(i), listEndDate.get(i), mtipus};
                int nup = coreCfg.getMysql().preparedUpdate(SQL2, obj);
            
            }
      
    }

    /**
     * @return the listIniDate
     */
    public ArrayList<java.util.Date> getListIniDate() {
        return listIniDate;
    }

    /**
     * @return the listEndDate
     */
    public ArrayList<java.util.Date> getListEndDate() {
        return listEndDate;
    }
    
}
class String2DateSI
{
        private java.util.Date date1;
        private java.util.Date date2;
        
        public String2DateSI(String input)
        {
            String iniString = "";
            String finString = "";
            if(input.contains("a"))
            {
               iniString = StringUtils.BeforeFirst(input, "a").trim();
               finString = StringUtils.AfterFirst(input, "a").trim();
            }
            else
            {
                iniString = input.trim();
                finString = iniString;
            }
            SimpleDateFormat df = null;
            if(input.contains("-")) {
                df = new SimpleDateFormat("dd-MM-yyyy");
            } 
            else {
                df = new SimpleDateFormat("dd/MM/yyyy");
            } 
                
                 
            try {
                date1= df.parse(iniString);
                date2= df.parse(finString);
            } catch (ParseException ex) {
                Logger.getLogger(PeriodParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public java.util.Date getInici()
        {
            return date1;
        }
        
        public java.util.Date getFinal()
        {
            return date2;
        }
  
}