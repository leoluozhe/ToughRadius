package org.toughradius;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.toughradius.data.RadBlacklistMapper;
import org.toughradius.data.RadClientMapper;
import org.toughradius.data.RadGroupMapper;
import org.toughradius.data.RadGroupMetaMapper;
import org.toughradius.data.RadUserMapper;
import org.toughradius.data.RadUserMetaMapper;
import org.toughradius.server.ToughServer;
import org.toughradius.service.BaseService;
import org.toughradius.service.UserService;

public class Project
{
    public static final String LOG4J_FILE = "conf/log4j.xml";
    public static final String CONFIG_FILE = "conf/system.xml";
    private static final HashMap<Class<?>, Object> objectMap = new HashMap<Class<?>, Object>();
    private static Log log = LogFactory.getLog(Project.class);
    private XMLConfiguration config = null;
    private ToughServer server;
    
    /**
     * 注册系统单例对象
     * @param clasz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObject(Class<T> clasz)
    {
        return (T) objectMap.get(clasz);
    }

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
     * 初始化系统配置
     * @return
     */
    private boolean loadConfig()
    {
        try
        {
            config = new XMLConfiguration(CONFIG_FILE);
            objectMap.put(XMLConfiguration.class, config);
            log.info("加载系统配置完成");
        }
        catch (ConfigurationException e)
        {
            e.printStackTrace();
            System.out.println("初始化配置异常,请检查conf/system.xml文件;"+e.getMessage());
            return false;
        }
        return true;
    }
    

    
    
    private boolean initDB()
    {
        try
        {
            PooledDataSourceFactory dataSource = new PooledDataSourceFactory();
            Properties dbpr = new Properties();
            dbpr.setProperty("driver", config.getString("database.driver", "org.hsqldb.jdbc.JDBCDriver"));
            dbpr.setProperty("url", config.getString("database.url", "jdbc:hsqldb:./data/radius"));
            dbpr.setProperty("username", config.getString("database.username", "sa"));
            dbpr.setProperty("password", config.getString("database.password", ""));
            dataSource.setProperties(dbpr);
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("development", transactionFactory, dataSource.getDataSource());
            Configuration configuration = new Configuration(environment);
            configuration.setCacheEnabled(true);
            configuration.addMapper(RadUserMapper.class);
            configuration.addMapper(RadUserMetaMapper.class);
            configuration.addMapper(RadGroupMapper.class);
            configuration.addMapper(RadGroupMetaMapper.class);
            configuration.addMapper(RadClientMapper.class);
            configuration.addMapper(RadBlacklistMapper.class);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            objectMap.put(SqlSessionFactory.class, sqlSessionFactory);
            log.info("初始化数据库完成");
        }
        catch (Exception e)
        {
            System.out.println("初始化数据库失败；"+e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * 启动RADIUS服务器
     */
    private void startRadiusServer()
    {
        log.info("启动 RadiusServer...");
        server = new ToughServer();
        server.setAuthPort(config.getInt("radius.authPort", 1812));
        server.setAcctPort(config.getInt("radius.acctPort", 1813));
        objectMap.put(ToughServer.class, server);
        server.start(true, true);
    }
    
    /**
     * 初始化系統環境，啓動服務器
     */
    public void start()
    {
        if(!initLogger())
            System.exit(1);
        
        if(!loadConfig())
            System.exit(1);
        
        if(!initDB())
            System.exit(1);
        
        objectMap.put(BaseService.class, new BaseService(getObject(SqlSessionFactory.class)));
        objectMap.put(UserService.class, new UserService(getObject(SqlSessionFactory.class)));
        
        startRadiusServer();
    }
    
    public static SqlSession getSession()
    {
        SqlSessionFactory ssf = getObject(SqlSessionFactory.class);
        return ssf.openSession();
    }
    
    public static BaseService getBaseService()
    {
        return getObject(BaseService.class);
    }
    
    public static UserService getUserService()
    {
        return getObject(UserService.class);
    }
    
    public static void main(String[] args)
    {
        Project startup = new Project();
        startup.start();
    }
}
