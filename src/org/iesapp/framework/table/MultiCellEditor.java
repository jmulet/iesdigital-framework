package org.iesapp.framework.table;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JSpinnerDateEditor;
import java.awt.Component;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


public class MultiCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private HashMap<Integer, Component> comp = new HashMap<Integer, Component>();
        private int m_row=0;

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

                Component rowComp = null;
                Date date = null;
                //System.out.println(value+" "+value.getClass());
                if (value instanceof java.util.Date || value instanceof java.util.Calendar)
                {
                    JDateChooser dateChooser = new JDateChooser(null, null, null, new JSpinnerDateEditor());
                    if (value instanceof Date)
                        {
                            date = (Date) value;
                        }
                        else if (value instanceof  Calendar)
                        {
                            date = ((Calendar) value).getTime();
                        }
                        dateChooser.setDate(date);
                        java.util.Locale mlocale = new java.util.Locale( "ca", "ES", "" );
                        dateChooser.setLocale(mlocale);
                        dateChooser.setDateFormatString("dd-MM-yyyy");
                        rowComp = dateChooser;
                }
                else if (value instanceof String)
                {
                     rowComp = new javax.swing.JTextField((String) value);
                }
                else if (value instanceof Boolean)
                {
                    javax.swing.JCheckBox jcheckbox = new javax.swing.JCheckBox();
                    jcheckbox.setSelected((Boolean) value);
                    rowComp = jcheckbox;
                }
		
              
                if(rowComp == null) {
                                rowComp = new javax.swing.JTextField("");
                            }
		comp.put(row, rowComp);
                m_row = row;
                return rowComp;
	}

	public Object getCellEditorValue() {
            
                //System.out.println("agafant de la finla "+m_row);
                return comp.get(m_row);
            
//                if(comp instanceof javax.swing.JTextField)
//                {
//                    return (javax.swing.JTextField) comp;
//                }
//            
//		return new java.sql.Date(dateChooser.getDate().getTime());
	}
}