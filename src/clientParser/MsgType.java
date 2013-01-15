package clientParser;

public enum MsgType {
	None					(' '),
	NyseCtsShortTrade		('A'),
	NyseCtsLongTrade		('B'),
	NyseCtsEnchShortTrade	('I'),
	NyseCtsLineIntegrity	('T'),
	NyseCqsShortQuote		('D'),
	NyseCqsLongQuote		('B');

	private char value = ' ';
	MsgType (char value) { this.value = value; }
	public char getValue(){ return this.value; }

}
