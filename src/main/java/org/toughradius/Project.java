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
import org.toughradius.components.CacheService;
import org.toughradius.components.DBService;
import org.toughradius.components.UserService;
import org.toughradius.server.AcctServer;
import org.toughradius.server.AuthServer;
import org.toughradius.server.WebServer;
import org.xlightweb.Mapping;


public class Project
{
    public static final String LOG4J_FILE = "conf/log4j.xml";
    public static final String CONFIG_FILE = "conf/system.xml";
    private static Log log = LogFactory.getLog(Project.class);
    private AuthServer server;
    
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
    
    /**
     * 初始化系統環境，啓動服務器
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void start()
    {
        if(!initLogger())
            System.exit(1);
        
        Beans.put(Config.class);
        
        HashSet<Class> CLASSES = Utils.loadClasses("org.toughradius");
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
        Beans.start();
        
        AuthServer authServer = new AuthServer(Beans.getBean(Config.class));
        AcctServer acctServer = new AcctServer(Beans.getBean(Config.class));
        WebServer wserv = new WebServer(Beans.getBean(Config.class), actionSet);
        
        authServer.start();
        acctServer.start();
        wserv.start();
        
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
    
    public static CacheService getCacheService()
    {
        return Beans.getBean(CacheService.class);
    }
    
    public static void main(String[] args)
    {
        Project startup = new Project();
        startup.start();
    }
}
