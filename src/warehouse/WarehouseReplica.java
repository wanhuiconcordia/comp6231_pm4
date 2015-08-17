package warehouse;

import java.net.InetAddress;

import tools.ConfigureManager;
import tools.LoggerClient;
import tools.channel.ChannelManager;

public class WarehouseReplica {
	String name;
	int warehouseIndex;
	int replicaIndex;
	int mode;	
	ChannelManager channelManager; 
	LoggerClient loggerClient;
	public WarehouseReplica(LoggerClient loggerClient, int warehouseIndex, int replicaIndex, int mode) throws Exception{
		this.warehouseIndex = warehouseIndex;
		this.replicaIndex = replicaIndex;
		this.mode = mode;
		
		
		
		
		
		//TODO
		
		
		
		
	}
	public static void main(String[] args) {
		String baseName = "Warehouse";
		String extraName = "Replica";
		String fullName = baseName + extraName;
		String paraOptions = "Wrong parameters. 3 parameters are expected. Para 1 is for Warehouse index. Para 2 is for Replica index(1-4). Para 3 is for open mode(0-4)";
		LoggerClient loggerClient = new LoggerClient(fullName);
		if(args.length == 3){
			try{
				int warehouseIndex = Integer.parseInt(args[0]);
				int replicaIndex = Integer.parseInt(args[1]);
				int mode = Integer.parseInt(args[2]);
				
				if(warehouseIndex > 0 
						&& warehouseIndex < 5 
						&& replicaIndex > 0 
						&& replicaIndex < 5 
						&& mode >=0 
						&& mode < 5){
					fullName = baseName + warehouseIndex + extraName + replicaIndex;
					loggerClient.setSenderName(fullName);
					try {
						String localIp = InetAddress.getLocalHost().getHostAddress();
						String configHost = ConfigureManager.getInstance().getString(fullName + "Host");
						if(localIp.equals(configHost)){
							WarehouseReplica retailerReplica = new WarehouseReplica(loggerClient, warehouseIndex, replicaIndex, mode);
						}else{
							System.out.println("Please run the " + fullName + " on:" 
									+ configHost + " or change the " + fullName + "Host of configure file to:" + localIp);

							loggerClient.write("Please run the " + fullName +" on:" 
									+ configHost + " or change the " + fullName + "Host of configure file to:" + localIp);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						loggerClient.write(e1.toString());
					}
				}else{
					System.out.println(paraOptions);
					loggerClient.write(paraOptions);	
				}
				
			}catch(NumberFormatException e){
				System.out.println(paraOptions);
				loggerClient.write(paraOptions);
			}
		}else{
			System.out.println(paraOptions);
			loggerClient.write(paraOptions);
		}
	}
}
