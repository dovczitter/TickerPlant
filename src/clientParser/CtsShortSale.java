/*
 *   File : CtsShortSale.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

public enum CtsShortSale {
	symbol						(3),
	saleCondition				(1),
	volume						(4),
	denominator					(1),
	price						(8),
	consolidatedPriceIndicator	(1),
	participantPriceIndicator	(1),
	reserved					(1);
	private int size;
	CtsShortSale (int size) { this.size = size; }
	public int getSize()    { return this.size; }
}
