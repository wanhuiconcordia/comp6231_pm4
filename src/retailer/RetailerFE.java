package retailer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.xml.ws.Endpoint;

import sequencer.retailerSequencer.RetailerSequencer;
import tools.ConfigureManager;

public class RetailerFE {
	
	public static void main(String []args){
		
		try {
			String localIp = InetAddress.getLocalHost().getHostAddress();
			String retailerFEHost = ConfigureManager.getInstance().getString("RetailerFEHost");
			
			if(!localIp.equals(retailerFEHost)){
				System.out.println("Please run the RetailerFEHost on:" + retailerFEHost + " or change the RetailerFEHost of configure file to:" + localIp);
				return;
			}
			
			String name = "RetailerFE";
			String RetailerFEServicePort = ConfigureManager.getInstance().getString("RetailerFEServicePort");
			String url = "http://" + retailerFEHost + ":" + RetailerFEServicePort + "/ws/retailerFE";
			RetailerFEImpl retailerFEImpl = new RetailerFEImpl(name);
			Endpoint.publish(url, retailerFEImpl);
			System.out.println(name + " is published at:" + url);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
