package tools.message.manufacturerFE;

import tools.message.Action;
import tools.message.Message;

public class ManufacturerFEGetProductInfoMessage extends Message{
	public String aProdName;
	public ManufacturerFEGetProductInfoMessage(String sender, int senderSeq, int receiverSeq, String aProdName){
		
		super(sender,senderSeq,receiverSeq, Action.getProductInfo);
		this.aProdName = aProdName;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + aProdName;
	}

}
