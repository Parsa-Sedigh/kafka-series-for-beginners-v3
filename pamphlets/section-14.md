# Section 14 - Real World Insights and Case Studies Big Data Fast Data

## 96 - Choosing Partition Count & Replication Factor
### Partitions count & replication factor
If you increase the replication factor during the topic lifecycle, you're going to put more pressure on the cluster because you're
going to have more network communication and more disk space use.

![](../pamphlets/img/96-1.png)

Note: Higher number of partitions means possibly a higher number of consumers in a group.

The problem with more elections to perform for zookeeper is gonna be solved if using kraft mode.

How do we get these numbers right from the get go?

### Choosing the right partitions count
![](../pamphlets/img/96-2.png)

When you have more partitions, you are going to have more elections to perform in case a broker goes down(if using zookeeper, this problem
is gonna get solved using KRaft). 

### Cluster guidelines
Max total number of partitions in the cluster => 200,000 partitions

Kraft mode was created to scale to a lot more partitions than zookeeper(millions instead of 200,000).

## 97 - Kafka Topics Naming Convention
https://cnr.sh/essays/how-paint-bike-shed-kafka-topic-naming-conventions

![](../pamphlets/img/97-1.png)

## 98 - Case Study MovieFlix
### Video analytics - movieflix
#### Resuming a video
![](img/98-1.png)

First topic to discuss is Show Position which is a topic that tells us how far the users have consumed a tv show even within a video.
So sth needs to produce to that topic. For this, we know there's a video player on people's browser and while the video is playing,
once in a while, it will send some data to sth that is called the **video position service** and it's kinda a proxy and that service is a producer
and it will send the data it received from the video players, to kafka, to the show position topic. The video position service also needs to
do some work to make sure that the data it receives is correct before sending it to kafka.

To implement the resume capability, we can have a resuming service which is the consumer of the show position topic and it's gonna have a
DB of how far each user for each show they've been consuming, it doesn't need to know every consumption point, it just needs to know the latest.

So when someone opens up their browser because they stopped playing a video and they want to restart it two days later, then the video player
does a request to the resuming service to find the point where the user last time played the video till that point and receives that point.

So that's for the resume and storing position.

#### Recommendations
In show position topic we have data about which user watches which show and how far? and that's really good data. Because we know exactly 
which users what they like and if they go all the way to the end of the season for the show, well that indicates they must have liked it, if not,
if they stop for five minutes and never get back to it, they must have hated it. So there could be a recommendation engine in real time that is
maybe powered by kafka streams, that takes the show position performs some good algorithm and from there, creates recommendations in real time.

Now these recommendations are consumed by a recommendation service and basically whenever a user quites his video player and goes back to
the list of tv shows page, the recommendation service is gonna send the right movies.

All this data is really good data and shouldn't stay in kafka, so there should be analytics consumer powered by kafka connect to put the data
into an analytics store for further processing such as hadoop.

