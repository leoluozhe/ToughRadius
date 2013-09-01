/*
 * 版权所有 (C) 2001-2009 深圳市凌亚科技有限公司。保留所有权利。
 * 版本：
 * 修改记录：
 *		1、2009-9-28，zouzhigang创建。 
 */
package org.tinyradius.util;

import java.text.SimpleDateFormat;

import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;

public class RadiusStat
{
    private long startTime = 0;
  
    //认证包
    private int authAll = 0;
    private int authAccept = 0;
    private int authReject = 0;
    private int authAbandon = 0;
    //记帐包
    private int acctAll = 0;
    private int acctStart = 0;
    private int acctUpdate = 0;
    private int acctStop = 0;
    private int acctOn = 0;
    private int acctOff = 0;
    private int acctAbandon = 0;
    private int acctRetry = 0;
    
    public RadiusStat()
    {
        setStartTime(System.currentTimeMillis());
    }
    
  
    public long getStartTime()
    {
        return startTime;
    }
    
    public String getStartTimeString()
    {
       return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(startTime));
    }
    
    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }
    
    public void clear()
    {
        //认证包
        this.authAll = 0;
        this.authAccept = 0;
        this.authReject = 0;
        this.authAbandon = 0;
        //记帐包
        this.acctAll = 0;
        this.acctStart = 0;
        this.acctUpdate = 0;
        this.acctStop = 0;
        this.acctOn = 0;
        this.acctOff = 0;
        this.acctAbandon = 0;
        this.acctRetry = 0;
    }
    
    public int getAuthAll()
    {
        return authAll;
    }
    
    public void addAuthAll()
    {
        this.authAll++;
    }
    
    public int getAuthAccept()
    {
        return authAccept;
    }
    
    public void addAuthAccept()
    {
        this.authAccept++;
    }
    
    public int getAuthReject()
    {
        return authReject;
    }
    
    public void addAuthReject()
    {
        this.authReject++;
    }
    
    public int getAuthAbandon()
    {
        return authAbandon;
    }
    
    public void addAuthAbandon()
    {
        this.authAbandon++;
    }
    
    public int getAcctAll()
    {
        return acctAll;
    }
    
    public void addAcctAll()
    {
        this.acctAll++;
    }
    
    public int getAcctStart()
    {
        return acctStart;
    }
    
    public void addAcctStart()
    {
        this.acctStart++;
    }
    
    public int getAcctUpdate()
    {
        return acctUpdate;
    }
    
    public void addAcctUpdate()
    {
        this.acctUpdate++;
    }
    
    public int getAcctStop()
    {
        return acctStop;
    }
    
    public void addAcctStop()
    {
        this.acctStop++;
    }
    
    public int getAcctOn()
    {
        return acctOn;
    }
    
    public void addAcctOn()
    {
        this.acctOn++;
    }
    
    public int getAcctOff()
    {
        return acctOff;
    }
    
    public void addAcctOff()
    {
        this.acctOff++;
    }
    
    public int getAcctAbandon()
    {
        return acctAbandon;
    }
    
    public void addAcctAbandon()
    {
        this.acctAbandon++;
    }
    
    public int getAcctRetry()
    {
        return acctRetry;
    }
    
    public void addAcctRetry()
    {
        this.acctRetry++;
    }
  
  
}
