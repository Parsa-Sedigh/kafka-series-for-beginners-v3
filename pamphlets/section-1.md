# Section 1

## 1-1 - Course Introduction

## 2-2 - Apache Kafka in 5 minutes
- if you have 4 source systems and 6 target systems, you need to write 24 integrations
- each integration comes with difficulties around
  - protocol(because the technologies change) - how data is transported(TCP, HTTP, REST, FTP, JDBC, ...)
  - data format - how the data is parsed(binary, CSV, JSON, avro, protobuf ...)
  - data schema & evolution - what happens if the data changes it's shape, how the data is shaped and may change
- each source system will have an increased load from all the connections

How do we solve these problems?

We do decoupling using kafka.

### Why apache kafka: Decoupling of data streams & systems
Now the source systems are responsible for sending data(producing) into kafka. Now the kafka is gonna have
a data stream(data created in real time) of all your data of all your systems within it. Now the target systems, if they
ever need to receive data from your source systems, they will tap into data of kafka(consuming).

The source systems could be:
- website events
- pricing data
- financial transactions
- user interactions

The target systems could be:
- databases
- analytic systems
- email systems
- audit systems

### Why apache kafka
- distributed, resilient architecture, fault tolerant(which means you can upgrade kafka, you can do kafka maintenance without taking the
  whole system down)
- horizontal scalability
  - can scale to 100s of brokers
  - can scale to millions of messages per second - this is the case of twitter(huge throughput)
- high performance(latency of less than 10ms) - real time

### Kafka use cases
- messaging system
- activity tracking system
- gather metrics from many different locations
- application logs gathering
- stream processing(with kafka streams API for example)
- de-coupling of system dependencies
- integration with Spark, flink, storm, hadoop and many other big data technologies
- microservice pub/sub

### For example
- netflix uses kafka to apply recommendations in realtime while you're watching tv shows
- uber uses kafka to gather user, taxi and trip data in real time to compute and forecast demand and compute surge pricing in real time
- linkedin uses kafka to prevent spam, collect user interactions to make better connection recommendations in realtime

Remember: Kafka is only used as a **transportation** mechanism

## 3-3 - Course Objectives
Kafka producers take data from source systems into the kafka cluster.

Consumers take data from cluster and send it to target systems.

Kafka is managed either using zookeeper or more recently using kafka craft mode.

![](./img/3-3-1.png)

Plus:
- conduktor
- kafka connect
- kafka streams
- confluent schema registry
- kafka architecture in the enterprise
- real world use cases
- advanced API + configurations
- topic configurations

1) kafka connect API: understand how to import/export data to/from kafka 
2) kafka streams API: Learn how to process and transform data within kafka 
3) ksqlDB: write kafka streams applications using SQL 
4) confluent components: REST proxy and schema registry 
5) kafka security: setup kafka security in a cluster and integrate your applications with kafka security 
6) kafka monitoring and operations: use prometheus and grafana to monitor kafka, learn operations 
7) kafka cluster setup & administration: get an understanding of how kafka & zookeeper works, how to setup kafka and various administration tasks 
8) confluent certifications for developers practice exams 
9) confluent certifications for operators practice exams

## 4-4 - Welcome About your instructor