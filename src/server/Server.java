/*
 *   File : Server.java 
 * Author : Dov Czitter
 *   Date : 04feb2013
 *   
 *   This Server will provide market data to a tcp or udp configured connection.
 *   The data is read from file and can easily be modified to read from a live
 *   market data connection.
 *   Tcp is sent as a standard streaming ASCII data packet with a 4 byte length header,
 *   Udp is broadcast as a per message Datagram.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static common.ConfigType.*;
import common.StatusType;

public class Server extends Thread
{ 
	protected static boolean serverContinue;
	protected static boolean sendFlag;
	public static common.Logger logger;
	/*
	 * main():
	 * 		Server mainline for data processing based on configuration file parameters.
	 */
	public static void main (String[] args) throws IOException 
	{ 
		// logger.
		logger =  new common.Logger (Server.class.getName());
		// internal flags.
		serverContinue = true;
		sendFlag = false;
		// internal configuration.
		loadConfigFile("Server.cfg");
		// Start up the console.
		new Console();
		logger.logInfo("Server start...");
		try {
			switch (getConnectType()) {
				case TCP: processTcp(); break;
				case UDP: processUdp(); break;
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
	/*
	 * processTcp ():
	 * 		- Post an accept().
	 *      - Wait for a new connection.
	 *      - Start a WorkerThread to send tcp data on that socket.
	 */
	private static void processTcp ()
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
	/*
	 * processUdp ():
	 *      Start a WorkerThread to broadcast udp data on a well known port.
	 */
	private static void processUdp ()
	{
		new WorkerThread ();
	}
} 
