package com.akdag.config;

import com.akdag.App;
import com.akdag.kafka.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class KafkaConfig {

    @Bean
    @Qualifier("kafkaProperties")
    public Properties properties() throws IOException {
        Properties props = new Properties();
        InputStream input = App.class.getClassLoader().getResourceAsStream("kafka.properties");
        if (input == null) {
            throw new FileNotFoundException("kafka.properties not found in classpath");
        }
        props.load(input);
        return props;
    }

    @Bean
    public KafkaProducer<String, Message> kafkaProducer(@Qualifier("kafkaProperties") Properties kafkaProperties) {
        return new KafkaProducer<>(kafkaProperties);
    }

}
