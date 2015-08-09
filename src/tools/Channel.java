package tools;

public class Channel {
	public String peerName;
	public Message lastSentMsg;
	public String peerHost;
	public int peerPort;
	public int peerSeq;
	public int localSeq;
	
	public Channel(String peerName, String peerHost, int peerPort){
		this.peerName = peerName;
		this.peerHost = peerHost;
		this.peerPort = peerPort;
		peerSeq = 1;
		localSeq = 0;
	}
}
