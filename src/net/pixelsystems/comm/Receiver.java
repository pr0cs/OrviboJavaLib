package net.pixelsystems.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import net.pixelsystems.support.OrviboS20;
import net.pixelsystems.support.ReceiverResponseCallback;

public class Receiver extends Thread {
	private InetAddress receiverIP;
	private int timeout;
	private boolean subscribed;
	private ReceiverResponseCallback callback;
	
	
	public Receiver(int timeout,ReceiverResponseCallback callback) throws UnknownHostException{
		this.timeout=timeout;
		receiverIP = InetAddress.getLocalHost();
		this.callback = callback;
	}
	public boolean isSubscribed(){
		return subscribed;
	}
	
	@Override
	public void run(){
		DatagramSocket client = null;
		try{
			client = new DatagramSocket(Util.Port,receiverIP);
			client.setBroadcast(true);
			client.setSoTimeout(timeout);
		}catch(SocketException se){
			callback.exceptionEncountered(se);
			client.close();
			return;
		}
		byte[] buffer = new byte[1024];
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		try{
			client.receive(reply);
		}catch(IOException ioex){
			callback.exceptionEncountered(ioex);
			client.close();
			return;
		}
		byte[] response = reply.getData();
		if(response[0]=='h' && response[1]=='d'){
			if(Util.getMessageType(response)==Util.MessageType.GlobalDiscovery){
				String str = Util.bytesToHex(response);
				int startMACIdx = str.indexOf("ACCF");
				OrviboS20 s20 = new OrviboS20(str.substring(startMACIdx,startMACIdx+12),
						reply.getAddress());
				callback.s20Acquired(s20);
			}
		}
		client.close();
	}
}
