package tools.message;

import tools.ItemList;

public class TestMessage extends Message{
	ItemList itemList;
	public TestMessage(String sender, int senderSeq, int receiverSeq, ItemList itemList){
		super(sender, senderSeq, receiverSeq, Action.test);
		this.itemList = itemList;
	}
	
	public String toString(){
		return super.toString() + ", " + itemList.toString();
	}
}
