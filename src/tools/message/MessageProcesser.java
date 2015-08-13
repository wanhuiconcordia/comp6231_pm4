package tools.message;
import tools.channel.Channel;
import tools.channel.ChannelManager;

public abstract class MessageProcesser {
	public void processMessage(ChannelManager channelManager, Message msg){
		if(channelManager.channelMap.containsKey(msg.sender)){
			Channel channel = channelManager.channelMap.get(msg.sender);
			channel.timeoutTimes = 0;
			if(msg.senderSeq < channel.peerSeq){
				if(msg.action == Action.INIT){
					channel.localSeq = 0;
					channel.backupPacket = new Packet(channel.peerHost
							, channel.peerPort
							, new AckMessage(channel.localProcessName
									, ++channel.localSeq
									, msg.senderSeq));
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(channel.backupPacket);
					}
				}else{
					System.out.println("Delayed msg, drop...");
				}
			}else if(msg.senderSeq == channel.peerSeq){
				System.out.println("Just received msg. Respond with backupPacket.");
				synchronized(channelManager.outgoingPacketQueueLock) {
					channelManager.outgoingPacketQueue.add(channel.backupPacket);
				}
				
			}else if(msg.senderSeq == channel.peerSeq + 1){
				System.out.println("New good seq. Response and backup the packet");
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
		for(Channel channel: channelManager.channelMap.values()){
			if(channel.isWaitingForRespose){
				channel.timeoutTimes++;
				
				if(channel.timeoutTimes > 3){
					channel.isWaitingForRespose = false;
				}else{
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(channel.backupPacket);
					}
				}
			}
		}
	}
	
	public abstract void processNewRequest(ChannelManager channelManager, Channel channel, Message msg);
}
