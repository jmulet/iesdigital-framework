/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.modulesAPI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.iesapp.framework.pluggable.pluginsAPI.BeanAnchorPoint;
import org.iesapp.framework.pluggable.pluginsAPI.PluginManager;
import org.iesapp.framework.table.CellTableState;
import org.iesapp.framework.table.MyIconButtonRenderer;
import org.iesapp.framework.table.MyIconLabelRenderer;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.CoreIni;
import org.iesapp.framework.util.Unzip;
import org.iesapp.updater.BeanModuleVersion;
import org.iesapp.updater.BeanModulesRepo;
import org.iesapp.updater.FileDownloaderPanel;
import org.iesapp.updater.RemoteRepository;
import org.iesapp.updater.RemoteUpdater;
import org.iesapp.util.StringUtils;
import org.xml.sax.SAXException;

/**
 *
 * @author Josep
 */
public class ModulesManager extends javar.JRDialog {

  
    private DefaultTableModel modelTable1;
    private final Class classfor;
    private ArrayList<BeanModule> listInstalled;
    private boolean listening;
    private ArrayList<BeanModule> moduleList;
    private String tmpdir;
    private File lastDirectory;
    private final ResourceBundle bundle;
    private final String requiredModuleName;
    private GenericFactory genericFactory;
    private File lock;
    private final String requiredJar;
    private DefaultTableModel modelTable2;
    private final CoreCfg coreCfg;

    /**
     * Creates new form PluginManager
     */
    public ModulesManager(java.awt.Frame parent, boolean modal, Class classfor, String requiredModuleName, String requiredJar,
            CoreCfg coreCfg) {
       super(parent, modal);
       this.coreCfg = coreCfg;
       
        
        this.requiredModuleName = requiredModuleName;
        this.requiredJar = requiredJar;
        this.setIconImage( new ImageIcon(ModulesManager.class.getResource("/org/iesapp/framework/icons/module_icon.gif")).getImage());
        this.classfor = classfor;
        initComponents();
        
        DocumentListener documentListener = new DocumentListener(){

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        boolean q = checkInstallable();
                        jButton2.setEnabled(q);
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        boolean q=checkInstallable();
                        jButton2.setEnabled(q);
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        boolean q= checkInstallable();
                        jButton2.setEnabled(q);
                    }
            
        }
        ;
        
        jTextField3.getDocument().addDocumentListener(documentListener);
        jTextField4.getDocument().addDocumentListener(documentListener);
        jTextField6.getDocument().addDocumentListener(documentListener);
        
