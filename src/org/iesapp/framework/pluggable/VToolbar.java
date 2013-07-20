/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import com.l2fprod.common.swing.PercentLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicLabelUI;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class VToolbar extends javax.swing.JDialog implements VPositionable {
    public static final int SOUTH = 0;
    public static final int NORTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    
    protected int vlocation = EAST;
    private final Dimension screenSize;
    private final GlassPane glass;
    private final HashMap<String,String> notifications;
    protected NotificationPanel notificationPanel;
    private String unlockpwd;

    /**
     * Creates new form VToolbar
     */
    public VToolbar(final CoreCfg coreCfg) {
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        notifications = new HashMap<String, String>();
        this.setBackground(new Color(0, 0, 0, 0));

        String userLocation = coreCfg.getUserPreferences().getProperty("framework.vtoolbar.location", "EAST");
        if(userLocation.equalsIgnoreCase("WEST"))
        {
            vlocation = WEST;
        }
        else if(userLocation.equalsIgnoreCase("NORTH"))
        {
            vlocation = NORTH;
        }
        else if(userLocation.equalsIgnoreCase("SOUTH"))
        {
            vlocation = SOUTH;
        }

        initComponents();

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        //Locate this frame
        glass = new GlassPane(this, (VPositionable) this);
        this.add(glass);






        // locateToolbar();
    }

    public void clear() {
        glass.getToolBar().removeAll();
        notifications.clear();
        notificationPanel = null;
        glass.getSystemUser().setText("---");
    }

    public void addNotificationFor(String moduleClassName, String notification) {
        if (notification != null && !notification.isEmpty()) {
            notifications.put(moduleClassName, notification);
        } else {
            notifications.remove(moduleClassName);
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (notificationPanel != null) {
            notificationPanel.setVisible(false);
        }
        super.setVisible(b);
    }

    public JButton findButtonByModuleClassName(String className) {
        JButton button = null;
        for (Component comp : glass.getToolBar().getComponents()) {
            if (comp instanceof JButton) {
                String actionCommand = ((JButton) comp).getActionCommand();
                if (actionCommand != null && actionCommand.equals(className)) {
                    button = (JButton) comp;
                    break;
                }
            }
        }
        return button;
    }

    public void setSystemUser(String text, String unlockpwd) {
        this.unlockpwd = unlockpwd;
        glass.getSystemUser().setText(text);
        glass.getSystemUser().setToolTipText(text);
    }

    public void addModuleIcon(final JButton button) {
        button.setPreferredSize(new Dimension(35, 35));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                Point bpos = button.getLocationOnScreen();
                Dimension bsize = button.getSize();
                String actionCommand = button.getActionCommand();
                if (notifications.containsKey(actionCommand)) {
                    Point location = VToolbar.this.getLocation();
                    getNotificationPanel().setText(notifications.get(actionCommand));
                    Dimension size = getNotificationPanel().getSize();
                    if (vlocation == EAST) {
                        getNotificationPanel().setLocation(location.x - size.width, bpos.y + (bsize.height - size.height) / 2);
                    } else if (vlocation == WEST) {
                        getNotificationPanel().setLocation(VToolbar.this.getWidth(), bpos.y + (bsize.height - size.height) / 2);
                    } else if (vlocation == NORTH) {
                        getNotificationPanel().setLocation(bpos.x + (bsize.width - size.width) / 2, VToolbar.this.getHeight());
                    } else if (vlocation == SOUTH) {
                        getNotificationPanel().setLocation(bpos.x + (bsize.width - size.width) / 2, location.y - size.height);
                    }

                    getNotificationPanel().setVisible(true);

                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                String actionCommand = button.getActionCommand();
                if (notifications.containsKey(actionCommand)) {
                    getNotificationPanel().setVisible(false);
                }
            }
        });

        glass.getToolBar().add(button);
        glass.getToolBar().validate();
        locateToolbar();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    private void locateToolbar() {
        if (getVlocation() == EAST) {
            glass.setOrientation(PercentLayout.VERTICAL);
            this.pack();
            Dimension size1 = glass.getToolBar().getSize();
            this.setSize(40, size1.height + 38);
            Dimension size = this.getSize();

            this.setLocation(screenSize.width - size.width, (int) (0.5 * (screenSize.height - size.getHeight())));
        } else if (getVlocation() == WEST) {
            glass.setOrientation(PercentLayout.VERTICAL);
            this.pack();
            Dimension size1 = glass.getToolBar().getSize();
            this.setSize(40, size1.height + 38);
            Dimension size = this.getSize();
            this.setLocation(0, (int) (0.5 * (screenSize.height - size.getHeight())));

        } else if (getVlocation() == SOUTH) {
            glass.setOrientation(PercentLayout.HORIZONTAL);
            this.pack();
            Dimension size1 = glass.getToolBar().getSize();
            this.setSize(size1.width + 58, 40);
            Dimension size = this.getSize();
            this.setLocation((int) (0.5 * (screenSize.width - size.getWidth())), screenSize.height - 40 - size.height);
        } else if (getVlocation() == NORTH) {
            glass.setOrientation(PercentLayout.HORIZONTAL);
            this.pack();
            Dimension size1 = glass.getToolBar().getSize();
            this.setSize(size1.width + 58, 40);
            Dimension size = this.getSize();
            this.setLocation((int) (0.5 * (screenSize.width - size.getWidth())), 0);
        }
    }

    public NotificationPanel getNotificationPanel() {
        if (notificationPanel == null) {
            notificationPanel = new NotificationPanel(this);
        }
        return notificationPanel;
    }

    @Override
    public int getVlocation() {
        return vlocation;
    }

    @Override
    public void setVlocation(int vlocation) {
        this.vlocation = vlocation;
        this.locateToolbar();
    }

    private class GlassPane extends javax.swing.JPanel {

        protected final JPanel toolBar;
        private final JDialog frame;
        protected final JToggleButton toggleButton;
        protected final JLabel systemUser;
        private final PercentLayout percentLayout;
        private Point initialClick;
        private final VPositionable vpos;

        public GlassPane(final JDialog frame, final VPositionable vpos) {
            this.frame = frame;
            this.vpos = vpos;
            this.setOpaque(true);
            this.setLayout(new BorderLayout());
            toolBar = new JPanel();
            toolBar.setOpaque(false);
            percentLayout = new PercentLayout();
            percentLayout.setOrientation(PercentLayout.VERTICAL);
            percentLayout.setGap(0);
            toolBar.setLayout(percentLayout);

            toggleButton = new JToggleButton();
            toggleButton.setFont(new Font("arial unicode", Font.PLAIN, 10));
            toggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setAlwaysOnTop(getToggleButton().isSelected());
                }
            });
            toggleButton.setSelected(true);
            toggleButton.setPreferredSize(new Dimension(20, 20));
            toggleButton.setToolTipText("Always on top");
            toggleButton.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/pinh.png")));
            toggleButton.setSelectedIcon(new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/pinv.png")));

            systemUser = new JLabel();
            systemUser.setOpaque(true);
            systemUser.setBackground(new Color(0, 0, 100));
            systemUser.setForeground(new Color(255, 255, 255));
            systemUser.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            systemUser.setFont(new Font("arial unicode", Font.BOLD, 10));


            systemUser.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    initialClick = e.getPoint();
                    getComponentAt(initialClick);
                }
            });

            systemUser.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (initialClick == null) {
                        return;
                    }
                    // get location of Window
                    int thisX = VToolbar.this.getLocation().x;
                    int thisY = VToolbar.this.getLocation().y;

                    // Determine how much the mouse moved since the initial click
                    int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                    int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

                    // Move window to this position
                    int X = thisX + xMoved;
                    int Y = thisY + yMoved;

                    //Position according to actual orientation
                    int vlocation1 = vpos.getVlocation();
                    if (vlocation1 == NORTH) {
                        if (Math.abs(yMoved) < 2.5 * frame.getHeight()) {
                            if (X >= 0 && X + frame.getWidth() <= screenSize.width) {
                                VToolbar.this.setLocation(X, thisY);
                            } else if (X <= 0) {
                                vpos.setVlocation(WEST);
                                initialClick = null;
                            } else {
                                vpos.setVlocation(EAST);
                                initialClick = null;
                            }
                        } else {
                            vpos.setVlocation(SOUTH);
                            initialClick = null;

                        }
                    } else if (vlocation1 == SOUTH) {
                        if (Math.abs(yMoved) < 2.5 * frame.getHeight()) {
                            if (X >= 0 && X + frame.getWidth() <= screenSize.width) {
                                VToolbar.this.setLocation(X, thisY);
                            } else if (X <= 0) {
                                vpos.setVlocation(WEST);
                                initialClick = null;
                            } else {
                                vpos.setVlocation(EAST);
                                initialClick = null;
                            }
                        } else {
                            vpos.setVlocation(NORTH);
                            initialClick = null;
                        }
                    } else if (vlocation1 == EAST) {
                        if (Math.abs(xMoved) < 2.5 * frame.getWidth()) {
                            if (Y >= 0 && Y + frame.getHeight() <= screenSize.height - 40) {
                                VToolbar.this.setLocation(screenSize.width - frame.getWidth(), Y);
                            } else if (Y <= 0) {
                                vpos.setVlocation(NORTH);
                                initialClick = null;
                            } else {
                                vpos.setVlocation(SOUTH);
                                initialClick = null;
                            }
                        } else {
                            vpos.setVlocation(WEST);
                            initialClick = null;
                        }

                    } else if (vlocation1 == WEST) {
                        if (Math.abs(xMoved) < 2.5 * frame.getWidth()) {
                            if (Y >= 0 && Y + frame.getHeight() <= screenSize.height - 40) {
                                VToolbar.this.setLocation(0, Y);
                            } else if (Y <= 0) {
                                vpos.setVlocation(NORTH);
                                initialClick = null;
                            } else {
                                vpos.setVlocation(SOUTH);
                                initialClick = null;
                            }
                        } else {
                            vpos.setVlocation(EAST);
                            initialClick = null;
                        }
                    }




                }
            });


        }

        public void setOrientation(int orientation) {

            this.removeAll();
            this.setLayout(new BorderLayout());
            //Border Layout

            if (orientation == PercentLayout.VERTICAL) {
                systemUser.setUI(new BasicLabelUI());
                this.add(toggleButton, BorderLayout.SOUTH);
                this.add(toolBar, BorderLayout.CENTER);
                this.add(systemUser, BorderLayout.NORTH);
            } else {
                systemUser.setUI(new VerticalLabelUI(false));
                this.add(toggleButton, BorderLayout.EAST);
                this.add(toolBar, BorderLayout.CENTER);
                this.add(systemUser, BorderLayout.WEST);
            }

            //Toolbar Layout
            percentLayout.setOrientation(orientation);



            this.revalidate();
            this.repaint();
        }

        public JPanel getToolBar() {
            return toolBar;
        }

        public JToggleButton getToggleButton() {
            return toggleButton;
        }

        public JLabel getSystemUser() {
            return systemUser;
        }
    }

    private static class VerticalLabelUI extends BasicLabelUI {

        static {
            labelUI = new VerticalLabelUI(false);
        }
        protected boolean clockwise;

        public VerticalLabelUI(boolean clockwise) {
            super();
            this.clockwise = clockwise;
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            Dimension dim = super.getPreferredSize(c);
            return new Dimension(dim.height, dim.width);
        }
        private static Rectangle paintIconR = new Rectangle();
        private static Rectangle paintTextR = new Rectangle();
        private static Rectangle paintViewR = new Rectangle();
        private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

        @Override
        public void paint(Graphics g, JComponent c) {
            JLabel label = (JLabel) c;
            String text = label.getText();
            Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

            if ((icon == null) && (text == null)) {
                return;
            }

            FontMetrics fm = g.getFontMetrics();
            paintViewInsets = c.getInsets(paintViewInsets);

            paintViewR.x = paintViewInsets.left;
            paintViewR.y = paintViewInsets.top;

            // Use inverted height &amp; width
            paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
            paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

            paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
            paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

            String clippedText = layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

            Graphics2D g2 = (Graphics2D) g;
            AffineTransform tr = g2.getTransform();
            if (clockwise) {
                g2.rotate(Math.PI / 2);
                g2.translate(0, -c.getWidth());
            } else {
                g2.rotate(-Math.PI / 2);
                g2.translate(-c.getHeight(), 0);
            }

            if (icon != null) {
                icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
            }

            if (text != null) {
                int textX = paintTextR.x;
                int textY = paintTextR.y + fm.getAscent();

                if (label.isEnabled()) {
                    paintEnabledText(label, g, clippedText, textX, textY);
                } else {
                    paintDisabledText(label, g, clippedText, textX, textY);
                }
            }
            g2.setTransform(tr);
        }
    }
}
