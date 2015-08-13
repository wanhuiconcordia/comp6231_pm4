package tools.message;

public class RetailerSequencerGetCatelogMessage extends
		RetailerFEGetCatelogMessage {
	public int sequencerID;
	public RetailerSequencerGetCatelogMessage(String sender
			, int senderSeq
			, int receiverSeq
			, int customerReferenceNumber
			, int sequencerID) {
		super(sender, senderSeq, receiverSeq, customerReferenceNumber);
		this.sequencerID = sequencerID; 
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}
}
