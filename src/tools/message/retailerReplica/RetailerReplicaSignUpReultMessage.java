package tools.message.retailerReplica;

import tools.SignUpResult;
import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class RetailerReplicaSignUpReultMessage extends Message implements ResultComparator<RetailerReplicaSignUpReultMessage>{
	public SignUpResult signUpResult;
	public RetailerReplicaSignUpReultMessage(String sender
			, int senderSeq
			, int receiverSeq
			, SignUpResult signUpResult) {
		super(sender, senderSeq, receiverSeq, Action.signUp);
		this.signUpResult = signUpResult;
	}

	public String toString(){
		return super.toString() + ", " + signUpResult.toString();
	}

	@Override
	public boolean hasSameResult(RetailerReplicaSignUpReultMessage other) {
		if(signUpResult == null){
			if(other.signUpResult == null){
				return true;
			}else{
				return false;
			}
		}else{
			if(other.signUpResult == null){
				return false;
			}else{
				return signUpResult.isSame(other.signUpResult);
			}
		}
	}
}
