/*
 *   File : Console.java [Server] 
 * Author : Dov Czitter
 *   Date : 08jan2013
 */
package serverData;

import java.io.*;
import common.*;
import common.ConfigType.ServerType;

public class Console extends Thread
{
	/*
	 * Console():
	 * 		Initiallize the console as a Server.
	 */
	Console ()
	{
		ConfigType.setServerType (ServerType.ServerData);
		start();
	}
	public void run()
	{
		ServerData.logger.logInfo("Console start...");
		try { 
			BufferedReader stdIn = new BufferedReader (new InputStreamReader (System.in));
			String userInput;
			String banner = ConfigType.getBanner();
			System.out.print (banner);
			// Pend for console user input.
			while ((userInput = stdIn.readLine()) != null)
			{ 
				boolean isDown = false;
				switch (ConsoleCmd.getCmd (userInput)) {
					case Help:
						System.out.print (ConsoleCmd.getCmds());
						break;
					case Ticker:
						// Toggle the global 'sendFlag' to alert the WorkerThread
						// to process data.
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
			ServerData.logger.logInfo("Console end...");
			System.exit(0);
		} 
		catch (IOException e) { 
			ServerData.logger.logError("Problem with Communication Server");
			System.exit(1);
		}
	}
}
