package tools.message;
import rm.ReplicaStatus;

public class ReplicaResultMessage extends Message {
	public ReplicaStatus replicaStatus;
	public int goodReplicaIndex;
	public ReplicaResultMessage(String sender
			, int senderSeq
			, int receiverSeq
			, ReplicaStatus replicaStatus
			, int goodReplicaIndex
			) {
		super(sender, senderSeq, receiverSeq, Action.REPLICA_RESULT);
		this.replicaStatus = replicaStatus;
		this.goodReplicaIndex = goodReplicaIndex;
	}
	public String toString(){
		return super.toString() + ", " + replicaStatus + ", " + goodReplicaIndex;
	}
}