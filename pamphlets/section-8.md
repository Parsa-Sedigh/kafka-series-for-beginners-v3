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
Kafka consumer - graceful shutdown
- ensure we have code in place to respond to termination signals
- improve oru java code

We're gonna add a shutdown hook to our consumer. Look at ConsumerDemoWithShutdown

Since `consumer.wakeup()` makes the next call of `consumer.poll()` to throw an exception, we need to put the surrounding code of `consumer.poll()`
into a try catch block.

Now to test things, exit the consumer code. You should see the exit logs that we wrote for our consumer.

Also the log that says: `Detected a shutdown...` which runs in a different thread, is distinguished by [Thread-1] instead of [main] which shows
it ran on a different thread.

## 50 - Java Consumer inside Consumer Group
### Kafka consumer: Java API - consumer groups
We're gonna look at the behavior of our consumer as part of a consumer group.

- make your consumer in java consume data as part of a consumer group
- observe partition re-balance mechanisms
![](./img/49-49-1.png)

### Rebalancing in consumer group
Now we want to start a second version of our consumer code. To do this, in intellij, go to edit configuration modal which is for
run configuration of the project, click on `Edit configuration`, then click on `Modify options`, then enable `Allow multiple instances` which
allows us to run the same program multiple times.

Now if you re-run the program, it won't overwrite the already running program, but instead it will run it as another instance.

By having more consumers in a consumer group, the partitions gonna split between consumers and each consumer will be assigned to equal number
of partitions, in case of even number of partitions or in case of odd number of partitions, one of the consumers will be assigned to one more
partition. If we add more consumers to the group, the group is gonna rebalance the partitions to consumers. A related log to this, is:
`Request joining group due to: group is already rebalancing`.

Also when a consumer goes down(or stopped), the other consumers in the group will be notified that that consumer left and the group will be 
rebalanced(meaning the partitions assigned to the consumer that went down, gonna get rebalanced among the existing consumers of the group).

Now if you produce some messages by running for example ProducerDemoWithKeys program, you see that the messages are split between the 
consumers of the same consume group.

## 51 - Java Consumer Incremental Cooperative Rebalance & Static Group Membership
### Consumer groups and partition rebalance strategies
Whenever you have consumers joining and leaving a group, partitions are going to move and when partitios move between consumers, it's
called a rebalance.
- moving partitions between consumers is called a rebalance
- reassignment of partitions happen when a consumer leaves or joins a group
- it also happens if an administrator adds new partitions into a topic

Q: How do these partitions get assigned to the consumers?

A: Based on the strategy, the outcome is different.
![](./img/51-51-1.png)

Some strategies are:
- eager rebalance

### Eager rebalance
Default behavior(strategy).

- all consumers are gonna stop, give up their membership of partitions. So no consumer is reading from no partitions
- then all consumers are gonna rejoin the group they were in and get a new partition assignment
- during a short period of time, the entire consumer group stops processings. It's called the stop the world event.
- consumers don't necessarily "get back" the same partitions as they used to. In other words, there's no guarantee that your consumers
are going to get back the partitions that they used to have.

So there are two problems with this strategy:
1. maybe you do want your consumers to get back the same partitions they had before
2. you don't want some consumers to stop consuming. You don't want the stop the world event.

Therefore, the next strategy is useful:
### Cooperative reblance(incremental rebalance)
Instead of reassigning all partitions to all consumers, reassign a small subset of the partitions from one consumer to another.

- reassigning a small subset of the partitions from one consumer to another.
- other consumers that don't have reassigned partitions, can still process uninterrupted
- it can go through several iterations to find a "stable" assignment(hence the name "incremental")
- avoids the "stop the world" events where all consumers stop processing data

In image, we see that the incremental rebalance is smart and says: I only need to revoke partition 2, so consumer one and two can keep
on reading from partition zero and one and then after this, the partition 2 is gonna get assigned to consumer 3 and consumer 3 can start
reading from partition 2. So this was less disruptive than eager rebalance. It allowed us to keep on reading from the partitions that did not
get rebalance.
![](./img/51-51-2.png)

### Cooperative reblance, how to use?
The strategies that are not cooperative, are eager strategies. It means every time you use them, there's going to be a stop the world
event.

- kafka consumer: `partition.assignment.strategy`. The default value of this option used to be `RangeAssignor`
  - RangeAssignor: assign partitions on a per-topic basis(can lead to imbalance)
  - RoundRobin: assign partitions across all topics in round-robin fashion, optimal balance. This is also an eager type of assignment
  - StickyAssignor: balanced like RoundRobin and then minimises partition movements when consumer join/leave the group in order to minimize
  movements
  - CooperativeStickyAssignor: rebalance strategy is identical to StickyAssignor but supports cooperative rebalances and therefore
  consumers can keep on consuming from the topic

The default assignor in kafka 3.0 is [RangeAssignor, CooperativeStickyAssignor], it will use the RangeAssignor by default, but allows
upgrading to the CooperativeStickyAssignor with just a single rolling bounce that removes the RangeAssignor from the list.
- kafka connect: if you use kafka connect, cooperative rebalance is already implemented(enabled by default)
- kafka streams: cooperative rebalance is turned on by default using StreamsPartitionAssignor

### Static group membership
Sometimes we want to say: when a consumer leaves, then do not change assignments.

Note: The reason why when a consumer leaves and comes back to the consumer group there's a re-assignment, is that it gets a new member ID. Because
it leaves then upon joining, boom: here's a new member ID for you.

- by default, when a consumer leaves a group, it's partitions are revoked and re-assigned
- if it joins back, it will have a new "member ID" and new partitions assigned
- if you specify `group.instance.id` as part of the consumer config, it makes the consumer a static member
- this is helpful when consumers maintain local state and cache(to avoid re-building the cache)

As you can see in the image below, consumer3 with id=consumer3 left the group but partition 2 is not going to be re-assigned, because
consumer 3 was a static member. Now if the consumer3 joins back within the session timeout(`session.timeout.ms`), then partition 2 will
be re-assigned to that consumer3 automatically without triggering re-balance.

So this allows you to restart your consumer and not be worried that a rebalance is going to happen.

But if your consumer is away for more than `session.timeout.ms`, then a re-balance is going to happen and in this case, partition 2 is gonna move to
different consumer.

This feature is helpful when you have sth like k8s.

![](./img/51-51-3.png)

## 52 - Java Consumer Incremental Cooperative Rebalance Practice
## 53 - Java Consumer Auto Offset Commit Behavior

## 54 - Programming Advanced Tutorials

### 54 - Advanced Programming Tutorials
https://www.conduktor.io/kafka/advanced-kafka-consumer-with-java