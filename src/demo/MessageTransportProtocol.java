/**
 * Interface point for JMS-MTP and JADE platform
 * 
 * <p>
 * Handles the send and receving of JMS messages to and from the JADE platform
 * </p>
 * 
 */
package demo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jade.core.Profile;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.mtp.MTP;
import jade.mtp.MTPException;
import jade.mtp.TransportAddress;

public class MessageTransportProtocol implements MTP {

	private static HashMap providerManagers; // HashMap of connections to message brokers
	private static final Logger log = Logger.getLogger(MessageTransportProtocol.class.getName());
	static {

		// Load in config info
//    String resource = "/log4j-mtp.properties";
//    URL configFileResource = MessageTransportProtocol.class.getResource(
//                                   resource);
//    PropertyConfigurator.configure(configFileResource);

		JmsMtpConfig temp = new JmsMtpConfig();
//    log = Category.getRoot();

		log.log(Level.INFO, "Init of KAFKA-MTP");
		log.log(Level.INFO, "Starting MTP configuration");

		providerManagers = new HashMap();
	}

	@Override
	public TransportAddress activate(Dispatcher disp, Profile p) throws MTPException {
		log.log(Level.INFO, "Default Activate Called");
		log.log(Level.INFO, "Create a Transport Address");

		TCPAddress jmsTA = new TCPAddress();

		try {

			log.log(Level.INFO, "Create a Default QL");

			this.activate(disp, jmsTA, p);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error in Addition:" + e.toString());
			throw new MTPException("Error during default activation: ", e);
		}

		log.log(Level.INFO, "Returning TA: " + jmsTA.getString());

		return (TransportAddress) jmsTA;
	}

	@Override
	public void activate(Dispatcher disp, TransportAddress ta, Profile p) throws MTPException {
		log.log(Level.INFO, "Activate on a specific Transport Address");

		try {

			TCPAddress jmsTA = (TCPAddress) ta;

			log.log(Level.INFO, "Create a specific QL: " + jmsTA.getString());

			getProviderManager(jmsTA).activate(disp, jmsTA);
		} catch (Exception e) {
			throw new MTPException("Error during address specific activation: ", e);
		}

	}

	/**
	 * Deactivate a specific address
	 * 
	 * @param ta Address to deactivate
	 * @throws MTPException Error during address deactivation
	 */
	public void deactivate(TransportAddress ta) throws MTPException {

		log.log(Level.INFO, "Deactivate specific TA");

		try {

			TCPAddress jmsTA = (TCPAddress) ta;

			log.log(Level.INFO, "Remove QL for ta");

			getProviderManager(jmsTA).deactivate(jmsTA);
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

		this.closeProviderManagers();

		log.log(Level.INFO, "Remove all QLs");

		try {
			this.closeProviderManagers();
		} catch (Exception e) {
			throw new MTPException("Error in deactivating MTP: ", e);
		}
	}

	/**
	 * Deliver a message to a jmsTA
	 * 
	 * @param addr    Address to deliver too
	 * @param env     Envelope of message
	 * @param payload Message payload
	 * @throws MTPException Error during message send
	 */
	public void deliver(String addr, Envelope env, byte[] payload) throws MTPException {

		log.log(Level.INFO, "Deliver a Message to an address");

		try {

			log.log(Level.INFO, "Create the TA");

			TCPAddress jmsTA = new TCPAddress(addr);
			getProviderManager(jmsTA).deliver(jmsTA, env, payload);
		} catch (ClassCastException cce) {
			log.log(Level.SEVERE, "Invaild JMS Address");
			throw new MTPException("Address mismatch: this is not a valid JMS address.", cce);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error in sending: " + e.toString());
			throw new MTPException("Error sending the Message: ", e);
		}
	}

	/**
	 * Used to create a new broker connection or to retrive one from the hashmap
	 * 
	 * @param jmsTA Address of the Broker
	 * @return QueueConnection A QueueConnection object for the broker in jmsTA
	 * @throws Exception Error during manager creation
	 */
	public JmsProviderManager getProviderManager(TCPAddress jmsTA) throws Exception {

		// check to see if the Queue connection has been connected
		if (!providerManagers.containsKey(jmsTA.getProviderType())) {
			setupProviderManager(jmsTA);
		}

		return (JmsProviderManager) providerManagers.get(jmsTA.getProviderType());
	}

	/**
	 * Create a QueueConnection to a specified broker and place it in the hashmap
	 * 
	 * @param jmsTA Contains details of the broker
	 * @throws Exception Error during provider setup
	 */
	private void setupProviderManager(TCPAddress jmsTA) throws MTPException {

		log.log(Level.INFO, "Creating a connection to broker: " + jmsTA.getProviderType());

		try {

			JmsProviderManager pm = new ProviderManager();

			log.log(Level.INFO, "Placing the connection into the hashmap");

			providerManagers.put(jmsTA.getProviderType(), pm);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to create connection to queue:" + e.toString());
			throw new MTPException("Failed to create connection to queue: " + jmsTA.toString(), e);
		}
	}

	/**
	 * Close all connections in the hashmap
	 */
	private void closeProviderManagers() {

		if (true) {
			log.log(Level.INFO, "Close all connections in the hashmap");
		}

		Set pms = providerManagers.entrySet();

		for (Iterator i = pms.iterator(); i.hasNext();) {

			JmsProviderManager temp = (JmsProviderManager) i.next();

			try {
				temp.deactivate();
			} catch (Exception e) {

				// Ignore the closing errors
			}
		}

		providerManagers.clear();
	}

	/**
	 * Converts a string to a JMS Transport address
	 * 
	 * @param rep Contains the address as a string
	 * @return TransportAddress The address as a JMS Transport Address
	 * @throws MTPException Error in Address conversion
	 */
	public TransportAddress strToAddr(String rep) throws MTPException {

		if (true) {
			log.log(Level.INFO, "Convert String to TA");
		}

		return new TCPAddress(rep);
	}

	/**
	 * Given a TransportAddress convert it to a string
	 * 
	 * @param ta The Address as a TransportAddress object
	 * @return String Address as a string
	 * @throws MTPException Error in Address conversion
	 */
	public String addrToStr(TransportAddress ta) throws MTPException {

		if (true) {
			log.log(Level.INFO, "Convert TA to String");
		}

		try {

			TCPAddress jmsTA = (TCPAddress) ta;

			return jmsTA.getString();
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			throw new MTPException("Address mismatch: this is not a valid JMS address.");
		}
	}

	/**
	 * Return the name of this MTP
	 * 
	 * @return MTP Name
	 */
	public String getName() {

		return "kafka";
	}

	/**
	 * Get prtocols supported by this MTP
	 * 
	 * @return The Protocols supported by this MTP
	 */
	public String[] getSupportedProtocols() {

		return new String[] { "tcp" };
	}

}
