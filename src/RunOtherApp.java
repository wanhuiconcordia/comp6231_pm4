import java.io.IOException;


public class RunOtherApp {

	public static void main(String[] args) {
		try {
//			Process p = Runtime.getRuntime().exec("./start_logger_server.sh");
			Process p = Runtime.getRuntime().exec("./startRetailerRM.sh");
//			Process p = Runtime.getRuntime().exec("gnome-terminal -x sh -c './startRetailerRM.sh; bash'&");
//			Process p = Runtime.getRuntime().exec("gnome-terminal -x sh -c './ls; bash'");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
