package retailer;

import tools.SignUpResult;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.replica.AskSyncMessage;
import tools.message.replica.DoSyncMessage;
import tools.message.retailerReplica.RetailerReplicaSignUpReultMessage;
import tools.message.rm.RMSyncMessage;

public class RetailerReplicaMessageProcesser extends MessageProcesser {

	@Override
	public void processNewRequest(ChannelManager channelManager, Channel channel, Message msg) {
		if(msg.action == Action.ACK){
			channel.isWaitingForRespose = false;
		}else{
			channel.receivedMessage = msg;
			switch(msg.action){
			case askSync:
				channel.localSeq = 0;
				channel.peerSeq = msg.senderSeq;
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
							, channel.peerPort
							, new DoSyncMessage(channel.localProcessName
									, ++channel.localSeq
									, channel.peerSeq
									, "dddd"));
				break;
			case doSync:
				ackBack(channelManager, channel);
				DoSyncMessage doSyncMessage = (DoSyncMessage)msg;
				//todo
				break;
			case sync:
				ackBack(channelManager, channel);
				RMSyncMessage syncMsg = (RMSyncMessage)msg;

				if(channelManager.channelMap.containsKey(syncMsg.goodReplicaName)){
					Channel replicaChannel = channelManager.channelMap.get(syncMsg.goodReplicaName);
					replicaChannel.backupPacket = new Packet(replicaChannel.peerProcessName, replicaChannel.peerHost
							, replicaChannel.peerPort
							, new AskSyncMessage(replicaChannel.localProcessName
									, ++replicaChannel.localSeq
									, replicaChannel.peerSeq));
				}else{
					System.out.println("Wrong. Cannot find " + syncMsg.goodReplicaName);
				}
				break;
			case HEART_BEAT:
				ackBack(channelManager, channel);
				break;
			case getCatelog:
				//TODO real logic
				break;
			case signIn:
				ackBack(channelManager, channel);
				//TODO real logic
				break;
			case signUp:
				ackBack(channelManager, channel);

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
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
					synchronized (channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(FEChannel.backupPacket);
					}

				}

				break;
			case submitOrder:
				//TODO real logic
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void processDuplicaRequest(ChannelManager channelManager,
			Channel channel, Message msg) {
		// TODO Auto-generated method stub
		
	}

}
