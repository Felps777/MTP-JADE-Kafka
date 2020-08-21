package demo;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.mtp.MTPException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main2 {

	public static void main(String[] args) {
//		String host = "127.0.0.1"; // Platform IP
//		int port = 1099; // default-port 1099

		String MTP_hostIP = "192.168.1.96"; // setting the same IP for both "172.16.200.100";
		String MTP_Port = "7778";

		Runtime runtime = Runtime.instance();
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.GUI, "true");
		profile.setParameter(Profile.PLATFORM_ID, "Plat2");

//		profile.setParameter(Profile.MTPS,
//				"demo.MessageTransportProtocol(tcp://" + MTP_hostIP + ":" + MTP_Port + "/acc)");

//		profile.setParameter(Profile.MAIN_HOST, "172.16.200.100");
//		profile.setParameter(Profile.MAIN_PORT, "1337");
		// profile.setParameter(Profile.CONTAINER_NAME, "container1");

		AgentContainer home = runtime.createMainContainer(profile);
//		tcp:kafka:json:192.168.0.6:7779/tcpEvents
		try {
			home.installMTP("tcp:kafka:json:" + MTP_hostIP + ":" + MTP_Port + "/tpcEvents",
					MessageTransportProtocol.class.getName());
			AgentController agc = home.createNewAgent("lector", AgentSmith.class.getName(), null);
			//agc.start();
		} catch (StaleProxyException | MTPException e) {
			e.printStackTrace();
		}
	}

}
