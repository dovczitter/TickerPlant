/*
 *   File : WorkerThread.java  [Client] 
 * Author : Dov Czitter
 *   Date : 08jan2013
 */
package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.hibernate.Session;
import org.hibernate.Transaction;

import common.*;
import common.ConfigType.*;
 
public class WorkerThread implements Runnable
{
	// Common reader worker queue.
	private final BlockingQueue<String> queue;
	private SourceType sourceType;
	private static List <Data> dataItems = new ArrayList<Data>();
	WorkerThread (BlockingQueue<String> queue)
	{
		this.sourceType = ConfigType.getSourceType();
		this.queue = queue;
	}
	private List<Data> getDataItems()		{ return dataItems; }
	private SourceType getSourceType()		{ return this.sourceType; }
	private BlockingQueue<String> getQueue(){ return this.queue; }
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
		String testSymbol = ConsoleCmd.Symbol.getValue();
		String msgSymbol = ParserUtil.getMsgSymbol (map,msg);

		if (testSymbol != null && !testSymbol.isEmpty())
		{
			if (msgSymbol.contentEquals(testSymbol))
			{
				String msgTime   = ParserUtil.getMsgTime   (map,msg);
				String msgPrice  = ParserUtil.getMsgPrice  (map,msg);
				String msgQuote  = ParserUtil.getMsgQuote  (map,msg);
				long   msgSeqnum = ParserUtil.getMsgSeqnum (map,msg);
				String msgBid    = ParserUtil.getMsgBid    (map,msg);
				String msgOffer  = ParserUtil.getMsgOffer  (map,msg);
				addDataItem (getDataItems(), msgSeqnum, msgSymbol, msgTime, msgPrice, msgBid, msgOffer);

				StatusType.Symbol.setStringValue (String.format ("symbol=%s, time=%s, price=%s, %s",
								                  msgSymbol, msgTime, msgPrice, msgQuote));
				StatusType.Symbol.incrementIntValue();
			}
		}
	}
	
	private void addDataItem (List<Data> items, long msgSeqnum, String msgSymbol, String msgTime, String msgPrice, String msgBidPrice, String msgOfferPrice)
	{
		try {
			Data data = new Data();
			data.setTime_stamp (msgTime);
			data.setSeqnum (msgSeqnum);
			data.setSymbol (msgSymbol);
			data.setPrice (msgPrice);
			data.setBid_price (msgBidPrice);
			data.setOffer_price (msgOfferPrice);
			items.add(data);
			if (items.size() == HibernateUtil.getHibernateBatchSize()) {
				setDataItemsDB (items);
				items.clear();
			}
		}
		catch (Exception ex) {
	    	Client.logger.logError(ex.getMessage());
		}
	}
	
	private void setDataItemsDB (List<Data> items)
	{
		try {
			Session session = HibernateUtil.getCurrentSession();
			Transaction transaction = session.beginTransaction();
			for (Data d : items) {
				session.save(d);
			}
			session.flush();
			transaction.commit();
		}
		catch (Exception ex) {
	    	Client.logger.logError(ex.getMessage());
		}
	}
}
