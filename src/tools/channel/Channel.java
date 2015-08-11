package tools.channel;

import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.channel.Group;

public class Channel {
	public String localProcessName;
	public String peerProcessName;
	public String peerHost;
	public int peerPort;
	public Group group;
	public MessageProcesser messageProcesser; 
	public int peerSeq;
	public int localSeq;
	public boolean hasCachedMsg;
	public Message cachedMsg;
	
	public Channel(String localProcessName, String peerProcessName, String peerHost, int peerPort, Group group){
		this.localProcessName = localProcessName;
		this.peerProcessName = peerProcessName;
		this.peerHost = peerHost;
		this.peerPort = peerPort;
		this.group = group;
		peerSeq = 1;
		localSeq = 0;
		
		hasCachedMsg = false;
	}
	
	public boolean verifySequence(Message msg){
		if(msg.receiverSeq == localSeq 
				&& msg.senderSeq == peerSeq + 1
				|| msg.action == Action.ACK){
			return true;
		}
		
		System.out.println("Bad seq(delayed msg).");
		return false;
	}
}
