// = [client] Console.java =
package clientParser;

import java.io.*;
import java.util.concurrent.BlockingQueue;

import common.*;
import common.ConfigType.ServerType;

public class Console extends Thread
{
	private static BlockingQueue<String> sharedQueue = null;
	private common.Logger logger = new common.Logger (ClientParser.class.getName());
	
	Console ()
	{
		startConsole ();
	}
	Console (BlockingQueue<String> sharedQ)
	{
		sharedQueue = sharedQ;
		startConsole ();
	}
	private void startConsole ()
	{
		ConfigType.setServerType (ServerType.ClientParser);
		start();
	}
	public static int getSharedQueueSize()
	{
		if (sharedQueue != null)
			return sharedQueue.size();
		return 0;
	}
	public void run()
	{
		logger.logInfo("Console start...");
		String banner = ConfigType.getBanner();
		try {
			BufferedReader stdIn = new BufferedReader (new InputStreamReader(System.in));
			String userInput;
			System.out.print (banner);
			while ((userInput = stdIn.readLine()) != null)
			{ 
				boolean isDown = false;
				switch (ConsoleCmd.getCmd (userInput)) {
					case Help:
						System.out.print (ConsoleCmd.getCmds());
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
		}
		catch (IOException e) {
			System.out.println (banner+" : ERROR - "+e.getMessage()); 
			e.printStackTrace();
		}
		logger.logInfo("Console end...");
		System.exit(0);
	}
}