Even that analytics store can power our recommendation engine in real time(there's no arrow for this in the img).
![](img/98-2.png)

### View analytics - movieflix - comments
The show_position topic can have multiple producers because all around the world people are gonna watch tv shows, so multiple producers will
put data into the topic(for example in each region we can put a couple of producers).

It should be highly distributed because there's gonna be high volume, so we probably need 30 partitions(first measure).

The key would be user_id for a topic and that's because we want all the data for one user to be in order, we don't care about ordering across users.

The recommendation topic is gonna have less partitions than show_position topic because it has a lower volume(there's not gonna be new recommendations
every 30 seconds).
![](img/98-3.png)

## 99 - Case Study GetTaxi
### IoT example - GetTaxi
![](img/99-1.png)

We have a `user_position` topic which contains all the users position when they open their app and leave it open. Because we need to know
where the users are. So we have a user application and it needs to get into kafka(send or get data). But we never connect applications
directly to kafka, we always use a service as a proxy, so there should be a user position service receiving data from the user application
and that service is going to be a producer for our user_position topic. We get high volume of data here.

There should be the taxi driver application, similarly that's going to produce to a service to send position data to the `taxi_position` topic
and here we also gonna have high volume of data.

The volume of data for taxis are gonna be more than users.

We chose to make them two separate topics because a user and a taxi driver are very two different entities.

For `surge_pricing` topic we need to take user position and taxi position into account. So we should have a surge pricing computation model
with simple or maybe complicated rules using kafka streams, or apache spark.

Both user_position and taxi_position topics are gonna be both read by the kafka streams application(computation model) and it will output
to one topic which is `surge_pricing`. Note that kafka streams can take as many input topics as it wants and perform whatever computation
on them.

Now we can feed the generated pricing in surge_pricing topic, into the user application to give the user an estimated cost and for this we need a
**taxi cost service** which is going to be a consumer of our surge_pricing topic.

Finally, to make our data science happy, because they want to get all this information into their analytics DBs and stores to do whatever they want,
then we should use kafka connect and this time instead of hadoop, maybe our analytics store is amazon s3. So there is a kafka connector for doing this.

In this architecture, our kafka streams app is reading from two topics instead of one contrary to the video streaming service app that we discussed.

![](img/99-2.png)

### IoT example - GetTaxi - comments
The cluster should be highly distributed because we can get a peak in the volume of data like in new year's eve and ... .

If we were to choose a key, for the user_position topic we choose user_id and for taxi_position, we choose taxi_id because we definitely want to
get my data from users and taxis in order.

We don't need to store data in kafka for long time, so we don't need a long retention period.

The data of surge_pricing topic comes from computation model which itself gets data from user_position and taxi_position topic, hence the topic to
topic transformations, so we need kafka streams here.
![](img/99-3.png)

## 100 - Case Study MySocialMedia
### CQRS - MySocialMedia
![](img/100-1.png)

### CQRS - MySocialMedia - architecture
We need 3 topics:
- posts
- likes
- comments

We need to get user posts. The user is gonna create some post and there's gonna be a posting service(the post has text, links, media, hashtags) and
this posting service on it's own will create and send all the data after validation to the `posts` topic.

For likes and comments, there's gonna be a user likes and comments service and it's gonna be a producer. The users are going to like and comment
and this producer will send data to `likes` and `comments` topics. Note that we could make the likes and comments in the same topic, but the data
is different enough to have two topics.

So here we have an example of a producer that produces to two different topics.

What we want at the end(we want to consume to send data back to the app) is a `posts_with_counts`(posts with **counts** of likes and comments), but
we have to realize that the data comes from all over and it could be multiple people liking at the same time or commenting at the same time
and so if we were doing this against a traditional DB, the DB would be swamped and there would be some race conditions and .... , but using kafka,
we're able to decouple the production of data to the actual aggregation of data and for this, we'll use kafka streams.

Kafka streams will read the data from the posts, likes and comments topics and it will perform some aggregations such as how many likes for my post?
and ... and put this into the `posts_with_counts` topic.

Now similarly for trending posts(posts that are very liked and have many comments), we could have a new kafka streams app that figures out
the trending posts in the past hour for example. The trending posts can be the posts with the most likes or the most comments or a combination of both
and send this data to `trending_posts` topic.

The refresh feed service(consumer) is gonna consume from posts_with_counts and trending feed service which could be a consumer
or could be kafka connect sync, is gonna consume from trending_posts and finally they send the data to the website so that the users can see it.

Now why is this CQRS?

In img, on the left side, we have the commands which indicates the action like user liked this post and ... and on the right side(right side of kafka)
we have the queries and commands and queries are decoupled and that decoupling is what makes this architecture scalable for a lot of users.

Finally on the bottom left, once we have figured out the queries results, we're free to read it anyway we want into whatever store we want,
so the website reads from a decoupled read store and so even if there's a high like or high comment pressure on the website,
the read store itself is just fine.
![](img/100-2.png)

### CQRS - MySocialMedia - comments
We want to get the posts by a user in order, so we use user_id as the key. But for likes and comments topics, we want to use the post_id
instead of user_id as the key. Because we want to have all the likes and all the comments for each post to be in the same partition, that will
help the kafka streams app to make the aggregation properly

When the data is produced, it should be formatted as events. What is an event? Like user_123 created a post with id of post456 at 2 pm and similarly
for liking and deleting a post. An event describes what happened.
![](img/100-3.png)

## 101 - Case Study MyBank
### Finance application - MyBank
![](img/101-1.png)

### Finance application - MyBank - architecture
There must be a `bank_transactions` topic.

All the bank transactions is **already** in a DB, so we want to get that information from the DB into kafka and for this, we're gonna
use kafka connect source and there is sth called the CDC connector.

CDC = change data capture. and one of these is debezium which allows us to read all the records in real time from DB and put them right away
into kafka.

So we don't need to write a producer for the data in DB, instead we can use kafka connect source.

We need `user_settings` topic. Users will set their thresholds in the app and it talks to the app threshold service which is going to be a 
proxy producer which sends data to the `user_settings` topic.

We need `user_alerts` topic which holds the results.

We need a kafka streams application that takes all the bank transactions from `bank_transactions` topic and the `user_settings` topic and checks
if the transaction is greater than the user threshold specified in `user_settings`, if so, create a message in the `user_alerts` topic
and notification service(consumer) will pull from `user_alerts` and send a notification to the user.

![](img/101-2.png)

About kafka streams app:

When a user changed his setting, the alerts won't be triggered for past transactions, because we only read from the new transactions onward.

User thresholds topic is actually user_settings in the img.

![](img/101-3.png)

## 102 - Case Study Big Data Ingestion
Kafka historically was created to do big data ingestion. It was very common in the old days to have generic connectors that will take data and
put it into kafka and then from kafka to offload it into HDFS, amazon s3 or elasticsearch. Kafka can serve a double purpose in that case,
it can be a speed layer while having a slow layer that will have applications extract, in a batch manner and put data into data stores(HDFS, s3) where
you wanna do analytics.

![](img/102-1.png)

The architecture:

![](img/102-2.png)

The speed layer can be kafka consumer or big data frameworks like spark, storm or ... that allows you to perform real time analytics,
create dashboards and alerts, apps and consumers. Now if you don't want to do stuff in real time, instead you want to analyze things in batch,
then you can have kafka connect or a kafka consumer to take all of your data from kafka(broker) and send it to hadoop(HDFS), amazon s3, RDBMs,
elasticsearch or ... . This way, you can perform data science, reporting, audit or just for backup/long term storage.

This is a common architecture with kafka.

## 103 - Case Study Logging and Metrics Aggregation