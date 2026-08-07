package io.karloslcp.transactionaloutbox;

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
    Queue dlq() {
        return new Queue("dlq", true);
    }

    @Bean
    DirectExchange dlx() {
        return new DirectExchange("dlx");
    }

    @Bean
    Binding dlb(Queue dlq, DirectExchange dlx) {
        return BindingBuilder.bind(dlq).to(dlx).with("dl");
    }

    @Bean
    Queue mainQ(Binding dlb) {
        return QueueBuilder
                .durable("main-q")
                .deadLetterExchange(dlb.getExchange())
                .deadLetterRoutingKey(dlb.getRoutingKey())
                .build();
    }

    @Bean
    TopicExchange mainEx() {
        return new TopicExchange("main-ex");
    }

    @Bean
    Binding mainB(Queue mainQ, TopicExchange mainEx) {
        return BindingBuilder.bind(mainQ).to(mainEx).with("main.*");
    }

    @Bean
    Queue mainDelayQ(Binding mainB) {
        return QueueBuilder
                .durable("main-delay")
                .deadLetterExchange(mainB.getExchange())
                .deadLetterRoutingKey("main.delay")
                .build();
    }

    @Bean
    DirectExchange mainDelayEx() {
        return new DirectExchange("main-delay-ex");
    }

    @Bean
    Binding mainDelayB(Queue mainDelayQ, DirectExchange mainDelayEx) {
        return BindingBuilder.bind(mainDelayQ).to(mainDelayEx).with("delay");
    }
}
