package retailer;
import java.net.InetAddress;

import tools.ConfigureManager;
import tools.LoggerClient;

public class RetailerRMApp {

	public static void main(String[] args) {
		if(args.length == 1){
			try{
				int index = Integer.parseInt(args[0]); 
				if(index > 0 && index < 5){
					LoggerClient loggerClient = new LoggerClient("RetailerRM" + index);
					try {
						String localIp = InetAddress.getLocalHost().getHostAddress();
						String configHost = ConfigureManager.getInstance().getString("RetailerRM" + index + "Host");
						if(localIp.equals(configHost)){
							try{
								RetailerRM retailerRM = new RetailerRM(loggerClient, index);
							}catch(Exception e){
								loggerClient.write(e.toString());
							}
						}else{
							System.out.println("Please run the RetailerRM" + index +" on:" 
									+ configHost + " or change the RetailerRM" + index + "Host of configure file to:" + localIp);
							loggerClient.write("Please run the RetailerRM" + index +" on:" 
									+ configHost + " or change the RetailerRM" + index + "Host of configure file to:" + localIp);
						}
					} catch (Exception e1) {
						loggerClient.write(e1.toString());
					}
				}else{
					LoggerClient loggerClient = new LoggerClient("NON serialized RetailerRM");
					loggerClient.write("index range[1-4]. Received index:" + index);
				}
			}catch(NumberFormatException e){
				LoggerClient loggerClient = new LoggerClient("NON serialized RetailerRM");
				loggerClient.write("Wrong parameter(s):" + args[0] + ", " + args[1]);
			}
		}else{
			LoggerClient loggerClient = new LoggerClient("NON serialized RetailerRM");
			loggerClient.write("Miss parameter(s). Parameter count:" + args.length);
		}
	}
}
