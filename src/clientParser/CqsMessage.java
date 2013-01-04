package clientParser;

public class CqsMessage
{
	public CqsMessage ()
	{
		totalHeaderSize = 0;
		for (Header h: Header.values()) {
			h.startIndex = totalHeaderSize;
			h.endIndex = h.startIndex + h.size;
			totalHeaderSize = h.endIndex;
		}
		int totalSize = 0;
		for (ShortQuote q: ShortQuote.values()) {
			q.startIndex = totalHeaderSize + totalSize;
			q.endIndex = q.startIndex + q.size;
			totalSize += q.size;
		}
		totalSize = 0;
		for (LongQuote q: LongQuote.values()) {
			q.startIndex = totalHeaderSize + totalSize;
			q.endIndex = q.startIndex + q.size;
			totalSize += q.size;
		}
	}
	private static int totalHeaderSize = 0;

	public enum Category {
		NONE			(' '),
		LISTED_EQUITY	('E'),
		LOCAL_ISSUE		('L'),
		CONTROL			('C');

		private char value = ' ';
		Category (char value) { this.value = value; }
		public char getValue(){ return this.value; }
	}
	public enum MessageType {
		NONE				(' '),
		SHORT_QUOTE			('D'),
		LONG_QUOTE			('B');

		private char value = ' ';
		MessageType (char value) { this.value = value; }
		public char getValue(){ return this.value; }
	}
	public enum Header {
		category		(0,0,1),
		type			(0,0,1),
		network			(0,0,1),
		retransReq		(0,0,2),
		identifier		(0,0,1),
		reserved		(0,0,2),
		seqnum			(0,0,9),
		participant		(0,0,1),
		timestamp		(0,0,6);
		private int startIndex;
		private int endIndex;
		private int size;
		Header (int startIndex, int endIndex, int size) {
			this.startIndex=startIndex; this.endIndex=endIndex; this.size=size; }
		private int getStartIndex() { return this.startIndex; }
		private int getEndIndex()   { return this.endIndex; }
	}
	public enum ShortQuote {
		symbol					(0,0,3),
		quoteCondition			(0,0,1),
		reserved1				(0,0,2),
		bidDenominator			(0,0,1),
		bidPrice				(0,0,8),
		bidSize					(0,0,3),
		reserved2				(0,0,1),
		offerDenominator		(0,0,1),
		offerPrice				(0,0,8),
		offerSize				(0,0,3),
		reserved3				(0,0,1),
		nationalBboIndicator	(0,0,1),
		nasdaqBboIndicator		(0,0,1);
		
		private int startIndex;
		private int endIndex;
		private int size;
		ShortQuote (int startIndex, int endIndex, int size) {
			this.startIndex=startIndex; this.endIndex=endIndex; this.size=size; }
		private int getStartIndex() { return this.startIndex; }
		private int getEndIndex()   { return this.endIndex; }
	}
	public enum LongQuote {
		symbol						(0,0,11),
		suffix						(0,0, 1),
		testIndicator				(0,0, 1),
		reserved1					(0,0, 3),
		financialStatus				(0,0, 1),
		currency					(0,0, 3),
		instrumentType				(0,0, 1),
		cancelCorrectionIndicator	(0,0, 1),
		settlementCondition			(0,0, 1),
		marketCondition				(0,0, 1),
		quoteCondition				(0,0, 1),
		reserved2					(0,0, 2),
		bidDenominator				(0,0, 1),
		bidPrice					(0,0,12),
		bidSize						(0,0, 7),
		offerDenominator			(0,0, 1),
		offerPrice					(0,0,12),
		offerSize					(0,0, 7),
		nasdaqMmid					(0,0, 4),
		nasdaqLocation				(0,0, 2),
		nasdaqDesk					(0,0, 1),
		reserved3					(0,0, 2),
		nationalBboIndicator		(0,0, 1),
		nasdaqBboIndicator			(0,0, 1);
		private int startIndex;
		private int endIndex;
		private int size;
		LongQuote (int startIndex, int endIndex, int size) {
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
	public String getMsgSymbol (String msg)
	{
		String symbol = "";

		switch (getType(msg)) {
			case SHORT_QUOTE:
				symbol = msg.substring (ShortQuote.symbol.getStartIndex(), ShortQuote.symbol.getEndIndex());
				break;
			case LONG_QUOTE:
				symbol = msg.substring (LongQuote.symbol.getStartIndex(), LongQuote.symbol.getEndIndex());
				break;
			default:
				break;
		}
		return symbol;
	}
	public String getMsgQuote (String msg)
	{
		String bid = "";
		String offer = "";

		switch (getType(msg)) {
			case SHORT_QUOTE:
				bid   = msg.substring (ShortQuote.bidPrice.getStartIndex(),  ShortQuote.bidPrice.getEndIndex());
				offer = msg.substring (ShortQuote.offerPrice.getStartIndex(),ShortQuote.offerPrice.getEndIndex());
				break;
			case LONG_QUOTE:
				bid   = msg.substring (LongQuote.bidPrice.getStartIndex(),  LongQuote.bidPrice.getEndIndex());
				offer = msg.substring (LongQuote.offerPrice.getStartIndex(),LongQuote.offerPrice.getEndIndex());
				break;
			default:
				break;
		}
		return (bid+":"+offer);
	}

}
