package net.pixelsystems.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Server {
	private static byte[]GlobalDiscovery=new byte[]{0x68, 0x64, 0x00, 0x06, 0x71, 0x61};
	private static byte[]Twenties=new byte[]{0x20, 0x20, 0x20, 0x20, 0x20, 0x20};
	private static byte[]Discovery=new byte[]{0x68, 0x64, 0x00, 0x06, 0x71, 0x67};
	
	private static byte[]StateOn=new byte[]{0x68,0x64,0x00,0x17,0x64,0x63};
	private static byte[]StateOff = new byte[]{0x68,0x64,0x00,0x17,0x64,0x63};
	private static int Port = 10000;
	private InetAddress S20InetAddress;
	private String MAC;
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	public static byte[]hex2ba(String hex){
	//OrviboAllOne.prototype.hex2ba = function(hex) { // Takes a string of hex and turns it into a byte array: ['0xAC', '0xCF] etc.
	//    arr = []; // New array
//		for (var i = 0; i < hex.length; i += 2) { // Loop through our string, jumping by 2 each time
//		    arr.push("0x" + hex.substr(i, 2)); // Push 0x and the next two bytes onto the array
	//	}
//		return arr;
		return null;
	}

	public Server(){

	}
	private static short getAsShort(byte[] buffer, int pos1, int pos2) {
        return (short) (((buffer[pos1] & 0xff) << 8) | (buffer[pos2] & 0xff));
    }
	public boolean globalDiscover(int timeOut) throws IOException{
		// parts of this method shamelessly stolen from
		// http://michieldemey.be/blog/network-discovery-using-udp-broadcast/

		// Broadcast the message over all the network interfaces
		Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
			if (networkInterface.isLoopback() || !networkInterface.isUp()) {
				continue; // Don't want to broadcast to the loopback interface
			}
			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
				InetAddress broadcast = interfaceAddress.getBroadcast();
				if (broadcast == null) {
					continue;
				}
				// Send the broadcast package!			

				DatagramSocket broadcastSocket = new DatagramSocket();
				broadcastSocket.setBroadcast(true);
				DatagramPacket sendPacket = new DatagramPacket(GlobalDiscovery, GlobalDiscovery.length, broadcast, Port);
				broadcastSocket.send(sendPacket);
			//	broadcastSocket.close();
				System.out.println(getClass().getName() + " >>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
			}
		}
		InetAddress clientadd = InetAddress.getLocalHost();//InetAddress.getByName("192.168.0.12");

		DatagramSocket client = new DatagramSocket(Port,clientadd);
		client.setBroadcast(true);
		client.setSoTimeout(timeOut);
		try{
			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			client.receive(reply);
			byte[] response = reply.getData();
			if(response[0]=='h' && response[1]=='d'){
				if(getAsShort(response,4,5)==getAsShort(GlobalDiscovery,4,5)){
					String str = bytesToHex(response);
					int startMACIdx = str.indexOf("ACCF");
					MAC=str.substring(startMACIdx,startMACIdx+12);
					System.out.println("MAC:"+MAC);
					S20InetAddress = reply.getAddress();
					String s = new String(response, 0, reply.getLength());
					System.out.println(S20InetAddress.getHostAddress() + " : " + reply.getPort() + " - " + s);
				}
			}
		}catch(SocketException se){
			client.close();
			return false;
		}
		client.close();
		//echo the details of incoming data - client ip : client port - client message

		return true;

	}
	
	/*macReversed = this.hex2ba(item.macaddress); // Convert our MAC address into a byte array (e.g. [0x12, 0x23] etc.)
			macReversed = macReversed.slice().reverse(); // And reverse the individual sections (e.g. ACCF becomes CFAC etc.)
		    payload = []; // Clear out our payload
		    payload = payload.concat(['0x68', '0x64', '0x00', '0x1e', '0x63', '0x6c'], this.hex2ba(item.macaddress), twenties, macReversed, twenties); // The subscription packet
		    this.sendMessage(payload, item.ipaddress, function(){ // Send the message and when that's done..
		    */
}
