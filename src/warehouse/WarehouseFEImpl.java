package warehouse;

import javax.jws.WebService;

import tools.ConfigureManager;
import tools.ItemList;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.channel.ReplicaChannel;
import tools.fe.FE;
import tools.fe.ReplicaResponse;
import tools.message.Packet;
import tools.message.warehouseFE.WarehouseFEGetProductsByIDMessage;
import tools.message.warehouseFE.WarehouseFEGetProductsByRegisteredManufacturersMessage;
import tools.message.warehouseFE.WarehouseFEGetProductsByTypeMessage;
import tools.message.warehouseFE.WarehouseFEShippingGoodsMessage;
import tools.message.warehouseReplica.WarehouseReplicaGetProductsByIDMessage;
import tools.message.warehouseReplica.WarehouseReplicaGetProductsByRegisteredManufacturersMessage;
import tools.message.warehouseReplica.WarehouseReplicaGetProductsByTypeMessage;
import tools.message.warehouseReplica.WarehouseReplicaShippingGoodsMessage;

@WebService(endpointInterface = "warehouse.WarehouseInterface")
public class WarehouseFEImpl extends FE implements WarehouseInterface {
	public LoggerClient loggerClient;
	public String name;
	public int currentSequencerID = 0;
	public Object cachedObj;
	public Object lock = new Object();
	/**
	 * Constructor
	 * @param name
	 */
	public WarehouseFEImpl(String name, LoggerClient loggerClient) throws Exception{
		this.name = name;
		String fullName = name + "FE";
		this.loggerClient = loggerClient;
		String host = ConfigureManager.getInstance().getString(fullName + "Host");
		int port = ConfigureManager.getInstance().getInt(fullName + "Port");
		System.out.println(fullName + " udp channel:" + host + ":" + port);

		channelManager = new ChannelManager(port, loggerClient, new WarehouseFEMessageProcesser());

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
	public ItemList getProductsByID(String productID, int sequencerID) {
		synchronized (lock) {
			if(sequencerID == currentSequencerID){
				if(cachedObj == null){
					return null;
				}else{
					return (ItemList)cachedObj;
				}
			}else if(currentSequencerID + 1 == sequencerID){
				currentSequencerID = sequencerID;
				resetReplicaChannel();
				Channel channel = channelManager.channelMap.get(name + "Sequencer");
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
						, channel.peerPort
						, new WarehouseFEGetProductsByIDMessage(channel.localProcessName
								, channel.localSeq
								, channel.peerPort
								, productID));
				channel.isWaitingForRespose = true;
				
				ReplicaResponse replicaResponse = waitForReplicResponse();

				if(replicaResponse == null){
					return null;
				}else{
					reportReplicaResult(replicaResponse);
					return ((WarehouseReplicaGetProductsByIDMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).itemList;
				}
			}else{
				System.out.println("Bad sequencerID. currentSequencerID:" + currentSequencerID + ", Received sequencerID:" + sequencerID);
				return null;
			}
		}
	}
	@Override
	public ItemList getProductsByType(String productType, int sequencerID) {
		synchronized (lock) {
			if(sequencerID == currentSequencerID){
				if(cachedObj == null){
					return null;
				}else{
					return (ItemList)cachedObj;
				}
			}else if(currentSequencerID + 1 == sequencerID){
				currentSequencerID = sequencerID;
				resetReplicaChannel();
				Channel channel = channelManager.channelMap.get(name + "Sequencer");
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
						, channel.peerPort
						, new WarehouseFEGetProductsByTypeMessage(channel.localProcessName
								, channel.localSeq
								, channel.peerPort
								, productType));
				channel.isWaitingForRespose = true;
				
				ReplicaResponse replicaResponse = waitForReplicResponse();

				if(replicaResponse == null){
					return null;
				}else{
					reportReplicaResult(replicaResponse);
					return ((WarehouseReplicaGetProductsByTypeMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).itemList;
				}
			}else{
				System.out.println("Bad sequencerID. currentSequencerID:" + currentSequencerID + ", Received sequencerID:" + sequencerID);
				return null;
			}
		}
	}
	@Override
	public ItemList getProductsByRegisteredManufacturers(
			String manufacturerName, int sequencerID) {
		synchronized (lock) {
			if(sequencerID == currentSequencerID){
				if(cachedObj == null){
					return null;
				}else{
					return (ItemList)cachedObj;
				}
			}else if(currentSequencerID + 1 == sequencerID){
				currentSequencerID = sequencerID;
				resetReplicaChannel();
				Channel channel = channelManager.channelMap.get(name + "Sequencer");
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
						, channel.peerPort
						, new WarehouseFEGetProductsByRegisteredManufacturersMessage(channel.localProcessName
								, channel.localSeq
								, channel.peerPort
								, manufacturerName));
				channel.isWaitingForRespose = true;
				
				ReplicaResponse replicaResponse = waitForReplicResponse();

				if(replicaResponse == null){
					return null;
				}else{
					reportReplicaResult(replicaResponse);
					return ((WarehouseReplicaGetProductsByRegisteredManufacturersMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).itemList;
				}
			}else{
				System.out.println("Bad sequencerID. currentSequencerID:" + currentSequencerID + ", Received sequencerID:" + sequencerID);
				return null;
			}
		}
	}

	@Override
	public ItemList shippingGoods(ItemList itemList,
			int sequencerID) {
		synchronized (lock) {
			if(sequencerID == currentSequencerID){
				if(cachedObj == null){
					return null;
				}else{
					return (ItemList)cachedObj;
				}
			}else if(currentSequencerID + 1 == sequencerID){
				currentSequencerID = sequencerID;
				resetReplicaChannel();
				Channel channel = channelManager.channelMap.get(name + "Sequencer");
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
						, channel.peerPort
						, new WarehouseFEShippingGoodsMessage(channel.localProcessName
								, channel.localSeq
								, channel.peerPort
								, itemList));
				channel.isWaitingForRespose = true;
				
				ReplicaResponse replicaResponse = waitForReplicResponse();

				if(replicaResponse == null){
					return null;
				}else{
					reportReplicaResult(replicaResponse);
					return ((WarehouseReplicaShippingGoodsMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).itemList;
				}
			}else{
				System.out.println("Bad sequencerID. currentSequencerID:" + currentSequencerID + ", Received sequencerID:" + sequencerID);
				return null;
			}
		}
	}

}