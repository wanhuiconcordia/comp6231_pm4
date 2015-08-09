package tools;
public class RetailerFEGetCatelogMessage extends Message {
	
	public RetailerFEGetCatelogMessage(String sender, int senderSeq, int receiverSeq){
		super(sender, senderSeq, receiverSeq, Action.getCatelog, true);
	}
	
	public String toString(){
		return sender + ", " + senderSeq + ", " + receiverSeq + ", " + action + ", " + needAck;  
	}
}
