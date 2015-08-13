package tools.message;

public class ManufacturerFEGetProductListMessage extends Message{

	public ManufacturerFEGetProductListMessage(String sender, int senderSeq, int receiverSeq){
		
		super(sender, senderSeq, receiverSeq, Action.getProductList);
	}
}
