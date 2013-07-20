/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.actuacions;

import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import com.l2fprod.common.swing.PercentLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javar.JRComponent;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import org.iesapp.clients.iesdigital.actuacions.Actuacio;
import org.iesapp.clients.iesdigital.actuacions.BeanCategory;
import org.iesapp.clients.iesdigital.actuacions.BeanFieldReport;
import org.iesapp.clients.iesdigital.actuacions.BeanFieldSet;
import org.iesapp.clients.iesdigital.actuacions.IFormulari;
import org.iesapp.framework.util.CoreCfg;
import org.iesapp.framework.util.PeriodParser;
import org.iesapp.framework.util.SplashWindow3;
import org.iesapp.util.StringUtils;

/**
 *
 * @author Josep
 */
public abstract class BasicForm extends javax.swing.JInternalFrame implements IFormulari{
 
    protected Actuacio act;
    protected HashMap<String,JRComponent> uiComponentsMap;

    // Variables declaration - do not modify                     
    protected javax.swing.JButton jButton2;
    protected javax.swing.JButton jButton3;
    protected javax.swing.JButton jButton4;
    protected javax.swing.JPanel jPanel2;
    protected JTaskPane mainPane;
    protected javax.swing.JPanel jPanel1;
    protected javax.swing.JLabel jLabel1;
    protected javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JScrollPane jScrollPane2;
    protected javax.swing.JScrollPane jScrollPane4;
    
