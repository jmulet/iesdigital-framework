/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javar;

import com.toedter.calendar.IDateEvaluator;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Josep
 */
public class JRMultiMonthDayChooser extends javax.swing.JPanel implements java.io.Serializable{
    protected int nMonths = 3;
    public final static byte FORWARD = 1;
    public final static byte BACKWARD = 0;
    protected byte timeLine = BACKWARD;
    private JRDayChooser[] months;
    protected Calendar calendar;
    protected Date selectedDate;
     
    private ArrayList<IDateEvaluator> iDateEvaluators = new ArrayList<IDateEvaluator>();

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }
    
   
    public JRMultiMonthDayChooser()
    {
        this.setLayout(new FlowLayout());
        calendar = Calendar.getInstance();
        initialize();
    }

    public int getnMonths() {
        return nMonths;
    }

    public void setnMonths(int nMonths) {
        this.nMonths = nMonths;     
        initialize();
    }

    public byte getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(byte timeLine) {
        this.timeLine = timeLine;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void addDateEvaluator(IDateEvaluator iDateEvaluator)
    {
        iDateEvaluators.add(iDateEvaluator);
        for (int i = 0; i < nMonths; i++) {
                months[i].addDateEvaluator(iDateEvaluator);
        }
        initialize();
    }
    
    public void shiftMonths(int shift)
    {
        for(JRDayChooser jr: months)
        {
            jr.setDay(-1); //Lleva la selecció
            Calendar calendar1 = jr.getCalendar();
            calendar1.add(Calendar.MONTH, shift);
            jr.setCalendar(calendar1);
        }
    }
    
    public void constructDate()
    {
        Calendar tmp = null;
        for(JRDayChooser jr: months)
        {
            if (jr.getDay() > 0) {
                //Calendar.getInstance();
                tmp = jr.getCalendar();
                tmp.set(Calendar.DAY_OF_MONTH, jr.getDay());
                break;
            }
        }
        
        if(tmp!=null)
        {
            //System.out.println("Detected new date "+tmp.getTime());
            java.util.Date newDate = tmp.getTime();
            java.util.Date oldDate = selectedDate;
            selectedDate = newDate;
            this.firePropertyChange("day", oldDate, selectedDate);
           
        }
        else
        {
            //System.out.println("ERROR: tmp cal null");
        }
    }

   

    private void initialize() {
        this.removeAll();
        Calendar tmp = (Calendar) calendar.clone();
        months = new JRDayChooser[nMonths];
        if(timeLine==BACKWARD)
        {
                tmp.add(Calendar.MONTH, -nMonths+1);
        }
        
        for(int i=0; i<nMonths; i++)
        {

            months[i] = new JRDayChooser();
            months[i].setMaxDayCharacters(2);
            for(IDateEvaluator ide: iDateEvaluators)
            {
                months[i].addDateEvaluator(ide);
            }
            months[i].setDay(-1);
           
            months[i].setMonth(tmp.get(Calendar.MONTH));
            months[i].setYear(tmp.get(Calendar.YEAR));
            
            tmp.add(Calendar.MONTH, 1);
            this.add(months[i]);
            months[i].addPropertyChangeListener(new PropertyChangeListener(){

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if(evt.getPropertyName().equals("day"))
                    {
                        for(JRDayChooser m: months)
                        {
                            if(evt.getSource()!=m)
                            {
                                m.setDay(-1);
                            }
                        }
                        constructDate();
                    }
                    if(evt.getPropertyName().equals("shift"))
                    {
                        int shift = ((Number) evt.getNewValue()).intValue();
                        shiftMonths(shift);
                    }
                    
                }
            });
        }
        
        months[0].setShiftLessVisible(true);
        months[nMonths-1].setShiftMoreVisible(true);
        
      
    }

  

}
