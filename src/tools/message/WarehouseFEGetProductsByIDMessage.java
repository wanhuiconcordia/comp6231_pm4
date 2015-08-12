package tools.message;

public class WarehouseFEGetProductsByIDMessage extends Message{
	
	public String productID;
	
	public WarehouseFEGetProductsByIDMessage(String sender, int senderSeq, int receiverSeq, String productID){
		super(sender, senderSeq, receiverSeq, Action.getProductsByID);
		this.productID = productID;
	}

}
