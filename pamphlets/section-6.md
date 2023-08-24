# 6 - Starting Kafka without Zookeeper

## 30 - Note not ready for production yet
Starting Kafka without Zookeeper (KRaft mode)

These lectures have been included as a demo if you are curious about trying out this mode, to see the future of Apache Kafka. 

Note: the KRaft mode is not ready for production yet.

If you have successfully completed the previous section, you can skip these lectures and go straight into the next section.

Happy learning!

## 31 - Mac OS X Start Kafka in KRaft mode
### Mac OS X: One kafka broker = KRaft mode(no ZK)
We're gonna start a kafka cluster in KRaft mode. So we just have one broker and no zookeeper.

This mode is in early access mode.

KRaft is in early access mode as of apache kafka 2.8 and should be used in development only. It is not suitable for production.

1. generate a cluster id and format the storage using `kafka-storage.sh`. Like:
```shell
<path to kafka bin folder>/kafka-storage.sh random-uuid

# if you have added bin directory of kafka to the $PATH, just run: kafka-storage.sh random-uuid
```
This is gonna generate a cluster ID for your cluster.
2. start kafka using binaries
```shell
<path to kafka bin folder>/kafka-storage.sh format -t <uuid> -c <path to kafka folder>/config/kraft/server.properties
```
After running this, it would print: `Formatting /tmp/kraft-combined-logs` and this is where kafka is going to store it's data.

Stop the commands that you're running in order to run kafka with zookeeper(stop kafka server and zookeeper).

You would only need one terminal to run kafka in kraft mode.

Now we can run the kafka broker:
```shell
kafka-server-start.sh <path to kafka folder>/config/kraft/server.properties
```

### 31 - Mac Kafka KRaft Conduktor Kafkademy
https://www.conduktor.io/kafka/how-to-install-apache-kafka-on-mac-without-zookeeper-kraft-mode

### 32 - Linux Kafka KRaft Conduktor Kafkademy
https://www.conduktor.io/kafka/how-to-install-apache-kafka-on-linux-without-zookeeper-kraft-mode

## 32 - Linux Start Kafka in KRaft mode

## 33 - Windows WSL2 Start Kafka in KRaft mode
### 33 - Windows Kafka KRaft Conduktor Kafkademy
https://www.conduktor.io/kafka/how-to-install-apache-kafka-on-windows-w ithout-zookeeper-kraft-mode