/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.pluggable.modulesAPI;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.iesapp.framework.pluggable.deamons.BeanDeamon;
import org.iesapp.framework.pluggable.pluginsAPI.BeanAnchorPoint;
import org.iesapp.framework.util.CoreCfg;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 *
 * @author Josep
 */
public class GenericFactory {
    
    protected String currentModuleClass;
    protected String currentPluginClass;
    protected String appClass;
    protected String requiredModuleName;
    protected File MODULESFILE;
    protected Document doc;
    protected Node currentModuleNode;
    protected Node currentPluginNode;
    public static final int PLUGIN=0;
    public static final int MODULE=1;
    private Node nodeApp;
    private String requiredJar;
    private static final String ENCODING= "ISO-8859-1";
    private ArrayList<BeanModule> loadModulesCache;
    private long lastModified;
    
    public GenericFactory(InputStream inputstream, int type)
    {
          String tag = "module";
          if (type == PLUGIN) {
              tag = "plugin";
          }
          try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();        
            DocumentBuilder b = f.newDocumentBuilder();
            
            doc = b.parse(inputstream);
            
          }
          catch(Exception ex)
          {
              ErrorDisplay.showMsg(ex.toString());
              //Logger.getLogger(GenericFactory.class.getName()).log(Level.SEVERE, null, ex);
          }
         
