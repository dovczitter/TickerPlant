package common;

public class SequenceNumber {
	private static long lastSeq = 0;
	private static long hiSeq = 0;
	private static long skipCount = 0;
	private static long gapCount = 0;
	private static long firstSeq = 0;
	public static void setStats (long seqnum)
	{
		if ((firstSeq > 0) && (seqnum > (lastSeq+1))) {
			skipCount += (seqnum - (lastSeq+1));
			gapCount++;
		}
		if (firstSeq == 0)
			firstSeq = seqnum;
		
		if (seqnum > hiSeq)
			hiSeq = seqnum;
		
		lastSeq = seqnum;
		return;
	}
	public static String  getStats ()
	{
		return ("lastSeq="+lastSeq+", skipCount="+skipCount+", gapCount="+gapCount);
	}
}
