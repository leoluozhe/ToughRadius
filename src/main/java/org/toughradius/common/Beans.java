package org.toughradius.common;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.SetterInjectionComponentAdapterFactory;
import org.picocontainer.defaults.VerifyingVisitor;

/**
 * 实例管理系统初始化时将所有服务组件装
 * @author @jamiesun
 */
public class Beans
{
    private final MutablePicoContainer pico = new DefaultPicoContainer(new CachingComponentAdapterFactory(new SetterInjectionComponentAdapterFactory()));
    private final static Beans beans = new Beans();
    private final static Log log = LogFactory.getLog(Beans.class);
    private final ReentrantLock lock = new ReentrantLock();
    private Beans()
    {
    }

    public static Beans getInstance()
    {
        return beans;
    }


	public void registryBean(Class clasz)
    {
    	if(pico.getComponentInstanceOfType(clasz)!=null)
    		return;
        pico.registerComponentImplementation(clasz);
        log.info("registry [" + clasz.getName() + "] done !");
    }

    public void registryBean(Object obj)
    {
        pico.registerComponentInstance(obj);
        log.info("registry [" + obj.getClass().getName() + "]  done !");
    }

    public void startAll()
    {
    	new VerifyingVisitor().traverse(pico);
        pico.start();
    }
    
    public void stopAll()
    {
        pico.stop();
    }
    
    public void dispose()
    {
    	pico.dispose();
    }

    @SuppressWarnings("unchecked")
	public <T> T get(Class<T> clasz)
    {
        lock.lock(); 
        try {
            return (T) pico.getComponentInstanceOfType(clasz);
        } finally {
          lock.unlock();
        }
    }
    
	public static <T> T getBean(Class<T> clasz)
    {
        return getInstance().get(clasz);
    }

	public static void put(Class cs)
    {
        getInstance().registryBean(cs);
    }

    public static void put(Object obj)
    {
        getInstance().registryBean(obj);
    }

    public static synchronized void start()
    {
        getInstance().startAll();
    }
    
    public static void stop()
    {
        getInstance().stopAll();
    }
    

    public String toString()
    {
        return pico.toString();
    }
    


}
