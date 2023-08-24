package org.example.demos.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I am a kafka consumer");

        Properties properties = new Properties();

        String boostrapServer = "127.0.0.1:9092";
        String groupId = "my-group-id";
        String topic = "demo_java";

        // create consumer configs
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // create the consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer(properties);

        // subscribe consumer to our topic(s).
        consumer.subscribe(Collections.singleton(topic));

        // If you wanna subscribe to multiple topics, write:
//        consumer.subscribe(Arrays.asList(topic));

        // pull for new data
        while (true) {
            log.info("Polling");

            /* Hey, consumer: Poll kafka and get as many records as you can. If you don't have any reply from kafka, then I'm willing to
            wait up to 100 milliseconds to get me some messages. Now if by this 100 milliseconds, no records have been received,
            then go to the next line of code and `records` will be an empty collection.
            So poll() either returns right away with some records or it waits up until the timeout to return empty.*/
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record: records) {
                log.info("Key: " + record.key() + ", value: " + record.value());
                log.info("Partition: " + record.partition() + ", offset: " + record.offset());
            }
        }
    }
}
