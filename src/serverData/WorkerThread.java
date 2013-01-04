// [server] Worker.java
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
import java.util.List;

import common.ConfigType;
import common.StatusType;

public class WorkerThread extends Thread
{
	protected Socket clientSocket;
	private static common.Logger logger = new common.Logger (ServerData.class.getName());
	
	WorkerThread () { start(); }
	WorkerThread (Socket clientSocket) {
		this.clientSocket = clientSocket;
		start();
	}
	private Socket getClientSocket(){ return this.clientSocket; }

	public void run()
	{
		logger.logInfo("Worker start...");
		switch (getConnectType())
		{
			case TCP:
				sendTcp(getClientSocket());
				break;
			case UDP:
				sendUdp();
				break;
			default:
				break;
		}
		logger.logInfo("Worker end...");
		System.exit(0);
	}
	private void sendTcp (Socket clientSocket)
	{
		try { 
			DataOutputStream dos = new DataOutputStream (clientSocket.getOutputStream());
			while (ServerData.serverContinue)
			{
				while (!ServerData.sendFlag)
					Thread.sleep(1);
				sendTcpData (dos);
			}
			clientSocket.close(); 
		} 
		catch (Exception e) { 
			logger.logError ("Problem with Communication Server: "+e.getMessage());
		}
	}
	private void sendUdp ()
	{
		try {
			int serverPort = 9999;
			int datagramPort = ConfigType.getHostPort();;
			
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
			logger.logError ("Problem with Communication Server: "+e.getMessage());
		}
	}
	private void sendTcpData (DataOutputStream dos)
	{
		try { 
			logger.logInfo ("Server: About to send data...");
			StringBuffer sb = new StringBuffer();
			for (String d : getFileData ()) {
				String s = String.format("%04d%s",d.length(),d);
				sb.append(s);
				StatusType.SendMsg.incrementIntValue();
				/****
				 * 	This pre-buffering increased throughput
				 *  from 55k to 70k mps.
				 */
				if (sb.length() > 4096) { 
					s = sb.toString();
					dos.write(s.getBytes(),0,s.length());
					sb.setLength(0);
				}
			}
			logger.logInfo ("Server: ...Complete."); 
		} 
		catch (Exception e) { 
			logger.logError ("Problem with Communication Server : "+e.getMessage());
			System.exit(1); 
		} 
	}
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
