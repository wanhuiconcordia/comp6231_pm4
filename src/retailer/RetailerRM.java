package retailer;

import java.net.InetAddress;

import tools.ConfigureManager;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;

public class RetailerRM {
	String name;
	int index;
	ChannelManager channelManager; 
	LoggerClient loggerClient;
	public RetailerRM(LoggerClient loggerClient, int index) throws Exception{
		this.name = "RetailerRM";
		this.loggerClient = loggerClient;
		this.index = index;
		String host = ConfigureManager.getInstance().getString(name + index + "Host");
		int port = ConfigureManager.getInstance().getInt(name + index + "Port");
		System.out.println(name + index + " udp channel:" + host + ":" + port);
		loggerClient.write(name + index + " udp channel:" + host + ":" + port);
		
		ChannelManager channelManager = new ChannelManager(port, loggerClient, new RetailerRMMessageProcesser());
		
		for(int i = 1; i <= 4; i++){
			if(i != index){
				host = ConfigureManager.getInstance().getString(name + i + "Host");
				port = ConfigureManager.getInstance().getInt(name + i + "Port");
				channelManager.addChannel(new Channel(name + index, name + i, host, port, Group.RM));
			}
		}
		
		host = ConfigureManager.getInstance().getString("RetailerFEHost");
		port = ConfigureManager.getInstance().getInt("RetailerFEPort");
		channelManager.addChannel(new Channel(name + index, "RetailerFE", host, port, Group.FE));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica" + index + "Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica" + index + "Port");
		channelManager.addChannel(new Channel(name + index, "RetailerReplica" + index, host, port, Group.REPLICA));
		String cmd = "java retailer.RetailerReplica 4 0";
		cmd = "./startRetailerReplica.sh " + index + " 0"; 
		//Runtime.getRuntime().exec("./startRetailerReplica.sh 3 0");
		Runtime.getRuntime().exec(cmd);
		
		channelManager.start();
	}
	
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
