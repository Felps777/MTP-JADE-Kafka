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
 * JMS implementation of a Transport Address
 * 
 * <p>
 * Used to represent a transport address in the JMS-MTP
 * </p>
 * 
 */
package demo;

import jade.mtp.MTPException;
import jade.mtp.TransportAddress;

public class TCPAddress implements TransportAddress {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9163465094196353499L;
	private String protocol = "tcp"; // protocols supported by this address
	private String providerType = "kafka"; // format of the JMS Provider
	private String msgType; // Format of message to be used on this address
	private String msgPersistence; // Message Persistence Setting for this Address
	private String username; // Username used to access queue
	private String password; // Password used to access queue
	private String brokerURL;
//	private String queueName;
	private String topicName;
	private String groupID;


	public TCPAddress(String addy) throws MTPException {

//		if (log.isDebugEnabled()) {
		System.out.println("Creating specific TA:" + addy);
//		}

		try {

			if (!addy.startsWith("tcp:")) {
				throw new MTPException("Missing 'tcp:' " + addy);
			}

			protocol = "tcp";

			int endOfProtocol = addy.indexOf(':') + 1;

			// Provider Type: eg.'jossmq'
			int endOfProviderType = addy.indexOf(':', 4);
			providerType = addy.substring(4, endOfProviderType);

			if (providerType.trim().equals("")) {
				throw new MTPException("Missing provider format");
			}

			// Message Type: eg. 'xml'
			int endOfMsgType = addy.indexOf(':', endOfProviderType + 1);
			msgType = addy.substring(endOfProviderType + 1, endOfMsgType);

			if (msgType.trim().equals("") || ((!msgType.equals(KfkMtpConfig.MSG_XML))
					&& (!msgType.equals(KfkMtpConfig.MSG_MAP)) && (!msgType.equals(KfkMtpConfig.MSG_JSON)))) {
				throw new MTPException("Missing  or invalid message type: " + msgType);
			}

//			 Broker URL: '127.0.0.1:1099'
			int brokerUrlEnd = addy.indexOf('/', endOfProtocol + 1);
			brokerURL = addy.substring(endOfProtocol + 10, brokerUrlEnd);

			// Queue Name : 'queue/jade/159.134.244.58'
			topicName = addy.substring(brokerUrlEnd + 1);
			if (topicName.trim().equals("")) {
				throw new MTPException("Missing topicName ");
			}

		} catch (Exception e) {
			System.out.println("Error in supplied bTA, Default setting assigned");
			throw new MTPException("Invalid TCP Address': " + addy, e);
		}
	}

	/**
	 * Creates a new JMSAddress object.
	 * 
	 * @throws MTPException Error with Address Activation
	 */
	public TCPAddress() throws MTPException {

//		if (log.isDebugEnabled()) {

		// Create a default address using the default address
		System.out.println("Create TA with Defaults");
//		}

		protocol = "tcp";
		providerType = KfkMtpConfig.DEFAULT_PROVIDER_TYPE;
		msgType = KfkMtpConfig.DEFAULT_MSG_TYPE;
//		msgPersistence = JmsMtpConfig.DEFAULT_MSG_PERSISTENCE;
//		username = JmsMtpConfig.DEFAULT_USERNAME;
//		password = JmsMtpConfig.DEFAULT_PASSWORD;
		brokerURL = KfkMtpConfig.DEFAULT_BROKER_URL;
//		queueName = JmsMtpConfig.DEFAULT_QUEUE_NAME;
		topicName = KfkMtpConfig.DEFAULT_TOPIC_NAME;
	}

	/**
	 * Return a String repsentation of this JMSAddress
	 * 
	 * @return String representation of a JMSAddress
	 */
	public String getString() {

//		if (log.isDebugEnabled()) {
		System.out.println("Get String of TA");
//		}

		return protocol + ":" + providerType + ":" + msgType + ":" + brokerURL + "/" + topicName;
//		return protocol + ":" + providerType + ":" + msgType + ":" + msgPersistence + ":" + username + ":" + password
//				+ ":" + brokerURL + "/" + topicName;
	}

	/**
	 * Returns the JMS Provider used by this address
	 * 
	 * @return JMS Provider for this Address
	 */
	public String getProviderType() {

		return providerType;
	}

	/**
	 * Message Type used by this address XML or MapMessage
	 * 
	 * @return Message Type used by this Address
	 */
	public String getMsgType() {

		return msgType;
	}

	/**
	 * Message Persistence setting of this address
	 * 
	 * @return Setting of this Address
	 */
	public String getMsgPersistence() {

		return msgPersistence;
	}

	/**
	 * Username to access the boker/queue (Optional)
	 * 
	 * @return Usename of this Address
	 */
	public String getUsername() {

		return username;
	}

	/**
	 * Password used to access broker/queue (Optional)
	 * 
	 * @return Password of this Address
	 */
	public String getPassword() {

		return password;
	}

	/**
	 * URL Connection information of Broker
	 * 
	 * @return BrokerURL for this Address
	 */
	public String getBrokerURL() {

		return brokerURL;
	}

	/**
	 * Queue releated to this Address
	 * 
	 * @return queue to listen or send to
	 */
//	public String getQueueName() {
//
//		return queueName;
//	}

	/**
	 * Protocol used by this Address
	 * 
	 * @return protocol of this address
	 */
	public String getProto() {

		return protocol;
	}

	/**
	 * Hostname of the broker for this address
	 * 
	 * @return Brokers host name
	 */
	public String getHost() {

		return brokerURL.substring(0, brokerURL.length() - 5);
	}

	/**
	 * Not used in Address
	 * 
	 * @return null
	 */
	public String getPort() {

		return "";
	}

	/**
	 * Not used in Address
	 * 
	 * @return null
	 */
	public String getFile() {

		return "";
	}

	/**
	 * Not used in Address
	 * 
	 * @return null
	 */
	public String getAnchor() {

		return "";
	}

	public String getTopicName() {
		return topicName;
	}

	public String getGroupID() {
		return groupID;
	}

}
