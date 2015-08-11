package retailer;

import tools.LoggerClient;

public class RetailerReplica {

	public static void main(String[] args) {
		String name = "Replica";
		if(args.length == 2){
			name += args[0];
			LoggerClient loggerClient = new LoggerClient(name);
			loggerClient.write(name + " is created in " + args[1] + " mode.");
		}else{
			LoggerClient loggerClient = new LoggerClient(name);
			loggerClient.write("Two parameters are required, but " + args.length + " parameter(s) received.");
		}
	}
}
