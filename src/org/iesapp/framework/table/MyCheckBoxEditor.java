/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.table;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;

/**
 *
 * @author Josep
 */
public class MyCheckBoxEditor extends DefaultCellEditor {

    public MyCheckBoxEditor( ) {
       super(new JCheckBox());
    }

public Object getCellEditorValue() {
    JCheckBox mbox = (JCheckBox) this.getComponent();
    int val = 0;
    if (mbox.isSelected()) {
        val = 1;
    }
    return val;

}

}
