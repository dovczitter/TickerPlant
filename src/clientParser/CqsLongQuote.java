/*
 *   File : CqsLongQuote.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

public enum CqsLongQuote {
	symbol						(11),
	suffix						( 1),
	testIndicator				( 1),
	reserved1					( 3),
	financialStatus				( 1),
	currency					( 3),
	instrumentType				( 1),
	cancelCorrectionIndicator	( 1),
	settlementCondition			( 1),
	marketCondition				( 1),
	quoteCondition				( 1),
	reserved2					( 2),
	bidDenominator				( 1),
	bidPrice					(12),
	bidSize						( 7),
	offerDenominator			( 1),
	offerPrice					(12),
	offerSize					( 7),
	nasdaqMmid					( 4),
	nasdaqLocation				( 2),
	nasdaqDesk					( 1),
	reserved3					( 2),
	nationalBboIndicator		( 1),
	nasdaqBboIndicator			( 1);
	private int size;
	CqsLongQuote (int size) { this.size=size; }
	public int getSize() { return this.size; }
}
