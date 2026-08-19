package io.karloslcp.saga.choreography.billing;

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

    @Value("${eda.tenants.provisioned.queue}")
    private String tenantProvisionedQueue;

    @Value("${eda.tenants.provisioned.routing-key}")
    private String tenantProvisionedRoutingKey;

    @Value("${eda.roles.failed.queue}")
    private String rolesFailedQueue;

    @Value("${eda.roles.failed.routing-key}")
    private String rolesFailedRoutingKey;

    @Bean
    DirectExchange sagaExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    Queue tenantProvisionedQueue() {
        return new Queue(tenantProvisionedQueue);
    }

    @Bean
    Binding tenantProvisionedBinding(Queue tenantProvisionedQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(tenantProvisionedQueue).to(sagaExchange).with(tenantProvisionedRoutingKey);
    }

    @Bean
    Queue rolesFailedQueue() {
        return new Queue(rolesFailedQueue);
    }

    @Bean
    Binding rolesFailedBinding(Queue rolesFailedQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(rolesFailedQueue).to(sagaExchange).with(rolesFailedRoutingKey);
    }
}
