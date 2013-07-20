/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.util;

/**
 *
 * @author Josep
 */
public class LongText {

    public LongText(String txt)
    {
        text = txt;
    }

    public LongText()
    {
        text = "";
    }

    public String getText()
    {
        return text;
    }

    public static String RemoveNewLines(String txt)
    {
        if(txt != null) {
            txt = txt.replaceAll("\n", "\\\n");
        }
        else {
            txt = "";
        }
        
        return txt;
    }

    public String RemoveNewLines()
    {
        String txt = text.replaceAll("\n", "\\\n");
        return txt;
    }

    private String text;
}
