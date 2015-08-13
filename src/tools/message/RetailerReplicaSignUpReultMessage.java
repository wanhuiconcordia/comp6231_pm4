package tools.message;

import tools.SignUpResult;

public class RetailerReplicaSignUpReultMessage extends Message {
	public SignUpResult signUpResult;
	public RetailerReplicaSignUpReultMessage(String sender
			, int senderSeq
			, int receiverSeq
			,SignUpResult signUpResult) {
		super(sender, senderSeq, receiverSeq, Action.signUp);
		this.signUpResult = signUpResult;
	}
	public String toString(){
		return super.toString() + ", " + signUpResult.toString();
	}
}
