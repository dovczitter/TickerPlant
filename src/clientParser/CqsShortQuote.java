/*
 *   File : CqsShortQuote.java 
 * Author : Dov Czitter
 *   Date : 10jan2013
 */
package clientParser;

public enum CqsShortQuote {
	symbol					(3),
	quoteCondition			(1),
	reserved1				(2),
	bidDenominator			(1),
	bidPrice				(8),
	bidSize					(3),
	reserved2				(1),
	offerDenominator		(1),
	offerPrice				(8),
	offerSize				(3),
	reserved3				(1),
	nationalBboIndicator	(1),
	nasdaqBboIndicator		(1);
	private int size;
	CqsShortQuote (int size) { this.size=size; }
	public int getSize() { return this.size; }
}
