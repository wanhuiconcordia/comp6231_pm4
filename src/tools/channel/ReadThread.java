package tools.channel;

import tools.message.Message;

public class ReadThread extends Thread {
	ChannelManager channelManager;
	NetworkIO networkIO;
	boolean keepReading;
	public ReadThread(ChannelManager channelManager, NetworkIO networkIO){
		this.channelManager = channelManager;
		this.networkIO = networkIO;
		keepReading = true;
	}

	public void run(){
		while(keepReading){
			try{
				Message msg = networkIO.receiveMessage();
				if(msg != null){
					channelManager.processMessage(msg);
				}
			}catch(java.net.SocketTimeoutException e){
				channelManager.processTimeout();
			}
		}
	}

	public void terminate(){
		keepReading = false;
	}
}