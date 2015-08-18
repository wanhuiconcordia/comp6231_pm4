package tools.message.replica;

import tools.message.Action;
import tools.message.Message;

public class AskInitSync extends Message {

	public AskInitSync(String sender, int senderSeq, int receiverSeq) {
		super(sender, senderSeq, receiverSeq, Action.askInitSync);
	}

}
