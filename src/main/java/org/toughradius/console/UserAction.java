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

import org.apache.ibatis.session.RowBounds;
import org.toughradius.annotation.AuthAdmin;
import org.toughradius.common.ValidateUtil;
import org.toughradius.constant.Constant;
import org.toughradius.model.RadGroup;
import org.toughradius.model.RadGroupMeta;
import org.toughradius.model.RadUser;
import org.toughradius.model.RadUserExample;
import org.toughradius.model.RadUserExample.Criteria;
import org.toughradius.model.RadUserMeta;
import org.xlightweb.BadMessageException;
import org.xlightweb.IHttpExchange;
import org.xlightweb.IHttpRequest;
import org.xlightweb.Mapping;

@AuthAdmin
@Mapping( { "/user" })
public class UserAction extends FliterAction{

    /**
     * 查询用户集合
     */
	public void doGet(IHttpExchange http) throws IOException,BadMessageException {
	    IHttpRequest request = http.getRequest();
	    String userName = request.getParameter("userName");
	    String groupName = request.getParameter("groupName");
	    
	    RadUserExample example = new RadUserExample();
	    Criteria query = example.createCriteria();
	    if(!ValidateUtil.isEmpty(userName))
	    {
	        query.andUserNameLike("%"+userName+"%");
	    }
        if(!ValidateUtil.isEmpty(groupName))
        {
            query.andGroupNameEqualTo(groupName);
        }
	    List<RadGroup> groups = userServ.getGroups();
	    List<RadUser> users = userServ.getUsers(example, new RowBounds(0,10));
	    request.setAttribute("groups", groups);
	    request.setAttribute("users", users);
		http.send(freemaker.render(http, "user"));
	}
	
	/**
	 * 新增用户表单
	 * @param http
	 * @throws IOException
	 * @throws BadMessageException
	 */
    public void add(IHttpExchange http) throws IOException, BadMessageException
    {
        List<RadGroup> groups = userServ.getGroups();
        http.getRequest().setAttribute("groups", groups);
        http.send(freemaker.render(http, "user_add"));
    }
    
