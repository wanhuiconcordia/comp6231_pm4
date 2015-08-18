package tools.message;
import tools.channel.Channel;
import tools.channel.ChannelManager;

public abstract class MessageProcesser {
	public synchronized void processMessage(ChannelManager channelManager, Message msg){
		if(channelManager.channelMap.containsKey(msg.sender)){
			Channel channel = channelManager.channelMap.get(msg.sender);
			channel.timeoutTimes = 0;
			if(msg.senderSeq < channel.peerSeq){
				if(msg.action == Action.INIT
						|| msg.action == Action.askInitSync){
					channel.localSeq = 0;
					processNewRequest(channelManager, channel, msg);
				}else{
					System.out.println("Delayed msg, drop...");
				}
			}else if(msg.senderSeq == channel.peerSeq){
				processDuplicaRequest(channelManager, channel, msg);				
			}else if(msg.senderSeq == channel.peerSeq + 1){
//				System.out.println("New good seq. Response and backup the packet");
				channel.peerSeq = msg.senderSeq;
				processNewRequest(channelManager, channel, msg);				
			}else{
				System.out.println("Messed seq.");
			}
		}else{
			System.out.println("channelMap does not contian " + msg.sender);
			channelManager.loggerClient.write("channelMap does not contian " + msg.sender);
		}
	}
	
	public void processTimeout(ChannelManager channelManager) {
		//System.out.println("MessageProcesser::processTimeout() is called.");
		for(Channel channel: channelManager.channelMap.values()){
			if(channel.isWaitingForRespose){
				channel.timeoutTimes++;
				//System.out.println(channel.peerProcessName + " channel.timeoutTimes:" + channel.timeoutTimes);
				
				if(channel.timeoutTimes > 10){
					channel.isWaitingForRespose = false;
				}else{
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(channel.backupPacket);
					}
				}
			}
		}
	}
	
	public void ackBack(ChannelManager channelManager,
			Channel channel){
		Message outGoingMsg = new AckMessage(channel.localProcessName
				, ++channel.localSeq
				, channel.peerSeq);
		channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost,  channel.peerPort, outGoingMsg); 
		channel.isWaitingForRespose = false;
		synchronized (channelManager.outgoingPacketQueueLock) {
			channelManager.outgoingPacketQueue.add(channel.backupPacket);
		}
	}
	
	public abstract void processNewRequest(ChannelManager channelManager, Channel channel, Message msg);
	public abstract void processDuplicaRequest(ChannelManager channelManager, Channel channel, Message msg);
}
