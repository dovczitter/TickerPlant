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
	private BlockingQueue<String> queue;
	private common.Logger logger = new common.Logger (ClientParser.class.getName());

	ReaderThread (BlockingQueue<String> q) { this.queue = q; }
	
	private BlockingQueue<String> getQueue() { return this.queue; }
	
	public void run()
	{
		switch (getConnectType()) {
			case TCP: recvTcp (); break;
			case UDP: recvUdp (); break;
			default:
				break;
		}
	}
	private void recvTcp()
	{
		try {
			Socket socket = getTcpSocket();
			DataInputStream dis = new DataInputStream (socket.getInputStream());
			BlockingQueue<String> q = getQueue();
		    while (true)
		    {
		    	int msgLen = readLength(dis);
		    	String msg = readFully (dis, msgLen);
			    StatusType.RecvMsg.incrementIntValue();
			    q.put(msg);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("resource")
	private void recvUdp()
	{
		int port = getHostPort();
		try {
			byte[] receiveData = new byte[1024];
			DatagramSocket clientSocket  = new DatagramSocket (port);
			BlockingQueue<String> q = getQueue();
		    while (true)
		    {
		    	DatagramPacket receivePacket = new DatagramPacket (receiveData, receiveData.length);
    			clientSocket.receive (receivePacket);
	    		StatusType.RecvMsg.incrementIntValue();
			    q.put (new String (receivePacket.getData()));
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Socket getTcpSocket () throws Exception
	{
		Socket socket = null;
		String server = hostName.getValue();
		int port = getHostPort();
		while (socket == null) {
		    try {
			    logger.logInfo ("Connecting to host "+server+",  port "+port);
			    StatusType.Waiting.incrementIntValue();
			    
		    	InetAddress inteAddress = InetAddress.getByName (server);
		    	SocketAddress socketAddress = new InetSocketAddress (inteAddress, port);
		    	socket = new Socket();
		    	socket.connect(socketAddress, getTimeout());
		    } 
		    catch (SocketTimeoutException ste) {
				socket = null;
				logger.logConsole ("Waiting for connection to: " + server);
		    }
		}
	    return socket;
	}
	private int readLength (DataInputStream dis) throws IOException
	{
	    int len = 4;
	    byte[] data = new byte[len];
	    dis.readFully(data,0,len);
	    String s = new String (data);
	    int rlen = 0;
	    try {
	    	rlen = Integer.parseInt (s.replaceAll ("^0*", ""));
	    }
	    catch (Exception e) {
	    	logger.logError (e.getMessage());
	    	rlen = 0;
		}
		return rlen;
	}
	private String readFully (DataInputStream dis, int length) throws IOException
	{
	    byte[] data = new byte[length];
	    dis.readFully(data,0,length);
		return new String (data);
	}
}
