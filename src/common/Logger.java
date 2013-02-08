// = Logger.java =
package common;
 
public class Logger
{
	private org.apache.log4j.Logger logger = null;
	public Logger (String className)
	{
		if (this.logger == null)
			this.logger = org.apache.log4j.Logger.getLogger (className);
	}
	public void logInfo (String message)
	{
		this.logger.log (org.apache.log4j.Level.INFO, message);
	}
	public void logError (String message)
	{
		this.logger.log (org.apache.log4j.Level.ERROR, message);
	}
	public void logTrace (String message)
	{
		this.logger.log (org.apache.log4j.Level.TRACE, message);
	}
	public void logConsole (String message)
	{
		System.out.println (message);
		logInfo (message);
	}
}
