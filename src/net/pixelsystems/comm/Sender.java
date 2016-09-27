package net.pixelsystems.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Sender extends Thread {
	private Receiver receiver;
	
	public Sender(Receiver receiver){
		this.receiver = receiver;
	}
	
	public void globalDiscover() throws IOException{
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
				DatagramPacket sendPacket = new DatagramPacket(Util.GlobalDiscovery, Util.GlobalDiscovery.length, broadcast, Util.Port);
				broadcastSocket.send(sendPacket);
				//	broadcastSocket.close();
				System.out.println(getClass().getName() + " >>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
			}
		}
		receiver.start();
	}
}