          NodeList app = doc.getElementsByTagName(tag);
          nodeApp = app.item(0);
    }
    
    
    public GenericFactory(String appClass, String requiredModuleClass, String requiredJar) throws ParserConfigurationException, SAXException, IOException
    {
          this.appClass = appClass;
          this.requiredModuleName = requiredModuleClass;
          this.requiredJar = requiredJar;
          MODULESFILE = new File(CoreCfg.contextRoot+File.separator+"config"+
                  File.separator+appClass.replaceAll("\\.","-")+".xml");  
          parseFile();
    }

    private void parseFile() throws ParserConfigurationException, SAXException, IOException
    {
          DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();         
          DocumentBuilder b = f.newDocumentBuilder();
 
            if(MODULESFILE.exists())
            {
                doc = b.parse(MODULESFILE);
            }
            else
            {
                doc = b.newDocument();                          
            }
            
            NodeList app = doc.getElementsByTagName("application");
            if(app.getLength()==0)
            {
                createBlankAppFile();    
                //Add requiredModule to xml
                appendRequiredModule();
                this.saveXmlDoc();
                app = doc.getElementsByTagName("application");
            }
            nodeApp = app.item(0);
    }
    
    public GenericFactory(File configfile) throws ParserConfigurationException, SAXException, IOException
    {
         // this.appClass = appClass;
         // this.requiredModuleName = requiredModuleClass;
          MODULESFILE = configfile;  
          
          
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();         
            DocumentBuilder b = f.newDocumentBuilder();

           
            if(MODULESFILE.exists())
            {
                doc = b.parse(MODULESFILE);
            }
            else
            {
                doc = b.newDocument();                          
            }
            
            
            NodeList app = doc.getElementsByTagName("application");
            if(app.getLength()==0)
            {
               nodeApp = null;
            }
            else
            {
                nodeApp = app.item(0);
            }
            
          
          
          
         
    }

    /***
     * Get application parameters     **/
    public String getApplicationAttribute(String key)
    {
        String value = null;
        Node namedItem = nodeApp.getAttributes().getNamedItem(key);
        if(namedItem!=null)
        {
            value = namedItem.getNodeValue();
            if(value!=null)
            {
                value = value.trim();
            }
        }
        
        //Default values for known attributes
        if(value==null && key.equalsIgnoreCase("display"))
        {
            value = "extended";
        }
        else if(value==null && key.equalsIgnoreCase("validateLogout"))
        {
            value = "no";
        }
        else if(value==null && key.equalsIgnoreCase("preventSwitchUser"))
        {
            value = "no";
        }
        else if(value==null && key.equalsIgnoreCase("showToolBar"))
        {
            value = "yes";
        }
        else if(value==null && key.equalsIgnoreCase("showStatusBar"))
        {
            value = "yes";
        }
        else if(value==null && key.equalsIgnoreCase("showMenuBar"))
        {
            value = "yes";
        }
        else if(value==null && key.equalsIgnoreCase("roles"))
        {
            value = "*";
        }
        else if(value==null && key.equalsIgnoreCase("users"))
        {
            value = "";
        }
       
        else if(value==null)
        {
            value = "";
        }
        
        return value;
    }
    
    
    /***
     * Set application parameters
     **/
    public void setApplicationAttribute(String key, String value)
    {
        ((Element) nodeApp).setAttribute(key, value);
    }
    
    /**
     * Additional initialization parameters
     * passed to the application
     */
    public HashMap<String,Property> getApplicationInitParameters()
    {
        HashMap<String, Property> map = new HashMap<String, Property>();
        NodeList nodes = ((Element) nodeApp.getChildNodes()).getElementsByTagName("initialization");
        for(int i=0; i<nodes.getLength(); i++)
        {
            
                Node subnode = nodes.item(i);
                NodeList parametersNodes = ((Element) subnode).getElementsByTagName("parameter");
                for (int j = 0; j < parametersNodes.getLength(); j++) {
                    Node parameter = parametersNodes.item(j);
                    
                    NamedNodeMap attributes = parameter.getAttributes();
                    String key = attributes.getNamedItem("key").getNodeValue();
                    Node namedItem0 = attributes.getNamedItem("type");
                    String type="";
                    if(namedItem0!=null)
                    {
                        type = namedItem0.getNodeValue();
                    }
                    
                    Node namedItem = attributes.getNamedItem("value");
                    String value = "";
                    if(namedItem!=null)
                    {
                        value = namedItem.getNodeValue();
                    }
                    Node namedItem2= attributes.getNamedItem("description");
                    String description = "";
                    if(namedItem2!=null)
                    {
                        description = namedItem2.getNodeValue();
                    }
                    //ADD CDATA SUPPORT for value and description
                    NodeList valueList = ((Element) parameter).getElementsByTagName("value");
                    NodeList descList = ((Element) parameter).getElementsByTagName("description");
                    if(valueList.getLength()>0)
                    {
                        Node firstChild = valueList.item(0).getFirstChild();
                        if (firstChild != null && firstChild instanceof CharacterData) {
                            value = ((CharacterData) firstChild).getData();
                        }
                    }
                    if(descList.getLength()>0)
                    {
                        Node firstChild = descList.item(0).getFirstChild();
                        if (firstChild != null && firstChild instanceof CharacterData) {
                            description = ((CharacterData) firstChild).getData();
                        }
                    }
                    Property property = new Property(key, value, type, description);
                    map.put(key, property);
                }
            
            
        }
        return map;
    }
    
   
     /**
     * Set parameter value
     * passed to the application
     */
    public boolean setApplicationInitParameter(String key, String value)
    {
        boolean changed  = false;
        NodeList nodes = ((Element) nodeApp).getElementsByTagName("initialization");
        for(int i=0; i<nodes.getLength(); i++)
        {
          if (nodes.item(i).getNodeName().equalsIgnoreCase("initialization")) {
            Node subnode = nodes.item(i);
            NodeList parametersNodes = ((Element) subnode.getChildNodes()).getElementsByTagName("parameter");
            for(int j=0; j<parametersNodes.getLength(); j++)
            {
                Node parameter = parametersNodes.item(j);
                NamedNodeMap attributes = parameter.getAttributes();
                if(attributes.getNamedItem("key").getNodeValue().equals(key))
                {
                    //attributes.getNamedItem("value").setNodeValue(value); --> set as CDATA
                    NodeList nodeList = ((Element) parameter).getElementsByTagName("value");
                    Node node = null;
                    if(nodeList.getLength()>0)
                    {
                        node = nodeList.item(0);
                    }
                    else
                    {
                        node = doc.createElement("value");
                        parameter.appendChild(node);
                    }
                    Node firstChild = node.getFirstChild();
                    if(firstChild!=null && firstChild instanceof CharacterData)
                    {
                        ((CharacterData) firstChild).setData(value);
                    }
                    else
                    {
                        node.appendChild(doc.createCDATASection(value));
                    }
                    changed = true;
                }                
            }  
            break;
          }
        }        
        return changed;
    }
    
    
    /**
     * blank with no modules within
     */
    private void createBlankAppFile() {
        Element element = doc.createElement("application");
        element.setAttribute("class", appClass);
        element.setAttribute("display", "extended");  //normal, extended, fullscreen
        element.setAttribute("preventSwitchUser", "no");
        element.setAttribute("validateLogout", "no");
        doc.appendChild(element);
        saveXmlDoc();
     }
      
    
    
     public Node getModuleNode(String name)
     {
        Node node = null;
        NodeList modules = doc.getElementsByTagName("module");
        for(int i=0; i<modules.getLength(); i++)
        {
             NamedNodeMap attributes = modules.item(i).getAttributes();
             //
             //System.out.println("Trying to find all modules "+modules.item(i)+"  "+attributes.getNamedItem("class")+"   "+currentModuleClass);
             if(attributes.getNamedItem("class").getNodeValue().equals(name))
             {
                 node = modules.item(i);
                 //System.out.println("FOUND currentModulenode is "+node+ "....#child "+node.getChildNodes().getLength());
                 break;
             }
        }
        //System.out.println("FOUND currentModulenode is "+node);
        return node;
     }
     
     public NodeList getModuleNodes()
     {        
        NodeList modules = doc.getElementsByTagName("module");        
        return modules;
     }
     
     public NodeList getPluginNodes()
     {            
        NodeList plugins = ((Element) currentModuleNode).getElementsByTagName("plugin");        
        return plugins;
     }
     
     public Node getNodeByClass(Node fromNode, String tagName, String namedItemName, String namedItemValue)
     {
         Node node = null;
         NodeList modules = fromNode.getChildNodes();
         for(int i=0; i<modules.getLength(); i++)
         {
             Node item = modules.item(i);
             if(item.getNodeName().equals(tagName))
             {
                 NamedNodeMap attributes = item.getAttributes();
                 Node namedItem = attributes.getNamedItem(namedItemName);
                 if (namedItem!=null && namedItem.getNodeValue().equals(namedItemValue)) {
                     node = item;
                     break;
                 }
             }             
         }
         return node;
     }
   
     public Node getBundleModuleNode()
     {
        return basicNodeFinder(currentModuleNode, "bundle", true);
     }
     
     public Node getInitializationModuleNode()
     {
        return basicNodeFinder(currentModuleNode, "initialization", true);
     }
     
     public Node getDependenciesModuleNode()
     {
        return basicNodeFinder(currentModuleNode, "dependencies", true);
     }
    
     public Node getCustomModuleNode(String nodeName)
     {
        return basicNodeFinder(currentModuleNode, nodeName, true);
     }
     
   
      public Node getBundlePluginNode()
     {
        return basicNodeFinder(currentPluginNode, "bundle", true);
     }
     
     public Node getInitializationPluginNode()
     {
        return basicNodeFinder(currentPluginNode, "initialization", true);
     }
     
     public Node getDependenciesPluginNode()
     {
        return basicNodeFinder(currentPluginNode, "dependencies", true);
     }
    
     public Node getCustomPluginNode(String nodeName)
     {
        return basicNodeFinder(currentPluginNode, nodeName, true);
     }
    
     
     public BeanModule genericLoader(Node fromNode, int type)
     {
        BeanModule bean = new BeanModule();
        String tag = "module";
        if (type == PLUGIN) {
            tag = "plugin";
            bean.setPlugin(true);
        }
     
        NamedNodeMap attributes1 = fromNode.getAttributes();
        bean.setAttributes(attributes1);

        NodeList childNodes = fromNode.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            //Parse anchor points
            if (childNodes.item(k).getNodeName().equalsIgnoreCase("anchor")) {
                BeanAnchorPoint bac = new BeanAnchorPoint();
                bac.setAttributes(childNodes.item(k).getAttributes());
                bean.listAnchorPoints.add(bac);
            }
            //Parse display point
            else if (childNodes.item(k).getNodeName().equalsIgnoreCase("display")) {
                 bean.displayPoint.setAttributes(childNodes.item(k).getAttributes());
                
            //Parse bundle properties
            } else if (childNodes.item(k).getNodeName().equalsIgnoreCase("bundle")) {
                NodeList childNodes1 = ((Element) childNodes.item(k)).getElementsByTagName("locale");
                for (int r = 0; r < childNodes1.getLength(); r++) {
                    
                        NamedNodeMap attributes2 = childNodes1.item(r).getAttributes();
                        Node namedItem = attributes2.getNamedItem("key");
                        if(namedItem!=null)
                        {
                            String key = namedItem.getNodeValue();
                            String value = attributes2.getNamedItem("value").getNodeValue(); //language 
                            Node namedItem1 = attributes2.getNamedItem("name"); //String
                            String name = "";
                            if(namedItem1!=null)
                            {
                                name = namedItem1.getNodeValue();
                            }
                            Node firstChild = childNodes1.item(r).getFirstChild();
                            if(firstChild!=null && firstChild instanceof CharacterData) //String from CDATA section
                            {
                                name = ((CharacterData) firstChild).getData();
                            }
                            if(key.equalsIgnoreCase("title")) //add to title map
                            {
                                bean.getModuleNameBundle().put(value.toLowerCase(), name);
                            }
                            else if(key.equalsIgnoreCase("description")) //add to description map
                            {
                                 bean.getBeanMetaINF().getDescriptionMap().put(value.toLowerCase(), name);
                            }
                        }
                       
                    
                       
                }
            
            //Parse dependencies
             } else if (childNodes.item(k).getNodeName().equalsIgnoreCase("dependencies")) {
                                 
                NodeList childNodes1 = childNodes.item(k).getChildNodes();
                bean.requiredLibs = UtilsFactory.nodeListToArray(childNodes1, "lib", "path");
                            
            //Parse META-INF
             } else if (childNodes.item(k).getNodeName().equalsIgnoreCase("metaINF")) {
                NamedNodeMap attributes = childNodes.item(k).getAttributes();
                Node author = attributes.getNamedItem("author");
                Node dependencies = attributes.getNamedItem("dependencies");
                Node version = attributes.getNamedItem("version");
                Node url = attributes.getNamedItem("url");
                Node minFrameworkVersion = attributes.getNamedItem("minFrameworkVersion");
                Node minClientID = attributes.getNamedItem("minClientIDVersion");
                Node minClientSGD = attributes.getNamedItem("minClientSGDVersion");
                
                if(version!=null)
                {
                     bean.getBeanMetaINF().setVersion(version.getNodeValue());
                }
                if(dependencies!=null)
                {
                     bean.getBeanMetaINF().setDependencies(dependencies.getNodeValue());
                }
                if(author!=null)
                {
                     bean.getBeanMetaINF().setAuthor(author.getNodeValue());
                }
                if(url!=null)
                {
                    bean.getBeanMetaINF().setUrl(url.getNodeValue());
                }
                if(minFrameworkVersion!=null)
                {
                    bean.getBeanMetaINF().setMinFrameworkVersion(minFrameworkVersion.getNodeValue());
                } 
                if(minClientID!=null)
                {
                    bean.getBeanMetaINF().setMinClientID(minClientID.getNodeValue());
                }
                if(minClientSGD!=null)
                {
                    bean.getBeanMetaINF().setMinClientSGD(minClientSGD.getNodeValue());
                }
                
            }
             
            //Parse ini-PARAMETERS
             else if (childNodes.item(k).getNodeName().equalsIgnoreCase("initialization")) {
                NodeList childNodes1 = childNodes.item(k).getChildNodes();
                bean.iniParameters = UtilsFactory.nodeListToObjectMap(childNodes1, bean.iniParametersDescription);
            }
            
            //Parse deamons (if any)
            else if (childNodes.item(k).getNodeName().equalsIgnoreCase("deamons")) {
                NodeList childNodes1 = ((Element)childNodes.item(k)).getElementsByTagName("deamon");
                for(int r=0; r<childNodes1.getLength(); r++)
                {
                    BeanDeamon deamon = new BeanDeamon();
                    deamon.setAttributes(childNodes1.item(r).getAttributes());
                    bean.getDeamons().add(deamon);
                }
                
            }
            
            //Parse plugins if any (recursively)
             else if(type==MODULE && childNodes.item(k).getNodeName().equalsIgnoreCase("plugins"))
             {
                NodeList childNodes1 = ( (Element) childNodes.item(k) ).getElementsByTagName("plugin");
                for(int r=0; r<childNodes1.getLength(); r++)
                {
                     BeanModule beanplugin = genericLoader(childNodes1.item(r), PLUGIN);
                     bean.getInstalledPlugins().add(beanplugin);
                }
                 
             }
        }
 
         return bean;
     }
     
     
     public ArrayList<BeanModule> loadModulesFromManifest(InputStream inputstream)
     {
         //Create a temporary factory
         GenericFactory tmp = new GenericFactory(inputstream, MODULE);
         ArrayList<BeanModule> list = new ArrayList<BeanModule>();
         NodeList moduleNodes = tmp.doc.getElementsByTagName("module");
         for(int i=0; i<moduleNodes.getLength(); i++)
         {
             list.add( tmp.genericLoader(moduleNodes.item(i), MODULE) );
         }
         return list;     
     }
     
     
     public ArrayList<BeanModule> loadPluginsFromManifest(InputStream inputstream)
     {
         //Create a temporary factory
         GenericFactory tmp = new GenericFactory(inputstream, PLUGIN);
         ArrayList<BeanModule> list = new ArrayList<BeanModule>();
         NodeList moduleNodes = tmp.doc.getElementsByTagName("plugin");
         for(int i=0; i<moduleNodes.getLength(); i++)
         {
             list.add( tmp.genericLoader(moduleNodes.item(i), PLUGIN) );
         }
         return list;     
     }
     
     public synchronized ArrayList<BeanModule> loadModulesCache()
     {
         MODULESFILE = new File(CoreCfg.contextRoot+File.separator+"config"+
                  File.separator+appClass.replaceAll("\\.","-")+".xml");  
         
         if(loadModulesCache==null || MODULESFILE.lastModified()!=lastModified)
         {
             try {
                 if(lastModified!=0)
                 {
                    parseFile();
                 }
                 loadModules();
             } catch (ParserConfigurationException ex) {
                 Logger.getLogger(GenericFactory.class.getName()).log(Level.SEVERE, null, ex);
             } catch (SAXException ex) {
                 Logger.getLogger(GenericFactory.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IOException ex) {
                 Logger.getLogger(GenericFactory.class.getName()).log(Level.SEVERE, null, ex);
             }
            
         }
         return loadModulesCache;
     }
     /**
      * Loaded modules are stored in cache
      * this method overrides the cache
      * use loadModulesCache() instead
      * @return 
      */
     public synchronized ArrayList<BeanModule> loadModules()
     {
         loadModulesCache = new ArrayList<BeanModule>();
         NodeList nodes = getModuleNodes();
         for(int i=0; i<nodes.getLength(); i++)
         {
             BeanModule loadModule = genericLoader(nodes.item(i), MODULE);
             loadModulesCache.add(loadModule);  
         }
         MODULESFILE = new File(CoreCfg.contextRoot+File.separator+"config"+
                  File.separator+appClass.replaceAll("\\.","-")+".xml");  
         lastModified = MODULESFILE.lastModified();
         return loadModulesCache;
     }
     
     
     public ArrayList<BeanModule> loadPlugins() {
         ArrayList<BeanModule> list = new ArrayList<BeanModule>();
         NodeList nodes = getPluginNodes();
         for(int i=0; i<nodes.getLength(); i++)
         {
             BeanModule loadModule = genericLoader(nodes.item(i), PLUGIN);
             list.add(loadModule);  
         }
         return list;
     }
      
     public BeanModule loadModule()
     {
         return genericLoader(currentModuleNode, MODULE);
     }   
     
     public void writeModule(BeanModule bip)
     {         
         genericWriter(nodeApp, bip, MODULE);
     }
    
    
     public void writePlugin(BeanModule bip)
     {
         genericWriter(getCustomModuleNode("plugins"), bip, PLUGIN);
     }
    
     public void genericWriter(Node fromNode, BeanModule bip, int type)
     {
        String tag = "module";
        if (type == PLUGIN) {
            tag = "plugin";
        }
        Element module = doc.createElement(tag);
        module.setAttribute("jar", bip.getJar());
        module.setAttribute("class", bip.getClassName());
        String cases = "no";
        if (bip.getAutoStart() == BeanModule.YES) {
            cases = "yes";
        } else if (bip.getAutoStart() == BeanModule.ONDEMAND) {
            cases = "onDemand";
        }
        module.setAttribute("autoStart", cases);
        module.setAttribute("multipleInstances", bip.multipleInstance ? "yes" : "no");
        module.setAttribute("closable", bip.closable ? "yes" : "no");
        module.setAttribute("enabled", bip.isEnabled() ? "yes" : "no");
        module.setAttribute("roles", bip.getRoles());
        if(bip.getUsers()!=null && !bip.getUsers().isEmpty())
        {
            module.setAttribute("users", bip.getUsers());
        }
        if(bip.getShortcut()!=null && !bip.getShortcut().isEmpty())
        {
            module.setAttribute("shortcut", bip.shortcut);
        }
        if(type==PLUGIN)
        {
            module.setAttribute("forModule", bip.forModule);
        }
        if(bip.getHelpSetJar()!=null)
        {
            module.setAttribute("helpSetJar", bip.getHelpSetJar());
        }
        if(bip.getHelpSet()!=null)
        {
            module.setAttribute("helpSet", bip.getHelpSet());
        }
        //add deamons point if any
        if(bip.getDeamons().size()>0)
        {
          Element deamons = doc.createElement("deamons");   
          for(BeanDeamon bd: bip.getDeamons())
          {
              Element deamon = doc.createElement("deamon"); 
              deamon.setAttribute("class", bd.getDeamonClassName());
              deamon.setAttribute("timeInMillis", bd.getTimeInMillis()+"");
              deamon.setAttribute("showMessage", bd.isShowMessage()?"yes":"no");
              deamon.setAttribute("activateIcon", bd.isActivateIcon()?"yes":"no");
              deamon.setAttribute("enabled", bd.isEnabled()?"yes":"no");
              deamon.setAttribute("activateModule", bd.isActivateModule()?"yes":"no");
              deamons.appendChild(deamon);
          }
          module.appendChild(deamons);
         }
        
        //add display point
        Element display = doc.createElement("display");
        display.setAttribute("location", bip.getDisplayPoint().getLocation());
        display.setAttribute("parentId", bip.getDisplayPoint().getParentId());
        module.appendChild(display);

        for (int ik = 0; ik < bip.getListAnchorPoints().size(); ik++) {
            BeanAnchorPoint bap = bip.getListAnchorPoints().get(ik);
            Element anchor = doc.createElement("anchor");
            anchor.setAttribute("location", bap.getLocation());
            anchor.setAttribute("parentId", bap.getParentId());
            module.appendChild(anchor);
        }

        //Add bundle
        Element bundle = doc.createElement("bundle");
           //Add title to bundle
            for (String ky : bip.getModuleNameBundle().keySet()) {
                Element lang = doc.createElement("locale");
                lang.setAttribute("value", ky);
                //lang.setAttribute("name",); --> set as CDATA section     
                lang.setAttribute("key", "title");
                lang.appendChild( doc.createCDATASection(bip.getModuleNameBundle().get(ky)) );
                bundle.appendChild(lang);
            }
            //Add description to bundle
          for (String ky : bip.getBeanMetaINF().getDescriptionMap().keySet()) {
                Element lang = doc.createElement("locale");
                lang.setAttribute("value", ky);
                //lang.setAttribute("name",); --> set as CDATA section     
                lang.setAttribute("key", "description");
                lang.appendChild( doc.createCDATASection(bip.getBeanMetaINF().getDescriptionMap().get(ky)) );
                bundle.appendChild(lang);
            }
        
        module.appendChild(bundle);
      
        //appends required libs if any
        if(bip.getRequiredLibs().size()>0)
        {
            Element dependencies = doc.createElement("dependencies");
            for (String s : bip.getRequiredLibs()) {
                Element lib = doc.createElement("lib");
                lib.setAttribute("path", s);
                dependencies.appendChild(lib);
            }
            module.appendChild(dependencies);
        }

        //appends meta-inf
        
            Element createElement = doc.createElement("metaINF");
            createElement.setAttribute("author",
                        escape(bip.getBeanMetaINF().getAuthor()));
            createElement.setAttribute("version",
                        escape(bip.getBeanMetaINF().getVersion()));
            createElement.setAttribute("dependencies",
                        escape(bip.getBeanMetaINF().getDependencies()));
            createElement.setAttribute("url",
                        escape(bip.getBeanMetaINF().getUrl()));
            createElement.setAttribute("minFrameworkVersion",
                        escape(bip.getBeanMetaINF().getMinFrameworkVersion()));
            createElement.setAttribute("minClientIDVersion",
                        escape(bip.getBeanMetaINF().getMinClientID()));
            createElement.setAttribute("minClientSGDVersion",
                        escape(bip.getBeanMetaINF().getMinClientSGD()));
            module.appendChild(createElement);
        

        //appends ini-parameters (if any)
        if (!bip.getIniParameters().isEmpty()) {
            Element createElement2 = UtilsFactory.objectMapToNodeList(doc, "initialization", bip.getIniParameters(), bip.getIniParametersDescription());
            module.appendChild(createElement2);
        }

        
        //if this is a module, it must incorporate their plugins
        if(type==MODULE && bip.getInstalledPlugins().size()>0)
        {
             Element createElement2 = doc.createElement("plugins");
             module.appendChild(createElement2);
             for(int k=0; k<bip.getInstalledPlugins().size(); k++)
             {
                genericWriter(createElement2, bip.getInstalledPlugins().get(k), PLUGIN);
             }
        }
            
        
        fromNode.appendChild(module);

     }

    private Node basicNodeFinder(Node fromNode, String nodeName, boolean create) {
        Node node = null;
        NodeList childNodes = fromNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeName().equals(nodeName)) {
                node = item;
                break;
             }
         }
         if(create && node==null)
         {
             Element createElement = doc.createElement(nodeName);
             fromNode.appendChild(createElement);
             node = createElement;
         }
         return node;
     }
      
    public void createRequiredModule()
    {
       
        try {
            //Try to find the jar containing this class
            //and add the default/required module for this application
            
            //Must include the requiredModuleJar in the classpath
            Class<?> moduleClass = Class.forName(requiredModuleName);
            //System.out.println("requiredmodule :"+requiredModuleName);
            CodeSource codeSource = moduleClass.getProtectionDomain().getCodeSource();
            String tmp = codeSource.getLocation().toURI().getPath();
            InputStream inputStream = null;
            ZipFile zipfile = null;
            if(!tmp.endsWith(".jar"))
            {
                //try to see if we are executing the module from the same jar
                 inputStream = moduleClass.getResourceAsStream("/META-INF/module.xml");
            }
            else
            {
                File file = new File(tmp);
                //System.out.println("found jar file :"+file.getAbsolutePath());
                zipfile = new ZipFile(file);
                ZipEntry entry = zipfile.getEntry("META-INF/module.xml");
                inputStream = zipfile.getInputStream(entry);
                
            }
            if(inputStream!=null)
            {
                ArrayList<BeanModule> manifestModules = this.loadModulesFromManifest(inputStream);
                for(BeanModule bean: manifestModules)
                {
                    genericWriter(nodeApp, bean, MODULE);
                }
                inputStream.close();                
                if(zipfile!=null) {
                    zipfile.close();
                }
                //Overwrite module according the needs of requiredModule
                Element element = (Element) currentModuleNode;
                element.setAttribute("autoStart", "yes");
                element.setAttribute("closable", "no");
                element.setAttribute("roles", "*");
              
            }
        } catch (Exception ex) {
            //System.out.println(ex);
        }

   }
    
    
