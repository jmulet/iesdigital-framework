/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PeriodSelect.java
 *
 * Created on 27-ago-2011, 19:12:22
 */
package org.iesapp.framework.dialogs;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.util.DataCtrl;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class PeriodSelect extends javar.JRDialog {
    private DefaultListModel listmodel1;
    public final static byte SINGLE=0;
    public final static byte SINGLE_INTERVAL=1;
    public final static byte MULTIPLE_INTERVAL=2;
    private final byte mselmode;
    private final int idTask;
    private int mexp2;
    private String mtipus;
    public boolean accept=false;
    public boolean esborra=false;
    private final CoreCfg coreCfg;

    /**
     * 
     * @param parent
     * @param modal
     * @param selmode
     * @param idt
     * @param exp2
     * @param tipus 
     */
    public PeriodSelect(java.awt.Frame parent, boolean modal, byte selmode, int idt, int exp2, String tipus,
            CoreCfg coreCfg) {
        super(parent, modal);
        this.coreCfg = coreCfg;
        mselmode = selmode;
        mexp2 = exp2;
        mtipus = tipus;
        idTask = idt;  //id de la tasca associada
        initComponents();
        startUp();  
        jAlert.setVisible(false);
    }

   
    
    private void startUp()
    {
        jButton2.setEnabled(false);
        
        listmodel1 = new DefaultListModel();
        jList1.setModel(listmodel1);
        
        java.util.Date data1 = new java.util.Date();
        java.util.Date data2 = new java.util.Date();
        
        jDateChooser1.setDate(data1);
        jCalendar1.setDate(data1);
        //jDateChooser1.setMinSelectableDate(data1);
        jDateChooser2.setDate(data2);
        //jDateChooser2.setMinSelectableDate(data1);
        
        listmodel1.addElement("");
        jList1.setSelectedIndex(0);
      
        updateEntry();
        
        //System.out.println(mselmode + " "+ SINGLE_INTERVAL);
        
        jCalendar1.setVisible(false);
        
        if(mselmode==SINGLE)
        {
            jPanel1.setVisible(false);
            jPanel6.setVisible(false);
            jDateChooser1.setVisible(false);
            jDateChooser2.setVisible(false);
            jLabel1.setText("");
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);
            jCalendar1.setVisible(true);
            this.setTitle("Tria el dia");
        }
        else if(mselmode==SINGLE_INTERVAL)
        {
            jPanel1.setVisible(false);
            jLabel1.setText("Tria l'interval comprès entre dia inici i final");
            this.setTitle("Tria l'interval de dies");
        }
        else if(mselmode==MULTIPLE_INTERVAL)
        {
            this.setTitle("Tria els intervals de dies");
        }
        
        this.pack();
    }
   
    
    public void setPeriods(String cmds)
    {
        if(cmds.trim().isEmpty()) {
            return;
        }
        ArrayList<String> list = StringUtils.parseStringToArray(cmds, ",", StringUtils.CASE_INSENSITIVE);
        listmodel1.removeAllElements();
        for(int i=0; i<list.size(); i++)
        {
            String interval = list.get(i);
           //System.out.println("detectat::"+interval);
            String2Date s2d = new String2Date(interval);
            jDateChooser1.setDate(s2d.getInici());
            jCalendar1.setDate(s2d.date1);
            jDateChooser2.setDate(s2d.getFinal());
            listmodel1.addElement( new DataCtrl(s2d.getInici()).getDiaMesComplet() + " a " +  new DataCtrl(s2d.getFinal()).getDiaMesComplet());
            updateEntry();
        }
        jList1.setSelectedIndex(0);
        if(listmodel1.getSize()>1) {
            jButton2.setEnabled(true);
        }
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
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jAlert = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();

        setTitle("Selecciona el període");

        jLabel1.setText("Afegeix diferents períodes compresos entre dia inici i dia final");
        jLabel1.setName("jLabel1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jButton1.setText("Nou");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Esborra");
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setName("jList1"); // NOI18N
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jList1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jList1PropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jLabel4.setText("Períodes seleccionats");
        jLabel4.setName("jLabel4"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addContainerGap(19, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)))
        );

        jAlert.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jAlert.setName("jAlert"); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 25, 5));

        jButton4.setText("Cancel·la");
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton4);

        jButton5.setText("Esborra dia/es");
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton5);

        jButton3.setText("Accepta");
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton3);

        jCalendar1.setName("jCalendar1"); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 81, Short.MAX_VALUE)
        );

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));

        jLabel2.setText("inici  ");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel2.add(jLabel2);

        jDateChooser1.setDateFormatString("dd/MM/yyyy");
        jDateChooser1.setName("jDateChooser1"); // NOI18N
        jDateChooser1.setPreferredSize(new java.awt.Dimension(195, 25));
        jDateChooser1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser1PropertyChange(evt);
            }
        });
        jPanel2.add(jDateChooser1);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));

        jLabel3.setText("final");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel3.add(jLabel3);

        jDateChooser2.setDateFormatString("dd/MM/yyyy");
        jDateChooser2.setName("jDateChooser2"); // NOI18N
        jDateChooser2.setPreferredSize(new java.awt.Dimension(200, 25));
        jDateChooser2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser2PropertyChange(evt);
            }
        });
        jPanel3.add(jDateChooser2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCalendar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jAlert, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 307, Short.MAX_VALUE))
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Nou període
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int n = listmodel1.getSize();
        String last = (String) listmodel1.get(listmodel1.getSize()-1);
        listmodel1.add(n-1, last);
        jList1.setSelectedIndex(n);
        jButton2.setEnabled(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    //Esborra període
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int id = jList1.getSelectedIndex();
        if(id<0 || listmodel1.getSize()==1) {
            return;
        }
        
        listmodel1.remove(id);
        jList1.setSelectedIndex(0);
        
        if(listmodel1.getSize()==1) {
            jButton2.setEnabled(false);
        }
        
    }//GEN-LAST:event_jButton2ActionPerformed

    //Tanca o amaga el diàleg
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       
        
        if(mselmode==SINGLE)
        {
            jDateChooser1.setDate(jCalendar1.getDate());
            jDateChooser2.setDate(jCalendar1.getDate());
        }
        
        //System.out.println(getAllPeriods());
        
        doDBUpdate(true);
        accept = true;
        this.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jList1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jList1PropertyChange
     
    }//GEN-LAST:event_jList1PropertyChange

    //Modifica el periode de la llista seleccionat
    private void jDateChooser1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser1PropertyChange
        updateEntry();
    }//GEN-LAST:event_jDateChooser1PropertyChange

    //Modifica el periode de la llista seleccionat
    private void jDateChooser2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser2PropertyChange
        updateEntry();
    }//GEN-LAST:event_jDateChooser2PropertyChange

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
      //System.out.println("Now the selection is"+jList1.getSelectedIndex());
      int id = jList1.getSelectedIndex();
      if(id<0) {
            return;
        }
      String str = (String) jList1.getSelectedValue();
      if(!str.contains("a")) {
            return;
        }
      String2Date s2d = new String2Date(str);
      jDateChooser1.setDate(s2d.date1);
      jDateChooser2.setDate(s2d.date2);
    }//GEN-LAST:event_jList1ValueChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    //Esborra les referencies a aquestes dates
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        doDBUpdate(false);
        accept = true;
        esborra = true;
        this.setVisible(false);
    }//GEN-LAST:event_jButton5ActionPerformed

    public void setTooltip(String tip)
    {
        jAlert.setText(tip);
        jAlert.setVisible(true);
    }
    
