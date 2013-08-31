package org.toughradius;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 工程CLASSPATH
 */
public class Classpath
{
    private static final String LIB_FOLDER = "./lib";   
    private List<File> _elements = new ArrayList<File>();

    /** 初始化CLASSPATH */
    public void initClassPath() throws IOException
    {
        _elements.clear();

        File jarDir = new File(LIB_FOLDER);
        File[] jars = jarDir.listFiles(new FileFilter()   
        {   
            public boolean accept(File dir)   
            {   
              String name = dir.getName().toLowerCase();   
              return name.endsWith(".jar") || name.endsWith(".zip");   
            }   
          });   

        for (int i=0;i<jars.length;i++)
        {
            addJarFile(jars[i]);
        }

        System.setProperty("java.class.path", toString());
        ClassLoader cl = getClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
    }
    
    private boolean addJarFile(File jar) throws IOException
    {
        if (jar == null || !jar.exists()) 
            return false;

        File key = jar.getCanonicalFile();
        if (!_elements.contains(key))
        {
            _elements.add(key);
            return true;
        }
  
        return false;
    }
    
    public ClassLoader getClassLoader() 
    {
        int cnt = _elements.size();
        URL[] urls = new URL[cnt];
        for (int i=0; i < cnt; i++) 
        {
            try 
            {
                urls[i] = ((File)(_elements.get(i))).toURI().toURL();
            } 
            catch (MalformedURLException e) 
            {
            }
        }
        
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null)
            parent = Classpath.class.getClassLoader();

        if (parent == null)
            parent = ClassLoader.getSystemClassLoader();

        return new Loader(urls, parent);
    }

    public String toString()
    {
        StringBuffer cp = new StringBuffer(1024);
        int cnt = _elements.size();
        if (cnt >= 1) 
            cp.append(((File)(_elements.get(0))).getPath());

        for (int i=1; i < cnt; i++) 
        {
            cp.append(File.pathSeparatorChar);
            cp.append(((File)(_elements.get(i))).getPath());
        }
        
        return cp.toString();
    }


    private class Loader extends URLClassLoader
    {
        String name;
        
        Loader(URL[] urls, ClassLoader parent)
        {
            super(urls, parent);
            name = "BootLoader";
        }

        public String toString()
        {
            return name;
        }
    }
}
