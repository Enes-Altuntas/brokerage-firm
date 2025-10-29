package com.inghubs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableKafka
@Configuration
@EnableScheduling
@EnableKafkaRetryTopic
public class KafkaConfig {

}
