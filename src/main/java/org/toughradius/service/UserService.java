package org.toughradius.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.toughradius.Project;
import org.toughradius.data.RadGroupMapper;
import org.toughradius.data.RadGroupMetaMapper;
import org.toughradius.data.RadUserMapper;
import org.toughradius.data.RadUserMetaMapper;
import org.toughradius.model.RadGroup;
import org.toughradius.model.RadGroupMeta;
import org.toughradius.model.RadGroupMetaExample;
import org.toughradius.model.RadGroupMetaKey;
import org.toughradius.model.RadUser;
import org.toughradius.model.RadUserMeta;
import org.toughradius.model.RadUserMetaExample;
import org.toughradius.model.RadUserMetaKey;

public class UserService
{
    private static Log logger = LogFactory.getLog(UserService.class);
    
    private SqlSessionFactory sessionFactory;
    
    public UserService(SqlSessionFactory ssf)
    {
        sessionFactory = ssf;
    }
    
    
    /**
     * 查询单个用户
     * @param username
     * @return
     */
    public RadUser getUser(String username)
    {
        SqlSession session = sessionFactory.openSession();
        try
        {
            RadUserMapper mapper = session.getMapper(RadUserMapper.class);
            RadUser ru = mapper.selectByPrimaryKey(username);
            return ru;
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 查询用户单个属性
     * @param username
     * @param metaname
     * @return
     */
    public RadUserMeta getUserMeta(String username,String metaname)
    {
        SqlSession session = sessionFactory.openSession();
        try
        {
            RadUserMetaMapper mapper = session.getMapper(RadUserMetaMapper.class);
            RadUserMetaKey key = new RadUserMetaKey();
            key.setUserName(username);
            key.setName(metaname);
            RadUserMeta meta = mapper.selectByPrimaryKey(key);
            return meta;
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 查询用户属性集合
     * @param username
     * @return
     */
    public List<RadUserMeta> getUserMetas(String username)
    {
        SqlSession session = sessionFactory.openSession();
        try
        {
            RadUserMetaMapper mapper = session.getMapper(RadUserMetaMapper.class);
            RadUserMetaExample example = new RadUserMetaExample();
            example.createCriteria().andUserNameEqualTo(username);
            List<RadUserMeta> metas= mapper.selectByExample(example);
            return metas;
        }
        finally
        {
            session.close();
        }
    }
    
    
    /**
     * 查询单个用户组
     * @param username
     * @return
     */
    public RadGroup getGroup(String groupname)
    {
        SqlSession session = sessionFactory.openSession();
        try
        {
            RadGroupMapper mapper = session.getMapper(RadGroupMapper.class);
            RadGroup ru = mapper.selectByPrimaryKey(groupname);
            return ru;
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 查询用户组单个属性
     * @param username
     * @param metaname
     * @return
     */
    public RadGroupMeta getGroupMeta(String groupname,String metaname)
    {
        SqlSession session = sessionFactory.openSession();
        try
        {
            RadGroupMetaMapper mapper = session.getMapper(RadGroupMetaMapper.class);
            RadGroupMetaKey key = new RadGroupMetaKey();
            key.setGroupName(groupname);
            key.setName(metaname);
            RadGroupMeta meta = mapper.selectByPrimaryKey(key);
            return meta;
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 查询用户组属性集合
     * @param username
     * @return
     */
    public List<RadGroupMeta> getGroupMetas(String groupname)
    {
        SqlSession session = sessionFactory.openSession();
        try
        {
            RadGroupMetaMapper mapper = session.getMapper(RadGroupMetaMapper.class);
            RadGroupMetaExample example = new RadGroupMetaExample();
            example.createCriteria().andGroupNameEqualTo(groupname);
            List<RadGroupMeta> metas= mapper.selectByExample(example);
            return metas;
        }
        finally
        {
            session.close();
        }
    }
    
}
