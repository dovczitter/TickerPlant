/*
 *   File : CtsMessage.java 
 * Author : Dov Czitter
 *   Date : 08jan2013
 */
package clientParser;

public class CtsMessage
{
	/*
	 * The constructor will initialize the component field
	 * start and end positions of the raw data message.
	 * This enables efficient field extraction. 
	 */
	public CtsMessage ()
	{
		totalHeaderSize = 0;
		for (Header h: Header.values()) {
			h.startIndex = totalHeaderSize;
			h.endIndex = h.startIndex + h.size;
			totalHeaderSize = h.endIndex;
		}
		int totalSize = 0;
		for (ShortTrade t: ShortTrade.values()) {
			t.startIndex = totalHeaderSize + totalSize;
			t.endIndex = t.startIndex + t.size;
			totalSize += t.size;
		}
		totalSize = 0;
		for (LongTrade t: LongTrade.values()) {
			t.startIndex = totalHeaderSize + totalSize;
			t.endIndex = t.startIndex + t.size;
			totalSize += t.size;
		}
	}
	private static int totalHeaderSize = 0;

	public enum Category {
		NONE			(' '),
		LISTED_EQUITY	('E'),
		CONTROL			('C');

		private char value = ' ';
		Category (char value) { this.value = value; }
		public char getValue(){ return this.value; }
	}
	public enum MessageType {
		NONE				(' '),
		SHORT_TRADE			('A'),
		LONG_TRADE			('B'),
		ENCH_SHORT_TRADE	('I'),
		LINE_INTEGRITY		('T');

		private char value = ' ';
		MessageType (char value) { this.value = value; }
		public char getValue(){ return this.value; }
	}
	public enum Header {
		category		(0,0,1),
		type			(0,0,1),
		network			(0,0,1),
		retransReq		(0,0,2),
		seqnum			(0,0,7),
		participant		(0,0,1),
		timestamp		(0,0,3);
		private int startIndex;
		private int endIndex;
		private int size;
		Header (int startIndex, int endIndex, int size) {
			this.startIndex=startIndex; this.endIndex=endIndex; this.size=size; }
		private int getStartIndex() { return this.startIndex; }
		private int getEndIndex()   { return this.endIndex; }
	}
	public enum ShortTrade {
		symbol						(0,0,3),
		saleCondition				(0,0,1),
		volume						(0,0,4),
		denominator					(0,0,1),
		price						(0,0,8),
		consolidatedPriceIndicator	(0,0,1),
		participantPriceIndicator	(0,0,1),
		reserved					(0,0,1);
		private int startIndex;
		private int endIndex;
		private int size;
		ShortTrade (int startIndex, int endIndex, int size) {
			this.startIndex=startIndex; this.endIndex=endIndex; this.size=size; }
		private int getStartIndex() { return this.startIndex; }
		private int getEndIndex()   { return this.endIndex; }
	}
	public enum LongTrade {
		symbol						(0,0,11),
		suffix						(0,0, 1),
		testIndicator				(0,0, 1),
		reserved1					(0,0, 3),
		financialStatus				(0,0, 1),
		currency					(0,0, 3),
		heldIndicator				(0,0, 1),
		instrumentType				(0,0, 1),
		saleDays					(0,0, 3),
		saleCondition				(0,0, 4),
		reserved2					(0,0, 3),
		denominator					(0,0, 1),
		price						(0,0,12),
		volume						(0,0, 9),
		consolidatedPriceIndicator	(0,0, 1),
		participantPriceIndicator	(0,0, 1),
		reserved3					(0,0, 1),
		stopStockIndicator			(0,0, 1);
		private int startIndex;
		private int endIndex;
		private int size;
		LongTrade (int startIndex, int endIndex, int size) {
			this.startIndex = startIndex; this.endIndex = endIndex; this.size = size; }
		private int getStartIndex() { return this.startIndex; }
		private int getEndIndex()   { return this.endIndex; }
	}
	public static MessageType getType (String msg)
	{
		String st = msg.substring (Header.type.getStartIndex(), Header.type.getEndIndex());
		char ct = ((st==null || st.isEmpty()) ? ' ' : st.charAt(0));
		for (MessageType mt : MessageType.values()) {
			if (mt.getValue() == ct)
				return mt;
		}
		return MessageType.NONE;
	}
	public String getMsgTime (String msg)
	{
		String ts = msg.substring (Header.timestamp.getStartIndex(), Header.timestamp.getEndIndex());
		if (ts==null || ts.isEmpty())
			return "n/a";
		int h=(int)(ts.charAt(0) - '0');
		int m=(int)(ts.charAt(1) - '0');
		int s=(int)(ts.charAt(2) - '0');

		return String.format("%02d:%02d:%02d",h,m,s);
	}
	public String getMsgPrice (String msg)
	{
		String price = "";

		switch (getType(msg)) {
			case SHORT_TRADE:
			case ENCH_SHORT_TRADE:
				price = msg.substring (ShortTrade.price.getStartIndex(), ShortTrade.price.getEndIndex());
				break;
			case LONG_TRADE:
				price = msg.substring (LongTrade.price.getStartIndex(), LongTrade.price.getEndIndex());
				break;
			default:
				break;
		}
		return price;
	}
	public String getMsgSymbol (String msg)
	{
		String symbol = "";

		switch (getType(msg)) {
			case SHORT_TRADE:
			case ENCH_SHORT_TRADE:
				symbol = msg.substring (ShortTrade.symbol.getStartIndex(), ShortTrade.symbol.getEndIndex());
				break;
			case LONG_TRADE:
				symbol = msg.substring (LongTrade.symbol.getStartIndex(), LongTrade.symbol.getEndIndex());
				break;
			default:
				break;
		}
		return symbol;
	}
}
