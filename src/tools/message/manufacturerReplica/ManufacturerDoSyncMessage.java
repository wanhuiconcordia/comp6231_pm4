package tools.message.manufacturerReplica;

import tools.message.Action;
import tools.message.Message;

public class ManufacturerDoSyncMessage extends Message {

	public ManufacturerDoSyncMessage(String sender, int senderSeq,
			int receiverSeq) {
		super(sender, senderSeq, receiverSeq,  Action.doSync);
		// TODO Auto-generated constructor stub
	}

}
