/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.actuacions;

 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.iesapp.clients.iesdigital.actuacions.BeanAction;
import org.iesapp.clients.sgd7.incidencias.BeanIncidenciasDia;
import org.iesapp.database.MyDatabase;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.PeriodParser;

/**
 *
 * @author Josep
 * 
 * Comunicacio entre les incidencies de l'sgd i les accions
 * del programa de fitxes.
 * En aquesta versio, permet multiples incidencies
 * 
 */
public class IncidenciesSGD1 {  
    private int idActuacio;
    private final int idTipoIncidencias;
    private final BeanAction actionbean;
    private final int exp2;
    private final int idAlumnos;
    private String msg;
    private final CoreCfg coreCfg;

    /**
     * Utilitzar aquest constructor per generar incidencies
     * @param idActuacio
     * @param exp2
     * @param actionbean 
     */
    public IncidenciesSGD1(int idActuacio, int exp2, BeanAction actionbean, CoreCfg coreCfg)
    {
        this.coreCfg = coreCfg;
       //Determina si aquest cas té incidencies registrades
        this.idActuacio = idActuacio;
        idTipoIncidencias = getIdsIncidencies(actionbean.getSimboloIncidencia());
        this.actionbean = actionbean;
        this.exp2 = exp2;
        this.idAlumnos = org.iesapp.clients.sgd7.alumnos.Alumnos.getID(exp2, coreCfg.getSgd());
        msg = "Creades incidencies "+actionbean.getSimboloIncidencia()+" a SGD:\n";
    }
    
    /**
     * Aquest constructor nomes serveix per destruir el registre
     * @param id
     * @param expedient 
     */
//     public IncidenciesSGD1(int idActuacio, int exp2) {
//        this.idActuacio = idActuacio;
//        this.exp2 = exp2;
//        this.idAlumnos = org.iesapp.clients.sgd7.alumnos.Alumnos.getID(exp2);
//        idTipoIncidencias = 0;
//        actionbean = null;
//    }
    
  

