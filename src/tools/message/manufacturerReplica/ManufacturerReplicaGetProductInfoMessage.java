package tools.message.manufacturerReplica;

import tools.Product;
import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class ManufacturerReplicaGetProductInfoMessage extends Message implements ResultComparator<ManufacturerReplicaGetProductInfoMessage>{
	public Product product;
	public ManufacturerReplicaGetProductInfoMessage(String sender,
			int senderSeq, int receiverSeq, Product product) {
		super(sender, senderSeq, receiverSeq, Action.getProductInfo);
		this.product = product;
	}
	
	public String toString(){
		return super.toString() + ", " + product.toString();
	}

	@Override
	public boolean hasSameResult(ManufacturerReplicaGetProductInfoMessage other) {
		if(product == null){
			if(other.product == null){
				return true;
			}else{
				return false;
			}
		}else{
			if(other.product == null){
				return false;
			}else{
				return product.isSame(other.product);
			}
		}
	}

}
