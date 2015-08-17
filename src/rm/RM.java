package rm;

import java.net.InetAddress;

import tools.ConfigureManager;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.MessageProcesser;

public class RM {
	String type;
	String name;
	int index;
	ChannelManager channelManager; 
	LoggerClient loggerClient;
	Process replicaProcess;
	
	public RM(LoggerClient loggerClient, String type, int index) throws Exception{
		this.loggerClient = loggerClient;
		name = type + "RM";
		this.type = type;
		this.index = index;
		String host = ConfigureManager.getInstance().getString(name + index + "Host");
		int port = ConfigureManager.getInstance().getInt(name + index + "Port");
		System.out.println(name + index + " udp channel:" + host + ":" + port);
		loggerClient.write(name + index + " udp channel:" + host + ":" + port);

		//String runReplicaCmd = "./start" + type + "Replica.sh";
		String runReplicaCmd = ConfigureManager.getInstance().getString("run" + type + "ReplicaCMD");
		channelManager = new ChannelManager(port, loggerClient, new RMMessageProcesser(runReplicaCmd, index));

		for(int i = 1; i <= 4; i++){
			if(i != index){
				host = ConfigureManager.getInstance().getString(name + i + "Host");
				port = ConfigureManager.getInstance().getInt(name + i + "Port");
				channelManager.addChannel(new Channel(name + index, name + i, host, port, Group.RM));
			}
		}

		host = ConfigureManager.getInstance().getString(type + "FEHost");
		port = ConfigureManager.getInstance().getInt(type + "FEPort");
		channelManager.addChannel(new Channel(name + index, type + "FE", host, port, Group.FE));

		host = ConfigureManager.getInstance().getString(type + "Replica" + index + "Host");
		port = ConfigureManager.getInstance().getInt(type + "Replica" + index + "Port");
		channelManager.addChannel(new Channel(name + index, type + "Replica" + index, host, port, Group.REPLICA));
	}
	
	public void start(){
		channelManager.start();
	}

	public static void main(String[] args) {
		String name = "RM";
		String typeOptions = "para1 options: Retailer,"
				+ "Warehouse1,"
				+ "Warehouse2,"
				+ "Warehouse3,"
				+ "Manufacturer1,"
				+ "Manufacturer2,"
				+ "Manufacturer3";
		String indexOptions = "para2 options: 1,2,3,4";
				
		LoggerClient loggerClient = new LoggerClient(name);
		if(args.length == 2){
			String type = args[0];
			try{
				int index = Integer.parseInt(args[1]); 
				if(index > 0 && index < 5){
					if(type.equals("Retailer")
							|| type.equals("Warehouse1")
							|| type.equals("Warehouse2")
							|| type.equals("Warehouse3")
							|| type.equals("Manufacturer1")
							|| type.equals("Manufacturer2")
							|| type.equals("Manufacturer3")){
						try {
							loggerClient.setSenderName(type + name + index);
							String localIp = InetAddress.getLocalHost().getHostAddress();
							String configHost = ConfigureManager.getInstance().getString(type + name + index + "Host");
							if(localIp.equals(configHost)){
								try{
									RM rm = new RM(loggerClient, type, index);
									rm.start();
								}catch(Exception e){
									loggerClient.write(e.toString());
									e.printStackTrace();
								}
							}else{
								System.out.println("Please run the " + type + name + index +" on:" 
										+ configHost + " or change the " + type + name + index + "Host of configure file to:" + localIp);
								loggerClient.write("Please run the " + type + name + index +" on:" 
										+ configHost + " or change the " + type + name + index + "Host of configure file to:" + localIp);
							}
						} catch (Exception e1) {
							loggerClient.write(e1.toString());
							System.out.println(e1.toString());
						}
					}else{
						loggerClient.write("Wrong input. " + typeOptions + ". " + indexOptions);
						System.out.println("Wrong input. " + typeOptions + ". " + indexOptions);
					}
				}else{
					loggerClient.write("Wrong input. " + typeOptions + ". " + indexOptions);
					System.out.println("Wrong input. " + typeOptions + ". " + indexOptions);
				}
			}catch(NumberFormatException e){
				loggerClient.write("Wrong input. " + typeOptions + ". " + indexOptions);
				System.out.println("Wrong input. " + typeOptions + ". " + indexOptions);
			}
		}else{
			loggerClient.write("Wrong input. " + typeOptions + ". " + indexOptions);
			System.out.println("Wrong input. " + typeOptions + ". " + indexOptions);
		}
	}
}
