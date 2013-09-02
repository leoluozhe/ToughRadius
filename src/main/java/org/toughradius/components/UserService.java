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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.toughradius.annotation.Inject;
import org.toughradius.data.RadGroupMapper;
import org.toughradius.data.RadGroupMetaMapper;
import org.toughradius.data.RadUserMapper;
import org.toughradius.data.RadUserMetaMapper;
import org.toughradius.model.RadGroup;
import org.toughradius.model.RadGroupExample;
import org.toughradius.model.RadGroupMeta;
import org.toughradius.model.RadGroupMetaExample;
import org.toughradius.model.RadGroupMetaKey;
import org.toughradius.model.RadUser;
import org.toughradius.model.RadUserExample;
import org.toughradius.model.RadUserMeta;
import org.toughradius.model.RadUserMetaExample;
import org.toughradius.model.RadUserMetaKey;

@Inject
public class UserService
{
    private static Log log = LogFactory.getLog(UserService.class);
    
    private DBService dbservice;
    
    private CacheService cacheService;
    
    public void setDbservice(DBService dbservice)
    {
        this.dbservice = dbservice;
    }
    
    public void setCacheService(CacheService cacheService)
    {
        this.cacheService = cacheService;
    }
    
    /**
     * 查询单个用户
     * @param username
     * @return
     */
    public RadUser getUser(String username)
    {
        SqlSession session = dbservice.openSession();
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
     * 查询用户列表
     * @param example
     * @return
     */
    public List<RadUser> getUsers(RadUserExample example,RowBounds rowBounds)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadUserMapper mapper = session.getMapper(RadUserMapper.class);
            List<RadUser> users = mapper.selectByExampleWithRowbounds(example, rowBounds);
            return users;
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 新增用户
     * @param user
     */
    public void addUser(RadUser user)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadUserMapper mapper = session.getMapper(RadUserMapper.class);
            mapper.insert(user);
            session.commit();
            cacheService.updateUser(user);
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 修改用户
     * @param user
     */
    public void updateUser(RadUser user)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadUserMapper mapper = session.getMapper(RadUserMapper.class);
            mapper.updateByPrimaryKey(user);
            session.commit();
            cacheService.updateUser(user);
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 删除用户
     * @param username
     */
    public void deleteUser(String username)
    {
        if(username == null || "".equals(username))
            return;
        SqlSession session = dbservice.openSession();
        try
        {
            RadUserMetaMapper mapper = session.getMapper(RadUserMetaMapper.class);
            RadUserMetaExample example = new RadUserMetaExample();
            example.createCriteria().andNameEqualTo(username);
            mapper.deleteByExample(example);
            
            RadUserMapper mapper2 = session.getMapper(RadUserMapper.class);
            mapper2.deleteByPrimaryKey(username);
            session.commit();
            cacheService.removeUser(username);
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
        SqlSession session = dbservice.openSession();
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
     * 新增用户属性
     * @param username
     * @param metaname
     * @param value
     */
    public void addUserMeta(RadUserMeta meta)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadUserMetaMapper mapper = session.getMapper(RadUserMetaMapper.class);
            mapper.insert(meta);
            session.commit();
        }
        finally
        {
            session.close();
        }
    }
    /**
     * 修改用户属性
     * @param username
     * @param metaname
     * @param value
     */
    public void updateUserMeta(RadUserMeta meta)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadUserMetaMapper mapper = session.getMapper(RadUserMetaMapper.class);
            mapper.updateByPrimaryKey(meta);
            session.commit();
            cacheService.updateUserMeta(meta);
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 删除用户属性
     * @param username
     */
    public void deleteUserMeta(String username)
    {
        if(username==null||"".equals(username))
            return;
        SqlSession session = dbservice.openSession();
        try
        {
            RadUserMetaMapper mapper = session.getMapper(RadUserMetaMapper.class);
            RadUserMetaExample example = new RadUserMetaExample();
            example.createCriteria().andNameEqualTo(username);
            mapper.deleteByExample(example);
            session.commit();
            cacheService.removeUserMeta(username);
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 删除用户属性
     * @param username
     * @param metaname
     */
    public void deleteUserMeta(String username,String metaname)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadUserMetaMapper mapper = session.getMapper(RadUserMetaMapper.class);
            RadUserMetaKey key = new RadUserMetaKey();
            key.setUserName(username);
            key.setName(metaname);
            mapper.deleteByPrimaryKey(key);
            session.commit();
            cacheService.removeUserMeta(username, metaname);
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
        SqlSession session = dbservice.openSession();
        try
        {
            RadUserMetaMapper mapper = session.getMapper(RadUserMetaMapper.class);
            RadUserMetaExample example = new RadUserMetaExample();
            if(username!=null)
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
        SqlSession session = dbservice.openSession();
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
     * 查询用户组集合
     * @return
     */
    public List<RadGroup> getGroups()
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadGroupMapper mapper = session.getMapper(RadGroupMapper.class);
            List<RadGroup> groups = mapper.selectByExample(null);
            return groups;
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 新增用户组
     * @param group
     */
    public void addGroup(RadGroup group)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadGroupMapper mapper = session.getMapper(RadGroupMapper.class);
            mapper.insert(group);
            session.commit();
            cacheService.updateGroup(group);
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 更新用户组
     * @param group
     */
    public void updateGroup(RadGroup group)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadGroupMapper mapper = session.getMapper(RadGroupMapper.class);
            mapper.updateByPrimaryKey(group);
            session.commit();
            cacheService.updateGroup(group);
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 删除用户组
     * @param groupname
     */
    public void deleteGroup(String groupname)
    {
        if(groupname==null || "".equals(groupname))
            return;
        SqlSession session = dbservice.openSession();
        try
        {
            RadGroupMetaMapper mapper2 = session.getMapper(RadGroupMetaMapper.class);
            RadGroupMetaExample example = new RadGroupMetaExample();
            example.createCriteria().andGroupNameEqualTo(groupname);
            mapper2.deleteByExample(example);
            
            RadGroupMapper mapper = session.getMapper(RadGroupMapper.class);
            mapper.deleteByPrimaryKey(groupname);            
            
            session.commit();
            cacheService.removeGroup(groupname);
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 查询用户组集合
     * @param example
     * @return
     */
    public List<RadGroup> getGroups(RadGroupExample example)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadGroupMapper mapper = session.getMapper(RadGroupMapper.class);
            List<RadGroup> data= mapper.selectByExample(example);
            return data;
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
        SqlSession session = dbservice.openSession();
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
     * 新增用户组属性
     * @param groupname
     * @param metaname
     * @param value
     */
    public void addGroupMeta(RadGroupMeta meta)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadGroupMetaMapper mapper = session.getMapper(RadGroupMetaMapper.class);
            mapper.insert(meta);
            session.commit();
            cacheService.updateGroupMeta(meta);
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 修改用户组属性
     * @param groupname
     * @param metaname
     * @param value
     */
    public void updateGroupMeta(RadGroupMeta meta)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadGroupMetaMapper mapper = session.getMapper(RadGroupMetaMapper.class);
            mapper.updateByPrimaryKey(meta);
            session.commit();
            cacheService.updateGroupMeta(meta);
        }
        finally
        {
            session.close();
        }
    }
    
    
    /**
     * 删除用户组属性
     * @param groupname
     * @param metaname
     */
    public void deleteGroupMeta(String groupname)
    {
        if(groupname==null||"".equals(groupname))
            return;
        SqlSession session = dbservice.openSession();
        try
        {
            RadGroupMetaMapper mapper = session.getMapper(RadGroupMetaMapper.class);
            RadGroupMetaExample example = new RadGroupMetaExample();
            example.createCriteria().andGroupNameEqualTo(groupname);
            mapper.deleteByExample(example);
            session.commit();
            cacheService.removeGroupMeta(groupname);
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 删除用户组属性
     * @param groupname
     * @param metaname
     */
    public void deleteGroupMeta(String groupname,String metaname)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadGroupMetaMapper mapper = session.getMapper(RadGroupMetaMapper.class);
            RadGroupMetaKey meta = new RadGroupMetaKey();
            meta.setGroupName(groupname);
            meta.setName(metaname);
            mapper.deleteByPrimaryKey(meta);
            session.commit();
            cacheService.removeGroupMeta(groupname, metaname);
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
        SqlSession session = dbservice.openSession();
        try
        {
            RadGroupMetaMapper mapper = session.getMapper(RadGroupMetaMapper.class);
            RadGroupMetaExample example = new RadGroupMetaExample();
            if(groupname!=null)
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
