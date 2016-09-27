package net.pixelsystems;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import net.pixelsystems.comm.Receiver;
import net.pixelsystems.comm.Sender;
import net.pixelsystems.server.Server;
import net.pixelsystems.support.OrviboS20;
import net.pixelsystems.support.ReceiverResponseCallback;

public class SockOrPrototype implements ReceiverResponseCallback{

	public static void Endian(){
		//  reversed = (i&0xff)<<24 | (i&0xff00)<<8 | (i&0xff0000)>>8 | (i>>24)&0xff;
		//reversed = i<<24 | i>>8 & 0xff00 | i<<8 & 0xff0000 | i>>>24;
		
		/*
		 int little2big(int i) {
    return (i&0xff)<<24 | (i&0xff00)<<8 | (i&0xff0000)>>8 | (i>>24)&0xff;
}

		 */
	}
	public static void main(String[]args){
		//System.out.println("HERE");
		SockOrPrototype proto = new SockOrPrototype();
	
	
		try {
		//	proto.socket();
			//proto.multiCastSocket();
			//proto.pythonGlobalDiscover();
			//proto.pixelSystems();
			proto.RandS();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	//	SockOrDlg dlg = new SockOrDlg();
	//	dlg.setVisible(true);
	}
	
	private void RandS()throws IOException{
		Receiver receiver;
		try {
			receiver = new Receiver(8000, this);
			Sender sender = new Sender(receiver);
			sender.globalDiscover();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioex){
			ioex.printStackTrace();
		}
		
	}
	@Override
	public void s20Acquired(OrviboS20 s20){
		System.out.println("Found S20 socket:"+s20.getIP()+" MAC:"+s20.getMAC());
	}
	@Override
	public void exceptionEncountered(Exception ex){
		System.out.println("ERROR:"+ex.getMessage());
		ex.printStackTrace();
	}
	
	private void pixelSystems()throws IOException{
		Server server = new Server();
		try{
		    server.globalDiscover(8000);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	private void pythonGlobalDiscover() throws IOException{
		DatagramSocket sock = null;//new DatagramSocket();
		//sock.setBroadcast(true);
		// HF-LPB100 chip can be controlled over port 48899
		String hostIP = "192.168.0.255";
		int port=10000;
		InetAddress host = InetAddress.getByName(hostIP);

		while(true)
		{
			//byte[]payload=new byte[]{0x71, 0x61};// globaldiscover(
			//byte[] data = new byte[]{0x68,0x64,0x00,(byte)(payload.length+4),payload[0],payload[1]};
			byte[]data=new byte[]{0x68, 0x64, 0x00, 0x06, 0x71, 0x61};
		//	data=new byte[]{  0x61, 0x71, 0x06, 0x00,0x64,0x68};
		//	data=new byte[]{  0x64, 0x68, 0x06, 0x00,0x61,0x71};
			
			// data = [ 0x68, 0x64, 0x00, len(payload)+4 ]
			// data.extend ( payload )
			// self.sock.sendto ( ''.join([ struct.pack ( 'B', x ) for x in data ]), ( ip, 10000 ) )

		//	DatagramPacket  dp = new DatagramPacket(data , data.length , host , port);
			//sock.send(dp);

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
					try {
						sock = new DatagramSocket();//port,broadcast);
						sock.setBroadcast(true);
						DatagramPacket sendPacket = new DatagramPacket(data, data.length, broadcast, port);
						sock.send(sendPacket);
						host = broadcast;
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
				}
			}

			System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

			
			//now receive reply
			//buffer to receive incoming data
			String clientIP = "127.0.0.1";
			
			InetAddress clientadd = InetAddress.getByName("192.168.0.12");

			DatagramSocket client = new DatagramSocket(port,clientadd);
			client.setBroadcast(true);
			
			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			System.out.println("about to receive");
			client.receive(reply);
			System.out.println("received");
			byte[] data2 = reply.getData();
			String s = new String(data2, 0, reply.getLength());
			client.close();
			//echo the details of incoming data - client ip : client port - client message
			System.out.println(reply.getAddress().getHostAddress() + " : " + reply.getPort() + " - " + s);
			sock.close();
			if(true){
				break;
			}
		}
		
	}
	private void multiCastSocket() throws IOException{
		MulticastSocket socket = new MulticastSocket(48899);
		InetAddress group = InetAddress.getByName("192.168.0.255");//"("203.0.113.0");
		socket.joinGroup(group);

		DatagramPacket packet;
		for (int i = 0; i < 5; i++) {
		    byte[] buf = new byte[256];
		    String message="HF-A11ASSISTHREAD";
		    buf = message.getBytes();
		    packet = new DatagramPacket(buf, buf.length);
		    socket.receive(packet);

		    String received = new String(packet.getData());
		    System.out.println("Quote of the Moment: " + received);
		}

		socket.leaveGroup(group);
		socket.close();
	}
	private void socket()throws IOException{
		DatagramSocket sock = new DatagramSocket();
		// HF-LPB100 chip can be controlled over port 48899
		String hostIP = "192.168.0.32";
		int port=48899;
		InetAddress host = InetAddress.getByName(hostIP);

		while(true)
		{
			//take input and send the packet
			//     System.out.println("Enter message to send : ");
			String s = (String)"HF-A11ASSISTHREAD";
			byte[] b = s.getBytes();

			DatagramPacket  dp = new DatagramPacket(b , b.length , host , port);
			sock.send(dp);

			//now receive reply
			//buffer to receive incoming data
			byte[] buffer = new byte[65536];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			sock.receive(reply);

			byte[] data = reply.getData();
			s = new String(data, 0, reply.getLength());

			//echo the details of incoming data - client ip : client port - client message
			System.out.println(reply.getAddress().getHostAddress() + " : " + reply.getPort() + " - " + s);
			sock.close();
			if(true){
				break;
			}
		}
	}
}
