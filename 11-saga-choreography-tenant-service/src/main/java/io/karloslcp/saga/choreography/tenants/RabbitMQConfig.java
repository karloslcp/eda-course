package io.karloslcp.saga.choreography.tenants;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${eda.exchange}")
    private String exchangeName;

    @Value("${eda.billing.failed.queue}")
    private String billingFailedQueueName;

    @Value("${eda.billing.failed.routing-key}")
    private String billingFailedRoutingKey;

    @Value("${eda.billing.compensated.queue}")
    private String billingCompensatedQueueName;

    @Value("${eda.billing.compensated.routing-key}")
    private String billingCompensatedRoutingKey;

    @Bean
    DirectExchange sagaExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    Queue billingFailedQueue() {
        return new Queue(billingFailedQueueName);
    }

    @Bean
    Binding tenantProvisionedBinding(Queue billingFailedQueue, DirectExchange sagaExchange) {
        return BindingBuilder
                .bind(billingFailedQueue)
                .to(sagaExchange)
                .with(billingFailedRoutingKey);
    }

    @Bean
    Queue billingCompensatedQueue() {
        return new Queue(billingCompensatedQueueName);
    }

    @Bean
    Binding billingCompensatedBinding(Queue billingCompensatedQueue, DirectExchange sagaExchange) {
        return BindingBuilder
                .bind(billingCompensatedQueue)
                .to(sagaExchange)
                .with(billingCompensatedRoutingKey);
    }
}
