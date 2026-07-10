package io.karloslcp.fanout;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    Queue queueOne() {
        return new Queue("queue-one", true);
    }

    @Bean
    Queue queueTwo() {
        return new Queue("queue-two", true);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("test-fanout-exchange");
    }

    @Bean
    Binding queueOneBinding(Queue queueOne, FanoutExchange fanoutExchange) {
        return BindingBuilder
                .bind(queueOne)
                .to(fanoutExchange);
    }

    @Bean
    Binding queueTwoBinding(Queue queueTwo, FanoutExchange fanoutExchange) {
        return BindingBuilder
                .bind(queueTwo)
                .to(fanoutExchange);
    }
}
