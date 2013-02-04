/*
 *   File : Client.java 
 * Author : Dov Czitter
 *   Date : 04feb2013
 */
package client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static common.ConfigType.*;

public class Client implements Runnable
{
	public static common.Logger logger = new common.Logger (Client.class.getName());
	private final static int MaxQueueSize = 100000;
	/*
	 * main():
	 * 		Client mainline for data processing based on configuration file parameters.
	 */	
	public static void main (String[] args) throws Exception
	{
		logger.logInfo("Client start...");
		loadConfigFile("Client.cfg");
		
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(MaxQueueSize);
		new Console (queue);
		
		// ReaderThread reads tcp or udp data as per configuration, 
		// then places the single message on the worker queue.
		ReaderThread  readerThread = new ReaderThread (queue);
		// WorkerThread does the data extraction and business logic.
		WorkerThread  workerThread = new WorkerThread (queue);
		
		new Thread(workerThread).start();
		new Thread(readerThread).start();
	}
	public void run() { }
}
	