package org.toughradius.view;

import java.io.IOException;

import org.toughradius.annotation.AuthAdmin;
import org.toughradius.annotation.AuthUser;
import org.toughradius.components.Freemarker;
import org.xlightweb.BadMessageException;
import org.xlightweb.IHttpExchange;
import org.xlightweb.IHttpRequest;
import org.xlightweb.IHttpRequestHandler;
import org.xlightweb.IHttpSession;


public abstract class FliterAction implements IHttpRequestHandler{
	private Freemarker freemaker;
	public void setFreemaker(Freemarker freemaker) {
		this.freemaker = freemaker;
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
        
        if(req.getMethod().toLowerCase().equals("get"))
        	doGet(http);
        else if(req.getMethod().toLowerCase().equals("get"))
        	doPost(http);
        else 
        	doPost(http);
        
	}
	
	public abstract void doGet(IHttpExchange http) throws IOException,BadMessageException;
	public abstract void doPost(IHttpExchange http) throws IOException,BadMessageException;
}

