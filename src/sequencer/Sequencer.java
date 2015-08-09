package sequencer;
import tools.Channel;
import tools.ConfigureManager;
import tools.NetworkIO;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Sequencer{
	String name;
	int port;
	HashMap<String, Channel> channelMap;
	ReadThread readThread;
	WriteThread writeThread;
	NetworkIO networkIO;
	
	public Sequencer() throws Exception{
		name = "Sequencer";
		channelMap = new HashMap<String, Channel>();

		channelMap.put("RetailerFE"
				, new Channel("RetailerFE"
						, ConfigureManager.getInstance().getString("RetailerFEHost")
						, ConfigureManager.getInstance().getInt("RetailerFEPort")));
		
		channelMap.put("RetailerReplica1"
				, new Channel("RetailerReplica1"
						, ConfigureManager.getInstance().getString("RetailerReplicaHost1")
						, ConfigureManager.getInstance().getInt("RetailerReplicaPort1")));
		
		channelMap.put("RetailerReplica2"
				, new Channel("RetailerReplica2"
						, ConfigureManager.getInstance().getString("RetailerReplicaHost2")
						, ConfigureManager.getInstance().getInt("RetailerReplicaPort2")));
		
		channelMap.put("RetailerReplica3"
				, new Channel("RetailerReplica3"
						, ConfigureManager.getInstance().getString("RetailerReplicaHost3")
						, ConfigureManager.getInstance().getInt("RetailerReplicaPort3")));
		
		channelMap.put("RetailerReplica4"
				, new Channel("RetailerReplica4"
						, ConfigureManager.getInstance().getString("RetailerReplicaHost4")
						, ConfigureManager.getInstance().getInt("RetailerReplicaPort4")));
		readThread = new ReadThread(this);
		writeThread = new WriteThread(this);
		
		networkIO = new NetworkIO(ConfigureManager.getInstance().getInt("SequencerPort"));
		
		System.out.println("Sequencer is running on:"
		+ ConfigureManager.getInstance().getString("SequencerHost")
		+ ":" + ConfigureManager.getInstance().getInt("SequencerPort"));
	}

	public void start(){
		readThread.start();
		writeThread.start();
	}

	public void terminate(){
		readThread.terminate();
		writeThread.terminate();
	}
	
	public static void main(String []argv){
	
		try {
			String localIp = InetAddress.getLocalHost().getHostAddress();
			String sequencerHost = ConfigureManager.getInstance().getString("SequencerHost");
			if(!localIp.equals(sequencerHost)){
				System.out.println("Please run the sequencer on:" + sequencerHost + " or change the sequencerHost of configure file to:" + localIp);
				return;
			}
			Sequencer sequencer = new Sequencer();
			sequencer.start();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ReadThread extends Thread{
	Sequencer sequencer;
	boolean keepReading;
	public ReadThread(Sequencer sequencer){
		this.sequencer = sequencer;
		keepReading = true;
	}

	public void run(){
		while(keepReading){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("read thread is running...");
		}
	}
	
	public void terminate(){
		keepReading = false;
	}
}

class WriteThread extends Thread{
	Sequencer sequencer;
	boolean keepWriting;
	public WriteThread(Sequencer sequencer){
		this.sequencer = sequencer;
		keepWriting = true;
	}

	public void run(){
		while(keepWriting){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("write thread is running...");
		}
	}
	
	public void terminate(){
		keepWriting = false;
	}
}