// = Client.java =
package clientParser;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import static common.ConfigType.*;

public class ClientParser implements Runnable
{
	private static common.Logger logger = new common.Logger (ClientParser.class.getName());
	private final static int MaxQueueSize = 100000;
		
	public static void main (String[] args) throws Exception
	{
		logger.logInfo("Client start...");
		loadConfigFile("Client.cfg");
		
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(MaxQueueSize);
		new Console (queue);
		
		ReaderThread  readerThread = new ReaderThread (queue);
		WorkerThread  workerThread = new WorkerThread (queue);
		new Thread(workerThread).start();
		new Thread(readerThread).start();
	}
	public void run() { }
}
