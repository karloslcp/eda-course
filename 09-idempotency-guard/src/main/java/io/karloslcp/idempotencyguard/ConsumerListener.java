package io.karloslcp.idempotencyguard;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConsumerListener {

    private static final String ATTEMPT = "x-retry-attempt";
    private static final int MAX_ATTEMPTS = 3;

    private final RabbitTemplate rabbitTemplate;
    private final ConsumerService consumerService;

    public ConsumerListener(RabbitTemplate rabbitTemplate, ConsumerService consumerService) {
        this.rabbitTemplate = rabbitTemplate;
        this.consumerService = consumerService;
    }

    @RabbitListener(queues = "main-queue")
    void consume(CreditRequest creditRequest, Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        String messageId = message.getMessageProperties().getMessageId();
        try {
            consumerService.consume(creditRequest, messageId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            int attempt = getRetryAttempt(message);
            if (attempt < MAX_ATTEMPTS) {
                message.getMessageProperties().setHeader(ATTEMPT, attempt + 1);
                rabbitTemplate.send("main-exchange", "main-routing-key", message);
                channel.basicAck(deliveryTag, false);
            } else {
                channel.basicReject(deliveryTag, false);
            }
        }
    }

    int getRetryAttempt(Message message) {
        Object attempt = message.getMessageProperties().getHeader(ATTEMPT);
        return attempt instanceof Integer ? (Integer) attempt : 0;
    }
}
