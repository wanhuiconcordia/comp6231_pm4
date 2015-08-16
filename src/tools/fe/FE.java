package tools.fe;

import java.util.ArrayList;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.ResultComparator;

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
//					System.out.println(channel.peerProcessName + " give message:" + channel.receivedMessage.toString());
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
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		if(waitingReplicaChannelList.size() >= noAnswerReplicaChannelList.size() * 3 + 1){
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
				return new ReplicaResponse(channelListList.get(majorIndex), failChannelList, noAnswerReplicaChannelList);
			}else{
				System.out.println("**************very bad. Betray the assumption of (3f + 1). Too many failed replica");
				return null;
			}
		}else{
			System.out.println("**************very bad. Betray the assumption of (3f + 1). Too many no answer replica.");
			return null;
		}
	}
}