//    
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//                PeriodSelect dialog = new PeriodSelect(new javax.swing.JFrame(), true, PeriodSelect.SINGLE, 333, 3206, "EXPULSIO");
//                dialog.setPeriods("01/09/2011, 10/09/2011 a 12/11/2011");
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jAlert;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private com.toedter.calendar.JCalendar jCalendar1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    private void updateEntry() {
        int id = jList1.getSelectedIndex();
        if(id<0) {
            return;
        } //this is an error
        java.util.Date date1 = jDateChooser1.getDate();
        java.util.Date date2 = jDateChooser2.getDate();        
        listmodel1.setElementAt( new DataCtrl(date1).getDiaMesComplet() + " a " +  new DataCtrl(date2).getDiaMesComplet() , id);
    }

    public String getAllPeriods() {
       
       String txt = "";
       if(esborra) {
            return txt;
        }
        
       for(int i=0; i<listmodel1.size(); i++)
       {
           String item = (String) listmodel1.get(i);
           String date1 = StringUtils.BeforeFirst(item, "a").trim();
           String date2 = StringUtils.AfterFirst(item, "a").trim();
           if(date1.equals(date2))
           {
               txt += date1 + ", ";
           }
           else
           {
               txt += item + ", ";
           }
       }
       
       return StringUtils.BeforeLast(txt, ",");
        
    }

    public void doDBUpdate(boolean write) {
        
        String SQL1 = "DELETE FROM tuta_dies_sancions WHERE idActuacio="+idTask;
        int nup = coreCfg.getMysql().executeUpdate(SQL1);
        
        if(write && mexp2>0 && idTask>0)
        {
        for(int i=0; i<listmodel1.size(); i++)
        {
            String cmd = (String) listmodel1.get(i);
            String2Date s2d = new String2Date(cmd);
            
            String SQL2="INSERT INTO tuta_dies_sancions (exp2, idActuacio, desde, fins, tipus) VALUES (?,?,?,?,?)";
            Object[] obj = new Object[]{mexp2, idTask, s2d.getInici(), s2d.getFinal(), mtipus};
            nup = coreCfg.getMysql().preparedUpdate(SQL2, obj);
            
        }
        }
        
    }

    
    public java.util.Date getDate1()
    {
        return jDateChooser1.getDate();
    }

    public java.util.Date getDate2()
    {
        return jDateChooser2.getDate();
    }

    public void setDate1(Date date1) {
        jDateChooser1.setDate(date1);
        jCalendar1.setDate(date1);
    }
    
     public void setDate2(Date date2) {
        jDateChooser2.setDate(date2);
        jCalendar1.setDate(date2);
    }
     
     public void showEsborraDies(boolean show)
     {
         jButton5.setVisible(show);
     }
     
    class String2Date
    {
        private java.util.Date date1;
        private java.util.Date date2;
        
        public String2Date(String input)
        {
            String iniString = "";
            String finString = "";
            if(input.contains("a"))
            {
               iniString = StringUtils.BeforeFirst(input, "a").trim();
               finString = StringUtils.AfterFirst(input, "a").trim();
            }
            else
            {
                iniString = input.trim();
                finString = iniString;
            }
            SimpleDateFormat df = null;
            if(input.contains("-")) {
                df = new SimpleDateFormat("dd-MM-yyyy");
            } 
            else { 
                df = new SimpleDateFormat("dd/MM/yyyy");
            } 
                
                 
            try {
                date1= df.parse(iniString);
                date2= df.parse(finString);
            } catch (ParseException ex) {
                Logger.getLogger(PeriodSelect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public java.util.Date getInici()
        {
            return date1;
        }
        
        public java.util.Date getFinal()
        {
            return date2;
        }
        
    
    }
}
