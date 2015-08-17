package tools.message.manufacturerFE;

import tools.message.Action;
import tools.message.Message;

public class ManufacturerFEReceivePaymentMessage extends Message{
	public String orderNum;
	public float totalPrice;
	public ManufacturerFEReceivePaymentMessage(String sender, int senderSeq, int receiverSeq, String orderNum, float totalPrice){
		super(sender, senderSeq, receiverSeq, Action.receivePayment);
		this.orderNum = orderNum;
		this.totalPrice = totalPrice;
	}

	public String toString(){
		return super.toString() 
				+ ", " + orderNum
				+ ", " + totalPrice;
	}
}
