/*
 *   File : WorkerThread.java  [Client] 
 * Author : Dov Czitter
 *   Date : 08jan2013
 */
package client;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import common.SequenceNumber;
import common.StatusType;
import common.ConfigType;
import common.ConfigType.SourceType;
 
public class WorkerThread implements Runnable
{
	// Common reader worker queue.
	private final BlockingQueue<String> queue;
	private SourceType sourceType;

	WorkerThread (BlockingQueue<String> queue)
	{
		this.sourceType = ConfigType.getSourceType();
		this.queue = queue;
	}
	private SourceType getSourceType()       { return this.sourceType; }
	private BlockingQueue<String> getQueue() { return this.queue; }
	
	public void run()
	{
		Client.logger.logInfo("Worker start...");
		StatusType.setStartMS();
		SourceType sourceType = getSourceType();
		String msg = "";
		HashMap<String,MsgIndex> map = null;
	    try {
	        while(true) {
	        	// Pend of a queue element.
	        	msg = (String)getQueue().take();
				SequenceNumber.setStats (ParserUtil.getMsgSeqnum (sourceType, msg));
	        	// Retrieve the appros map.
	        	map = ParserUtil.getMap (sourceType, msg);
				if (map != null) {
					// 'null' map means an unprocessed msg, ie Control.
					processMsg (map, msg);
				}
	        }
	    } catch (InterruptedException ex) {
	    	Client.logger.logError(ex.getMessage());
	    }			
	    Client.logger.logInfo("Worker end...");
	}
	/*
	 * processMsg():
	 * 		This is where the Cts business logic is applied.
	 * 		We update a ststus field with symbol, time, price info for console display.
	 */
	private void processMsg (HashMap<String,MsgIndex> map, String msg)
	{
		final String testSymbol = common.ConfigType.testSymbol.getValue();
		String msgSymbol = ParserUtil.getMsgSymbol (map,msg);
		String msgTime   = ParserUtil.getMsgTime(map, msg);
		String msgPrice  = ParserUtil.getMsgPrice(map, msg);
		String msgQuote  = ParserUtil.getMsgQuote(map, msg);
		if (testSymbol != null && !testSymbol.isEmpty())
		{
			if (msgSymbol.contentEquals(testSymbol))
			{
				StatusType.Symbol.setStringValue 
				(String.format("symbol=%s, time=%s, price=%s, %s",
								msgSymbol, msgTime, msgPrice, msgQuote));
				StatusType.Symbol.incrementIntValue();
			}
		}
	}
}