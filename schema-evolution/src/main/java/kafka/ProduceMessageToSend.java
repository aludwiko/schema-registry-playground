package kafka;

import com.ludwikowski.schema.Email;
import com.ludwikowski.schema.MessageToSend;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;

import static java.util.UUID.randomUUID;

public class ProduceMessageToSend {

    private static final Logger LOG = Logger.getLogger(ProduceMessageToSend.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        String topic = "union-test";
        LOG.info("Using topic: " + topic);
        Email email1 = new Email("addressTo1", "title1", "text1");
        Email email2 = new Email("addressTo2", "title2", "text2");
        MessageToSend messageToSend1 = new MessageToSend(MessageToSend.class.getCanonicalName(), randomUUID().toString(), email1);
        MessageToSend messageToSend2 = new MessageToSend(MessageToSend.class.getCanonicalName(), randomUUID().toString(), email2);

        try (Sender<Object, MessageToSend> sender = new Sender<>(topic)){
            final RecordMetadata recordMetadata = sender.send(messageToSend1).get();
            LOG.info("Sent: " + messageToSend1);
            final RecordMetadata recordMetadata2 = sender.send(messageToSend2).get();
            LOG.info("Sent: " + messageToSend2);
        }
    }
}
