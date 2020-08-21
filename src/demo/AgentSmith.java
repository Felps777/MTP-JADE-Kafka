package demo;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentSmith extends Agent {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {

		addBehaviour(new CyclicBehaviour(this) {

			private static final long serialVersionUID = 1L;

			public void action() {
				ACLMessage msg = myAgent.receive();
				if (msg != null) {

					String content = msg.getContent();
					if (content != null) {
						System.out.println("Received Request from " + msg.getSender().getLocalName());
						System.out.println("Received Message : " + content);
					}

				}
			}
		});

		System.out.println("Setup done!");
	}
}
