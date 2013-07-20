/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.grantsystem;

/**
 *
 * @author Josep
 */
public class Profile {
    protected int nexp;
    protected boolean nese;
    protected boolean belongs;
    protected boolean repetidor;

    public int getNexp() {
        return nexp;
    }

    public void setNexp(int nexp) {
        this.nexp = nexp;
    }

 
    public boolean isNese() {
        return nese;
    }

    public void setNese(boolean nese) {
        this.nese = nese;
    }

    public boolean isBelongs() {
        return belongs;
    }

    public void setBelongs(boolean belongs) {
        this.belongs = belongs;
    }

    public boolean isRepetidor() {
        return repetidor;
    }

    public void setRepetidor(boolean repetidor) {
        this.repetidor = repetidor;
    }
    
    @Override
    public String toString()
    {
        return "<<pertany:"+belongs+","+"nese:"+nese+",repetidor:"+repetidor+">>";
    }
}
