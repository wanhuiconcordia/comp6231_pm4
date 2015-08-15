import java.io.IOException;
import java.util.Scanner;


public class RunOtherApp {

	public static void main(String[] args) {
		try {
			Process p = Runtime.getRuntime().exec("java Test");
			Scanner in = new Scanner(System.in);
			in.next();
			System.out.println("After Waiting...");
			System.out.println(p.toString());
			p.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
