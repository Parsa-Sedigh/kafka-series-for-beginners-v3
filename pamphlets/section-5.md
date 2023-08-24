# Section 5 - Starting Kafka

## 17-17 - Important Starting Kafka & Lectures Order
### Starting kafka - a big challenge
Why is starting kafka a big challenge?

It's very difficult and instructions vary based on different platforms and you need to start kafka and zookeeper.

- no-brainer solution: starting kafka using conduktor(free - all platform) using a UI
- alternatives for mac OS X
    - starting kafka with zookeeper(recommended)
    - starting kafka without zookeeper - KRaft mode(for development only)
    - installing kafka using brew
- alternatives for linux
    - starting kafka with zookeeper(recommended)
    - starting kafka without zookeeper - KRaft mode(for development only)
- alternatives for windows
    - windows wsl2: starting kafka with zookeeper(recommended)
    - windows wsl2: starting kafka without zookeeper - KRaft mode(for development only)
    - plain windows: not recommended, there are caveats that I will show you, conduktor helps

### Important: Starting kafka
- locally: it will be accessible on 127.0.0.1(localhost)
- natively: we will use the native kafka binaries from the website(it's the same even if using conduktor)
- with **ONE** broker and (optionally) ONE zookeeper only(perfect for development)

Note: the production-ready kafka cluster setup takes over 4 hours and is dedicated to another course in the apache kafka series.

### 17 - Install-Kafka-Diagram

### 17 - Starting Kafka Conduktor Kafkademy
https://www.conduktor.io/kafka/starting-kafka

## 18 - FAQ for Setup Problems
Please refer to this lecture first if you have set up problems.

We recommend starting Kafka with Conduktor if you're having issues even after troubleshooting

=====================

> Zookeeper - java.net.BindException: Address already in use

Something is already occupying your port 2181. Figure out which application it is and stop it

> Kafka - org.apache.kafka.common.KafkaException: Socket server failed to bind to 0.0.0.0:9092: Address already in use.

Something is already occupying your port 9092. Figure out what it is and stop it.
Otherwise, if you really insist, you can change the Kafka port by adding the following line to server.properties

### example for port 9093
listeners=PLAINTEXT://:9093

> My topics are losing their data after a while

This is how Kafka works. Data is only retained for 7 days.

> The topics list is disappearing

Make sure you have changed the Zookeeper dataDir=/path/to/data/zookeeper , and Kafka log.dirs=/path/to/data/kafka

> I have launched Kafka in a VM or in the Cloud, and I can't produce to Kafka

If you can't produce to Kafka, it's possible you are using a VM and this can break the Kafka behaviour. Please look at the 
annex lectures for solutions of how to deal with that. I strongly recommend doing this tutorial using the Kafka binaries and localhost

## 19 - Starting Kafka with Conduktor Multi Platform
- fixes many issues you have with kafka on windows
- start any kafka version(easy to switch)
- can add separate components such as schema registry

Even if you do start kafka with conduktor, you still need to install the kafka CLI tools on your OS.

### 19 - Conduktor Download Page

## 20 - Mac OS X Download and Setup Kafka in PATH
### Mac OS X: Setup kafka binaries
- necessary step regardless if you use conduktor or not to start kafka. Because we're going to launch CLI commands using this method
- this is so that we can start running kafka cli commands

1. install java JDK version 11
2. download apache kafka from https://kafka.apache.org/downloads under binary downloads. Download the one with the latest scala version.
3. extract the contents on your mac
4. setup the $PATH environment variable for easy access to the kafka binaries

Note: This step can be replaced with "brew" which is demonstrated after all mac setup videos.

To download JDK, you can use **amazon coretto** which is a good distribution of java JDK.

```shell
java --version
```

After extracting the `tgz` file, put the contents into your user's directory on mac, the path would be: `/Users/parsa`

To run kafka commands, instead of using the full path of where the command programs are located, we would like to just write the name of the
command and run it, instead of specifying the full path. To do so, we need to add kafka binaries to $PATH:
```shell
nano <path to .zshrc>

# update the $PATH to include where the kafka binaries are installed:
PATH="$PATH:<absolute path to where the downloaded kafka bin directory is located>"
```

To verify kafka bin directory is added to $PATH, run one of the commands in that directory, for example, run:
```shell
kafka-topics.sh
```

We have added kafka to $PATH.

Now we need to start kafka using it's command lines.

### 20 - Install Kafka on Mac Conduktor Kafkademy

### 20 - Kafka downloads page

## 21 - Mac OS X Start Zookeeper and Kafka
### Mac OS X: one kafka broker - with zookeeper
We have a kafka cluster of one broker and one zookeeper.

1. start zookeeper using the kafka binaries
2. start kafka using the kafka binaries in another process

Open two terminal windows, then run command below in one window: `zookeeper-server-start.sh`
If you run this, it says: `sh [-daemon] zookeeper.properties`. Which means it doesn't work because it needs to have a `zookeeper.properties` filed to it.
Thankfully, the `zookeeper.properties` file is available within the `<kafka directory>/config/zookeeper.properties`.

This file tells zookeeper how to get started. Now run, this command to start zookeeper.
```shell
# start zookeeper
# Pass the path to zookeeper.properties file
zookeeper-server-start.sh /Users/<username>/kafka_2.13-3.5.1/config/zookeeper.properties
```

Now in another terminal window, we wanna start kafka: `kafka-server-start.sh`
But it won't work, it says: `USAGE: /Users/<username>/kafka_2.13-3.5.1/bin/kafka-server-start.sh [-daemon] server.properties [--override property=value]*`.
It means we need to pass `server.properties` file with the full path:
```shell
# start kafka
kafka-server-start.sh /Users/<username>/kafka_2.13-3.5.1/config/server.properties
```

keep both these terminal windows open. With konduktor, you don't need to run these commands, konduktor runs them itself.

**Optional:** You can change the kafka and zookeeper data storage directory.
1. For zookeeper: `zookeeper.properties` and set the following to your heart's desire:
`dataDir=/your/path/to/data/zookeeper`. Default option is: `dataDir=/tmp/zookeeper`
2. For kafka: edit `server.properties` and set the following to your heart's desire:
`log.dirs=/your/path/to/data/kafka`. Default option is: `log.dirs=/tmp/kafka-logs`

The defaults are the temporary files.

## 22 - Mac OS X Using brew
### Mac OS X: start kafka using brew
An alternative to installing java(brew will install it automatically) and then installing kafka binaries and then setting up the $PATH, we 
can use a shortcut and install kafka using brew.
1. install brew
2. install kafka using brew(will install java JDK for you)
3. start zookeeper using the binaries
4. start kafka using the binaries in another process

When kafka is installed with brew, you shouldn't use `.sh` extension files to use the commands, so for example use: `kafka-topics` and 
**not** `kafka-topics.sh`.

### 22 - Kafka Brew Conduktor Kafkademy
https://www.conduktor.io/kafka/how-to-install-apache-kafka-on-linux

## 23 - Linux Download and Setup Kafka in PATH
### 23 - Install Kafka on Linux Conduktor Kafkademy
https://www.conduktor.io/kafka/how-to-install-apache-kafka-on-linux

### 23 - Kafka Downloads Page
https://kafka.apache.org/downloads

## 24 - Linux Start Zookeeper and Kafka
## 25-25 - Windows WSL2 Download Kafka and PATH Setup

### 25 - Install Kafka on Windows WSL2 Conduktor Kafkademy
https://www.conduktor.io/kafka/how-to-install-apache-kafka-on-windows
### 25 - Kafka Downloads Page
## 26 - Windows WSL2 Start Zookeeper & Kafka
## 27 - Windows WSL2 How to Fix Problems

## 28 - Windows WSL2 Extra Instructions
IMPORTANT: If you're using Kafka on Windows WSL2

When running a command from the CLI outside of WSL2 or your Java programs, you may get the following error:

If do you get that error, please run the instructions below
(this is to edge a WSL2 networking bug with Kafka, so we have to disable IPv6 on WSL2 to remediate)

First, stop Kafka and Zookeeper.

Then, please run these commands on your end on WSL2, one by one.

Your password will be prompted on the first command.

sudo sysctl -w net.ipv6.conf.all.disable_ipv6=1
and

```shell
sudo sysctl -w net.ipv6.conf.default.disable_ipv6=1
```
and then, go to config/server.properties and edit the file to add the line:

listeners=PLAINTEXT://localhost:9092

When the three commands have succeeded, relaunch Zookeeper and Kafka.

You should be able to connect from your Java programs or CLI without a problem!

In case the fix above does not help

Try the fixes outlined in these pages:

https://docs.conduktor.io/kafka-cluster-connection/setting-up-a-connection-to-kafka/connecting-to-kafka-running-on-windows-wsl-2

https://stackoverflow.com/questions/64177422/unable-to-produce-to-kafka-topic-that-is-running-on-wsl-2-from-windows

If after using these fixes, you still can't connect to Kafka, we recommend either
Running Kafka on Windows non-WSL2 (next lecture)

Running Kafka using Conduktor

Happy learning!

## 29 - Windows nonWSL2 Start Zookeeper and Kafka