package org.toughradius.common;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Config {

    private static Log log = LogFactory.getLog(Config.class);
    public static final String CONFIG_FILE = "conf/system.xml";
    private XMLConfiguration config = null;

    public Config(){
        try
        {
            config = new XMLConfiguration(CONFIG_FILE);
        }
        catch (ConfigurationException e)
        {
            log.error("Configuring ["+CONFIG_FILE+"] Load Error !",e);
        }
    }
    
    public String getString(String key )
    {
        return config.getString(key);
    }
    
    public String getString(String key,String defaultValue)
    {
        return config.getString(key, defaultValue);
    }
    
    
    public int getInt(String key )
    {
        return config.getInt(key);
    }
    
    public int getInt(String key,int defaultValue)
    {
        return config.getInt(key, defaultValue);
    }



}
