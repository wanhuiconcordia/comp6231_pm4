package tools.channel;

import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
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
	public boolean isWaitingForRespose;
	public int timeoutTimes;
	public Packet backupPacket;
	public Message receivedMessage;
	
	public Channel(String localProcessName, String peerProcessName, String peerHost, int peerPort, Group group){
		this.localProcessName = localProcessName;
		this.peerProcessName = peerProcessName;
		this.peerHost = peerHost;
		this.peerPort = peerPort;
		this.group = group;
		peerSeq = 0;
		localSeq = 0;
		
		isWaitingForRespose = false;
		timeoutTimes = 0;
	}
	
	public boolean verifySequence(Message msg){
		if(msg.senderSeq == peerSeq + 1		//This is the expecting seq
				|| msg.action == Action.INIT){
			return true;
		}
		
		System.out.println("Bad seq(delayed msg).");
		return false;
	}
}
