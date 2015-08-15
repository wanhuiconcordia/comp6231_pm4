package retailer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.xml.ws.Endpoint;

import sequencer.retailerSequencer.RetailerSequencer;
import tools.ConfigureManager;
import tools.LoggerClient;

public class RetailerFE {
	
	public static void main(String []args){
		String name = "RetailerFE";
		try {
			LoggerClient loggerClient = new LoggerClient(name);
			String localIp = InetAddress.getLocalHost().getHostAddress();
			String retailerFEHost = ConfigureManager.getInstance().getString(name + "Host");
			
			if(localIp.equals(retailerFEHost)){
				String RetailerFEServicePort = ConfigureManager.getInstance().getString("RetailerFEServicePort");
				String url = "http://" + retailerFEHost + ":" + RetailerFEServicePort + "/ws/retailerFE";
				RetailerFEImpl retailerFEImpl = new RetailerFEImpl(name, loggerClient);
				Endpoint.publish(url, retailerFEImpl);
				System.out.println(name + " is published at:" + url);				
			}else{
				System.out.println("Please run the RetailerFEHost on:" + retailerFEHost + " or change the RetailerFEHost of configure file to:" + localIp);
				return;
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
