package common;

import java.util.Calendar;

public enum StatusType {
	StartMS	("", 0),
	RecvMPS	("", 0),
	SendMPS	("", 0),
	RecvMsg ("", 0),
	SendMsg ("", 0),
	QueSize	("", 0),
	NewCnct	("", 0),
	Waiting	("", 0),
	Timeout	("", 0),
	Symbol  ("", 0);
	
	private String sValue = "";
	private long   lValue = 0;

	StatusType (String sValue, long lValue)
	{
		this.sValue = sValue;
		this.lValue = lValue;
	}
	public void setStringValue (String sValue) { this.sValue = sValue; }
	public void setLongValue   (long lValue)   { this.lValue = lValue; }
	public void incrementIntValue ()           { this.lValue++;	}
	public String getStringValue ()            { return this.sValue; }
	public long getLongValue ()                { return this.lValue; }

	private static long getNowTime()
	{
		final Calendar c = Calendar.getInstance();
		return c.getTimeInMillis();
	}
	public static void setStartMS ()
	{
		StatusType.StartMS.setLongValue(getNowTime());
	}
	public static void resetAll ()
	{
		for (StatusType st : StatusType.values()) {
			st.setLongValue(0);
			st.setStringValue("");
		}
		StatusType.setStartMS();
	}
	public static void setMPS (StatusType stMsg, StatusType stMps)
	{
		long secs = (getNowTime() - StatusType.StartMS.getLongValue()) / 1000;
		long msgs = stMsg.getLongValue();
		if (secs > 0)
			stMps.setLongValue (msgs/secs);
	}
}
