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
			synchronized(channelManager.outgoingPacketQueueLock) {
				while(!channelManager.outgoingPacketQueue.isEmpty()){
					Packet packet = channelManager.outgoingPacketQueue.remove();
					networkIO.sendMsg(packet.msg, packet.receiverHost, packet.receiverPort);				
				}
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void terminate(){
		keepWriting = false;
	}
}