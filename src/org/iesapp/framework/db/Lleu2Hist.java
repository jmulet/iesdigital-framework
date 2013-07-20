package org.iesapp.framework.db;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.iesapp.clients.sgd7.alumnos.Alumnos;
import org.iesapp.clients.sgd7.alumnos.BeanAlumno;
import org.iesapp.clients.sgd7.incidencias.BeanIncidencias;
import org.iesapp.clients.sgd7.incidencias.Incidencias;
import org.iesapp.database.MyDatabase;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.DataCtrl;

/**
 *
 * @author Josep
 */
public class Lleu2Hist {
    private final boolean programada;
    private String log="";
    public int numInconsistencies = 0;
   
    private int ID_OBSERVACION_AGACUMULACIO;
    private final String invoker;
    
    public static final byte MODE_SIMULACIO=0;
    public static final byte MODE_ACTUALITZA=1;
    
    private final MyDatabase sgd;
    private final MyDatabase mysql;
    private boolean success = true;
    private final CoreCfg coreCfg;
 
    
 
    public Lleu2Hist(String invoker, CoreCfg coreCfg)
    {
        this.coreCfg = coreCfg;
        programada = invoker.equalsIgnoreCase("PROGRAMADA");
        this.invoker = invoker;
        
        sgd = new MyDatabase(CoreCfg.coreDB_sgdHost, CoreCfg.coreDB_sgdDB, 
                CoreCfg.coreDB_sgdUser, CoreCfg.coreDB_sgdPasswd, CoreCfg.coreDB_sgdParam);
        boolean q1 = sgd.connect();
        
        
        mysql = new MyDatabase(CoreCfg.core_mysqlHost, CoreCfg.core_mysqlDB, 
                CoreCfg.core_mysqlUser, CoreCfg.core_mysqlPasswd, CoreCfg.coreDB_mysqlParam);
        boolean q2 = mysql.connect();
        
        if(!q1 || !q2) {
            JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(),"WARNING: org.iesapp.framework.db.LLeu2Hist no pot conectar-se a les bases sgd/fitxes->"+q1+"/"+q2);
            success = false;
        }
        
