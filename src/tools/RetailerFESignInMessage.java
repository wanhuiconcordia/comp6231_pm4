package tools;

public class RetailerFESignInMessage extends Message{
	public int customerReferenceNumber;
	public String password;
	public RetailerFESignInMessage(String sender, int senderSeq, int receiverSeq, int customerReferenceNumber, String password){
		super(sender, senderSeq, receiverSeq, Action.getCatelog);
		this.customerReferenceNumber = customerReferenceNumber;
		this.password = password;
	}
	
	public String toString(){
		return super.toString() + ", " + customerReferenceNumber + ", " + password;
	}
}
