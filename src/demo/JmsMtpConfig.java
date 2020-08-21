/**
 * Reads the properties file and control the configuration for the JMS-MTP
 * 
 * <p>
 * Used to configure the JMS-MTP and store setting for the plugable Provider
 * admin objects
 * </p>
 */
package demo;

import java.util.Properties;
import java.util.logging.Logger;

public class JmsMtpConfig {

	private static final Logger log = Logger.getLogger(JmsMtpConfig.class.getName());
	private static Properties props = new Properties();

	// Default Setting read in from the Configuration file
//	public static String DEFAULT_MSG_PERSISTENCE;
	public static String DEFAULT_MSG_TYPE;
	public static String DEFAULT_PROVIDER_TYPE;
	public static String DEFAULT_BROKER_URL;
//	public static String DEFAULT_QUEUE_NAME;
	public static String DEFAULT_TOPIC_NAME;
	public static String DEFAULT_USERNAME;
	public static String DEFAULT_PASSWORD;

	// Constants used within the JMS-MTP
	public final static String MSG_XML = "xml";
	public final static String MSG_MAP = "map";
	public final static String MSG_JSON = "json";
//	public final static String MSG_NON_PERSISTENT = "non_persistent";

	/** stores the packagename of use with keys in the propertyfile */
//	private static String packageName = "ie.nuigalway.ecrg.jade.jmsmtp.";

	// ConfigJadeAgents configJadeAgents;

	/**
	 * Constructor which loads in the configuration file
	 */
	public JmsMtpConfig() {

		// default providerFormat setting
		DEFAULT_PROVIDER_TYPE = "kafka";

		// default brokerURL setting
		DEFAULT_BROKER_URL = "127.0.0.1:29092";

		// default Message Type setting
		// String msgType = MSG_XML;
		DEFAULT_MSG_TYPE = MSG_JSON;

//		if ((!msgType.equals(MSG_XML)) && (!msgType.equals(MSG_MAP))) {
//			DEFAULT_MSG_TYPE = MSG_JSON;
//		}

		// default queueName setting
		// DEFAULT_QUEUE_NAME = "tpcEvents/127.0.0.1:1098";
		DEFAULT_TOPIC_NAME = "tpcEvents";
	}

	/**
	 * Given a key return its value from the configfile
	 * 
	 * @param key          Key to search for in the prop file
	 * @param defaultValue Default value to use if not found
	 * @return Value from propfile or default value
	 */
	public static String getProperty(String key, String defaultValue) {

		return props.getProperty(key, defaultValue).trim();
	}
}
