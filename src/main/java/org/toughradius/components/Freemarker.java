package org.toughradius.components;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.Startable;
import org.toughradius.annotation.Inject;
import org.toughradius.common.Config;
import org.xlightweb.HttpResponse;
import org.xlightweb.IHttpExchange;
import org.xlightweb.IHttpResponse;



import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
@Inject
public class Freemarker implements Startable {
    private final static Log log = LogFactory.getLog(Freemarker.class);
    private Configuration cfg;
    private Config config;
    private PageContext context;

    public void setContext(PageContext context) {
        this.context = context;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public  void start() {
        try {
            cfg = new Configuration();
            cfg.setDirectoryForTemplateLoading(new File("templates"));
            cfg.setTemplateUpdateDelay(0);
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
            cfg.setDefaultEncoding("utf-8");
            cfg.setLocale(Locale.CHINESE);
            cfg.setSharedVariable("sitename", config.getString("radius.version"));
            cfg.setSharedVariable("context", context);

        } catch (Exception e) {
            log.error("freemarker init error", e);
        }

    }

    public void stop() {

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public IHttpResponse render(IHttpExchange http, String tmpname){

    	IHttpResponse resp = null; 
        try {
            Template tmp = cfg.getTemplate(tmpname + ".html");

            Writer out = new StringWriter();

            Map content = new HashMap();

            for (String name : http.getRequest().getAttributeNameSet()) {
                content.put(name, http.getRequest().getAttribute(name));
            }
            content.put("session", http.getSession(true));
            tmp.process(content, out);

            String body = out.toString();
            
            resp = new HttpResponse("text/html", body.getBytes("utf-8"));


        } catch (Exception e) {
            log.error("template file render error " + tmpname, e);
            try {
				resp = new HttpResponse(500, e.getMessage());
			} catch (IOException e1) {
			}

        }
		return resp;
    }
    
    @SuppressWarnings({ "rawtypes" })
    public String render(String tmpname,Map content) {

        try {

            Template tmp = cfg.getTemplate(tmpname + ".html");

            Writer out = new StringWriter();

            tmp.process(content, out);

            String body = out.toString();

            return body;

        } catch (Exception e) {

            log.error("template file render error " + tmpname, e);

        }
		return null;
    }

}
