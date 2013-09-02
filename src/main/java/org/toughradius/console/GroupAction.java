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
package org.toughradius.console;

import java.io.IOException;
import java.util.List;

import org.toughradius.annotation.AuthAdmin;
import org.toughradius.common.ValidateUtil;
import org.toughradius.constant.Constant;
import org.toughradius.model.RadGroup;
import org.toughradius.model.RadGroupMeta;
import org.xlightweb.BadMessageException;
import org.xlightweb.IHttpExchange;
import org.xlightweb.IHttpRequest;
import org.xlightweb.Mapping;


@AuthAdmin
@Mapping(
{ "/group" })
public class GroupAction extends FliterAction
{

    /**
     * 查询用户组列表
     */
    public void doGet(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        List<RadGroup> groups = userServ.getGroups();
        request.setAttribute("groups", groups);
        http.send(freemaker.render(http, "group"));
    }

    public void add(IHttpExchange http) throws IOException, BadMessageException
    {
        http.send(freemaker.render(http, "group_add"));
    }
    
    public void insert(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String groupName = request.getParameter("groupName");
        String groupDesc = request.getParameter("groupDesc");
        if(ValidateUtil.isEmpty(groupName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户组名不能为空"));
            return;
        }
        
        if(userServ.getGroup(groupName)!=null)
        {
            http.send(freemaker.renderWithAlert(http, "error","用户组已经存在"));
            return;
        }
        
        RadGroup group = new RadGroup();
        group.setGroupName(groupName);
        group.setGroupDesc(groupDesc);
        userServ.addGroup(group);
        
        http.sendRedirect("/group?op=view&groupName="+groupName);
    }  
    
    /**
     * 用户组详细资料，可修改
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void view(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String groupName = request.getParameter("groupName");
        
        RadGroup group = userServ.getGroup(groupName);
        
        if(group==null)
        {
            http.send(freemaker.renderWithAlert(http, "error","用户组不存在"));
            return;
        }
        
        List<RadGroupMeta> metas = userServ.getGroupMetas(groupName);
        request.setAttribute("group", group);
        request.setAttribute("metas", metas);
        request.setAttribute("GroupMetaList",Constant.GroupMetaList );
        http.send(freemaker.render(http, "group_view"));
    }
    
    /**
     * 修改用户组资料
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void update(IHttpExchange http) throws IOException, BadMessageException
    {

        IHttpRequest request = http.getRequest();
        String groupName = request.getParameter("groupName");
        String groupDesc = request.getParameter("groupDesc");
        if(ValidateUtil.isEmpty(groupName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户组名不能为空"));
            return;
        }
        RadGroup group = new RadGroup();
        group.setGroupName(groupName);
        group.setGroupDesc(groupDesc);
        userServ.updateGroup(group);
        
        http.sendRedirect("/group?op=view&groupName="+groupName);
    }  
    
    /**
     * 增加用户组属性
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void addMeta(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String groupName = request.getParameter("groupName");
        request.setParameter("groupName", groupName);
        String metaName = request.getParameter("addMetaName");
        String metaValue = request.getParameter("addMetaValue");
        
        if(ValidateUtil.isEmpty(groupName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户组名不能为空"));
            return;
        }
        
        if(ValidateUtil.isEmpty(metaName))
        {
            http.send(freemaker.renderWithAlert(http, "error","属性名不能为空"));
            return;
        }
        
        if(ValidateUtil.isEmpty(metaValue))
        {
            http.send(freemaker.renderWithAlert(http, "error","属性值不能为空"));
            return;
        }
        
        if(metaName.equals(Constant.GROUP_PERIOD.value()))
        {
            if(!ValidateUtil.isRegExp(metaValue, "\\d{2}:\\d{2}-\\d{2}:\\d{2}"))
            {
                http.send(freemaker.renderWithAlert(http, "error","时段属性值格式必须为 hh:ss-hh:ss"));
                return;
            }
        }
        
        RadGroupMeta meta = userServ.getGroupMeta(groupName, metaName);
        
        if(meta == null)
        {
            meta = new RadGroupMeta();
            meta.setGroupName(groupName);
            meta.setName(metaName);
            meta.setValue(metaValue);
            meta.setDesc(Constant.getGroupMetaDesc(metaName));
            userServ.addGroupMeta(meta);
        }
        else
        {
            meta.setValue(metaValue);
            meta.setDesc(Constant.getGroupMetaDesc(metaName));
            userServ.updateGroupMeta(meta);
            
        }
        
        http.sendRedirect("/group?op=view&groupName="+groupName);
        
    }  
    
    /**
     * 更新用户组属性
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void updateMeta(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String groupName = request.getParameter("groupName");
        String metaName = request.getParameter("metaName");
        String metaValue = request.getParameter("metaValue");
        
        if(ValidateUtil.isEmpty(groupName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户组名不能为空"));
            return;
        }
        
        if(ValidateUtil.isEmpty(metaName))
        {
            http.send(freemaker.renderWithAlert(http, "error","属性名不能为空"));
            return;
        }
        
        if(ValidateUtil.isEmpty(metaValue))
        {
            http.send(freemaker.renderWithAlert(http, "error","属性值不能为空"));
            return;
        }
        
        if(metaName.equals(Constant.GROUP_PERIOD.value()))
        {
            if(!ValidateUtil.isRegExp(metaValue, "\\d{2}:\\d{2}-\\d{2}:\\d{2}"))
            {
                http.send(freemaker.renderWithAlert(http, "error","时段属性值格式必须为 hh:ss-hh:ss"));
                return;
            }
        }
        
        RadGroupMeta meta = userServ.getGroupMeta(groupName, metaName);
        
        if(meta == null)
        {
            http.send(freemaker.renderWithAlert(http, "error","属性不存在"));
            return;
        }

        meta.setValue(metaValue);
        userServ.updateGroupMeta(meta);
            
        http.sendRedirect("/group?op=view&groupName="+groupName);
        
    }  
    
    /**
     * 删除用户组属性
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void deleteMeta(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String groupName = request.getParameter("groupName");
        String metaName = request.getParameter("metaName");
        if(ValidateUtil.isEmpty(groupName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户组名不能为空"));
            return;
        }
        
        if(ValidateUtil.isEmpty(metaName))
        {
            http.send(freemaker.renderWithAlert(http, "error","属性名不能为空"));
            return;
        }
        
        userServ.deleteGroupMeta(groupName, metaName);
        http.sendRedirect("/group?op=view&groupName="+groupName);
        
    }     
    
    /**
     * 删除用户组
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void delete(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String groupName = request.getParameter("groupName");
        if(ValidateUtil.isEmpty(groupName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户组名不能为空"));
            return;
        }
        
        userServ.deleteGroup(groupName);
        http.sendRedirect("/group");
    }     
    
    public void doPost(IHttpExchange http) throws IOException, BadMessageException
    {
        doGet(http);
    }

}
