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
            
            
            hsrv = new HttpServer(config.getInt("http.port", 9000), rootCtx);
            hsrv.setWorkerpool(Executors.newCachedThreadPool(new ServerThreadFactory()));
            hsrv.setMaxConcurrentConnections(config.getInt("http.maxConn", 10000));
            hsrv.setAutoCompressThresholdBytes(2048);
            hsrv.setFlushmode(FlushMode.ASYNC);

            hsrv.setAutoUncompress(true);
            hsrv.setConnectionTimeoutMillis(600*1000);
            hsrv.setBodyDataReceiveTimeoutMillis(300*1000);
            hsrv.setRequestBodyDefaultEncoding("utf-8");
            hsrv.setStartUpLogMessage("web server starting...");
            hsrv.start();
            log.info("web server start.....");
        } catch (Exception e) {
            log.error("web server start fail", e);
            System.exit(1);
        }
    }

    @Override
    public void stop()
    {
        // TODO Auto-generated method stub
        
    }

}
