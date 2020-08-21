package demo;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

public class ProducerMananger {

	private static final String KAFKA_BROKER = "localhost:29092";

	public ProducerMananger() {
		// TODO Auto-generated constructor stub
	}

	private Map<String, Object> senderProps_String() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}

	public KafkaTemplate<String, String> getSenderTemplate_String() {
		Map<String, Object> senderProps = senderProps_String();
		ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<String, String>(senderProps);
		KafkaTemplate<String, String> template = new KafkaTemplate<>(pf);
		return template;
	}
	
}
