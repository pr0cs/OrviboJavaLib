package net.pixelsystems.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.pixelsystems.support.SocketResponseCallback;
import net.pixelsystems.thread.DatagramListener;

public class SockOrDlg extends JDialog implements ActionListener,SocketResponseCallback {
	private JTextField message;
	private JTextField ip;
	private JTextField portControl;
	private int port=48899;
	private DefaultListModel<String>responsesModel;
	private JList responses;
	private JButton quit;
	private DatagramSocket socket=null;
	private boolean listening=false;
	private InetAddress host = null;
	private JButton send;
	DatagramListener listener= new DatagramListener(this);
	
	public SockOrDlg(){
		super((JFrame)null,false);
		setTitle("Orvibo socket");
		initControls();
		setSize(640, 480);
		pack();
	}
	private void initControls(){
		setLayout(new BorderLayout());
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel messagePanel = new JPanel();
		messagePanel.add(new JLabel("Message:"));
		message = new JTextField(30);
		message.setText("HF-A11ASSISTHREAD");
		messagePanel.add(message);
		send = new JButton("Send");
		send.addActionListener(this);
		messagePanel.add(send);
		JPanel configPanel = new JPanel();
		configPanel.add(new JLabel("IP:"));
		ip = new JTextField(15);
		ip.setText("192.168.0.32");
		configPanel.add(ip);
		configPanel.add(new JLabel("Port:"));
		portControl = new JTextField(10);
		portControl.setText("48899");
		configPanel.add(portControl);
		topPanel.add(configPanel,BorderLayout.NORTH);
		topPanel.add(messagePanel,BorderLayout.CENTER);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(topPanel, BorderLayout.NORTH);
		responsesModel = new DefaultListModel<String>();
		responses = new JList(responsesModel);
		
		responses.setMinimumSize(new Dimension(400, 400));
		getContentPane().add(responses, BorderLayout.CENTER);
		responsesModel.addElement("Application started...");
		quit = new JButton("Quit");
		quit.addActionListener(this);
		getContentPane().add(quit, BorderLayout.SOUTH);
		portControl.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {			
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				String portString = portControl.getText();
				try{
					int testPort = Integer.parseInt(portString);
					port = testPort;
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(SockOrDlg.this, "Port:"+portString+" is not a valid port");
					return;
				}
			
			}
			
		});
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource()==send){
			if(send.getText().length()<3){
				JOptionPane.showMessageDialog(this, "The message is empty, ignoring");
				return;
			}
			if(socket==null){
				try {
					InetAddress hostadd = InetAddress.getByName(ip.getText());
					host = hostadd;
				} catch (UnknownHostException e) {
					e.printStackTrace();
					thrownException(e);
					return;
				}
				try {
					DatagramSocket newsocket = new DatagramSocket();
					socket = newsocket;
				} catch (SocketException e) {
					e.printStackTrace();
					thrownException(e);
					return;
				}
			}
			listener.addMessage(message.getText());
			if(!listening){
				listening = true;
				listener.start();
			}
			
		}else if (arg0.getSource()==quit){
			if(!listening){
				dispose();
				System.exit(0);
				return;
			}
			listening=false;
		}
	}
	@Override
	public DatagramSocket getSocket() {
		return socket;
	}
	@Override
	public boolean isListening() {
		return listening;
	}
	@Override
	public InetAddress getHost() {
		return host;
	}
	@Override
	public int getPort() {
		return port;
	}
	@Override
	public void thrownException(IOException e) {
		responsesModel.addElement("ERROR:"+e.getMessage());
		
	}
	@Override
	public void response(byte[] data) {
		String s = new String(data, 0, data.length);
		responsesModel.addElement(s);
		
	}
	@Override
	public void shutdownComplete() {
		if(!socket.isClosed()){
			JOptionPane.showMessageDialog(SockOrDlg.this, "Could not reliably shut down socket connection");
			socket.close();
		}
		this.dispose();
		System.exit(0);
	}
}
