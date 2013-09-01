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
package org.toughradius.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.Startable;
import org.toughradius.common.ActionSet;
import org.toughradius.common.Beans;
import org.toughradius.common.Config;
import org.xlightweb.Context;
import org.xlightweb.FileServiceRequestHandler;
import org.xlightweb.IWebHandler;
import org.xlightweb.server.HttpServer;
import org.xsocket.connection.IConnection.FlushMode;

/**
 * WEB服务器定义
 */
@SuppressWarnings("rawtypes")
public class WebServer implements Startable
{

    private static AtomicInteger number = new AtomicInteger(0);
    private final static Log log = LogFactory.getLog(WebServer.class);
    private HttpServer hsrv = null;
    private Config config;
    
    private ActionSet<Class> actionSet;

    static class ServerThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            return new Thread(r, "Server-Workerpool-Thread-"+ number.incrementAndGet());
        }
    }
    
    public WebServer(Config config,ActionSet<Class> actionSet)
    {
        this.config = config;
        this.actionSet = actionSet;
    }

    @SuppressWarnings("unchecked")
    public void start() {
        try {
            Context rootCtx = new Context("");
//            rootCtx.addHandler("/files/*", new FileServiceRequestHandler("files/", true));
            rootCtx.addHandler("/static/*", new FileServiceRequestHandler("templates/static/", true));
            for (Class clasz : actionSet) {
                
                IWebHandler action =  (IWebHandler) Beans.getBean(clasz);
                rootCtx.addHandler(action);
            }
            System.setProperty("org.xlightweb.showDetailedError", "true");
            
            
            hsrv = new HttpServer(config.getInt("webapp.port", 9000), rootCtx);
            hsrv.setWorkerpool(Executors.newCachedThreadPool(new ServerThreadFactory()));
            hsrv.setMaxConcurrentConnections(config.getInt("webapp.maxConn", 1024));
            hsrv.setAutoCompressThresholdBytes(2048);
            hsrv.setFlushmode(FlushMode.ASYNC);

            hsrv.setAutoUncompress(true);
            hsrv.setConnectionTimeoutMillis(600*1000);
            hsrv.setBodyDataReceiveTimeoutMillis(300*1000);
            hsrv.setRequestBodyDefaultEncoding("utf-8");
            hsrv.setStartUpLogMessage("web server starting...");
            hsrv.start();
            log.info("start WebServer ...");
        } catch (Exception e) {
            log.error("WebServer start fail", e);
            System.exit(1);
        }
    }

    @Override
    public void stop()
    {
        
    }

}
