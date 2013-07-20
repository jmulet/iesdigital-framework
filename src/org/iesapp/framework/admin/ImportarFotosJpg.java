/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ImportarFotos.java
 *
 * Created on 18-jun-2011, 12:27:41
 */

package org.iesapp.framework.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class ImportarFotosJpg extends javar.JRDialog {
    private final DefaultListModel listModel1;
    private String path;
    private String directory="";
    private LongTask task;
    private final Timer timer;
    private final int AnimationRate=1000;
    private final CoreCfg coreCfg;

    /** Creates new form ImportarFotos */
    public ImportarFotosJpg(java.awt.Frame par, boolean modal, CoreCfg coreCfg) {
        super(par, modal);
        this.coreCfg = coreCfg;
        initComponents();
        listModel1 = new DefaultListModel();
        jList1.setModel(listModel1);

        timer = new javax.swing.Timer(AnimationRate, new
                ActionListener() {
                         public void actionPerformed(ActionEvent e) {
                                 if(task!=null && task.isAlive())
                                 {
                                    jButton3.setEnabled(false);
                                    jButton4.setEnabled(false);
                                 }
                                 else
                                 {
                                    jButton3.setEnabled(true);
                                    jButton4.setEnabled(true);
                                    jProgressBar1.setIndeterminate(false);
                                    timer.stop();
                                 }
                         }
         });

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jProgressBar1 = new javax.swing.JProgressBar();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Importació fotos des d'imatges jpg");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Choose directory");

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        jButton2.setText("Delete");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Log");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jButton3.setText("Import");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Close");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Do update if thumbnail already exists in database");

        jButton1.setText("· · ·");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jCheckBox1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 212, Short.MAX_VALUE)
                        .addComponent(jButton4)
                        .addGap(8, 8, 8))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Add new File to the List
    //Esborra el selected file
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int id = jList1.getSelectedIndex();
        if(id>=0) {
            listModel1.remove(id);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
      this.dispose();

    }//GEN-LAST:event_jButton4ActionPerformed

    //Importa
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jTextArea1.removeAll();
        timer.start();
        jProgressBar1.setIndeterminate(true);
        task = new LongTask();
        task.start();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(task!=null && task.isAlive())
        {
                task.interrupt();
                timer.stop();
        }
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    //Selecciona el diretori
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Selecciona el directori ...");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //
        java.io.File fpath = null;
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//          //System.out.println("getCurrentDirectory(): "
//             +  chooser.getCurrentDirectory());
//          //System.out.println("getSelectedFile() : "
//             +  chooser.getSelectedFile());
//          }
            fpath =  chooser.getSelectedFile();
         }
        else {
          return;
          }

        jTextField1.setText(fpath.getAbsolutePath());
        // It is also possible to filter the list of returned files.
        // This example does not return any files that start with `.'.
        FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                return  name.endsWith(".jpg");
         }
        };

        String[] fotosList = fpath.list(filter);
        listModel1.clear();
        for(int i=0; i<fotosList.length;i++) {
            listModel1.addElement(fotosList[i]);
        }

       
    }//GEN-LAST:event_jButton1ActionPerformed


    class LongTask extends Thread
    {
        public void run()
        {
            jTextArea1.removeAll();
            for(Object fitxer: listModel1.toArray())
            {
                  String ruta = jTextField1.getText() + "\\"+ fitxer;
                  String aux = StringUtils.BeforeLast( (String) fitxer, ".");

                  int expd2 = -1;
                  try{
                    expd2 = (int) Double.parseDouble(aux);
                  }
                  catch( java.lang.NumberFormatException e)
                  {
                      //
                  }
                  if(expd2>=0) {
                    uploadImage(expd2, ruta);
                }
            }
        }

       
        private void uploadImage(int Exp2, String linkHref) {

            boolean q = jCheckBox1.isSelected();

            String SQL1="SELECT * FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_detall WHERE Exp_FK_ID="+Exp2;
            String SQL2 ="";
            String missatge ="";
            try {
                Statement st = coreCfg.getMysql().createStatement();
                ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1,st); 
                if (rs1 != null && rs1.next())
                {
                    SQL2 = "UPDATE `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_detall SET FOTO=? WHERE Exp_FK_ID=?";
                    missatge = "--> ["+Exp2+"] OK UPDATE \n";
                    if(!q)
                    {
                        jTextArea1.append("--> ["+Exp2+"] EXISTEIX - NO MODIFICAT\n");
                        return;
                    }
                } else {
                    SQL2 = "INSERT INTO `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_detall SET Foto=?,  Exp_FK_ID=?";
                    missatge = " --> ["+Exp2+"] OK :: INSERT \n";
                }

                if(rs1!=null)
                {
                      rs1.close();
                      st.close();
                }
                else
                {
                    jTextArea1.append(" --> ERROR: No hi ha connexio\n");
                    return;
                }
            } catch (SQLException ex) {
                Logger.getLogger(ImportarFotosJpg.class.getName()).log(Level.SEVERE, null, ex);
            }
            PreparedStatement ps = null;

            try {
                Connection con = coreCfg.getMysql().getConnection();
                //con.setAutoCommit(false);
                ps = con.prepareStatement(SQL2);

                File file = new File(linkHref);
                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                    ps.setBinaryStream(1, fis, (int) file.length());
                    ps.setInt(2, Exp2);

                } catch (FileNotFoundException ex) {
                  jTextArea1.append(" --> ERROR: No es pot trobar el fitxer amb la imatge\n");

                }

                int s = ps.executeUpdate();
                if(s>0) {
                       jTextArea1.append(missatge);
                }
                else {
                       jTextArea1.append(" --> ERROR: No es pot fer upload a la base de dades\n");
                }

                //con.commit();
                ps.close();
            } catch (java.sql.SQLException ex) {
                //Logger.getLogger(BeanDadesPersonals.class.getName()).log(Level.SEVERE, null, ex);
                jTextArea1.append(" --> ERROR SQL: "+ex+"\n");
            }
            }

    }

 @Override
    protected JRootPane createRootPane() {
    JRootPane rootPane2 = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
    Action actionListener = new AbstractAction() {
        @Override
      public void actionPerformed(ActionEvent actionEvent) {
        setVisible(false);
      }
    } ;
    InputMap inputMap = rootPane2.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(stroke, "ESCAPE");
    rootPane2.getActionMap().put("ESCAPE", actionListener);

    return rootPane2;
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}
