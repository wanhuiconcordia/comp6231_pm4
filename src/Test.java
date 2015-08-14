import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Vector;

import tools.SuplyChainObjComparator;


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
		tools.Product comp1 = new tools.Product("Apple", "TV", 32);
		tools.Product comp2 = new tools.Product("Samsung", "TV", 32);
		tools.Product comp3 = new tools.Product("Apple", "TV", 32);
		
		
		System.out.println(comp1.isSame(comp2));
		System.out.println(comp1.isSame(comp3));
		
		tools.Item comp4 = new tools.Item("Apple", "TV", 32, 10);
		tools.Item comp5 = new tools.Item("Samsung", "TV", 32, 10);
		tools.Item comp6 = new tools.Item("Apple", "TV", 32, 10);
		
		
		System.out.println(comp4.isSame(comp5));
		System.out.println(comp4.isSame(comp6));
//		
//		Process p = Runtime.getRuntime().exec("gedit");
//		System.out.println(p.toString());
		
//		Scanner in = new Scanner(System.in);
//		in.next();
//		System.out.println("After Waiting...");
////		p.destroy();
	}

}
