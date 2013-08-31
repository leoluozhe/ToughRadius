package org.toughradius.view;

import java.io.IOException;

import org.toughradius.annotation.AuthAdmin;
import org.toughradius.components.Freemarker;
import org.xlightweb.BadMessageException;
import org.xlightweb.IHttpExchange;
import org.xlightweb.Mapping;

@AuthAdmin
@Mapping( { "/blacklist" })
public class BlacklistAction extends FliterAction{
	
	private Freemarker free;
	public void setFree(Freemarker free) {
		this.free = free;
	}

	public void doGet(IHttpExchange http) throws IOException,BadMessageException {
		http.send(free.render(http, "blacklist"));
	}

	public void doPost(IHttpExchange http) throws IOException,BadMessageException {

		
	}



}