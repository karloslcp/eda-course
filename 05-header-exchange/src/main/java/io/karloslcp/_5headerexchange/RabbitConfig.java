package io.karloslcp._5headerexchange;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
public class RabbitConfig {

    @Bean
    Queue queue() {
        return new Queue("new-test-queue", true);
    }

    @Bean
    HeadersExchange headersExchange() {
        return new HeadersExchange("test-headers-exchange");
    }

    @Bean
    Binding binding(Queue queue, HeadersExchange headersExchange) {
        return BindingBuilder
                .bind(queue)
                .to(headersExchange)
//                .where("x-ola").exists(); // didn't work
                .where("ola").matches("k-ase"); // didn't work'
//                .whereAll("x-ola").exist(); // didn't work
//                .whereAll(Map.of("x-ola", "k-ase")).match();

        // the reason some attempts didn't work is because the headers with
        // the "x-" prefix are not recognized by RabbitMQ by default."
    }
}
