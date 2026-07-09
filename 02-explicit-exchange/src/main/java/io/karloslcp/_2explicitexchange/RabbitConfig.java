package io.karloslcp._2explicitexchange;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    Queue testQueue() {
        return new Queue("test-queue-n2", true);
    }

    @Bean
    DirectExchange olaExchange() {
        return new DirectExchange("test-exchange");
    }

    @Bean
    Binding binding(Queue testQueue, DirectExchange olaExchange) {
        return BindingBuilder
                .bind(testQueue)
                .to(olaExchange)
                .with("ola-k-routing");
    }
}
