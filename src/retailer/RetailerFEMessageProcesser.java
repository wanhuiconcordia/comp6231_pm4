package retailer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.AckMessage;
import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;

public class RetailerFEMessageProcesser extends MessageProcesser {

	@Override
	public void processNewRequest(ChannelManager channelManager, Channel channel, Message msg) {
		if(msg.action == Action.ACK){
			channel.isWaitingForRespose = false;
		}else{
			channel.receivedMessage = msg;
			channel.backupPacket = new Packet(channel.peerHost
					, channel.peerPort
					, new AckMessage(channel.localProcessName
							, ++channel.localSeq
							, msg.senderSeq));
			synchronized(channelManager.outgoingPacketQueueLock) {
				channelManager.outgoingPacketQueue.add(channel.backupPacket);
				System.out.println("put ack packet in outgoing queue:" + channel.backupPacket.toString());
			}

			switch(msg.action){
			case getCatelog:
			case signIn:
			case signUp:
			case submitOrder:
				break;
			default:
				System.out.println("Unrecognizable action");
				break;
			}
		}
	}

}
