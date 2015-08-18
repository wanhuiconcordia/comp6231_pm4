package retailer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jws.WebService;

import rm.ReplicaStatus;
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
import tools.channel.ReplicaChannel;
import tools.fe.FE;
import tools.fe.ReplicaResponse;
import tools.message.Action;
import tools.message.Message;
import tools.message.Packet;
import tools.message.ReplicaResultMessage;
import tools.message.ResultComparator;
import tools.message.retailerFE.RetailerFEGetCatelogMessage;
import tools.message.retailerFE.RetailerFESignInMessage;
import tools.message.retailerFE.RetailerFESignUpMessage;
import tools.message.retailerFE.RetailerFESubmitOrderMessage;
import tools.message.retailerReplica.RetailerReplicaGetCatalogResultMessage;
import tools.message.retailerReplica.RetailerReplicaSignInResultMessage;
import tools.message.retailerReplica.RetailerReplicaSignUpReultMessage;
import tools.message.retailerReplica.RetailerReplicaSubmitOrderMessage;

@WebService(endpointInterface = "retailer.RetailerInterface")
public class RetailerFEImpl extends FE implements RetailerInterface {
	public LoggerClient loggerClient;
	public String name;
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

		channelManager = new ChannelManager(port, loggerClient, new RetailerFEMessageProcesser());

		host = ConfigureManager.getInstance().getString("RetailerSequencerHost");
		port = ConfigureManager.getInstance().getInt("RetailerSequencerPort");
		channelManager.addChannel(new Channel(name, "RetailerSequencer", host, port, Group.SEQUENCER));

		for(int i = 1; i <= 4; i++){
			host = ConfigureManager.getInstance().getString("RetailerReplica" + i + "Host");
			port = ConfigureManager.getInstance().getInt("RetailerReplica" + i + "Port");
			channelManager.addChannel(new ReplicaChannel(name, "RetailerReplica" + i, host, port, Group.REPLICA));

			host = ConfigureManager.getInstance().getString("RetailerRM" + i + "Host");
			port = ConfigureManager.getInstance().getInt("RetailerRM" + i + "Port");
			channelManager.addChannel(new Channel(name, "RetailerRM" + i, host, port, Group.RM));
		}
		
		channelManager.start();
	}

	@Override
	public synchronized ItemList getCatalog(int customerReferenceNumber) {
		System.out.println("getCatalog is called...");
		resetReplicaChannel();
		Channel channel = channelManager.channelMap.get("RetailerSequencer");
		channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
				, channel.peerPort
				, new RetailerFEGetCatelogMessage(channel.localProcessName
						, channel.localSeq
						, channel.peerPort
						, customerReferenceNumber));
		channel.isWaitingForRespose = true;
		
		ReplicaResponse replicaResponse = waitForReplicResponse();

		if(replicaResponse == null){
			return null;
		}else{
			reportReplicaResult(replicaResponse);
			return ((RetailerReplicaGetCatalogResultMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).itemList;
		}
	}

	@Override
	public synchronized ItemShippingStatusList submitOrder(int customerReferenceNumber,
			ItemList itemOrderList) {
		System.out.println("ItemShippingStatusList is called...");
		resetReplicaChannel();
		Channel channel = channelManager.channelMap.get("RetailerSequencer");
		channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
				, channel.peerPort
				, new RetailerFESubmitOrderMessage(channel.localProcessName
						, channel.localSeq
						, channel.peerPort
						, customerReferenceNumber
						, itemOrderList));
		channel.isWaitingForRespose = true;
		
		ReplicaResponse replicaResponse = waitForReplicResponse();

		if(replicaResponse == null){
			return null;
		}else{
			reportReplicaResult(replicaResponse);
			return ((RetailerReplicaSubmitOrderMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).itemShippingStatusList;
		}
	}

	@Override
	public SignUpResult signUp(String name, String password, String street1,
			String street2, String city, String state, String zip,
			String country) {
		System.out.println("signUp is called...");
		resetReplicaChannel();
		Channel channel = channelManager.channelMap.get("RetailerSequencer");
		channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
				, channel.peerPort
				, new RetailerFESignUpMessage(channel.localProcessName
						, ++channel.localSeq
						, channel.peerSeq
						, name
						, password
						, street1
						, street2
						, city
						, state
						, zip
						, country));
		channel.isWaitingForRespose = true;

		ReplicaResponse replicaResponse = waitForReplicResponse();

		if(replicaResponse == null){
			return null;
		}else{
			reportReplicaResult(replicaResponse);
			return ((RetailerReplicaSignUpReultMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).signUpResult;
		}
	}

	@Override
	public Customer signIn(int customerReferenceNumber, String password) {
		resetReplicaChannel();
		Channel channel = channelManager.channelMap.get("RetailerSequencer");
		channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
				, channel.peerPort
				, new RetailerFESignInMessage(channel.localProcessName
						, ++channel.localSeq
						, channel.peerSeq
						, customerReferenceNumber
						, password));
		channel.isWaitingForRespose = true;
		
		ReplicaResponse replicaResponse = waitForReplicResponse();

		if(replicaResponse == null){
			return null;
		}else{
			reportReplicaResult(replicaResponse);
			return ((RetailerReplicaSignInResultMessage)(replicaResponse.goodReplicaChannelList.get(0).receivedMessage)).customer;
		}
	}
}





