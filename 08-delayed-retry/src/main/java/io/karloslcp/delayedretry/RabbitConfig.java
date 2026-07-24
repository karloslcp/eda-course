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

    @Bean
    Queue delay5sQueue() {
        return QueueBuilder
                .durable("5s-delay-queue")
                .ttl(5000)
                .deadLetterExchange("main-t8-exchange")
                .deadLetterRoutingKey("main-routing-key")
                .build();
    }

    @Bean
    Queue delay10sQueue() {
        return QueueBuilder
                .durable("10s-delay-queue")
                .ttl(10000)
                .deadLetterExchange("main-t8-exchange")
                .deadLetterRoutingKey("main-routing-key")
                .build();
    }

    @Bean
    Queue delay15sQueue() {
        return QueueBuilder
                .durable("15s-delay-queue")
                .ttl(15000)
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
    Binding delay5sBinding(Queue delay5sQueue, DirectExchange delayExchange) {
        return BindingBuilder
                .bind(delay5sQueue)
                .to(delayExchange)
                .with("5s-delay");
    }

    @Bean
    Binding delay10sBinding(Queue delay10sQueue, DirectExchange delayExchange) {
        return BindingBuilder
                .bind(delay10sQueue)
                .to(delayExchange)
                .with("10s-delay");
    }

    @Bean
    Binding delay15sBinding(Queue delay15sQueue, DirectExchange delayExchange) {
        return BindingBuilder
                .bind(delay15sQueue)
                .to(delayExchange)
                .with("15s-delay");
    }

    @Bean
    Queue mainQueue() {
        return QueueBuilder
                .durable("main-t8-queue")
//                .deadLetterExchange("delay-exchange-1")
//                .deadLetterRoutingKey("delay-1-routing-key")
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
