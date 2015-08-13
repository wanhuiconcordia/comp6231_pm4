package tools.message;


public class RetailerFEGetCatelogMessage extends Message {
	public int customerReferenceNumber;
	public RetailerFEGetCatelogMessage(String sender, int senderSeq, int receiverSeq, int customerReferenceNumber){
		super(sender, senderSeq, receiverSeq, Action.getCatelog);
		this.customerReferenceNumber = customerReferenceNumber;
	}
}
