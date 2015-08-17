package manufacturer;

import java.net.InetAddress;

import tools.ConfigureManager;
import tools.LoggerClient;
import tools.channel.ChannelManager;

public class ManufacturerReplica {
	String name;
	int manufacturerIndex;
	int replicaIndex;
	int goodReplicaIndex;	
	ChannelManager channelManager; 
	LoggerClient loggerClient;
	public ManufacturerReplica(LoggerClient loggerClient, int manufacturerIndex, int replicaIndex, int goodReplicaIndex) throws Exception{
		this.manufacturerIndex = manufacturerIndex;
		this.replicaIndex = replicaIndex;
		this.goodReplicaIndex = goodReplicaIndex;
		
		
		
		
		
		//TODO
		
		
		
		
	}
	public static void main(String[] args) {
		String baseName = "Manufacturer";
		String extraName = "Replica";
		String fullName = baseName + extraName;
		String paraOptions = "Wrong parameters. 3 parameters are expected. Para 1 is for Manufacturer index. Para 2 is for Replica index(1-4). Para 3 is for good replica index(0-4)";
		LoggerClient loggerClient = new LoggerClient(fullName);
		if(args.length == 3){
			try{
				int manufacturerIndex = Integer.parseInt(args[0]);
				int replicaIndex = Integer.parseInt(args[1]);
				int goodReplicaIndex = Integer.parseInt(args[2]);
				
				if(manufacturerIndex > 0 
						&& manufacturerIndex < 5 
						&& replicaIndex > 0 
						&& replicaIndex < 5 
						&& goodReplicaIndex >=0 
						&& goodReplicaIndex < 5){
					fullName = baseName + manufacturerIndex + extraName + replicaIndex;
					loggerClient.setSenderName(fullName);
					try {
						String localIp = InetAddress.getLocalHost().getHostAddress();
						String configHost = ConfigureManager.getInstance().getString(fullName + "Host");
						if(localIp.equals(configHost)){
							ManufacturerReplica retailerReplica = new ManufacturerReplica(loggerClient, manufacturerIndex, replicaIndex, goodReplicaIndex);
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