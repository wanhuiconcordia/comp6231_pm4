package tools.message.retailerSequencer;

import tools.message.retailerFE.RetailerFESignInMessage;

public class RetailerSequencerSignInMessage extends RetailerFESignInMessage{
	public int sequencerID;
	public RetailerSequencerSignInMessage(String sender
			, int senderSeq
			, int receiverSeq
			, int customerReferenceNumber
			, String password
			, int sequencerID){
		super(sender, senderSeq, receiverSeq, customerReferenceNumber, password);
		this.sequencerID = sequencerID; 
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}
}