        ID_OBSERVACION_AGACUMULACIO = coreCfg.getSgdClient().getTipoObservaciones().getID("Acumulació de 5 Amon", "faltas");
        if(ID_OBSERVACION_AGACUMULACIO==0) {
            ID_OBSERVACION_AGACUMULACIO = (Integer) CoreCfg.configTableMap.get("idObservAcumulAL");
        }
        if(ID_OBSERVACION_AGACUMULACIO==0) {
            JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(),"WARNING: org.iesapp.framework.db.LLeu2Hist no pot trobar ID_OBSERVACION_AGACUMULACIO");
            success = false;
        }
            
    
    }
    
    public void close()
    {
        if(mysql!=null) {
            mysql.close();
        }
        if(sgd!=null) {
            sgd.close();
        }
    }
    
    
    //Obte els alumnes que requereixen la conversio
    
    public List<BeanLleu2Hist> getAlumnes()
    {
        List<Integer> idAlumnos = coreCfg.getSgdClient().getIncidenciasCollection().getIdIncidenciasThreshold(new String[]{"AL","AM"}, true, 5);
        
        ArrayList<BeanLleu2Hist> list = new ArrayList<BeanLleu2Hist>();
        
        for(int i=0; i<idAlumnos.size(); i++)
        {
            int idAlumno = idAlumnos.get(i);
            
            BeanLleu2Hist bean = new BeanLleu2Hist();
            ArrayList<BeanIncidencias> aux = coreCfg.getSgdClient().getIncidenciasCollection().getIncidenciasOf(idAlumno,new String[]{"AL","AM"},-1, true, -1, "ASC");
            
            bean.setListInc(  aux  );
            Alumnos alumno = new Alumnos(idAlumno);
            alumno.getTutoriaInfo();
            
            bean.setAlumno( alumno.getBean() );
            list.add(bean);                    
        }
            
        return list;
    }


    //Comprova la consistencia de tots els alumnes de la base de dades
    public void checkConsistencia(int mode)
    {
        numInconsistencies = 0; 
        log = "";
        String SQL1 = "SELECT id, expediente, nombre FROM alumnos order by nombre";
        try {
            Statement st = sgd.createStatement();
            ResultSet rs1 = sgd.getResultSet(SQL1,st);
            while(rs1!=null && rs1.next())
            {
                int id = rs1.getInt("id"); 
                doCheckInconsistencies(new Alumnos(id).getBean(), mode); 
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Lleu2Hist.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    private int doCheckInconsistencies(BeanAlumno ba, int mode)
    {
        int numInconsistencies0 = numInconsistencies;
        int nAG = coreCfg.getSgdClient().getIncidenciasCollection().getTotalIncidenciasOf(ba.getId(),
                new String[]{"AG"}, ID_OBSERVACION_AGACUMULACIO, false);
        
        int nALH = coreCfg.getSgdClient().getIncidenciasCollection().getTotalIncidenciasOf(ba.getId(),
                new String[]{"ALH"}, -1, false);

        //Tipus d'inconsistencia 0; no hi pot haver amonestacions lleus
        //amb el motiu "Acumulacio de 5 lleus"
        int nErrors = coreCfg.getSgdClient().getIncidenciasCollection().getTotalIncidenciasOf(ba.getId(),
                new String[]{"AL"}, ID_OBSERVACION_AGACUMULACIO, false);
        
        if (nErrors != 0) {
            log += "?? ALUMNE:"+ba.getExpediente()+" Inconsistencia: Té " + nErrors + " AL amb motiu `acumulacio´\n";
            numInconsistencies += 1;
            for (int k = 0; k < nErrors; k++) {
                ArrayList<BeanIncidencias> wrongInc =
                        coreCfg.getSgdClient().getIncidenciasCollection().getIncidenciasOf(ba.getId(), new String[]{"AL"},
                        ID_OBSERVACION_AGACUMULACIO, true, -1, "ASC");

                 if(mode==MODE_SIMULACIO)
                 {
                    log +="\tSIMULACIO: s'ha d'esborrar incidencia AL amb id="+wrongInc.get(k).getId()+"\n";
                 }
                 else
                 {
                    Incidencias inc2 = new Incidencias(wrongInc.get(k));
                    int delete = inc2.delete();
                    if (delete > 0) {
                        log += "\t\tS'ha esborrat AL amb id=" + inc2.getId() + "\n";
                    }
                 }
        }
        }

        //Primer tipus d'inconsistencia es que nALH no sigui multiple de 5
        if (nALH!=0 && (nALH % 5 != 0)) {
       
             
            ArrayList<BeanIncidencias> amonHist =
                        coreCfg.getSgdClient().getIncidenciasCollection().getIncidenciasOf(ba.getId(), new String[]{"ALH"},
                        0, true, -1, "DESC");
            
            log += "?? ALUMNE:"+ba.getExpediente()+" Inconsistencia: Té núm ALH=" + amonHist.size() + " que no és múltiple de 5\n";
            
            numInconsistencies += 1;
            
           
            //Solucio: vaig llevant ALH i les torn a convertir amb AL fins que sigui multiple de 5
          int pos = 0;
          int mida = amonHist.size();
          while(nALH % 5 != 0 && pos<mida)
          {
              Incidencias inc = new Incidencias(amonHist.get(pos));
              inc.findCodigoIncidencia("AL", "faltas"); //La canvia a AL normal
        
              int nup = 0;
              if(mode==MODE_SIMULACIO)
              {
                  log += "\tSIMULACIO:: canviam de ALH-->AL id=" + inc.getId() + "\n";
                  nup = 1;
              }
              else
              {
                nup = inc.save();
                if(nup>0) {
                        log += "\tS'ha canviat de ALH-->AL id=" + inc.getId() + "\n";
                    }
              }
                        
              pos +=1;
              nALH -=1;
              if(nup == 0) {
                    break;
                }
        }
        }
        
        
        //Sobren o falten AG per acumulacio de 5 lleus
        
        int excess = (int) (nAG - nALH / 5.);        
        if (excess != 0) {
            log += "?? ALUMNE: "+ba.getExpediente()+" Inconsistencia: Té núm ALH=" + nALH + " i núm AG(per acumulació)=" + nAG + "\n";
            numInconsistencies += 1;

            
            if(excess<0)
            {
              
                //Li he de posar més amonestacions greus
                for(int k=0; k<(int) Math.abs(excess); k++)
                {
                    if(mode==MODE_SIMULACIO)
                    {
                        log +="\tSIMULACIO: Cal afegir una AG per acumulació de 5 lleus\n";                        
                    }
                    else
                    {
                       Incidencias inc = createAGacumul(ba);
                       inc.setId(-1);
                       int addGreu2 = inc.save();
                       if(addGreu2<1) { 
                            log += "  Error adding AG. ";
                        } 
                       log +="   add AG id="+inc.getId()+"\n";
                    }
                }

            }
            else
            {
                //Li he de llevar amonestacions greus
                 ArrayList<BeanIncidencias> incGreusAcumul = 
                    coreCfg.getSgdClient().getIncidenciasCollection().getIncidenciasOf(ba.getId(), new String[]{"AG"}, 
                                        ID_OBSERVACION_AGACUMULACIO, true, -1, "DESC");

                  
                 for(int k=0; k<excess; k++)
                 {
                    if(mode==MODE_SIMULACIO)
                    {
                        log +="\tSIMULACIO: Cal esborrar AG per acumulació de 5 lleus amb id="+incGreusAcumul.get(k).getId() +"\n";                        
                    }
                    else
                    {
                         Incidencias inc2 = new Incidencias(incGreusAcumul.get(k));
                         inc2.delete();
                         log +="  delete AG amb id="+inc2.getId()+"\n";
                    }
                    
                }
            }
        }
    
        return (numInconsistencies-numInconsistencies0);
}

//     
    public void convert(int mode)
    {

        //Deixa constància que ha començat la tasca
        log = "";
        int jobid=-1;
        if(mysql!=null && mode==MODE_ACTUALITZA)
        {
            String SQL1 = "INSERT INTO sig_log (usua, tasca, inici, resultat) "
                    + " VALUES('"+invoker+"','LLEU2HIST', NOW(),'') ";
            jobid = mysql.executeUpdateID(SQL1);
        }

        
        List<BeanLleu2Hist> alumnes = getAlumnes();
        for(int i=0; i<alumnes.size(); i++)
        {
            //Per cada alumne cal comprovar si esta net d'inconsistències
            //Si no ens negam a fer cap canvi
            int ni = doCheckInconsistencies(alumnes.get(i).getAlumno(), MODE_SIMULACIO);
            log +="\n*Exp. "+alumnes.get(i).getAlumno().getExpediente()+": ";
            if(ni>0)
            {
                log += "[CHECK NEEDED];";
                continue;
            }
            
            int namon = alumnes.get(i).getListInc().size();
            for(int j=0; j<(int)(namon/5.); j++) //iteracio sobre n. de greus
            {
               
                int success = 0;
                for(int k=0; k<5; k++)
                {
                    int pos = j*5 + k;
                    Incidencias inc = new Incidencias(alumnes.get(i).getListInc().get(k));
                    inc.findCodigoIncidencia("ALH", "faltas"); //La canvia a historica
                    int nup = 0;
                    if(mode==MODE_ACTUALITZA)
                    {
                       nup = inc.save();
                       success += nup;
                       if(nup>0) {
                            log += "c"+inc.getId()+",";
                        }
                    }
                    else if(mode==MODE_SIMULACIO)
                    {
                        nup = 1;
                        if(nup>0) {
                            log += "\tSIMULACIO::Convertida AL--> ALH amb id="+inc.getId()+",\n";
                        }
                        success += nup;
                    }
                         
                    
                }
                
              
                
                if(success==5) 
                {
                    BeanAlumno ba = alumnes.get(i).getAlumno();
                                        
                    Incidencias inc = createAGacumul(ba);
                    
                    if(mode==MODE_ACTUALITZA)
                    {
                        int addGreu = inc.save();
                        if(addGreu>0) {
                            log +=" n"+inc.getId()+";";
                        }
                        else {
                            log += "Error adding AG. ";
                        }
                    }
                    else if(mode==MODE_SIMULACIO)
                    {
                        log += "\tSIMULACIO::S'ha d'introduir AG per acumulació \n";
                    }
                } 
                    
                  
            }
        }
        
        //Deixa constància que ha acaba la tasca
        //System.out.println("El log seria:: "+log);
        if(mysql!=null && jobid>0 &&  mode==MODE_ACTUALITZA)
        {
            String SQL1 = "UPDATE sig_log SET fi=NOW(), resultat=? WHERE id="+jobid;
            Object[] obj = new Object[]{log};
            int nup = mysql.preparedUpdate(SQL1, obj);
        }
    }

    private Incidencias createAGacumul(BeanAlumno ba) {
        
        //Carrega informacio de tutoria    
        Alumnos alumn = new Alumnos(ba);
        alumn.getTutoriaInfo();
        ba = alumn.getBean();       
        
        Incidencias inc = new Incidencias();
        inc.setIdAlumnos(ba.getId());
        inc.setIdGrupAsig(ba.getIdGrupoAsig());
        inc.setId(-1); //molt important
        //Pot ser que no trobi l'observacio AG acumulacio de 5 lleus, 
        //ara ho feim amb una id que s'estableix en la taula de configuracio
        //inc.findNombreObservaciones("Acumulació de 5 Amon", "faltas");
        inc.setIdTipoObservaciones(ID_OBSERVACION_AGACUMULACIO);
        inc.setComentarios("Acumulació de 5 Amonestacions lleus");
        java.util.Date avui = new java.util.Date();
        inc.setDia(new java.sql.Date(avui.getTime()));
        inc.setHora(new DataCtrl().getHora());

        //Introduim les amonestacions com a convivència, per
        //evitar que no es puguin editar des de SGD
//        if (ba.getIdTutor() > 0) {
//            //Inclou a l'hora de tutoria
//            inc.findCodigoIncidencia("AG", "faltas");
            inc.setIdProfesores(ba.getIdTutor() + "");
//            inc.setIdHorasCentro(ba.getIdHoraTutoria());
//            inc.setIdGrupAsig(ba.getIdGrupAsigTutoria());
//        } else {
            //Inclou a l'hora actual com a convivencia

            inc.findCodigoIncidencia("AG", "faltas");
            inc.setDia(new java.sql.Date(avui.getTime()));
//        }
        return inc;
    }

    
    /**
     * @return the log
     */
    public String getLog() {
        return log;
    }

    public boolean isSucess() {
        return success;
    }
  


}



  //Aqui ha de comprovar la consistència, és a dir
                    //Comprovar si hi ha errades quan es feina manualment
                    //el nombre d'amon greus amb el idMotiu Acumulacio ha
                    //d'esser igual a (n. incidencies tipo ALH)/5
                    
                    
//                    int nAG = coreCfg.getSgdClient().getIncidenciasCollection().getTotalIncidenciasOf(ba.getId(), 
//                            new String[]{"AG"}, ID_OBSERVACION_AGACUMULACIO, false);
//                            
//                    int nALH = coreCfg.getSgdClient().getIncidenciasCollection().getTotalIncidenciasOf(ba.getId(), 
//                            new String[]{"ALH"}, -1, false);
//                    
//                    //Tipus d'inconsistencia 0; no hi pot haver amonestacions lleus
//                    //amb el motiu "Acumulacio de 5 lleus"
//                    int nErrors = coreCfg.getSgdClient().getIncidenciasCollection().getTotalIncidenciasOf(ba.getId(), 
//                             new String[]{"AL"}, ID_OBSERVACION_AGACUMULACIO, false);
//                    
//                    if(nErrors!=0)
//                    {
//                        log +="?? Inconsistencia: Té "+nErrors+" AL amb motiu `acumulacio´\n";
//                        
//                        for(int k=0; k<nErrors; k++)
//                        {
//                           ArrayList<BeanIncidencias> wrongInc = 
//                                coreCfg.getSgdClient().getIncidenciasCollection().getIncidenciasOf(ba.getId(), new String[]{"AL"}, 
//                                                    ID_OBSERVACION_AGACUMULACIO, true, -1, "ASC");
//                           
//                            Incidencias inc2 = new Incidencias(wrongInc.get(k));
//                            int delete = inc2.delete();
//                            if(delete>0) 
//                                log +="   delete AL amb id="+inc2.getId()+"\n";
//                       
//                        }
//                    }
//                    
//                    //Primer tipus d'inconsistencia es que nALH no sigui multiple de 5
//                    if(nALH % 5 !=0)
//                    {
//                        log +="?? Inconsistencia: Té núm ALH="+nALH+" que no és múltiple de 5\n";
//                        log +="   Arregleu-lo manualment\n";
//                    }
//                    
//                    int excess = (int) (nAG-nALH/5.);
//                  
//                    if(excess!=0)
//                    {
//                        log +="?? Inconsistencia: Té núm ALH="+nALH+" i núm AG(per acumulació)="+nAG+"\n";
//                       
//                        
//                        if(excess<0)
//                        {
//                            //Li he de posar més amonestacions greus
//                            for(int k=0; k<(int) Math.abs(excess); k++)
//                            {
//                                 inc.setId(-1);
//                                 int addGreu2 = inc.save();
//                                 if(addGreu2<1) log += "  Error adding AG. "; 
//                                 log +="   add AG id="+inc.getId()+"\n";
//                            }
//                            
//                        }
//                        else
//                        {
//                            //Li he de llevar amonestacions greus
//                             ArrayList<BeanIncidencias> incGreusAcumul = 
//                                coreCfg.getSgdClient().getIncidenciasCollection().getIncidenciasOf(ba.getId(), new String[]{"AG"}, 
//                                                    ID_OBSERVACION_AGACUMULACIO, true, -1, "DESC");
//                             
//                             for(int k=0; k<excess; k++)
//                             {
//                                 Incidencias inc2 = new Incidencias(incGreusAcumul.get(k));
//                                 inc2.delete();
//                                 log +="  delete AG amb id="+inc2.getId()+"\n";
//                             }
//                            
//                        }
//                    }
//                    
//                }
//                else
//                {
//                    log += "Error nhist<5.\n";
//                }