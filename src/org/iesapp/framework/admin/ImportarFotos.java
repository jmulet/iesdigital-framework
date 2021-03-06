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
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author Josep
 */
public class ImportarFotos extends javar.JRDialog {
    private final DefaultListModel listModel1;
    private String path;
    private String directory="";
    private LongTask task;
    private final Timer timer;
    private final int AnimationRate=1000;
    private final CoreCfg coreCfg;

    /** Creates new form ImportarFotos */
    public ImportarFotos(java.awt.Frame par, boolean modal, CoreCfg coreCfg) {
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jCheckBox1 = new javax.swing.JCheckBox();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Importació fotos des de XESTIB html");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Html files generated by xestib");

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Do update if thumbnail exists in database");

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 60, 5));

        jButton3.setText("Import");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);

        jButton5.setText("Check");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5);

        jButton4.setText("Close");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 424, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Add new File to the List
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
             "Html exported page", "html", "htm");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(filter);

              if(directory==null) {
            directory = ".";
        }

         chooser.setCurrentDirectory(new File(directory));
            chooser.setCurrentDirectory(new File(directory));
        int returnVal = chooser.showOpenDialog(javar.JRDialog.getActiveFrame());
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
        
        File[] selectedFiles = chooser.getSelectedFiles();
        for(int i=0; i<selectedFiles.length; i++)
        {
            listModel1.addElement(selectedFiles[i].getPath());
            directory = selectedFiles[i].getAbsolutePath();
        }
    }
    }//GEN-LAST:event_jButton1ActionPerformed

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

    
    //Comprova alumnes que no tenen foto
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
  
            jTextArea1.setText("");
            jTextArea1.append("* Alumnes sense foto pel curs "+coreCfg.anyAcademic+"-"+(coreCfg.anyAcademic+1)+":\n");
            jTextArea1.append("  -------------------\n\n");
            
           String SQL1=" SELECT  "+
                 " xes.Exp2, CONCAT(llinatge1,', ', llinatge2,' ', nom1) AS nom, CONCAT(xh.Estudis,' ', xh.Grup) AS grupo  "+
               " FROM  `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_historic AS xh  "+
                 " INNER JOIN  `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne AS xes   "+
                 " ON xes.Exp2=xh.Exp2 AND xh.AnyAcademic='"+coreCfg.anyAcademic+"'  "+
                 " LEFT JOIN  `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_detall AS xdet   "+
                 " ON xes.Exp2 = xdet.Exp_FK_ID   "+
               " WHERE xdet.foto IS NULL OR xes.Exp2 NOT IN (SELECT Exp_FK_ID AS Exp2 FROM `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_detall)  "+
               " ORDER BY grupo, llinatge1, llinatge2, nom1  ";
      
           
        try {
            Statement st = coreCfg.getMysql().createStatement();
            ResultSet rs1 = coreCfg.getMysql().getResultSet(SQL1,st); 
              while(rs1!=null && rs1.next())
              {
                 String msg = "["+rs1.getString("Exp2")+"] "+rs1.getString("nom")+" : "+ rs1.getString("grupo")+"\n";
                 jTextArea1.append(msg);
              }
              if(rs1!=null) {
                rs1.close();
                st.close();
            }
                  
        } catch (SQLException ex) {
            Logger.getLogger(ImportarFotos.class.getName()).log(Level.SEVERE, null, ex);
        }
          jTextArea1.append("Fi");
    }//GEN-LAST:event_jButton5ActionPerformed


    class LongTask extends Thread
    {
        public void run()
        {
            jTextArea1.removeAll();
            for(Object ruta: listModel1.toArray())
            {
                  parseHtml((String) ruta);
            }
        }

        private void parseHtml(String ruta) {

        jTextArea1.append("*Parsing xestib2.0 html file "+ ruta+"\n");
        jTextArea1.append(" ---\n");

        File input = new File(ruta);
        path="";
        if(ruta.contains("\\")) {
                path = StringUtils.BeforeLast(ruta, "\\");
        }
        else {
                path = StringUtils.BeforeLast(ruta, "/");
        }
        
        Document doc;
            try {
                doc = Jsoup.parse(input, "UTF-8");
                Elements media = doc.getElementsByClass("contenidorFotoAlumne");

            for (Element link : media) {
                String linkHref = link.getElementsByTag("img").attr("src");
                Elements peus = link.getElementsByClass("peuFotoAlumne");
                String sExp = "";
                int Exp2 = -1;
                for(Element link2: peus)
                {
                    sExp = link2.text();
                    sExp = StringUtils.AfterLast(sExp, ":");
                    try{
                        Exp2 = (int) Double.parseDouble(sExp);
                    }
                    catch(java.lang.NumberFormatException e)
                    {
                        //impossible to convert string to int
                    }
                }
                if(Exp2>=0)
                {
                    String fotofile = path+"\\"+linkHref;
                    jTextArea1.append("EXP["+Exp2+"] -> "+fotofile );
                    uploadImage(Exp2, fotofile);
                }
            }

          } catch (IOException ex) {
                Logger.getLogger(ImportarFotos.class.getName()).log(Level.SEVERE, null, ex);
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
                    missatge = " --> OK :: UPDATE \n";
                    if(!q)
                    {
                        jTextArea1.append(" --> EXISTEIX - NO MODIFICAT\n");
                        return;
                    }
                } else {
                    SQL2 = "INSERT INTO `"+CoreCfg.core_mysqlDBPrefix+"`.xes_alumne_detall SET Foto=?,  Exp_FK_ID=?";
                    missatge = " --> OK :: INSERT \n";
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
                Logger.getLogger(ImportarFotos.class.getName()).log(Level.SEVERE, null, ex);
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
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

}
