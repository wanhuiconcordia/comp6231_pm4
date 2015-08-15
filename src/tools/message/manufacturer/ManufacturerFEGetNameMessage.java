package tools.message.manufacturer;

import tools.message.Action;
import tools.message.Message;

public class ManufacturerFEGetNameMessage extends Message{
	
	public ManufacturerFEGetNameMessage(String sender, int senderSeq, int receiverSeq){
		super(sender, senderSeq, receiverSeq, Action.getName);
	}

}
