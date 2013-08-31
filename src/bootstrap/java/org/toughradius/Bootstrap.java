package org.toughradius;

import java.lang.reflect.Method;

public class Bootstrap
{
    public static void main(String[] args) throws Exception
    {
        if (args == null || args.length != 1)
            return;
        
        String clazz = args[0];
        Classpath _classpath = new Classpath();
        _classpath.initClassPath();
        ClassLoader loader = _classpath.getClassLoader();
        
        Class<?> c = loader.loadClass(clazz);   
        Method method = c.getMethod("main", new Class[]{String[].class});   
        method.invoke(null, new Object[]{args});   
    }
}