package kafka;

import com.ludwikowski.schema.Email;
import com.ludwikowski.schema.MessageToSend;
import com.ludwikowski.schema.Sms;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.util.UUID.randomUUID;

public class ProduceMessageToSend {

    private static final Logger LOG = Logger.getLogger(ProduceMessageToSend.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        String topic = "union-test";
        LOG.info("Using topic: " + topic);
        Email email1 = new Email("addressTo1", "title1", "text1");
        Email email2 = new Email("addressTo2", "title2", "text2");
        Sms sms = new Sms("123123123", "hello");
        List<MessageToSend> allMessages = new ArrayList<>();
        allMessages.add(new MessageToSend(Sms.class.getCanonicalName(), randomUUID().toString(), sms));
        allMessages.add(new MessageToSend(Email.class.getCanonicalName(), randomUUID().toString(), email1));
        allMessages.add(new MessageToSend(Email.class.getCanonicalName(), randomUUID().toString(), email2));

        try (Sender<Object, MessageToSend> sender = new Sender<>(topic)){
            for(MessageToSend messageToSend: allMessages){
                RecordMetadata recordMetadata = sender.send(messageToSend).get();
                LOG.info("Sent: " + messageToSend);
            }
        }
    }
}
