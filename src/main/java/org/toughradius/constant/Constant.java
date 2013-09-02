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
package org.toughradius.constant;

import java.util.ArrayList;
import java.util.List;


public class Constant
{
    public static final StringConst USER_EXPIRE = new StringConst("EXPIRE","过期时间(####-##-##)");
    public static final StringConst USER_STATUS = new StringConst("STATUS","状态(0/1/2:未激活/正常/停机)");
    public static final StringConst USER_PERIOD = new StringConst("PERIOD","上网时段(比如 08:00-21:00)");
    public static final StringConst USER_BIND_MAC = new StringConst("PERIOD","绑定MAC地址(0/1:不绑定/绑定)");
    public static final StringConst USER_CONCUR_NUMBER  = new StringConst("CONCUR_NUMBER","并发数（0-20）");
    public static final StringConst USER_Framed_IP_Address  = new StringConst("Framed-IP-Address","固定IP");
    public static final StringConst USER_Filter_ID  = new StringConst("Filter-ID","过滤规则组");
    public static final StringConst USER_Mikrotik_Rate_Limit  = new StringConst("Mikrotik-Rate-Limit","RouterOS限速属性");
    

    
    public static final StringConst GROUP_STATUS = new StringConst("STATUS","状态(1/2:正常/停机)");
    public static final StringConst GROUP_PERIOD = new StringConst("PERIOD","上网时段（比如 08:00-21:00）"); 
    public static final StringConst GROUP_CLIENT = new StringConst("CLIENT","绑定客户端"); 
    public static final StringConst GROUP_BIND_MAC = new StringConst("PERIOD","绑定MAC地址(0/1:不绑定/绑定)");
    public static final StringConst GROUP_Filter_ID  = new StringConst("Filter-ID","过滤规则组");
    public static final StringConst GROUP_Session_Timeout  = new StringConst("Session-Timeout","最大会话时长（秒）");
    public static final StringConst GROUP_Framed_Pool   = new StringConst("Framed-Pool ","地址池");
    public static final StringConst GROUP_CONCUR_NUMBER  = new StringConst("CONCUR_NUMBER","并发数（0-20）");
    public static final StringConst GROUP_Mikrotik_Rate_Limit  = new StringConst("Mikrotik-Rate-Limit","RouterOS限速属性");
    
    public final static List<StringConst> UserMetaList = new ArrayList<StringConst>();
    public final static List<StringConst> GroupMetaList = new ArrayList<StringConst>();
    
    static 
    {
        UserMetaList.add(USER_EXPIRE);
        UserMetaList.add(USER_STATUS);
        UserMetaList.add(USER_PERIOD);
        UserMetaList.add(USER_BIND_MAC);
        UserMetaList.add(USER_CONCUR_NUMBER);
        UserMetaList.add(USER_Framed_IP_Address);
        UserMetaList.add(USER_Filter_ID);
        UserMetaList.add(USER_Mikrotik_Rate_Limit);
        
        
        GroupMetaList.add(GROUP_STATUS);
        GroupMetaList.add(GROUP_PERIOD);
        GroupMetaList.add(GROUP_CLIENT);
        GroupMetaList.add(GROUP_BIND_MAC);
        GroupMetaList.add(GROUP_Filter_ID);
        GroupMetaList.add(GROUP_Session_Timeout);
        GroupMetaList.add(GROUP_Framed_Pool);
        GroupMetaList.add(GROUP_CONCUR_NUMBER);
        GroupMetaList.add(GROUP_Mikrotik_Rate_Limit);
    }
    

    public static String getGroupMetaDesc(String name)
    {
        for (StringConst meta : GroupMetaList)
        {
            if(meta.value().equals(name))
                return meta.desc();
        }
        return "Radius扩展属性";
    }
    
    public static String getUserMetaDesc(String name)
    {
        for (StringConst meta : UserMetaList)
        {
            if(meta.value().equals(name))
                return meta.desc();
        }
        return "Radius扩展属性";
    }
    
}
