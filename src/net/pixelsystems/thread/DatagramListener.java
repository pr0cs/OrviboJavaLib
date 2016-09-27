package net.pixelsystems.thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import net.pixelsystems.support.SocketResponseCallback;

public class DatagramListener extends Thread{
	private SocketResponseCallback callback;
	private boolean waitingForResponse = false;
	List<String>messages = new ArrayList<String>();
	
	public DatagramListener(SocketResponseCallback callback){
		this.callback = callback;
	}
	public void addMessage(String message){
		synchronized (messages) {
			messages.add(message);
		}
	}
	@Override
	public void run(){
		while(callback.isListening()){
			if(!waitingForResponse){
				synchronized (messages) {
					int idx = messages.size()-1;
					if(idx !=-1){
					String toBroadcast = messages.get(idx);
					byte[] b = toBroadcast.getBytes();
		                 
		                DatagramPacket  dp = new DatagramPacket(b , b.length , callback.getHost() , callback.getPort());
		                try {
							waitingForResponse = true;
							messages.remove(idx);
							callback.getSocket().send(dp);
						} catch (IOException e) {						
							e.printStackTrace();
							callback.thrownException(e);
						}
					}
				}
			}else{
				byte[] buffer = new byte[65536];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                try {
					callback.getSocket().receive(reply);
					byte[] data = reply.getData();
	                String response = new String(data, 0, reply.getLength());
	                 
	                //echo the details of incoming data - client ip : client port - client message
	                System.out.println(reply.getAddress().getHostAddress() + " : " + reply.getPort() + " - " + response);
	                callback.response(data);
				} catch (IOException e) {
					e.printStackTrace();
					callback.thrownException(e);
				}
                waitingForResponse=false; 
                
			}
		}
		callback.getSocket().close();
		callback.shutdownComplete();
	}
}
