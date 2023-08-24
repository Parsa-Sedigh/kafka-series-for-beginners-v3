## 43 - Kafka SDK List
https://www.conduktor.io/kafka/kafka-sdk-list
### SDK list of kafka
- the official SDK for apache kafka is the Java SDK
- for other languages, it's community supported
- list of recommended SDK at https://www.conduktor.io/kafka/kafka-sdk-list

## 44 - Creating Kafka Project
- maven vs gradle?
  - in this course, gradle because it's easier to write, read and less errors
- for maven: conduktor.io/kafka/creating-a-kafka-java-project-using-maven-pom.xml

After creating the project with gradle, we're not gonna use the src directory with default main and test subprojects. So delete the
default src directory that has default main and test subprojects and then in intellij, click on new>module>gradle and
name the subproject `kafka-basics`.

Intellij could keep recreating the src directory at the root of the project with main and test subprojects, ignore it.

Also remember to use build.gradle in subproject directory and not the one in the root directory of the project.

Now add the kafka deps in build.gradle of kafka-basics module. For this go to `mvnrepository.com` and go ti org.apache.kafka repo.
Click on `kafka-clients`, `choose gradle(short)` (you can choose gradle long version as well) and put it in `dependencies` block.

Then search for `slf4j api` and click on `slf4j-api` and click on the latest stable version(not alpha or beta versions).

Then go get `slf4j-simple`.

We need:
- kafka-clients
- slf4j-api(for logging)
- slf4j-simple(for logging) - choose the exact same version as the previous one

Remove jUnit deps because we're not gonna test our code.

Also make `slf4j-simple` dep as `implementation` **not** `testImplementation`.

Now we have setup our java project with gradle.

Now pull the deps. To verify you have them, see the `External Libraries` in intellij to check if the deps are installed.

Go to Preferences in intellij, go to `Build, Execution, Deployment`>Build Tools>Gradle , then choose `Build and run using` to `Intellij IDEA `
instead of `Gradle`. Then run the project again.

### 44 - Install Amazon Corretto 11
https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/what-is-corretto-11.html

### 44 - IntelliJ IDEA Community Edition
https://www.jetbrains.com/idea/download
### 44 - Kafka Project Gradle

https://www.conduktor.io/kafka/creating-a-kafka-java-project-using-gradle-build-gradle
### 44 - Kafka Project Maven
https://www.conduktor.io/kafka/creating-a-kafka-java-project-using-maven-pom-xml

## 45 - Java Producer
### Kafka producer: Java API - basics
`Cmd + p` inside the parentheses -> see the type of parameters of a method.

To run zookeeper:
```shell
zookeeper-server-start.sh <path to kafka config folder>/zookeeper.properties
```

To run kafka server:
```shell
kafka-server-start.sh <path to kafka config folder>/server.properties
```

Before we run the code, first we need to create that topic and then start a console consumer on it.
To create the topic:
```shell
kafka-topics --bootstrap-server 127.0.0.1:9092 --create --topic demo_java --partitions 3 --replication-factor 1
```
having 3 partitions is a good practice.

Now start a kafka console consumer:
```shell
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic demo_java
```

Now run the producer code that we wrote.

The program should exit with code 0 to be successful. The consumer should print the received message in this case: "hello world".

## 46 - Java Producer Callbacks
### Kafka producer: Java API - callbacks
- confirm the partition and offset the message was sent to, using callbacks. So this is for us to understand from the producer itself
to which partition and offset, the message was sent to? and to know this, we use callbacks
- we'll look at the interesting behavior of a producer and that behavior is called **StickyPartitioner**

![](./img/46-46-1.png)

If you run the code multiple times to send a message to kafka, we see the partition that received the message changes every time in sequence.
Because the message doesn't have a key and therefore it gets sent to random partition every time.

Q: If we send multiple messages from the producer, we see that the partition which receives the message is always gonna be the same(when
we log the message in callback function). Why is that? We know the key is null so the messages should be sent to random partitions, not the
same partition?

A: This has to do with the sticky assignor. We know that the producer sends data round-robin and we observed this behavior when running
the producer code multiple times. But somehow when sending multiple messages in the same run of the code, the producer is using 
sticky partitioner which is a performance improvement. If you send multiple messages very quickly, the producer is smart enough to now be
batching these messages into one batch to just make it more efficient. But if there is some delay between sending messages, maybe some of them
are batched and sent to partition #x and then after the delay is over, some other messages get batched and sent into partition #x+1.
If the producer continued to use the round-robin for each message, it means having one batch for every message and it's not efficient for producer.
So this was the behavior of sticky partitioner.

