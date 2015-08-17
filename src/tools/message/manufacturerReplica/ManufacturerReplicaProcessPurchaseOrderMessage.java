package tools.message.manufacturerReplica;

import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class ManufacturerReplicaProcessPurchaseOrderMessage extends Message implements ResultComparator<ManufacturerReplicaProcessPurchaseOrderMessage>{
	public String purchaseOrderNum; 
	public ManufacturerReplicaProcessPurchaseOrderMessage(String sender,
			int senderSeq, int receiverSeq, String purchaseOrderNum) {
		super(sender, senderSeq, receiverSeq, Action.processPurchaseOrder);
		this.purchaseOrderNum = purchaseOrderNum;
	}
	
	public String toString(){
		return super.toString() + ", " + purchaseOrderNum;
	}

	@Override
	public boolean hasSameResult(
			ManufacturerReplicaProcessPurchaseOrderMessage other) {
		if(purchaseOrderNum == null){
			if(other.purchaseOrderNum == null){
				return true;
			}else{
				return false;
			}
		}else{
			if(other.purchaseOrderNum == null){
				return false;
			}else{
				return purchaseOrderNum.equals(other.purchaseOrderNum);
			}
		}
	}

}
