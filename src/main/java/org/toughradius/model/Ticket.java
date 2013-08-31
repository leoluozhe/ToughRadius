package org.toughradius.model;

import java.io.Serializable;

public class Ticket implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String ticketCode;
    private String userName;//用户名
    private int acctStatusType;//计费类型
    private String eventTimestamp;//
    private String nasIpAddress;//接入服务器的IP地址
    private String acctSessionId;//帐会话标识
    private String acctStartTime;//计费开始时间
    private String acctStopTime;//计费结束时间
    private int acctSessionTime;//会话时间
    private int acctInputGigawords;//会话下行的字（4字节）的吉倍数
    private long acctInputOctets;//输入字节总数
    private int acctInputPackets;//输入数据包
    private int acctOutputGigawords;//会话上行的字（4字节）的吉倍数
    private long acctOutputOctets;//输出字节总数
    private int acctOutputPackets;//输出数据包
    private String callingStationId;//用户呼叫号码
    private int nasPort;//网络接入服务器（NAS）端口
    private int nasPortType;//网络接入服务器（NAS）端口类型
    private String nasPortId;//网络接入服务器 (NAS) 编号信息
    private String nasClass;//类
    private int serviceType;//服务类型
    private int sessionTimeout;//会话超时
    private String framedIpAddress;//客户端的IP地址
    private String framedIpNetmask;//客户端的子网掩码
    private int acctTerminateCause;//记帐中止原因
    private int startSource;//上线包来源(1=start,2=stop,3=update,7=acct-on,8=acct-off)
    private int stopSource;//下线包来源(1=start,2=stop,3=update,7=acct-on,8=acct-off)
    private int acctFee;//计费金额，单位：分
    private int feeReceivables;//应扣信誉额，单位：分
    private int isDeduct;//是否已扣费（0/1 没扣费/已扣费）

    public String toString()
    {
        return new StringBuffer("上网计费记录:[")
            .append("ticketCode=").append(ticketCode).append(";")
            .append("nasIpAddress=").append(nasIpAddress).append(";")
            .append("acctSessionId=").append(acctSessionId).append(";")
            .append("userName=").append(userName).append(";")
            .append("acctStartTime=").append(acctStartTime).append(";")
            .append("acctStopTime=").append(acctStopTime).append(";")
            .append("acctSessionTime=").append(acctSessionTime).append(";")
            .append("acctInputGigawords=").append(acctInputGigawords).append(";")
            .append("acctInputOctets=").append(acctInputOctets).append(";")
            .append("acctInputPackets=").append(acctInputPackets).append(";")
            .append("acctOutputGigawords=").append(acctOutputGigawords).append(";")
            .append("acctOutputOctets=").append(acctOutputOctets).append(";")
            .append("acctOutputPackets=").append(acctOutputPackets).append(";")
            .append("callingStationId=").append(callingStationId).append(";")
            .append("nasPort=").append(nasPort).append(";")
            .append("nasPortType=").append(nasPortType).append(";")
            .append("nasPortId=").append(nasPortId).append(";")
            .append("nasClass=").append(nasClass).append(";")
            .append("serviceType=").append(serviceType).append(";")
            .append("sessionTimeout=").append(sessionTimeout).append(";")
            .append("framedIpAddress=").append(framedIpAddress).append(";")
            .append("framedIpNetmask=").append(framedIpNetmask).append(";")
            .append("acctTerminateCause=").append(acctTerminateCause).append(";")
            .append("startSource=").append(startSource).append(";")
            .append("stopSource=").append(stopSource).append(";")
            .append("acctFee=").append(acctFee).append(";")
            .append("feeReceivables=").append(feeReceivables).append(";")
            .append("isDeduct=").append(isDeduct).append(";")
            .append("]").toString();
    }

    
    public String toTicket()
    {
        return new StringBuffer()
            .append(getNasIpAddress()).append(",")//NOT NULL
            .append(getAcctSessionId()).append(",")//NOT NULL
            .append(getUserName()).append(",")//NOT NULL
            .append(getCallingStationId()==null?"":getCallingStationId()).append(",")//String
            
            .append(getAcctStartTime()).append(",")//NOT NULL
            .append(getAcctStopTime()).append(",")//NOT NULL
            .append(getAcctSessionTime()).append(",")//int
            
            .append(getAcctInputGigawords()).append(",")//int
            .append(getAcctInputOctets()).append(",")//long
            .append(getAcctInputPackets()).append(",")//int
            .append(getAcctOutputGigawords()).append(",")//int
            .append(getAcctOutputOctets()).append(",")//long
            .append(getAcctOutputPackets()).append(",")//int
            
            .append(getNasPort()).append(",")//int
            .append(getNasPortType()).append(",")//int
            .append(getNasPortId()==null?"":getNasPortId()).append(",")//String
            .append(getNasClass()==null?"":getNasClass()).append(",")
            
            .append(getServiceType()).append(",")//int
            .append(getSessionTimeout()).append(",")//int
            .append(getFramedIpAddress()==null?"":getFramedIpAddress()).append(",")
            .append(getFramedIpNetmask()==null?"":getFramedIpNetmask()).append(",")
            .append(getAcctTerminateCause()).append(",")//int
            
            .append(getStartSource()).append(",")//int
            .append(getStopSource()).append(",")//int
            .append(getAcctFee()).append(",")//int
            .append(getFeeReceivables()).append(",")//int
            .append(getIsDeduct())//int
            .toString();
    }
    

    public int getAcctStatusType()
    {
        return acctStatusType;
    }


    public void setAcctStatusType(int acctStatusType)
    {
        this.acctStatusType = acctStatusType;
    }


    public String getEventTimestamp()
    {
        return eventTimestamp;
    }


    public void setEventTimestamp(String eventTimestamp)
    {
        this.eventTimestamp = eventTimestamp;
    }


    public String getTicketCode()
    {
        return ticketCode;
    }


    public void setTicketCode(String ticketCode)
    {
        this.ticketCode = ticketCode;
    }


    public String getUserName()
    {
        return userName;
    }


    public void setUserName(String userName)
    {
        this.userName = userName;
    }


    public String getNasIpAddress()
    {
        return nasIpAddress;
    }


    public void setNasIpAddress(String nasIpAddress)
    {
        this.nasIpAddress = nasIpAddress;
    }


    public String getAcctSessionId()
    {
        return acctSessionId;
    }


    public void setAcctSessionId(String acctSessionId)
    {
        this.acctSessionId = acctSessionId;
    }


    public String getAcctStartTime()
    {
        return acctStartTime;
    }


    public void setAcctStartTime(String acctStartTime)
    {
        this.acctStartTime = acctStartTime;
    }


    public String getAcctStopTime()
    {
        return acctStopTime;
    }


    public void setAcctStopTime(String acctStopTime)
    {
        this.acctStopTime = acctStopTime;
    }


    public int getAcctSessionTime()
    {
        return acctSessionTime;
    }


    public void setAcctSessionTime(int acctSessionTime)
    {
        this.acctSessionTime = acctSessionTime;
    }


    public int getAcctInputGigawords()
    {
        return acctInputGigawords;
    }


    public void setAcctInputGigawords(int acctInputGigawords)
    {
        this.acctInputGigawords = acctInputGigawords;
    }


    public long getAcctInputOctets()
    {
        return acctInputOctets;
    }


    public void setAcctInputOctets(long acctInputOctets)
    {
        this.acctInputOctets = acctInputOctets;
    }


    public int getAcctInputPackets()
    {
        return acctInputPackets;
    }


    public void setAcctInputPackets(int acctInputPackets)
    {
        this.acctInputPackets = acctInputPackets;
    }


    public int getAcctOutputGigawords()
    {
        return acctOutputGigawords;
    }


    public void setAcctOutputGigawords(int acctOutputGigawords)
    {
        this.acctOutputGigawords = acctOutputGigawords;
    }


    public long getAcctOutputOctets()
    {
        return acctOutputOctets;
    }


    public void setAcctOutputOctets(long acctOutputOctets)
    {
        this.acctOutputOctets = acctOutputOctets;
    }


    public int getAcctOutputPackets()
    {
        return acctOutputPackets;
    }


    public void setAcctOutputPackets(int acctOutputPackets)
    {
        this.acctOutputPackets = acctOutputPackets;
    }


    public String getCallingStationId()
    {
        return callingStationId;
    }


    public void setCallingStationId(String callingStationId)
    {
        this.callingStationId = callingStationId;
    }


    public int getNasPort()
    {
        return nasPort;
    }


    public void setNasPort(int nasPort)
    {
        this.nasPort = nasPort;
    }


    public int getNasPortType()
    {
        return nasPortType;
    }


    public void setNasPortType(int nasPortType)
    {
        this.nasPortType = nasPortType;
    }


    public String getNasPortId()
    {
        return nasPortId;
    }


    public void setNasPortId(String nasPortId)
    {
        this.nasPortId = nasPortId;
    }


    public String getNasClass()
    {
        return nasClass;
    }


    public void setNasClass(String nasClass)
    {
        this.nasClass = nasClass;
    }


    public int getServiceType()
    {
        return serviceType;
    }


    public void setServiceType(int serviceType)
    {
        this.serviceType = serviceType;
    }


    public int getSessionTimeout()
    {
        return sessionTimeout;
    }


    public void setSessionTimeout(int sessionTimeout)
    {
        this.sessionTimeout = sessionTimeout;
    }


    public String getFramedIpAddress()
    {
        return framedIpAddress;
    }


    public void setFramedIpAddress(String framedIpAddress)
    {
        this.framedIpAddress = framedIpAddress;
    }


    public String getFramedIpNetmask()
    {
        return framedIpNetmask;
    }


    public void setFramedIpNetmask(String framedIpNetmask)
    {
        this.framedIpNetmask = framedIpNetmask;
    }


    public int getAcctTerminateCause()
    {
        return acctTerminateCause;
    }


    public void setAcctTerminateCause(int acctTerminateCause)
    {
        this.acctTerminateCause = acctTerminateCause;
    }


    public int getStartSource()
    {
        return startSource;
    }


    public void setStartSource(int startSource)
    {
        this.startSource = startSource;
    }


    public int getStopSource()
    {
        return stopSource;
    }


    public void setStopSource(int stopSource)
    {
        this.stopSource = stopSource;
    }


    public int getAcctFee()
    {
        return acctFee;
    }


    public void setAcctFee(int acctFee)
    {
        this.acctFee = acctFee;
    }


    public int getFeeReceivables()
    {
        return feeReceivables;
    }


    public void setFeeReceivables(int feeReceivables)
    {
        this.feeReceivables = feeReceivables;
    }


    public int getIsDeduct()
    {
        return isDeduct;
    }


    public void setIsDeduct(int isDeduct)
    {
        this.isDeduct = isDeduct;
    }

    
    
}
