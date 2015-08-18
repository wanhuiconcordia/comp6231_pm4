package tools.message.replica;

import tools.message.Action;
import tools.message.Message;

public class ReplicaCrushMessage extends Message {
	public String replicaName;
	public ReplicaCrushMessage(String sender, int senderSeq, int receiverSeq,
			String replicaName) {
		super(sender, senderSeq, receiverSeq, Action.REPLICA_CRUSH);
	}
	
	public String toString(){
		return super.toString() + ", " + replicaName;
	}

}
