package tools.message.warehouseFE;

import tools.message.Action;
import tools.message.Message;

public class WarehouseFEGetProductsByIDMessage extends Message{
	public String productID;
	public WarehouseFEGetProductsByIDMessage(String sender, int senderSeq, int receiverSeq, String productID){
		super(sender, senderSeq, receiverSeq, Action.getProductsByID);
		this.productID = productID;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + productID;
	}

}