        ActionListener buttonListener = new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jButton5.setEnabled(jRadioButton1.isSelected());
                    }
            
        };
        
        jRadioButton1.addActionListener(buttonListener);
        jRadioButton2.addActionListener(buttonListener);
        
        setTitle("Modules Manager @ Application: "+classfor.getName());
        listening = false;
        lastDirectory = new File(CoreCfg.contextRoot+"\\installables");
        bundle = java.util.ResourceBundle.getBundle("org/iesapp/framework/pluggable/pluggable");
        try {
            genericFactory = new GenericFactory(classfor.getName(), requiredModuleName, requiredJar);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Values from application attributes
        jTextField2.setText( genericFactory.getApplicationAttribute("display").trim() );
        jCheckBox1.setSelected( genericFactory.getApplicationAttribute("validateLogout").trim().equalsIgnoreCase("yes") );
        jCheckBox2.setSelected( genericFactory.getApplicationAttribute("preventSwitchUser").trim().equalsIgnoreCase("no") );
        
        jCheckBox3.setSelected( genericFactory.getApplicationAttribute("showMenuBar").trim().equalsIgnoreCase("yes") );
        jCheckBox4.setSelected( genericFactory.getApplicationAttribute("showToolBar").trim().equalsIgnoreCase("yes") );
        jCheckBox5.setSelected( genericFactory.getApplicationAttribute("showStatusBar").trim().equalsIgnoreCase("yes") );
       
        
        jTextField2.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                 genericFactory.setApplicationAttribute("display", jTextField2.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                 genericFactory.setApplicationAttribute("display", jTextField2.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                genericFactory.setApplicationAttribute("display", jTextField2.getText());
            }
        });
        
        //table
        jTable1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if(listening && e.getColumn()==2)
                {
                    int row = jTable1.getSelectedRow();
                    String roles = (String) jTable1.getValueAt(row,e.getColumn());
                    genericFactory.setCurrentModuleClass(listInstalled.get(row).getClassName());
                    genericFactory.setModuleAttribute("roles", roles);                   
                    fillTable1();
                }
            }
        });
        
        
        //table
        jTable2.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if(listening && e.getColumn()==1)
                {
                    int row = jTable2.getSelectedRow();
                    String key = (String) jTable2.getValueAt(row, 0);
                    String newvalue = (String) jTable2.getValueAt(row,e.getColumn());
                    genericFactory.setApplicationInitParameter(key, newvalue);                   
                    //fillTable1();
                }
            }
        });
        
        
        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(jRadioButton1);
        bg1.add(jRadioButton2);
        
        fillTable1();
        
        fillTable2();
        
        fillModList();
        
        jTextField7.setText(CoreIni.getCore_repoURLs());
        fillTree();
    }

    private void fillTable2()
    {
        HashMap<String, Property> map = genericFactory.getApplicationInitParameters();
        for(String key: map.keySet())
        {
            Property prop = map.get(key);
            modelTable2.addRow(new Object[]{key, prop.value, prop.description});
        }
    }
    
    private boolean checkInstallable()
    {
        boolean q = true;
        if(jTextPane1.getText().contains("Error:"))
        {
            return false;
        }
        
        if(jTabbedPane2.getSelectedIndex()==0)
        {
            q =!jTextField1.getText().isEmpty();
        }
        else
        {
            q &= !jTextField3.getText().trim().isEmpty();
            q &= !jTextField4.getText().trim().isEmpty();
            q &= !jTextField6.getText().trim().isEmpty();
        }
        return q;
    }
    
    private void fillTable1()
    {
        listening = false;
        while(jTable1.getRowCount()>0)
        {
            modelTable1.removeRow(0);
        }
            
        listInstalled = genericFactory.loadModules();
        for(BeanModule bm: listInstalled)
        {
                int status = 0;
                String label = "OFF";
                if(bm.isEnabled())
                {
                    status = 1;
                    label = "ON";
                }
                
                
                if(bm.getClassName().equals(requiredModuleName))
                {
                    status = 2;
                    label = "ON";
                }
                
                
                CellTableState cts0 = new CellTableState("", -1, status==2?1:0);
                cts0.setTooltip("Uninstall module from this application");
                 
                CellTableState cts = new CellTableState(label, -1, status);
                
                CellTableState cts3 = new CellTableState("",-1,3);
                String moduleType = "STANDARD";
                String numPlugins = bm.getInstalledPlugins().size()+"";
                String numDeamons = bm.getDeamons().size()+"";
                
                if(bm.getModuleType()==BeanModule.MODULETYPE_DESKTOP_BROWSE)
                {
                    moduleType = "DESKTOP-BROWSE";
                    numPlugins = "N.A.";
                    numDeamons = "N.A.";
                }
                else if(bm.getModuleType()==BeanModule.MODULETYPE_DESKTOP_OPEN)
                {
                    moduleType = "DESKTOP-OPEN";
                    numPlugins = "N.A.";
                    numDeamons = "N.A.";
                }
                modelTable1.addRow(new Object[]{cts0, cts, bm.getClassName(), moduleType, cts3, numPlugins, numDeamons});
            
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
        jButton3 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel8 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jTextField7 = new javax.swing.JTextField();
        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jTaskPane1 = new com.l2fprod.common.swing.JTaskPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jPanel4 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jTextField2 = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable(){
            public boolean isCellEditable(int row, int col)
            {
                return col==1;
            }
        };

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modules Manager");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        modelTable1 = new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "","Activació", "Mòdul", "Tipus", "Opcions", "Plugins", "Deamons"
            }
        );
        jTable1.setModel(modelTable1);
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setRowHeight(32);
        Icon[] icons = new Icon[]{
            new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/redspot.gif")),
            new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/greenspot.gif")),
            new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/greyspot.gif")),
            new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/configIcon.gif"))};

        Icon[] icons2 = new Icon[]{
            new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/delete.gif")),
            new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/blank.gif"))};

        jTable1.getColumnModel().getColumn(0).setCellRenderer(new MyIconLabelRenderer(icons2));
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new MyIconLabelRenderer(icons));
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(240);
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new MyIconButtonRenderer(icons));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton3.setText("Updater");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/framework/icons/up.gif"))); // NOI18N
        jButton6.setText("Move up");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/iesapp/framework/icons/down.gif"))); // NOI18N
        jButton7.setText("Move down");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton6)
                    .addComponent(jButton7)))
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/iesapp/framework/pluggable/pluggable"); // NOI18N
        jTabbedPane1.addTab(bundle.getString("installed"), jPanel1); // NOI18N

        jButton2.setText(bundle.getString("instalaModul")); // NOI18N
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2);

        jTabbedPane2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane2StateChanged(evt);
            }
        });

        jTextField1.setEditable(false);

        jLabel1.setText(bundle.getString("moduleFile")); // NOI18N

        jButton1.setText("···");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setOneTouchExpandable(true);

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jList1);

        jSplitPane1.setLeftComponent(jScrollPane4);

        jTextPane1.setContentType("text/html"); // NOI18N
        jScrollPane2.setViewportView(jTextPane1);

        jSplitPane1.setRightComponent(jScrollPane2);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSplitPane1))
                .addGap(1, 1, 1))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(1, 1, 1)
                .addComponent(jSplitPane1)
                .addGap(2, 2, 2))
        );

        jTabbedPane2.addTab("New STD module", jPanel7);

        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Desktop Open");

        jRadioButton2.setText("Desktop Browse");

        jLabel3.setText("Type");

        jLabel4.setText("Description");

        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });

        jLabel5.setText("Icon");

        jLabel6.setText("File / URL");

        jLabel7.setText("URL parameters");

        jTextField4.setEditable(false);
        jTextField4.setToolTipText("icon path relative to iesDigital path");

        jTextField6.setToolTipText("Absolute file path");

        jButton4.setText("···");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("···");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jRadioButton1)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButton2))
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                                .addComponent(jTextField4)
                                .addComponent(jTextField3)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton5)
                    .addComponent(jButton4))
                .addGap(2, 2, 2))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(200, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("New Desktop-Open/Browse", jPanel8);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane2)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab(bundle.getString("local"), jPanel2); // NOI18N

        jButton8.setText("Download");
        jButton8.setEnabled(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton8);

        jLabel8.setText("Repository URL");

        jButton9.setText("Refresh");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jButton9)
                .addGap(11, 11, 11)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField7)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(5, 5, 5))
        );

        jSplitPane2.setResizeWeight(0.4);
        jSplitPane2.setToolTipText("");
        jSplitPane2.setOneTouchExpandable(true);

        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setOneTouchExpandable(true);

        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(jTree1);

        jSplitPane3.setTopComponent(jScrollPane5);

        jTaskPane1.setFocusCycleRoot(true);
        jSplitPane3.setBottomComponent(jTaskPane1);

        jSplitPane2.setLeftComponent(jSplitPane3);

        jTextPane2.setContentType("text/html"); // NOI18N
        jScrollPane6.setViewportView(jTextPane2);

        jSplitPane2.setRightComponent(jScrollPane6);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane2)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                        .addGap(5, 5, 5))
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );

        jTabbedPane1.addTab(bundle.getString("remote"), jPanel9); // NOI18N

        jCheckBox1.setText(" Require ADMIN password to quit");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("Allow fast user session switching");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Display at startup");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "normal", "extended", "fullscreen" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Menus"));

        jCheckBox3.setText("Show menubar");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jCheckBox4.setText("Show toolbar");
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });

        jCheckBox5.setText("Show statusbar");
        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox5)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox5)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addComponent(jCheckBox2))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(230, 230, 230))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(259, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("App Preferences", jPanel4);

        modelTable2 = new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Property", "Value", "Description"
            }
        );
        jTable2.setModel(modelTable2);
        jScrollPane3.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 119, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("App Init Parameters", jPanel6);

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
         if(col==0) //Delete
         {
             if(((CellTableState) jTable1.getValueAt(row, 0)).getState()==1)
             {
                 return;
             }
             //Remove module from this app
             int result = JOptionPane.showConfirmDialog(this, "This module will be removed from this application.\nDo you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION);
             if(result!=JOptionPane.YES_OPTION)
             {
                 return;
             }
             genericFactory.removeModuleClass( (String) jTable1.getValueAt(row, 2) );
             genericFactory.saveXmlDoc();
             modelTable1.removeRow(row);
         
         }
         else if(col==1)
         {
             //toggle activation
             
             CellTableState cts0 = (CellTableState) jTable1.getValueAt(row, col);
             if(cts0.getState()<2)
             {
                 listInstalled.get(row).enabled = !listInstalled.get(row).enabled;
                 CellTableState cts = new CellTableState(listInstalled.get(row).enabled?"ON":"OFF", -1, listInstalled.get(row).enabled?1:0);
                 jTable1.setValueAt(cts, row, col);     
                 genericFactory.setCurrentModuleClass(listInstalled.get(row).getClassName());
                 genericFactory.setModuleAttribute("enabled", listInstalled.get(row).isEnabled()?"yes":"no");
                 fillTable1();
             }
         }
         else if(col==4)
          {  
             CellTableState cts0 = (CellTableState) jTable1.getValueAt(row, 1);
             String className = listInstalled.get(row).getClassName();
             genericFactory.setCurrentModuleClass(className);
             boolean isrequired = cts0.getState()>=2;
             
             //module configuration must know which type of module is going to pass
             
             ModuleOptionsManager options = new ModuleOptionsManager(javar.JRDialog.getActiveFrame(), true, listInstalled.get(row), genericFactory, isrequired, coreCfg);
             options.setLocationRelativeTo(null);
             options.setVisible(true);
             listInstalled = genericFactory.loadModules();
         }
         else if(col==5)
         {
             //Plugins are only for standard modules
             if(listInstalled.get(row).getModuleType()!=BeanModule.MODULETYPE_STD)
             {
                 return;
             }
              
             String className = listInstalled.get(row).getClassName();
             genericFactory.setCurrentModuleClass(className);
             //First save
             genericFactory.saveXmlDoc();
             
             String forModule = listInstalled.get(row).className;
             PluginManager dlg= new PluginManager(javar.JRDialog.getActiveFrame(), true, forModule, classfor.getName(), genericFactory, coreCfg);
             dlg.setLocationRelativeTo(this);
             dlg.setVisible(true);
            try {
                //reload document
                genericFactory = new GenericFactory(classfor.getName(), requiredModuleName, requiredJar);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
             //Go to the current module
             int row2 = jTable1.getSelectedRow();
             if(row2>=0)
             {
                genericFactory.setCurrentModuleClass( (String) jTable1.getValueAt(row2, 2) );
             }
             fillTable1();
         }
         else if(col==6) ////Deamons
         {
             //Deamons are only for standard modules
             if(listInstalled.get(row).getModuleType()!=BeanModule.MODULETYPE_STD)
             {
                 return;
             }
              
             String className = listInstalled.get(row).getClassName();
             genericFactory.setCurrentModuleClass(className);
             //First save
             genericFactory.saveXmlDoc();
             
            
             ModulesDeamonsManager dlg= new ModulesDeamonsManager(javar.JRDialog.getActiveFrame(), true, genericFactory, listInstalled.get(row).getDeamons());
             dlg.setLocationRelativeTo(this);
             dlg.setVisible(true);
             //save changes
             genericFactory.saveXmlDoc();
             
         }
    }//GEN-LAST:event_jTable1MouseClicked

    
    /* 
     * Install new module to this application
     * Modules are zipped with this structure
     * /ieapp-module-Name.mod
     * /lib/.... dir containing required libs
     * Main jar must contains META-INF/module.xml
     * and META-INF/module.gif
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jFileChooser1.setCurrentDirectory(lastDirectory);
        jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser1.setFileFilter(new FileFilter(){

            @Override
            public boolean accept(File f) {
                return f.getAbsolutePath().endsWith(".mod")  || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Module files (*.mod)";
            }
        });

        int showDialog = jFileChooser1.showDialog(this, bundle.getString("tria"));        
        if(showDialog==JFileChooser.APPROVE_OPTION)
        {
            File file = jFileChooser1.getSelectedFile();
            lastDirectory = file.getParentFile();
            
            displayInformationFor(file);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * Actual installation if not exists
     * @param evt 
     */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(jTabbedPane2.getSelectedIndex()==0)
        {
            try {
                installSTD();
            } catch (IOException ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            installSpecial();
        }
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void installSpecial()
    {
        BeanModule bip = new BeanModule();
        if(jRadioButton1.isSelected())
        {
            bip.setClassName("desktopOpen."+jTextField3.getText().replaceAll(" ", ""));
            bip.getIniParameters().put("icon", jTextField4.getText().trim());
            bip.getIniParameters().put("file", jTextField6.getText().trim());
           
        }
        else
        {
            bip.setClassName("desktopBrowse."+jTextField3.getText().replaceAll(" ", ""));
            bip.getIniParameters().put("icon", jTextField4.getText().trim());
            bip.getIniParameters().put("url", jTextField6.getText().trim());
            bip.getIniParameters().put("urlparameters", jTextField5.getText().trim());
        }
        
        //Set anchor
        BeanAnchorPoint banchor = new BeanAnchorPoint();
        banchor.setLocation("toolbar");
        banchor.setParentId("jToolBarModules");
        bip.getListAnchorPoints().add(banchor);
        
        bip.setAutoStart(BeanModule.NO);
        bip.getModuleNameBundle().put("default", jTextField3.getText());
        genericFactory.writeModule(bip);   
        jButton2.setEnabled(false);
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        genericFactory.saveXmlDoc();
        
        fillTable1();
        jTabbedPane1.setSelectedIndex(0);
    }
    
    
    private void installSTD() throws IOException
    {
        if(jTextPane1.getText().contains("Warning:"))
        {
            int showConfirmDialog = JOptionPane.showConfirmDialog(this, "Do you want to continue with warnings?", "Warnings", JOptionPane.YES_NO_OPTION);
            if(showConfirmDialog != JOptionPane.OK_OPTION)
            {
                return;
            }
        }
        ArrayList<BeanModule> installed = genericFactory.loadModules();
        for(BeanModule binst: installed)
        {
            for(BeanModule bip: moduleList)
            {
                if(binst.getClassName().equals(bip.getClassName()))
                {
                    JOptionPane.showMessageDialog(this, bundle.getString("alreadyInstalledModule")+" : "+bip.getClassName());
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
                    File tmp = new File(CoreCfg.contextRoot+"\\lib\\"+f1.getName());
                    if(!tmp.exists())
                    {
                        FileUtils.copyFile(f1, tmp); 
                    }
                    moduleList.get(0).getRequiredLibs().add("lib\\"+f1.getName());
                }
            }
            else if(f.isFile() && f.getAbsolutePath().endsWith(".jar"))
            {
                //copy modules.jar to modules folder
                //Use a version convention for jar files modules\iesapp-module-xxxx-v1_2.jar
                String version = moduleList.get(0).getBeanMetaINF().version;
                if(version==null || version.isEmpty())
                {
                    version = "1.0";
                }
                version = version.replaceAll("\\.", "_");
                String realName = StringUtils.BeforeLast( f.getName(), ".jar")+"-v"+version+".jar";
                FileUtils.copyFile(f, new File(CoreCfg.contextRoot+File.separator+"modules"+File.separator+realName));
                
                //Replace the jar file name in the beans (which are to be saved in xml)
                for(BeanModule bip: moduleList)
                {
                    bip.setJar(realName);
                }
            }
            else if(f.isFile() && f.getAbsolutePath().endsWith(".ini"))
            {
                //copy plugin icon to plugins resource folder
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
        for(BeanModule bip: moduleList)
        {
            genericFactory.writeModule(bip);
        }
        genericFactory.saveXmlDoc();
       
        
        //get rid of tmp dir
         try {
                //Delete dir in case it may exist (we must also getrid of subdirectories)
                org.apache.commons.io.FileUtils.deleteDirectory(contents);
            } catch (IOException ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        JOptionPane.showMessageDialog(this, bundle.getString("restartForModule"));
        fillTable1();
        jButton2.setEnabled(false);
        jTextField1.setText("");
        jTextPane1.setText("");    
        jTabbedPane1.setSelectedIndex(0);
       
    }
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //Desa el document abans de sortir
        genericFactory.saveXmlDoc();
        //Remove lock
        lock.delete();
    }//GEN-LAST:event_formWindowClosing

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
         jTextField2.setText( (String) jComboBox1.getSelectedItem());
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        genericFactory.setApplicationAttribute("validateLogout", jCheckBox1.isSelected()?"yes":"no");
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        genericFactory.setApplicationAttribute("preventSwitchUser", jCheckBox2.isSelected()?"no":"yes");
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        genericFactory.setApplicationAttribute("showMenuBar", jCheckBox3.isSelected()?"yes":"no");
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
       genericFactory.setApplicationAttribute("showToolBar", jCheckBox4.isSelected()?"yes":"no");
    }//GEN-LAST:event_jCheckBox4ActionPerformed

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox5ActionPerformed
        genericFactory.setApplicationAttribute("showStatusBar", jCheckBox5.isSelected()?"yes":"no"); 
    }//GEN-LAST:event_jCheckBox5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        int row = jTable1.getSelectedRow();
        String selectedModule = "";
        if(row>=0)
        {
            selectedModule = (String) jTable1.getValueAt(row, 2);
        }
        ModulesUpdater dlg = new ModulesUpdater(javar.JRDialog.getActiveFrame(),true, selectedModule);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
        try {
            //Ha de tornar a generar el factory i obrir la llista
            genericFactory = new GenericFactory(classfor.getName(), requiredModuleName, requiredJar);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        fillTable1();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        lock = new File(CoreCfg.contextRoot+"\\config\\modulesManager.lock");
        if(lock.exists())
        {
            String info =  "";
            try {
                //Read lock information
                info = " by "+FileUtils.readFileToString(lock);
            } catch (IOException ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            info = "Modules manager was locked\n"+info+"\nIf you are sure this is an error, please\nmanually remove file config/modulesManager.lock";
            JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), info);
            this.setVisible(false);
            this.dispose();      
        }
        else
        {
            try {
                boolean created = lock.createNewFile();
                
                if(!created)
                {
                    JOptionPane.showMessageDialog(javar.JRDialog.getActiveFrame(), "Can't create lock file");
                    this.dispose();
                }
                FileUtils.writeStringToFile(lock, coreCfg.getUserInfo().getName()+"\nat "+new java.util.Date());
                
            } catch (IOException ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      
    }//GEN-LAST:event_formWindowOpened

    private void jTabbedPane2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane2StateChanged
         
         jButton2.setEnabled(checkInstallable());
    }//GEN-LAST:event_jTabbedPane2StateChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
         JFileChooser fc = new JFileChooser();
         fc.addChoosableFileFilter(new FileFilter(){

            @Override
            public boolean accept(File f) {
                 String filename = f.getAbsolutePath().toLowerCase();
                 return  f.isDirectory() ||
                         filename.endsWith(".jpg")  ||
                         filename.endsWith(".jpeg") || 
                         filename.endsWith(".gif")  ||
                         filename.endsWith(".png"); 
            }

            @Override
            public String getDescription() {
                return "Image files (.jpg, .gif, .png)";
            }
        });
         if(lastDirectory!=null)
         {
             fc.setCurrentDirectory(lastDirectory);
         }
         fc.setMultiSelectionEnabled(false);
         fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
         if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
         {
             lastDirectory = fc.getSelectedFile().getParentFile();
             if(fc.getSelectedFile().getAbsolutePath().startsWith(CoreCfg.contextRoot))
             {
                 jTextField4.setText(fc.getSelectedFile().getAbsolutePath().replace(CoreCfg.contextRoot, ""));
             }
         }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
         JFileChooser fc = new JFileChooser();
         if(lastDirectory!=null)
         {
             fc.setCurrentDirectory(lastDirectory);
         }
         fc.setMultiSelectionEnabled(false);
         fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
         if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
         {
             lastDirectory = fc.getSelectedFile().getParentFile();
         }
         jTextField6.setText(fc.getSelectedFile().getAbsolutePath());
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3KeyReleased

    //Move UP
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int row = jTable1.getSelectedRow();
        if(row<1)
        {
            return;
        }
        String moduleName = (String) jTable1.getValueAt(row, 2);
        String moduleNameBefore = (String) jTable1.getValueAt(row-1, 2);
        genericFactory.moveModuleBefore(moduleName, moduleNameBefore);
        genericFactory.saveXmlDoc();
        fillTable1();
        //Choose again
        for(int i=0; i<jTable1.getRowCount(); i++)
        {
          String str = (String) jTable1.getValueAt(i, 2);
          if(str.equals(moduleName))
          {
              jTable1.setRowSelectionInterval(i, i);
              break;
          }
        }
            
    }//GEN-LAST:event_jButton6ActionPerformed

    //Move Down
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        int row = jTable1.getSelectedRow();
        if(row<1 || row==jTable1.getRowCount()-1)
        {
            return;
        }
        String moduleName = (String) jTable1.getValueAt(row, 2);
        String moduleNameAfter = (String) jTable1.getValueAt(row+1, 2);
        genericFactory.moveModuleAfter(moduleName, moduleNameAfter);
        genericFactory.saveXmlDoc();
        fillTable1();
        //Choose again
        for(int i=0; i<jTable1.getRowCount(); i++)
        {
          String str = (String) jTable1.getValueAt(i, 2);
          if(str.equals(moduleName))
          {
              jTable1.setRowSelectionInterval(i, i);
              break;
          }
        }
        
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        int id = jList1.getSelectedIndex();
        if(id>=0)
        {
            String filename = (String) jList1.getSelectedValue();
            File file = new File(CoreCfg.contextRoot+File.separator+"installables"+File.separator+filename); 
            displayInformationFor(file);
        }
    }//GEN-LAST:event_jList1ValueChanged

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        TreePath path = jTree1.getSelectionPath();
        if(path.getPathCount()==3) //Correct depth
        {
           DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) path.getLastPathComponent();
           DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) node1.getParent();
           BeanModuleVersion bmv = (BeanModuleVersion) node1.getUserObject();
           BeanModulesRepo bmr = (BeanModulesRepo) node2.getUserObject();
           
           String url = bmv.getFileName();
           String name = StringUtils.AfterLast("/", url);
           File file = new File(CoreCfg.contextRoot+File.separator+"installables"+File.separator+name);
           FileDownloaderPanel fdp = new FileDownloaderPanel(url, file);
           jTaskPane1.add(fdp);
           fdp.download();
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        fillTree();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        TreePath path = evt.getPath();
        System.out.println(path);
        if(path.getPathCount()==3) //Correct depth
        {
           DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) path.getLastPathComponent();
           DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) node1.getParent();
           BeanModuleVersion bmv = (BeanModuleVersion) node1.getUserObject();
           BeanModulesRepo bmr = (BeanModulesRepo) node2.getUserObject();
           
           jTextPane2.setText(bmv.toString());
           jButton8.setEnabled(true);
        }
        else
        {
            jButton8.setEnabled(false);
        }
    }//GEN-LAST:event_jTree1ValueChanged

   
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private com.l2fprod.common.swing.JTaskPane jTaskPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables


    /**
     * Given a .mod or .plg file unzips its contents
     * returns dir where contents are unziped
     * @param installable
     * @return 
     */
    public static File unzipInstallable(final File installable)
    {
        String temporalDir = System.getProperty("java.io.tmpdir")+StringUtils.BeforeLast(installable.getName(), ".");
        //System.out.println("TemporalDir is "+temporalDir);
        //Delete dir in case it may exist
        File dir = new File(temporalDir);        
        
        if(dir.exists())
        {
             try {
                //Delete dir in case it may exist (we must also getrid of subdirectories)
                org.apache.commons.io.FileUtils.deleteDirectory(dir);
            } catch (IOException ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }
        Unzip.unzip(installable.getAbsolutePath(), temporalDir);
        return dir;
    }
    
    /**
     * Deploys a module installable
     * installable file of the installable mod or plg
     * list: list of modules/plugins which contains the extension jar, set null (the program will search for them)
     * @param installable : .mod or .plg file
     * @param appClassName : name of the aplication class 
     * @param type : GenericFactory.PLUGIN or GenericFactory.MODULE
     * @param moduleClassName : name of the module classs
     * @param ver : new version of the module
     * @param replaceJar : boolean    
     * @param replaceLibs : boolean
     * @throws java.io.IOException 
     */
    public static void update(final File installable, 
            String appClassName, int type, String moduleClassName, final String ver,
            boolean replaceJar, boolean replaceLibs) throws IOException
    {

       File contents = unzipInstallable(installable);
       GenericFactory factory = null;
       try{
       if(type==GenericFactory.PLUGIN)
       {
            ArrayList<String> parsed = StringUtils.parseStringToArray(moduleClassName, "\\", StringUtils.CASE_INSENSITIVE);
            factory = new GenericFactory(appClassName,"","");
            boolean found1 = factory.setCurrentModuleClass(parsed.get(0));
            boolean found2 = factory.setCurrentPluginClass(parsed.get(1));
            //if this application does not contain such a module/plugin, nothing to do
            if(!found1 || !found2)
            {
                return;
            }
       }
       else
       {
             factory = new GenericFactory(appClassName,"","");
             boolean found = factory.setCurrentModuleClass(moduleClassName);
             //if this application does not contain such a module, nothing to do
             if(!found)
             {
                 return;
             }
       }
       }
       catch(Exception ex)
       {
                //
       }
       
       
        File[] listFiles = contents.listFiles();
        for(File f: listFiles)
        {
            //Copy module libraries in lib/ folder
            if(f.isDirectory() && f.getName().equals("lib"))
            {
                File[] listFiles1 = f.listFiles();
                for(File f1: listFiles1)
                {
                    File file = new File(CoreCfg.contextRoot+File.separator+"lib"+File.separator+f1.getName());
                    if(replaceLibs || (!replaceLibs && !file.exists()))
                    {
                        FileUtils.copyFile(f1, file);
                    }                     
                }
            }
            else if(f.isFile() && f.getAbsolutePath().endsWith(".jar"))
            {
                //copy modules.jar to modules folder
                //Use a version convention for jar files modules\iesapp-module-xxxx-v1_2.jar
                String version = ver;
                if(version==null || version.isEmpty())
                {
                    version = "1.0";
                }
                version = version.replaceAll("\\.", "_");
                String realName = StringUtils.BeforeLast( f.getName(), ".jar")+"-v"+version+".jar";
                
                //Check for existing target
                File file = new File(CoreCfg.contextRoot+File.separator+"modules"+File.separator+realName);
                if(replaceJar || (!replaceJar && !file.exists()))
                {
                    FileUtils.copyFile(f, file);
                }
                        
                //Replace the jar file name in the beans (which are to be saved in xml)
                //these methods do nothing if application does not contain such a module
                if(type == GenericFactory.MODULE)
                {
                    factory.setModuleAttribute("jar", realName);
                    factory.setModuleMetaINF("version", ver);
                }
                else
                {                   
                    factory.setPluginAttribute("jar", realName);
                    factory.setPluginMetaINF("version", ver);
                }
            }
            else if(f.isFile() && f.getAbsolutePath().endsWith(".ini"))
            {
                //copy plugin icon to plugins resource folder
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
        factory.saveXmlDoc();
       
        
        //get rid of tmp dir
         try {
                //Delete dir in case it may exist (we must also getrid of subdirectories)
                org.apache.commons.io.FileUtils.deleteDirectory(contents);
            } catch (IOException ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }

/**
 * The same as before but update applies to all exisiting applications in config/*.xml
 * @param installable
 * @param type
 * @param moduleClassName
 * @param ver
 * @param replaceJar
 * @param replaceLibs
     * @throws java.io.IOException
 */    
     public static void update(final File installable, int type, String moduleClassName, final String ver,
            boolean replaceJar, boolean replaceLibs) throws IOException  
    {
        //Look for all existing applications
        ArrayList<String> list = getAllApplicationClassNames();
        for(String appClassName: list)
        {
            update(installable, appClassName, type, moduleClassName, ver, replaceJar, replaceLibs); 
        }
    }

    public static ArrayList<String> getAllApplicationClassNames() {
        ArrayList<String> list = new ArrayList<String>();
        //
        File dir = new File(CoreCfg.contextRoot+File.separator+"config");
        
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File file, String name) {
                return name.toLowerCase().endsWith(".xml");
            }
        };
        File[] listFiles = dir.listFiles(filter);
        //Make sure this files are valid application files
        //Check if they contain tag <application ...
        for(File f: listFiles)
        {
            System.out.println(f);
            String content = "";
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(f);
                content = IOUtils.toString(inputStream);
            } catch (Exception ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if(content.toLowerCase().contains("<application"))
            {
                list.add(StringUtils.BeforeLast(f.getName(),".").replaceAll("-", "."));
            }
        }
        return list;
    }
     
    private void displayInformationFor(File file) {
       
            jTextField1.setText(file.getAbsolutePath());
          
            //Unzip package to temporary dir
            tmpdir = System.getProperty("java.io.tmpdir")+StringUtils.BeforeLast(file.getName(), ".");
            try {
                //Delete dir in case it may exist (we must also getrid of subdirectories)
                org.apache.commons.io.FileUtils.deleteDirectory(new File(tmpdir));
            } catch (IOException ex) {
                Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
            }
                   
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
                jTextPane1.setText(bundle.getString("invalidModule"));
                return;
            }
          
            
            
            moduleList=null;
            try {
                ZipFile jarfile = new ZipFile(modulefile);
                ZipEntry ze = jarfile.getEntry("META-INF/module.xml");
                InputStream inputStream = jarfile.getInputStream(ze);
                moduleList = genericFactory.loadModulesFromManifest(inputStream);                
                inputStream.close();
                jarfile.close();
            } catch (IOException ex) {
                //Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
                jTextPane1.setText(bundle.getString("invalidModule"));
                return;
            }
                       
            if(moduleList!=null)
            {
                StringBuilder builder = new StringBuilder();
                
                for(BeanModule bip: moduleList)
                {
                String dependencies = bip.metaINF.dependencies;
                dependencies = dependencies.isEmpty()?"none":dependencies;
                jButton2.setEnabled(true);
                
                builder.append("<span><b>Author:</b> ");
                builder.append(bip.metaINF.author).append("</span><br>");
                builder.append("<span><b>Version:</b> ");
                builder.append(bip.metaINF.version).append("</span><br>");
                builder.append("<span><b>Description:</b></span><br><span> ");
                builder.append(bip.metaINF.getDescription(coreCfg.core_lang)).append("</span><br><span>");
                builder.append("<b>Dependencies:</b></span><br><span>  ");
                builder.append(dependencies).append("</span><br>");  
                builder.append("<b>URL:</b></span><br><span>  ");
                builder.append(bip.metaINF.getUrl()).append("</span><br>");  
                builder.append("<b>Min framework version:</b></span><span>  ");
                builder.append(bip.metaINF.getMinFrameworkVersion()).append("</span><br>");  
                builder.append("<b>Min client iesdigital version:</b></span><span>  ");
                builder.append(bip.metaINF.getMinClientID()).append("</span><br>"); 
                builder.append("<b>Min client SGD version:</b></span><span>  ");
                builder.append(bip.metaINF.getMinClientSGD()).append("</span><br>"); 
                
                //Do checks
                if(!bip.metaINF.getMinFrameworkVersion().isEmpty() &&
                    StringUtils.compare(bip.metaINF.getMinFrameworkVersion(), CoreCfg.VERSION)>0)
                {
                     builder.append("<span style=\"color:red\">Error: This module requires ").append(bip.metaINF.getMinFrameworkVersion()).
                             append(" framework version. Current version is ").append(CoreCfg.VERSION).append(", please upgrade your system.</span><br>");
                }
                else if(!bip.metaINF.getMinFrameworkVersion().isEmpty() &&
                    StringUtils.compare(bip.metaINF.getMinFrameworkVersion(), CoreCfg.VERSION)<0)
                {
                     builder.append("<span style=\"color:blue\">Warning: This module requires ").append(bip.metaINF.getMinFrameworkVersion()).
                             append(" framework version. Current version is ").append(CoreCfg.VERSION).append(".</span><br>");
                }
                
                if(!bip.metaINF.getMinClientID().isEmpty() &&
                    StringUtils.compare(bip.metaINF.getMinClientID(), coreCfg.getIesClient().getClientVersion())>0)
                {
                      builder.append("<span style=\"color:red\">Error: This module requires ").append(bip.metaINF.getMinClientID()).
                             append(" iesdigital client version. Current version is ").append(coreCfg.getIesClient().getClientVersion()).append(", please upgrade your system.</span><br>");
                }
                else if(!bip.metaINF.getMinClientID().isEmpty() &&
                    StringUtils.compare(bip.metaINF.getMinClientID(), coreCfg.getIesClient().getClientVersion())<0)
                {
                      builder.append("<span style=\"color:blue\">Warning: This module requires ").append(bip.metaINF.getMinClientID()).
                             append(" iesdigital client version. Current version is ").append(coreCfg.getIesClient().getClientVersion()).append(".</span><br>");
                }
                
                if(!bip.metaINF.getMinClientSGD().isEmpty() &&
                    StringUtils.compare(bip.metaINF.getMinClientSGD(), coreCfg.getSgdClient().getClientVersion())>0)
                {
                     builder.append("<span style=\"color:red\">Error: This module requires ").append(bip.metaINF.getMinClientSGD()).
                             append(" sgd client version. Current version is ").append(coreCfg.getSgdClient().getClientVersion()).append(", please upgrade your system.</span><br>");
                }
                else  if(!bip.metaINF.getMinClientSGD().isEmpty() &&
                    StringUtils.compare(bip.metaINF.getMinClientSGD(), coreCfg.getSgdClient().getClientVersion())<0)
                {
                     builder.append("<span style=\"color:blue\">Warning: This module requires ").append(bip.metaINF.getMinClientSGD()).
                             append(" sgd client version. Current version is ").append(coreCfg.getSgdClient().getClientVersion()).append(".</span><br>");
                }
                }
                
                jTextPane1.setText(builder.toString());
                jButton2.setEnabled(checkInstallable());
            }
            else
            {
                JOptionPane.showMessageDialog(this, bundle.getString("invalidModule"));
            }
                
        
        
    }

    private void fillModList() {
       DefaultListModel model = new DefaultListModel();
       jList1.setModel(model);
       
       File f = new File(CoreCfg.contextRoot+File.separator+"installables");
       FilenameFilter filter = new FilenameFilter() {

           @Override
           public boolean accept(File dir, String name) {
               return name.toLowerCase().endsWith(".mod");
           }
       };
       
       File[] listFiles = f.listFiles(filter);
       for(File file: listFiles)
       {
           model.addElement(file.getName());
       }
    }

    private void fillTree() {
        
        //Clear tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Modules");
        DefaultTreeModel treeModel1 = new DefaultTreeModel(root);
        jTree1.setModel(treeModel1);
        jTree1.setCellRenderer(new RepoTreeCellRenderer());
        
        //Reload Repository
        String url = jTextField7.getText();
        RemoteUpdater remoteUpdater1 = new RemoteUpdater(url);
        RemoteRepository repository;
        try {
            repository = remoteUpdater1.getModulesRepo();
            //Fill tree
            for(BeanModulesRepo bmr: repository.getModules())
            {
                DefaultMutableTreeNode module = new DefaultMutableTreeNode(bmr);
                for(BeanModuleVersion bmv: bmr.getModuleVersions())
                {
                     DefaultMutableTreeNode version = new DefaultMutableTreeNode(bmv);
                     module.add(version);
                }
                root.add(module);
            }
        } catch (Exception ex) {
            Logger.getLogger(ModulesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        for(int i=0; i<jTree1.getRowCount(); i++)
        {
            jTree1.expandPath(jTree1.getPathForRow(i));
        }
            
        
        
    }

    
    protected class RepoTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            
//                Component component = super.getTreeCellRendererComponent(
//                    tree, value, selected,
//                    expanded, leaf, row,
//                    hasFocus);
            
            JLabel returnValue = new JLabel();
           
            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                if (userObject instanceof BeanModulesRepo) {
                    BeanModulesRepo bmr = (BeanModulesRepo) userObject;
                    returnValue.setText( bmr.getName().toUpperCase() +" : "+bmr.getClassName());
                }
                else if (userObject instanceof BeanModuleVersion) {
                    BeanModuleVersion bmr = (BeanModuleVersion) userObject;
                    returnValue.setText( "Version : "+bmr.getVersion());
                }
                else
                {
                    returnValue.setText(value.toString());
                }
            }

            return returnValue;
        }
    }

}
