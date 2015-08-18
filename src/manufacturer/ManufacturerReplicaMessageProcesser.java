package manufacturer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.manufacturerReplica.ManufacturerDoSyncMessage;
import tools.message.manufacturerReplica.ManufacturerReplicaGetProductInfoMessage;
import tools.message.manufacturerReplica.ManufacturerReplicaGetProductListMessage;
import tools.message.manufacturerReplica.ManufacturerReplicaProcessPurchaseOrderMessage;
import tools.message.manufacturerReplica.ManufacturerReplicaReceivePaymentMessage;
import tools.message.manufacturerSequencer.ManufacturerSequencerGetProductInfoMessage;
import tools.message.manufacturerSequencer.ManufacturerSequencerGetProductListMessage;
import tools.message.manufacturerSequencer.ManufacturerSequencerProcessPurchaseOrderMessage;
import tools.message.manufacturerSequencer.ManufacturerSequencerReceivePaymentMessage;
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
								, manufacturerReplica.purchaseOrderManager.itemsMap));
				synchronized (channelManager.outgoingPacketQueueLock) {
					channelManager.outgoingPacketQueue.add(channel.backupPacket);
				}
				break;
			case doSync:
				ManufacturerDoSyncMessage doSyncMessage = (ManufacturerDoSyncMessage)msg;
				manufacturerReplica.purchaseOrderManager.itemsMap = doSyncMessage.itemsMap;
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

			case processPurchaseOrder:
				ackBack(channelManager, channel);
				ManufacturerSequencerProcessPurchaseOrderMessage processPurchaseMsg 
				= (ManufacturerSequencerProcessPurchaseOrderMessage) msg;
				
				
				if(channelManager.channelMap.containsKey(manufacturerReplica.baseName 
						+ manufacturerReplica.manufacturerIndex + "FE")){
					Channel FEChannel = channelManager.channelMap.get(manufacturerReplica.baseName 
							+ manufacturerReplica.manufacturerIndex + "FE");
					Message responsMsg = new ManufacturerReplicaProcessPurchaseOrderMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, manufacturerReplica.processPurchaseOrder(processPurchaseMsg.item));
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
				}
				break;
			case getProductInfo:
				ackBack(channelManager, channel);
				ManufacturerSequencerGetProductInfoMessage getProductInfoMsg 
				= (ManufacturerSequencerGetProductInfoMessage) msg;
				
				
				if(channelManager.channelMap.containsKey(manufacturerReplica.baseName 
						+ manufacturerReplica.manufacturerIndex + "FE")){
					Channel FEChannel = channelManager.channelMap.get(manufacturerReplica.baseName 
							+ manufacturerReplica.manufacturerIndex + "FE");
					Message responsMsg = new ManufacturerReplicaGetProductInfoMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, manufacturerReplica.getProductInfo(getProductInfoMsg.aProdName));
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
				}
				break;
				
			case receivePayment:
				
				ackBack(channelManager, channel);
				ManufacturerSequencerReceivePaymentMessage receivePayMentMsg 
				= (ManufacturerSequencerReceivePaymentMessage) msg;
				
				
				if(channelManager.channelMap.containsKey(manufacturerReplica.baseName 
						+ manufacturerReplica.manufacturerIndex + "FE")){
					Channel FEChannel = channelManager.channelMap.get(manufacturerReplica.baseName 
							+ manufacturerReplica.manufacturerIndex + "FE");
					Message responsMsg = new ManufacturerReplicaReceivePaymentMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, manufacturerReplica.receivePayment(receivePayMentMsg.orderNum, receivePayMentMsg.totalPrice));
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
				}
				break;
				
			case getProductList:
				ackBack(channelManager, channel);
				ManufacturerSequencerGetProductListMessage getProductListMsg
				= (ManufacturerSequencerGetProductListMessage) msg;
				
				
				if(channelManager.channelMap.containsKey(manufacturerReplica.baseName 
						+ manufacturerReplica.manufacturerIndex + "FE")){
					Channel FEChannel = channelManager.channelMap.get(manufacturerReplica.baseName 
							+ manufacturerReplica.manufacturerIndex + "FE");
					Message responsMsg = new ManufacturerReplicaGetProductListMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, manufacturerReplica.getProductList());
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
				}
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
