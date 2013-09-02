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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.picocontainer.Startable;
import org.toughradius.annotation.Inject;
import org.toughradius.data.RadGroupMapper;
import org.toughradius.data.RadGroupMetaMapper;
import org.toughradius.data.RadUserMapper;
import org.toughradius.data.RadUserMetaMapper;
import org.toughradius.model.RadGroup;
import org.toughradius.model.RadGroupMeta;
import org.toughradius.model.RadGroupMetaExample;
import org.toughradius.model.RadUser;
import org.toughradius.model.RadUserExample;
import org.toughradius.model.RadUserMeta;
import org.toughradius.model.RadUserMetaExample;
import org.toughradius.model.RadUserMetaKey;

/**
 * @author jamiesun
 */
@Inject
public class CacheService implements Startable
{
    private static Log log = LogFactory.getLog(CacheService.class);
    private Object lock = new Object();
    private ConcurrentHashMap<String,RadUser> userCache = new ConcurrentHashMap<String, RadUser>();
    private ConcurrentHashMap<String,List<RadUserMeta>> userMetaCache = new ConcurrentHashMap<String, List<RadUserMeta>>();
    private ConcurrentHashMap<String,RadGroup> groupCache = new ConcurrentHashMap<String, RadGroup>();
    private ConcurrentHashMap<String,List<RadGroupMeta>> groupMetaCache = new ConcurrentHashMap<String, List<RadGroupMeta>>();
   
    private DBService dbservice;
    
    public void setDbservice(DBService dbservice)
    {
        this.dbservice = dbservice;
    }
    
    
    /**
     * 加载缓存数据
     */
    public void start()
    {
        log.info("starting load cache data ...");
        
        SqlSession session = dbservice.openSession();
        try
        {
            RadUserMapper mapper = session.getMapper(RadUserMapper.class);
            RadUserMetaMapper umMapper = session.getMapper(RadUserMetaMapper.class);
            RadGroupMapper gMapper = session.getMapper(RadGroupMapper.class);
            RadGroupMetaMapper gmMapper = session.getMapper(RadGroupMetaMapper.class);
            List<RadUser> users = mapper.selectByExample(null);
            List<RadUserMeta> userMetas = umMapper.selectByExample(null);
            List<RadGroup> groups = gMapper.selectByExample(null);
            List<RadGroupMeta> groupMetas = gmMapper.selectByExample(null);
            
            synchronized (lock)
            {
                for (RadUser user : users)
                {
                    userCache.put(user.getUserName(), user);
                }
                
                log.info("load user cache data done !");
                
                for (RadUserMeta userMeta : userMetas)
                {
                    List<RadUserMeta> ums = userMetaCache.get(userMeta.getUserName());
                    
                    if(ums==null)
                    {
                        ums = new ArrayList<RadUserMeta>();
                        ums.add(userMeta);
                    }
                    else
                    {
                        ums.add(userMeta);
                    }
                    
                }
                
                log.info("load userMeta cache data done !");
                
                for (RadGroup group : groups)
                {
                    groupCache.put(group.getGroupName(), group);
                }
                
                log.info("load group cache data done !");
                
                for (RadGroupMeta groupMeta : groupMetas)
                {
                  List<RadGroupMeta> ums = groupMetaCache.get(groupMeta.getGroupName());
                    
                    if(ums==null)
                    {
                        ums = new ArrayList<RadGroupMeta>();
                        ums.add(groupMeta);
                    }
                    else
                    {
                        ums.add(groupMeta);
                    }
                }
            }
            log.info("load groupMeta cache data done !");
            log.info("load cache data done !");
        }
        finally
        {
            session.close();
        }
        
        

    }
    
    public void reload()
    {
        userCache.clear();
        userMetaCache.clear();
        groupCache.clear();
        groupMetaCache.clear();
        start();
    }
    
    public List<RadUser> getUsers()
    {
        return Collections.unmodifiableList(new ArrayList<RadUser>(userCache.values()));
    }

    public RadUser getUser(String userName)
    {
        return userCache.get(userName);  
    }
    
    public List<RadUserMeta> getUserMetas(String userName)
    {
        return userMetaCache.get(userName);  
    }
    
    public RadUserMeta getUserMeta(String userName,String metaName)
    {
        List<RadUserMeta> metas = userMetaCache.get(userName);  
        
        if(metas==null)
            return null;
        
        for (RadUserMeta meta : metas)
        {
            if(meta.getName().equals(metaName))
                return meta;
        }
        return null;  
    }
    
