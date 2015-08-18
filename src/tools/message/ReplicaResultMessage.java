package tools.message;
import rm.ReplicaStatus;

public class ReplicaResultMessage extends Message {
	public ReplicaStatus replicaStatus;
	public String goodReplicaName;
	public ReplicaResultMessage(String sender
			, int senderSeq
			, int receiverSeq
			, ReplicaStatus replicaStatus
			, String goodReplicaName
			) {
		super(sender, senderSeq, receiverSeq, Action.REPLICA_RESULT);
		this.replicaStatus = replicaStatus;
		this.goodReplicaName = goodReplicaName;
	}
	public String toString(){
		return super.toString() + ", " + replicaStatus + ", " + goodReplicaName;
	}
}