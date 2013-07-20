/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.table;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 *
 * @author Josep
 */
public class MyComboBoxEditor extends DefaultCellEditor {
    public MyComboBoxEditor(String[] items) {
        super(new JComboBox(items));
    }

    //Returns the selected index
    @Override
    public Object getCellEditorValue() {
        JComboBox mbox = (JComboBox) this.getComponent();
        return mbox.getSelectedIndex();
    }
 
}