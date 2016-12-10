package kafka;

import com.ludwikowski.schema.MessageToSend;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificData;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReceiveMessageToSendNewVersion {

    private static final Logger LOG = Logger.getLogger(ReceiveMessageToSendNewVersion.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        String topic = "union-test";
        LOG.info("Using topic: " + topic);
        Receiver<Object, GenericRecord> receiver = new Receiver<>(topic, true);
        List<GenericRecord> results = receiver.receive();
        results.forEach((result) -> {
            MessageToSend messageToSendNewVersion = (MessageToSend) SpecificData.get().deepCopy(MessageToSend.SCHEMA$, result);
            LOG.info("Received the new version of MessageToSend: " + messageToSendNewVersion);
        });
    }
}