    public static void clearAll(int idActuacio, MyDatabase mysql)
    {
        String SQL1 = "SELECT idSGD FROM tuta_incidenciessgd WHERE idCas="+idActuacio;
         try {
            Statement st = mysql.createStatement();
            ResultSet rs1 = mysql.getResultSet(SQL1,st); 
            while(rs1!=null && rs1.next())
            {
                int idsgd = rs1.getInt("idSgd");
                new org.iesapp.clients.sgd7.incidencias.Incidencias(idsgd).delete();                
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String SQL2 = "DELETE FROM tuta_incidenciessgd WHERE idCas="+idActuacio;          
        int nup = mysql.executeUpdate(SQL2);
    }

 
    
    
    private boolean addIdSgd(int idSgd)
    {        
        String SQL1 = "INSERT INTO tuta_incidenciessgd (idCas, idSgd) VALUES("+this.idActuacio+", "+idSgd+")";          
        int nup = coreCfg.getMysql().executeUpdate(SQL1);      
        return (nup>0);
    }
    
    //metode public per efectar la creacio de les incidencies en el beanAccio
    public void create()
    {
        if(coreCfg.getSgdClient().getUser()==null ||
           coreCfg.getSgdClient().getUser().getIdProfesor().equals("-1"))
        {
            
            JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), "No teniu associat un id d'usuari SGD.\nNo podeu desar incidències en el sistema.");
            return;
        }
        
        //Acording to this bean nothing has to be rendered
        if(!actionbean.isRegisterSGD() || idTipoIncidencias<=0 || coreCfg.getSgdClient().getUser().getIdProfesor().isEmpty())
        {
            return;
        }
        //Analitza el actionbean per saber el tipus d'incidencia i els dies
        PeriodParser parser = new PeriodParser(actionbean.getActualDates(), coreCfg);
        
       
        
        for(int i=0; i<parser.getListIniDate().size(); i++)
        {
             Calendar cal1 = Calendar.getInstance();
             cal1.setTime(parser.getListIniDate().get(i));
             Calendar cal2 = Calendar.getInstance();
             cal2.setTime(parser.getListEndDate().get(i));
             
             //Posa incidencies tots els dies
             //Caldria un missatge si són incidencies de mes d'un dia
             
             while(cal1.compareTo(cal2)<=0)
             {
                if(actionbean.isTodoElDia())  //Marca la incidencia a totes les hores del dia
                {
                     //Obté una llista en "blanc" de les classes que té l'alumne aquest dia
                     ArrayList<BeanIncidenciasDia> list = coreCfg.getSgdClient().getIncidenciasCollection().getIncidenciasInDay(idAlumnos, new String[]{}, cal1.getTime());  
                     for(BeanIncidenciasDia bic: list)
                     {
                         //System.out.println("idhora="+bic.getHora()+"hora="+bic.getHoraCentro()+" assig="+bic.getAsignatura());
                         bic.setId(0); //per forçar l'escriptura
                         bic.setIdTipoIncidencias(idTipoIncidencias);                     
                         bic.setComentarios(actionbean.getObservaciones());
                         //La incidencia l'anota l'usuari que esta conectat en aquests moments
                         bic.setIdProfesores(coreCfg.getSgdClient().getUser().getIdProfesor());
                         org.iesapp.clients.sgd7.incidencias.Incidencias inc = new org.iesapp.clients.sgd7.incidencias.Incidencias(bic);
                         int nup = inc.save();
                         //Generated id
                         if(nup>0)
                         {
                            msg += inc.getId()+", ";
                            this.addIdSgd(inc.getId());
                         }
                     }
                }
                else  //marca la incidencia com a convivencia
                {
                    //Quantes incidencies s'han de posar per sessio
                    for(int j=0; j<actionbean.getIncidenciasSesion(); j++)
                    {
                         org.iesapp.clients.sgd7.incidencias.Incidencias inc = new org.iesapp.clients.sgd7.incidencias.Incidencias();
                         inc.setId(0);
                         inc.setIdAlumnos(idAlumnos);
                         inc.setIdProfesores(coreCfg.getSgdClient().getUser().getIdProfesor());
                         inc.setDia(new java.sql.Date(cal1.getTime().getTime()));
                         inc.setIdTipoIncidencias(idTipoIncidencias);
                         inc.setComentarios(actionbean.getObservaciones());
                         inc.setIdGrupAsig(0);
                         inc.setIdHorasCentro(0);
                         int nup = inc.save();
                         //Generated id
                         if(nup>0)
                         {
                            msg += inc.getId()+", ";
                            this.addIdSgd(inc.getId());
                         }
                    }
                }
                cal1.add(Calendar.DAY_OF_MONTH, 1);
             }
        }
        
    }
    
//    //Retornara la id de la incidencia creada
//    private int createIncSgd(java.util.Date dia, String codigo, String motiu)
//    {
//        
//        int idsgd_new;
//
//        //Necessitam saber quin es el seu tutor i la idAsig per poder crear
//        //una nova incidencia en la base sgd
//        cercaTutor(exp2);
//        int useidInc = getIdsIncidencies(codigo);
//        
//        //Cal crear la incidencia
//        org.iesapp.clients.sgd7.incidencias.Incidencias inc = new org.iesapp.clients.sgd7.incidencias.Incidencias();
//        inc.setIdAlumnos(idAlumnos);
//        inc.setIdProfesores(idProfe+"");
//        inc.setIdTipoIncidencias(useidInc);
//        inc.setIdHorasCentro(codighora);
//        inc.setIdGrupAsig(idAsig);
//        inc.setIdTipoObservaciones(0);
//        inc.setDia(new java.sql.Date(dia.getTime()));
//        inc.setHora(StringUtils.formatTime(SgdBase.getSgd().getServerTime()));
//        inc.setComentarios(motiu);
//        idsgd_new = inc.save();
//
//        if (idsgd_new >= 0) {
//            addIdSgd(idsgd_new);
//        }
//
//        m_idsgd_new = idsgd_new;
//        return idsgd_new;
//    }
//    
    
