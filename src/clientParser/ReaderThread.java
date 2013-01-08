/*
 *   File : ReaderThread.java 
 * Author : Dov Czitter
 *   Date : 08jan2013
 */
package clientParser;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;

import common.StatusType;
import static common.ConfigType.*;

public class ReaderThread implements Runnable
{
	// Common reader worker queue.
	private final static int MaxMessageSize = 1024;
	private BlockingQueue<String> queue;
	private DataInputStream dataInputStream;
	private DatagramSocket  datagramSocket;
	private DatagramPacket  datagramPacket;
	
	private BlockingQueue<String> getQueue()     { return this.queue; }
	private DataInputStream getDataInputStream() { return this.dataInputStream; }
	private DatagramSocket  getDatagramSocket()  { return this.datagramSocket; }
	private DatagramPacket  getDatagramPacket()  { return this.datagramPacket; }
	/*
	 * ReaderThread();
	 * 		Initialize connection in the constructor.
	 */
	ReaderThread (BlockingQueue<String> q) throws Exception
	{
		this.queue = q;
		switch (getConnectType()) {
			case TCP: 
				dataInputStream = new DataInputStream (getTcpSocket().getInputStream());
				break;
			case UDP:
				datagramSocket = new DatagramSocket (getHostPort());
				byte[] receiveData = new byte[MaxMessageSize];
		    	datagramPacket = new DatagramPacket (receiveData, receiveData.length);
				break;
			default:
				break;
		}
	}
	public void run()
	{
		String msg = null;
		try {
			while (true) {
				switch (getConnectType()) {
					case TCP: msg = recvTcp(); break;
					case UDP: msg = recvUdp(); break;
					default: break;
				}
				if (msg!=null && !msg.isEmpty()) {
				    StatusType.RecvMsg.incrementIntValue();
					getQueue().put(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * recvTcp():
	 * 		Pended streaming tcp data recieve.
	 * 		Streaming tcp packet receiver, <4 byte data length> + <length data>.
	 */
	private String recvTcp() throws IOException
	{
    	return readFully (readLength());
	}
	/*
	 * recvUdp():
	 * 		Udp datagram receiver, one complete message per packet.
	 */
	private String recvUdp() throws IOException
	{
    	getDatagramSocket().receive (getDatagramPacket());
   		return (new String (getDatagramPacket().getData()));
	}
	/*
	 * getTcpSocket():
	 * 		Recieve tcp socket initialization.
	 * 		Based on initial Client configuration parameters.
	 */
	private Socket getTcpSocket () throws Exception
	{
		Socket socket = null;
		String server = hostName.getValue();
		int port = getHostPort();
		while (socket == null) {
		    try {
		    	ClientParser.logger.logInfo ("Connecting to host "+server+",  port "+port);
			    StatusType.Waiting.incrementIntValue();
			    
		    	InetAddress inteAddress = InetAddress.getByName (server);
		    	SocketAddress socketAddress = new InetSocketAddress (inteAddress, port);
		    	socket = new Socket();
		    	socket.connect(socketAddress, getTimeout());
		    } 
		    catch (SocketTimeoutException ste) {
				socket = null;
				ClientParser.logger.logConsole ("Waiting for connection to: " + server);
		    }
		}
	    return socket;
	}
	/*
	 * readLength ():
	 * 		Read the
	 */
	private int readLength () throws IOException
	{
		;
	    int len = 4;
	    byte[] data = new byte[len];
	    getDataInputStream().readFully(data,0,len);
	    String s = new String (data);
	    int rlen = 0;
	    try {
	    	rlen = Integer.parseInt (s.replaceAll ("^0*", ""));
	    }
	    catch (Exception e) {
	    	ClientParser.logger.logError (e.getMessage());
	    	rlen = 0;
		}
		return rlen;
	}
	private String readFully (int length) throws IOException
	{
	    byte[] data = new byte[length];
	    getDataInputStream().readFully(data,0,length);
		return new String (data);
	}
}
