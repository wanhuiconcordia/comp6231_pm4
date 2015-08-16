package tools.message.warehouse;

import tools.message.Action;
import tools.message.Message;

public class WarehouseFEGetProductsByTypeMessage extends Message{

	public String productType;
	
	public WarehouseFEGetProductsByTypeMessage(String sender, int senderSeq, int receiverSeq, String productType){
		super(sender, senderSeq, receiverSeq, Action.getProductsByType);
		this.productType = productType;
	}

	public String toString(){
		return super.toString() 
				+ ", " + productType;
	}
}
