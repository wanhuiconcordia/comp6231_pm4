package rm;
import java.net.InetAddress;
import tools.ConfigureManager;

public class RetailerRMApp {

	public static void main(String[] args) {
		if(args.length == 1){
			String lastPara = args[args.length - 1];

			if(lastPara.equals("1")
					|| lastPara.equals("2")
					|| lastPara.equals("3")
					|| lastPara.equals("4"))
			{
				String localIp;
				try {
					localIp = InetAddress.getLocalHost().getHostAddress();

					String sequencerHost = ConfigureManager.getInstance().getString("RetailerSequencerHost");
					if(localIp.equals(sequencerHost)){
						int index = Integer.parseInt(lastPara);
						RetailerRM retailerSequencer = new RetailerRM(index);					
					}else{
						System.out.println("Please run the RetailerSequencerHost on:" + sequencerHost + " or change the RetailerSequencerHost of configure file to:" + localIp);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}


			}else{
				System.out.println("Wrong index! Please specify the index of retailer replica manager(1-4).");
			}
		}else{
			System.out.println("One parameter is required for the index of retailer replica manager(1-4).");
		}
	}
}
