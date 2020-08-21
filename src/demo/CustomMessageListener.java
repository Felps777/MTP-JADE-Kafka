package demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

import jade.domain.FIPAAgentManagement.Envelope;
import jade.lang.acl.ACLMessage;
import jade.mtp.InChannel;
import otros.MapMessageUtil;

public class CustomMessageListener implements MessageListener<String, String> {

	private FipaXMLUtil xmlUtil;
	private MapMessageUtil mapUtil;
	private InChannel.Dispatcher dispatcher; // dispatcher used to pass messages to the platform
	private TCPAddress tAddress;
//	private ProviderAdmin pAdmin;

	// inject your own concrete processor
	// private IMessageProcessor messageProcessor;
	private static final Logger log = Logger.getLogger(CustomMessageListener.class.getName());

	public CustomMessageListener(InChannel.Dispatcher dispatcher, TCPAddress jmsTA) {

		this.dispatcher = dispatcher;
		this.tAddress = jmsTA;
		xmlUtil = new FipaXMLUtil();
		mapUtil = new MapMessageUtil();

//		pAdmin = new KafkaProviderAdmin();
	}

	@Override
	public void onMessage(ConsumerRecord<String, String> consumerRecord) {
		// process message
		// messageProcessor.process(consumerRecord.key(), consumerRecord.value());

		StringBuffer payload = new StringBuffer();
		Envelope env = new Envelope();

		String msg = consumerRecord.value();
		StringReader sr = new StringReader(msg);
		try {
			System.out.println(env.toString());
			Object obj =  new InputStream(sr).readObject();
			System.out.println(obj.toString());
			env = (Envelope)obj;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}

		ACLMessage al = new ACLMessage();
		

		// env = xmlUtil.decode(tm, payload);

		log.log(Level.INFO, "Message decode");

		dispatcher.dispatchMessage(env, payload.toString().getBytes());
	}

}
