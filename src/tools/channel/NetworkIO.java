package tools.channel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import tools.message.Message;


public class NetworkIO {
	DatagramSocket datagramSocket;
	boolean keepWorking;

	public NetworkIO(int port) throws SocketException{
		keepWorking = true;
		datagramSocket = new DatagramSocket(port);
		datagramSocket.setSoTimeout(1000);
	}
	
	public Message receiveMessage(){
		byte[] incomingData = new byte[1024];
		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
		while(keepWorking){
			try {
				datagramSocket.receive(incomingPacket);

				byte[] data = incomingPacket.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is;

				is = new ObjectInputStream(in);

				Message msg;

				msg = (Message) is.readObject();
				
				System.out.println("Receive msg:" + msg);
				//packet.peerPort = incomingPacket.getPort();
				return msg;
			}catch(java.net.SocketTimeoutException e){
				//System.out.println("time out...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}
	
	public boolean sendMsg(Message msg, String receiverHost, int receiverPort){
		System.out.println("Send msg:" + msg + " to " + receiverHost + ":" + receiverPort);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream os;
		try {
			os = new ObjectOutputStream(outputStream);
			os.writeObject(msg);
			byte[] data = outputStream.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(receiverHost), receiverPort);
			datagramSocket.send(sendPacket);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
