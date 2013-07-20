/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.awt.Color;
import java.util.Date;
import org.iesapp.clients.iesdigital.dates.DatesControl;
import org.iesapp.util.DataCtrl;

/**
 *
 * @author Josep
 */
public class FestiusDateEvaluator implements com.toedter.calendar.IDateEvaluator {
    private final Date minSelectableDate;
    private final Date maxSelectableDate;
    private final String allowDies;
    private final CoreCfg coreCfg;

    public FestiusDateEvaluator(java.util.Date min, java.util.Date max, CoreCfg coreCfg) {
        this.coreCfg = coreCfg;
        this.minSelectableDate = min;
        this.maxSelectableDate = max;
        allowDies = "1,2,3,4,5"; //tots estan permesos
    }

    public FestiusDateEvaluator(java.util.Date min, java.util.Date max, String allowDies, CoreCfg coreCfg) {
        this.coreCfg = coreCfg;
        this.minSelectableDate = min;
        this.maxSelectableDate = max;
        this.allowDies = allowDies;
    }

    @Override
    public boolean isSpecial(Date date) {
        return new DatesControl(date, coreCfg.getIesClient()).esFestiu();
    }

    @Override
    public Color getSpecialForegroundColor() {
        return Color.WHITE;
    }

    @Override
    public Color getSpecialBackroundColor() {
        return Color.RED;
    }

    public String getSpecialTooltip() {
        return "Festiu";
    }

    public boolean isInvalid(Date date) {
        int intdia = new DataCtrl(date).getIntDia();
        return (intdia>5 || !allowDies.contains(""+intdia) || (minSelectableDate!=null 
                && date.before(minSelectableDate)) || (maxSelectableDate!=null 
                && date.after(maxSelectableDate)));
    }

    public Color getInvalidForegroundColor() {
        return Color.WHITE;
    }

    public Color getInvalidBackroundColor() {
        return Color.RED;
    }

    public String getInvalidTooltip() {
        return "No es pot seleccionar";
    }
    
}
