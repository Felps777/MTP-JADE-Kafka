package demo;

import java.util.List;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Agent1A extends Agent{

	private static String AGENT_DOMAIN = "AgenteLogistica";
	
	private static final long serialVersionUID = 1968856210218561967L;
	protected List<String> data;

	protected void setup(){

		super.setup();

		//get the parameters given into the object[] (here no parameters are expected)

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(AGENT_DOMAIN);
		sd.setName(AGENT_DOMAIN);
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Add the behaviours
		addBehaviour(new ReceiveACLMessageBlockBehaviour(this));

		System.out.println("the receiver agent "+this.getLocalName()+ " is started");
		
	}


	protected void takeDown(){

	}

	@SuppressWarnings("serial")
	public class ReceiveACLMessageBlockBehaviour extends SimpleBehaviour {

	    private boolean finished = false;
	    private String content = "Recibido por Dominio="+AGENT_DOMAIN+"Enviando mensaje a dominio: AgenteTraslado";


	    /*Required no-argument constructor for being a singleton component*/
	    public ReceiveACLMessageBlockBehaviour(final Agent myagent){
	    	super(myagent);
	    }

	    @Override
	    public void action() {
//	            finished = false;
	            System.out.println ("Action in ReceiveMesageBehaviour is running -> I will try to receive a new message");
	            ACLMessage msg =this.myAgent.receive();

	            if (msg != null) {
	                System.out.println(myAgent.getLocalName() + ": have just received this msg: ");
	                System.out.println(msg.toString());
	                ACLMessage reply = msg.createReply();
	                reply.setPerformative(ACLMessage.INFORM);
	                reply.setContent(content);
	                reply.setConversationId("AgenteTraslado");
	                myAgent.send(reply);
	                System.out.println(myAgent.getLocalName() + ": sending an answer");
	                //finished = true;
	            } else {
	                System.out.println("Waiting for the message");
	                System.out.println("Agent who is waiting is: " + myAgent.getLocalName());
	                block(); //only block this behaviour until you receive a msg or a explicit wake up
	            }
	        
	    }


	    @Override
	    public boolean done() {
	        return finished;
	    }

	    @Override
	    public void reset() {
	        super.reset();
	        this.finished = false;
	    }


	    @Override
	    public Agent getAgent() { //required only for adding @JsonIgnore and avoid the behaviour had problems with jackson parse (infinity cycle)
	        return super.getAgent();
	    }
	}
}
