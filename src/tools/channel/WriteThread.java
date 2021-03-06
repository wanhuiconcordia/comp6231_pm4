package tools.channel;

import tools.message.Action;
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
				if(packet != null){
					if(!(packet.msg.action == Action.ACK || packet.msg.action == Action.HEART_BEAT)){
						System.out.println("Send packet:" + packet.toString());
						channelManager.loggerClient.write(packet.toString());	
					}
					networkIO.sendMsg(packet.msg, packet.receiverHost, packet.receiverPort);
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void terminate(){
		keepWriting = false;
	}
}