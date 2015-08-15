package retailer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.AckMessage;
import tools.message.Action;
import tools.message.ReplicaResultMessage;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;

public class RetailerRMMessageProcesser extends MessageProcesser{

	public RetailerRMMessageProcesser(String replicaName , int index){
		
	}
	@Override
	public void processNewRequest(ChannelManager channelManager,
			Channel channel, Message msg) {

		System.out.println("New good seq. Response and backup the packet");
		if(msg.action == Action.ACK){
			channel.isWaitingForRespose = false;
		}else if(msg.action == Action.REPLICA_RESULT){
			channel.backupPacket = new Packet(channel.peerHost
					, channel.peerPort
					, new AckMessage(channel.localProcessName
							, ++channel.localSeq
							, msg.senderSeq));
			synchronized(channelManager.outgoingPacketQueueLock) {
				channelManager.outgoingPacketQueue.add(channel.backupPacket);
			}
			//-----------------------------------
			ReplicaResultMessage failProcessMessage = (ReplicaResultMessage)msg;
			for(String failProcessName: failProcessMessage.goodReplicaList){
				if(channelManager.channelMap.containsKey(failProcessName)){
					//recover
					break;
				}else{
					//set good process
				}
			}
			
//			
//			channelManager.sequencerID++;
//				for(Channel castChannel: channelManager.channelMap.values()){
//					if(castChannel.group == Group.RetailerReplica){
//						castChannel.cachedMsg = generateRetailerSequencerMessage(castChannel.localProcessName
//								, ++castChannel.localSeq
//								, castChannel.peerSeq
//								, channelManager.sequencerID
//								, msg); 
//						castChannel.isWaitingForRespose = true;
//						synchronized(channelManager.outgoingPacketQueueLock) {
//							channelManager.outgoingPacketQueue.add(castChannel.backupPacket);
//						}
//					}
//				}
			//-----------------------------------
		}
		else{
			System.out.println("Unrecognizable action");
		}
	}
}
