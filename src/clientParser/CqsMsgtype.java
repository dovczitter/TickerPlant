/*
 *   File : CqsMsgtype.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

public enum CqsMsgtype {
	NONE				(' '),
	SHORT_QUOTE			('D'),
	LONG_QUOTE			('B');

	private char value = ' ';
	CqsMsgtype (char value) { this.value = value; }
	public char getValue(){ return this.value; }
}
