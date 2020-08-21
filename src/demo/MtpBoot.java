package demo;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.mtp.InChannel;
import jade.mtp.MTP;
import jade.mtp.MTPException;
import jade.mtp.TransportAddress;

/**
 * Boot Class used to load in MTP Creation
 * 
 * <p>
 * Handles the loading of relevant library files for the MTP and starts up the
 * MTP
 * </p>
 */
public class MtpBoot implements MTP {

	private static MTP mtp; // Created MTP

	// Load the library files for this MTP
	static {

		Properties props = null;

//		try {
//
//			String resource = "/jms-mtp.properties";
//			props = new Properties();
//			props.load(MtpBoot.class.getResourceAsStream(resource));
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Error loading the MTP configuration file: Could not find /jms-mtp.properties"
//					+ ", is it on the classpath? " + e.toString());
//		}

		try {

//			ClassLoader urlCl = URLClassLoader
//					.newInstance(getLibJars(props.getProperty("ie.nuigalway.ecrg.jade.jmsmtp.lib").trim() + "/common"));

			// Startup MTP
//			mtp = (MTP) Class.forName(MessageTransportProtocol.class.getName(), false, urlCl).newInstance();
			mtp = (MTP) Class.forName(MessageTransportProtocol.class.getName()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error loading the MTP Library: Does the location '"
					+ props.getProperty("ie.nuigalway.ecrg.jade.jmsmtp.lib").trim() + "/common' contain the libraries? "
					+ e.toString());
		}
	}
//
//	/**
//	 * Activate a default Address
//	 * 
//	 * @param disp Passed messages to the platform
//	 * @return Trnasport Address for the address activated
//	 * @throws MTPException Error during address activation
//	 */
//	public TransportAddress activate(InChannel.Dispatcher disp) throws MTPException {
//
//		return mtp.activate(disp,new ProfileImpl());
//	}
//
//	/**
//	 * Activate a specific TransportAddress
//	 * 
//	 * @param disp Used to pass messages to the platform
//	 * @param ta   Address to activate
//	 * @throws MTPException Error during address activation
//	 */
//	public void activate(InChannel.Dispatcher disp, TransportAddress ta) throws MTPException {
//		mtp.activate(disp, ta, new ProfileImpl());
//	}

	@Override
	public TransportAddress activate(Dispatcher disp, Profile p) throws MTPException {
		return mtp.activate(disp, p);
	}

	@Override
	public void activate(Dispatcher disp, TransportAddress ta, Profile p) throws MTPException {
		mtp.activate(disp, ta, p);

	}

	/**
	 * Deactivate a specific address
	 * 
	 * @param ta Address to deactivate
	 * @throws MTPException Error during address deactivation
	 */
	public void deactivate(TransportAddress ta) throws MTPException {
		mtp.deactivate(ta);
	}

	/**
	 * Deactivate the MTP
	 * 
	 * @throws MTPException Error with MTP deactivation
	 */
	public void deactivate() throws MTPException {
		mtp.deactivate();
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
		mtp.deliver(addr, env, payload);
	}

	/**
	 * Converts a string to a JMS Transport address
	 * 
	 * @param rep Contains the address as a string
	 * @return TransportAddress The address as a JMS Transport Address
	 * @throws MTPException Error During Address conversion
	 */
	public TransportAddress strToAddr(String rep) throws MTPException {

		return mtp.strToAddr(rep);
	}

	/**
	 * Given a TransportAddress convert it to a string
	 * 
	 * @param ta The Address as a TransportAddress object
	 * @return String Address as a string
	 * @throws MTPException Error during Address conversion
	 */
	public String addrToStr(TransportAddress ta) throws MTPException {

		return mtp.addrToStr(ta);
	}

	/**
	 * Return the name of this MTP
	 * 
	 * @return MTP Name
	 */
	public String getName() {

		return mtp.getName();
	}

	/**
	 * Get prtocols supported by this MTP
	 * 
	 * @return The Protocols supported by this MTP
	 */
	public String[] getSupportedProtocols() {

		return mtp.getSupportedProtocols();
	}

	/**
	 * Reads a given directory and returns the contents as a URL []
	 * 
	 * @param lib JMS Provider Type to learn
	 * @return A URL [] of the contents of the directory
	 * @throws Exception Error reading in Librarys
	 */
	private static URL[] getLibJars(String lib) throws Exception {

		File libDir = new File(lib);
		File[] libFileList = {};
		URL[] libURLList;

		if (libDir.isDirectory()) {
			libFileList = libDir.listFiles();
		} else {
			throw new Exception("Location: " + lib + " is not a directory");
		}

		libURLList = new URL[libFileList.length];

		for (int ii = 0; ii < libFileList.length; ii++) {

			try {
				libURLList[ii] = libFileList[ii].toURL();
			} catch (Exception e) {
				System.out.print("Error loading the following jar file:" + e.toString());
			}
		}

		return libURLList;
	}
}
