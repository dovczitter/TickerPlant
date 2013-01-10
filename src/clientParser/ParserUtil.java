/*
 *   File : ParserUtil.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

import java.util.HashMap;

import common.ConfigType.SourceType;

public class ParserUtil
{
	private static int ctsHeaderSize = 0;
	private static int cqsHeaderSize = 0;
	private static final HashMap<String,Element> ctsShortSale  = ParserUtil.getCtsShortSale();
	private static final HashMap<String,Element> ctsLongSale   = ParserUtil.getCtsLongSale();
	private static final HashMap<String,Element> cqsShortQuote = ParserUtil.getCqsShortQuote();
	private static final HashMap<String,Element> cqsLongQuote  = ParserUtil.getCqsLongQuote();

	/*********************************************/
	/***                CTS                    ***/
	/*********************************************/
	private static HashMap<String,Element> getCtsHeaderMap ()
	{
		HashMap<String,Element> map = new HashMap<String,Element>();
		int startIndex,endIndex;
		ctsHeaderSize = 0;
		for (CtsHeader h : CtsHeader.values())
		{
			startIndex = ctsHeaderSize;
			endIndex   = ctsHeaderSize + h.getSize();
			ctsHeaderSize  = endIndex;
			Element e  = new Element (startIndex, endIndex);
			map.put(h.name(), e);
		}
		return map;
	}
	public static HashMap<String,Element> getCtsShortSale ()
	{
		HashMap<String,Element> map = getCtsHeaderMap ();
		int startIndex,endIndex;
		int totalSize = ctsHeaderSize;
		for (CtsShortSale t : CtsShortSale.values())
		{
			startIndex = totalSize;
			endIndex   = totalSize + t.getSize();
			totalSize  = endIndex;
			Element e  = new Element (startIndex, endIndex);
			map.put(t.name(), e);
		}
		return map;
	}
	public static HashMap<String,Element> getCtsLongSale ()
	{
		HashMap<String,Element> map = getCtsHeaderMap ();
		int startIndex,endIndex;
		int totalSize = ctsHeaderSize;
		for (CtsLongSale t : CtsLongSale.values())
		{
			startIndex = totalSize;
			endIndex   = totalSize + t.getSize();
			totalSize  = endIndex;
			Element e  = new Element (startIndex, endIndex);
			map.put(t.name(), e);
		}
		return map;
	}
	private static CtsMsgtype getCtsType (String msg)
	{
		char msgtype = CtsHeader.getMsgtypeValue(msg);
		for (CtsMsgtype t : CtsMsgtype.values()) {
			if (t.getValue() == msgtype)
				return t;
		}
		return CtsMsgtype.NONE;
	}
	private static HashMap<String,Element> getCtsMap (CtsMsgtype type)
	{
		HashMap<String,Element> map = null;
		switch (type)
		{
			case SHORT_TRADE:
			case ENCH_SHORT_TRADE:
				map = ctsShortSale;
				break;
			case LONG_TRADE:
				map = ctsLongSale;
				break;
			default:
				break;
		}
		return map;
	}
	/*********************************************/
	/***                CQS                    ***/
	/*********************************************/
	private static HashMap<String,Element> getCqsHeaderMap ()
	{
		HashMap<String,Element> map = new HashMap<String,Element>();
		int startIndex,endIndex;
		cqsHeaderSize = 0;
		for (CqsHeader h : CqsHeader.values())
		{
			startIndex = cqsHeaderSize;
			endIndex   = cqsHeaderSize + h.getSize();
			cqsHeaderSize  = endIndex;
			Element e  = new Element (startIndex, endIndex);
			map.put(h.name(), e);
		}
		return map;
	}
	public static HashMap<String,Element> getCqsShortQuote ()
	{
		HashMap<String,Element> map = getCqsHeaderMap ();
		int startIndex,endIndex;
		int totalSize = cqsHeaderSize;
		for (CqsShortQuote q : CqsShortQuote.values())
		{
			startIndex = totalSize;
			endIndex   = totalSize + q.getSize();
			totalSize  = endIndex;
			Element e  = new Element (startIndex, endIndex);
			map.put (q.name(),e);
		}
		return map;
	}
	public static HashMap<String,Element> getCqsLongQuote ()
	{
		HashMap<String,Element> map = getCqsHeaderMap ();
		int startIndex,endIndex;
		int totalSize = cqsHeaderSize;
		for (CqsLongQuote q : CqsLongQuote.values())
		{
			startIndex = totalSize;
			endIndex   = totalSize + q.getSize();
			totalSize  = endIndex;
			Element e  = new Element (startIndex, endIndex);
			map.put (q.name(),e);
		}
		return map;
	}
	private static CqsMsgtype getCqsType (String msg)
	{
		char msgtype = CqsHeader.getMsgtypeValue(msg);
		for (CqsMsgtype t : CqsMsgtype.values()) {
			if (t.getValue() == msgtype)
				return t;
		}
		return CqsMsgtype.NONE;
	}
	private static HashMap<String,Element> getCqsMap (CqsMsgtype type)
	{
		HashMap<String,Element> map = null;
		switch (type)
		{
			case SHORT_QUOTE:
				map = cqsShortQuote;
				break;
			case LONG_QUOTE:
				map = cqsLongQuote;
				break;
			default:
				break;
		}
		return map;
	}
	/*********************************************/
	/***               Common                  ***/
	/*********************************************/
	public static HashMap<String,Element> getMap(SourceType sourceType, String msg)
	{
		HashMap<String,Element> map = null;
		switch (sourceType)
		{
			case Cts:
				map = getCtsMap (getCtsType(msg));
				break;
			case Cqs:
				map = getCqsMap (getCqsType(msg));
				break;
			default:
				break;
		}
		return map;
	}
	private static String getMsgValue (Fields field, HashMap<String,Element> map, String msg)
	{
		String value = "";
		Element e = map.get(field.name());
		if (e != null)
			value = msg.substring (e.getStartIndex(), e.getEndIndex());
		return value;
	}
	public static String getMsgSymbol (HashMap<String,Element> map, String msg)
	{
		return getMsgValue (Fields.symbol, map, msg);
	}
	public static String getMsgTime (HashMap<String,Element> map, String msg)
	{
		String ts = getMsgValue (Fields.timestamp, map, msg);
		if (ts==null || ts.isEmpty())
			return "n/a";
		int h = (int)(ts.charAt(0)-'0');
		int m = (int)(ts.charAt(1)-'0');
		int s = (int)(ts.charAt(2)-'0');
		return String.format("%02d:%02d:%02d",h,m,s);
	}
	public static String getMsgPrice (HashMap<String,Element> map, String msg)
	{
		return getMsgValue (Fields.price, map, msg);
	}
	public static String getMsgQuote (HashMap<String,Element> map, String msg)
	{
		String bid   = getMsgValue (Fields.bidPrice, map, msg);
		String offer = getMsgValue (Fields.offerPrice, map, msg);
		return ("bid="+bid+":offer="+offer);
	}
	public static long getMsgSeqnum (HashMap<String,Element> map, String msg)
	{
		long seqnum = 0;
		String s = getMsgValue (Fields.seqnum, map, msg);
		if (s!=null && !s.isEmpty()) {
			try{
			seqnum = Integer.parseInt (s);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return seqnum;
	}
}
