package warehouse;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.ws.Endpoint;

import tools.ConfigureManager;
import tools.LoggerClient;

public class WarehouseFE {
	public static void main(String []args){
		String baseName = "Warehouse";
		String fullName = baseName + "FE";
		String paraOptions = "para options: 1, 2, 3";
		LoggerClient loggerClient = new LoggerClient(fullName);
		if((args.length == 1) 
				&& (args[0].equals("1")
						|| args[0].equals("2")
						|| args[0].equals("3"))){
			int index = Integer.parseInt(args[0]); 
			baseName = baseName + index;
			fullName = baseName + "FE";
			loggerClient.setSenderName(fullName);
			try {
				String localIp = InetAddress.getLocalHost().getHostAddress();
				String warehouseFEHost = ConfigureManager.getInstance().getString(fullName + "Host");
				
				if(localIp.equals(warehouseFEHost)){
					String warehouseFEServicePort = ConfigureManager.getInstance().getString(fullName + "ServicePort");
					String url = "http://" + warehouseFEHost + ":" + warehouseFEServicePort + "/ws/" + fullName;
					WarehouseFEImpl warehouseFEImpl = new WarehouseFEImpl(baseName, loggerClient);
					Endpoint.publish(url, warehouseFEImpl);
					System.out.println(fullName + " is published at:" + url);
					loggerClient.write(fullName + " is published at:" + url);
				}else{
					System.out.println("Please run the " + fullName + " on:" + warehouseFEHost + " or change the " + fullName + "Host of configure file to:" + localIp);
					loggerClient.write("Please run the " + fullName + " on:" + warehouseFEHost + " or change the " + fullName + "Host of configure file to:" + localIp);
					return;
				}
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
				loggerClient.write(e.toString());
			} catch (Exception e) {
				e.printStackTrace();
				loggerClient.write(e.toString());
			}		
			
		}else{
			loggerClient.write("Wrong input. " + paraOptions);
			System.out.println("Wrong input. " + paraOptions);
		}
	}
}
