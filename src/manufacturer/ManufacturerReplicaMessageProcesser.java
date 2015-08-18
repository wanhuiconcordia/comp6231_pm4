package manufacturer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.manufacturerReplica.ManufacturerDoSyncMessage;
import tools.message.replica.AskSyncMessage;
import tools.message.rm.RMSyncMessage;

public class ManufacturerReplicaMessageProcesser extends MessageProcesser{
	public ManufacturerReplica manufacturerReplica;

	public ManufacturerReplicaMessageProcesser(ManufacturerReplica manufacturerReplica){
		this.manufacturerReplica = manufacturerReplica;
	}
	
	@Override
	public void processNewRequest(ChannelManager channelManager,
			Channel channel, Message msg) {
		if(msg.action == Action.ACK){
			channel.isWaitingForRespose = false;
		}else{
			channel.receivedMessage = msg;
			switch(msg.action){
			case INIT:
				channel.localSeq = 0;
				ackBack(channelManager, channel);
				break;
			case askInitSync:
				channel.localSeq = 0;
			case askSync:
				channel.peerSeq = msg.senderSeq;
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
						, channel.peerPort
						, new ManufacturerDoSyncMessage(channel.localProcessName
								, ++channel.localSeq
								, channel.peerSeq
								, manufacturerReplica.purchaseOrderMap));
				synchronized (channelManager.outgoingPacketQueueLock) {
					channelManager.outgoingPacketQueue.add(channel.backupPacket);
				}
				break;
			case doSync:
				ManufacturerDoSyncMessage doSyncMessage = (ManufacturerDoSyncMessage)msg;
				manufacturerReplica.purchaseOrderMap = doSyncMessage.itemsMap;
				channel.isWaitingForRespose = false;
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

			case signIn:
//				ackBack(channelManager, channel);
//				ManufacturerSequencerSignInMessage signInMsg = (ManufacturerSequencerSignInMessage)msg;
//				Customer customer = manufacturerReplica.customerManager.find(signInMsg.customerReferenceNumber, signInMsg.password);
//				if(channelManager.channelMap.containsKey("ManufacturerFE")){
//					Channel FEChannel = channelManager.channelMap.get("ManufacturerFE");
//					Message responsMsg = new ManufacturerReplicaSignInResultMessage(FEChannel.localProcessName
//							, ++FEChannel.localSeq
//							, FEChannel.peerSeq
//							, customer);
//					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
//					FEChannel.isWaitingForRespose = true;
//				}

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
