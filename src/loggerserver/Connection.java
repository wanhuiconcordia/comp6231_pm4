package loggerserver;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
/**
 * Connection maintains the connection to logger server
 * @author comp6231.team5
 */
public class Connection extends Thread{
	private Socket socket;
	private boolean keepWorking;
	private boolean isWorking;
	private String remoteHost;
	private LoggerWriter loggerWriter;
	private DataInputStream in;	
	
	/**
	 * Constructor
	 * @param socket_t
	 * @param loggerWriter
	 * @throws IOException
	 */
	public Connection(Socket socket_t, LoggerWriter loggerWriter) throws IOException{
		keepWorking = true;
		isWorking = false;
		this.socket = socket_t;
		socket.setSoTimeout(100);
		
		remoteHost = socket.getRemoteSocketAddress().toString();
		in = new DataInputStream(socket.getInputStream());
		this.loggerWriter = loggerWriter;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		isWorking = true;
		while(keepWorking){
			try {
				loggerWriter.write(remoteHost, in.readUTF());
			}catch(java.net.SocketTimeoutException e)
			{
				//System.out.println("time out...");
			}
			catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Remote socket closed.");
				keepWorking = false;
			}
		}
		isWorking = false;
	}
	
	/**
	 * cleanup will wait for reading thread to finish its job then close socket
	 */
	public void cleanup() {
		keepWorking = false;
		while(isWorking){
			System.out.println("Waiting for this thread to finish...");
			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try{
			socket.close();
		}catch(IOException e){
//			e.printStackTrace();
			System.out.println("Exception on close socket!");
		}
	}
	
	/**
	 * status shows is reading thread still working or not
	 */
	public void status(){
		if(isWorking){
			System.out.println("Connection is active to receive message from:" + remoteHost);
		}else{
			System.out.println("Connection is not active to receive message from:" + remoteHost);
		}
	}
}
