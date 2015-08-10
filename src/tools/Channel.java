package tools;

public abstract class Channel {
	public String localProcessName;
	public String peerProcessName;
	public String peerHost;
	public int peerPort;
	public int peerSeq;
	public int localSeq;
	public boolean hasCachedMsg;
	
	public Message cachedMsg;
	
	public Channel(String localProcessName, String peerProcessName, String peerHost, int peerPort){
		this.localProcessName = localProcessName;
		this.peerProcessName = peerProcessName;
		this.peerHost = peerHost;
		this.peerPort = peerPort;
		peerSeq = 1;
		localSeq = 0;
		hasCachedMsg = false;
	}
	
	public abstract void processMessage(Message message);
	
	public boolean isValidSequence(Message msg){
		if(msg.receiverSeq == localSeq 
				&& msg.senderSeq == peerSeq + 1
				|| msg.action == Action.ACK){
			return true;
		}
		
		System.out.println("Bad seq(delayed msg).");
		return false;
	}
}
