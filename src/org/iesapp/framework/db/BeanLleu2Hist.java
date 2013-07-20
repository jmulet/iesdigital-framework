/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.db;

import java.util.ArrayList;
import org.iesapp.clients.sgd7.alumnos.BeanAlumno;
import org.iesapp.clients.sgd7.incidencias.BeanIncidencias;

/**
 *
 * @author Josep
 */
public class BeanLleu2Hist{
  
    private BeanAlumno alumno;
    private ArrayList<BeanIncidencias> listInc;
       
    public BeanLleu2Hist()
    {
        listInc = new ArrayList<BeanIncidencias>();
    }

    /**
     * @return the listInc
     */
    public ArrayList<BeanIncidencias> getListInc() {
        return listInc;
    }

    /**
     * @param listInc the listInc to set
     */
    public void setListInc(ArrayList<BeanIncidencias> listInc) {
        this.listInc = listInc;
    }

    /**
     * @return the alumno
     */
    public BeanAlumno getAlumno() {
        return alumno;
    }

    /**
     * @param alumno the alumno to set
     */
    public void setAlumno(BeanAlumno alumno) {
        this.alumno = alumno;
    }
}
