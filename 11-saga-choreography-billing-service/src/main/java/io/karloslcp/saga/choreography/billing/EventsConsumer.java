package io.karloslcp.saga.choreography.billing;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class EventsConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventsConsumer.class);

    private final BillingAccountService billingAccountService;

    public EventsConsumer(BillingAccountService billingAccountService) {
        this.billingAccountService = billingAccountService;
    }

    @RabbitListener(queues = "${eda.tenants.provisioned.queue}")
    void createAccount(UUID tenantId, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("Received tenant provisioned event for tenant {}", tenantId);
        try {
            billingAccountService.createAccount(tenantId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(queues = "${eda.roles.failed.queue}")
    void deleteAccount(UUID tenantId, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.error("Received roles failed event for tenant {}", tenantId);
        try {
            billingAccountService.deleteAccount(tenantId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
