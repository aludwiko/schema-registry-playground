package kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.Future;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class Sender<K, V> implements Closeable {

    private static final Logger LOG = Logger.getLogger(Sender.class);


    private final Producer<K, V> producer;
    private final String topic;

    public Sender(String topic) {

        this.topic = topic;

        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ACKS_CONFIG, "all");
        properties.put(RETRIES_CONFIG, 0);
        properties.put(BATCH_SIZE_CONFIG, 16384);
        properties.put(LINGER_MS_CONFIG, 1);
        properties.put(BUFFER_MEMORY_CONFIG, 33554432);
        properties.put(KEY_SERIALIZER_CLASS_CONFIG,
                io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG,
                io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        properties.put("schema.registry.url", "http://localhost:8081");

        this.producer = new KafkaProducer<>(properties);
        LOG.info("Created Kafka Producer for topic " + topic);

    }

    public Future<RecordMetadata> send(V message) {

        ProducerRecord<K, V> record = new ProducerRecord<>(topic, message);
        try {
            return producer.send(record);
        } catch(SerializationException se) {
            LOG.error("Couldn't send message:", se);
            throw se;
        }

    }

    @Override
    public void close() {
        LOG.info("Closing Kafka Producer");
        producer.close();
    }

}
