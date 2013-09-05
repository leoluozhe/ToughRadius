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
package org.toughradius.server;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.Startable;
import org.tinyradius.attribute.IntegerAttribute;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RadiusServer;
import org.toughradius.Project;
import org.toughradius.common.Config;
import org.toughradius.common.DateTimeUtil;
import org.toughradius.common.ValidateUtil;
import org.toughradius.components.CacheService;
import org.toughradius.components.StatService;
import org.toughradius.components.UserService;
import org.toughradius.constant.Constant;
import org.toughradius.constant.FeePolicys;
import org.toughradius.model.RadClient;
import org.toughradius.model.RadGroupMeta;
import org.toughradius.model.RadOnline;
import org.toughradius.model.RadUser;
import org.toughradius.model.RadUserMeta;
import org.toughradius.model.Ticket;

public class AcctServer extends RadiusServer implements Startable {
    private static Log logger = LogFactory.getLog(AcctServer.class);
    private static final Log acctLog = LogFactory.getLog("acct");
    private Config config;
    private CacheService cacheServ = Project.getCacheService();
    private StatService statServ = Project.getStatService();
    private UserService userServ = Project.getUserService();
    

    public AcctServer() {
    }

    public AcctServer(Config config) {
        this.config = config;
    }

    public void start() {
        logger.info("start AcctServer...");
        this.setAcctPort(config.getInt("radius.acctPort", 1813));
        this.start(false, true);
    }

    @Override
    public String getSharedSecret(InetSocketAddress client) {
        RadClient rc = Project.getBaseService().getClient(client.getAddress().getHostAddress());
        return rc != null ? rc.getSecret() : null;
    }

    @Override
    public String getUserPassword(String userName) {
        RadUser user = Project.getUserService().getUser(userName);
        return user != null ? user.getPassword() : null;
    }

