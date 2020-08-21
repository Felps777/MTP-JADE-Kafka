package otros;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class Configurer {

	private static final String PROP_ZOOKEEPER_CONNECT = "zookeeper.connect";
	private static final String PROP_TOPIC_CONSUMER = "topic.consumer";
	private static final String PROP_HOST = "localhost:29092";
	private static final String PROP_HOST_ZOOKEEPER = "localhost:2181";

//	
//	 private ConsumerFactory<String, User> userConsumerFactory() {
//	        Map<String, Object> config = new HashMap<>(kafkaProperties.buildConsumerProperties());
//	        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//	        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//
//	        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(User.class));
//	    }
//
//	    @Bean
//	    public ConcurrentKafkaListenerContainerFactory<String, User> userKafkaListenerFactory() {
//	        ConcurrentKafkaListenerContainerFactory<String, User> factory = new ConcurrentKafkaListenerContainerFactory<>();
//	        factory.setConsumerFactory(userConsumerFactory());
//	        return factory;
//	    }
//	

	// ==
	public static HashMap<String, HashMap<String, String>> props;

	public static KafkaProducer<Long, String> kafkaProducerConfigurer(String topic) {

		Properties pp = new Properties();
		// Productor de Kafka
		pp.put(PROP_ZOOKEEPER_CONNECT, PROP_HOST_ZOOKEEPER);
		pp.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, PROP_HOST);
		pp.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		pp.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		pp.put(PROP_TOPIC_CONSUMER, topic);
		return new KafkaProducer<Long, String>(pp);

	}

	public static KafkaConsumer<String, String> kafkaConsumerConfigurer(String topic, String idGroup) {

		Properties pp = new Properties();

		// Consumidor de Kafka
		pp.put(PROP_ZOOKEEPER_CONNECT, PROP_HOST_ZOOKEEPER);
		pp.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, PROP_HOST);
		pp.put(ConsumerConfig.GROUP_ID_CONFIG, idGroup);
		pp.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		pp.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // "org.apache.kafka.common.serialization.StringDeserializer"
		pp.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // "org.apache.kafka.common.serialization.StringDeserializer"
		pp.put(PROP_TOPIC_CONSUMER, topic);
		KafkaConsumer<String, String> cons = new KafkaConsumer<String, String>(pp);
		cons.subscribe(Arrays.asList((pp.getProperty(PROP_TOPIC_CONSUMER)).split(",")));
		return cons;
	}

}
