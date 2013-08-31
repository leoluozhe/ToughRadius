package org.toughradius.view;

import java.io.IOException;

import org.toughradius.annotation.AuthAdmin;
import org.toughradius.components.Freemarker;
import org.xlightweb.BadMessageException;
import org.xlightweb.IHttpExchange;
import org.xlightweb.Mapping;

@AuthAdmin
@Mapping( { "/user" })
public class UserAction extends FliterAction{
	
	private Freemarker free;
	public void setFree(Freemarker free) {
		this.free = free;
	}

	public void doGet(IHttpExchange http) throws IOException,BadMessageException {
		http.send(free.render(http, "user"));
	}

	public void doPost(IHttpExchange http) throws IOException,BadMessageException {

		
	}



}
