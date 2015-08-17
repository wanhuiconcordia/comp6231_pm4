package tools.message.manufacturerReplica;

import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class ManufacturerReplicaReceivePaymentMessage extends Message implements ResultComparator<ManufacturerReplicaReceivePaymentMessage> {
	public boolean result;
	public ManufacturerReplicaReceivePaymentMessage(String sender,
			int senderSeq, int receiverSeq, boolean result) {
		super(sender, senderSeq, receiverSeq, Action.receivePayment);
		this.result = result;
	}

	public String toString(){
		return super.toString() + ", " + result;
	}

	@Override
	public boolean hasSameResult(ManufacturerReplicaReceivePaymentMessage other) {
		return result == other.result;
	}

}
