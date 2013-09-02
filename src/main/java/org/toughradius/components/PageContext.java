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
package org.toughradius.components;

import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URLEncoder;

import org.toughradius.annotation.Inject;
import org.toughradius.common.DateTimeUtil;
import org.toughradius.common.Utils;

@Inject
public class PageContext {


    public PageContext() {

    }
    
    public String seclet(String value1,String value2){
        if(value1.equals(value2))
        {
            return "selected";
        }
        return "";
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
   
   
   public String getDateTime()
   {
       return DateTimeUtil.getDateTimeString();
   }
   
   public String getUptime(){
       RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
       long ms = mxBean.getUptime()/1000;
       long day = ms / (24 * 60 *60);
       long daymod = ms % (24 * 60 *60);
       long hour = daymod / (60*60);
       long hourmod =  daymod % (60*60);
       long minute = hourmod / 60;
       return String.format("%s天%s小时%s分钟", day,hour,minute);
   }
   
   public String getMemaryUsing(){
       Runtime rt = Runtime.getRuntime();
       long use = (rt.totalMemory() - rt.freeMemory())  / 1024 / 1024;
       long max = rt.maxMemory()  / 1024 / 1024;
       return String.format("已用%sM / 最大%sM", use,max);
   }
 
    
}
