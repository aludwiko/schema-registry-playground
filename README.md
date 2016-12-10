# Prerequisites
All modules should be compiled:

```schema-registry-playground$ mvn clean compile```

Zookeeper, Kafka nad the Schema Registry have to be running:

```
$ ./bin/zookeeper-server-start ./etc/kafka/zookeeper.properties &

$ ./bin/kafka-server-start ./etc/kafka/server.properties &

$ ./bin/schema-registry-start ./etc/schema-registry/schema-registry.properties &
```

# Produce MessageToSend

Verify that you are in `schema-evolution` directory and run:

```schema-registry-playground/schema-evolution$ mvn compile exec:java -Dexec.mainClass="kafka.ProduceMessageToSend" ```

You should see something similar to:
```
[2016-12-10 14:25:41,454 kafka.ProduceMessageToSend] INFO Sent: {"type": "com.ludwikowski.schema.MessageToSend", "correlationId": "a004124f-d690-4abe-8255-f076ef77710d", "payload": {"addressTo": "addressTo1", "title": "title1", "text": "text1"}}
[2016-12-10 14:25:41,456 kafka.ProduceMessageToSend] INFO Sent: {"type": "com.ludwikowski.schema.MessageToSend", "correlationId": "b4e24693-9ede-467c-897a-9a4c9bfa4956", "payload": {"addressTo": "addressTo2", "title": "title2", "text": "text2"}}
```

# Consume the new version of MessageToSend

Verify that you are in `schema-registry` directory and run:

```schema-registry-playground/schema-registry$ mvn compile exec:java -Dexec.mainClass="kafka.ReceiveMessageToSendNewVersion"```

You should see something similar to:
```
[2016-12-10 14:26:11,342 kafka.ReceiveMessageToSendNewVersion] INFO Received the new version of MessageToSend: {"type": "com.ludwikowski.schema.MessageToSend", "correlationId": "a004124f-d690-4abe-8255-f076ef77710d", "payload": {"addressFrom": "andrzej@test.pl", "addressTo": "addressTo1", "title": "title1", "text": "text1"}}
[2016-12-10 14:26:11,342 kafka.ReceiveMessageToSendNewVersion] INFO Received the new version of MessageToSend: {"type": "com.ludwikowski.schema.MessageToSend", "correlationId": "b4e24693-9ede-467c-897a-9a4c9bfa4956", "payload": {"addressFrom": "andrzej@test.pl", "addressTo": "addressTo2", "title": "title2", "text": "text2"}}
```

In this approach a schema is provided in form of an Avro schema file, which is translated into a class file, which then can be used to create objects, either via constructors or builders.

# Schema registry

*Get all schema names (subjects)*

```curl -X GET -i http://localhost:8081/subjects```

*Retrieve all version of a particular schema*

```$ curl -X GET -i http://localhost:8081/subjects/union-test-value/versions```

*Retrieve a particular version of a given schema*

```$ curl -X GET -i http://localhost:8081/subjects/union-test-value/versions/1```

or

```$ curl -X GET -i http://localhost:8081/subjects/union-test-value/versions/latest```