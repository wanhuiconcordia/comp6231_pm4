package tools.message.retailerFE;

import tools.message.Action;
import tools.message.Message;

public class RetailerFESignUpMessage extends Message {
	public String name;
	public String password;
	public String street1;
	public String street2;
	public String city;
	public String state;
	public String zip;
	public String country;
	
	public RetailerFESignUpMessage(String sender
			, int senderSeq
			, int receiverSeq
			, String name
			, String password
			, String street1
			, String street2
			, String city
			, String state
			, String zip
			, String country) {
		super(sender, senderSeq, receiverSeq, Action.signUp);
		this.name = name;
		this.password = password;
		this.street1 = street1;
		this.street2 = street2;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + name
				+ ", " + password
				+ ", " + street1
				+ ", " + street2
				+ ", " + city
				+ ", " + state
				+ ", " + zip
				+ ", " + country;
		}
}
