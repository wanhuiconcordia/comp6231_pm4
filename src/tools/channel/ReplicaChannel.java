package tools.channel;

import rm.ReplicaStatus;

public class ReplicaChannel extends Channel {
	public ReplicaStatus replicaStatus;
	public ReplicaChannel(String localProcessName, String peerProcessName,
			String peerHost, int peerPort, Group group) {
		super(localProcessName, peerProcessName, peerHost, peerPort, group);
		this.replicaStatus = ReplicaStatus.REPLICA_GOOD;
	}
	
	public ReplicaChannel(String localProcessName, String peerProcessName,
			String peerHost, int peerPort, Group group, ReplicaStatus replicaStatus) {
		super(localProcessName, peerProcessName, peerHost, peerPort, group);
		this.replicaStatus = replicaStatus;
	}
}
