package tools.message.replica;

import tools.message.Action;
import tools.message.Message;

public class DoSyncMessage extends Message {
	public Object obj;
	public DoSyncMessage(String sender, int senderSeq, int receiverSeq,
			Object obj) {
		super(sender, senderSeq, receiverSeq, Action.doSync);

	}

}
