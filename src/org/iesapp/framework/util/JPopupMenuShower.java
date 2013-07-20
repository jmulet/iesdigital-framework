/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 *
 * @author Josep
 */
public class JPopupMenuShower extends MouseAdapter {

  private JPopupMenu popup;

  public JPopupMenuShower(JPopupMenu popup) {
    this.popup = popup;
  }

  private void showIfPopupTrigger(MouseEvent mouseEvent) {
    if (popup.isPopupTrigger(mouseEvent)) {
      popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent
          .getY());
    }
  }

  public void mousePressed(MouseEvent mouseEvent) {
    showIfPopupTrigger(mouseEvent);
  }

  public void mouseReleased(MouseEvent mouseEvent) {
    showIfPopupTrigger(mouseEvent);
  }
}
