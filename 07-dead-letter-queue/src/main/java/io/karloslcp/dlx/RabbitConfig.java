package io.karloslcp.dlx;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    Queue mainQueue() {
        return QueueBuilder
                .durable("main-queue")
//                .withArgument("x-dead-letter-exchange", "dlx-exchange")
                .deadLetterExchange("dlx-exchange")
//                .withArgument("x-dead-letter-routing-key", "dl-routing-key")
                .deadLetterRoutingKey("dl-routing-key")
                .build();
    }

    @Bean
    Queue deadLetterQueue() {
        return new Queue("dlq-queue");
    }

    @Bean
    TopicExchange mainExchange() {
        return new TopicExchange("main-exchange");
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange("dlx-exchange");
    }

    @Bean
    Binding mainBinding(Queue mainQueue, TopicExchange mainExchange) {
        return BindingBuilder
                .bind(mainQueue)
                .to(mainExchange)
                .with("main.routing.#");
    }

    @Bean
    Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with("dl-routing-key");
    }
}
