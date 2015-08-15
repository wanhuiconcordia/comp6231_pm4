package manufacturer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.xml.ws.Endpoint;

import tools.ConfigureManager;
import tools.LoggerClient;

public class ManufacturerFE {
	
	public static void main(String []args){
		String name = "ManufacturerFE";
		try {
			LoggerClient loggerClient = new LoggerClient(name);
			String localIp = InetAddress.getLocalHost().getHostAddress();
			String ManufacturerFEHost = ConfigureManager.getInstance().getString(name + "Host");
			
			if(localIp.equals(ManufacturerFEHost)){
				String ManufacturerFEServicePort = ConfigureManager.getInstance().getString("ManufacturerFEServicePort");
				String url = "http://" + ManufacturerFEHost + ":" + ManufacturerFEServicePort + "/ws/ManufacturerFE";
				ManufacturerFEImpl ManufacturerFEImpl = new ManufacturerFEImpl(name, loggerClient);
				Endpoint.publish(url, ManufacturerFEImpl);
				System.out.println(name + " is published at:" + url);				
			}else{
				System.out.println("Please run the ManufacturerFEHost on:" + ManufacturerFEHost + " or change the ManufacturerFEHost of configure file to:" + localIp);
				return;
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
