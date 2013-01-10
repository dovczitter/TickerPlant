/*
 *   File : CtsLongSale.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

public enum CtsLongSale {
	symbol						(11),
	suffix						( 1),
	testIndicator				( 1),
	reserved1					( 3),
	financialStatus				( 1),
	currency					( 3),
	heldIndicator				( 1),
	instrumentType				( 1),
	saleDays					( 3),
	saleCondition				( 4),
	reserved2					( 3),
	denominator					( 1),
	price						(12),
	volume						( 9),
	consolidatedPriceIndicator	( 1),
	participantPriceIndicator	( 1),
	reserved3					( 1),
	stopStockIndicator			( 1);
	private int size;
	CtsLongSale (int size) { this.size = size; }
	public int getSize()   { return this.size; }
}
