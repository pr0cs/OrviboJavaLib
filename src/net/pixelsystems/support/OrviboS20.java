package net.pixelsystems.support;

import java.net.InetAddress;

public class OrviboS20 {
	private String MAC=null;
	private InetAddress S20IP;
	
	public OrviboS20(String mac, InetAddress ip){
		MAC = mac;
		S20IP = ip;
	}

	public String getIP() {
		// TODO Auto-generated method stub
		return S20IP.getHostAddress();
	}

	public String getMAC() {
		// TODO Auto-generated method stub
		return MAC;
	}
}
