package tools.message;

public class RetailerSequencerSignUpMessage extends RetailerFESignUpMessage {
	public int sequencerID;
	public RetailerSequencerSignUpMessage(String sender
			, int senderSeq
			, int receiverSeq
			, String name
			, String password
			, String street1
			, String street2
			, String city
			, String state
			, String zip
			, String country
			, int sequencerID) {
		super(sender, senderSeq, receiverSeq, name, password, street1, street2, city,
				state, zip, country);
		this.sequencerID= sequencerID; 
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}

}
