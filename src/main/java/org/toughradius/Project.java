package org.toughradius;

import java.io.File;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.xml.DOMConfigurator;
import org.toughradius.annotation.Inject;
import org.toughradius.common.ActionSet;
import org.toughradius.common.Beans;
import org.toughradius.common.Config;
import org.toughradius.common.Utils;
import org.toughradius.components.BaseService;
import org.toughradius.components.DBService;
import org.toughradius.components.UserService;
import org.toughradius.server.ToughServer;
import org.toughradius.server.WebServer;
import org.xlightweb.Mapping;


public class Project
{
    public static final String LOG4J_FILE = "conf/log4j.xml";
    public static final String CONFIG_FILE = "conf/system.xml";
    private static Log log = LogFactory.getLog(Project.class);
    private ToughServer server;
    
    /**
     * 初始化日志环境
     * @return
     */
    public boolean initLogger()
    {
        File file = new File(LOG4J_FILE);
        if (file == null || !file.isFile())
        {
            System.out.println("初始化Log异常,请检查conf/log4j.xml文件");
            return false;
        }
        
        try
        {
            DOMConfigurator.configure(LOG4J_FILE);
        }
        catch(Exception e)
        {
            System.out.println("初始化Log异常,请检查conf/log4j.xml文件,"+e.getMessage());
            return false;
        }
        return true;
        
    }
    
    private void startWebServer()
    {
        
    }

    
    /**
     * 初始化系統環境，啓動服務器
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void start()
    {
        if(!initLogger())
            System.exit(1);
        
        Beans.put(Config.class);
        
        HashSet<Class> CLASSES = Utils.loadClasses("com.zhengtu");
        ActionSet<Class> actionSet = new ActionSet<Class>();
        
        for (Class clasz : CLASSES) {
            if(clasz.getAnnotation(Inject.class)!=null)
            {
                Beans.put(clasz);
            }
            if(clasz.getAnnotation(Mapping.class)!=null)
            {
                Beans.put(clasz);
                actionSet.add(clasz);
            }
        }
        
        ToughServer rserver = new ToughServer(Beans.getBean(Config.class));
        WebServer wserv = new WebServer(Beans.getBean(Config.class), actionSet);

        Beans.put(rserver);
        Beans.put(wserv);
        
        Beans.start();
    }
    
    public static SqlSession getSession()
    {
        DBService db = Beans.getBean(DBService.class);
        return db.openSession();
    }
    
    public static BaseService getBaseService()
    {
        return Beans.getBean(BaseService.class);
    }
    
    public static UserService getUserService()
    {
        return Beans.getBean(UserService.class);
    }
    
    public static void main(String[] args)
    {
        Project startup = new Project();
        startup.start();
    }
}
