package retailer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jws.WebService;

import jdk.internal.util.xml.impl.Pair;
import tools.ConfigureManager;
import tools.Customer;
import tools.Item;
import tools.ItemList;
import tools.ItemShippingStatus;
import tools.ItemShippingStatusList;
import tools.LoggerClient;
import tools.SignUpResult;
//import warehouse.WarehouseInterface;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.Message;
import tools.message.Packet;
import tools.message.ResultComparator;
import tools.message.retailerFE.RetailerFEGetCatelogMessage;
import tools.message.retailerFE.RetailerFESignInMessage;
import tools.message.retailerFE.RetailerFESignUpMessage;
import tools.message.retailerFE.RetailerFESubmitOrderMessage;
import tools.message.retailerReplica.RetailerReplicaSignUpReultMessage;

@WebService(endpointInterface = "retailer.RetailerInterface")
public class RetailerFEImpl implements RetailerInterface {
	public LoggerClient loggerClient;
	public String name;
	ChannelManager channelManager; 	
	/**
	 * Constructor
	 * @param name
	 */
	public RetailerFEImpl(String name, LoggerClient loggerClient) throws Exception{
		this.name = name;
		this.loggerClient = loggerClient;
		String host = ConfigureManager.getInstance().getString("RetailerFEHost");
		int port = ConfigureManager.getInstance().getInt("RetailerFEPort");
		System.out.println(name + " udp channel:" + host + ":" + port);
		
		ChannelManager channelManager = new ChannelManager(port, loggerClient, new RetailerFEMessageProcesser());
		
		host = ConfigureManager.getInstance().getString("RetailerSequencerHost");
		port = ConfigureManager.getInstance().getInt("RetailerSequencerPort");
		channelManager.addChannel(new Channel(name, "RetailerSequencer", host, port, Group.SEQUENCER));
		
		for(int i = 1; i <= 4; i++){
			host = ConfigureManager.getInstance().getString("RetailerReplica" + i + "Host");
			port = ConfigureManager.getInstance().getInt("RetailerReplica" + i + "Port");
			channelManager.addChannel(new Channel(name, "RetailerReplica" + i, host, port, Group.REPLICA));
			
			host = ConfigureManager.getInstance().getString("RetailerRM" + i + "Host");
			port = ConfigureManager.getInstance().getInt("RetailerRM" + i + "Port");
			channelManager.addChannel(new Channel(name, "RetailerRM1", host, port, Group.RM));
		}
		channelManager.start();
	}

	/* (non-Javadoc)
	 * @see retailer.RetailerInterface#getCatalog(int)
	 */
	@Override
	public synchronized ItemList getCatalog(int customerReferenceNumber) {
		System.out.println("getCatalog is called...");

		Channel channel = channelManager.channelMap.get("RetailerSequencer");
		channel.backupPacket = new Packet(channel.peerHost
				, channel.peerPort
				, new RetailerFEGetCatelogMessage(channel.localProcessName
						, channel.localSeq
						, channel.peerPort
						, customerReferenceNumber));
		channel.isWaitingForRespose = true;
		synchronized(channelManager.outgoingPacketQueueLock) {
			channelManager.outgoingPacketQueue.add(channel.backupPacket);
		}
		
		//TODO wait for replica message coming
		
		return null;
	}

	/* (non-Javadoc)
	 * @see retailer.RetailerInterface#submitOrder(int, tools.ItemList)
	 */
	@Override
	public synchronized ItemShippingStatusList submitOrder(int customerReferenceNumber,
			ItemList itemOrderList) {
		System.out.println("ItemShippingStatusList is called...");
		
		Channel channel = channelManager.channelMap.get("RetailerSequencer");
		channel.backupPacket = new Packet(channel.peerHost
				, channel.peerPort
				, new RetailerFESubmitOrderMessage(channel.localProcessName
						, channel.localSeq
						, channel.peerPort
						, customerReferenceNumber
						, itemOrderList));
		channel.isWaitingForRespose = true;
		synchronized(channelManager.outgoingPacketQueueLock) {
			channelManager.outgoingPacketQueue.add(channel.backupPacket);
		}
		//TODO wait for replica message coming
		return null;
	}

