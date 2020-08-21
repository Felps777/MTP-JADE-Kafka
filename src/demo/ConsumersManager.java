package demo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

import jade.mtp.InChannel;

public class ConsumersManager {

	private static final String KAFKA_BROKER = "localhost:29092";
//	Configurer conf = new Configurer();
	private static Map<String, ConcurrentMessageListenerContainer<String, String>> consumersMap = new HashMap<>();
	// stores the topic consumers
	private ConsumerFactory<String, String> consumerFactory_String = null;
	private ContainerProperties containerProps;

	public ConsumersManager() {
	}

	public void addConsumer(InChannel.Dispatcher disp, TCPAddress jmsTA) throws Exception {

		ConcurrentMessageListenerContainer<String, String> container = consumersMap.get(jmsTA.getTopicName());
		if (container != null) {
			if (!container.isRunning()) {
				System.out.println(String.format("Consumer already created for topic {}, starting consumer!!",
						jmsTA.getTopicName()));
				// container.start();
				System.out.println(String.format("Consumer for topic {} started!!!!", jmsTA.getTopicName()));
			}
			return;
		}

		// pasamos el nombre del topico a escuchar
		containerProps = new ContainerProperties(jmsTA.getTopicName());
		containerProps.setPollTimeout(100);
		container = createContainer(containerProps);

		// KafkaConsumer<String, String> kCons =
		// conf.kafkaConsumerConfigurer(jmsTA.getTopicName(), jmsTA.getGroupID());
		container.setupMessageListener(new CustomMessageListener(disp, jmsTA));

		container.setConcurrency(1);

		try {
//			consumersMap.put(jmsTA.getString(), new QueueListener(conn, disp, jmsTA));
			consumersMap.put(jmsTA.getTopicName(), container);
		} catch (Exception e) {

			System.out.println("Error adding the QL to the listenerMap: " + e.toString());

			throw e;
		}
	}

	/**
	 * Remove a specific queue listener
	 * 
	 * @param key Key of the queue listener to remove
	 * @throws Exception Error during queue removal
	 */
	public void removeListener(String topic) throws Exception {

		System.out.println("Removing Listener for topic: " + topic);

		ConcurrentMessageListenerContainer<String, String> container = consumersMap.get(topic);

		if (container != null) {
			if (!container.isRunning()) {

				container.stop();
				System.out.println("Consumer for topic " + topic + " stopped!!!!");

				System.out.println("Consumer for topic is deleted!!!!");
			}
			consumersMap.remove(topic);

		} else {
			System.out.println("There is no consumer for topic " + topic);
		}

	}

	public void removeAllListeners() throws Exception {
		Set keys = consumersMap.keySet();

		for (Iterator i = keys.iterator(); i.hasNext();) {

			try {
				this.removeListener((String) i.next());
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public void startListener(String topic) {
		ConcurrentMessageListenerContainer<String, String> container = consumersMap.get(topic);
		if (container != null) {
			if (!container.isRunning()) {

				container.start();
				System.out.println("Consumer for topic " + topic + " started!!!!");
			} else {
				System.out.println("Consumer for topic is already running!!!!");
			}
			return;
		}
	}

	private ConcurrentMessageListenerContainer<String, String> createContainer(ContainerProperties containerProps) {
		Map<String, Object> props = consumerProps("prueba");
		ConsumerFactory<String, String> factory = consumerFactory_String(props);
		Boolean enableAutoCommit = (Boolean) props.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG);
		if (!enableAutoCommit) {
			containerProps.setAckMode(AckMode.MANUAL_IMMEDIATE);
		}

		return new ConcurrentMessageListenerContainer<>(factory, containerProps);
	}

	public ConsumerFactory<String, String> consumerFactory_String(Map<String, Object> consumerProps) {

		return new DefaultKafkaConsumerFactory<>(consumerProps);
	}

	private Map<String, Object> consumerProps(String groupId) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		return props;
	}

	public static Map<String, ConcurrentMessageListenerContainer<String, String>> getConsumersMap() {
		return consumersMap;
	}

}
