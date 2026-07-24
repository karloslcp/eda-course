package io.karloslcp.delayedretry;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // --- Dead Letter Queue --- //

    @Bean
    Queue dlQueue() {
        return QueueBuilder
                .durable("dead-letter-queue")
                .build();
    }

    @Bean
    DirectExchange dlExchange() {
        return ExchangeBuilder
                .directExchange("dead-letter-exchange")
                .build();
    }

    @Bean
    Binding dlBinding(Queue dlQueue, DirectExchange dlExchange) {
        return BindingBuilder
                .bind(dlQueue)
                .to(dlExchange)
                .with("dlx-routing-key");
    }

    // --- Delay Queue --- //

    @Bean
    Queue delayQueue() {
        return QueueBuilder
                .durable("delay-queue")
                .deadLetterExchange("main-t8-exchange")
                .deadLetterRoutingKey("main-routing-key")
                .build();
    }

    @Bean
    DirectExchange delayExchange() {
        return ExchangeBuilder
                .directExchange("delay-exchange")
                .build();
    }

    @Bean
    Binding delayBinding(Queue delayQueue, DirectExchange delayExchange) {
        return BindingBuilder
                .bind(delayQueue)
                .to(delayExchange)
                .with("delay-key");
    }

    // --- Main Queue --- //

    @Bean
    Queue mainQueue() {
        return QueueBuilder
                .durable("main-t8-queue")
                .deadLetterExchange("dead-letter-exchange")
                .deadLetterRoutingKey("dlx-routing-key")
                .build();
    }

    @Bean
    TopicExchange mainExchange() {
        return ExchangeBuilder
                .topicExchange("main-t8-exchange")
                .build();
    }

    @Bean
    Binding mainBinding(Queue mainQueue, TopicExchange mainExchange) {
        return BindingBuilder
                .bind(mainQueue)
                .to(mainExchange)
                .with("main-routing-key");
    }
}
