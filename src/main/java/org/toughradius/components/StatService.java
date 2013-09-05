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
import org.toughradius.data.RadOnlineMapper;
import org.toughradius.data.RadUserMapper;
import org.toughradius.model.RadOnline;
import org.toughradius.model.RadOnlineExample;
import org.toughradius.model.RadOnlineKey;
import org.toughradius.model.RadUser;

@Inject
public class StatService {
    private static Log logger = LogFactory.getLog(StatService.class);
    private DBService dbservice;

    public void setDbservice(DBService dbservice) {
        this.dbservice = dbservice;
    }

    /**
     * 查询在线数
     * 
     * @param example
     * @return
     */
    public int countOnline(RadOnlineExample example) {
        SqlSession session = dbservice.openSession();
        try {
            RadOnlineMapper mapper = session.getMapper(RadOnlineMapper.class);
            int count = mapper.countByExample(example);
            return count;
        } finally {
            session.close();
        }
    }
    
    public int countOnline(String  userName) {
        SqlSession session = dbservice.openSession();
        try {
            RadOnlineMapper mapper = session.getMapper(RadOnlineMapper.class);
            RadOnlineExample example = new RadOnlineExample();
            example.createCriteria().andUserNameEqualTo(userName);
            int count = mapper.countByExample(example);
            return count;
        } finally {
            session.close();
        }
    }

    /**
     * 查询在线用户
     * 
     * @param clientAddr
     * @param sessionId
     * @return
     */
    public RadOnline getOnline(String clientAddr, String sessionId) {
        SqlSession session = dbservice.openSession();
        try {
            RadOnlineMapper mapper = session.getMapper(RadOnlineMapper.class);
            RadOnlineKey key = new RadOnlineKey();
            key.setClientAddress(clientAddr);
            key.setSessionId(sessionId);
            return mapper.selectByPrimaryKey(key);
        } finally {
            session.close();
        }

    }
    
    public RadOnline getOnlineAndRemove(String clientAddr, String sessionId) {
        SqlSession session = dbservice.openSession();
        try {
            RadOnlineMapper mapper = session.getMapper(RadOnlineMapper.class);
            RadOnlineKey key = new RadOnlineKey();
            key.setClientAddress(clientAddr);
            key.setSessionId(sessionId);
            RadOnline online = mapper.selectByPrimaryKey(key);
            mapper.deleteByPrimaryKey(key);
            return online;
        } finally {
            session.close();
        }

    }

    /**
     * 查询在线用户集合
     * 
     * @param example
     * @param rowBounds
     * @return
     */
    public List<RadOnline> getOnlines(RadOnlineExample example, RowBounds rowBounds) {
        SqlSession session = dbservice.openSession();
        try {
            RadOnlineMapper mapper = session.getMapper(RadOnlineMapper.class);
            return mapper.selectByExampleWithRowbounds(example, rowBounds);
        } finally {
            session.close();
        }

    }

    public List<RadOnline> getOnlinesAndClear() {
        SqlSession session = dbservice.openSession();
        try {
            RadOnlineMapper mapper = session.getMapper(RadOnlineMapper.class);
            List<RadOnline> result = mapper.selectByExample(null);
            mapper.deleteByExample(null);
            return result;
        } finally {
            session.close();
        }

    }

    /**
     * 新增在线用户
     * 
     * @param online
     */
    public void addOnline(RadOnline online) {
        SqlSession session = dbservice.openSession();
        try {
            RadOnlineMapper mapper = session.getMapper(RadOnlineMapper.class);
            mapper.insert(online);
            session.commit();
        } finally {
            session.close();
        }
    }

    /**
     * 删除在线用户
     * 
     * @param clientAddr
     * @param sessionId
     */
    public void deleteOnline(String clientAddr, String sessionId) {
        SqlSession session = dbservice.openSession();
        try {
            RadOnlineMapper mapper = session.getMapper(RadOnlineMapper.class);
            RadOnlineKey key = new RadOnlineKey();
            key.setClientAddress(clientAddr);
            key.setSessionId(sessionId);
            mapper.deleteByPrimaryKey(key);
            session.commit();
        } finally {
            session.close();
        }
    }
}
