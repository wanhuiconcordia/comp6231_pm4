package tools.message.retailerFE;

import tools.message.Action;
import tools.message.Message;


public class RetailerFESignInMessage extends Message{
	public int customerReferenceNumber;
	public String password;
	public RetailerFESignInMessage(String sender, int senderSeq, int receiverSeq, int customerReferenceNumber, String password){
		super(sender, senderSeq, receiverSeq, Action.signIn);
		this.customerReferenceNumber = customerReferenceNumber;
		this.password = password;
	}
	
	public String toString(){
		return super.toString()
				+ ", " + customerReferenceNumber 
				+ ", " + password;
	}
}
