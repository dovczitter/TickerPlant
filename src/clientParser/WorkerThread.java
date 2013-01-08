/*
 *   File : WorkerThread.java  [Client] 
 * Author : Dov Czitter
 *   Date : 08jan2013
 */
package clientParser;

import java.util.concurrent.BlockingQueue;
import common.*;
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
	private SourceType getSourceType() { return this.sourceType; }
	private BlockingQueue<String> getQueue() { return this.queue; }
	
	public void run()
	{
		ClientParser.logger.logInfo("Worker start...");
		StatusType.setStartMS();
		CtsMessage ctsMessage = new CtsMessage();
		CqsMessage cqsMessage = new CqsMessage();
		SourceType sourceType = getSourceType();
		
	    try {
	        while(true) {
	        	// Pend of a queue element.
	        	String msg = (String)getQueue().take();
	        	// Update status info for console display.
	        	switch (sourceType) {
			    	case Cts:
			    		processCtsInfo (ctsMessage, msg);
			    		break;
			    	case Cqs:
			    		processCqsInfo (cqsMessage, msg);
			    		break;
			    	default:
			    		break;
			    }
	        }
	    } catch (InterruptedException ex) {
	    	ClientParser.logger.logError(ex.getMessage());
	    }			
	    ClientParser.logger.logInfo("Worker end...");
	}
	/*
	 * processCtsInfo():
	 * 		This is where the Cts business logic is applied.
	 * 		We update a ststus field with symbol, time, price info for console display.
	 */
	private void processCtsInfo (CtsMessage ctsMessage, String msg)
	{
		final String testSymbol = common.ConfigType.testSymbol.getValue();
		String msgSymbol = ctsMessage.getMsgSymbol (msg);
		if (testSymbol != null && !testSymbol.isEmpty())
		{
			if (msgSymbol.contentEquals(testSymbol)) {
				StatusType.Symbol.setStringValue (String.format("symbol=%s, time=%s, price=%s",
								msgSymbol, ctsMessage.getMsgTime (msg), ctsMessage.getMsgPrice (msg)));
				StatusType.Symbol.incrementIntValue();
			}
		}
	}
	/*
	 * processCqsInfo():
	 * 		This is where the Cqs business logic is applied.
	 * 		We update a ststus field with symbol, time, quote info for console display.
	 */
	private void processCqsInfo (CqsMessage cqsMessage, String msg)
	{
		final String testSymbol = common.ConfigType.testSymbol.getValue();
		String msgSymbol = cqsMessage.getMsgSymbol (msg);
		if (testSymbol != null && !testSymbol.isEmpty())
		{
			if (msgSymbol.contentEquals(testSymbol)) {
				StatusType.Symbol.setStringValue (
						String.format("symbol=%s, time=%s, quote=%s",
								msgSymbol, cqsMessage.getMsgTime (msg), cqsMessage.getMsgQuote (msg)));
				StatusType.Symbol.incrementIntValue();
			}
		}
	}
}
