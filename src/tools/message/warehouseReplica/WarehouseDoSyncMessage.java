package tools.message.warehouseReplica;

import tools.message.Action;
import tools.message.Message;

public class WarehouseDoSyncMessage extends Message {

	public WarehouseDoSyncMessage(String sender, int senderSeq,
			int receiverSeq) {
		super(sender, senderSeq, receiverSeq, Action.doSync);
	}

}
