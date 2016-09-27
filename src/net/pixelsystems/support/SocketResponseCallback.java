package net.pixelsystems.support;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public interface SocketResponseCallback {

	public DatagramSocket getSocket();

	public boolean isListening();

	public InetAddress getHost();
	public int getPort();

	public void thrownException(IOException e);

	public void response(byte[] data);

	public void shutdownComplete();

}
