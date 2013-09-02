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

import org.toughradius.Project;
import org.toughradius.common.DateTimeUtil;
import org.toughradius.model.RadAdmin;
import org.xlightweb.BadMessageException;
import org.xlightweb.IHttpExchange;
import org.xlightweb.IHttpSession;
import org.xlightweb.Mapping;

@Mapping("/login")
public class LoginAction extends FliterAction{
    
	public void doGet(IHttpExchange http) throws IOException,BadMessageException {
		http.send(freemaker.render(http, "login"));
	}

	public void doPost(IHttpExchange http) throws IOException,BadMessageException {

        IHttpSession session = http.getSession(true);
        String name = http.getRequest().getParameter("loginName");
        String pass = http.getRequest().getParameter("password");
        
        RadAdmin admin = Project.getBaseService().getAdmin(name);
        
        if(admin!=null&&admin.getPassword().equals(pass))
        {
            session.setAttribute("login", name);
            session.setAttribute("lastLogin", DateTimeUtil.getDateTimeString());
            session.setAttribute("loginIp", http.getRequest().getRemoteAddr());
            session.setAttribute("version", config.getString("radius.version"));
            http.sendRedirect("/");
            return;
        }
        else
        {
            http.send(freemaker.renderWithAlert(http, "login","用户名和密码不匹配"));
            return;
        }
	}

}