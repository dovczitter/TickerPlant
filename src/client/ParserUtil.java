/*
 *   File : ParserUtil.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package client;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.ConfigType.SourceType;

public class ParserUtil
{
	private static HashMap <String, HashMap<String,MsgIndex>> parserMaps = new HashMap<String, HashMap<String,MsgIndex>>();
	
	private final static int MsgOffset_type = 1;
	private final static int CtsSeqnum_startIndex = 5;
	private final static int CtsSeqnum_endIndex = CtsSeqnum_startIndex + 7;
	private final static int CqsSeqnum_startIndex = 8;
	private final static int CqsSeqnum_endIndex = CqsSeqnum_startIndex + 9;
	private final static String xmlRoot = System.getProperty("user.dir") +File.separator+ "xml" +File.separator;

	/*********************************************************/
	/********      Interal Hashmap Initiallization      ******/
	/*********************************************************/

	public static HashMap<String,MsgIndex> getParserMap (MsgType msgType)
	{
		return parserMaps.get (msgType.name());
	}
	public static void setParserMap (MsgType msgType, HashMap<String,MsgIndex>  map)
	{
		if (getParserMap (msgType) == null)
			parserMaps.put (msgType.name(), map);
	}
	public static HashMap<String,MsgIndex> getMap (SourceType sourceType, String msg)
	{
		HashMap<String,MsgIndex> map = null;
		MsgType msgType = getMsgType (sourceType, msg);
		map = getParserMap (msgType);
		if (map == null && msgType != MsgType.None)
		{
			map = new HashMap<String,MsgIndex>();
			NodeList nodeList = getNodeList (getXmlFilename (sourceType));
			int startIndex,endIndex;
			int totalSize = 0;
			for (int i=0; i < nodeList.getLength(); i++)
			{
				Node node = (Node) nodeList.item(i);
				String parentName = node.getParentNode().getNodeName();
				if (parentName.toLowerCase().contains("header") || parentName.compareToIgnoreCase(msgType.name())==0)
				{
					if (nodeList.item(i).hasAttributes())
					{
						NamedNodeMap attrs = node.getAttributes();
				        //System.out.print(node.getNodeName());
				        for (int j = 0; j < attrs.getLength(); j++)
			        	{
				        	Attr attribute = (Attr) attrs.item(j);
				        	int size  = Integer.parseInt(attribute.getValue());
				        	startIndex = totalSize;
				        	endIndex   = totalSize + size;
				        	totalSize  = endIndex;
				        	map.put (attribute.getName(), new MsgIndex (startIndex, endIndex));
			        	}
					}
				}
			}
			setParserMap (msgType, map);
		}
		return map;
	}
	private static String getXmlFilename (SourceType sourceType)
	{
		return (xmlRoot +sourceType.name()+ ".xml");
	}
	private static NodeList getNodeList (String xmlName)
	{
		NodeList nodeList = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse (new File(xmlName));
			doc.getDocumentElement().normalize();
			nodeList = doc.getElementsByTagName("field");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return nodeList;
	}
	/*********************************************************/
	/********         Parsing of incoming message       ******/
	/*********************************************************/
	
	private static String getMsgValue (Fields field, HashMap<String,MsgIndex> map, String msg)
	{
		String value = "";
		MsgIndex e = map.get(field.name());
		if (e != null)
			value = msg.substring (e.getStartIndex(), e.getEndIndex());
		return value;
	}
	public static String getMsgSymbol (HashMap<String,MsgIndex> map, String msg)
	{
		return getMsgValue (Fields.symbol, map, msg);
	}
	public static String getMsgTime (HashMap<String,MsgIndex> map, String msg)
	{
		String ts = getMsgValue (Fields.timestamp, map, msg);
		if (ts==null || ts.isEmpty())
			return "n/a";
		int h = (int)(ts.charAt(0)-'0');
		int m = (int)(ts.charAt(1)-'0');
		int s = (int)(ts.charAt(2)-'0');
		return String.format("%02d:%02d:%02d",h,m,s);
	}
	public static String getMsgPrice (HashMap<String,MsgIndex> map, String msg)
	{
		String price = getMsgValue (Fields.price, map, msg);
		if (price!=null && !price.isEmpty())
			return price;
		return "NA";
	}
	public static String getMsgQuote (HashMap<String,MsgIndex> map, String msg)
	{
		String bid   = getMsgValue (Fields.bidPrice, map, msg);
		String offer = getMsgValue (Fields.offerPrice, map, msg);
		if (bid!=null && !bid.isEmpty() && offer!=null && !offer.isEmpty() )
			return ("bid="+bid+":offer="+offer);
		return ("bid=NA:offer=NA");
	}
	public static String getMsgBid (HashMap<String,MsgIndex> map, String msg)
	{
		String bid = getMsgValue (Fields.bidPrice, map, msg);
		if (bid!=null && !bid.isEmpty())
			return bid;
		return "0";
	}
	public static String getMsgOffer (HashMap<String,MsgIndex> map, String msg)
	{
		String offer = getMsgValue (Fields.offerPrice, map, msg);
		if (offer!=null && !offer.isEmpty())
			return offer;
		return "0";
	}
	public static long getMsgSeqnum (HashMap<String,MsgIndex> map, String msg)
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
	public static long getMsgSeqnum (SourceType sourceType, String msg)
	{
		long seqnum = 0;
		try{
			if (sourceType == SourceType.NyseCts)
				seqnum = Integer.parseInt (msg.substring (CtsSeqnum_startIndex, CtsSeqnum_endIndex));
			else if (sourceType == SourceType.NyseCqs)
				seqnum = Integer.parseInt (msg.substring (CqsSeqnum_startIndex, CqsSeqnum_endIndex));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seqnum;
	}
	public static String getTagValue(String sTag, Element eElement)
	{
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}
	public static MsgType getMsgType (SourceType sourceType, String msg)
	{
		String source = sourceType.name().toLowerCase();
		char msgType = msg.charAt(MsgOffset_type);
		for (MsgType t : MsgType.values())
		{
			if (t.name().toLowerCase().startsWith(source) && t.getValue()==msgType)
				return t;
		}
		return MsgType.None;
	}
}