The `DefaultPartitioner` by default is of type `StickyPartitioner`(as part of new versions of kafka) and therefore brings us this optimization.
Now if you look at the logs of when starting the producer, if you look at `partitioner.class` the value is 
`class.org.apache.kafka.clients.producer.internals.DefaultPartitioner`. But if we want, we could change the `DefaultPartitioner` config to
be the round-robin partitioner but that would be extremely inefficient.

If we want to see the round-robin behavior when sending multiple messages very quickly, we need to force kafka to create different batches.
To do so, add some sleep at the end of each iteration. With this, each message is gonna send to a different partition in round-robin manner.

We learned:
- callbacks to get metadata when producer successfully sends the data or get an exception if it happens
- Sticky Partitioner which sends batches of messages inefficiency to the same partition. If a message doesn't have a key, it will still
be sent to the same partition. If the messages have keys, they still be sent to the same partition. We expected that by not having a key,
the messages are gonna be sent to different partitions but since we're sending messages very fast, they all gonna be sent to the same partition.

## 47 - Java Producer with Keys
### Kafka producer: Java API - with keys
- send non-null keys to the kafka topic
- same key -> same partition

For example truck_id_123 will always go to partition 0.
![](./img/47-47-1.png)

If you run the code multiple times, which means we're gonna have messages with the same keys between the execution of code, messages with the
same key always go to the same partition.

**Note:** You set a key for a message to make sure that all messages that have the same key, are gonna be sent to the same partition.

## 48 - Java Consumer
### Kafka consumer: Java API - Basics
- learn how to write a basic consumer to receive data from kafka
- view basic configuration parameters
- confirm we receive the data from the kafka producer written in java

The poll() method returns data immediately if possible, else it returns empty and waits until it has data within the timeout and returns empty.
![](./img/48-48-1.png)

`ConsumerConfig.AUTO_OFFSET_RESET_CONFIG` can have 3 possible values:
- none: if no previous offsets are found, then do not even start
- earliest: read from the very beginning of the topic(earliest messages) 
- latest: read from latest of the topic

When the consumer is joining the consumer group, when we see the first `Polling` log, instead of other Polling logs, we see some other logs
about `Resetting the last seen epoch ...`.

We can see multiple `Polling` logs without consuming any messages. This is natural. This is because our consumer still hasen't subscribed
to the topic(s), because this is the first time it connects. Then we see the log: `Succesfully joined group with generation
Generation{generationId=1, ...}`.

Then we see these logs: 
`Found no committed offset for partition demo_java-0
Found no committed offset for partition demo_java-1
Found no committed offset for partition demo_java-2`
So it's gonna reset offsets to the earliest because of the `AUTO_OFFSET_RESET_CONFIG`. Therefore, we see:
`Resetting offset for partition demo_java-0 to position FetchPosition{offset=0, ...
Resetting offset for partition demo_java-1 to position FetchPosition{offset=0, ...
Resetting offset for partition demo_java-2 to position FetchPosition{offset=0, ...`.

Then it's gonna actually retrieve the messages from kafka:
`ConsumerDemo - Key: null, value: hello world
ConsumerDemo - Partition: 0, Offset: 0
ConsumerDemo - Key: null, value: hello world
ConsumerDemo - Partition: 0, Offset: 1
ConsumerDemo - Key: null, value: hello world
ConsumerDemo - Partition: 0, Offset: 2
...`
It's reading a batch of messages from partition 0, let's say 20 messages), so we go until offset: 20 for partition: 0 and then it's
gonna read the partition 1 batch, so partition: 1, offset: 0 ... partition: 1, offset: 17 .

**Note:** As you see, the records are in order within each partition but not across partitions.

To stop the consumer demo, use `control + c` but you see the exit code is not 0 but it's 130 because we don't have a
nice graceful shutdown hook in our code.

Note: Since our consumer is into a group with some name, if you restart the consumer, no new records are gonna get polled.
So it just logs `Polling`.

After a while you see logs for `ConsumerCoordinator` log lines and it says: the consumer has joined the group again, but this time with a
new generationId(incremented by one). Then the consumer was assigned partition 0 and 1 and 2 again and the offset was set for these partitions
to offsets that were last committed, for example for partition 0, the last committed offset is 24, for partition 1 is 20 and for partition 2 is 39.
So we're seeing the fact that consumer groups are able to restart to read where they last left off(if the offsets were correctly committed which is the
case in our example).

## 49 - Java Consumer Graceful Shutdown

## 50 - Java Consumer inside Consumer Group
## 51 - Java Consumer Incremental Cooperative Rebalance & Static Group Membership
## 52 - Java Consumer Incremental Cooperative Rebalance Practice
## 53 - Java Consumer Auto Offset Commit Behavior

## 54 - Programming Advanced Tutorials

### 54 - Advanced Programming Tutorials
https://www.conduktor.io/kafka/advanced-kafka-consumer-with-java