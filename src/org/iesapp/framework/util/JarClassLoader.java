/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.hibernate.util.ReflectHelper;
import org.iesapp.framework.pluggable.modulesAPI.BeanModule;

/**
 *
 * @author Josep
 */
public class JarClassLoader extends URLClassLoader{
    //private final  URLClassLoader sysLoader;  //Extends URLClassLoader
    private final  ArrayList<String> registeredDirs = new ArrayList<String>();
    private static JarClassLoader mainInstance;
    
    /**
     * Parent ClassLoader passed to this constructor
     * will be used if this ClassLoader can not resolve a
     * particular class.
     *
     * @param parent Parent ClassLoader
     *              (may be from getClass().getClassLoader())
     */
    private JarClassLoader(ClassLoader parent)
    {
        super(new URL[]{}, parent);
        //this.sysLoader = (URLClassLoader) parent;   
     }
    
    public void addToClasspath(File file)
    {
        if(file==null || !file.exists())
        {
            return;
        }
        
        if(file.isFile()) 
        {
            this.addJarToClasspath(file);
        }
        else if(file.isDirectory()) 
        {
            this.addDirToClasspath(file);
        }   
    }
    
    private void addJarToClasspath(File jar)
    {
        try {
           addURLReflection(jar.toURI().toURL());
        } catch (MalformedURLException ex) {
            Logger.getLogger(JarClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    private void addDirToClasspath(File directory)
    {
        if(!registeredDirs.contains(directory.getAbsolutePath()))
        {
            registeredDirs.add(directory.getAbsolutePath());
            FilenameFilter filter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                   return name.toLowerCase().endsWith(".jar");
                }
            };
            File[] files = directory.listFiles(filter);
            for(File f: files)
            {
                this.addJarToClasspath(f);
            }
        } 
    }

    private void addURLReflection(URL u) {
        URL urls[] = this.getURLs();
        for(URL url: urls)
        {
            if(url.toString().equalsIgnoreCase(u.toString()))
            {
                //System.out.println("Error "+u.toString()+ " already exists in classpath");
                return;
            }
        }
        
        //Access the private method through reflection
        Class sysclass = URLClassLoader.class;
        
        try {
            Method method;
            method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(this, new Object[]{u});
            //System.out.println("added to classpath: "+u.toString());
        } catch (Exception ex) {
            Logger.getLogger(JarClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Closes this current class loader
     */
    public void close() {
        try {
            Class clazz = java.net.URLClassLoader.class;
            java.lang.reflect.Field ucp = clazz.getDeclaredField("ucp");
            ucp.setAccessible(true);
            Object sun_misc_URLClassPath = ucp.get(this);
            java.lang.reflect.Field loaders
                    = sun_misc_URLClassPath.getClass().getDeclaredField("loaders");
            loaders.setAccessible(true);
            Object java_util_Collection = loaders.get(sun_misc_URLClassPath);
            for (Object sun_misc_URLClassPath_JarLoader
                    : ((java.util.Collection) java_util_Collection).toArray()) {
                try {
                    java.lang.reflect.Field loader
                            = sun_misc_URLClassPath_JarLoader.getClass().getDeclaredField("jar");
                    loader.setAccessible(true);
                    Object java_util_jar_JarFile
                            = loader.get(sun_misc_URLClassPath_JarLoader);
                    ((java.util.jar.JarFile) java_util_jar_JarFile).close();
                } catch (Throwable t) {
                    // if we got this far, this is probably not a JAR loader so skip it
                }
            }
        } catch (Throwable t) {
            // probably not a SUN VM
        }
         
    }

    /**
     * cleanup jar file factory cache
     */
    @SuppressWarnings({"nls", "unchecked"})
    public boolean cleanupJarFileFactory(List setJarFileNames2Close) {
        boolean res = false;
        Class classJarURLConnection = null;
        try {
            classJarURLConnection = ReflectHelper.classForName("sun.net.www.protocol.jar.JarURLConnection");
        } catch (ClassNotFoundException e) {
            //System.out.println(e);
        }
        if (classJarURLConnection == null) {
            return res;
        }
        Field f = null;
        try {
            f = classJarURLConnection.getDeclaredField("factory");
        } catch (NoSuchFieldException e) {
            //ignore
        }
        if (f == null) {
            return res;
        }
        f.setAccessible(true);
        Object obj = null;
        try {
            obj = f.get(null);
        } catch (IllegalAccessException e) {
            //ignore
        }
        if (obj == null) {
            return res;
        }
        Class classJarFileFactory = obj.getClass();
        //
        HashMap fileCache = null;
        try {
            f = classJarFileFactory.getDeclaredField("fileCache");
            f.setAccessible(true);
            obj = f.get(null);
            if (obj  instanceof HashMap) {
                  fileCache = (HashMap) obj;
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
            //ignore
        }
        //System.out.println("fileCache "+fileCache.toString());
        HashMap urlCache = null;
        try {
            f = classJarFileFactory.getDeclaredField("urlCache");
            f.setAccessible(true);
            obj = f.get(null);
            if (obj  instanceof HashMap) {
                urlCache = (HashMap) obj;   
            }

        } catch (Exception e) {
            //System.out.println(e);
        }
        //System.out.println("urlCache "+urlCache.toString());
        if (urlCache != null) {
            HashMap urlCacheTmp = (HashMap) urlCache.clone();
            Iterator it = urlCacheTmp.keySet().iterator();
            while (it.hasNext()) {
                obj = it.next();
                if (!(obj instanceof  JarFile) )
                {
                    continue;
                }
                JarFile jarFile = (JarFile) obj;
                //System.out.println("found jarFile "+jarFile.getName());
                if (setJarFileNames2Close.contains(jarFile.getName())) {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        //ignore
                    }
                    if (fileCache != null) {
                        fileCache.remove(urlCache.get(jarFile));
                    }
                    urlCache.remove(jarFile);
                }
            }
            res = true;
        } else if (fileCache != null) {
            // urlCache := null
            HashMap fileCacheTmp = (HashMap) fileCache.clone();
            Iterator it = fileCacheTmp.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                obj = fileCache.get(key);
                if (!(obj instanceof  JarFile) )
                {
                    continue;
                }
                JarFile jarFile = (JarFile) obj;
                //System.out.println("found jarFile "+jarFile.getName());

                if (setJarFileNames2Close.contains(jarFile.getName())) {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        //ignore
                    }
                    fileCache.remove(key);
                }
            }
            res = true;
        }
        setJarFileNames2Close.clear();
        return res;
    }

    /**
     * Creates another classLoader that has the initial object as parent
     * It will import jars as specified in module bean
     * @param bean
     * @return 
     */
    public JarClassLoader getSubInstance(BeanModule bean)
    {
        JarClassLoader subInstance = new JarClassLoader(this);
        File moduleJar = new File(CoreCfg.contextRoot + File.separator + "modules" + File.separator + bean.getJar());
        subInstance.addJarToClasspath(moduleJar);
        //Add any possible dependencies
        for (String file : bean.getRequiredLibs()) {
            File libjar = new File(CoreCfg.contextRoot + File.separator + file);
            subInstance.addJarToClasspath(libjar);
        }
        
        return subInstance;
    }
    
    
    public static JarClassLoader getInstance()
    {
        if(mainInstance==null)
        {
            mainInstance = new JarClassLoader(ClassLoader.getSystemClassLoader());
        }
        return mainInstance;
    }
    
}
