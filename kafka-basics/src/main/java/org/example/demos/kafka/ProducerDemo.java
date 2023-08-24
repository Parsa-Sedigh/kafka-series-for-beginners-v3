package org.example.demos.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I am a kafka producer");

        // create producer properties
        Properties properties = new Properties();

        // the address where kafka broker is started
        // Note: instead of using strings for properties key and values, which can have typos, we use ProducerConfig
//        properties.setProperty("bootstrap.servers", "127.0.0.1:9002");
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");

//        properties.setProperty("key.serializer", "");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

//        properties.setProperty("value.serializer", "");
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create a producer
        KafkaProducer<String, String> producer = new KafkaProducer(properties);

        // create a producer record(this is what we actually gonna send into kafka)
        ProducerRecord<String, String> producerRecord = new ProducerRecord("demo_java", "hello world");

        // send the data - asynchronous operation
        /* Note: If we just stop here, the producer record will never make it into kafka because the program was shut down before the
        producer even had the chance to send the record to kafka(this is an async operation). */
        producer.send(producerRecord);

        // flush data synchronous
        /* producer.flush() means block here up until all the data in my producer being sent, is actually sent and received by kafka. */
        producer.flush();

        // flush and close producer
        /* Note: Doing a producer.close() also calls flush() for you, but we wanted to highlight flush() method exists. */
        producer.close();
    }
}