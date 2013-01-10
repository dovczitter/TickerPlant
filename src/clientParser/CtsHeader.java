/*
 *   File : CtsHeader.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

public enum CtsHeader
{
	category	(1),
	type		(1),
	network		(1),
	retransReq	(2),
	seqnum		(7),
	participant	(1),
	timestamp	(3);
	private int size;
	CtsHeader (int size) { this.size = size; }
	public int getSize() { return this.size; }
	public static char getMsgtypeValue (String msg) { return msg.charAt(1); }
}
