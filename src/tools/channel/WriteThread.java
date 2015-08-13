package tools.channel;

import tools.message.Packet;

public class WriteThread extends Thread {
	ChannelManager channelManager;
	NetworkIO networkIO;
	boolean keepWriting;
	public WriteThread(ChannelManager channelManager, NetworkIO networkIO){
		this.channelManager = channelManager;
		this.networkIO = networkIO;
		keepWriting = true;
	}

	public void run(){
		while(keepWriting){
			while(!channelManager.outgoingPacketQueue.isEmpty()){
				Packet packet;
				synchronized(channelManager.outgoingPacketQueueLock) {
					packet = channelManager.outgoingPacketQueue.remove();
				}
				networkIO.sendMsg(packet.msg, packet.receiverHost, packet.receiverPort);				
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			channelManager.loggerClient.write("write thread is running...");
		}
	}

	public void terminate(){
		keepWriting = false;
	}
}