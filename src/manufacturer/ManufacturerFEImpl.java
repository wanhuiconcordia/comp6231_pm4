package manufacturer;

import javax.jws.WebService;

import manufacturer.ManufacturerFEMessageProcesser;
import tools.ConfigureManager;
import tools.Item;
import tools.ItemList;
import tools.LoggerClient;
import tools.Product;
import tools.ProductList;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.channel.ReplicaChannel;
import tools.fe.FE;
import tools.fe.ReplicaResponse;
import tools.message.Packet;
import tools.message.manufacturerFE.ManufacturerFEGetProductInfoMessage;
import tools.message.manufacturerFE.ManufacturerFEGetProductListMessage;
import tools.message.manufacturerFE.ManufacturerFEProcessPurchaseOrderMessage;
import tools.message.manufacturerFE.ManufacturerFEReceivePaymentMessage;
import tools.message.manufacturerReplica.ManufacturerReplicaGetProductInfoMessage;
import tools.message.manufacturerReplica.ManufacturerReplicaGetProductListMessage;
import tools.message.manufacturerReplica.ManufacturerReplicaProcessPurchaseOrderMessage;
import tools.message.manufacturerReplica.ManufacturerReplicaReceivePaymentMessage;
@WebService(endpointInterface = "manufacturer.ManufacturerInterface")
public class ManufacturerFEImpl extends FE implements ManufacturerInterface {
	public LoggerClient loggerClient;
	public String name;
	public int currentSequencerID;
	public Object cachedObj;
	public Object lock = new Object();
	
	/**
	 * Constructor
	 * @param name
	 * @param loggerClient
	 * @throws Exception 
	 */
	public ManufacturerFEImpl(String name , LoggerClient loggerClient) throws Exception {
		this.name = name;
		String fullName = name + "FE";
		this.loggerClient = loggerClient;
		String host = ConfigureManager.getInstance().getString(fullName + "Host");
		int port = ConfigureManager.getInstance().getInt(fullName + "Port");
		System.out.println(fullName + " udp channel:" + host + ":" + port);

		channelManager = new ChannelManager(port, loggerClient, new ManufacturerFEMessageProcesser());

		host = ConfigureManager.getInstance().getString(name + "SequencerHost");
		port = ConfigureManager.getInstance().getInt(name + "SequencerPort");
		channelManager.addChannel(new Channel(name, name + "Sequencer", host, port, Group.SEQUENCER));

		for(int i = 1; i <= 4; i++){
			host = ConfigureManager.getInstance().getString(name + "Replica" + i + "Host");
			port = ConfigureManager.getInstance().getInt(name + "Replica" + i + "Port");
			channelManager.addChannel(new ReplicaChannel(name, name + "Replica" + i, host, port, Group.REPLICA));

			host = ConfigureManager.getInstance().getString(name + "RM" + i + "Host");
			port = ConfigureManager.getInstance().getInt(name + "RM" + i + "Port");
			channelManager.addChannel(new Channel(name, name + "RM" + i, host, port, Group.RM));
		}
		
		channelManager.start();
	}

	@Override
	public boolean processPurchaseOrder(ItemList itemList, int sequencerID) {
		synchronized (lock) {
			if(sequencerID == currentSequencerID){
				if(cachedObj == null){
					return false;
				}else{
					return (Boolean)cachedObj;
				}
			}else if(currentSequencerID + 1 == sequencerID){
				currentSequencerID = sequencerID;
				resetReplicaChannel();
				Channel channel = channelManager.channelMap.get(name + "Sequencer");
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
						, channel.peerPort
						, new ManufacturerFEProcessPurchaseOrderMessage(channel.localProcessName
								, channel.localSeq
								, channel.peerPort
								, itemList));
				channel.isWaitingForRespose = true;
				
				ReplicaResponse replicaResponse = waitForReplicResponse();

				if(replicaResponse == null){
					return false;
				}else{
					reportReplicaResult(replicaResponse);
					return ((ManufacturerReplicaProcessPurchaseOrderMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).result;
				}
			}else{
				System.out.println("Bad sequencerID. currentSequencerID:" + currentSequencerID + ", Received sequencerID:" + sequencerID);
				return true;
			}
		}
	}

	@Override
	public Product getProductInfo(String aProdName, int sequencerID) {
		synchronized (lock) {
			if(sequencerID == currentSequencerID){
				if(cachedObj == null){
					return null;
				}else{
					return (Product)cachedObj;
				}
			}else if(currentSequencerID + 1 == sequencerID){
				currentSequencerID = sequencerID;
				resetReplicaChannel();
				Channel channel = channelManager.channelMap.get(name + "Sequencer");
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
						, channel.peerPort
						, new ManufacturerFEGetProductInfoMessage(channel.localProcessName
								, channel.localSeq
								, channel.peerPort
								, aProdName));
				channel.isWaitingForRespose = true;
				
				ReplicaResponse replicaResponse = waitForReplicResponse();

				if(replicaResponse == null){
					return null;
				}else{
					reportReplicaResult(replicaResponse);
					return ((ManufacturerReplicaGetProductInfoMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).product;
				}
			}else{
				System.out.println("Bad sequencerID. currentSequencerID:" + currentSequencerID + ", Received sequencerID:" + sequencerID);
				return null;
			}
		}
	}

	@Override
	public boolean receivePayment(String orderNum, float totalPrice,
			int sequencerID) {
		synchronized (lock) {
			if(sequencerID == currentSequencerID){
				if(cachedObj == null){
					return false;
				}else{
					return (Boolean) cachedObj;
				}
			}else if(currentSequencerID + 1 == sequencerID){
				currentSequencerID = sequencerID;
				resetReplicaChannel();
				Channel channel = channelManager.channelMap.get(name + "Sequencer");
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
						, channel.peerPort
						, new ManufacturerFEReceivePaymentMessage(channel.localProcessName
								, channel.localSeq
								, channel.peerPort
								, orderNum
								, totalPrice));
				channel.isWaitingForRespose = true;
				
				ReplicaResponse replicaResponse = waitForReplicResponse();

				if(replicaResponse == null){
					return false;
				}else{
					reportReplicaResult(replicaResponse);
					return ((ManufacturerReplicaReceivePaymentMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).result;
				}
			}else{
				System.out.println("Bad sequencerID. currentSequencerID:" + currentSequencerID + ", Received sequencerID:" + sequencerID);
				return false;
			}
		}
	}

	@Override
	public ProductList getProductList(int sequencerID) {
//		synchronized (lock) {
//			if(sequencerID == currentSequencerID){
//				if(cachedObj == null){
//					return null;
//				}else{
//					return (ProductList)cachedObj;
//				}
//			}else if(currentSequencerID + 1 == sequencerID){
//				currentSequencerID = sequencerID;
				resetReplicaChannel();
				Channel channel = channelManager.channelMap.get(name + "Sequencer");
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
						, channel.peerPort
						, new ManufacturerFEGetProductListMessage(channel.localProcessName
								, channel.localSeq
								, channel.peerPort));
				channel.isWaitingForRespose = true;
				
				ReplicaResponse replicaResponse = waitForReplicResponse();

				if(replicaResponse == null){
					return null;
				}else{
					reportReplicaResult(replicaResponse);
					return ((ManufacturerReplicaGetProductListMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).productList;
				}
//			}else{
//				System.out.println("Bad sequencerID. currentSequencerID:" + currentSequencerID + ", Received sequencerID:" + sequencerID);
//				return null;
//			}
//		}
	}
	
}
