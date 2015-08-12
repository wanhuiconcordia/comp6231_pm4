package tools.message;

public class WarehouseFEGetProductsMessage extends Message{
	public String productID;
	public String manufacturerName;
	
	public WarehouseFEGetProductsMessage(String sender, int senderSeq, int receiverSeq, String productID, String manufacturerName){
		super(sender, senderSeq, receiverSeq, Action.getProducts);
		this.productID = productID;
		this.manufacturerName = manufacturerName;
	}

}
