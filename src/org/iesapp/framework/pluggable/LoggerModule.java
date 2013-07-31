/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable;

import com.javamex.classmexer.MemoryUtil;
import com.javamex.classmexer.MemoryUtil.VisibilityFilter;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import org.iesapp.framework.data.User;
import org.iesapp.framework.dialogs.LoginAdmin;
import org.iesapp.framework.dialogs.LoginInterface;
import org.iesapp.framework.pluggable.daemons.TopModuleDaemon;
import org.iesapp.framework.table.CellTableState;
import org.iesapp.framework.table.MyIconButtonRenderer;
import org.iesapp.framework.table.MyIconLabelRenderer;
import org.iesapp.framework.util.IconUtils;
import org.iesapp.util.StringUtils;
/**
 *
 * @author Josep
 */
public class LoggerModule extends TopModuleWindow {
    private DefaultTableModel modelTable1;
    private static final int timerTime = 3000;
    private Timer timer;
    private long usedAverage;
    private long freeAverage;
    private long totalAverage;
    private boolean adminMode;
    /**
     * Creates new form LoggerModule
     */
    public LoggerModule() {
        this.moduleName = "mysqlLogger";
        this.moduleDescription ="Mysql logger";
        this.moduleDisplayName = "Mysql Logger";
        
        initComponents();
       
    }
    
