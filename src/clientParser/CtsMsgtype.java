/*
 *   File : CtsMsgtype.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

public enum CtsMsgtype {
	NONE				(' '),
	SHORT_TRADE			('A'),
	LONG_TRADE			('B'),
	ENCH_SHORT_TRADE	('I'),
	LINE_INTEGRITY		('T');

	private char value = ' ';
	CtsMsgtype (char value) { this.value = value; }
	public char getValue(){ return this.value; }
}
