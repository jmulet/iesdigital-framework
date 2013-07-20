/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.pluginsAPI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.io.FileUtils;
import org.iesapp.framework.data.RolesChooser;
import org.iesapp.framework.pluggable.modulesAPI.BeanModule;
import org.iesapp.framework.pluggable.modulesAPI.GenericFactory;
import org.iesapp.framework.pluggable.modulesAPI.ModulesManager;
import org.iesapp.framework.table.CellTableState;
import org.iesapp.framework.table.MyIconLabelRenderer;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.Unzip;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public class PluginManager extends javar.JRDialog {
    private DefaultTableModel modelTable1;
    private final String forModule;
    private ArrayList<BeanModule> listInstalled;
    private boolean listening;
    private ArrayList<BeanModule> pluginList;
    private String tmpdir;
    private File lastDirectory;
    private final ResourceBundle bundle;
    private final String forApp;
    private final GenericFactory genericFactory;
    private final CoreCfg coreCfg;

    /**
     * Creates new form PluginManager
     */
    public PluginManager(java.awt.Frame parent, boolean modal, String forModule, String forApp, final GenericFactory genericFactory,
            CoreCfg coreCfg) {
       super(parent, modal);
        this.forModule = forModule;
        this.forApp = forApp;
        this.coreCfg = coreCfg;
        initComponents();
        listening = false;
        this.setTitle("Plugin Manager @ Module : "+forModule);
           this.setIconImage( new ImageIcon(ModulesManager.class.getResource("/org/iesapp/framework/icons/plugin.gif")).getImage());
     
        this.genericFactory = genericFactory;
//        genericFactory = new GenericFactory(forApp, "?", "?");  
//        genericFactory.setCurrentModuleClass(forModule);
        
        lastDirectory = new File(CoreCfg.contextRoot+"\\installables");
        bundle = ResourceBundle.getBundle("org/iesapp/framework/pluggable/pluggable");
                
        jTable1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if(listening && e.getColumn()==2)
                {
                    int row = jTable1.getSelectedRow();
                    String visibility = (String) jTable1.getValueAt(row,e.getColumn());
                    genericFactory.setCurrentPluginClass(listInstalled.get(row).getClassName());
                    genericFactory.setPluginAttribute("roles", visibility);
                    fillTable1();
                }
            }
        });
        
        fillTable1();
        
    }

    private void fillTable1()
    {
        listening = false;
        while(jTable1.getRowCount()>0)
        {
            modelTable1.removeRow(0);
        }
            
        listInstalled = genericFactory.loadPlugins();
       // //System.out.println("genericfactoy loadplugins gives "+listInstalled.size());
        for(BeanModule bplug: listInstalled)
        {
           // if(bplug.getForModule().equals(forModule))
            {
                CellTableState cts0 = new CellTableState("", -1, 0);
                CellTableState cts = new CellTableState(bplug.isEnabled()?"ON":"OFF", -1, bplug.isEnabled()?1:0);
                modelTable1.addRow(new Object[]{cts0, cts, bplug.getClassName(), bplug.getRoles()});
            }
            
            
        }
        listening = true;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Plugin Manager");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        modelTable1 = new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "","Activació", "Plugin", "Disponible per a roles"
            }
        );
        jTable1.setModel(modelTable1);
        jTable1.setRowHeight(32);
        String[] icons = new String[]{"/org/iesapp/framework/icons/redspot.gif","/org/iesapp/framework/icons/greenspot.gif"};
        String[] icons2 = new String[]{"/org/iesapp/framework/icons/delete.gif"};

        jTable1.getColumnModel().getColumn(0).setCellRenderer(new MyIconLabelRenderer(icons2));
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new MyIconLabelRenderer(icons));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 23, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Instal·lats", jPanel1);

        jLabel1.setText("Plugin file");

        jButton1.setText("···");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextPane1.setEditable(false);
        jTextPane1.setContentType("text/html"); // NOI18N
        jScrollPane2.setViewportView(jTextPane1);

        jButton2.setText("Instal·la el plugin");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Instal·la", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
         int row = jTable1.getSelectedRow();
         int col = jTable1.getSelectedColumn();
         if(row<0 || col<0)
         {
             return;
         }
         if(col==0)
         {
              //Remove plugin from the currrent module
             int result = JOptionPane.showConfirmDialog(this, "This plugin will be removed from module "+forModule+".\nDo you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION);
             if(result!=JOptionPane.YES_OPTION)
             {
                 return;
             }
             genericFactory.removePluginClass( (String) jTable1.getValueAt(row, 2) );
             genericFactory.saveXmlDoc();
              
             fillTable1();
         }
         else if(col==1)
         {
             listInstalled.get(row).setEnabled( !listInstalled.get(row).isEnabled() );
//             CellTableState cts = new CellTableState(listInstalled.get(row).enabled?"ON":"OFF", -1, listInstalled.get(row).enabled?1:0);
//             jTable1.setValueAt(cts, row, col);             
             genericFactory.setCurrentPluginClass(listInstalled.get(row).getClassName());
             genericFactory.setPluginAttribute("enabled", listInstalled.get(row).isEnabled()?"yes":"no");
             fillTable1();
         }
         else if(col==3)
         {
            String roles =  (String) jTable1.getValueAt(row, 3); 
            String pclass =  (String) jTable1.getValueAt(row, 2); 
            RolesChooser dlg = new RolesChooser(javar.JRDialog.getActiveFrame(),roles,coreCfg);
            dlg.setLocationRelativeTo(jTable1);
            if(dlg.showModal()==RolesChooser.CHOOSER_ACCEPT)
            {
                roles = dlg.getSelection();
                jTable1.setValueAt(roles, row, 3); 
                genericFactory.setCurrentPluginClass(pclass);
                genericFactory.setPluginAttribute("roles", roles);
            }
            dlg.dispose();
         }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        jFileChooser1.setCurrentDirectory(lastDirectory);
        jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser1.setFileFilter(new FileFilter(){

            @Override
            public boolean accept(File f) {
                return f.getAbsolutePath().endsWith(".plg")  || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Plugin files (*.plg)";
            }
        });


        int showDialog = jFileChooser1.showDialog(this, bundle.getString("tria"));        
        if(showDialog==JFileChooser.APPROVE_OPTION)
        {
            File file = jFileChooser1.getSelectedFile();
            lastDirectory = file.getParentFile();
            jTextField1.setText(file.getAbsolutePath());
          
            //Unzip package to temporary dir
            tmpdir = System.getProperty("java.io.tmpdir")+StringUtils.BeforeLast(file.getName(), ".");
            //Delete dir in case it may exist
            new File(tmpdir).delete();
            
            Unzip.unzip(file.getAbsolutePath(), tmpdir);
            //System.out.println("contents unzipped to "+tmpdir);
            //search for jar in tmpdir
            File contents = new File(tmpdir);
            File[] listFiles = contents.listFiles();
            File modulefile = null;
            for(File f: listFiles)
            {
                if(f.getAbsolutePath().endsWith(".jar"))
                {
                    modulefile = f;
                    break;
                }
            }
            
            if(modulefile==null)
            {
                jTextPane1.setText(bundle.getString("invalidPlugin"));
                return;
            }
          
            
            
            pluginList = null;
            try {
                ZipFile jarfile = new ZipFile(modulefile);
                ZipEntry ze = jarfile.getEntry("META-INF/plugin.xml");
                InputStream inputStream = jarfile.getInputStream(ze);
                pluginList = genericFactory.loadPluginsFromManifest(inputStream);              
                inputStream.close();
                jarfile.close();
            } catch (IOException ex) {
                //Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
                jTextPane1.setText(bundle.getString("invalidPlugin"));
                return;
            }
                       
            if(pluginList!=null)
            {
                 StringBuilder builder = new StringBuilder();
                 jButton2.setEnabled(true);
              
                for(BeanModule bip: pluginList)
                {
                String dependencies = bip.getBeanMetaINF().getDependencies();
                dependencies = dependencies.isEmpty()?"none":dependencies;
                builder.append("<span><b>Author:</b> ");
                builder.append(bip.getBeanMetaINF().getAuthor()).append("</span><br>");
                builder.append("<span><b>Version:</b> ");
                builder.append(bip.getBeanMetaINF().getVersion()).append("</span><br>");
                builder.append("<span><b>Description:</b></span><br><span> ");
                builder.append(bip.getBeanMetaINF().getDescription(coreCfg.core_lang)).append("</span><br><span>");
                builder.append("<b>For Module:</b></span><br><span>  ");
                builder.append(bip.getForModule()).append("</span><br>");
                builder.append("<b>Dependencies:</b></span><br><span>  ");
                builder.append(dependencies).append("</span><br><br>");
                }
                jTextPane1.setText(builder.toString());
            }
            else
            {
                JOptionPane.showMessageDialog(this, bundle.getString("invalidModule"));
            }
                
        }
       
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try{
            
        for(BeanModule bip: pluginList)
        {
        if(bip.getForModule()!=null && !bip.getForModule().equals(forModule))
        {    
             JOptionPane.showMessageDialog(this, bundle.getString("inadequatePlugin")+" :\n"+bip.getForModule());
             return;   
        }
        
        ArrayList<BeanModule> installed = genericFactory.loadPlugins();
        for(BeanModule binst: installed)
        {
            if(binst.getClassName().equals(bip.getClassName()))
            {
                JOptionPane.showMessageDialog(this, bundle.getString("alreadyInstalledPlugin")+" : "+bip.getClassName());
                return;
            }
        }
        }
                        
        //copy stuff from tmpdir to the correct folders
        File contents = new File(tmpdir);
        File[] listFiles = contents.listFiles();
        for(File f: listFiles)
        {
            //Copy module libraries in lib/ folder
            if(f.isDirectory() && f.getName().equals("lib"))
            {
                File[] listFiles1 = f.listFiles();
                for(File f1: listFiles1)
                {
                    File tmp = new File(CoreCfg.contextRoot+File.separator+"lib"+File.separator+f1.getName());
                    if(!tmp.exists())
                    {
                            FileUtils.copyFile(f1, tmp);
                       
                    }
                    pluginList.get(0).getRequiredLibs().add("lib"+File.separator+f1.getName());
                }
            }
            else if(f.isFile() && f.getAbsolutePath().endsWith(".jar"))
            {
                String version = pluginList.get(0).getBeanMetaINF().getVersion();
                if(version==null || version.isEmpty())
                {
                    version = "1.0";
                }
                version = version.replaceAll("\\.", "_");
                String realName = StringUtils.BeforeLast( f.getName(), ".jar")+"-v"+version+".jar";
                FileUtils.copyFile(f, new File(CoreCfg.contextRoot+File.separator+"modules"+File.separator+realName));
                
                //Replace the jar file name in the beans (which are to be saved in xml)
                for(BeanModule bip: pluginList)
                {
                    bip.setJar(realName);
                }
            }
            else if(f.isFile() && f.getAbsolutePath().endsWith(".ini"))
            {
                //copy plugin initializaton
                FileUtils.copyFile(f, new File(CoreCfg.contextRoot+File.separator+"config"+File.separator+f.getName()));
            }
            else if(f.isFile() && f.getAbsolutePath().endsWith(".gif") || 
                    f.getAbsolutePath().endsWith(".jpg") ||
                    f.getAbsolutePath().endsWith(".png"))
            {
                //copy plugin icon to plugins resource folder
                FileUtils.copyFile(f, new File(CoreCfg.contextRoot+File.separator+"resources"+File.separator+f.getName()));
            }
        }
        
        //Desa xml
        for(BeanModule bip: pluginList)
        {
            genericFactory.writePlugin(bip);
        }
        genericFactory.saveXmlDoc();
        
        //get rid of tmp dir
        contents.delete();
        JOptionPane.showMessageDialog(this, bundle.getString("restartForModule"));
        fillTable1();
        jButton2.setEnabled(false);
        jTextField1.setText("");
        jTextPane1.setText("");    
        jTabbedPane1.setSelectedIndex(0);
        } catch (IOException ex) {
             Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        genericFactory.saveXmlDoc();
    }//GEN-LAST:event_formWindowClosing

   
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
