package org.example.demos.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemoCooperative {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I am a kafka consumer");

        Properties properties = new Properties();

        String boostrapServer = "127.0.0.1:9092";
        String groupId = "my-third-application";
        String topic = "demo_java";

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // force the consumer to go only in CooperativeStickyAssignor mode.
        properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
//        properties.setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "some id");

        KafkaConsumer<String, String> consumer = new KafkaConsumer(properties);

        consumer.subscribe(Collections.singleton(topic));

        while (true) {
            log.info("Polling");

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record: records) {
                log.info("Key: " + record.key() + ", value: " + record.value());
                log.info("Partition: " + record.partition() + ", offset: " + record.offset());
            }
        }
    }
}
