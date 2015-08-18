package tools.message.warehouseFE;

import tools.message.Action;
import tools.message.Message;

public class WarehouseFEGetProductsMessage extends Message{
	
	public String productID;
	
	public WarehouseFEGetProductsMessage(String sender, int senderSeq, int receiverSeq, String productID){
		super(sender, senderSeq, receiverSeq, Action.getProducts);
		this.productID = productID;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + productID;
	}

}
