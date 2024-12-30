package com.akdag;

import com.akdag.kafka.Message;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream("kafka.properties")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Properties file 'kafka-consumer.properties' not found in resources folder");
            }
            properties.load(inputStream);
        }

        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(Collections.singletonList("topic"));

        while (true) {
            ConsumerRecords<String, Message> records = consumer.poll(Duration.ofMillis(1000));
            if (records.isEmpty()) {
                System.out.println("Nothing to read.");
            } else {
                for (ConsumerRecord<String, Message> record : records) {
                    System.out.println(String.format(
                            "Key = %s, Value = %s, Topic = %s, Partition = %d, Offset = %d",
                            record.key(), record.value(), record.topic(), record.partition(), record.offset()
                    ));
                }
            }
        }

    }
}