//    public void saveXmlDoc() {
//        try {           
//            TransformerFactory tranFactory = TransformerFactory.newInstance();
//            //tranFactory.setAttribute("indent-number", new Integer(2));
//            
//            Transformer aTransformer = tranFactory.newTransformer();
//            aTransformer.setParameter(OutputKeys.INDENT, "yes");
//            aTransformer.setParameter(OutputKeys.METHOD, "xml");
//            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//            aTransformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
//            aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//           
//           
//            Writer out = new OutputStreamWriter(new FileOutputStream(MODULESFILE), "ISO-8859-1");
//            
//            Source src = new DOMSource(doc);
//            StreamResult dest = new StreamResult(out);
//            aTransformer.transform(src, dest);
//             
//            out.flush();
//            out.close();
//        } catch (Exception ex) {
//            Logger.getLogger(GenericFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    public final void saveXmlDoc() {
        try {
            OutputFormat format = new OutputFormat(doc);
            format.setIndenting(true);
            format.setOmitXMLDeclaration(false);
            format.setOmitDocumentType(false);
//            //System.out.println("Setting encoding to "+doc.getXmlEncoding());
//            
             
//            if (doc.getXmlEncoding() != null) {
//                
//                format.setEncoding(doc.getXmlEncoding());
//            }
            
            format.setEncoding(ENCODING);
            Writer outputwriter = new OutputStreamWriter(new FileOutputStream(MODULESFILE), ENCODING);
            XMLSerializer serializer = new XMLSerializer(outputwriter, format);
            serializer.serialize(doc);
            outputwriter.close();   
        } catch (IOException ex) {
            Logger.getLogger(GenericFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public String getCurrentModuleClass() {
        return currentModuleClass;
    }

    public boolean setCurrentModuleClass(String currentModuleClass) {
        this.currentModuleClass = currentModuleClass;
        this.currentModuleNode = getModuleNode(this.currentModuleClass);
        return currentModuleNode!=null; 
    }

    public String getCurrentPluginClass() {
        return currentPluginClass;
    }

    public boolean setCurrentPluginClass(String currentPluginClass) {
        this.currentPluginClass = currentPluginClass;
        Node from = getCustomModuleNode("plugins");
        this.currentPluginNode = getNodeByClass(from, "plugin", "class", currentPluginClass);
        return currentPluginNode!=null;
    }

    public void setModuleAttribute(String attName, String attValue) {
        if(currentModuleNode!=null)
        {
          ((Element) currentModuleNode).setAttribute(attName, attValue);
        }
    }

    public void setPluginAttribute(String attName, String attValue) {
        if(currentPluginNode!=null)
        {
            ((Element) currentPluginNode).setAttribute(attName, attValue);
        }
    }

    public void setModuleInitializationAttribute(String key, String value) {
        Node nodeInitialization = getCustomModuleNode("initialization");
        Node nodeByClass = getNodeByClass(nodeInitialization, "parameter", "key", key);
        //((Element) nodeByClass).setAttribute("value", value); --> Set as CDATA
        NodeList list = ((Element) nodeByClass).getElementsByTagName("value");
        Node firstChild = null;
        if(list.getLength()>0)
        {
            firstChild = list.item(0);
        }
        if(firstChild!=null)
        {
            Node firstChild1 = firstChild.getFirstChild();
            if(firstChild1!=null && firstChild1 instanceof CharacterData)
            {
                ((CharacterData) firstChild1).setData(value);
            }
            else
            {
                firstChild.appendChild(doc.createCDATASection(value));
            }
        }
        else
        {
            Element valueNode = doc.createElement("value");
            valueNode.appendChild(doc.createCDATASection(value));
            nodeByClass.appendChild(valueNode);
        }
    }
    
     public void setPluginInitializationAttribute(String key, String value) {
        Node nodeInitialization = getCustomPluginNode("initialization");
        Node nodeByClass = getNodeByClass(nodeInitialization, "parameter", "key", key);
       // ((Element) nodeByClass).setAttribute("value", value); --> AS CDATA
        Node firstChild = nodeByClass.getFirstChild();
        if(firstChild!=null && firstChild instanceof CharacterData)
        {
            ((CharacterData) firstChild).setData(value);
        }
        else
        {
            nodeByClass.appendChild(doc.createCDATASection(value));
        }
    }

    private void appendRequiredModule() {
        if(requiredModuleName==null)
        {
            return;
        }
        try {
            //We first need to add to classpath
            org.iesapp.framework.util.JarClassLoader.getInstance().addToClasspath(new File(CoreCfg.contextRoot+"\\modules\\"+requiredJar));
           
            Class<?> forName = Class.forName(requiredModuleName, false, org.iesapp.framework.util.JarClassLoader.getInstance());
            URL location = forName.getProtectionDomain().getCodeSource().getLocation();
            File file = new File(location.toURI());
            InputStream stream = null;
            if(!file.getAbsolutePath().endsWith(".jar"))
            {
                //Try as local resource
                stream = GenericFactory.class.getResourceAsStream("/META-INF/module.xml");
            }
            else
            {
                //open jar and read stream
                ZipFile jar = new ZipFile(file);
                ZipEntry entry = jar.getEntry("META-INF/module.xml");
                stream = jar.getInputStream(entry);
            }
            
            if(stream!=null)
            {
                GenericFactory tmp = new GenericFactory(stream, MODULE);
                BeanModule loadModule = tmp.genericLoader(tmp.nodeApp, MODULE);
                NodeList app = doc.getElementsByTagName("application");
                nodeApp = app.item(0);
                //Since this must be required must autostart and be non-closable
                loadModule.setAutoStart(BeanModule.YES);
                loadModule.setClosable(false);
               
                this.genericWriter(nodeApp, loadModule, MODULE);
                
            }
        } catch (Exception ex) {
            Logger.getLogger(GenericFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Node getAppNode() {
        return nodeApp;
    }

    public void setModuleMetaINF(String key, String value) {
        if(currentModuleNode!=null)
        {
            Node customModuleNode = this.getCustomModuleNode("metaINF");
            if(customModuleNode!=null)
            {
                ((Element) customModuleNode).setAttribute(key, value);
            }
        }
    }

    public void setPluginMetaINF(String key, String value) {
        if (currentPluginNode != null) {
            Node customModuleNode = this.getCustomPluginNode("metaINF");
            if (customModuleNode != null) {
                ((Element) customModuleNode).setAttribute(key, value);
            }
        }
    }

    public void removeModuleClass(String moduleClassName) {
        NodeList modules = doc.getElementsByTagName("module");
        for(int i=0; i<modules.getLength(); i++)
        {
             Node item = modules.item(i);
             NamedNodeMap attributes = item.getAttributes();
             if(attributes.getNamedItem("class").getNodeValue().equals(moduleClassName))
             {
                 nodeApp.removeChild(item);
                 break;
             }
        }
    }

    public void removePluginClass(String pluginClass) {
        //from currentModuleNode
        //System.out.print("in remove plugin currentModuleNode is " + currentModuleClass + "  " + currentModuleNode);
        NodeList tmp = ((Element) currentModuleNode).getElementsByTagName("plugins");
        for(int j=0; j<tmp.getLength(); j++)
        {
        NodeList plugins = ((Element) tmp.item(j)).getElementsByTagName("plugin");
        for (int i = 0; i < plugins.getLength(); i++) {
            Node item = plugins.item(i);
            NamedNodeMap attributes = item.getAttributes();

            //System.out.println("i=" + i + "   attributes " + attributes);
            if (attributes.getNamedItem("class").getNodeValue().equals(pluginClass)) {
                tmp.item(j).removeChild(item);
                break;
            }
        }
        }
    }

    public void setModuleDeamonAttribute(String deamonClass, String key, String value) {
        //Get the current module node
        NodeList childNodes = currentModuleNode.getChildNodes();
        for(int i = 0; i< childNodes.getLength(); i++)
        {
            Node item = childNodes.item(i);
            if(item.getNodeName().equals("deamons"))
            {
                NodeList childNodes1 = item.getChildNodes();
                for(int j=0; j<childNodes1.getLength(); j++)
                {
                    Node item1 = childNodes1.item(j);
                    if(item1.getNodeName().equals("deamon"))
                    {
                        NamedNodeMap attributes = item1.getAttributes();
                        Node namedItem = attributes.getNamedItem("class");
                        if(namedItem!=null && namedItem.getNodeValue().equals(deamonClass))
                        {
                           ((Element) item1).setAttribute(key, value);  
                            break;
                        }
                        
                    }
                }
                    
                break;
            }
        }
    }

    public void removeModuleAttribute(String key) {
        ((Element) currentModuleNode).removeAttribute(key);
    }

    private String escape(String str) {
        return org.apache.commons.lang3.StringEscapeUtils.escapeXml(str);
    }

    //The module 1 must be moved before 2
    public void moveModuleBefore(String moduleName, String moduleNameBefore) {
        Node customModuleNode = this.getModuleNode(moduleName);
        Node customModuleNodeCopy = customModuleNode.cloneNode(true);
        Node customModuleNode1 = this.getModuleNode(moduleNameBefore);
        
        this.nodeApp.insertBefore(customModuleNodeCopy, customModuleNode1);
        this.nodeApp.removeChild(customModuleNode);
    }

    //The module 1 must be moved after 2
    public void moveModuleAfter(String moduleName, String moduleNameAfter) {
        Node customModuleNode = this.getModuleNode(moduleName);
        Node customModuleNode1 = this.getModuleNode(moduleNameAfter);
        Node customModuleNode1Copy = customModuleNode1.cloneNode(true);
    
        this.nodeApp.insertBefore(customModuleNode1Copy, customModuleNode);
        this.nodeApp.removeChild(customModuleNode1);
    }
    
     
}
