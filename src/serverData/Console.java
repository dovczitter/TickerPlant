// = [server] Console.java =
package serverData;

import java.io.*;
import common.*;
import common.ConfigType.ServerType;

public class Console extends Thread
{
	private static common.Logger logger = new common.Logger (ServerData.class.getName());

	Console ()
	{
		ConfigType.setServerType (ServerType.ServerData);
		start();
	}
	public void run()
	{
		logger.logInfo("Console start...");
		try { 
			BufferedReader stdIn = new BufferedReader (new InputStreamReader (System.in));
			String userInput;
			String banner = ConfigType.getBanner();
			System.out.print (banner);
			while ((userInput = stdIn.readLine()) != null)
			{ 
				boolean isDown = false;
				switch (ConsoleCmd.getCmd (userInput)) {
					case Help:
						System.out.print (ConsoleCmd.getCmds());
						break;
					case Ticker:
						ServerData.sendFlag = ServerData.sendFlag ? false:true;
						if (ServerData.sendFlag)
							StatusType.resetAll();
						break;
					case Down:
						isDown = true;
						break;
					case Stats:
						System.out.print (ConsoleCmd.getStats());
						break;
					case ResetStats:
						ConsoleCmd.resetStats();
						System.out.print (ConsoleCmd.getStats());
						break;
					default:
						break;
				}
				System.out.print (banner);
				if (isDown)
					break;
	        } 
			stdIn.close();
			logger.logInfo("Console end...");
			System.exit(0);
		} 
		catch (IOException e) { 
			logger.logError("Problem with Communication Server");
			System.exit(1);
		}
	}
}
