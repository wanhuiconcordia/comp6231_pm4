package tools.message.warehouse;

import tools.message.Action;
import tools.message.Message;

public class WarehouseFEGetProductsByRegisteredManufacturersMessage extends Message{
	
	public String manufacturerName;
	
	public WarehouseFEGetProductsByRegisteredManufacturersMessage(String sender, int senderSeq, int receiverSeq, String manufacturerName){
		super(sender, senderSeq, receiverSeq, Action.getProductsByRegisteredManufacturers);
		
		this.manufacturerName = manufacturerName;
	}

	public String toString(){
		return super.toString() 
				+ ", " + manufacturerName;
	}
}
