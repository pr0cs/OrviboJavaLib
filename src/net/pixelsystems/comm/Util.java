package net.pixelsystems.comm;

public class Util {
	static byte[]GlobalDiscovery=new byte[]{0x68, 0x64, 0x00, 0x06, 0x71, 0x61};
	private static byte[]Twenties=new byte[]{0x20, 0x20, 0x20, 0x20, 0x20, 0x20};
	private static byte[]Discovery=new byte[]{0x68, 0x64, 0x00, 0x06, 0x71, 0x67};
	
	private static byte[]StateOn=new byte[]{0x68,0x64,0x00,0x17,0x64,0x63};
	private static byte[]StateOff = new byte[]{0x68,0x64,0x00,0x17,0x64,0x63};
	static int Port = 10000;
	public enum MessageType{GlobalDiscovery,PowerOn,PowerOff,Unknown};
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
	public static MessageType getMessageType(byte[]response){
		if(getAsShort(response,4,5)==getAsShort(GlobalDiscovery,4,5)){
			return MessageType.GlobalDiscovery;
		}
		return MessageType.Unknown;
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
	public static short getAsShort(byte[] buffer, int pos1, int pos2) {
        return (short) (((buffer[pos1] & 0xff) << 8) | (buffer[pos2] & 0xff));
    }
}
