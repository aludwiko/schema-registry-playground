package schema;

import com.ludwikowski.schema.Email;
import com.ludwikowski.schema.EmailV2;
import com.ludwikowski.schema.MessageToSend;
import com.ludwikowski.schema.MessageToSendV2;
import com.ludwikowski.schema.Metric;
import com.ludwikowski.schema.MetricV2;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PlainAvroSchemaEvolutionTest {

    @Test
    public void shouldDeserializeSimplePojoWithNewSchema() throws IOException {
        //given
        final File file = new File("target/metrics.avro");
        Metric oldMetric = new Metric("Ip", "MyName", 1.23f);
        final String defaultTime = "12345";

        DatumWriter<Metric> metricWriter = new SpecificDatumWriter<>(Metric.class);
        DataFileWriter<Metric> dataFileWriter = new DataFileWriter<>(metricWriter);
        dataFileWriter.create(oldMetric.getSchema(), file);
        dataFileWriter.append(oldMetric);
        dataFileWriter.close();

        //when
        // Deserialize Users from disk
        DatumReader<MetricV2> metricDatumReader = new SpecificDatumReader<>(MetricV2.class);
        DataFileReader<MetricV2> dataFileReader = new DataFileReader<>(file, metricDatumReader);

        //then
        final MetricV2 metricV2 = dataFileReader.next();
        assertThat(metricV2).isEqualTo(new MetricV2("Ip", "MyName", 1.23f, defaultTime));
    }

    @Test
    public void shouldDeserializePojoWithUnionsAndNewSchema() throws IOException {
        //given
        final File file = new File("target/messageToSend.avro");
        String defaultAddressFrom = "andrzej@test.pl";
        Email email = new Email("addressTo", "title", "text");
        MessageToSend messageToSend = new MessageToSend("type", "correlationId", email);

        DatumWriter<MessageToSend> writer = new SpecificDatumWriter<>(MessageToSend.class);
        DataFileWriter<MessageToSend> dataFileWriter = new DataFileWriter<>(writer);
        dataFileWriter.create(messageToSend.getSchema(), file);
        dataFileWriter.append(messageToSend);
        dataFileWriter.close();

        //when
        DatumReader<MessageToSendV2> reader = new SpecificDatumReader<>(MessageToSendV2.class);
        DataFileReader<MessageToSendV2> dataFileReader = new DataFileReader<>(file, reader);

        //then
        final MessageToSendV2 messageToSendV2 = dataFileReader.next();
        //in case of new pojo name in union - it is necessary to provide an alias
        final EmailV2 emailV2 = (EmailV2) messageToSendV2.getPayload();
        assertThat(emailV2).isEqualTo(new EmailV2(defaultAddressFrom, "addressTo", "title", "text"));
    }
}
