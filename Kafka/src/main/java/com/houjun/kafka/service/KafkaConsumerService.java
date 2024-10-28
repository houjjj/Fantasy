package com.houjun.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Service
public class KafkaConsumerService {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;

    private KafkaConsumer<String, String> consumer;

//    @PostConstruct
//    public void init() {
//        Properties properties = new Properties();
//        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
//        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
//        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
//
//        consumer = new KafkaConsumer<>(properties);
//        consumer.subscribe(Collections.singletonList("my-topic"));
//
//        new Thread(this::consumeMessages).start();
//    }

//    public void consumeMessages() {
//        try {
//            while (true) {
//                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
//                for (ConsumerRecord<String, String> record : records) {
//                    System.out.printf("Consumed message: key = %s, value = %s, offset = %d, partition = %d%n",
//                            record.key(), record.value(), record.offset(), record.partition());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            consumer.close();
//        }
//    }
}