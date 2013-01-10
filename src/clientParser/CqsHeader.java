/*
 *   File : CqsHeader.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

public enum CqsHeader {
	category		(1),
	type			(1),
	network			(1),
	retransReq		(2),
	identifier		(1),
	reserved		(2),
	seqnum			(9),
	participant		(1),
	timestamp		(6);
	private int size;
	CqsHeader (int size) { this.size=size; }
	public int getSize() { return this.size; }
	public static char getMsgtypeValue (String msg) { return msg.charAt(1); }
}
