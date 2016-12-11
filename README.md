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

```
schema-registry-playground/schema-evolution$ mvn compile exec:java -Dexec.mainClass="kafka.ProduceMessageToSend"
```

You should see something similar to:
```
[2016-12-11 09:30:51,691 kafka.ProduceMessageToSend] INFO Sent: {"type": "com.ludwikowski.schema.Sms", "correlationId": "0ccbd77b-62a9-4d50-912f-6a6b98644823", "payload": {"phoneNumber": "123123123", "text": "hello"}}
[2016-12-11 09:30:51,694 kafka.ProduceMessageToSend] INFO Sent: {"type": "com.ludwikowski.schema.Email", "correlationId": "b287095a-e73f-40d4-974f-5577344d1837", "payload": {"addressTo": "addressTo1", "title": "title1", "text": "text1"}}
[2016-12-11 09:30:51,696 kafka.ProduceMessageToSend] INFO Sent: {"type": "com.ludwikowski.schema.Email", "correlationId": "67d9a4b8-6004-4b04-9226-28fb2494f40e", "payload": {"addressTo": "addressTo2", "title": "title2", "text": "text2"}}
```

# Consume the new version of MessageToSend

Verify that you are in `schema-registry` directory and run:

```
schema-registry-playground/schema-registry$ mvn compile exec:java -Dexec.mainClass="kafka.ReceiveMessageToSendNewVersion"
```

You should see something similar to:
```
[2016-12-11 09:31:30,241 kafka.ReceiveMessageToSendNewVersion] INFO Received the new version of MessageToSend: {"type": "com.ludwikowski.schema.Sms", "correlationId": "0ccbd77b-62a9-4d50-912f-6a6b98644823", "payload": {"phoneNumber": "123123123", "text": "hello"}}
[2016-12-11 09:31:30,241 kafka.ReceiveMessageToSendNewVersion] INFO Received the new version of MessageToSend: {"type": "com.ludwikowski.schema.Email", "correlationId": "b287095a-e73f-40d4-974f-5577344d1837", "payload": {"addressFrom": "andrzej@test.pl", "addressTo": "addressTo1", "title": "title1", "text": "text1"}}
[2016-12-11 09:31:30,241 kafka.ReceiveMessageToSendNewVersion] INFO Received the new version of MessageToSend: {"type": "com.ludwikowski.schema.Email", "correlationId": "67d9a4b8-6004-4b04-9226-28fb2494f40e", "payload": {"addressFrom": "andrzej@test.pl", "addressTo": "addressTo2", "title": "title2", "text": "text2"}}
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