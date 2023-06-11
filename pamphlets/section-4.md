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
## 10-10 - Consumer Groups & Consumer Offsets
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