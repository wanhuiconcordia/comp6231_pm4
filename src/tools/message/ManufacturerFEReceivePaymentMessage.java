package tools.message;

public class ManufacturerFEReceivePaymentMessage extends Message{
	public String orderNum;
	public float totalPrice;
	public ManufacturerFEReceivePaymentMessage(String sender, int senderSeq, int receiverSeq, String orderNum, float totalPrice){
		super(sender, senderSeq, receiverSeq, Action.receivePayment);
		this.orderNum = orderNum;
		this.totalPrice = totalPrice;
	}

}
