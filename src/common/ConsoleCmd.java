// = ConsoleCmd.java =
package common;

import client.Console;

public enum ConsoleCmd
{
	None		(""),
	Down		(""),
	Help		(""),
	ResetStats	(""),
	Stats		(""),
	Symbol		(""),
	Ticker		("");
	private String value = "";
	ConsoleCmd (String value)            { this.value = value; }
	public  String getValue ()           { return this.value; }
	public  void setValue (String value) { this.value = value; }

	public static ConsoleCmd getCmd (String input)
	{
		input = input.trim();
		int    beginIndex = input.indexOf(" ");
		String cmdString  = (beginIndex > 0) ? input.substring(0,beginIndex) : input;
		String value      = (beginIndex > 0) ? input.substring(beginIndex) : "";
		value = value.trim().toUpperCase();
		for (ConsoleCmd cmd : ConsoleCmd.values()) {
			String cmdName = cmd.name().toLowerCase();
			if ((cmd != None) && input.length() > 0 && cmdName.startsWith(cmdString.toLowerCase()))
			{
				if (!value.isEmpty())
					cmd.setValue(value);
				return cmd;
			}
		}
		return None;
	}
	public static String getCmds()
	{
		StringBuilder sb = new StringBuilder();
		for (ConsoleCmd cmd : ConsoleCmd.values()) {
			switch (ConfigType.getServerType()) {
				case Client:
					if (cmd==Help || cmd==Down || cmd==Stats || cmd==ResetStats)
						break;
					continue;
				case Server:
					if (cmd==Help || cmd==Ticker || cmd==Down || cmd==Stats || cmd==ResetStats)
						break;
					continue;
				default:
					continue;
			}
			sb.append (String.format("[%s] ",cmd.name()));
		}
		return sb.toString()+"\n";
	}
	private static void updateStats()
	{
		StatusType.setMPS (StatusType.RecvMsg, StatusType.RecvMPS);
		StatusType.setMPS (StatusType.SendMsg, StatusType.SendMPS);
		StatusType.QueSize.setLongValue(Console.getSharedQueueSize());
		StatusType.Seqnum.setStringValue(SequenceNumber.getStats());
	}
	public static String getStats()
	{
		updateStats();
		StringBuilder sb = new StringBuilder();
		boolean isServer = (ConfigType.getServerType() == ConfigType.ServerType.Server); 
		
		for (StatusType stat : StatusType.values()) {	
			if (stat == StatusType.StartMS || (isServer && stat==StatusType.Seqnum))
				continue;
			String sValue = stat.getStringValue();
			sb.append (String.format("[%s %d] ", (sValue.isEmpty() ? stat.name():sValue), stat.getLongValue()));
		}
		return sb.toString()+"\n";
	}
	public static void resetStats()
	{
		StatusType.resetAll();
	}
	public String setUserValue (String userInput)
	{
		String val = "";
		int beginIndex = userInput.trim().indexOf(" ");
		if (beginIndex > 0) {
			String symbol = userInput.trim().substring(beginIndex).trim();
			setValue(symbol);
		}
		return val;
	}

	

}
