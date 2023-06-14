# Section 4 - Kafka Theory

## 7-7 - Topics Partitions and Offsets
### Kafka topics
**topic:** A particular stream of data within your kafka cluster. A topic in kafka is like a table in a database(without all the constraints)
- You can have as many topics as you want in your kafka cluster.
- A topic is identified by it's **name**, like: logs, purchases, twitter_tweets and trucks_gps
- these kafka topics support any kind of message formats(you can send json, avro, text file, binary and ...)
- the sequence of the messages in a topic is called a **data stream**. You make data streams through topics.
- you can't query topics, instead, to add data into a topic, use kafka producers and to read the data from the topic, use kafka consumers. But there's
no querying capability within kafka

### Partitions and offsets
- Topics are general but you can divide them into partitions. So a topic can be made of for example 100 partitions
- The messages sent to kafka topic are going to end up in these partitions and messages within each partition are going to be ordered
- data is written into partitions
- the messages in these partitions(where they are written) are getting an incrementing id and this id is called a **kafka partition offset(offset)**
- kafka topics are immutable which means once data is written into a partition it can not be changed. So we can't delete or update data in kafka, you
have to keep on writing to the partition

![](./img/7-7-1.png)

### Topic example: truck_gps
- say you have a fleet of trucks; each truck reports it's GPS position to kafka
- each truck will send a message to kafka every 20 seconds, each message will contain the truckID and the truck position(latitude and longitude)
- you can have a topic `trucks_gps` that contains the position of all trucks
- we chose to create that topic with 10 partitions(arbitrary number - we will see how choose the best value)
- once the topic is created, we have some use cases: for example:
    - we have some consumers of that topic and send it into a location dashboard to track the location of trucks in real time
    - maybe we also want to have a notification service consuming the same stream of data to send notification to customers when that delivery is close

So with kafka, multiple services are reading from the same stream of data

### Topics, partitions and offsets - important notes
- once a data is written to a partition, it **can not be changed**(immutability)
- data in kafka is kept only for a limited time(default is one week - configurable)
- offsets only have a meaning for a specific partition:
    - Offsets are repeated across partitions, so offset 3 in partition 0 doesn't represent the
    same data as offset 3 in partition 1
    - offsets are not re-used even if previous messages have been deleted
- order of messages is guaranteed only within a partition(not across partitions)
- when data is sent to kafka topic, is gonna be assigned randomly to a partition unless a key is provided
- you can have as many partitions as you want per topic

## 8-8 - Producers and Message Keys
### Producers
- topics hold data
- producers write data to topic(which are made of partitions, so producers write to topic partitions)
- producers know in advance to which partition they write to(and which kafka broker(which is a kafka server) has it). This means that the
producers know in advance in which partition the message is gonna be written. Some people think that kafka(the server) decides at the end which
partition data gets written to. But this is wrong, the producer decides in advance which partition to write to.
- in case of kafka broker(server) failure which would have partitions, producers will know how to automatically recover. So there's a lot of behind the
scenes magic
- we have load balancing because the producers gonna send data across all partitions based on some mechanism and this is why kafka scales, because
we have many partitions within a topic and each partition is gonna receive messages from one or more producers

### Producers: Message keys
- the message itself has self contained data, but then the producer can add a key and it's optional(can be anything string, number, binary).
- for example we have a producer that's writing to a topic with two partitions. If the **key** is null(means key is not provided in the
producer message), then the data is sent round robin and this is how we get load balancing
- if key != null then all messages that share the same key always go to the same partition(hashing strategy) in other words they will always end up being written
to the same partition. This is a property of kafka producers
- a key is typically sent if you need message ordering for a specific field(ex: truck_id). Now for example the truck_id 123 is gonna be always sent to
partition 0 and we can read data **in order** for that one truck from partition 0 and truck id 234 is always gonna be sent to partition 0. Note: Which key
ends up in which partition is made thanks to the hashing technique. For example truck_id 345, 456 will always end up in partition 1 of your topic A.

### Kafka messages anatomy
- key(can be null). It's in binary
- value(can be null but usually is not) which is the message content. In binary
- compression type. Do we want messages to be smaller? types: none, gzip, snappy, lz4, zstd
- headers(optional) list of key-value pairs
- the partition that the message is going to be sent to
- offset
- timestamp(either set by system or by user)

This message gets sent into kafka for storage.

How do these messages get created?

### Kafka message serializer
We have a kafka message serializer.

- Kafka only accepts bytes as an input from producers and sends bytes out as an output to consumers. But when we construct messages, they're
not bytes. So we're going to perform message serialization
- message serialization means transforming objects/data into bytes
- these serializers are gonna be used only on the value and key. For example the key object is 123 and value object is "hello world". So these are
not bytes just yet. Then we specify the `KeySerializer` to be an `IntegerSerializer` and kafka producer is smart enough to transform that key object(123)
through the serializer into a series of bytes and this will give us the binary representation of that key. For value object we specify a
StringSerializer and producer is gonna smart enough to transform the string into a series of bytes for our value. Now that we have key and value as binary
representations, this message is ready to be sent into kafka.
- kafka producers come with common serializers that help you do this transformation, common message serializers include:
  - string(including the JSON representation of the string)
- int, float
- avro
- protobuf

### For the curios: Kafka message key hashing
How the message keys are hashed?

- a kafka partitioner is a code logic that takes a record(message) and determines to which partition to send it into
- so when we do `.send()`, the producer partitioner logic is gonna look at the record and then assigns it to a partition and then it gets sent
by the producer into kafka
- **key hashing** is the process of determining the mapping of a key to a partition
- in the default kafka partitioner, the keys are hashed using the **murmur2 algorithm**, with the formula below for curios:
`targetPartition = Math.abs(Utils.murmur2(keyBytes) % (numPartitions - 1))`

