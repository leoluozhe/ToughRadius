/**
 * Copyright (c) 2013, jamiesun, All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 
 *     Redistributions in binary form must reproduce the above copyright notice, this
 *     list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 * 
 *     Neither the name of the {organization} nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
