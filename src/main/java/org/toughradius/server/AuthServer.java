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
package org.toughradius.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.Startable;
import org.tinyradius.attribute.IntegerAttribute;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.dictionary.AttributeType;
import org.tinyradius.dictionary.DefaultDictionary;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RadiusServer;
import org.toughradius.Project;
import org.toughradius.common.Config;
import org.toughradius.common.DateTimeUtil;
import org.toughradius.common.ValidateUtil;
import org.toughradius.components.BaseService;
import org.toughradius.components.CacheService;
import org.toughradius.constant.Constant;
import org.toughradius.constant.GroupStatus;
import org.toughradius.constant.UserStatus;
import org.toughradius.model.RadClient;
import org.toughradius.model.RadGroupMeta;
import org.toughradius.model.RadUser;
import org.toughradius.model.RadUserMeta;

public class AuthServer extends RadiusServer implements Startable
{
    private static Log logger = LogFactory.getLog(AuthServer.class);
    private Config config;
    private BaseService baseServ = Project.getBaseService();
    private CacheService cacheServ = Project.getCacheService();
    
    public AuthServer()
    {
    }
    
    public AuthServer(Config config)
    {
        this.config = config;
    }
    
    public void start()
    {
        logger.info("start AuthServer...");
        this.setAuthPort(config.getInt("radius.authPort", 1812));
        this.start(true, false);
    }

    @Override
    public String getSharedSecret(InetSocketAddress client)
    {
        RadClient rc = baseServ.getClient(client.getAddress().getHostAddress());
        return rc!=null?rc.getSecret():null;
    }


