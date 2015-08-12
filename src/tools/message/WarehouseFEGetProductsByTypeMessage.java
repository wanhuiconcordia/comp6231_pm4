package tools.message;

public class WarehouseFEGetProductsByTypeMessage extends Message{

	public String productType;
	
	public WarehouseFEGetProductsByTypeMessage(String sender, int senderSeq, int receiverSeq, String productType){
		super(sender, senderSeq, receiverSeq, Action.getProductsBytype);
		this.productType = productType;
	}

}
