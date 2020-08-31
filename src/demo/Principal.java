package demo;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.mtp.MTPException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;




public class Principal {

	/**
	 * IP (or host) of the main container
	 */
	private static String PLATFORM_IP = "127.0.0.1";  

	/**
	 * Port to use to conctact the AMS
	 */
	private static int PLATFORM_PORT=9888;

	/**
	 * ID (name) of the platform instance
	 */
	private static String PLATFORM_ID="Plat2";

	private static HashMap<String, ContainerController> containerList=new HashMap<String, ContainerController>();// container's name - container's ref
	private static List<AgentController> agentList;// agents's ref
	private static Runtime rt;	


	public static void main(String[] args) throws Exception{

		//1), create the platform (Main container (DF+AMS) + containers + monitoring agents : RMA and SNIFFER)
		rt=emptyPlatform(containerList);
		//rt2=emptyPlatform2(containerList);

		//2) create agents and add them to the platform.
		agentList=createAgents(containerList);

		//3) launch agents
		startAgents(agentList);

	}

	private static Runtime emptyPlatform(HashMap<String, ContainerController> containerList) throws MTPException, StaleProxyException{

		Runtime rt = Runtime.instance();

		// 1) create a platform (main container+DF+AMS)
		Profile pMain = new ProfileImpl(PLATFORM_IP, PLATFORM_PORT, PLATFORM_ID);
		System.out.println("Launching a main-container..."+pMain);
		pMain.setParameter(Profile.MTPS, "demo.MessageTransportProtocol(tcp:kafka:xml:localhost:29092/tpcTechLog)");
		AgentContainer mainContainerRef = rt.createMainContainer(pMain); //DF and AMS are include
		//mainContainerRef.installMTP("jms:jbossmq:xml:non_persistent:admin:admin:localhost:8080/Otro",MessageTransportProtocol.class.getName());
		containerList.put("Main1", mainContainerRef);
		// 2) create monitoring agents : rma agent, used to debug and monitor the platform; 
		createMonitoringAgents(mainContainerRef);
		System.out.println("Plaforma 1 ok");
		return rt;

	}
	

	private static void createMonitoringAgents(ContainerController mc) {

		System.out.println("Launching the rma agent on the main container ...");
		AgentController rma;

		try {
			rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
			rma.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
			System.out.println("Launching of rma agent failed");
		}

	}


	private static List<AgentController> createAgents(HashMap<String, ContainerController> containerList) {
		System.out.println("Launching agents for example ...");
		ContainerController c;
		String agentName;
		String containerName;
		List<AgentController> agentList=new ArrayList<AgentController>();

		containerName="Main1";
		c = containerList.get(containerName);
		agentName="Agente_1B";
		Object[] objtab=new Object[]{};//used to give informations to the agent (in that case, nothing)
		createOneAgent(c, agentName, Agent1B.class.getName(), agentList, objtab);
		
		agentName="Agente_1A";
		objtab=new Object[]{};//used to give informations to the agent (in that case, nothing)
			createOneAgent(c, agentName, Agent1A.class.getName(),agentList, objtab);
		
		
		
		System.out.println("Agents launched...");
		return agentList;
	}


	private static void createOneAgent(ContainerController c, String agentName, String className,List<AgentController> agentList, Object[] objtab) {
		try {						
			AgentController	ag=c.createNewAgent(agentName,className,objtab);
			agentList.add(ag);
			try {
				System.out.println(agentName+" launched on "+c.getContainerName());
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void startAgents(List<AgentController> agentList){

		System.out.println("Starting agents...");
		for(final AgentController ac: agentList){
			try {
				ac.start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Agents started...");
	}

}







