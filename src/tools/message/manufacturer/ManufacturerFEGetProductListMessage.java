package tools.message.manufacturer;

import tools.message.Action;
import tools.message.Message;

public class ManufacturerFEGetProductListMessage extends Message{

	public ManufacturerFEGetProductListMessage(String sender, int senderSeq, int receiverSeq){
		
		super(sender, senderSeq, receiverSeq, Action.getProductList);
	}
}
