package org.toughradius.components;

import java.util.Properties;

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
import org.picocontainer.Startable;
import org.toughradius.annotation.Inject;
import org.toughradius.common.Config;
import org.toughradius.data.RadBlacklistMapper;
import org.toughradius.data.RadClientMapper;
import org.toughradius.data.RadGroupMapper;
import org.toughradius.data.RadGroupMetaMapper;
import org.toughradius.data.RadUserMapper;
import org.toughradius.data.RadUserMetaMapper;


@Inject
public class DBService implements Startable
{
    private static Log log = LogFactory.getLog(DBService.class);
    
    private Config config;
    
    private SqlSessionFactory sqlSessionFactory;
    
    public void setConfig(Config config)
    {
        this.config = config;
    }
    
    @Override
    public void start()
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
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            log.info("初始化数据库完成");
        }
        catch (Exception e)
        {
            log.error("初始化数据库失败；",e);
        }

    }

    @Override
    public void stop()
    {
    }
    
    
    public SqlSessionFactory getSqlSessionFactory()
    {
        return sqlSessionFactory;
    }
    
    public SqlSession openSession()
    {
        return sqlSessionFactory.openSession();
    }

    
}
