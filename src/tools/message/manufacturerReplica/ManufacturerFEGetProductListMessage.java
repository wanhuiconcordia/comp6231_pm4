package tools.message.manufacturerReplica;

import tools.ProductList;
import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class ManufacturerFEGetProductListMessage extends Message implements ResultComparator<ManufacturerFEGetProductListMessage>{
	public ProductList productList;
	
	public ManufacturerFEGetProductListMessage(String sender, int senderSeq,
			int receiverSeq, ProductList productList) {
		super(sender, senderSeq, receiverSeq, Action.getProductList);
		this.productList = productList;
	}
	
	public String toString(){
		return super.toString() + ", " + productList.toString();
	}

	@Override
	public boolean hasSameResult(ManufacturerFEGetProductListMessage other) {
		if(productList == null){
			if(other.productList == null){
				return true;
			}else{
				return false;
			}
		}else{
			if(other.productList == null){
				return false;
			}else{
				return productList.isSame(other.productList);
			}
		}
	}
	
}