Note: Producers are the ones who choose where the message is gonna end up thanks to the key bytes(by hashing the key)

## 9-9 - Consumers & Deserialization
### Consumers
We have serializing at producer side and deserializing at the consumer side.

- consumers implement the pull model. That means that the consumers are going to request data from the **kafka brokers(servers**) and then
they will get a response back. It's **not** the kafka broker pushing data to the consumers. Instead, it's a pull model.
- consumer read data from a topic(identified by name) - pull model
- when consumers need to read data from a partition, they will automatically know which broker(kafka server) to read from and in case a broker has a 
failure, the consumers are again very smart and they will know how to recover from this.
- data is read in order from low to high offset within **each** partition. But remember there's no ordering guarantees **across** partitions.
Because they are different partitions. The only ordering we have is within each partition. So if a consumer consumes from multiple partitions, we have
ordering for each partition, but there is no ordering across partitions.

### Consumer deserializer
Consumers need to transform received bytes into objects or data
- deserialize indicates how to transform bytes into objects/data(both key and value is gonna be in binary format after receiving)
- they are used on the key and value of the message
- the consumer has to know in advance, what is the format of your messages. For example the key is integer, so it's gonna use IntegerDeserializer to transform
the key that is bytes into an integer
- common deserializers:
  - string(including JSON)
  - int, float
  - avro
  - protobuf
- since the consumer needs to know in advance what is the expected format for your key and value, the serialization/deserialization type
must not change during a topic lifecycle(create a new topic instead - also the consumers should be re-programmed a bit). Otherwise the consumers are gonna break.
So if you want to change the data type of your topic, create a new topic instead.

## 10-10 - Consumer Groups & Consumer Offsets
### Consumer groups
- all the consumers in an application read data as a consumer group. For example we have a kafka topic with 5 partitions and then we have a
consumer group that is called `consumer-group-application` and that consumer group has 3 consumers.
- each consumer within a group(if all belong to the same group) reads from exclusive partitions. Meaning we won't have two consumers that is consuming
from the same partition. For example consumer 1 is reading from partition 0 and 1, consumer 2 is reading from partition2 and 3 and consumer 3 is
reading from partition 4. consumers read from distinct partitions. This way a group is gonna read the topic as a whole.

### Consumer groups - what if too many consumers?
What if we have more consumers in your group than partitions?

- if you have more consumers than partitions, some consumers will be inactive(it won't help other consumers to read from partitions, it's gonna stay
inactive)

### Multiple consumer groups on one topic
You can have multiple consumer groups on one topic. 
- in kafka it's completely acceptable to have multiple consumer groups on the same topic. Therefore we could have some partitions that will
have multiple readers(consumers), but within a consumer group, only one consumer is going to be assigned to a partition.
- to create distinct consumer groups, use the consumer property `group.id` to give a name to consumer group and then consumers will know in which
group they belong

Q: Why would you have multiple consumer groups?

Let's say we have location service and notification service reading from the same data stream of `trucks_gps`, this means we're gonna have one consumer
group per service(because we can't have two consumers that are in the same consumer, reading from the same partition, therefore we need to have two
consumer groups in this case). So one consumer group will be for the location service and another consumer group for notif service.

![](./img/10-10-1.png)

### Consumer offset
Consumer groups are even more powerful than what we think.
- in a group we can define consumer offsets. What are they? Kafka is gonna store the offsets at which a consumer group has been reading and these
offsets are going to be in a kafka topic named `__consumer_offsets`.
- the offsets committed are in kafka `topic` named `__consumer_offsets`. It has two underscores at the beginning because it's an internal kafka topic.
- when a consumer in a group has processed data received from kafka, it should **periodically** committing the offsets(the kafka broker will
write to `__consumer_offsets`, not the group itself).
- by committing the offsets we're going to be able to tell the kafka broker how far we've been successfully reading into the kafka topic
- why consumers tell broker to commit offsets? if a consumer dies and then comes back, it will be able to read back from where it left off thanks to
the committed consumer offsets. For example the broker is gonna say: hey in partition 2, it seems you have been reading up to offset 4262, then when
you restart, I will only send you data from this offset onwards.

The consumers are gonna commit offsets once in a while and when the offsets are committed, this is gonna allow the consumer to keep on reading from that
offset onwards.

### Delivery semantics for consumers
based on how and when you commit offsets, you're gonna be in one of the delivery modes(there are 3 modes).

- by default, java consumers will **automatically** commit offsets(at least once)
- if you choose to commit manually, there are 3 delivery semantics
- at least once(usually preferred)
  - offsets are committed after the message is processed
  - if the processing goes wrong, the message will be read again
  - this can result in duplicate processing of messages. Make sure your processing is idempotent(i.e. processing again the message won't impact your
  systems)
- at most once
  - offsets are committed as soon as messages are received
  - if the processing goes wrong, some messages will be lost(they won't be read again, because we have committed offsets sooner than when we're done
  processing those messages)
- exactly once
  - for kafka => kafka workflows: use the transactional API(easy with kafka streams API)
  - for kafka => external system workflows: use an **idempotent** consumer

kafka => kafka workflows means when we read from topic and then we write back to topic as a result, we can use the transactional API.

## 11-11 - Brokers and Topics
## 12-12 - Topic Replication
## 13-13 - Producer Acknowledgements & Topic Durability
## 14-14 - Zookeeper
## 15-15 - Kafka KRaft Removing Zookeeper

### 15 - KRaft performance improvement
https://www.confluent.io/blog/kafka-without-zookeeper-a-sneak-peek/

### 15 - KRaft README
https://github.com/apache/kafka/blob/trunk/config/kraft/README.md

## 16-16 - Theory Roundup