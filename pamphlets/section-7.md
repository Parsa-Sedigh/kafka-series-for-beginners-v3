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
## 39-39 - Kafka Consumers in Group
## 40-40 - Kafka Consumer Groups CLI
## 41-41 - Resetting Offsets