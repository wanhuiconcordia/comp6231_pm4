package warehouse;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.warehouseReplica.WarehouseDoSyncMessage;
import tools.message.replica.AskSyncMessage;
import tools.message.rm.RMSyncMessage;

public class WarehouseReplicaMessageProcesser extends MessageProcesser{
	public WarehouseReplica warehouseReplica;

	public WarehouseReplicaMessageProcesser(WarehouseReplica warehouseReplica){
		this.warehouseReplica = warehouseReplica;
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
						, new WarehouseDoSyncMessage(channel.localProcessName
								, ++channel.localSeq
								, channel.peerSeq));
				break;
			case doSync:
				ackBack(channelManager, channel);
				WarehouseDoSyncMessage doSyncMessage = (WarehouseDoSyncMessage)msg;
				
				//TODO
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

			case shippingGoods:
				ackBack(channelManager, channel);
				break;
			case getProducts:
				ackBack(channelManager, channel);
//				WarehouseSequencerGetProductsMessage getProductsMsg = (WarehouseSequencerGetProductsMessage)msg;
//				Customer customer = warehouseReplica.customerManager.find(signInMsg.customerReferenceNumber, signInMsg.password);
//				if(channelManager.channelMap.containsKey("RetailerFE")){
//					Channel FEChannel = channelManager.channelMap.get("RetailerFE");
//					Message responsMsg = new RetailerReplicaSignInResultMessage(FEChannel.localProcessName
//							, ++FEChannel.localSeq
//							, FEChannel.peerSeq
//							, customer);
//					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
//					FEChannel.isWaitingForRespose = true;
//				}
				break;
			case getProductsByID:
				ackBack(channelManager, channel);
				break;
			case getProductsByType:
				ackBack(channelManager, channel);
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
