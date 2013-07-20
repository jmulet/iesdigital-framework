/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Josep
 */
public class JarClassLoader extends ClassLoader{
    private final URLClassLoader sysLoader;  //Extends URLClassLoader
    private final ArrayList<String> registeredDirs = new ArrayList<String>();
    private static JarClassLoader instance;
    
    /**
     * Parent ClassLoader passed to this constructor
     * will be used if this ClassLoader can not resolve a
     * particular class.
     *
     * @param parent Parent ClassLoader
     *              (may be from getClass().getClassLoader())
     */
    public JarClassLoader(ClassLoader parent)
    {
        super(parent);
        this.sysLoader = (URLClassLoader) parent;
        
    }
    
    public void addJarToClasspath(File jar)
    {
        if(jar.isFile() && jar.exists()) 
        {
        try {
            //System.out.println("triyng to add "+f.toURI().toURL());
            String key = jar.getAbsolutePath();
//            registry.put(key, jar.toURI().toURL());
            addURL(jar.toURI().toURL());
        } catch (Exception ex) {
            Logger.getLogger(JarClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
    
    public void addDirToClasspath(File directory)
    {
        
        if(directory.isDirectory() && directory.exists() && !registeredDirs.contains(directory.getAbsolutePath()))
        {
            registeredDirs.add(directory.getAbsolutePath());
            File[] files = directory.listFiles();
            for(File f: files)
            {
                try {
                    //System.out.println("triyng to add "+f.toURI().toURL());
                       String key = f.getAbsolutePath();
//                       registry.put(key, f.toURI().toURL());
                       addURL(f.toURI().toURL());
                    
                } catch (Exception ex) {
                    Logger.getLogger(JarClassLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else
        {
            //System.out.println("Error directory doesnt exists "+directory);
        }
    }

    private void addURL(URL u) {
        //URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        URL urls[] = sysLoader.getURLs();
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
            method.invoke(sysLoader, new Object[]{u});
            //System.out.println("added to classpath: "+u.toString());
        } catch (Exception ex) {
            Logger.getLogger(JarClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    
//    
//    public void close() {
//        try {
//            Class clazz = java.net.URLClassLoader.class;
//            java.lang.reflect.Field ucp = clazz.getDeclaredField("ucp");
//            ucp.setAccessible(true);
//            Object sun_misc_URLClassPath = ucp.get(this);
//            java.lang.reflect.Field loaders =
//                    sun_misc_URLClassPath.getClass().getDeclaredField("loaders");
//            loaders.setAccessible(true);
//            Object java_util_Collection = loaders.get(sun_misc_URLClassPath);
//            for (Object sun_misc_URLClassPath_JarLoader :
//                    ((java.util.Collection) java_util_Collection).toArray()) {
//                try {
//                    java.lang.reflect.Field loader =
//                            sun_misc_URLClassPath_JarLoader.getClass().getDeclaredField("jar");
//                    loader.setAccessible(true);
//                    Object java_util_jar_JarFile =
//                            loader.get(sun_misc_URLClassPath_JarLoader);
//                    ((java.util.jar.JarFile) java_util_jar_JarFile).close();
//                } catch (Throwable t) {
//                    // if we got this far, this is probably not a JAR loader so skip it
//                }
//            }
//        } catch (Throwable t) {
//            // probably not a SUN VM
//        }
//        return;
//    }
//    
//
//   /**
//   * cleanup jar file factory cache
//   */
//  @SuppressWarnings({ "nls", "unchecked" })
//  public boolean cleanupJarFileFactory(List setJarFileNames2Close)
//  {
//    boolean res = false;
//    Class classJarURLConnection = null;
//    try {
//      classJarURLConnection = ReflectHelper.classForName("sun.net.www.protocol.jar.JarURLConnection");
//    } catch (ClassNotFoundException e) {
//      //System.out.println(e);
//    }
//    if (classJarURLConnection == null) {
//      return res;
//    }
//    Field f = null;
//    try {
//      f = classJarURLConnection.getDeclaredField("factory");
//    } catch (NoSuchFieldException e) {
//      //ignore
//    }
//    if (f == null) {
//      return res;
//    }
//    f.setAccessible(true);
//    Object obj = null;
//    try {
//      obj = f.get(null);
//    } catch (IllegalAccessException e) {
//      //ignore
//    }
//    if (obj == null) {
//      return res;
//    }
//    Class classJarFileFactory = obj.getClass();
//    //
//    HashMap fileCache = null;
//    try {
//      f = classJarFileFactory.getDeclaredField("fileCache");
//      f.setAccessible(true);
//      obj = f.get(null);
//      if (obj instanceof HashMap) {
//        fileCache = (HashMap)obj;
//      }
//    } catch (NoSuchFieldException e) {
//    } catch (IllegalAccessException e) {
//      //ignore
//    }
//    //System.out.println("fileCache "+fileCache.toString());
//    HashMap urlCache = null;
//    try {
//      f = classJarFileFactory.getDeclaredField("urlCache");
//      f.setAccessible(true);
//      obj = f.get(null);
//      if (obj instanceof HashMap) {
//        urlCache = (HashMap)obj;
//      }
//       
//        } catch (Exception e) {
//            //System.out.println(e);
//    }
//    //System.out.println("urlCache "+urlCache.toString());
//    if (urlCache != null) {
//      HashMap urlCacheTmp = (HashMap)urlCache.clone();
//      Iterator it = urlCacheTmp.keySet().iterator();
//      while (it.hasNext()) {
//        obj = it.next();
//        if (!(obj instanceof JarFile)) {
//          continue;
//        }
//        JarFile jarFile = (JarFile)obj;
//        //System.out.println("found jarFile "+jarFile.getName());
//        if (setJarFileNames2Close.contains(jarFile.getName())) {
//          try {
//            jarFile.close();
//          } catch (IOException e) {
//            //ignore
//          }
//          if (fileCache != null) {
//            fileCache.remove(urlCache.get(jarFile));
//          }
//          urlCache.remove(jarFile);
//        }
//      }
//      res = true;
//    } else if (fileCache != null) {
//      // urlCache := null
//      HashMap fileCacheTmp = (HashMap)fileCache.clone();
//      Iterator it = fileCacheTmp.keySet().iterator();
//      while (it.hasNext()) {
//        Object key = it.next();
//        obj = fileCache.get(key);
//        if (!(obj instanceof JarFile)) {
//          continue;
//        }
//        JarFile jarFile = (JarFile)obj;
//        //System.out.println("found jarFile "+jarFile.getName());
//        
//        if (setJarFileNames2Close.contains(jarFile.getName())) {
//          try {
//            jarFile.close();
//          } catch (IOException e) {
//            //ignore
//          }
//          fileCache.remove(key);
//        }
//      }
//      res = true;
//    }
//    setJarFileNames2Close.clear();
//    return res;
//  }
    
    public static JarClassLoader getInstance()
    {
        if(instance==null)
        {
            instance = new JarClassLoader(ClassLoader.getSystemClassLoader());
        }
        return instance;
    }
}
