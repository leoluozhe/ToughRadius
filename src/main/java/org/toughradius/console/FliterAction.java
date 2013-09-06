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
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toughradius.annotation.AuthAdmin;
import org.toughradius.common.Config;
import org.toughradius.components.BaseService;
import org.toughradius.components.Freemarker;
import org.toughradius.components.StatService;
import org.toughradius.components.UserService;
import org.xlightweb.BadMessageException;
import org.xlightweb.IHttpExchange;
import org.xlightweb.IHttpRequest;
import org.xlightweb.IHttpRequestHandler;
import org.xlightweb.IHttpSession;

public abstract class FliterAction implements IHttpRequestHandler{
    private final static Log log = LogFactory.getLog(FliterAction.class);
    protected Freemarker freemaker;
    protected Config config;
	protected BaseService baseServ;
	protected UserService userServ;
	protected StatService statServ;
	public void setFreemaker(Freemarker freemaker) {
		this.freemaker = freemaker;
	}

	public void setBaseServ(BaseService baseServ)
    {
        this.baseServ = baseServ;
    }
	
	public void setUserServ(UserService userServ)
    {
        this.userServ = userServ;
    }

    public void setStatServ(StatService statServ) {
        this.statServ = statServ;
    }
	public void setConfig(Config config)
    {
        this.config = config;
    }
	public void onRequest(IHttpExchange http) throws IOException,BadMessageException {

		IHttpRequest req = http.getRequest();
        req.setHeader("Content-Type", req.getContentType() + "; charset=UTF-8");
        IHttpSession session = http.getSession(true);
        if(this.getClass().getAnnotation(AuthAdmin.class)!=null){
            if (session.getAttribute("login") == null) {
                http.sendRedirect("/login");
                return;
            }
        }
        
        String op = req.getParameter("op");
        
        if(op!=null&&!"".equals(op))
        {
            execute(http, op);
            return;
        }
        
        if(req.getMethod().toLowerCase().equals("get"))
        	doGet(http);
        else if(req.getMethod().toLowerCase().equals("get"))
        	doPost(http);
        else 
        	doPost(http);
        
	}
	
	private void execute(IHttpExchange http,String method) 
	{
        if(method==null||"".equals(method))
        {
            http.sendError(404);
            return;
        }
        Class<?>[] classes = new Class[1];
        classes[0] = IHttpExchange.class;
        try
        {
            Method m = getClass().getMethod(method, classes);
            m.invoke(this,new Object[]{http});
        }
        catch (Exception e)
        {
            http.sendError(500, "server error");
            log.error("execute method "+method+" error",e);
        }
	}
	
	public abstract void doGet(IHttpExchange http) throws IOException,BadMessageException;
	public abstract void doPost(IHttpExchange http) throws IOException,BadMessageException;
}

