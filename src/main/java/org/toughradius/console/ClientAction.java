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
import org.toughradius.components.Freemarker;
import org.toughradius.constant.ClientTypes;
import org.toughradius.model.RadClient;
import org.xlightweb.BadMessageException;
import org.xlightweb.IHttpExchange;
import org.xlightweb.IHttpRequest;
import org.xlightweb.Mapping;

@AuthAdmin
@Mapping( { "/client/*" })
public class ClientAction extends FliterAction{
	
	private Freemarker free;
	public void setFree(Freemarker free) {
		this.free = free;
	}

	/**
	 * 客户端查询
	 */
	public void doGet(IHttpExchange http) throws IOException,BadMessageException {
	    IHttpRequest request = http.getRequest();
	    List<RadClient> clients = baseServ.getClients();
	    request.setAttribute("clients", clients);
		http.send(free.render(http, "client"));
	}

	/**
	 * 新增客户端表单
	 * @param http
	 * @throws IOException
	 * @throws BadMessageException
	 */
    public void add(IHttpExchange http) throws IOException,BadMessageException {
        http.getRequest().setAttribute("typeList", ClientTypes.ClientTypeList);
        http.send(freemaker.render(http, "client_add"));
    }

    /**
     * 保存客户端信息
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void insert(IHttpExchange http) throws IOException,BadMessageException {

        
        IHttpRequest request = http.getRequest();
        request.setAttribute("typeList", ClientTypes.ClientTypeList);
        String address = request.getParameter("address");
        String clientType = request.getParameter("clientType");
        String secret = request.getParameter("secret");
        String clientDesc = request.getParameter("clientDesc");
        
        if(!ValidateUtil.isIP(address, false))
        {
            http.send(freemaker.renderWithError(http, "client_add","客户端地址不符合要求"));
            return;
        }
        
        if(ValidateUtil.isEmpty(secret))
        {
             http.send(freemaker.renderWithError(http, "client_add","共享密钥不能为空"));
             return;
        }
        
        if (ValidateUtil.isEmpty(clientDesc))
        {
            clientDesc = clientType;
        }
        
        RadClient client = new RadClient();
        client.setAddress(address);
        client.setClientType(clientType);
        client.setSecret(secret);
        client.setClientDesc(clientDesc);
        baseServ.addClient(client);
        
        http.sendRedirect("/client");
    }
    
    /**
     * 修改客户端表单
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void modify(IHttpExchange http) throws IOException,BadMessageException {
        IHttpRequest request = http.getRequest();
        http.getRequest().setAttribute("typeList", ClientTypes.ClientTypeList);
        RadClient client = baseServ.getClient(request.getParameter("address"));
        if(client==null)
        {
            http.send(freemaker.renderWithError(http, "error","没有符合条件的客户端信息"));
            return;
        }
        request.setAttribute("client", client);
        http.send(freemaker.render(http, "client_update"));
    }
    
    /**
     * 更新保存客户端数据
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void update(IHttpExchange http) throws IOException,BadMessageException {
        IHttpRequest request = http.getRequest();
        RadClient client = baseServ.getClient(request.getParameter("address"));
        if(client==null)
        {
            http.send(freemaker.renderWithError(http, "error","没有符合条件的客户端信息"));
            return;
        }
        
        String clientType = request.getParameter("clientType");
        String secret = request.getParameter("secret");
        String clientDesc = request.getParameter("clientDesc");
        
        if(ValidateUtil.isEmpty(secret))
        {
             http.send(freemaker.renderWithError(http, "client_add","共享密钥不能为空"));
             return;
        }
        
        if (ValidateUtil.isEmpty(clientDesc))
        {
            clientDesc = clientType;
        }
        
        client.setClientType(clientType);
        client.setSecret(secret);
        client.setClientDesc(clientDesc);
        baseServ.updateClient(client);
        
        http.sendRedirect("/client");
    }

    /**
     * 删除客户端信息
     * @param http
     * @throws IOException
     * @throws BadMessageException
     */
    public void delete(IHttpExchange http) throws IOException, BadMessageException
    {
        baseServ.deleteClient(http.getRequest().getParameter("address"));
        http.sendRedirect("/client");
    }
    
    @Override
    public void doPost(IHttpExchange http) throws IOException, BadMessageException
    {
        
    }


}