    protected javax.swing.JTabbedPane jTabbedPane1;
    protected javax.swing.JTextPane jTextPane1;
    protected javax.swing.JTextPane jTextPane2;
    protected byte exitedCode;
    private JCheckBox jCheckBox1;
    private JPanel jPanel3;
    private JComboBox jComboBox1;
    private DefaultComboBoxModel modelCombo1;
    private JLabel jAlert;
    private JLabel jLabel10;
    private ActionListener exitListener;
    protected boolean generatedMainReport=false;
    private JButton sgdButton1;
    private final CoreCfg coreCfg;
    private final ResourceBundle bundle;
    
    
    public BasicForm(CoreCfg coreCfg){
        this.coreCfg = coreCfg;
        jPanel1 = new JPanel();
        bundle = ResourceBundle.getBundle("org/iesapp/framework/actuacions/actuacions");
        initComponents();
   }

                         
    private void initComponents() {

        mainPane = new JTaskPane(){
            //Add a gradient background
            @Override
            public void paintComponent(Graphics g)
            {
                Graphics2D g2d = (Graphics2D)g;
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(w, 0, new Color(200,200,150), 0, h, Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        PercentLayout layout2 = new PercentLayout();
        layout2.setGap(0);
        layout2.setOrientation(PercentLayout.VERTICAL);
        mainPane.setLayout(layout2);
      
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setTitle(bundle.getString("preparador"));
        
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N
        jTabbedPane1.addTab(bundle.getString("dades"), jScrollPane4);
        jScrollPane4.setViewportView(mainPane);
        
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextPane2.setEditable(false);
        jTextPane2.setContentType("text/html"); // NOI18N
        jTextPane2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextPane2.setName("jTextPane2"); // NOI18N
        jScrollPane2.setViewportView(jTextPane2);

        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jTextPane1.setEditable(false);
        jTextPane1.setContentType("text/html"); // NOI18N
        jTextPane1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextPane1.setName("jTextPane1"); // NOI18N
        jScrollPane1.setViewportView(jTextPane1);

        jAlert = new JLabel();
        jAlert.setName("jAlert");
        jAlert.setBackground(new java.awt.Color(255, 51, 0));
        jAlert.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jAlert.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jAlert.setName("jAlert"); // NOI18N
        jAlert.setOpaque(true);
        
        //Panel de reports
        jPanel3 = new JPanel();
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setName("jPanel3"); 

        jLabel10 = new JLabel();
        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText(bundle.getString("documents"));
        jLabel10.setName("jLabel10"); // 
  
        jComboBox1 = new JComboBox();
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.setPreferredSize(new java.awt.Dimension(200, 30));
  
        jCheckBox1 = new JCheckBox();
        jCheckBox1.setText(bundle.getString("limit"));
        jCheckBox1.setName("jCheckBox1"); // NOI18N
        
        jButton4.setIcon(new javax.swing.ImageIcon(BasicForm.class.getResource("/org/iesapp/framework/actuacions/icons/print2.gif")));
        jButton4.setText(bundle.getString("mostra"));
        jButton4.setName("jButton4"); // NOI18N
     
        
    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton4)))
        );

        
        //Panel de cancel·lar / aceptar

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 55, 5));
                
        jButton2.setIcon(new javax.swing.ImageIcon(BasicForm.class.getResource("/org/iesapp/framework/actuacions/icons/exit.gif")));
        jButton2.setText(bundle.getString("cancel"));
        jButton2.setName("jButton2"); // NOI18N
        jPanel2.add(jButton2);

        jButton3.setIcon(new javax.swing.ImageIcon(BasicForm.class.getResource("/org/iesapp/framework/actuacions/icons/save.gif")));
        jButton3.setText(bundle.getString("save"));
        jButton3.setName("jButton3"); // NOI18N
        jPanel2.add(jButton3);

        jLabel1.setBackground(new java.awt.Color(255, 204, 51));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("");
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setOpaque(true);
        
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(jLabel1, BorderLayout.CENTER);
        sgdButton1 = new JButton();
        sgdButton1.setIcon(new ImageIcon(BasicForm.class.getResource("/org/iesapp/framework/actuacions/icons/sgd.gif")));
        sgdButton1.setPreferredSize(new Dimension(40,10));
        sgdButton1.setRolloverEnabled(false);
        sgdButton1.setFocusable(false);
        sgdButton1.setToolTipText(bundle.getString("associatedInc"));
        sgdButton1.addActionListener(new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {
                  IncidenciesAssociades dlg = new IncidenciesAssociades(javar.JRDialog.getActiveFrame(),false,act.idFaltasAlumnos);                 
                  dlg.setLocationRelativeTo(sgdButton1);
                  dlg.setAlwaysOnTop(true);
                  dlg.setVisible(true);
              }
          });
        jPanel1.add(sgdButton1, BorderLayout.EAST);
     
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jAlert, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addComponent(jAlert)
                    .addContainerGap(521, Short.MAX_VALUE)))
        );

      
    }// </editor-fold>                        

 
    @Override
    public void updateDocDatabase()
    {
       act.map = this.getMap();
       act.save();

        /**
         * Comprova si algun camp te associades accions - nomes associades a
         * components renderitzables - Si son selectables, nomes si el component
         * esta seleccionat - Si son de tipus radiobutton o checkbox, nomes si
         * el component esta seleccionat - Si l'actuacio no es nova, nomes fa
         * update si hi ha hagut canvis
       *
         */
        //Primer detecta si hi ha hagut algun canvi en els fields amb accions
        boolean areThereActionChanges = false;
        for (BeanFieldSet bean : act.beanRule.getFields()) {
            if (bean.getAction() != null && uiComponentsMap.containsKey(bean.getFieldName())) {
                JRComponent jrcomponent1 = uiComponentsMap.get(bean.getFieldName());

                if (!act.nova && jrcomponent1.hasChanged()) {
                    areThereActionChanges = true;
                    break;
                }

            }
        }
        //Si hi ha canvis -> Esborra els registres i les crea de nou
        if (areThereActionChanges || act.nova) {
            if (!act.nova) {
                Actuacio.clearRegisterIesDigital(act.id_actuacio, coreCfg.getMysql());
                IncidenciesSGD1.clearAll(act.id_actuacio, coreCfg.getMysql());
            }

            for (BeanFieldSet bean : act.beanRule.getFields()) {
                if (bean.getAction() != null && uiComponentsMap.containsKey(bean.getFieldName())) {
                    boolean selectable = bean.getSelectableField() != null && !bean.getSelectableField().isEmpty();
                    JRComponent jrcomponent1 = uiComponentsMap.get(bean.getFieldName());

                      boolean button = jrcomponent1.getClass().equals(javar.JRCheckBox.class)
                                || jrcomponent1.getClass().equals(javar.JRRadioButton.class);


                      
                      if ((!selectable && !button) || (!selectable && button && !jrcomponent1.getValue().isEmpty())
                                || (selectable && jrcomponent1.isSelected())) {

                            String actualDates = "";
                            if (bean.getAction().getDates().equalsIgnoreCase("$VALUE")) {
                                //Nomes `puc obtenir dates correctes de jrcomponents tipus DATE
                                if (jrcomponent1.getClass().equals(javar.JRDateChooser.class)
                                        || jrcomponent1.getClass().equals(javar.JRMultiDateChooser.class)
                                        || jrcomponent1.getClass().equals(javar.JRPeriodDateChooser.class)) {
                                    actualDates = jrcomponent1.getValue();
                                }
                            } else {
                                actualDates = (String) Actuacio.resourceMap.get(bean.getAction().getDates());
                            }
                            bean.getAction().setActualDates(actualDates);

                            if(actualDates.isEmpty())
                            {
                                    //System.out.println("Warning: "+getClass().getName()+" can't get dates from component "+jrcomponent1.getClass());
                                    return;
                            }
                           
                            //RegisterIESDIGITAL
                            if (bean.getAction().isRegisterIesDigital() ) {

                                PeriodParser parser = new PeriodParser(actualDates, coreCfg);
                                parser.doDBUpdate(act.exp2, act.id_actuacio, bean.getAction().getTipus());

                            }
                            //RegisterSGD
                            if (bean.getAction().isRegisterSGD() ) {
                                IncidenciesSGD1 incidenciesSgd1 = new IncidenciesSGD1(act.id_actuacio, act.exp2, bean.getAction(), coreCfg);
                                incidenciesSgd1.create();
                                SplashWindow3 splash = new SplashWindow3(incidenciesSgd1.getMessage(), null, 8000, new java.awt.Point(5, 200));
                                splash.setVisible(true);
                            }
                        }
               
                }
            }
        }
    }

    @Override
    public void addListenerDocButton(ActionListener listener) {
        jButton4.addActionListener(listener);
    }
    
     
    @Override
    public void setActuacio(Actuacio act) {
         this.act = act;     
         if(this.act.idFaltasAlumnos.isEmpty())
         {
             sgdButton1.setVisible(false);
         }
         jLabel1.setText(act.beanRule.getDescripcioLlarga());
         jLabel1.setToolTipText("idRule="+act.id_rule+"; idActuacio="+act.id_actuacio);
         if(act.beanRule.getAlert()==null || act.beanRule.getAlert().isEmpty())
         {
            jAlert.setVisible(false);
         }
         else{
            jAlert.setText(act.beanRule.getAlert());
         }
         
         
         
         //Botons per veure els reports
         if(getTotalVisibleReports()==0)
         {
            jPanel3.setVisible(false);
         }
         else 
         {
             modelCombo1 = new DefaultComboBoxModel();
             jComboBox1.setRenderer(new CustomCBRenderer());
             for(BeanFieldReport b: act.beanRule.getListReports())
             {
                 if(b.getVisibilitat().equals("*") || (b.getVisibilitat().equals("P") && act.admin))
                 {
                     modelCombo1.addElement(b);
                 }
             }
             jComboBox1.setModel(modelCombo1);
         }
          
         //Afegeix al tabbedPane l'ajuda i el reglament si no són nuls
         if(act.beanRule.getInstruccions()!=null && !act.beanRule.getInstruccions().isEmpty())
         {
          jTabbedPane1.addTab(bundle.getString("instruccions"), new javax.swing.ImageIcon(BasicForm.class.getResource("/org/iesapp/framework/actuacions/icons/help.gif")), jScrollPane2); // NOI18N
          jTextPane1.setText(act.beanRule.getInstruccions());
         }
         if(act.beanRule.getInstruccions()!=null && !act.beanRule.getInstruccions().isEmpty())
         {
          jTabbedPane1.addTab(bundle.getString("reglament"), new javax.swing.ImageIcon(BasicForm.class.getResource("/org/iesapp/framework/actuacions/icons/rof.gif")), jScrollPane1); // NOI18N
          jTextPane2.setText(act.beanRule.getReglament());
         }
         
         if(act.locked)
         {
             jTabbedPane1.setIconAt(0, new javax.swing.ImageIcon(BasicForm.class.getResource("/org/iesapp/framework/actuacions/icons/locked.gif")));
         }
         else
         {
             jTabbedPane1.setIconAt(0, new javax.swing.ImageIcon(BasicForm.class.getResource("/org/iesapp/framework/actuacions/icons/editable.gif")));             
         }
          
        jCheckBox1.setVisible(act.beanRule.getListReports()!=null && act.beanRule.getListReports().size()>0 && act.beanRule.getListReports().get(0).getLimitInc()>0);
         
        if(!act.beanRule.getListReports().isEmpty() && act.beanRule.getListReports().get(0).getLimitInc()>0)
        {
//              if(act.beanRule.getListReports().get(0).getIncludeSubReport().equals("F"))
//            {
//                incidencies = "faltes";
//            }
//            else if(act.beanRule.getListReports().get(0).getIncludeSubReport().equals("A"))
//            {
//                incidencies = "amonestacions";
//            }
//            else if(act.beanRule.getListReports().get(0).getIncludeSubReport().equals("R"))
//            {
//                incidencies = "retards";
//            }
            jCheckBox1.setText(bundle.getString("showup")+" " +act.beanRule.getListReports().get(0).getLimitInc()+" "+bundle.getString("incidencies"));
            jCheckBox1.setVisible(true);
        }

         
         this.createUI();
         pack();
         
    }

    /**
     * Problem: deletes params which are not displayable
     * @return 
     */
    @Override
    public HashMap<String, Object> getMap() {
          HashMap<String, Object> newmap = (HashMap<String, Object>) act.map.clone();
          
          for(String key: uiComponentsMap.keySet())
          {
              JRComponent jrComponent1 = uiComponentsMap.get(key);
              if(jrComponent1.isAddToMap())
              {
                newmap.put(key, jrComponent1.getValue());
              }
              //if the component is selectable then add selectableFieldName as well
              if(jrComponent1.isSelectable())
              {
                  newmap.put( jrComponent1.getSelectionFieldName(), jrComponent1.isSelected()?"X":"");
              }
          }
              
          return newmap;
    }
    
    /*
     * Basic checks based on Rule definition
     */
    @Override
    public String doCheck() {
        String errors = "";
        
        //Comprova tots els components (necessit tenir contruida la UI)
        if(uiComponentsMap==null) {
            return errors;
        }
        else
        {
            //First check all components which are not in a group
            for(JRComponent jrComponent1: uiComponentsMap.values())
            {
                boolean isPartOfGroup = false;
                for(String s: act.beanRule.getSelectableGroups())
                {
                    if(s.contains(jrComponent1.getId()))
                    {
                        isPartOfGroup = true;
                        break;
                    }
                }
            
                if(!isPartOfGroup || (isPartOfGroup && jrComponent1.isSelected()) )
                {
                
                boolean check = jrComponent1.check();
                 
                if(!check)
                {
                    errors += bundle.getString("required")+" "+ jrComponent1.getId()+";";                 
                }
                }
            }
            
            
            
        }
        
        errors += doExtraChecks();
        return errors;
    }
    
    /*
     * Overwrite this function to perform futher check problems
     */
    public String doExtraChecks()
    {
        return "";
    }
    
    @Override
    protected JRootPane createRootPane() {
    JRootPane rootPane2 = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
   
    InputMap inputMap = rootPane2.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(stroke, "ESCAPE");
    rootPane2.getActionMap().put("ESCAPE", (AbstractAction) exitListener);

    return rootPane2;
  }

    private void createUI() {
        uiComponentsMap = new HashMap<String,JRComponent>();
        Component currentPane = mainPane;
        for(BeanFieldSet bean: act.beanRule.getFields())
        {            
            try {
                if(bean.getFieldRenderClass()!=null && !bean.getFieldRenderClass().isEmpty())
                {
                 Class<?> loadClass = Class.forName(bean.getFieldRenderClass());
                 Object newInstance = loadClass.newInstance();
                 //Determina si és un component de javar.JR
                 //Els components de swing es poden emprar però no es posaran dins el mapa uiComponentsMap
                 if(bean.getFieldRenderClass().startsWith("javar.JR"))
                 {
                 //Crea una instancia i passa els parametres d'iniciacio
                 JRComponent jrComponent1 = (JRComponent) newInstance;
                 //System.out.println("Passing "+coreCfg+" coreCfg to compoennt");
                 jrComponent1.setCoreCfg(coreCfg);
                 if( bean.getFieldRenderClassParams()!=null) {
                        jrComponent1.setIniParams(bean.getFieldRenderClassParams());
                  }
                 jrComponent1.setValue((String) act.map.get(bean.getFieldName()));
                 jrComponent1.setLabel(bean.getFieldDescription());
                 jrComponent1.setEnable(!act.locked && (bean.getEditable().equals("S") || (bean.getEditable().equals("P") && act.admin)));
                 jrComponent1.setRequired(bean.isRequired());
                 jrComponent1.setId(bean.getFieldName());
                 jrComponent1.setAddToMap(bean.isAddToMap());
                  
                 if(bean.getSelectableField()!=null && !bean.getSelectableField().isEmpty())
                 {
                     
                    jrComponent1.setSelectable(true, bean.getSelectableField());
                    if(jrComponent1.isSelectable())
                    {
                        jrComponent1.setSelected(act.map.get(bean.getSelectableField())!=null &&
                                              !((String) act.map.get(bean.getSelectableField())).trim().isEmpty());
                    }    
                 }
                 
                 uiComponentsMap.put(bean.getFieldName(), jrComponent1);
                 }
                 
                 JComponent jcomponent = (JComponent) newInstance;
                 
                 if(bean.isVisible())
                 {
                    //System.out.println("Component "+bean.fieldName+" goes to "+bean.category);
                    if(bean.getCategory().isEmpty())
                    {
                        mainPane.add(jcomponent);
                    }
                    else
                    {
                        JTaskPaneGroup jtk1 = null;
                        for(Component comp: mainPane.getComponents())
                        {                 
                            if(comp.getClass().equals(JTaskPaneGroup.class) && comp.getName().equals(bean.getCategory()) )
                            {
                                        jtk1 = (JTaskPaneGroup) comp;
                                        break;
                            }
                        }
                        BeanCategory bcat = act.beanRule.getListCategories().get(bean.getCategory());
                        if(jtk1 == null)
                        {
                            jtk1 = new JTaskPaneGroup();                            
                            jtk1.setTitle(bcat.getTitle());
                            jtk1.setAnimated(false);
                            jtk1.setCollapsable(bcat.isCollapsable());
                            jtk1.setExpanded(!bcat.isCollapsed());
                            jtk1.setName(bean.getCategory());
                           
                            
                        }
                        jtk1.add(jcomponent);
                        mainPane.add(jtk1);
                        
                        
                    }
                 }
                }
            } catch (Exception ex) {
                Logger.getLogger(BasicForm.class.getName()).log(Level.SEVERE, null, ex);
            }   
            
        }
        
        //Comprova si hi ha components selectables que apareixen en group
        for(String g: act.beanRule.getSelectableGroups())
        {
            ArrayList<String> parsed = StringUtils.parseStringToArray(g, ",", StringUtils.CASE_INSENSITIVE);
            if(parsed.size()>1)
            {
                ButtonGroup bg = new ButtonGroup();
                for(String s2: parsed)
                {
                    //Cerca la id del component
//                    String selection = uiComponentsMap.get(s2).getSelectionFieldName();
//                    String selectedValue = (String) act.map.get(selection);
                     JRComponent comp = uiComponentsMap.get(s2);
                    if(comp!=null)
                    {
                        AbstractButton radioButton = comp.getRadioButton();
                        bg.add(radioButton);
                        //bg.setSelected(radioButton.getModel(), selectedValue!=null && !selectedValue.trim().isEmpty());
                    }
                    }
              
            }
        }
    }
 
    

    /**
     * @return the exitedCode
     */
    public byte getExitCode() {
        return exitedCode;
    }
    
    /**
     * Afegeix una accio als botons de report
     * @param idReport
     * @param listener 
     */
   
    public boolean isLimitador()
    {
        return jCheckBox1.isSelected();
    }
    
    public BeanFieldReport getSelectedReport()
    {
       // int row = jComboBox1.getSelectedIndex();
        return  (BeanFieldReport) jComboBox1.getSelectedItem();
    }


     public void addListenerCloseButtons(ActionListener actionListener)
     {
         this.exitListener = actionListener;      
         jButton2.addActionListener(actionListener);
     }
     
     public void addListenerAcceptButton(ActionListener actionListener)
     {
          jButton3.addActionListener(actionListener);
     }
             
  
    private int getTotalVisibleReports() {
        int total = 0;
        for(BeanFieldReport b: act.beanRule.getListReports())
        {
            if(b.getVisibilitat().equals("*") || (b.getVisibilitat().equals("P") && act.admin))
            {
                total += 1;
            }
        }
        return total;
    }

    /**
     * @return the generatedMainReport
     */
    public boolean isMainReportGenerated() {
        return generatedMainReport;
    }

    /**
     * @param generatedMainReport the generatedMainReport to set
     */
    public void setMainReportGenerated(boolean generatedMainReport) {
        this.generatedMainReport = generatedMainReport;
    }
}


