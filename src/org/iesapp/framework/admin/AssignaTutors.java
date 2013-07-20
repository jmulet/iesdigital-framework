/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iesapp.clients.iesdigital.alumnat.Grupo;
import org.iesapp.clients.iesdigital.alumnat.Historic;
import org.iesapp.clients.iesdigital.professorat.BeanProfessor;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.HtmlLog;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class AssignaTutors {

    private String anyAcademic;
    private final HtmlLog htmlog;
    private final CoreCfg coreCfg;

    public AssignaTutors(String anyAcademic, HtmlLog htmlLog, CoreCfg coreCfg) {
        this.coreCfg = coreCfg;
        this.anyAcademic = anyAcademic;
        this.htmlog = htmlLog;
        htmlog.append("<div id='at0'></div>", "body");
    }

    public HashMap<Integer, Historic> getHistoricMap() {
        HashMap<Integer, Historic> list = new HashMap<Integer, Historic>();

        int nerr = 0;

        String SQL1 = "SELECT DISTINCT aa.idAlumnos, alumn.expediente, alumn.nombre, "
                + "g.grupo, a.asignatura, p.codigo AS profesor,p.nombre AS NombreProfe "
                + " , CONCAT(CONCAT(hc.inicio, '-'), hc.fin) AS hora, h.idDias AS dia, "
                + " hc.inicio, au.codigo AS aula "
                + " FROM `" + CoreCfg.coreDB_sgdDB + "`.Asignaturas a "
                + " INNER JOIN `" + CoreCfg.coreDB_sgdDB + "`.ClasesDetalle cd ON 1=1 "
                + " INNER JOIN `" + CoreCfg.coreDB_sgdDB + "`.HorasCentro hc ON 1=1 "
                + " INNER JOIN `" + CoreCfg.coreDB_sgdDB + "`.Horarios h ON 1=1 "
                + " INNER JOIN `" + CoreCfg.coreDB_sgdDB + "`.GrupAsig ga ON 1=1 "
                + " INNER JOIN `" + CoreCfg.coreDB_sgdDB + "`.Grupos g ON 1=1 "
                + " INNER JOIN `" + CoreCfg.coreDB_sgdDB + "`.AsignaturasAlumno aa ON 1=1 "
                + " LEFT OUTER JOIN `" + CoreCfg.coreDB_sgdDB + "`.alumnos alumn ON alumn.id=aa.idAlumnos "
                + " LEFT OUTER JOIN `" + CoreCfg.coreDB_sgdDB + "`.Aulas au ON h.idAulas=au.id "
                + " LEFT OUTER JOIN `" + CoreCfg.coreDB_sgdDB + "`.Profesores p ON h.idProfesores=p.id "
                + " WHERE a.asignatura LIKE 'Tut%' "
                + " AND  aa.idGrupAsig=ga.id "
                + " AND  (aa.opcion<>'0' AND (cd.opcion='X' OR cd.opcion=aa.opcion)) "
                + " AND  h.idClases=cd.idClases "
                + " AND cd.idGrupAsig=ga.id "
                + " AND  h.idHorasCentro=hc.id "
                + " AND ga.idGrupos=g.id "
                + " AND  ga.idAsignaturas=a.id"
                + " ORDER BY g.grupo, alumn.nombre";


        String proftutor = "";

        htmlog.beginTable("table1", new String[]{"Expd.", "Curs", "Ensenyament", "Estudis", "Grup", "Tutor/a"}, "at0");
        int rows = 0;

        try {
            Statement st = coreCfg.getSgd().createStatement();
            ResultSet rs1 = coreCfg.getSgd().getResultSet(SQL1, st);
            while (rs1 != null && rs1.next()) {

                String sexp = rs1.getString("expediente");
                int exp2 = (int) Double.parseDouble(sexp);

                String nomAlumne = rs1.getString("nombre");

                String gruporaw = rs1.getString("grupo").toUpperCase();
                Grupo g1 = new Grupo(gruporaw, coreCfg.getIesClient());
                String ensenyament = g1.getEnsenyament();
                String estudis = g1.getEstudis();
                String grup = g1.getGrup();

                if (grup.isEmpty() || estudis.isEmpty()) {
                    htmlog.addRowTable(new String[]{"##ERROR! AQUI HI HA UN PROBLEMA :: " + gruporaw}, "table1");
                    nerr += 1;
                }


                proftutor = rs1.getString("NombreProfe");
                int codigprofesor = rs1.getInt("profesor"); //aquesta és la ID - sgd


                Historic h = null;
                if (list.containsKey(exp2)) {
                    h = list.get(exp2);

                    if (!h.profTutor.contains(proftutor)) {
                        h.profTutor.add(proftutor);
                        h.codigoTutor.add(codigprofesor);
                    }
                } else {
                    h = new Historic();
                    h.exp2 = exp2;
                    h.nomAlumne = nomAlumne;
                    h.anyAcademic = this.anyAcademic + "";
                    h.ensenyament = ensenyament;
                    h.estudis = estudis;
                    h.grup = grup;
                    h.profTutor.add(proftutor);
                    h.codigoTutor.add(codigprofesor);
                }

                htmlog.addRowTable(new Object[]{h.exp2, h.anyAcademic, h.ensenyament, h.estudis, h.grup, h.profTutor}, "table1");
                rows += 1;

                if (!h.anyAcademic.isEmpty() && !h.grup.isEmpty()) {
                    list.put(exp2, h);
                } else {
                    htmlog.addRowTable(new String[]{"##ERROR! AQUI HI HA UN PROBLEMA :: " + gruporaw}, "table1");
                    nerr += 1;
                }
            }
            if (rs1 != null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AssignaTutors.class.getName()).log(Level.SEVERE, null, ex);
        }



        return list;
    }

    public HashMap<Integer, String> getMapaIdAbrev() {
        HashMap mapa = new HashMap<Integer, String>();
        String SQL1 = "Select abrev, idSGD from sig_professorat";

        try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1, st);
            while (rs1 != null && rs1.next()) {
                mapa.put(rs1.getInt("idSGD"), rs1.getString("abrev"));
            }
            if (rs1 != null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AssignaTutors.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapa;
    }

    // Aquells que no hagi trobat pel metode 1, intenta trobar-los per aquesta altra query
    // sobretot per grups tarda que no tenen asignatura "tuta":
    public HashMap<Integer, Historic> getHistoricMap_refine(HashMap<Integer, Historic> mapa) {
        htmlog.append("REFINANT LA DETECCIÓ DELS TUTORS...", "at0", HtmlLog.TITLETYPE);
        htmlog.beginTable("table2", new String[]{"Expd.", "Curs", "Ensenyament", "Estudis", "Grup", "Tutor/a"}, "at0");
        int rows = 0;

        String SQL1 = " SELECT "
                + " alumnos.expediente "
                + " , alumnos.nombre "
                + " , profesores.nombre as profe"
                + " , profesores.codigo "
                + " , grupos.grupo "
                + " FROM "
                + " `" + CoreCfg.coreDB_sgdDB + "`.gruposalumno "
                + " INNER JOIN `" + CoreCfg.coreDB_sgdDB + "`.alumnos  "
                + "     ON (gruposalumno.idAlumnos = alumnos.id) "
                + " INNER JOIN `" + CoreCfg.coreDB_sgdDB + "`.grupos  "
                + "     ON (gruposalumno.grupoGestion = grupos.grupoGestion) "
                + " INNER JOIN `" + CoreCfg.coreDB_sgdDB + "`.profesores  "
                + "    ON (grupos.idProfesores = profesores.id) ";
        try {
            Statement st = coreCfg.getSgd().createStatement();
            ResultSet rs1 = coreCfg.getSgd().getResultSet(SQL1, st);
            while (rs1 != null && rs1.next()) {
                int expd = rs1.getInt("expediente");
                int codig = rs1.getInt("codigo");
                String profe = rs1.getString("profe");
                String grupo = rs1.getString("grupo");
                Grupo grp = new Grupo(grupo, coreCfg.getIesClient());

                //Si el mapa no conte, afegeix l'alumne
                if (!mapa.containsKey(expd)) {
                    Historic h = new Historic();
                    h.exp2 = expd;
                    h.codigoTutor.add(codig);
                    h.profTutor.add(profe);
                    h.ensenyament = grp.getEnsenyament();
                    h.estudis = grp.getEstudis();
                    h.grup = grp.getGrup();
                    h.nomAlumne = rs1.getString("nombre");
                    h.anyAcademic = "" + this.anyAcademic;
                    mapa.put(expd, h);
                    htmlog.addRowTable(new Object[]{h.exp2, h.anyAcademic, h.ensenyament, h.estudis, h.grup, h.profTutor}, "table2");
                    rows += 1;

                }
            }
            if (rs1 != null) {
                rs1.close();
                st.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AssignaTutors.class.getName()).log(Level.SEVERE, null, ex);
        }



        return mapa;
    }

    //
    // Assegura que tots els alumnes tenen assignat una condicio de pertanença a
    // un professor tutor i que aquest professor tutor té el role de TUTOR
    //
    public int assignaTutors(int mode) {

        htmlog.append("<div id='at1'></div>", "body");
        htmlog.append("DETECTANT ELS TUTORS DELS ALUMNES...", "at1", HtmlLog.TITLETYPE);

        //Obté el mapa historic amb tota la informacio de l'sgd
        HashMap<Integer, Historic> historicMap = getHistoricMap();
        //Refina el mapa (per professors que no tenen assignada cap assignatura tuta)
        historicMap = getHistoricMap_refine(historicMap);
        //Obte el link entre id-sgd i abrev
        HashMap<Integer, String> mapa = getMapaIdAbrev();
        HashMap<String, BeanProfessor> abrev2prof = coreCfg.getIesClient().getProfessoratData().getMapProf();

        htmlog.append("ASSIGNANT TUTORS (NOMES SI NO TENEN NINGU ASSIGNAT)...", "at1", HtmlLog.TITLETYPE);
        htmlog.beginTable("table3", new String[]{"Expd.", "Nom alumne/a", "Tutor/a", "Abrev.", "Arreglat"}, "at1");
        int nrows = 0;

        //Ara agafa tots els alumnes i comprova si se li ha assignat el mateix tutor?
        String SQL1 = "SELECT * FROM `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne as xes inner join `"
                + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne_historic as xh "
                + " on xes.exp2=xh.exp2 and xh.anyAcademic='" + this.anyAcademic + "'";

        try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1, st);
            while (rs1 != null && rs1.next()) {
                int exp2 = rs1.getInt("Exp2");
                String perm = rs1.getString("Permisos");
                if (perm.contains(";")) {
                    perm = StringUtils.BeforeFirst(perm, ";");
                }
                perm = perm.trim(); //aquesta és l'abrev


                //No té abrev assignada i intenta arreglar-ho
                if (perm.isEmpty() && historicMap.containsKey(exp2)) {

                    int idSGD = -1;
                    if (!historicMap.get(exp2).codigoTutor.isEmpty()) {
                        idSGD = historicMap.get(exp2).codigoTutor.get(0);
                    }

                    if (mapa.containsKey(idSGD)) {
                        String abrev = mapa.get(idSGD);
                        String profe = historicMap.get(exp2).profTutor.get(0);
                        String nomAlumne = historicMap.get(exp2).nomAlumne;


                        //Si estic fora mode simulacio, arregla el problema
                        boolean fixed = false;
                        if (mode != 0) {

                            SQL1 = "UPDATE `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne SET Permisos='" + abrev + "' WHERE exp2='" + exp2 + "'";
                            int nup1 = coreCfg.getMysql().executeUpdate(SQL1);

                            SQL1 = "UPDATE `" + CoreCfg.core_mysqlDBPrefix + "`.xes_alumne_historic SET ProfTutor='"
                                    + StringUtils.noNull(abrev2prof.get(abrev).getNombre()) + "' WHERE exp2='" + exp2 + "' AND"
                                    + " anyAcademic='" + this.anyAcademic + "'";
                            int nup2 = coreCfg.getMysql().executeUpdate(SQL1);

                            SQL1 = "UPDATE `" + CoreCfg.core_mysqlDBPrefix + "`.fitxa_alumne_curs SET Professor='"
                                    + StringUtils.noNull(abrev2prof.get(abrev).getNombre()) + "' WHERE Exp_FK_ID='" + exp2 + "' AND"
                                    + " IdCurs_FK_ID='" + this.anyAcademic + "'";
                            int nup3 = coreCfg.getMysql().executeUpdate(SQL1);

                            if (nup1 > 0 && nup2 > 0 && nup3 > 0) {
                                fixed = true;
                            }

                        }
                        htmlog.addRowTable(new Object[]{exp2, nomAlumne, profe, abrev, fixed}, "table3");
                        nrows += 1;
                    }


                }
//                else if(historicMap.containsKey(exp2))
//                {
//                    if(!historicMap.get(exp2).codigoTutor.contains(perm))
//                    {
//                        Sync_sgdGUI.htmlog1.append("Modified "+exp2+":detectat professor "+perm+"<br>");
//                    }
//                }

            }

            if (rs1 != null) {
                rs1.close();
                st.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(AssignaTutors.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nrows;

    }

    public void checkProfesRole(int mode) {
        htmlog.append("<div id='at2'></div>", "body");
        //Obté el mapa historic amb tota la informacio de l'sgd
        HashMap<Integer, Historic> historicMap = getHistoricMap();
        //Refina el mapa (per professors que no tenen assignada cap assignatura tuta)
        historicMap = getHistoricMap_refine(historicMap);
        //Obte el link entre id-sgd i abrev
        HashMap<Integer, String> mapa = getMapaIdAbrev();
        //LinkedHashMap<String, BeanProfessor> abrev2prof = coreCfg.getIesClient().getProfessoratData().getMapProf();

        //Comprova que tots els professors que s'han trobat tenen el role de TUTOR
        ArrayList<Integer> tutors = new ArrayList<Integer>();
        for (int exp2 : historicMap.keySet()) {
            if (!historicMap.get(exp2).codigoTutor.isEmpty()) {
                int id = historicMap.get(exp2).codigoTutor.get(0);
                if (!tutors.contains(id)) {
                    tutors.add(id);
                }
            }
        }

        htmlog.append("S'han detectat " + tutors.size() + " tutors...<br><br>\tCOMPROVANT ROLES:", "at2");

        for (int i = 0; i < tutors.size(); i++) {
            String SQL1 = "SELECT prof.abrev,prof.nombre,prof.idSGD,usu.GrupFitxes FROM usu_usuari AS usu "
                        + " INNER JOIN sig_professorat AS prof ON usu.Nom=prof.abrev where idSGD='" + tutors.get(i) + "'";
            try { 
                Statement st = coreCfg.getMysql().createStatement();
                ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1, st);
                if (rs1.next()) {
                    String role = rs1.getString("GrupFitxes");
                    if (role.equals("NOTUTOR")) {
                        String abrev = rs1.getString("abrev");
                        htmlog.append("!!!!: El professor " + rs1.getString("nombre") + " ("
                                + abrev + ") " + rs1.getInt("idSGD") + " "
                                + "  no té role de tutor<br>", "at2", HtmlLog.ERRORTYPE);

                        if (mode != 0) {
                            SQL1 = "UPDATE usu_usuari SET GrupFitxes='TUTOR' WHERE Nom='" + abrev + "'";
                            int nup = coreCfg.getMysql().executeUpdate(SQL1);
                            if (nup > 0) {
                                htmlog.append("FIXED.", "at2", HtmlLog.SUMMARYTYPE);
                            }
                        }
                    }
                }
                if (rs1 != null) {
                    rs1.close();
                    st.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AssignaTutors.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        tutors.clear();
    }
}
