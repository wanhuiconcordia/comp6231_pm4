package tools.message;

public class RetailerSequencerGetCatelogMessage extends
		RetailerFEGetCatelogMessage {
	public int sequencerID;
	public RetailerSequencerGetCatelogMessage(String sender
			, int senderSeq
			, int receiverSeq
			, int sequencerID) {
		super(sender, senderSeq, receiverSeq);
		this.sequencerID = sequencerID; 
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}
}
