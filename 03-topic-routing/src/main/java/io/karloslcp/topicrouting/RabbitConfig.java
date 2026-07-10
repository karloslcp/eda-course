package io.karloslcp.topicrouting;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    Queue oneWordMatchedQueue() {
        return new Queue("one-word-match");
    }

    @Bean
    Queue zeroOrMoreWordsMatchedQueue() {
        return new Queue("zero-or-more-words-match");
    }

    @Bean
    Queue anotherQueue() {
        return new Queue("test-queue-n3");
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange("test-topic-exchange");
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange("test-direct-exchange");
    }

    @Bean
    Binding oneWordMatchedBinding(Queue oneWordMatchedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(oneWordMatchedQueue)
                .to(topicExchange)
                .with("tenant.*.provisioned");
    }

    @Bean
    Binding zeroOrMoreWordsMatchedBinding(Queue zeroOrMoreWordsMatchedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(zeroOrMoreWordsMatchedQueue)
                .to(topicExchange)
                .with("tenant.#");
    }

    @Bean
    Binding anotherBinding(Queue anotherQueue, DirectExchange directExchange) {
        return BindingBuilder
                .bind(anotherQueue)
                .to(directExchange)
                .with("tenant.#");
    }
}
