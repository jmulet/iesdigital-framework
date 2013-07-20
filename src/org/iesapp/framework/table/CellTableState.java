/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.table;

/**
 *
 * @author Josep
 */
public class CellTableState {

    private String text="";
    private String tooltip=null;
    private int state;
    private int code;

    public CellTableState(String string, int mcode, int status) {
       text = string;
       code = mcode;
       state = status;
    }



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

     public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String text) {
        this.tooltip = text;
    }

}
