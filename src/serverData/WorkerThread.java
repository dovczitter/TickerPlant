/*
 *   File : WorkerThread.java [Server] 
 * Author : Dov Czitter
 *   Date : 08jan2013
 */
package serverData;

import static common.ConfigType.getConnectType;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import common.ConfigType;
import common.StatusType;

public class WorkerThread extends Thread
{
	protected Socket clientSocket;
	
	WorkerThread () { start(); }
	WorkerThread (Socket clientSocket) {
		this.clientSocket = clientSocket;
		start();
	}
	private Socket getClientSocket(){ return this.clientSocket; }

	public void run()
	{
		ServerData.logger.logInfo("Worker start...");
		switch (getConnectType())
		{
			case TCP:
				sendTcp (getClientSocket());
				break;
			case UDP:
				sendUdp ();
				break;
			default:
				break;
		}
		ServerData.logger.logInfo("Worker end...");
		System.exit(0);
	}
	/*
	 * sendTcp ():
	 * 		Send streaming tcp data to a well known client socket.
	 */
	private void sendTcp (Socket clientSocket)
	{
		try { 
			DataOutputStream dos = new DataOutputStream (clientSocket.getOutputStream());
			while (ServerData.serverContinue)
			{
				while (!ServerData.sendFlag)
					Thread.sleep(1);
				for (String d : getPackedData()) {
					dos.write(d.getBytes(),0,d.length());
				}
			}
			clientSocket.close(); 
		} 
		catch (Exception e) { 
			ServerData.logger.logError ("Problem with Communication Server: "+e.getMessage());
		}
	}
	/*
	 * sendUdp ():
	 * 		Send sample file test data as udp DatagramPackets.
	 */
	private void sendUdp ()
	{
		try {
			int serverPort = 9999;
			int datagramPort = ConfigType.getHostPort();;
			
			@SuppressWarnings("resource")
			DatagramSocket socket = new DatagramSocket(serverPort);
			socket.setBroadcast (true);
			InetAddress ipAddress = InetAddress.getLocalHost();
	        while (true)
	        {
				while (!ServerData.sendFlag)
					Thread.sleep(1);
				for (String d : getFileData ()) {
					StatusType.SendMsg.incrementIntValue();
					socket.send (new DatagramPacket (d.getBytes(), d.length(), ipAddress, datagramPort));
				}
	        }
       	}
		catch (Exception e) { 
			ServerData.logger.logError ("Problem with Communication Server: "+e.getMessage());
		}
	}
	/*
	 *  getPackedData ():
	 * 		Tcp data packet format : <4 byte message length>+<message> 
	 * 		Pack each data packet into 4k blocks for efficient transport.
	 */
	private List<String> getPackedData () throws IOException
	{
		List<String> data = new ArrayList<String>();
		try { 
			StringBuffer sb = new StringBuffer();
			for (String d : getFileData ()) {
				String s = String.format("%04d%s",d.length(),d);
				sb.append(s);
				StatusType.SendMsg.incrementIntValue();
				if (sb.length() > 4096) { 
					data.add(sb.toString());
					sb.setLength(0);
				}
			}
			if (sb.length() > 0)  
				data.add(sb.toString());
		} 
		catch (Exception e) { 
			ServerData.logger.logError ("Problem with Communication Server : "+e.getMessage());
			System.exit(1); 
		} 
		return data;
	}
	/*
	 *  getFileData ():
	 * 		Retrieve sample file test data for tcp or udp send.
	 */
	private List<String> getFileData () throws IOException
	{
		String fileName = ConfigType.dataFilename.getValue();
		File f = new File(fileName);
		if(!f.exists() || !f.canRead())
		{
  	  		String workingDir = System.getProperty("user.dir");
  	  		String finalFile  = workingDir + File.separator + fileName;
  	  		f = new File (finalFile);
  			if(!f.exists() || !f.canRead()) {
  				throw new IOException ("Failed to read "+fileName);
  			}
  			fileName = finalFile;
		}
	    Path path = Paths.get (fileName);
	    return Files.readAllLines (path, java.nio.charset.StandardCharsets.UTF_8);
	}
}
