package demo;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.kafka.listener.MessageListener;

import demo.TCPAddress;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.mtp.InChannel;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;


public class CustomMessageListener implements MessageListener<String, String> {
	ContainerController containerRef;
	Agent agente;
	@SuppressWarnings("unused")
	private FipaXMLUtil xmlUtil;

	private InChannel.Dispatcher dispatcher; // dispatcher used to pass messages to the platform
	@SuppressWarnings("unused")
	private TCPAddress tAddress;
//	private ProviderAdmin pAdmin;

	// inject your own concrete processor
	// private IMessageProcessor messageProcessor;
	private static final Logger log = Logger.getLogger(CustomMessageListener.class.getName());

	public CustomMessageListener(InChannel.Dispatcher dispatcher, TCPAddress jmsTA) {

		this.dispatcher = dispatcher;
		this.tAddress = jmsTA;
		xmlUtil = new FipaXMLUtil();


//		pAdmin = new KafkaProviderAdmin();
	}

	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	public void onMessage(ConsumerRecord<String, String> consumerRecord) {
		// process message
		// messageProcessor.process(consumerRecord.key(), consumerRecord.value());

		StringBuffer payload = new StringBuffer();
		StringBuffer dominio=new StringBuffer();
		Envelope env = new Envelope();
		
		String msg = consumerRecord.value();
		String xml="";
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(new StringReader(msg));	
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xml = xmlOutput.outputString(doc);			
		} catch (JDOMException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}			


		log.log(Level.INFO, "Message decode");
		//Agent a = new Agent();
		
		//AMSAgentDescription template = new AMSAgentDescription ();
		//SearchConstraints c = new SearchConstraints();
		//c.setMaxResults ( -1L); /// All of them


		
		if(containerRef==null) {
			Agent agenteNew = new Agent();
			Runtime rt = Runtime.instance();
	        ProfileImpl profile = new ProfileImpl(false);
	        profile.setParameter(Profile.CONTAINER_NAME,"containerPruebaUtima");
	        containerRef = rt.createAgentContainer(profile);
	        AgentController agentController;
			try {
				agentController = containerRef.acceptNewAgent("pruebaUltima", agenteNew);
				agentController.start();
				agente=agenteNew;
			} catch (StaleProxyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		}
        
		
		env = xmlUtil.decode(xml, payload,dominio);

		
		DFAgentDescription[] agents = null;
		DFAgentDescription templateDFA = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		if(!dominio.toString().contains("null")) {
			sd.setType(dominio.toString());
			sd.setName(dominio.toString());			
		}

		templateDFA.addServices(sd);


		List<AID> customAgents = new ArrayList<>();	
		try {
			agents = DFService.search(agente, templateDFA);
	        for (int i=0; i < agents.length; i++)
	        {
	            AID idd = agents[i].getName();
	            String agentLocalName = idd.getLocalName();
                customAgents.add(idd);
	            System.out.println(agentLocalName);
	        }
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		for (AID element : customAgents) {
		
			 env.addIntendedReceiver(element);
			 env.addTo(element);
		}

		//GenericMessage msgGene = (GenericMessage) payload;
		//ACLMessage acl = msgGene.getACLMessage();
		//ACLMessage prueba = (ACLMessage) payload.;
		dispatcher.dispatchMessage(env, payload.toString().getBytes());
		
	}

}
