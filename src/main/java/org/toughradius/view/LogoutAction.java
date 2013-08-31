package org.toughradius.view;

import java.io.IOException;

import org.toughradius.components.Freemarker;
import org.xlightweb.BadMessageException;
import org.xlightweb.IHttpExchange;
import org.xlightweb.Mapping;


@Mapping( { "/logout" })
public class LogoutAction extends FliterAction{
	
	private Freemarker free;
	public void setFree(Freemarker free) {
		this.free = free;
	}

	public void doGet(IHttpExchange http) throws IOException, BadMessageException {
        http.getSession(true)
            .invalidate();
        http.sendRedirect("/");
    }

	public void doPost(IHttpExchange http) throws IOException,BadMessageException {
	    http.getSession(true).invalidate();
        http.sendRedirect("/");
	}



}
