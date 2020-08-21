/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package otros;

import demo.MessageTransportProtocol;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.mtp.MTPException;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * This example shows a minimal agent that just prints "Hallo World!" and then
 * terminates.
 * 
 * @author Giovanni Caire - TILAB
 */
public class HelloWorldAgent2 extends Agent {

	static Runtime runtime = Runtime.instance();
	static Profile profile = new ProfileImpl();

	protected void setup() {
		System.out.println("Hello World! My name is " + getLocalName());

		try {
			ContainerController home = getContainerController();
//			AgentController agc = home.createNewAgent("correos", TopicMessageSenderAgent.class.getName(), null);
			AgentController agc2 = home.createNewAgent("lector1", TopicMessageReceiverAgent.class.getName(), null);

			//
			// agc.start();
			// agc2.start();

			home.installMTP("tcp://127.0.0.1:8089", MessageTransportProtocol.class.getName());

			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.addReceiver(new AID("AgentReceiver", AID.ISLOCALNAME));
			message.setContent("Hello The World");
			send(message);
			
		} catch (StaleProxyException | MTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Make this agent terminate
		doDelete();
	}
}
