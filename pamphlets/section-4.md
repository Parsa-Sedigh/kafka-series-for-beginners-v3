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