// = Server.java =
package serverData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static common.ConfigType.*;
import common.StatusType;

public class ServerData extends Thread
{ 
	protected static boolean serverContinue = true;
	protected static boolean sendFlag = false;
	private   static common.Logger logger =  new common.Logger (ServerData.class.getName());

	public static void main (String[] args) throws IOException 
	{ 
		logger.logInfo("Server start...");
		loadConfigFile("Server.cfg");
		new Console();

		try { 
			switch (getConnectType()) {
				case TCP: processTcpRecv(); break;
				case UDP: processUdpRecv(); break;
				default:
					break;
			}
		} 
		catch (Exception e) { 
			logger.logError ("Accept failed : " +e.getMessage()); 
			System.exit(1); 
		} 
		logger.logInfo("Server end...");
	}
	private static void processTcpRecv ()
	{
		int port = getHostPort();
		ServerSocket serverSocket = null;
		try { 
			serverSocket = new ServerSocket (port);
			serverSocket.setSoTimeout (getTimeout());
			logger.logInfo ("Connection Socket Created");
			try { 
				while (serverContinue)
				{
					StatusType.Waiting.incrementIntValue();
					try {
						Socket socket = serverSocket.accept();
						StatusType.NewCnct.incrementIntValue();
						new WorkerThread (socket);
					}
					catch (SocketTimeoutException ste) {
						StatusType.Timeout.incrementIntValue();
					}
             	}
			} 
			catch (IOException e) { 
				logger.logError ("Accept failed : "+e.getMessage()); 
				System.exit(1); 
			} 
		} 
		catch (IOException e) { 
			logger.logError ("Could not listen on port: "+port); 
			System.exit(1); 
		} 
		finally {
			try {
				logger.logInfo ("Closing Server Connection Socket");
				serverSocket.close(); 
			}
			catch (IOException e) { 
				logger.logError ("Could not close port: "+port); 
				System.exit(1); 
			} 
		}
	}
	private static void processUdpRecv ()
	{
		new WorkerThread ();
	}
} 
