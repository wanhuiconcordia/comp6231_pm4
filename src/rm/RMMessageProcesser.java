package rm;

import java.io.IOException;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.AckMessage;
import tools.message.Action;
import tools.message.ReplicaResultMessage;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.rm.HeartBeatMessage;
import tools.message.rm.RMSyncMessage;

public class RMMessageProcesser extends MessageProcesser{
	public Process replicaProcess;
	public String runReplicaCmd;
	public String fullRunReplicaCmd;
	public int index;
	public int failCount;
	public RMMessageProcesser(String runReplicaCmd, int index){
		this.runReplicaCmd = runReplicaCmd;
		this.index = index;
		fullRunReplicaCmd = runReplicaCmd + " " + index + " 0";
		System.out.println(fullRunReplicaCmd);
		try {
			replicaProcess = Runtime.getRuntime().exec(fullRunReplicaCmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
		failCount = 0;
	}

	@Override
	public void processNewRequest(ChannelManager channelManager,
			Channel channel, Message msg) {

		if(msg.action == Action.ACK){
			channel.isWaitingForRespose = false;
		}else if(msg.action == Action.REPLICA_RESULT){
			channel.receivedMessage = msg;
			ackBack(channelManager, channel);
			ReplicaResultMessage replicaResultMessage = (ReplicaResultMessage)msg;
			switch(replicaResultMessage.replicaStatus){
			case good:
				failCount = 0;
				break;
			case fail:
				failCount++;
				if(failCount > 2){
					try {
						replicaProcess.destroy();
						fullRunReplicaCmd = runReplicaCmd
								+ " " + index 
								+ " " + replicaResultMessage.goodReplicaIndex;
						replicaProcess = Runtime.getRuntime().exec(fullRunReplicaCmd);
						failCount = 0;
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}else{
					for(Channel tmpChannel: channelManager.channelMap.values()){
						if(tmpChannel.group == Group.REPLICA){
							
							tmpChannel.backupPacket = new Packet(tmpChannel.peerProcessName, tmpChannel.peerHost
									, tmpChannel.peerPort
									, new RMSyncMessage(tmpChannel.localProcessName
											, ++tmpChannel.localSeq
											, tmpChannel.peerSeq
											, replicaResultMessage.goodReplicaIndex));
							
							tmpChannel.isWaitingForRespose = true;
							synchronized(channelManager.outgoingPacketQueueLock) {
								channelManager.outgoingPacketQueue.add(tmpChannel.backupPacket);
							}
							break;
						}
					}
				}
				break;
			case noAnswer:
				try {
					replicaProcess.destroy();
					fullRunReplicaCmd = runReplicaCmd
							+ " " + index 
							+ " " + replicaResultMessage.goodReplicaIndex;
					replicaProcess = Runtime.getRuntime().exec(fullRunReplicaCmd);
					failCount = 0;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
		else{
			System.out.println("Unrecognizable action");
		}
	}
	
	public void processTimeout(ChannelManager channelManager) {
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.isWaitingForRespose){
					channel.timeoutTimes++;
					if(channel.timeoutTimes > 3){
						channel.isWaitingForRespose = false;
						
						if(channel.group == Group.REPLICA){
							//get a god replica
							
							System.out.println(channel.peerProcessName + " is dead will distroy it and create a new one.");
							
							int goodReplicaIndex = 0;
							
							replicaProcess.destroy();
							fullRunReplicaCmd = runReplicaCmd
									+ " " + index 
									+ " " + goodReplicaIndex;
							try {
								replicaProcess = Runtime.getRuntime().exec(fullRunReplicaCmd);
								failCount = 0;
								channel.timeoutTimes = 0;
								channel.localSeq = 0;
								channel.peerSeq = 0;
							} catch (IOException e) {
								e.printStackTrace();
							}
							
						}
					}else{
						synchronized(channelManager.outgoingPacketQueueLock) {
							channelManager.outgoingPacketQueue.add(channel.backupPacket);
						}
					}
				}else{
					if(channel.group == Group.REPLICA){
						sendHeartBeat(channelManager, channel);
					}
				}
			}
	}
	
	public void sendHeartBeat(ChannelManager channelManager, Channel channel){
		Message outGoingMsg = new HeartBeatMessage(channel.localProcessName
				, ++channel.localSeq
				, channel.peerSeq);
		channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost,  channel.peerPort, outGoingMsg); 
		channel.isWaitingForRespose = true;
		synchronized (channelManager.outgoingPacketQueueLock) {
			channelManager.outgoingPacketQueue.add(channel.backupPacket);
		}
	}
}
