package org.toughradius.components;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.toughradius.annotation.Inject;
import org.toughradius.common.Utils;

@Inject
public class PageContext {
	private static final String DBObject = null;


    public PageContext() {

    }
    public  String substr(String src,int len)
    {
        if(src==null)
            return null;
        if(src.length()<=len)
            return src;
        
        return src.substring(0, len-1);
    }
    
    public  String subhtml(String src,int len)
    {
        if(src==null)
            return null;
        src = Utils.Html2Text(src);
        if(src.length()<=len)
            return src;
        
        return src.substring(0, len-1);
    }
    
   public String encodeUrl(String src) 
   {
       if(src==null)return null;
       try {
            return URLEncoder.encode(src,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return src;
        }
   }
 
    
}