    @Override
    public String getUserPassword(String userName)
    {
        RadUser user = cacheServ.getUser(userName);
        return user!=null?user.getPassword():null;
    }
    
    
    /**
     * 认证请求处理
     */
    public RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client)
            throws RadiusException
    {
    	
		RadiusPacket packet = new RadiusPacket(RadiusPacket.ACCESS_ACCEPT, accessRequest.getPacketIdentifier());
		copyProxyState(accessRequest, packet);
    	RadUser user = cacheServ.getUser(accessRequest.getUserName());
    	if(user==null)
    	{
            packet.setPacketType(RadiusPacket.ACCESS_REJECT);
            packet.addAttribute("Reply-Message", "user not exists");
            return packet;
    	}
  
		if (user.getPassword() == null ||!accessRequest.verifyPassword(user.getPassword()))
		{
            packet.setPacketType(RadiusPacket.ACCESS_REJECT);
            packet.addAttribute("Reply-Message", "user password error");
            return packet;
		}

        if(packet.getPacketType() == RadiusPacket.ACCESS_REJECT)
            return packet;
        
        /**************************************************************
         * 用户状态判断 用户组状态优先
         **************************************************************/
        RadUserMeta userStatusMeta = cacheServ.getUserMeta(user.getUserName(),Constant.USER_STATUS.value());
        if(userStatusMeta!=null)
        {
            int uStatus = Integer.valueOf(userStatusMeta.getValue()); 
            if(UserStatus.Prepar.value() == uStatus)
            {
                packet.setPacketType(RadiusPacket.ACCESS_REJECT);
                packet.addAttribute("Reply-Message", "user not Active");
                return packet;
            }
            
            if(UserStatus.Pause.value() == uStatus)
            {
                packet.setPacketType(RadiusPacket.ACCESS_REJECT);
                packet.addAttribute("Reply-Message", "user has pause");
                return packet;
            }
        }
        
        RadGroupMeta groupStatusMeta = cacheServ.getGroupMeta(user.getGroupName(),Constant.GROUP_STATUS.value());
        if(groupStatusMeta!=null)
        {
            int gStatus = Integer.valueOf(groupStatusMeta.getValue()); 
            
            if(GroupStatus.Pause.value() == gStatus)
            {
                packet.setPacketType(RadiusPacket.ACCESS_REJECT);
                packet.addAttribute("Reply-Message", "user group has pause");
                return packet;
            }
        }
        
        /**************************************************************
         * 并发数判断 用户并发数优先
         **************************************************************/
        RadUserMeta userCnumMeta = cacheServ.getUserMeta(user.getUserName(),Constant.USER_CONCUR_NUMBER.value());
        RadGroupMeta groupCnumMeta = cacheServ.getGroupMeta(user.getGroupName(),Constant.GROUP_CONCUR_NUMBER.value());
        int _climit = groupCnumMeta!=null?Integer.valueOf(groupCnumMeta.getValue()):config.getInt("radius.concurNumber", 1);
        int climit = userCnumMeta!=null?Integer.valueOf(userCnumMeta.getValue()):_climit;
        
        
        
        
        /**************************************************************
         * MAC地址绑定处理
         **************************************************************/
        String macaddr = accessRequest.getAttributeValue("Calling-Station-Id");
        
        /**************************************************************
         * 获取最大会话时长
         **************************************************************/
        int sessionTimeout = config.getInt("radius.maxSessionTimeout");
        RadGroupMeta stimeoutattr = cacheServ.getGroupMeta(user.getGroupName(), Constant.GROUP_Session_Timeout.value());
        if(stimeoutattr!=null)
        	sessionTimeout = Integer.valueOf(stimeoutattr.getValue());
        
        /**************************************************************
         * 判断到期
         **************************************************************/
        RadUserMeta attr = cacheServ.getUserMeta(accessRequest.getUserName(), Constant.USER_EXPIRE.value());
       
        
        if(attr!=null)
        {
            //用户当晚到期
            if(attr.getValue().equals(DateTimeUtil.getDateString()))
            {
                String dateTime = DateTimeUtil.getDateTimeString();
                int _timeout = DateTimeUtil.compareSecond(attr.getValue()+" 23:59:59", dateTime);
                if(_timeout<sessionTimeout)
                	sessionTimeout = _timeout;
            }
            else if(DateTimeUtil.compareDay(attr.getValue(), DateTimeUtil.getDateString()) < 0)
            {
                packet.setPacketType(RadiusPacket.ACCESS_REJECT);
                packet.addAttribute("Reply-Message", "user expired");
                return packet;
            }
        }
        
        /**************************************************************
         * 上网时段控制
         **************************************************************/
        RadUserMeta periodAttr = cacheServ.getUserMeta(accessRequest.getUserName(), Constant.USER_PERIOD.value());
        if (periodAttr!=null&&!ValidateUtil.isEmpty(periodAttr.getValue()))
        {
            String startTime = periodAttr.getValue().substring(0, 5);
            String endTime = periodAttr.getValue().substring(6, 11);
            String nowDay = DateTimeUtil.getDateString();
            String afterDay = DateTimeUtil.getNextDateStringAddDay(nowDay, 1);
            String nowTime = DateTimeUtil.getDateTimeString();            
            String nowHour = DateTimeUtil.getTimeString().substring(0, 5);
            
            //时段判断
            boolean _auth = true;
            if(startTime.compareTo(endTime) < 0 && 
                    (nowHour.compareTo(startTime) < 0 || nowHour.compareTo(endTime) > 0 ) ) 
                _auth = false;
            else if(startTime.compareTo(endTime) > 0 && 
                    (nowHour.compareTo(startTime) < 0 && nowHour.compareTo(endTime) > 0 ) ) 
                _auth = false;
            
            if(!_auth)
            {
                packet.setPacketType(RadiusPacket.ACCESS_REJECT);
                packet.addAttribute("Reply-Message", "Invalid period");
                return packet;
            }
            
            //会话超时时长计算
            if(startTime.compareTo(endTime) < 0)                      
                endTime = nowDay + " " + endTime + ":00";
            else if (nowTime.compareTo(nowDay + " " + startTime + ":00") > 0)            
                endTime = afterDay + " " + endTime + ":00";//跨天
            else
                endTime = nowDay + " " + endTime + ":00";
            
            int timeLenth = DateTimeUtil.compareSecond(endTime, nowTime);
            if(timeLenth < sessionTimeout)
                sessionTimeout = timeLenth;
        }
        
        
        /**************************************************************
         * 扩展属性下发 用户属性优先
         **************************************************************/
        /////////
        List<RadUserMeta> userMetas = cacheServ.getUserMetas(user.getGroupName());
        List<RadGroupMeta> groupMetas = cacheServ.getGroupMetas(user.getGroupName());
        Map<String,String> metasMap = new HashMap<String,String>();
        if(groupMetas!=null)
        {
            for (RadGroupMeta radGroupMeta : groupMetas) {
            	metasMap.put(radGroupMeta.getName(), radGroupMeta.getValue());
    		}
        }
        
        if(userMetas!=null)
        {
            for (RadUserMeta radUserMeta : userMetas) {
            	metasMap.put(radUserMeta.getName(), radUserMeta.getValue());
    		}
        }

        for (Iterator<Map.Entry<String,String>> iterator = metasMap.entrySet().iterator(); iterator.hasNext();) {
        	Map.Entry<String,String> ent =  iterator.next();
        	AttributeType attrType = DefaultDictionary.getDefaultDictionary().getAttributeTypeByName(ent.getKey());
		    if(attrType!=null)
		    {
		    	packet.removeAttributes(attrType.getTypeCode());
		    	packet.addAttribute(ent.getKey().trim(), ent.getValue());
		    }
        }
        packet.removeAttributes(27);
        packet.addAttribute(new IntegerAttribute(27,sessionTimeout));
        return packet;
    }

}
