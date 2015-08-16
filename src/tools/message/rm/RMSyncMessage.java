package tools.message.rm;

import tools.message.Action;
import tools.message.Message;

public class RMSyncMessage extends Message {
	public int goodReplicaIndex;
	public RMSyncMessage(String sender, int senderSeq, int receiverSeq, int goodReplicaIndex) {
		super(sender, senderSeq, receiverSeq, Action.sync);
		this.goodReplicaIndex = goodReplicaIndex;
	}
	
	public String toString(){
		return super.toString() + ", " + goodReplicaIndex;
	}
}
