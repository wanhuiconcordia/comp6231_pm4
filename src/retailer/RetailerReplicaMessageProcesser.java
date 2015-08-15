package retailer;

import tools.SignUpResult;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.message.AckMessage;
import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.retailerReplica.RetailerReplicaSignUpReultMessage;

public class RetailerReplicaMessageProcesser extends MessageProcesser {

	@Override
	public void processNewRequest(ChannelManager channelManager, Channel channel, Message msg) {
		if(msg.action == Action.ACK){
			channel.isWaitingForRespose = false;
		}else{
			channel.receivedMessage = msg;
			switch(msg.action){
			case getCatelog:
				break;
			case signIn:
				break;
			case signUp:
				Message outGoingMsg = new AckMessage(channel.localProcessName
						, ++channel.localSeq
						, channel.peerSeq);
				channel.backupPacket = new Packet(channel.peerHost,  channel.peerPort, outGoingMsg); 
				channel.isWaitingForRespose = false;
				synchronized (channelManager.outgoingPacketQueueLock) {
					channelManager.outgoingPacketQueue.add(channel.backupPacket);
				}

				if(channelManager.channelMap.containsKey("RetailerFE")){
					Channel FEChannel = channelManager.channelMap.get("RetailerFE");
					SignUpResult result;
//TODO THIS IS A TMP FAILURE TRIGGER
					System.out.println();
					if(FEChannel.localProcessName.endsWith("4")){
						result = new SignUpResult(true, 2000, "good signup");
					}else{
						result = new SignUpResult(true, 1000, "good signup");
					}
					Message responsMsg = new RetailerReplicaSignUpReultMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, result);
					FEChannel.backupPacket = new Packet(FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
					synchronized (channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(FEChannel.backupPacket);
					}

				}

				break;
			case submitOrder:
				break;
			default:
				break;

			}
		}
	}

}
