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
	String mode;	//None; RetailerReplica1;RetailerReplica2;RetailerReplica3;RetailerReplica4
	ChannelManager channelManager; 
	LoggerClient loggerClient;
	public RetailerReplica(LoggerClient loggerClient, int index, String mode) throws Exception{
		String baseName = "RetailerReplica";
		String name = baseName + index;
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
		if(args.length == 2){
			try{
				int index = Integer.parseInt(args[0]); 
				if(index > 0 && index < 5){
					LoggerClient loggerClient = new LoggerClient("RetailerReplica" + index);
					try {
						String localIp = InetAddress.getLocalHost().getHostAddress();
						String configHost = ConfigureManager.getInstance().getString("RetailerReplica" + index + "Host");
						if(localIp.equals(configHost)){
							try{
								RetailerReplica retailerReplica = new RetailerReplica(loggerClient, index, args[1]);
							}catch(Exception e){
								loggerClient.write(e.toString());
								e.printStackTrace();
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
					LoggerClient loggerClient = new LoggerClient("NON serialized RetailerReplica");
					loggerClient.write("index range[1-4]. Received index:" + index);
				}
			}catch(NumberFormatException e){
				LoggerClient loggerClient = new LoggerClient("NON serialized RetailerReplica");
				loggerClient.write("Wrong parameter(s):" + args[0] + ", " + args[1]);
			}
		}else{
			LoggerClient loggerClient = new LoggerClient("NON serialized RetailerReplica");
			loggerClient.write("Miss parameter(s). Parameter count:" + args.length);
		}
	}
}
