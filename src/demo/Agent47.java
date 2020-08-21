package demo;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class Agent47 extends Agent {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		AID dest = new AID("AgentSmith@Plat2", AID.ISGUID);
		dest.addAddresses("tcp://192.168.0.7:7778/tpcEvents");
		msg.addReceiver(dest);
		msg.setContent("Hello!");
		send(msg);
	}
}
