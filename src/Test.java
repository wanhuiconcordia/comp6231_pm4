import java.io.IOException;
import java.util.Scanner;

import tools.Customer;
import tools.SignUpResult;
import tools.message.ResultComparator;
import tools.message.retailerReplica.RetailerReplicaSignInResultMessage;
import tools.message.retailerReplica.RetailerReplicaSignUpReultMessage;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		String testStr = "Warehouse1Replica1";
		String testStr2 = testStr.replaceAll("Replica", "RM");
		System.out.println(testStr);
		System.out.println(testStr2);
		
	}

}