      public String getMessage() {        
          return msg;
       }
        

//    private void cercaTutor(int expd) {
//        String SQL1 = "SELECT DISTINCT aa.idAlumnos, alumn.expediente, alumn.nombre, aa.idGrupAsig, "+
//               " g.grupo, a.asignatura, p.codigo AS profesor,p.nombre AS NombreProfe  "+
//                 " , CONCAT(CONCAT(hc.inicio, '-'), hc.fin) AS hora, hc.id as codighora, h.idDias AS dia,  "+
//                 " hc.inicio, au.codigo AS aula  "+
//                 " FROM Asignaturas a  "+
//                 " INNER JOIN ClasesDetalle cd ON 1=1  "+
//                 " INNER JOIN HorasCentro hc ON 1=1  "+
//                 " INNER JOIN Horarios h ON 1=1  "+
//                 " INNER JOIN GrupAsig ga ON 1=1  "+
//                 " INNER JOIN Grupos g ON 1=1  "+
//                 " INNER JOIN AsignaturasAlumno aa ON 1=1  "+
//                 " LEFT OUTER JOIN alumnos alumn ON alumn.id=aa.idAlumnos  "+
//                 " LEFT OUTER JOIN Aulas au ON h.idAulas=au.id  "+
//                 " LEFT OUTER JOIN Profesores p ON h.idProfesores=p.id  "+
//               "  WHERE a.asignatura LIKE 'Tut%'  "+
//                 " AND  aa.idGrupAsig=ga.id  "+
//                 " AND  (aa.opcion<>'0' AND (cd.opcion='X' OR cd.opcion=aa.opcion))  "+
//                 " AND  h.idClases=cd.idClases  "+
//                 " AND cd.idGrupAsig=ga.id  "+
//                 " AND  h.idHorasCentro=hc.id  "+
//                 " AND ga.idGrupos=g.id  "+
//                 " AND  ga.idAsignaturas=a.id "+
//                 " AND expediente="+expd;
//        
//         //System.out.println(SQL1);
//         ResultSet rs1 = CoreCfg.sgd.getResultSet(SQL1);
// 
//         idAlumnos = -1;
//         idProfe = -1;
//         idAsig = - 1;
//         
//          try {
//            if(rs1!=null && rs1.next())
//            {
//                idAlumnos = rs1.getInt("idAlumnos");
//                idProfe = rs1.getInt("profesor");
//                idAsig = rs1.getInt("idGrupAsig");
//                codighora = rs1.getInt("codighora");
//            }
//            if(rs1!=null) {
//                  rs1.close();
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
//        }
//          
//       //Intenta fer-ho com a convivència, si no pot trobar el professor tutor
//       if(idAlumnos==-1)
//       {
//            SQL1 = "SELECT * from alumnos where expediente='"+expd+"'";
//            rs1 = CoreCfg.sgd.getResultSet(SQL1);
//            try {
//            if(rs1!=null && rs1.next())
//            {
//                idAlumnos = rs1.getInt("id");
//            }
//            if(rs1!=null) {
//                    rs1.close();
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//             //L'usuari pot no tenir associada una IDsgd, per tant l'agafa de Cfg.abrevPref
//            String usuari = Fitxes.user;
//            if( (FitxesGUI.userInfo.getGrant()==User.ADMIN || FitxesGUI.userInfo.getGrant()==User.PREF) &&
//                 FitxesGUI.userInfo.getIdSGD()<=0)
//            {
//                usuari = Cfg.abrevPref;
//            }
//            
//            SQL1 = "SELECT * from sig_professorat where abrev='"+usuari+"'";
//            rs1 = coreCfg.getMysql().getResultSet(SQL1);
//            try {
//            if(rs1!=null && rs1.next())
//            {
//                idProfe= rs1.getInt("idSGD");
//            }
//            if(rs1!=null) rs1.close();
//            } catch (SQLException ex) {
//                Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//            codighora =0;
//            idAsig = 0;
//       }
//          
//    }

    private int getIdsIncidencies(String simbol) {
        String SQL1 = "Select id from tipoincidencias where simbolo='"+simbol+"' order by descripcion";
       
        int idinc = -1;
        
        try {
            Statement st = coreCfg.getSgd().createStatement();
            ResultSet rs1 = coreCfg.getSgd().getResultSet(SQL1,st); 
            if(rs1!=null && rs1.next())
            {
                idinc = rs1.getInt("id");
            }
            if(rs1!=null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(IncidenciesSGD1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return idinc;
    }

  
    
}
