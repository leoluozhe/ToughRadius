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
import org.toughradius.data.RadAdminMapper;
import org.toughradius.data.RadBlacklistMapper;
import org.toughradius.data.RadClientMapper;
import org.toughradius.data.RadGroupMapper;
import org.toughradius.data.RadGroupMetaMapper;
import org.toughradius.data.RadOnlineMapper;
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
            configuration.setLazyLoadingEnabled(true);
            configuration.setAggressiveLazyLoading(false);
            configuration.addMapper(RadUserMapper.class);
            configuration.addMapper(RadUserMetaMapper.class);
            configuration.addMapper(RadGroupMapper.class);
            configuration.addMapper(RadGroupMetaMapper.class);
            configuration.addMapper(RadClientMapper.class);
            configuration.addMapper(RadBlacklistMapper.class);
            configuration.addMapper(RadAdminMapper.class);
            configuration.addMapper(RadOnlineMapper.class);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            log.info("database init done !");
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
