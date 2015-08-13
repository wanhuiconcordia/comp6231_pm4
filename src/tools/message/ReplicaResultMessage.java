package tools.message;

import java.util.ArrayList;

public class ReplicaResultMessage extends Message {
	public ArrayList<String> goodReplicaList;
	public boolean result;
	public ReplicaResultMessage(String sender
			, int senderSeq
			, int receiverSeq
			, boolean result
			, ArrayList<String> goodReplicaList) {
		super(sender, senderSeq, receiverSeq, Action.REPLICA_RESULT);
		this.result = result;
		this.goodReplicaList = goodReplicaList;
	}
	public String toString(){
		String returnVal = super.toString();
		for(String processName: goodReplicaList){
			returnVal = returnVal + ", " + processName;
		}
		return returnVal;
	}
}
