/*
 *   File : Element.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

public class MsgIndex {
	private int startIndex;
	private int endIndex;
	MsgIndex (int startIndex, int endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	public int getStartIndex() { return this.startIndex; }
	public int getEndIndex()   { return this.endIndex; }
}