	/* (non-Javadoc)
	 * @see retailer.RetailerInterface#signUp(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SignUpResult signUp(String name, String password, String street1,
			String street2, String city, String state, String zip,
			String country) {
		System.out.println("signUp is called...");

		for(Channel replicaChannel: channelManager.channelMap.values()){
			replicaChannel.receivedMessage = null;
			replicaChannel.timeoutTimes = 0;
		}
		
		Channel channel = channelManager.channelMap.get("RetailerSequencer");
		channel.backupPacket = new Packet(channel.peerHost
				, channel.peerPort
				, new RetailerFESignUpMessage(channel.localProcessName
						, channel.localSeq
						, channel.peerPort
						, name
						, password
						, street1
						, street2
						, city
						, state
						, zip
						, country));
		channel.isWaitingForRespose = true;
		synchronized(channelManager.outgoingPacketQueueLock) {
			channelManager.outgoingPacketQueue.add(channel.backupPacket);
		}
		
		ReplicaResponse majorResponse = waitForReplicResponse();
		
		if(majorResponse == null){
			//BIG ERROR, WRITE TO LOGSERVER
			return null;
		}else{
			//TODO notifyRetailerRM();
			return ((RetailerReplicaSignUpReultMessage)(majorResponse.majorMsg)).signUpResult;
		}

	}

	/* (non-Javadoc)
	 * @see retailer.RetailerInterface#signIn(int, java.lang.String)
	 */
	@Override
	public Customer signIn(int customerReferenceNumber, String password) {
		System.out.println("signIn is called...");
		Channel channel = channelManager.channelMap.get("RetailerSequencer");
		channel.backupPacket = new Packet(channel.peerHost
				, channel.peerPort
				, new RetailerFESignInMessage(channel.localProcessName
						, channel.localSeq
						, channel.peerPort
						, customerReferenceNumber
						, password));
		channel.isWaitingForRespose = true;
		synchronized(channelManager.outgoingPacketQueueLock) {
			channelManager.outgoingPacketQueue.add(channel.backupPacket);
		}
		
		//TODO wait for replica message coming
		return null;
	}
	

	ReplicaResponse waitForReplicResponse(){
		ArrayList<Channel> waitingReplicaChannelList = new ArrayList<Channel>();
	
		for(Channel replicaChannel: channelManager.channelMap.values()){
			if(replicaChannel.group == Group.REPLICA){
				waitingReplicaChannelList.add(replicaChannel);
			}
		}

		ArrayList<Channel> answeredReplicaChannelList = new ArrayList<Channel>();

		ArrayList<Channel> crashReplicaChannelList = new ArrayList<Channel>();
		while(true){
			Channel replicaChannel;
			for(int i = 0; i < waitingReplicaChannelList.size();){
				replicaChannel = waitingReplicaChannelList.get(i);
				if(replicaChannel.receivedMessage == null){
					if(replicaChannel.timeoutTimes > 5){
						crashReplicaChannelList.add(replicaChannel);
						waitingReplicaChannelList.remove(i);
					}else{
						i++;
					}
				}else{
					answeredReplicaChannelList.add(replicaChannel);
					waitingReplicaChannelList.remove(i);
				}
			}
			if(waitingReplicaChannelList.isEmpty()){
				break;
			}else{
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		
		for(Channel answeredChannel: answeredReplicaChannelList){
			for(Map.Entry<Channel, Integer> channelCountPair: channelCountPairList){
				ResultComparator result1 = (ResultComparator) channelCountPair.getKey().receivedMessage;
				ResultComparator result2 = (ResultComparator) answeredChannel.receivedMessage;
				if(result1.hasSameResult(result2)){
					channelCountPairList.add(new Map.Entry<Channel, Integer>(answeredChannel, 1));
				}
			}
		}
		
		
		//TODO calculateMajority AND each replica status
		return null;
	}
}



class ReplicaResponse{
	ArrayList<String> goodReplicaList = new ArrayList<String>();
	ArrayList<String> failReplicaList = new ArrayList<String>();
	ArrayList<String> crashReplicaList = new ArrayList<String>();
	Message majorMsg;
}

