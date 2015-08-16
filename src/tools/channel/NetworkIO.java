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
import java.net.SocketTimeoutException;
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
	
	public Message receiveMessage() throws SocketTimeoutException{
		byte[] incomingData = new byte[1024 * 8];
		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
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
			throw new java.net.SocketTimeoutException();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public boolean sendMsg(Message msg, String receiverHost, int receiverPort){
//		System.out.println("NetworkIO::sendMsg():" + msg + " to " + receiverHost + ":" + receiverPort);
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
