// = [client] Worker.java =
package clientParser;

import java.util.concurrent.BlockingQueue;
import common.*;
import common.ConfigType.SourceType;
 
public class WorkerThread implements Runnable
{
	private final BlockingQueue<String> sharedQueue;
	private SourceType sourceType;
	private static common.Logger logger = new common.Logger (ClientParser.class.getName());

	WorkerThread (BlockingQueue<String> sharedQueue)
	{
		this.sourceType = ConfigType.getSourceType();
		this.sharedQueue = sharedQueue;
	}
	public SourceType getSourceType() { return this.sourceType; }
	
	public void run()
	{
		logger.logInfo("Worker start...");
		StatusType.setStartMS();
		CtsMessage ctsMessage = new CtsMessage();
		CqsMessage cqsMessage = new CqsMessage();
		SourceType sourceType = getSourceType();
		
	    try {
	        while(true) {
	        	String msg = (String)this.sharedQueue.take();
	        	switch (sourceType) {
			    	case Cts:
			    		processCtsTestSymbol (ctsMessage, msg);
			    		break;
			    	case Cqs:
			    		processCqsTestSymbol (cqsMessage, msg);
			    		break;
			    	default:
			    		break;
			    }
	        }
	    } catch (InterruptedException ex) {
	    	logger.logError(ex.getMessage());
	    }			
		logger.logInfo("Worker end...");
	}
	private void processCtsTestSymbol (CtsMessage ctsMessage, String msg)
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
	private void processCqsTestSymbol (CqsMessage cqsMessage, String msg)
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
