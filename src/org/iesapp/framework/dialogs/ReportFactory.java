/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import org.iesapp.framework.util.CoreCfg;

/**
 *
 * @author Josep
 */
public class ReportFactory {
   
      private static LongTask task;
      private Object printInstance;
      private Wait dlg;
      private Timer timer;
      private final int AnimationRate=1000;
      private final static String PATH = "reports";
      private final static String EXT = ".jasper"; 
      private ActionListener listener;
      private Object newViewerInstance;
      private String suitableTitle;
      private static boolean loaded = false;
  
      public ReportFactory() {
        //S'assegura que els reports estan en el classPath
        if(!loaded)
        {
            org.iesapp.framework.util.JarClassLoader.getInstance().addToClasspath(new java.io.File(CoreCfg.contextRoot + "\\lib\\"+PATH));
        }
        loaded = true;
        
        
        timer = new javax.swing.Timer(AnimationRate, new ActionListener() {
             
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dlg.stop) //must stop
                {
                    task.interrupt();
                    dlg.dispose();
                    timer.stop();
                }

                if (task != null && task.isAlive()) {
                    // it is running
                } else {
                    try {
                        dlg.dispose();
                        if(printInstance==null)
                        {
                            return;
                        }

                        suitableTitle = task.m_path;
                        //This to show an internalFrame
                        Class<?> classJRViewer = Class.forName("net.sf.jasperreports.swing.JRViewer");
                        Class<?> classJasperPrint = Class.forName("net.sf.jasperreports.engine.JasperPrint");
                        Constructor<?> constructor = classJRViewer.getConstructor(classJasperPrint);
                        newViewerInstance = constructor.newInstance(printInstance);
                        Method method = classJRViewer.getMethod("setZoomRatio", float.class);
                        method.invoke(newViewerInstance, 0.75f);
 
                        listener.actionPerformed(e);
                        
                        
                        timer.stop();
                    } catch (Exception ex) {
                        Logger.getLogger(ReportFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
      }
      
      public void setReportGeneratedListener(ActionListener listener)
      {
          this.listener = listener;
      }
      
      public Object getGeneratedReport()
      {
          return this.newViewerInstance;
      }
      
      public String getSuitableTitle()
      {
          return this.suitableTitle;
      }
      
      public void generateReport()
      {
        if(task!=null && task.isAlive()) {
           //can't start, there is an already running task
           return;
        } 
        timer.start();
        dlg = new Wait();
        dlg.setLocationRelativeTo(null);
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true); 
        if(task!=null) {
            task.start();
        }
    }

    public void customReport(List list, Map map, String filename) {
        
        File f = new File(CoreCfg.contextRoot+File.separator+PATH+File.separator+filename+EXT);
        String path = f.getAbsolutePath();
        task = new LongTask(path, list, map);
        
    }

    class LongTask extends Thread
    {
        private final String m_path;
        private final Map m_map;
        private List m_aux=null;
     
        private LongTask(String path, List list, Map map) {
           m_path = path;
           m_map = map;
           m_aux = list;
        }

        @Override
        public void run()
        {
            try {
                printInstance = null;
                //crea la base de dades
                Class<?> classDataSource = Class.forName("net.sf.jasperreports.engine.data.JRBeanCollectionDataSource");
                Constructor<?> constructor = classDataSource.getConstructor(Collection.class);
                Object instanceDs = constructor.newInstance(m_aux);
                
                
                //JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(m_aux);
                
                // Recuperamos el fichero fuente
                //JasperDesign jd = JRXmlLoader.load(m_path);
                // Compilamos el informe jrxml
                //JasperReport report = JasperCompileManager.compileReport(jd);
                // Rellenamos el informe con la conexion creada y sus parametros establecidos

                //carrega directament el fitxer compilat
                //System.out.println("carregant "+m_path);
                Class<?> classJasperReport = Class.forName("net.sf.jasperreports.engine.JasperReport");
                Class<?> classJasperLoader = Class.forName("net.sf.jasperreports.engine.util.JRLoader");
                Method methodLoader = classJasperLoader.getMethod("loadObject", java.io.File.class);
                Object instanceReport = methodLoader.invoke(null, new java.io.File(m_path));
               
                
               // JasperReport report = (JasperReport) JRLoader.loadObject( new java.io.File(m_path));

                //A qualsevol report li pas el directori complet
                m_map.put("SUBREPORT_DIR",CoreCfg.contextRoot+"\\reports\\");
             
                Class<?> classJRDataSource = Class.forName("net.sf.jasperreports.engine.JRDataSource");
                Class<?> classJasperFillManager = Class.forName("net.sf.jasperreports.engine.JasperFillManager");
                Method method1 = classJasperFillManager.getMethod("fillReport", new Class[]{classJasperReport, Map.class, classJRDataSource});
                printInstance = method1.invoke(null, new Object[]{instanceReport, m_map, instanceDs});
                //print = JasperFillManager.fillReport(report, m_map, ds);
                //JasperExportManager.exportReportToPdfFile(print, REPORT_EXPORT_PATH);
                //JasperExportManager.exportReportToPdfFile(print, REPORT_EXPORT_PATH);
            } catch (Exception ex) {
                Logger.getLogger(ReportFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }


    public static Object createJRBeanCollectionDataSource(List list) {
        if(!loaded)
        {
            org.iesapp.framework.util.JarClassLoader.getInstance().addToClasspath(new java.io.File(CoreCfg.contextRoot + "\\lib\\"+PATH));
        }
        Object obj = null;
        try {
            Class<?> classDataSource;
            classDataSource = Class.forName("net.sf.jasperreports.engine.data.JRBeanCollectionDataSource");
            Constructor<?> constructor = classDataSource.getConstructor(Collection.class);
            obj = constructor.newInstance(list);
           
        } catch (Exception ex) {
            Logger.getLogger(ReportFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }
    
}
