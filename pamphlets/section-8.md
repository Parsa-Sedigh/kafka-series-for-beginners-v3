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

## 47 - Java Producer with Keys
## 48 - Java Consumer
## 49 - Java Consumer Graceful Shutdown
## 50 - Java Consumer inside Consumer Group
## 51 - Java Consumer Incremental Cooperative Rebalance & Static Group Membership
## 52 - Java Consumer Incremental Cooperative Rebalance Practice
## 53 - Java Consumer Auto Offset Commit Behavior

## 54 - Programming Advanced Tutorials

### 54 - Advanced Programming Tutorials
https://www.conduktor.io/kafka/advanced-kafka-consumer-with-java