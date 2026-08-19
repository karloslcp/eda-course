package io.karloslcp.saga.choreography.tenants;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BillingEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(BillingEventConsumer.class);

    private final TenantService tenantService;

    public BillingEventConsumer(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @RabbitListener(queues = {"${eda.billing.failed.queue}", "${eda.billing.compensated.queue}"})
    void deprovisionTenant(String tenantId, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                           @Header(AmqpHeaders.MESSAGE_ID) String messageId) throws IOException {
        log.error("Received billing failed event for tenant {}", tenantId);
        try {
            tenantService.deprovisionTenant(tenantId, messageId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
