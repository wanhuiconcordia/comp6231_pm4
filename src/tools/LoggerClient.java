package tools;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * @author comp6231.team5
 *
 */
public class LoggerClient {
	boolean connected = false;
	private Socket clientSocket;
	private DataOutputStream dataOutputStream;
	
	/**
	 * constructor
	 * load the logger_client_settings.conf file, and create a new clientSocket according to the info in the file
	 * create a dataOutputStream according to the clientSocket
	 */
	public LoggerClient(){
		//ConfigureManager.getInstance().loadFile("./settings/settings.conf");
		try {
			clientSocket = new Socket(ConfigureManager.getInstance().getString("loggerServerName", "localhost"),
					ConfigureManager.getInstance().getInt("loggerServerPort", 2020));
			OutputStream outputStream = clientSocket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);
			connected = true;
		} catch (IOException e) {
			connected = false;
			e.printStackTrace();
		}
	}

	/**
	 * write message to the outputStream of the current clientSocket
	 * @param msg
	 * @return true if write message successfuly, false if IOException caught
	 */
	public boolean write(String msg){
		if(connected){
			try {
				dataOutputStream.writeUTF(msg);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * close the corrent connection
	 * @return turn if close successfully, false if Exception caught
	 */
	public boolean close(){
		if(connected){
			try {
				connected = false;
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
		}
		return true;
	}
}
