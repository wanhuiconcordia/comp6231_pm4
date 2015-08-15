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

public class RetailerRMMessageProcesser extends MessageProcesser{
	public Process replicaProcess;
	public String runReplicaCmd;
	public String fullRunReplicaCmd;
	public int index;
	public int failCount;
	public RetailerRMMessageProcesser(String runReplicaCmd, int index){
		this.runReplicaCmd = runReplicaCmd;
		this.index = index;
		fullRunReplicaCmd = runReplicaCmd + " " + index + " NONE"; 
		try {
			replicaProcess = Runtime.getRuntime().exec(fullRunReplicaCmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
						// TODO Auto-generated catch block
						e.printStackTrace();
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
					// TODO Auto-generated catch block
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
}
