package tools.channel;

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
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.hasCachedMsg){
					networkIO.sendMsg(channel.cachedMsg, channel.peerHost, channel.peerPort);
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