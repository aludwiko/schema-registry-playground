package kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class Receiver<K, V> {

    private static final Logger LOG = Logger.getLogger(Receiver.class);

    private final Consumer<K, V> consumer;

    private final String topic;

    public Receiver(String topic, boolean specificAvroReader) {

        this.topic = topic;

        Properties properties = new Properties();

        properties.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.put(CLIENT_ID_CONFIG, "client_id");
        properties.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG,
                io.confluent.kafka.serializers.KafkaAvroDeserializer.class);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG,
                io.confluent.kafka.serializers.KafkaAvroDeserializer.class);
        properties.put("schema.registry.url", "http://localhost:8081");
        properties.put("specific.avro.reader", String.valueOf(specificAvroReader));

        this.consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(topic));
        LOG.info("Created Kafka Consumer for topic " + topic);

    }

    public List<V> receive() {

        List<V> buffer = new ArrayList<>();

        ConsumerRecords<K, V> records;
        do {
            records = consumer.poll(100);
            if (records.count() > 0) {
                for (ConsumerRecord<K, V> record : records) {
                    buffer.add(record.value());
                }
                consumer.commitSync();
                consumer.close();
                LOG.info("Consumer closed successfully");
            }
        } while (records.count() == 0);
        return buffer;
    }
}
