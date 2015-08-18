package tools.message.manufacturerReplica;

import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class ManufacturerReplicaProcessPurchaseOrderMessage extends Message implements ResultComparator<ManufacturerReplicaProcessPurchaseOrderMessage>{
	public boolean result; 
	public ManufacturerReplicaProcessPurchaseOrderMessage(String sender,
			int senderSeq, int receiverSeq, Boolean result) {
		super(sender, senderSeq, receiverSeq, Action.processPurchaseOrder);
		this.result = result;
	}

	public String toString(){
		return super.toString() + ", " + result;
	}

	@Override
	public boolean hasSameResult(
			ManufacturerReplicaProcessPurchaseOrderMessage other) {
		return (result == other.result);
	}
}
