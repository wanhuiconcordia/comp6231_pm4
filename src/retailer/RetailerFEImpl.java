package retailer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jws.WebService;

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
import tools.message.Action;
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

		channelManager = new ChannelManager(port, loggerClient, new RetailerFEMessageProcesser());

		host = ConfigureManager.getInstance().getString("RetailerSequencerHost");
		port = ConfigureManager.getInstance().getInt("RetailerSequencerPort");
		channelManager.addChannel(new Channel(name, "RetailerSequencer", host, port, Group.SEQUENCER));

		for(int i = 1; i <= 4; i++){
			host = ConfigureManager.getInstance().getString("RetailerReplica" + i + "Host");
			port = ConfigureManager.getInstance().getInt("RetailerReplica" + i + "Port");
			channelManager.addChannel(new Channel(name, "RetailerReplica" + i, host, port, Group.REPLICA));

			host = ConfigureManager.getInstance().getString("RetailerRM" + i + "Host");
			port = ConfigureManager.getInstance().getInt("RetailerRM" + i + "Port");
			channelManager.addChannel(new Channel(name, "RetailerRM" + i, host, port, Group.RM));
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

		ArrayList<RetailerReplicaSignUpReultMessage> retailerReplicaSignUpReultMessageList = new ArrayList<RetailerReplicaSignUpReultMessage>();  
		for(Channel answeredChannel: replicaResponse.answeredReplicaChannelList){
			if(answeredChannel.receivedMessage.action == Action.signUp){
				retailerReplicaSignUpReultMessageList.add((RetailerReplicaSignUpReultMessage) answeredChannel.receivedMessage);
			}else{
				System.out.println("Received a different message. Should never happen. This replica is failed.");
				//TODO
			}
		}

		ArrayList<ArrayList<RetailerReplicaSignUpReultMessage>> messageGroup = new ArrayList<ArrayList<RetailerReplicaSignUpReultMessage>> ();
		for(RetailerReplicaSignUpReultMessage retailerReplicaSignUpReultMessage :retailerReplicaSignUpReultMessageList){
			boolean found = false;
			for(ArrayList<RetailerReplicaSignUpReultMessage> tmpMsgList: messageGroup){ 
				if(retailerReplicaSignUpReultMessage.hasSameResult(tmpMsgList.get(0))){
					tmpMsgList.add(retailerReplicaSignUpReultMessage);
					found = true;
					break;
				}
			}
			if(! found){
				ArrayList<RetailerReplicaSignUpReultMessage> tmpMsgList = new ArrayList<RetailerReplicaSignUpReultMessage>();
				tmpMsgList.add(retailerReplicaSignUpReultMessage);						
				messageGroup.add(tmpMsgList);
			}
		}


		ArrayList<String> noAnswerProcessList = new ArrayList<String>();
		for(Channel noAnswerChannel: replicaResponse.noAnswerReplicaChannelList){
			noAnswerProcessList.add(noAnswerChannel.peerProcessName);
			System.out.println("No response process:" + noAnswerChannel.peerProcessName);
		}

		int max = 0;
		int index = -1;
		for(int i = 0; i < messageGroup.size(); i++){
			if(messageGroup.get(i).size() > max){
				max = messageGroup.get(i).size();
				index = i;
			}
		}

		ArrayList<String> goodProcessList = new ArrayList<String>();
		ArrayList<String> failedProcessList = new ArrayList<String>();
		for(int i = 0; i < messageGroup.size(); i++){
			for(RetailerReplicaSignUpReultMessage tmpMsg: messageGroup.get(i)){
				if(i == index){
					goodProcessList.add(tmpMsg.sender);
					System.out.println("Good process:" + tmpMsg.sender);
				}else{
					failedProcessList.add(tmpMsg.sender);
					System.out.println("Failed process:" + tmpMsg.sender);
				}
			}
		}

		for(int i = 0; i < messageGroup.size(); i++){
			if(messageGroup.get(i).size() > max){
				max = messageGroup.get(i).size();
				index = i;
			}
		}

		reportRM(goodProcessList, failedProcessList, noAnswerProcessList);
		return messageGroup.get(index).get(0).signUpResult;
	}

	void reportRM(ArrayList<String> goodProcessList, ArrayList<String> failedProcessList, ArrayList<String> noAnswerProcessList){

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
						, ++channel.localSeq
						, channel.peerSeq
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
		ArrayList<Channel> noAnswerReplicaChannelList = new ArrayList<Channel>();

		int timeCount = 60;
		int interval = 50;
		while(true){
			for(Channel channel: waitingReplicaChannelList){
				if(channel.receivedMessage != null){
					System.out.println(channel.peerProcessName + " give message:" + channel.receivedMessage.toString());
					answeredReplicaChannelList.add(channel);
					waitingReplicaChannelList.remove(channel);
					break;
				}
			}
			
			if(waitingReplicaChannelList.size() == 0){
				break;
			}
			try {
				Thread.sleep(interval);
				timeCount--;
				if(timeCount <= 0){
					for(Channel channel: waitingReplicaChannelList){
						noAnswerReplicaChannelList.add(channel);
						System.out.println(channel.peerProcessName + " time out...");
					}
					System.out.println("time out. do not wait.");
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return new ReplicaResponse(noAnswerReplicaChannelList, answeredReplicaChannelList);
	}
}



class ReplicaResponse{
	public ArrayList<Channel> noAnswerReplicaChannelList = new ArrayList<Channel>();
	public ArrayList<Channel> answeredReplicaChannelList = new ArrayList<Channel>();
	public ReplicaResponse(ArrayList<Channel> noAnswerReplicaChannelList
			, ArrayList<Channel> answeredReplicaChannelList){
		this.noAnswerReplicaChannelList = noAnswerReplicaChannelList;
		this.answeredReplicaChannelList = answeredReplicaChannelList;
	}
}

