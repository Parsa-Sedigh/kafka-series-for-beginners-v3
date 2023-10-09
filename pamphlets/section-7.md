# Section 7 - CLI Command Line Interface 101

## 34 - CLI Introduction
 
## 35 - WINDOWS WARNING PLEASE READ
WINDOWS NON-WSL2 USERS PLEASE READ
In the next lecture, do not run the command to DELETE topics

Because of a bug here: https://issues.apache.org/jira/browse/KAFKA-1194, it doesn't work. I'm actively working with the Kafka Dev team to
see if I can get this fixed.

In the meantime, please do not delete topics. Otherwise, your Kafka will crash and you won't be able to do the tutorial.

Thanks

## 36-36 - Kafka Topics CLI
### Kafka CLI - how to invoke
- the CLI come bundled with the kafka binaries
- if you setup the `$PATH` variable correctly(from the kafka setup part), then you should be able to invoke the CLI from anywhere
on your computer
- using `kafka-topics` command, we can create, delete, describe or change a topic
- if you installed kafka using binaries, it should be either `kafka-topics.sh` - ending in `.sh` (linux, mac, windows), `kafka-topics.bat`(windows non WSL2),
`kafka-topics`(homebrew, apt, ...)
- use `--bootstrap-server` option everywhere, not `--zookeeper`. For example:
    - correct command: `kafka-topics --bootstrap-server` localhost:9092
    - incorrect command: `kafka-topics --zookeeper` localhost:9092

### Kafka CLI - in case of errors
- if the `$PATH` wasn't setup correctly, don't sweat
- just use the full path to kafka binaries: `kafka_2.13-3.0.0/bin/kafka-topics.sh`

## 37-37 - Kafka Console Producer CLI
### Kafka CLI - kafka-topics.sh
1. create kafka topics
2. list kafka topics
3. describe kafka topics
4. increase number of partitions in a kafka topic
5. delete a kafka topic

Note: If you're on windows non WSL2, don't delete a topic because things will crash!

```shell
# List all topics in your kafka cluster:
# use --bootstrap-server to specify the broker we're trying to connect to
kafka-topics --bootstrap-server localhost:9092 --list

# Create a topic
kafka-topics --bootstrap-server localhost:9092 --topic <topic name>

# create a topic with more than one partition(number of topics and replication factor by default is 1)
kafka-topics --bootstrap-server localhost:9092 --topic <topic name> --partitions 3

kafka-topics --bootstrap-server localhost:9092 --topic <topic name> --replication-factor 2

# describe a topic
kafka-topics --bootstrap-server localhost:9092 --describe --topic <topic name>

# describe all the topics in the cluster
kafka-topics --bootstrap-server localhost:9092 --describe

# delete a topic
kafka-topics --bootstrap-server localhost:9092 --delete --topic <topic name>
```

Note: The topic names that have underscore will collide with the ones with dots and vice-versa. So if you create a topic named: `first_topic`,
it will collide with `first.topic` and you can't create this and vice-versa.

Note: The number of replication factor can not be more than the number of brokers in our cluster.

## 38-38 - Kafka Console Consumer CLI
### Kafka CLI: kafka-console.consumer.sh
![](./img/38-38-1.png)

```shell
# Replace "kafka-console.consumer.sh" by "kafka-console.consumer" or "kafka-console.consumer.bat" based on your 
# system(or /bin/kafka-console.consumer.sh or \bin\windows\kafka-console.consumer.bat if you didn't setup PATH / environment variable)
kafka-console.consumer.sh

# consuming(consume a topic)
kafka-console.consumer.sh --bootstrap-server localhost:9092 --topic first_topic

# other terminal
# This will prompt you to write a message and by pressing enter, the message will appear in other terminal window(consumer)
kafka-console.producer.sh --bootstrap-server localhost:9092 --topic first_topic --from-beginning

# display key, values and timestamp of the message in consumer
kafka-console.consumer --bootstrap-server localhost:9092 --topic first_topic --formatter kafka.tools.DefaultMessageFormatter
--property print.timestamp=true --property print.key=true --property print.value=true --from-beginning
```
When you run `kafka-console.consumer.sh`, it won't receive the history of messages. In order to read from the history of the topic,
run the command with `--from-beginning` option to read the old messages in kafka topic. This isn't the default behavior because the topic
could be very large and you don't want to read all the messages. With this flag, we read from the last committed offset.

By default, consumer consumes from tail of the topic, but you can overwrite this to read from the beginning of the topic with this flag.

Note: When the consumer **group** has already committed the consumer offsets, if we start a new consumer in that group with `--from-beginning`,
it won't read the messages that were committed by the consumer group.

Note: Using this flag, on consumer, it looks like everything is out of order. No, it is in order. Because remember a topic can have multiple
partitions and therefore that means that the **messages are going to be in order within each partition**. But the messages are sent to 
different partitions and it can be round-robin(which partition to get the message) and therefore, in consumer, we're getting the messages
in the order of messages in each partition but not across partitions. For example we get the messages from partition 1 and then partition 2.
But you first message might be sent to partition 2 and second message to partition 1. So this might seem out of order. But it's not.

Only if you had a topic with one partition, then all the messages will be in order. But then you would lose the scaling aspect of kafka, because
you would only have one partition and therefore only one consumer at the same time.

## 39-39 - Kafka Consumers in Group
### CLI consumer in groups with kafka-console-consumer.sh
![](./img/39-39-1.png)

Look at `3-kafka-console-consumer-in-groups.sh` .

When the number of consumers in the same group, are more than partitions, some of consumers will be idle.

Note: When we have consumers in the same group, the messages will be load balanced between them. But when we have multiple consumer groups
reading the same topic partitions, all of those groups will receive the messages! So each consumer group get the messages.

## 40-40 - Kafka Consumer Groups CLI
### Consumer group management CLI - kafka-consumer-groups.sh
Multiple consumer groups can read from the same topic.
![](./img/40-40-1.png)

`4-kafka-consumer-groups.sh`

When describing a consumer group, `CURRENT-OFFSET` shows how far this consumer group has read from the topic and `LOG-END-OFFSET` shows
how many messages are in that topic. If `CURRENT-OFFSET` and `LOG-END-OFFSET` are equal, it means that consumer group has read all the messages
of the topic. This is why the `LAG` is 0. If the consumer group hasn't read until the end of the topic, we would have a LAG greater than 0.

Note: You can have a consumer group with no actual consumers inside.

## 41-41 - Resetting Offsets
### consumer groups - reset offsets - kafka-consumer-groups.sh
Consumers commit offsets once in a while, which allows them to restart the reads from where the offset was last committed.
![](./img/41-41-1.png)