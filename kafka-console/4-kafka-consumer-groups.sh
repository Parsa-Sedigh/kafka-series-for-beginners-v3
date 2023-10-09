# documentation for the command
kafka-consumer-groups.sh

# list consumer groups
kafka-consumer-groups.sh --botstrap-server localhost:9092 --list

# describe one specific group
kafka-consumer-groups.sh --botstrap-server localhost:9092 --describe --group my-second-application

# describe another group
kafka-consumer-groups.sh --botstrap-server localhost:9092 --describe --group my-first-application

# start a consumer
kafka-console-consumer.sh --botstrap-server localhost:9092 --topic first_topic --group my-first-application

# describe the group now
kafka-consumer-groups.sh  --botstrap-server localhost:9092 --describe --group console-consumer-<number>