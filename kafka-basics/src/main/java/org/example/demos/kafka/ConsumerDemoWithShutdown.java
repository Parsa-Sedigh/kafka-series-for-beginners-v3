package org.example.demos.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemoWithShutdown {
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

        KafkaConsumer<String, String> consumer = new KafkaConsumer(properties);

        // get a reference to the current thread
        final Thread mainThread = Thread.currentThread();

        // add the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Detected a shutdown, let's exit by calling consumer.wakeup()...");

                /* Using consumer.wakeup() the next time the consumer is going to do consumer.poll() , instead of working, it's gonna throw
                an exception and it's called a WakeupException. So after consumer.wakeup() is called, the consumer.poll() call will throw
                an exception of type `WakeupException` and that causes the infinite while loop to be done.*/
                consumer.wakeup();

                // wait for the code in the main to finish
                // join the main thread to allow the execution of the code in the main thread
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        try {
            consumer.subscribe(Collections.singleton(topic));

            while (true) {
//                log.info("Polling");

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record: records) {
                    log.info("Key: " + record.key() + ", value: " + record.value());
                    log.info("Partition: " + record.partition() + ", offset: " + record.offset());
                }
            }
        } catch (WakeupException e) {
            // WakeupException is an expected exception. So we use info() method.
            log.info("Wake up exception!");

            // we ignore this as this is an expected exception when closing a consumer

        } catch (Exception e) { // if we catch an exception that we don't know about - it would be an unexpected exception, so we use error() method
            log.error("Unexpected exception");
        } finally { // no matter what the exception is, this block will always be called
            /* is gonna gracefully close the consumer and close the connection to kafka which allows our consumer group to do a proper re-balance.
            This will also commit the offsets if need to */
            consumer.close();

            log.info("The consumer is now gracefully closed");
        }
    }
}
