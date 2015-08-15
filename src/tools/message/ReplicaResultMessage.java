package tools.message;
import java.util.ArrayList;

public class ReplicaResultMessage extends Message {
	public ArrayList<String> goodReplicaList;
	public ArrayList<String> failedReplicaList;
	public ArrayList<String> noAnswerReplicaList;
	public ReplicaResultMessage(String sender
			, int senderSeq
			, int receiverSeq
			, ArrayList<String> goodReplicaList
			, ArrayList<String> failedReplicaList
			, ArrayList<String> noAnswerReplicaList) {
		super(sender, senderSeq, receiverSeq, Action.REPLICA_RESULT);
		this.goodReplicaList = goodReplicaList;
		this.failedReplicaList = failedReplicaList;
		this.noAnswerReplicaList = noAnswerReplicaList;
	}
	public String toString(){
		String returnVal = super.toString();
		for(String processName: goodReplicaList){
			returnVal = returnVal + ", " + processName;
		}
		
		for(String processName: failedReplicaList){
			returnVal = returnVal + ", " + processName;
		}
		
		for(String processName: noAnswerReplicaList){
			returnVal = returnVal + ", " + processName;
		}
		return returnVal;
	}
}