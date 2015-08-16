import java.io.IOException;
import java.util.Scanner;

import tools.Customer;
import tools.SignUpResult;
import tools.message.ResultComparator;
import tools.message.retailerReplica.RetailerReplicaSignInResultMessage;
import tools.message.retailerReplica.RetailerReplicaSignUpReultMessage;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		RetailerReplicaSignUpReultMessage msg1 = new RetailerReplicaSignUpReultMessage("sender"
				, 22
				, 23
				, new SignUpResult());
		
		RetailerReplicaSignUpReultMessage msg2 = new RetailerReplicaSignUpReultMessage("sender"
				, 22
				, 23
				, new SignUpResult());
		
		RetailerReplicaSignInResultMessage msg3 = new RetailerReplicaSignInResultMessage("sender"
				, 22
				, 23
				, new Customer());
		if(((ResultComparator)msg1).hasSameResult((ResultComparator)msg3)){
			System.out.println("same");
		}else{
			System.out.println("different");
		}
	}

}
