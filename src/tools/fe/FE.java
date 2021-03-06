package tools.fe;

import java.util.ArrayList;

import rm.ReplicaStatus;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.channel.ReplicaChannel;
import tools.message.Action;
import tools.message.Packet;
import tools.message.ReplicaResultMessage;
import tools.message.ResultComparator;
import tools.message.replica.ReplicaCrushMessage;

public class FE {
	public ChannelManager channelManager;
	
	public void resetReplicaChannel(){
		for(Channel replicaChannel: channelManager.channelMap.values()){
			if(replicaChannel.group == Group.REPLICA){
				replicaChannel.receivedMessage = null;
				replicaChannel.timeoutTimes = 0;
			}
		}
	}
	
	public ReplicaResponse waitForReplicResponse(){
		ArrayList<ReplicaChannel> waitingReplicaChannelList = new ArrayList<ReplicaChannel>();
		
		for(Channel replicaChannel: channelManager.channelMap.values()){
			if(replicaChannel.group == Group.REPLICA){
				waitingReplicaChannelList.add((ReplicaChannel)replicaChannel);
			}
		}
		
		int totalWaitingChannelCount = waitingReplicaChannelList.size();

		ArrayList<ReplicaChannel> answeredReplicaChannelList = new ArrayList<ReplicaChannel>();
		ArrayList<Channel> crushedReplicaChannelList = new ArrayList<Channel>();

		int interval = 50;
		while(true){
			for(ReplicaChannel channel: waitingReplicaChannelList){
				if(channel.replicaStatus == ReplicaStatus.REPLICA_CRUSH){
					crushedReplicaChannelList.add(channel);
					waitingReplicaChannelList.remove(channel);
				}else if(channel.receivedMessage != null){
					answeredReplicaChannelList.add(channel);
					waitingReplicaChannelList.remove(channel);
					break;
				}
			}
			
			if(waitingReplicaChannelList.size() == 0){
				break;
			}else{
				try {
					Thread.sleep(interval);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		if(totalWaitingChannelCount >= crushedReplicaChannelList.size() * 3 + 1){
			int majorIndex = -1;
			int maxCount = 0;
			ArrayList<ArrayList<Channel>> channelListList = new ArrayList<ArrayList<Channel>>();
			
			for(Channel answeredChannel: answeredReplicaChannelList){
				boolean foundSame = false;
				for(int i = 0; i < channelListList.size(); i++){
					ArrayList<Channel> channelList = channelListList.get(i);
					try{
						if(((ResultComparator)answeredChannel.receivedMessage).hasSameResult(((ResultComparator)channelList.get(0).receivedMessage))){
							channelList.add(answeredChannel);
							foundSame = true;
							if(channelList.size() > maxCount){
								maxCount = channelList.size();
								majorIndex = i;
							}
							break;
						}
					}catch(ClassCastException e){
						System.out.println("**************very bad. Received different Message type.");
						return null;
					}
				}
				if(!foundSame){
					ArrayList<Channel> tmpList = new ArrayList<Channel>();
					tmpList.add(answeredChannel);
					channelListList.add(tmpList);
					if(maxCount < 1){
						maxCount = 1;
						majorIndex = 0;
					}
				}
			}
			
			if(maxCount * 2 > answeredReplicaChannelList.size()){
				ArrayList<Channel> failChannelList = new ArrayList<Channel>();
				for(int i = 0; i < channelListList.size(); i++){
					if(i != majorIndex){
						for(Channel channel: channelListList.get(i)){
							failChannelList.add(channel);
						}
					}
				}
				return new ReplicaResponse(channelListList.get(majorIndex), failChannelList, crushedReplicaChannelList);
			}else{
				System.out.println("**************very bad. Betray the assumption of (3f + 1). Too many failed replica");
				return null;
			}
		}else{
			System.out.println("**************very bad. Betray the assumption of (3f + 1). Too many no answer replica.");
			return null;
		}
	}
	
	public void reportReplicaResult(ReplicaResponse replicaResponse){
		String goodChannelProcessName = replicaResponse.goodReplicaChannelList.get(0).peerProcessName;
		
		for(Channel channel: replicaResponse.goodReplicaChannelList){
			String rm_processName = channel.peerProcessName.replaceAll("Replica", "RM");
			if(channelManager.channelMap.containsKey(rm_processName)){
				Channel rmChannel = channelManager.channelMap.get(rm_processName);
				ReplicaResultMessage msg = new ReplicaResultMessage(rmChannel.localProcessName
						, ++rmChannel.localSeq
						, rmChannel.peerSeq
						, ReplicaStatus.REPLICA_GOOD
						, goodChannelProcessName);
				
				rmChannel.backupPacket = new Packet(rmChannel.peerProcessName, rmChannel.peerHost
						, rmChannel.peerPort
						, msg);
				rmChannel.isWaitingForRespose = true;
			}
		}
		
		for(Channel channel: replicaResponse.failReplicaChannelList){
			String rm_processName = channel.peerProcessName.replaceAll("Replica", "RM");
			if(channelManager.channelMap.containsKey(rm_processName)){
				Channel rmChannel = channelManager.channelMap.get(rm_processName);
				ReplicaResultMessage msg = new ReplicaResultMessage(rmChannel.localProcessName
						, ++rmChannel.localSeq
						, rmChannel.peerSeq
						, ReplicaStatus.REPLICA_FAIL
						, goodChannelProcessName);
				
				rmChannel.backupPacket = new Packet(rmChannel.peerProcessName, rmChannel.peerHost
						, rmChannel.peerPort
						, msg);
				rmChannel.isWaitingForRespose = true;
			}
		}
		
		for(Channel channel: replicaResponse.noAnswerReplicaChannelList){
			String rm_processName = channel.peerProcessName.replaceAll("Replica", "RM");
			if(channelManager.channelMap.containsKey(rm_processName)){
				Channel rmChannel = channelManager.channelMap.get(rm_processName);
				ReplicaResultMessage msg = new ReplicaResultMessage(rmChannel.localProcessName
						, ++rmChannel.localSeq
						, rmChannel.peerSeq
						, ReplicaStatus.REPLICA_CRUSH
						, goodChannelProcessName);
				
				rmChannel.backupPacket = new Packet(rmChannel.peerProcessName, rmChannel.peerHost
						, rmChannel.peerPort
						, msg);
				rmChannel.isWaitingForRespose = true;
			}
		}
	}
}
