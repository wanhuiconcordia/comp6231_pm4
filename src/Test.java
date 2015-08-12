import java.util.Scanner;


public class Test {

	public static void main(String[] args) {
		Channel channel = new Channel();
		System.out.println("count:" + channel.count++);
		System.out.println("count:" + channel.count++);
		System.out.println("count:" + channel.count++);
		
		Scanner in = new Scanner(System.in);
		in.next();
		System.out.println("After Waiting...");
	}

}

class Channel{
	public int count = 0;
}