    public void updateUser(RadUser user)
    {
        if(user==null)
            return;
        
        RadUser cuser = userCache.get(user.getUserName());
        
        if(cuser==null)
        {
            userCache.put(user.getUserName(), user);
            
            SqlSession session = dbservice.openSession();
            try
            {
                RadUserMetaMapper umMapper = session.getMapper(RadUserMetaMapper.class);
                RadUserMetaExample example = new RadUserMetaExample();
                example.createCriteria().andUserNameEqualTo(user.getUserName());
                List<RadUserMeta> userMetas = umMapper.selectByExample(example);
                userMetaCache.put(user.getUserName(), userMetas);
            }
            finally
            {
                session.close();
            }
        }
        else
        {
            synchronized (cuser)
            {
                cuser.setGroupName(user.getGroupName());
                cuser.setPassword(user.getPassword());
            }
        }
    }
    
    public void removeUser(String userName)
    {
        userCache.remove(userName);
        userMetaCache.remove(userName);
    }
    
    public void removeUserMeta(String userName)
    {
        userMetaCache.remove(userName);
    }
    
    public void removeUserMeta(String userName,String metaName)
    {
        List<RadUserMeta> metas = userMetaCache.get(userName);  
        
        synchronized (metas)
        {
            for (Iterator<RadUserMeta> iterator = metas.iterator(); iterator.hasNext();)
            {
                RadUserMeta meta = iterator.next();
                if(meta.getName().equals(metaName))
                {
                    iterator.remove();
                    break;
                }
            }
        }
    }
    
    public void updateUserMeta(RadUserMeta newMeta)
    {
        List<RadUserMeta> metas = userMetaCache.get(newMeta.getUserName());  
        
        synchronized (metas)
        {
            boolean flag = false;
            for (Iterator<RadUserMeta> iterator = metas.iterator(); iterator.hasNext();)
            {
                RadUserMeta meta = iterator.next();
                if(meta.getName().equals(newMeta.getName()))
                {
                    flag = true;
                    meta.setValue(newMeta.getValue());
                    break;
                }
            }
            if(!flag)
                metas.add(newMeta);
        }
    }
    
    public List<RadGroup> getGroups()
    {
        return Collections.unmodifiableList(new ArrayList<RadGroup>(groupCache.values()));
    }

    public RadGroup getGroup(String groupName)
    {
        return groupCache.get(groupName);  
    }
    
    public List<RadGroupMeta> getGroupMetas(String groupName)
    {
        return groupMetaCache.get(groupName);  
    }
    
    public RadGroupMeta getGroupMeta(String groupName,String metaName)
    {
        List<RadGroupMeta> metas = groupMetaCache.get(groupName);  
        if(metas==null)
            return null;
        for (RadGroupMeta meta : metas)
        {
            if(meta.getName().equals(metaName))
                return meta;
        }
        return null;  
    }
    
    public void updateGroup(RadGroup group)
    {
        if(group==null)
            return;
        
        RadGroup cgroup = groupCache.get(group.getGroupName());
        if(cgroup==null)
        {
            groupCache.put(group.getGroupName(), group);
            
            SqlSession session = dbservice.openSession();
            try
            {
                RadGroupMetaMapper umMapper = session.getMapper(RadGroupMetaMapper.class);
                RadGroupMetaExample example = new RadGroupMetaExample();
                example.createCriteria().andGroupNameEqualTo(group.getGroupName());
                List<RadGroupMeta> groupMetas = umMapper.selectByExample(example);
                groupMetaCache.put(group.getGroupName(), groupMetas);
            }
            finally
            {
                session.close();
            }
        }
        else
        {
            synchronized (group)
            {
                group.setGroupDesc(group.getGroupDesc());
            }
        }

    }
    
    public void removeGroup(String groupName)
    {
        groupCache.remove(groupName);
        groupMetaCache.remove(groupName);
    }
    
    public void removeGroupMeta(String groupName)
    {
        groupMetaCache.remove(groupName);
    }
    
    public void removeGroupMeta(String groupName,String metaName)
    {
        List<RadGroupMeta> metas = groupMetaCache.get(groupName);  
        
        synchronized (metas)
        {
            for (Iterator<RadGroupMeta> iterator = metas.iterator(); iterator.hasNext();)
            {
                RadGroupMeta meta = iterator.next();
                if(meta.getName().equals(metaName))
                {
                    iterator.remove();
                    break;
                }
            }
        }

    }
    
    public void updateGroupMeta(RadGroupMeta newMeta)
    {
        List<RadGroupMeta> metas = groupMetaCache.get(newMeta.getGroupName());  
        
        synchronized (metas)
        {
            boolean flag = false;
            for (Iterator<RadGroupMeta> iterator = metas.iterator(); iterator.hasNext();)
            {
                RadGroupMeta meta = iterator.next();
                if(meta.getName().equals(newMeta.getName()))
                {
                    flag = true;
                    meta.setValue(newMeta.getValue());
                    break;
                }
            }
            
            if(!flag)
                metas.add(newMeta);
        }
    }

    public void stop()
    {
    }
}
