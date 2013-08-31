package org.toughradius;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.toughradius.data.RadClientMapper;
import org.toughradius.data.RadUserMapper;
import org.toughradius.model.RadClient;
import org.toughradius.model.RadUser;

public class InitDB
{
    public static void main(String[] args) throws IOException, SQLException, InterruptedException
    {
        String resource = "org/toughradius/data/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sessionFactory =  new SqlSessionFactoryBuilder().build(inputStream);
        
        SqlSession session = sessionFactory.openSession();
        try {
            RadUserMapper mapper = session.getMapper(RadUserMapper.class);
            RadClientMapper cmapper= session.getMapper(RadClientMapper.class);
           
            
            RadClient client  = new RadClient();
            client.setAddress("192.168.1.1");
            client.setClientDesc("ros");
            client.setSecret("123456");
            cmapper.insert(client);
            
            RadUser user = new RadUser();
            user.setUserName("test");
            user.setPassword("test");
            user.setGroupName("test");
            mapper.insert(user);
            session.commit();

            session.getConnection().createStatement().execute("SHUTDOWN SCRIPT");
          } finally {
            
            session.close();
          }
    }
}