    /**
     * 保存用户资料
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void insert(IHttpExchange http) throws IOException, BadMessageException
    {

        IHttpRequest request = http.getRequest();
        String userName = request.getParameter("userName");
        String groupName = request.getParameter("groupName");
        String password = request.getParameter("password");
        if(ValidateUtil.isEmpty(userName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户名不能为空"));
            return;
        }
        if(ValidateUtil.isEmpty(groupName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户组名不能为空"));
            return;
        }
        if(ValidateUtil.isEmpty(password))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户密码不能为空"));
            return;
        }
        
        if(userServ.getUser(userName)!=null)
        {
            http.send(freemaker.renderWithAlert(http, "error","用户已经存在"));
            return;
        }
        
        RadUser user = new RadUser();
        user.setUserName(userName);
        user.setGroupName(groupName);
        user.setPassword(password);
        userServ.addUser(user);
        
        http.sendRedirect("/user?op=view&userName="+userName);
    }  
    
    /**
     * 用户详细资料
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void view(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String userName = request.getParameter("userName");
        
        RadUser user = userServ.getUser(userName);
        
        if(user==null)
        {
            http.send(freemaker.renderWithAlert(http, "error","用户不存在"));
            return;
        }
        
        List<RadUserMeta> metas = userServ.getUserMetas(userName);
        List<RadGroup> groups = userServ.getGroups();
        request.setAttribute("groups", groups);
        request.setAttribute("metas", metas);
        request.setAttribute("user", user);  
        request.setAttribute("UserMetaList",Constant.UserMetaList );
        http.send(freemaker.render(http, "user_view"));
    }
    
    /**
     * 更新用户资料
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void update(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String userName = request.getParameter("userName");
        String groupName = request.getParameter("groupName");
        String password = request.getParameter("password");
        if(ValidateUtil.isEmpty(userName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户名不能为空"));
            return;
        }
        if(ValidateUtil.isEmpty(groupName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户组名不能为空"));
            return;
        }
        if(ValidateUtil.isEmpty(password))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户密码不能为空"));
            return;
        }
        
        RadUser user = userServ.getUser(userName);
        
        if(user==null)
        {
            http.send(freemaker.renderWithAlert(http, "error","用户不存在"));
            return;
        }
        
        user.setGroupName(groupName);
        user.setPassword(password);
        userServ.updateUser(user);
        
        http.sendRedirect("/user?op=view&userName="+userName);
    }  
    
    public void delete(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String userName = request.getParameter("userName");
        if(ValidateUtil.isEmpty(userName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户名不能为空"));
            return;
        }
        
        userServ.deleteUser(userName);
        http.sendRedirect("/user");
    }   
    
    /**
     * 增加用户属性
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void addMeta(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String userName = request.getParameter("userName");
        String metaName = request.getParameter("addMetaName");
        String metaValue = request.getParameter("addMetaValue");
        
        if(ValidateUtil.isEmpty(userName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户名不能为空"));
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
        
        if(metaName.equals(Constant.USER_PERIOD.value()))
        {
            if(!ValidateUtil.isRegExp(metaValue, "\\d{2}:\\d{2}-\\d{2}:\\d{2}"))
            {
                http.send(freemaker.renderWithAlert(http, "error","时段属性值格式必须为 hh:ss-hh:ss"));
                return;
            }
        }
        
        RadUserMeta meta = userServ.getUserMeta(userName, metaName);
        
        if(meta == null)
        {
            meta = new RadUserMeta();
            meta.setUserName(userName);
            meta.setName(metaName);
            meta.setValue(metaValue);
            meta.setDesc(Constant.getGroupMetaDesc(metaName));
            userServ.addUserMeta(meta);
        }
        else
        {
            meta.setValue(metaValue);
            meta.setDesc(Constant.getGroupMetaDesc(metaName));
            userServ.updateUserMeta(meta);
            
        }
        
        http.sendRedirect("/user?op=view&userName="+userName);
        
    } 
    
    /**
     * 更新用户资料
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void updateMeta(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String userName = request.getParameter("userName");
        String metaName = request.getParameter("metaName");
        String metaValue = request.getParameter("metaValue");
        
        if(ValidateUtil.isEmpty(userName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户名不能为空"));
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
        
        if(metaName.equals(Constant.USER_PERIOD.value()))
        {
            if(!ValidateUtil.isRegExp(metaValue, "\\d{2}:\\d{2}-\\d{2}:\\d{2}"))
            {
                http.send(freemaker.renderWithAlert(http, "error","时段属性值格式必须为 hh:ss-hh:ss"));
                return;
            }
        }
        
        RadUserMeta meta = userServ.getUserMeta(userName, metaName);
        
        if(meta == null)
        {
            http.send(freemaker.renderWithAlert(http, "error","属性不存在"));
            return;
        }
        else
        {
            meta.setValue(metaValue);
            userServ.updateUserMeta(meta);
        }
        
        http.sendRedirect("/user?op=view&userName="+userName);
        
    }  
    
    /**
     * 删除属性
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void deleteMeta(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String userName = request.getParameter("userName");
        String metaName = request.getParameter("metaName");
        
        if(ValidateUtil.isEmpty(userName))
        {
            http.send(freemaker.renderWithAlert(http, "error","用户名不能为空"));
            return;
        }
        
        if(ValidateUtil.isEmpty(metaName))
        {
            http.send(freemaker.renderWithAlert(http, "error","属性名不能为空"));
            return;
        }
        
        
        userServ.deleteUserMeta(userName, metaName);
        http.sendRedirect("/user?op=view&userName="+userName);
        
    }  
    
	public void doPost(IHttpExchange http) throws IOException,BadMessageException {

		doGet(http);
	}



}
