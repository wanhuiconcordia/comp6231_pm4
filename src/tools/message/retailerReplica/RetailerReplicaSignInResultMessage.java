package tools.message.retailerReplica;

import tools.Customer;
import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class RetailerReplicaSignInResultMessage extends Message implements ResultComparator<RetailerReplicaSignInResultMessage> {

	public Customer customer;
	public RetailerReplicaSignInResultMessage(String sender, int senderSeq,
			int receiverSeq, Customer customer) {
		super(sender, senderSeq, receiverSeq, Action.signIn);
		this.customer = customer;
	}
	
	@Override
	public String toString(){
		if(customer == null){
			return super.toString() + ", null";
		}
		return super.toString()
				+ ", " + customer.toString();
	}

	@Override
	public boolean hasSameResult(RetailerReplicaSignInResultMessage other) {
		if(customer == null){
			if(other.customer == null){
				return true;
			}else{
				return false;
			}
		}else{
			if(other.customer == null){
				return true;
			}else{
				return customer.isSame(other.customer);
			}
		}
	}
}
