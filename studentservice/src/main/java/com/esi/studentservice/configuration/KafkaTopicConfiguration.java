package com.esi.studentservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


/* Task 1   */
@Configuration
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic requestSubmissionTopicCreation() {
        return TopicBuilder.name("StdRequestSubmitted").build();
    }

    @Bean
    public NewTopic advisorResponseTopicCreation() {
        return TopicBuilder.name("AdvResSubmitted").build();
    }
}
