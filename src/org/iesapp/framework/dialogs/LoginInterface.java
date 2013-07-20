/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.dialogs;

import org.iesapp.clients.sgd7.profesores.BeanProfesor;

/**
 *
 * @author Josep
 */
public interface LoginInterface {
    
    public String getAbrev();
    public String getPwd();
    //1=OK, 0=Not validated, -1=exit (max. number tries attempted)
    public int getValidation();
    public BeanProfesor getProfesor();
    public void display();
    public String getRole();
    public void dispose();
}
