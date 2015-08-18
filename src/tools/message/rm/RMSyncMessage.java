package tools.message.rm;

import tools.message.Action;
import tools.message.Message;

public class RMSyncMessage extends Message {
	public String goodReplicaName;
	public RMSyncMessage(String sender, int senderSeq, int receiverSeq, String goodReplicaName) {
		super(sender, senderSeq, receiverSeq, Action.sync);
		this.goodReplicaName = goodReplicaName;
	}
	
	public String toString(){
		return super.toString() + ", " + goodReplicaName;
	}
}
