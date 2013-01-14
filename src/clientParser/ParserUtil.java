/*
 *   File : ParserUtil.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.ConfigType.SourceType;

public class ParserUtil
{
	private static int ctsHeaderSize = 0;
	private static int cqsHeaderSize = 0;
	private static HashMap <String, HashMap<String,MsgIndex>> parserMaps = new HashMap<String, HashMap<String,MsgIndex>>();
	
	private final static int MsgOffset_type = 1;
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
			map = getHeaderMap (sourceType);
			NodeList nodes = getNodeList (getXmlName (sourceType, msg));
	
			int startIndex,endIndex;
			int totalSize = getHeaderSize (sourceType);
			for (int i=0; i < nodes.getLength(); i++)
			{
				Node node = (Node) nodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
	 			   Element e = (Element) node;
				   int size  = Integer.parseInt(ParserUtil.getTagValue("size", e));
				   startIndex = totalSize;
				   endIndex   = totalSize + size;
				   totalSize  = endIndex;
				   map.put (ParserUtil.getTagValue("name", e), new MsgIndex (startIndex, endIndex));
			   }
			}
		}
		setParserMap (msgType, map);
		return map;
	}
	public static int getHeaderSize (SourceType sourceType)
	{
		int headerSize = 0;
		switch (sourceType)
		{
			case Cts: headerSize=ctsHeaderSize; break;
			case Cqs: headerSize=cqsHeaderSize; break;
			default: break;
		}
		return headerSize;
	}
	public static void setHeaderSize (SourceType sourceType, int headerSize)
	{
		switch (sourceType)
		{
			case Cts: ctsHeaderSize=headerSize; break;
			case Cqs: cqsHeaderSize=headerSize; break;
			default: break;
		}
	}
	private static String getXmlName (SourceType sourceType, String msg)
	{
		return (xmlRoot +getMsgType (sourceType,msg)+ ".xml");
	}
	private static String getHeaderXmlName (SourceType sourceType)
	{
		return (xmlRoot +sourceType.name()+ "Header.xml");
	}
	private static NodeList getNodeList (String xmlName)
	{
		NodeList nodeList = null;
		try {
			File fXmlFile = new File(xmlName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			nodeList = doc.getElementsByTagName("field");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return nodeList;
	}
	private static HashMap<String,MsgIndex> getHeaderMap (SourceType sourceType)
	{
		HashMap<String,MsgIndex> map = new HashMap<String,MsgIndex>();
		NodeList nodes = getNodeList (getHeaderXmlName(sourceType));

		int startIndex,endIndex;
		int headerSize = 0;
		for (int i=0; i < nodes.getLength(); i++)
		{
			Node node = (Node) nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
 			   Element e = (Element) node;
			   int size  = Integer.parseInt(ParserUtil.getTagValue("size", e));
			   startIndex = headerSize;
			   endIndex   = headerSize + size;
			   headerSize  = endIndex;
			   map.put (ParserUtil.getTagValue("name", e), new MsgIndex (startIndex, endIndex));
		   }
		}
		setHeaderSize (sourceType, headerSize);
		return map;
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
		return getMsgValue (Fields.price, map, msg);
	}
	public static String getMsgQuote (HashMap<String,MsgIndex> map, String msg)
	{
		String bid   = getMsgValue (Fields.bidPrice, map, msg);
		String offer = getMsgValue (Fields.offerPrice, map, msg);
		return ("bid="+bid+":offer="+offer);
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
