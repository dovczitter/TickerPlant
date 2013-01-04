package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public enum ConfigType
{
	none     	(""),
	serverType	(""),
	sourceType	(""),
	connectType	(""),
	hostName	(""),
	hostPort	(""),
	soTimeout   (""),
	dataFilename(""),
	testSymbol  (""),
	debugFlag1  (""),
	debugFlag2  (""),
	debugFlag3  ("");
	private String value = "";
	private static int hostport = 0;
	private static int timeout = 0;
	
	ConfigType (String value) {
		this.value = value;
	}
	public enum SourceType {
		None,
		Cts,
		Cqs;
	}
	public enum ServerType {
		None,
		ClientParser,
		ServerData;
	}
	public enum ConnectType {
		None,
		TCP,
		UDP;
	}
	public void setValue (String value)
	{
		if (this != serverType)
			this.value = value;
	}
	public String getValue ()
	{
		return this.value;
	}
	public static void setServerType (ServerType type)
	{
		serverType.value = type.name();
	}
	public static ServerType getServerType ()
	{
		for (ServerType type : ServerType.values())
		{
			if (type.name().equals(serverType.getValue()))
				return type;
		}
		return ServerType.None;
	}
	public static String getBanner ()
	{
		return (ConfigType.serverType.getValue()+" =>");
	}
	public static SourceType getSourceType ()
	{
		if (sourceType.getValue().equalsIgnoreCase(SourceType.Cts.name()))
			return SourceType.Cts; 
		else if (sourceType.getValue().equalsIgnoreCase(SourceType.Cqs.name()))
			return SourceType.Cqs; 
		return SourceType.None; 
	}
	public static ConnectType getConnectType ()
	{
		if (connectType.getValue().equalsIgnoreCase(ConnectType.TCP.name()))
			return ConnectType.TCP; 
		else if (connectType.getValue().equalsIgnoreCase(ConnectType.UDP.name()))
			return ConnectType.UDP; 
		return ConnectType.None; 
	}
	public static int getHostPort()
	{
		if (hostport == 0) {
			try {
				hostport = Integer.parseInt(ConfigType.hostPort.getValue());
			} catch (Exception e) { hostport = 0; }
		}
		return hostport;
	}
	public static int getTimeout()
	{
		if (timeout == 0) {
			try {
				timeout = Integer.parseInt(ConfigType.soTimeout.getValue());
			} catch (Exception e) { timeout = 0; }
		}
		return timeout;
	}
	public static void loadConfigFile (String filename) throws IOException
	{
		Properties config = new Properties();
  	  	String workingDir = System.getProperty("user.dir");
  	  	String finalfile  = workingDir + File.separator + filename;
        config.load (new FileInputStream(finalfile));
        Enumeration<Object> en = config.keys();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            for (ConfigType type : ConfigType.values())
            {
            	if (key.equalsIgnoreCase(type.name())) {
            		type.setValue((String)config.get(key));
            		break;
            	}
            }
        }
    }
}
