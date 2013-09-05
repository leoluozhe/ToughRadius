package org.toughradius.model;

import java.io.Serializable;

public class RadOnline extends RadOnlineKey implements Serializable {
    private String userName;

    private String ipAddress;

    private String macAddress;

    private String acctStart;

    private String nasPort;

    private Integer startSource;

    private static final long serialVersionUID = 1L;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getAcctStart() {
        return acctStart;
    }

    public void setAcctStart(String acctStart) {
        this.acctStart = acctStart;
    }

    public String getNasPort() {
        return nasPort;
    }

    public void setNasPort(String nasPort) {
        this.nasPort = nasPort;
    }

    public Integer getStartSource() {
        return startSource;
    }

    public void setStartSource(Integer startSource) {
        this.startSource = startSource;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userName=").append(userName);
        sb.append(", ipAddress=").append(ipAddress);
        sb.append(", macAddress=").append(macAddress);
        sb.append(", acctStart=").append(acctStart);
        sb.append(", nasPort=").append(nasPort);
        sb.append(", startSource=").append(startSource);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}