    /**
     * 计费请求处理
     */
    public RadiusPacket accountingRequestReceived(final AccountingRequest accountingRequest,
            final InetSocketAddress client) throws RadiusException {
        worker.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (accountingRequest.getAcctStatusType()) {
                    case AccountingRequest.ACCT_STATUS_TYPE_START:
                        startAcct(accountingRequest, client.getAddress().getHostAddress());
                        break;
                    case AccountingRequest.ACCT_STATUS_TYPE_STOP:
                        stopAcct(accountingRequest, client.getAddress().getHostAddress());
                        break;
                    case AccountingRequest.ACCT_STATUS_TYPE_INTERIM_UPDATE:
                        updateAcct(accountingRequest, client.getAddress().getHostAddress());
                        break;
                    case AccountingRequest.ACCT_STATUS_TYPE_ACCOUNTING_ON:
                        setAcctOnOff(AccountingRequest.ACCT_STATUS_TYPE_ACCOUNTING_ON, client.getAddress()
                                .getHostAddress());
                        break;
                    case AccountingRequest.ACCT_STATUS_TYPE_ACCOUNTING_OFF:
                        setAcctOnOff(AccountingRequest.ACCT_STATUS_TYPE_ACCOUNTING_OFF, client.getAddress()
                                .getHostAddress());
                        break;
                    default:
                        break;

                    }
                } catch (RadiusException e) {
                    logger.error("accounting error", e);
                }
            }
        });

        return super.accountingRequestReceived(accountingRequest, client);
    }

    /**
     * 记账开始请求
     * @param req
     * @param client
     * @throws RadiusException
     */
    private void startAcct(AccountingRequest req, String client) throws RadiusException {
        RadOnline online = statServ.getOnline(client, req.getAttributeValue(""));
        if (online != null) {
            if (logger.isInfoEnabled())
                logger.info("Accounting start request repeated");
        }
        RadUser user = cacheServ.getUser(req.getUserName());
        if (user == null) {
            if (logger.isInfoEnabled())
                logger.info("Accounting start request , but user not exist");

            return;
        }

        online = new RadOnline();
        online.setAcctStart(DateTimeUtil.getDateTimeString());
        online.setClientAddress(client);
        online.setIpAddress(req.getAttributeValue("Framed-IP-Address"));
        online.setMacAddress(req.getAttributeValue("Calling-Station-Id"));
        online.setNasPort(req.getAttributeValue("NAS-Port"));
        online.setSessionId(req.getAttributeValue("Acct-Session-Id"));
        online.setUserName(user.getUserName());
        online.setStartSource(AccountingRequest.ACCT_STATUS_TYPE_START);
        statServ.addOnline(online);

        if (logger.isInfoEnabled())
            logger.info("Accounting start request , add new online");

    }

    /**
     * 记账更新请求
     * @param req
     * @param client
     * @throws RadiusException
     */
    private void updateAcct(AccountingRequest req, String client) throws RadiusException {
        RadOnline online = statServ.getOnline(client, req.getAttributeValue(""));
        if (online == null) {
            if (logger.isInfoEnabled())
                logger.info("Accounting update request repeated");

            RadUser user = cacheServ.getUser(req.getUserName());
            if (user == null) {
                if (logger.isInfoEnabled())
                    logger.info("Accounting update request , but user not exist");

                return;
            }
            online = new RadOnline();
            online.setAcctStart(DateTimeUtil.getDateTimeString());
            online.setClientAddress(client);
            online.setIpAddress(req.getAttributeValue("Framed-IP-Address"));
            online.setMacAddress(req.getAttributeValue("Calling-Station-Id"));
            online.setNasPort(req.getAttributeValue("NAS-Port"));
            online.setSessionId(req.getAttributeValue("Acct-Session-Id"));
            online.setUserName(user.getUserName());

            // 计算起始时间
            RadiusAttribute attr = req.getAttribute("Acct-Session-Time");
            int sessionTime = ((IntegerAttribute) attr).getAttributeValueInt();
            String updateTime = DateTimeUtil.getDateTimeString();
            String startTime = DateTimeUtil.getPreviousDateTimeBySecondString(updateTime, sessionTime);

            online.setAcctStart(startTime);
            online.setStartSource(AccountingRequest.ACCT_STATUS_TYPE_INTERIM_UPDATE);// 上线包来源(1=start,3=interim,2=stop,7=nas-acct-start,8=nas-acct-stop)

            statServ.addOnline(online);
        } else {
            online.setIpAddress(req.getAttributeValue("Framed-IP-Address"));
        }

        if (logger.isInfoEnabled())
            logger.info("Accounting update request success");

    }

    /**
     * 记账结束请求
     * @param req
     * @param client
     * @throws RadiusException
     */
    private void stopAcct(AccountingRequest req, String client) throws RadiusException {

        Ticket ticket = Ticket.fromRequest(req);
        if (ticket.getNasIpAddress() == null)
            ticket.setNasIpAddress(client);

        RadOnline online = statServ.getOnlineAndRemove(client, ticket.getAcctSessionId());
        if (online == null) {
            // 计算起始时间
            int sessionTime = ticket.getAcctSessionTime();
            String stopTime = DateTimeUtil.getDateTimeString();
            String startTime = DateTimeUtil.getPreviousDateTimeBySecondString(stopTime, sessionTime);
            ticket.setAcctStartTime(startTime);
            ticket.setAcctStopTime(stopTime);
            ticket.setStopSource(AccountingRequest.ACCT_STATUS_TYPE_STOP);
            ticket.setStartSource(AccountingRequest.ACCT_STATUS_TYPE_STOP);
        } else {
            ticket.setAcctStartTime(online.getAcctStart());
            ticket.setAcctStopTime(DateTimeUtil.getDateTimeString());
            ticket.setStopSource(online.getStartSource());
            ticket.setStartSource(AccountingRequest.ACCT_STATUS_TYPE_STOP);
            if (ValidateUtil.isEmpty(ticket.getFramedIpAddress()))
                ticket.setFramedIpAddress(online.getIpAddress());
        }
        
        RadUser  user = cacheServ.getUser(ticket.getUserName());
        if(user==null)
        {
            acctLog.info(ticket.toTicket());
            return;
        }
        
        RadGroupMeta policy = cacheServ.getGroupMeta(user.getGroupName(), Constant.GROUP_FEE_POLICY.value());
        RadGroupMeta price = cacheServ.getGroupMeta(user.getGroupName(), Constant.GROUP_FEE_PRICE.value());
        int _price = price!=null?Integer.valueOf(price.getValue()):0;
        if(policy!=null)
        {
            int _policy = Integer.valueOf(policy.getValue());
            if(_policy==FeePolicys.PrePay_Month.value())
            {
                ticket.setAcctFee(0);
                ticket.setFeeReceivables(0);
                ticket.setIsDeduct(0);
                acctLog.info(ticket.toTicket());
            }
            else if(_policy==FeePolicys.PrePay_TimeLen.value())
            {
                doPrePayTimeLen(user, ticket,_price);
            }
        }
        
    }

    /**
     * 记账启动或停止请求
     * 
     * @param accType
     * @param client
     * @throws RadiusException
     */
    private void setAcctOnOff(int accType, String client) throws RadiusException {
        List<RadOnline> onlines = statServ.getOnlinesAndClear();
        for (Iterator iterator = onlines.iterator(); iterator.hasNext();) {
            RadOnline online = (RadOnline) iterator.next();
            Ticket ticket = new Ticket();
            ticket.setUserName(online.getUserName());
            ticket.setAcctSessionId(online.getSessionId());
            ticket.setAcctStartTime(online.getAcctStart());
            ticket.setStartSource(online.getStartSource());

            ticket.setNasIpAddress(online.getClientAddress());
            ticket.setFramedIpAddress(online.getIpAddress());

            // 计算SessionTime
            String stopTime = DateTimeUtil.getDateTimeString();

            int sessionTime = DateTimeUtil.compareSecond(stopTime, online.getAcctStart());
            ticket.setAcctSessionTime(sessionTime);

            ticket.setAcctStopTime(stopTime);
            ticket.setStopSource(accType);
            acctLog.info(ticket.toTicket());
        }

        String typ = accType == AccountingRequest.ACCT_STATUS_TYPE_ACCOUNTING_ON ? "on" : "off";
        if (logger.isInfoEnabled())
            logger.info("Accounting " + typ + " request success");

    }
    
    
    /**
     * 预付费计时扣费
     * @param user
     * @param ticket
     * @param price
     */
    private void doPrePayTimeLen(RadUser user,Ticket ticket,int price)
    {
        RadUserMeta userCredit = cacheServ.getUserMeta(user.getUserName(), Constant.USER_CREDIT.value());
        if(userCredit == null)
            return;
        
        int credit = Integer.valueOf(userCredit.getValue());
        try {
            int usedMimiter = (ticket.getAcctSessionTime() - 1) / 60 + 1;
            int usedFee = (int) (usedMimiter / 60 * price);
            int remainder = (int) (usedMimiter % 60);
            if (remainder > 0)
                usedFee = usedFee + remainder * price / 60;
            int balance = credit - usedFee;
            if (balance < 0) {// 余额不足
                userCredit.setValue("0");
            } else {
                userCredit.setValue(String.valueOf(balance));
            }
            userServ.updateUserMeta(userCredit);
            ticket.setAcctFee(usedFee);
            ticket.setFeeReceivables(usedFee);
            ticket.setIsDeduct(1);
            acctLog.info(ticket.toTicket());
            
        } catch (Exception e) {
            logger.error("doPrePayTimeLen error", e);
        }
    }

}
