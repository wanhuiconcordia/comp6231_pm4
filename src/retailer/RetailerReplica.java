package retailer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import loggerserver.LoggerServer;
import sequencer.retailerSequencer.RetailerSequencerMessageProcesser;
import tools.ConfigureManager;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;

public class RetailerReplica {
	String name;
	int index;
	int mode;	
	ChannelManager channelManager; 
	LoggerClient loggerClient;
	public RetailerReplica(LoggerClient loggerClient, int index, int mode) throws Exception{
		String baseName = "RetailerReplica";
		String name = baseName + index;
		this.index = index;
		this.mode = mode;
		this.loggerClient = loggerClient; 
		loggerClient.write("index:" + index + ", mode:" + mode);
		String host = ConfigureManager.getInstance().getString("RetailerReplica" + index + "Host");
		int port = ConfigureManager.getInstance().getInt("RetailerReplica" + index + "Port");
		System.out.println(name + " udp channel:" + host + ":" + port);
		loggerClient.write(name + " udp channel:" + host + ":" + port);

		ChannelManager channelManager = new ChannelManager(port, loggerClient, new RetailerReplicaMessageProcesser());

		for(int i = 1; i <= 4; i++){
			if(i != index){
				host = ConfigureManager.getInstance().getString(baseName + i + "Host");
				port = ConfigureManager.getInstance().getInt(baseName + i + "Port");
				channelManager.addChannel(new Channel(name, baseName + i, host, port, Group.REPLICA));
			}
		}

		host = ConfigureManager.getInstance().getString("RetailerSequencerHost");
		port = ConfigureManager.getInstance().getInt("RetailerSequencerPort");
		channelManager.addChannel(new Channel(name, "RetailerSequencer", host, port, Group.FE));
		
		host = ConfigureManager.getInstance().getString("RetailerRM" + index + "Host");
		port = ConfigureManager.getInstance().getInt("RetailerRM" + index + "Port");
		channelManager.addChannel(new Channel(name, "RetailerRM" + index, host, port , Group.RM));

		host = ConfigureManager.getInstance().getString("RetailerFEHost");
		port = ConfigureManager.getInstance().getInt("RetailerFEPort");
		channelManager.addChannel(new Channel(name, "RetailerFE", host, port, Group.FE));

		channelManager.start();
	}

	public static void main(String[] args) {
		String baseName = "RetailerReplica";
		String paraOptions = "Wrong parameters. 2 parameters are expected. Para 1 is for RetailerReplica index(1-4). Para 2 is for open mode(0-4)";
		LoggerClient loggerClient = new LoggerClient(baseName);
		if(args.length == 2){
			try{
				int index = Integer.parseInt(args[0]);
				int mode = Integer.parseInt(args[1]);
				if(index > 0 && index < 5 && mode >=0 && mode < 5){
					String fullName = baseName + index;
					loggerClient.setSenderName(fullName);
					try {
						String localIp = InetAddress.getLocalHost().getHostAddress();
						String configHost = ConfigureManager.getInstance().getString(fullName + "Host");
						if(localIp.equals(configHost)){
							RetailerReplica retailerReplica = new RetailerReplica(loggerClient, index, mode);
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
