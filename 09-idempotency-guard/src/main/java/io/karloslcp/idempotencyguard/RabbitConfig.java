package io.karloslcp.idempotencyguard;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    Queue mainQueue() {
        return QueueBuilder
                .durable("main-queue")
                .build();
    }

    @Bean
    DirectExchange mainExchange() {
        return ExchangeBuilder
                .directExchange("main-exchange")
                .build();
    }

    @Bean
    Binding mainBinding(Queue mainQueue, DirectExchange mainExchange) {
        return BindingBuilder
                .bind(mainQueue)
                .to(mainExchange)
                .with("main-routing-key");
    }

    @Bean
    MessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
