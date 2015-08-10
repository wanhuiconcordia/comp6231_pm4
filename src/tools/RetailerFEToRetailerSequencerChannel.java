package tools;

public class RetailerFEToRetailerSequencerChannel extends Channel {

	public RetailerFEToRetailerSequencerChannel(String localProcessName, String peerProcessName,
			String peerHost, int peerPort) {
		super(localProcessName, peerProcessName, peerHost, peerPort);
	}

	public void processMessage(Message msg){
		if(isValidSequence(msg)){
			peerSeq = msg.senderSeq;
			switch(msg.action){
			case ACK:
				hasCachedMsg = false;
				break;
			case INIT:
				cachedMsg = new AckMessage(localProcessName, ++localSeq, msg.senderSeq);
				hasCachedMsg = true;
				break;
			case getCatelog:
				cachedMsg = new AckMessage(localProcessName, ++localSeq, msg.senderSeq);
				hasCachedMsg = true;
				//TODO ChannelManager.dispatchFEMessage(msg);
				break;
			case signIn:
				cachedMsg = new AckMessage(localProcessName, ++localSeq, msg.senderSeq);
				hasCachedMsg = true;
				//TODO ChannelManager.dispatchFEMessage(msg);
				break;
			case signUp:
				cachedMsg = new AckMessage(localProcessName, ++localSeq, msg.senderSeq);
				hasCachedMsg = true;
				//TODO ChannelManager.dispatchFEMessage(msg);
				break;
			case submitOrder:
				cachedMsg = new AckMessage(localProcessName, ++localSeq, msg.senderSeq);
				hasCachedMsg = true;
				//TODO ChannelManager.dispatchFEMessage(msg);
				break;
			default:
				System.out.println("Unrecognizable action");
				break;
			}
		}
	}
}
