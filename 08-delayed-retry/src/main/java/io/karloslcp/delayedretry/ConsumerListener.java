package io.karloslcp.delayedretry;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ConsumerListener {

    private static final String RETRY_ATTEMPTS = "x-retry-attempt";

    private final RabbitTemplate rabbitTemplate;

    public ConsumerListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "main-t8-queue")
    void consumeMain(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                     @Header(name = "x-death", required = false) List<Map<String, Object>> death) throws IOException {
        String messageBody = new String(message.getBody());
        System.out.println("Received message: \"" + messageBody + "\"");
        if ("fail".equals(messageBody)) {
            int attempts = getRetryAttempt(message);
            String retryRoutingKey = switch (attempts) {
                case 0 -> "5s-delay";
                case 1 -> "10s-delay";
                case 2 -> "15s-delay";
                default -> null;
            };

            if (retryRoutingKey == null) {
                channel.basicReject(deliveryTag, false);
                return;
            }
            message.getMessageProperties().setHeader(RETRY_ATTEMPTS, attempts + 1);
            rabbitTemplate.send("delay-exchange", retryRoutingKey, message);
        }
        channel.basicAck(deliveryTag, false);
    }

    private int getRetryAttempt(Message message) {
        Object attempts = message.getMessageProperties().getHeader(RETRY_ATTEMPTS);
        return attempts == null ? 0 : (int) attempts;
    }
}
