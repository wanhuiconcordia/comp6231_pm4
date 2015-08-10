package tools.message;


public class RetailerFEGetCatelogMessage extends Message {
	
	public RetailerFEGetCatelogMessage(String sender, int senderSeq, int receiverSeq){
		super(sender, senderSeq, receiverSeq, Action.getCatelog);
	}
}
