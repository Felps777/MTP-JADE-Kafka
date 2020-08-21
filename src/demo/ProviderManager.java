/*
 * (c) Copyright Enterprise Computing Research Group (ECRG),
 *               National University of Ireland, Galway 2003.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE and
 * no warranty that the program does not infringe the Intellectual Property rights of a third party.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

/**
 * Interface point for JMS-MTP and MTP Booter class
 * 
 * <p>
 * Handles the send and receving of JMS messages to and from the JADE platform
 * </p>
 * 
 */
package demo;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jade.domain.FIPAAgentManagement.Envelope;
import jade.mtp.InChannel.Dispatcher;
import otros.MapMessageUtil;
import jade.mtp.MTPException;

public class ProviderManager implements JmsProviderManager{

	private static final Logger log = Logger.getLogger(ProviderManager.class.getName());
//	private QLManager qlManager; // Use to create queue listeners
	private FipaXMLUtil xmlUtil; // Utility used for FIPA XML messages
	private MapMessageUtil mapUtil; // Utility used for JMS MapMessages
//	public ProviderAdmin providerAdmin;
	private HashMap brokerConnections; // HashMap of connections to JMS broker
	private ConsumersManager cMan;
	private ProducerMananger pMAn;
	private String topico;
	private String grupoId;

	/**
	 * Creates a new ProviderManager object.
	 */
	public ProviderManager() {
		// topico = topic;
		// grupoId = groupId;
		xmlUtil = new FipaXMLUtil();
		mapUtil = new MapMessageUtil();
		brokerConnections = new HashMap();
//		qlManager = new QLManager();
		cMan = new ConsumersManager();
		pMAn = new ProducerMananger();

	}

	public void activate(Dispatcher disp, TCPAddress jmsTA) throws MTPException {

		log.log(Level.INFO, "Activate on a specific Transport Address");

		try {

			log.log(Level.INFO, "Create a specific QL: " + jmsTA.getString());

//			qlManager.addQL(this.getBrokerConnection(jmsTA), disp, jmsTA);
			cMan.addConsumer(disp, jmsTA);
			cMan.startListener(jmsTA.getTopicName());
		} catch (Exception e) {
			throw new MTPException("Error during address specific activation: ", e);
		}

	}

	/**
	 * Deactivate a specific address
	 * 
	 * @param jmsTA Address to deactivate
	 * @throws MTPException Error during address deactivation
	 */
	public void deactivate(TCPAddress jmsTA) throws MTPException {
		log.log(Level.INFO, "Deactivate specific TA");

		try {

			log.log(Level.INFO, "Remove QL for ta");

			cMan.removeListener(jmsTA.getTopicName());// (jmsTA.getString());
		} catch (Exception e) {
			throw new MTPException("Error deactivating Transport Address: ", e);
		}

	}

	/**
	 * Deactivate the MTP
	 * 
	 * @throws MTPException Error with MTP deactivation
	 */
	public void deactivate() throws MTPException {

		log.log(Level.INFO, "Remove all Connections to JMS Providers");
		log.log(Level.INFO, "Remove all QLs");

		try {
			//this.closeBrokerConnections();
			cMan.removeAllListeners();
		} catch (Exception e) {
			throw new MTPException("Error in deactivating MTP: ", e);
		}
	}

	/**
	 * Deliver a message to a jmsTA
	 * 
	 * @param jmsTA   Address to deliver too
	 * @param env     Envelope of message
	 * @param payload Message payload
	 * @throws MTPException Error during message send
	 */
	public void deliver(TCPAddress jmsTA, Envelope env, byte[] payload) throws MTPException {

		log.log(Level.INFO, "Deliver a Message to an address");

		try {

			log.log(Level.INFO, "Create connection to the JMS Server");

			String message;

			if (jmsTA.getMsgType().equals(JmsMtpConfig.MSG_XML)) {
				log.log(Level.INFO, "Build FIPA XML envelope");
				message = xmlUtil.encode(env, payload);
			} else {
//				log.log(Level.INFO, "Build MapMessage");
//				message = mapUtil.encode((MapMessage) message, env, payload);
				env.setPayloadLength((long) payload.length);
				message = env.toString();
			}

			pMAn.getSenderTemplate_String().send(jmsTA.getTopicName(), message);

//			QueueConnection conn = this.getBrokerConnection(jmsTA);
//			QueueSession session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
//			Queue que = session.createQueue(jmsTA.getQueueName());
//			QueueSender send = session.createSender(que);
//
//			log.log(Level.INFO, "Setting Persistence");
//
//			if (jmsTA.getMsgPersistence().equals(JmsMtpConfig.MSG_PERSISTENT)) {
//				send.setDeliveryMode(DeliveryMode.PERSISTENT);
//			} else {
//				send.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//			}
//
//			log.log(Level.INFO, "Create the Message");
//
//			Message message;
//
//			if (jmsTA.getMsgType().equals(JmsMtpConfig.MSG_XML)) {
//				log.log(Level.INFO, "Build FIPA XML envelope");
//				message = session.createTextMessage(xmlUtil.encode(env, payload));
//			} else {
//				log.log(Level.INFO, "Build MapMessage");
//				message = session.createMapMessage();
//				mapUtil.encode((MapMessage) message, env, payload);
//			}
//
//			log.log(Level.INFO, "Send the Message");
//
//			send.send(message);
//			send.close();
//			session.close();
		} catch (ClassCastException cce) {
			log.log(Level.SEVERE, "Invaild JMS Address");
			throw new MTPException("Address mismatch: this is not a valid JMS address.", cce);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error in sending: " + e.toString());
			throw new MTPException("Error sending the Message: ", e);
		}
	}

}
