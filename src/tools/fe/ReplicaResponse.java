package tools.fe;

import java.util.ArrayList;
import tools.channel.Channel;

public class ReplicaResponse{
	public ArrayList<Channel> goodReplicaChannelList;
	public ArrayList<Channel> failReplicaChannelList;
	public ArrayList<Channel> noAnswerReplicaChannelList;
	public ReplicaResponse(ArrayList<Channel> goodReplicaChannelList
			, ArrayList<Channel> failReplicaChannelList
			, ArrayList<Channel> noAnswerReplicaChannelList){
		this.goodReplicaChannelList = goodReplicaChannelList;
		this.failReplicaChannelList = failReplicaChannelList;
		this.noAnswerReplicaChannelList = noAnswerReplicaChannelList;
	}
}