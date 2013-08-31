package org.toughradius.components;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.toughradius.annotation.Inject;
import org.toughradius.data.RadBlacklistMapper;
import org.toughradius.data.RadClientMapper;
import org.toughradius.model.RadBlacklist;
import org.toughradius.model.RadClient;
@Inject
public class BaseService
{
    private static Log logger = LogFactory.getLog(BaseService.class);
    private DBService dbservice;
    
    public void setDbservice(DBService dbservice)
    {
        this.dbservice = dbservice;
    }
    /**
     * 查询单个客户端
     * @param address
     * @return
     */
    public RadClient getClient(String address)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadClientMapper mapper = session.getMapper(RadClientMapper.class);
            RadClient rc = mapper.selectByPrimaryKey(address);
            return rc;
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 查询客户端集合
     * @return
     */
    public List<RadClient> getClients()
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadClientMapper mapper = session.getMapper(RadClientMapper.class);
            List<RadClient> cs= mapper.selectByExample(null);
            return cs;
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 查询单个黑名单
     * @param macaddr
     * @return
     */
    public RadBlacklist getBlacklist(String macaddr)
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadBlacklistMapper mapper = session.getMapper(RadBlacklistMapper.class);
            RadBlacklist rbl = mapper.selectByPrimaryKey(macaddr);
            return rbl;
        }
        finally
        {
            session.close();
        }
    }
    
    /**
     * 查询黑名单集合
     * @return
     */
    public List<RadBlacklist> getBlacklists()
    {
        SqlSession session = dbservice.openSession();
        try
        {
            RadBlacklistMapper mapper = session.getMapper(RadBlacklistMapper.class);
            List<RadBlacklist> bs = mapper.selectByExample(null);
            return bs;
        }
        finally
        {
            session.close();
        }
    }
    
}
