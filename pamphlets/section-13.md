# Section 13-Kafka-Extended-APIs-for-Developers

## 87 - Kafka Extended APIs Overview
### What will we see in this section?
- kafka consumers and producers have existed for a long time and they're considered low-level(because we're dealing with sending and receiving each message)
- kafka & ecosystem has introduced over time some new API that are higher level that solves specific sets of problems
    - kafka connect: for example we use kafka connect to solve the problem of taking data from an external source and sending it to kafka or from kafka
    into an external sink
    - kafka streams: if you wanna do a transformation from a kafka topic to another kafka topic, instead of chaining a producer and consumer,
    you can just use kafka streams
    - schema registry: helps using schemas in kafka

Wikimedia data is gonna sent to kafka using source connector(the kafka SSE source connector). We'll use kafka streams to compute some statistics
on top of our dataset and we'll use kafka connect elastic search sink to send data into opensearch(external sink).

## 88 - Kafka Connect Introduction
- do you feel you're not the first person in the world to write a way to get data out of twitter?
- do you feel you're not the first person in the world to send data from kafka to postgresql/elastic search/ mongodb/ open search?

### Why kafka connect?
We use a source connector to get data from a source and a sink connector to send data into a sink.

### kafka connect - artchitecture design
We have a kafka cluster made of brokers and topics and partitions. The sources can be diverse(twitter, mongodb) and we'll create a connect cluster
made of workers and these workers are going to take data from the source and send it into our kafka cluster. The same connect workers can also
be used to deploy sink connectors and create tasks for that. Where they going to read data from our kafka cluster and send it to sinks.

- source connectors to get data from data sources
- sink connectors to publish that data in common data sources
- part of your ETL(extract, transform, load) pipeline
- connectors achieve fault tolerance, idempotence, distribution, ordering

## 89 - Kafka Connect Hands On Warning
Kafka Connect Hands-On: Warning

The next lecture is really advanced and I go quite fast.

If you cannot reproduce what I do, do not worry, this lecture is optional and not necessary to complete to finish the course. 
Kafka Connect takes about 3 hours to learn properly, and this one lecture is meant as an introduction to show you the power of it.

Happy learning!

## 90 - Kafka Connect Wikimedia & ElasticSearch Hands On
TODO

### 90 - Confluent Hub

## 91 - Kafka Streams Introduction
for:
- data transformation
- data enrichment
- fraud detection
- monitoring and alerting

## 92 - Kafka Streams HandsOn

## 93 - Kafka Schema Registry Introduction
### The need for a schema registry
There is no data verification in kafka. But:
- what if a producer sends bad data?
- what if a field gets renamed?
- what if the data format changes?

The consumers will break! Because the deserializer would be failing.

- we need data to be self describable
- we need to be able to evolve data without breaking downstream consumers
So we need schemas and a schema registry. Schemas describe how the data looks like.

Q: What if the kafka brokers were verifying the messages they receive?

A: It would break what makes kafka so good. Because kafka doesn't parse or even read your data(no CPU usage). Kafka takes bytes as input
without even loading them into memory(that's called zero copy) and then distributes the bytes. As far as kafka is concerned,
it doesn't even know if your data is an integer, a string or ... . So kafka can not be the schema registry.
So the schema registry needs to be a separate component and producers and consumers need to be able to talk to it. The scehma registry
must be able to reject bad data before it is sent to kafka and a common data format must be agreed on by the schema registry:
- it(that data format) needs to support schemas
- it needs to support evolution
- it needs to be lightweight

Apache avro as data format(protobuf, json schema also supported by the schema registry)

### pipeline without schema registry
source sends to producers, sends to kafka, sends to consumer, sends to target.

### with schema registry
The schema registry will store the schemas for your producer and consumer. It will be able to enforce
backward, forward(if you want to evolve your schemas) compatibility on topics and you can decrease the size of the payload
sent to kafka.

The schema registry is a separate component

Now the producer, before sending to kafka, will send the schema to schema registry(if the schema is not yet inserted). Then the schema registry
is going to validate the schema itself with kafka and then if all good, then the producer is gonna send data in avro format to kafka(but the
schema is externalized in schema registry). Now when a consumer reads data from kafka, it's first gonna receive avro data and now the deserializer
needs a schema to read the data with. So the consumer is gonna retrieve the schema from the schema registry . Now the consumer can produce your object
and send it to target.

### schema registry: gotchas
Since schema registry becomes a critical component, it needs to HA.

## 94 - Kafka Schema Registry Hands On

## 95 - Which Kafka API should I use
- If data is already somewhere(in a DB or ...) and you wanna put it into kafka => kafka connect source
- if data is produced directly like from trucks and you wanna send it to kafka => kafka producer
- to do kafka to kafka transformations => kafka streams or kSQL to sql queries
- to send data into a storage for storage and analysis later on => kafka connect sink
- if the next step is the final step in consuming data(like sending email) and then it just goes away => kafka consumer 