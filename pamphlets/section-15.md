# Section 15 - Kafka in the Enterprise for Admins
## 104 - Kafka Cluster Setup High Level Architecture Overview
We want multiple brokers in different datacenters to make sure we're not centralizing our failure points.

A classic kafka setup in aws where we have 3 availability zones, is shown in the img. In each AZ, we have one zookeeper and
two kafka brokers. So this is a cluster with 3 zookeepers and 6 brokers.
![](img/104-1.png)

Note that each zookeeper and broker should be on separate servers so we avoid a single point of failure.

It's easy to add brokers in kafka cluster.
`![](img/104-2.png)`

Kafka brokers are not the only thing you have to set up on your cluster, you have to setup kafka connect cluster, schema registry, UI tools,
admin tools and ... .
![](img/104-3.png)

- https://docs.confluent.io/current/kafka/monitoring.html
- https://www.datadoghq.com/blog/monitoring-kafka-performance-metrics/

## 105 - Kafka Monitoring & Operations
Once you have setup a kafka cluster, you need to enable monitoring and also master operations.
![](img/105-1.png)
![](img/105-2.png)

### kafka operations
Kafka operations team must be able to perform the following tasks:
- rolling restart or brokers
- updating configurations for topics and brokers
- rebalancing partitions across your cluster
- increasing or decreasing the replication factor of a topic
- adding/replacing/removing a broker
- upgrading a kafka cluster with zero downtime

It is important that monitoring your own cluster comes with all these responsibilities and more.

https://kafka.apache.org/documentation/#monitoring

## 106 - Kafka Security
### The need for security in apache kafka
- The way we have kafka set up, is that currently any client can access your kafka cluster, that means we have no authentication. 
- Any clients can publish and consume any topic data. That means we have no authorization
- all the data that is transmitted between your clients and kafka is going to be non-encrypted. It's going to be fully visible on the network.
That means we have no encryption

So that means that the risk of running kafka as is on plain text 9092, is that:
- someone can intercept the data being sent.
- someone could publish bad data or even steal data from your kafka cluster
- someone could delete things like topics

So we need security and authentication model.

### In-flight encryption in kafka
Note: There is some performance cost OFC to enable encryption, but if you're using java JDK 11, then it becomes negligible because the
SSL implementations in java got a whole lot better.
![](img/106-1.png)

**Once we have encryption, we need to set up authentication(SSL based auth or SASL).**

With auth, the system knows who the clients are.
![](img/107-1.png)

There are multiple forms of authentication

Note: In SASL/SCRAM, since the auth data is in zookeeper, it means we can change passwords without restarting the brokers.
![](img/107-2.png)

Once a client is authenticated, we know who he is(knows his identity), so we can combine the identity with authorization so that kafka knows
which client can do what.
![](img/107-3.png)
![](img/107-4.png)

## 107 - Kafka Multi Cluster & MirrorMaker

### 107 - Blog on Multi DC Pros and Cons
https://www.altoros.com/blog/multi-cluster-deployment-options-for-apache-kafka-pros-and-cons/

### 107 - Confluent Replicator

### 107 - Kafka Mirror Maker Best Practices

## 108 - Advertised Listeners Kafka Client & Server Communication Protocol