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

	public void doGet(IHttpExchange http) throws IOException,BadMessageException {
	    IHttpRequest request = http.getRequest();
	    String userName = request.getParameter("userName");
	    String groupName = request.getParameter("groupName");
	    
	    RadUserExample example = new RadUserExample();
	    Criteria query = example.createCriteria();
	    if(!ValidateUtil.isEmpty(userName))
	    {
	        query.andUserNameLike(userName);
	    }
        if(!ValidateUtil.isEmpty(groupName))
        {
            query.andGroupNameEqualTo(groupName);
        }
	    List<RadGroup> groups = userServ.getGroups();
	    List<RadUser> users = userServ.getUsers(example, new RowBounds(0,20));
	    request.setAttribute("groups", groups);
	    request.setAttribute("users", users);
		http.send(freemaker.render(http, "user"));
	}
	
    public void add(IHttpExchange http) throws IOException, BadMessageException
    {
        http.send(freemaker.render(http, "user_add"));
    }
    
    public void insert(IHttpExchange http) throws IOException, BadMessageException
    {

        http.sendRedirect("/user");
    }  
    
    public void view(IHttpExchange http) throws IOException, BadMessageException
    {
        IHttpRequest request = http.getRequest();
        String userName = request.getParameter("username");
        
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
        http.send(freemaker.render(http, "user_view"));
    }
    
    public void update(IHttpExchange http) throws IOException, BadMessageException
    {
        http.sendRedirect("/user");
    }  
    
    public void delete(IHttpExchange http) throws IOException, BadMessageException
    {
        http.sendRedirect("/user");
    }   
    
	public void doPost(IHttpExchange http) throws IOException,BadMessageException {

		
	}



}
