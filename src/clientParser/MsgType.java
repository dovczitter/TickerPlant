package clientParser;

public enum MsgType {
	None				(' '),
	CtsShortTrade		('A'),
	CtsLongTrade		('B'),
	CtsEnchShortTrade	('I'),
	CtsLineIntegrity	('T'),
	CqsShortQuote		('D'),
	CqsLongQuote		('B');

	private char value = ' ';
	MsgType (char value) { this.value = value; }
	public char getValue(){ return this.value; }

}
