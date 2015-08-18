package rm;

import java.io.IOException;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.channel.ReplicaChannel;
import tools.message.AckMessage;
import tools.message.Action;
import tools.message.ReplicaResultMessage;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.replica.ReplicaCrushMessage;
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
		fullRunReplicaCmd = runReplicaCmd + " " + index + " 0 null";
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
			System.out.println("On ack Lseq:" + channel.localSeq + ", Pseq:" + channel.peerSeq);
		}else if(msg.action == Action.REPLICA_RESULT){
			channel.receivedMessage = msg;
			switch(msg.action){
			case REPLICA_RESULT:
				ReplicaResultMessage replicaResultMessage = (ReplicaResultMessage)msg;
				switch(replicaResultMessage.replicaStatus){
				case REPLICA_GOOD:
					failCount = 0;
					break;
				case REPLICA_FAIL:
					failCount++;
					if(failCount > 2){
						try {
							replicaProcess.destroy();
							fullRunReplicaCmd = runReplicaCmd
									+ " " + index 
									+ " 1 " + replicaResultMessage.goodReplicaName;
							System.out.println(fullRunReplicaCmd);
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
												, replicaResultMessage.goodReplicaName));
	
								tmpChannel.isWaitingForRespose = true;
								synchronized(channelManager.outgoingPacketQueueLock) {
									channelManager.outgoingPacketQueue.add(tmpChannel.backupPacket);
								}
								break;
							}
						}
					}
					break;
				case REPLICA_CRUSH:
					for(Channel tmpChannel: channelManager.channelMap.values()){
						if(tmpChannel.group == Group.REPLICA){
							tmpChannel.isWaitingForRespose = false;
							tmpChannel.localSeq = 0;
							tmpChannel.peerSeq = 0;
							tmpChannel.timeoutTimes = 0;
							fullRunReplicaCmd = runReplicaCmd
									+ " " + index 
									+ " 1 " + replicaResultMessage.goodReplicaName;
							System.out.println(fullRunReplicaCmd);
							try {
								replicaProcess = Runtime.getRuntime().exec(fullRunReplicaCmd);
								failCount = 0;
							} catch (IOException e) {
								e.printStackTrace();
							} 
							break;
						}
					}
					break;
				default:
					System.out.println("Unexpected result");
					break;
				}
				break;
			case INIT:
				channel.localSeq = 0;
				channel.peerSeq = msg.senderSeq;
				break;
			default:
				System.out.println("Unrecognizable action");
				break;
			}
			
			ackBack(channelManager, channel);
		}
		else{
			System.out.println("Unrecognizable action");
		}
	}
	
	public void processTimeout(ChannelManager channelManager) {
		for(Channel channel: channelManager.channelMap.values()){
			if(channel.group == Group.REPLICA){
				if(channel.timeoutTimes < 10){
					channel.timeoutTimes++;
					if(channel.isWaitingForRespose){
						synchronized(channelManager.outgoingPacketQueueLock) {
							channelManager.outgoingPacketQueue.add(channel.backupPacket);
						}
					}else{
						sendHeartBeat(channelManager, channel);
					}
				}
				else if(channel.timeoutTimes == 10){
					channel.timeoutTimes++;
					channel.isWaitingForRespose = false;
					if(channel.group == Group.REPLICA){
						System.out.println(channel.peerProcessName + " is dead. Distroy the old process.");
						replicaProcess.destroy();
						
						for(Channel FEChannel: channelManager.channelMap.values()){
							if(FEChannel.group == Group.FE){
								Message outGoingMsg = new ReplicaCrushMessage(FEChannel.localProcessName
										, ++FEChannel.localSeq
										, FEChannel.peerSeq
										, channel.peerProcessName);
								FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, outGoingMsg); 
								FEChannel.isWaitingForRespose = true;
								break;
							}
						}
					}
				}else{
					//do nothing...
				}
			}else{
				super.processTimeout(channelManager);
			}
		}
	}
	
	public void sendHeartBeat(ChannelManager channelManager, Channel channel){
		Message outGoingMsg = new HeartBeatMessage(channel.localProcessName
				, ++channel.localSeq
				, channel.peerSeq);
		channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost,  channel.peerPort, outGoingMsg); 
		channel.isWaitingForRespose = true;
	}

	@Override
	public void processDuplicaRequest(ChannelManager channelManager,
			Channel channel, Message msg) {
		// TODO Auto-generated method stub
		
	}
}
