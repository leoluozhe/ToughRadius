package org.toughradius.view;

import java.io.IOException;

import org.toughradius.Project;
import org.toughradius.common.Config;
import org.toughradius.common.Utils;
import org.toughradius.components.Freemarker;
import org.toughradius.model.RadAdmin;
import org.xlightweb.BadMessageException;
import org.xlightweb.IHttpExchange;
import org.xlightweb.IHttpSession;
import org.xlightweb.Mapping;

@Mapping("/login")
public class LoginAction extends FliterAction{
	private Config cfg;
	public void setCfg(Config cfg) {
        this.cfg = cfg;
    }
	private Freemarker free;
	public void setFree(Freemarker free) {
		this.free = free;
	}

	public void doGet(IHttpExchange http) throws IOException,BadMessageException {
		http.send(free.render(http, "login"));
	}

	public void doPost(IHttpExchange http) throws IOException,BadMessageException {

        IHttpSession session = http.getSession(true);
        String name = http.getRequest().getParameter("loginName");
        String pass = http.getRequest().getParameter("password");
        
        RadAdmin admin = Project.getBaseService().getAdmin(name);
        
        if(admin!=null&&admin.getPassword().equals(pass))
        {
            session.setAttribute("login", name);
            http.sendRedirect("/");
            return;
        }
        http.sendRedirect("/");
	}


public static void main(String[] args) {
}
}