    @Override
    public void postInitialize() {
       
        //Determine if Logger is basic (everybody) of full mode (administrator)
        adminMode = false;
        
        if(coreCfg.getUserInfo()!=null)
        {
            adminMode = coreCfg.getUserInfo().getGrant() == User.ADMIN ||
                    coreCfg.getUserInfo().getRole().contains("ADMIN");
        }
        jScrollPane3.setViewportView(DebugLogger.getInstance());
              
        jTabbedPane1.setTitleAt(1, coreCfg.getMysql().getConBean().getDb()+"@"+coreCfg.getMysql().getConBean().getHost());
        jTabbedPane1.setTitleAt(2, coreCfg.getSgd().getConBean().getDb()+"@"+coreCfg.getSgd().getConBean().getHost());
   
        jTextPane1.setDocument(coreCfg.getMysql().getDocument());
        jTextPane2.setDocument(coreCfg.getSgd().getDocument());
        jTextPane1.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        jTextPane2.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        coreCfg.getMysql().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                int n = coreCfg.getMysql().getDocument().getLength();
                jTextPane1.setCaretPosition(n > 0 ? n - 1 : 0);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                int n = coreCfg.getMysql().getDocument().getLength();
                jTextPane1.setCaretPosition(n > 0 ? n - 1 : 0);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                int n = coreCfg.getMysql().getDocument().getLength();
                jTextPane1.setCaretPosition(n > 0 ? n - 1 : 0);
            }
        });
        
        
        coreCfg.getSgd().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                int n = coreCfg.getSgd().getDocument().getLength();
                jTextPane2.setCaretPosition(n > 0 ? n - 1 : 0);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                int n = coreCfg.getSgd().getDocument().getLength();
                jTextPane2.setCaretPosition(n > 0 ? n - 1 : 0);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                int n = coreCfg.getSgd().getDocument().getLength();
                jTextPane2.setCaretPosition(n > 0 ? n - 1 : 0);
            }
        });
        
        timer = new Timer(timerTime, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(jTabbedPane1.getSelectedIndex()==3)
                {
                    refreshUI();
                }
            }
        });
        timer.start();
        
        if(!adminMode)
        {
            jTabbedPane1.setEnabledAt(1, false);
            jTabbedPane1.setEnabledAt(2, false);
            jTabbedPane1.setEnabledAt(3, false);
        }
    }
    
    
    

    @Override
    public void refreshUI() {
        Runtime runtime = Runtime.getRuntime();
        int mb = 1024*1024;
        
        //Try to average memory sampling
        usedAverage = (usedAverage + (runtime.totalMemory() - runtime.freeMemory()) )/2;
        freeAverage = (freeAverage + runtime.freeMemory() )/2;
        totalAverage = (totalAverage + runtime.totalMemory())/2;
         
         //Print used memory
        jTextField1.setText(""+ usedAverage / mb);
 
        //Print free memory
        jTextField2.setText(""+ freeAverage / mb);
         
        //Print total available memory--
        jTextField3.setText(""+ totalAverage / mb);
 
        //Print Maximum available memory
        jTextField4.setText("" + runtime.maxMemory() / mb);
        
        while(jTable1.getRowCount()>0)
        {
            modelTable1.removeRow(0);
        }
      
        
        Collection<TopModuleWindow> listWindows = TopModuleRegistry.listWindows();
        Iterator<TopModuleWindow> iterator = listWindows.iterator();
        while(iterator.hasNext())
        {
            TopModuleWindow win = iterator.next();
            String memoryMB = "N.A.";
            try{
                VisibilityFilter referenceFilter = VisibilityFilter.NON_PUBLIC;
                long memory = MemoryUtil.deepMemoryUsageOf(win, referenceFilter)/(1024*1024);
                memoryMB = ""+memory;
            }
            catch(Exception ex)
            {
                //                
            }
            CellTableState cts1 = new CellTableState("", -1, 1);
            CellTableState cts4 = new CellTableState("", -1, 1);
            CellTableState cts5 = new CellTableState("", -1, 1);
            modelTable1.addRow(new Object[]{cts1, win.getName(), StringUtils.displayComplexTime(win.getUpTime()/1000), memoryMB, win.getModuleStatus(),cts4,cts5});
        }
        
        HashMap<String, TopModuleDaemon> activeDeamons = TopModuleDaemon.getActiveDeamons();
        for(String key: activeDeamons.keySet())
        {
            CellTableState cts0 = new CellTableState("", -3, 3);
            VisibilityFilter referenceFilter = VisibilityFilter.NON_PUBLIC;
            TopModuleDaemon daemon = activeDeamons.get(key);
            String memoryMB = "N.A.";
            try{
                memoryMB = ""+ MemoryUtil.deepMemoryUsageOf(daemon, referenceFilter)/(1024*1024);
            }
            catch(Exception ex)
            {
                //
            }
            CellTableState cts4 = new CellTableState("", -1, 1);
            CellTableState cts5 = new CellTableState("", -1, 1);
            modelTable1.addRow(new Object[]{cts0, key, StringUtils.displayComplexTime(daemon.getUpTime()/1000), memoryMB, daemon.getStatus(),cts4,cts5});       
        }
            
        CellTableState cts4 = new CellTableState("", -1, 0);
        CellTableState cts5 = new CellTableState("", -1, 0);
        CellTableState cts0 = new CellTableState("", -1, 0);
        modelTable1.addRow(new Object[]{cts0, "Framework", StringUtils.displayComplexTime(coreCfg.getUpTime()/1000), "N.A.", "--",cts4,cts5});
      
            
    }
    
    

    @Override
    public void dispose() {
        timer.stop();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jScrollPane9 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable(){
            @Override
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });
        jTabbedPane1.addTab("Log", jScrollPane3);

        jTextPane1.setEditable(false);
        jTextPane1.setContentType("text/html"); // NOI18N
        jScrollPane1.setViewportView(jTextPane1);

        jTabbedPane1.addTab("tab1", jScrollPane1);

        jTextPane2.setEditable(false);
        jTextPane2.setContentType("text/html"); // NOI18N
        jScrollPane2.setViewportView(jTextPane2);

        jTabbedPane1.addTab("tab2", jScrollPane2);

        modelTable1 = new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "","Component","Up time","Memory","Status","Refresh","Stop"
            }
        );
        jTable1.setModel(modelTable1);
        Icon[] icons = new Icon[]{IconUtils.getBlankIcon(),
            IconUtils.getModuleIcon(), IconUtils.getPluginIcon(),
            IconUtils.getFrameworkIcon("daemon.png")};

        Icon[] icons2 = new Icon[]{IconUtils.getBlankIcon(),
            IconUtils.getFrameworkIcon("refresh.png")};

        Icon[] icons3 = new Icon[]{IconUtils.getBlankIcon(),
            IconUtils.getFrameworkIcon("close.gif")};

        jTable1.setRowHeight(32);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(new MyIconLabelRenderer(icons));
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new MyIconButtonRenderer(icons2));
        jTable1.getColumnModel().getColumn(6).setCellRenderer(new MyIconButtonRenderer(icons3));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jTable1);

        jLabel1.setText("Module registry");

        jLabel2.setText("Memory usage (MB)");

        jLabel5.setText("Used");

        jLabel6.setText("Free");

        jLabel7.setText("Total");

        jLabel8.setText("Max");

        jTextField1.setEditable(false);

        jTextField2.setEditable(false);

        jTextField3.setEditable(false);

        jTextField4.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane5)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel5))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(43, 43, 43)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1))
                        .addGap(0, 13, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jScrollPane9.setViewportView(jPanel2);

        jTabbedPane1.addTab("Profiler", jScrollPane9);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentContainer());
        getContentContainer().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int col = jTable1.getSelectedColumn();
        int row = jTable1.getSelectedRow();
        if(col<0 || row<0)
        {
            return;
        }
        if(col==4)
        {
            //Refresh
            String id = (String) jTable1.getValueAt(row, 1);
            TopModuleWindow win = TopModuleRegistry.findId(id);
            if(win!=null)
            {
                win.refreshUI();
            }
            
            
        }
        else if(col==5)
        {
            //Close
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        int indexAtLocation = jTabbedPane1.indexAtLocation(evt.getX(), evt.getY());
        if(indexAtLocation>0 && !adminMode)
        {
            //Require administration VALIDATION
             LoginInterface dlg = (LoginInterface) new LoginAdmin(javar.JRDialog.getActiveFrame(), true);            
             dlg.display();          
             int code = dlg.getValidation();
             if(code!=1)
             {
                 dlg.dispose();
                 return;
             }
            dlg.dispose();
            
            adminMode = true;
            jTabbedPane1.setEnabledAt(1, adminMode);
            jTabbedPane1.setEnabledAt(2, adminMode);
            jTabbedPane1.setEnabledAt(3, adminMode);
        }
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    // End of variables declaration//GEN-END:variables
}
