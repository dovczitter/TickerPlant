// = ConsoleCmd.java =
package common;

import clientParser.Console;

public enum ConsoleCmd
{
	None		(""),
	Down		(""),
	Help		(""),
	ResetStats	(""),
	Stats		(""),
	Ticker		("");
	private String value = "";
	ConsoleCmd (String value)            { this.value = value; }
	public  String getValue ()           { return this.value; }
	public  void setValue (String value) { this.value = value; }

	public static ConsoleCmd getCmd (String name)
	{
		for (ConsoleCmd cmd : ConsoleCmd.values()) {
			String cmdName = cmd.name().toLowerCase();
			if ((cmd != None) && name.length() > 0 && cmdName.startsWith(name.toLowerCase()))
				return cmd;
		}
		return None;
	}
	public static String getCmds()
	{
		StringBuilder sb = new StringBuilder();
		for (ConsoleCmd cmd : ConsoleCmd.values()) {
			switch (ConfigType.getServerType()) {
				case ClientParser:
					if (cmd==Help || cmd==Down || cmd==Stats || cmd==ResetStats)
						break;
					continue;
				case ServerData:
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
		boolean isServer = (ConfigType.getServerType() == ConfigType.ServerType.ServerData); 
		
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
}
