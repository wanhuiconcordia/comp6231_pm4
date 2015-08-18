package manufacturer;

import rm.ReplicaStatus;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.channel.ReplicaChannel;
import tools.message.AckMessage;
import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.replica.ReplicaCrushMessage;

public class ManufacturerFEMessageProcesser extends MessageProcesser {

	@Override
	public void processNewRequest(ChannelManager channelManager, Channel channel, Message msg) {
		if(msg.action == Action.ACK){
			channel.isWaitingForRespose = false;
		}else{
			channel.receivedMessage = msg;

			switch(msg.action){
			case processPurchaseOrder:
			case getProductInfo:
			case receivePayment:
			case getProductList:
//				System.out.println(channel.peerProcessName + " message is saved in receivedMessage.");
				break;
			case INIT:
				channel.localSeq = 0;
				channel.peerSeq = msg.senderSeq;
				if(channel.group == Group.REPLICA){
					((ReplicaChannel)channel).replicaStatus = ReplicaStatus.REPLICA_GOOD;
				}
				break;
			case REPLICA_CRUSH:
				ReplicaCrushMessage replicaCrushMsg = (ReplicaCrushMessage) msg;
				if(channelManager.channelMap.containsKey(replicaCrushMsg.replicaName)){
					ReplicaChannel crushReplicaChannel = (ReplicaChannel)channelManager.channelMap.get(replicaCrushMsg.replicaName);
					crushReplicaChannel.replicaStatus = ReplicaStatus.REPLICA_CRUSH;
				}
				break;
			default:
				System.out.println("Unrecognizable action");
				break;
			}
			
			ackBack(channelManager, channel);
		}
	}

	@Override
	public void processDuplicaRequest(ChannelManager channelManager,
			Channel channel, Message msg) {
		// TODO Auto-generated method stub
		
	}

}
