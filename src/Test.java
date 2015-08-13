import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Vector;


public class Test {
	public static String getPid() throws IOException,InterruptedException {

		  Vector<String> commands=new Vector<String>();
		  commands.add("/bin/bash");
		  commands.add("-c");
		  commands.add("echo $PPID");
		  ProcessBuilder pb=new ProcessBuilder(commands);

		  Process pr=pb.start();
		  pr.waitFor();
		  if (pr.exitValue()==0) {
		    BufferedReader outReader=new BufferedReader(new InputStreamReader(pr.getInputStream()));
		    return outReader.readLine().trim();
		  } else {
		    System.out.println("Error while getting PID");
		    return "";
		  }
		}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println(Test.getPid());
		
		Process p = Runtime.getRuntime().exec("gedit");
		System.out.println(p.toString());
		
		Scanner in = new Scanner(System.in);
		in.next();
		System.out.println("After Waiting...");
		p.destroy();
	}

}
