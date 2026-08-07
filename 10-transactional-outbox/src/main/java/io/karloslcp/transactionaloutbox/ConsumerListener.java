package io.karloslcp.transactionaloutbox;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConsumerListener {

    private static final int MAX_RETRIES = 3;

    private final ConsumerService consumerService;
    private final RabbitTemplate rabbitTemplate;

    public ConsumerListener(ConsumerService consumerService, RabbitTemplate rabbitTemplate) {
        this.consumerService = consumerService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "main-q")
    public void receiveMessage(CreditRequest creditRequest, Channel channel,
                               @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                               @Header(AmqpHeaders.RETRY_COUNT) int retryCount,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            consumerService.processCreditMessage(messageId, creditRequest);
            channel.basicAck(deliveryTag, false);
        } catch (DuplicateKeyException ex) {
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            if (retryCount >= MAX_RETRIES) {
                channel.basicReject(deliveryTag, false);
                return;
            }
            rabbitTemplate.convertAndSend("main-delay-ex", "delay", creditRequest, message -> {
                message.getMessageProperties().setMessageId(messageId);
                message.getMessageProperties().setExpiration(String.valueOf(getNextRetryDelay(retryCount)));
//                message.getMessageProperties().setHeader(AmqpHeaders.RETRY_COUNT, retryCount + 1);
                message.getMessageProperties().incrementRetryCount(); // TODO: test if this does the same as the line above
                return message;
            });
            channel.basicAck(deliveryTag, false);
        }
    }

    private int getNextRetryDelay(int retryCount) {
        return switch (retryCount) {
            case 0 -> 5000;
            case 1 -> 10000;
            case 2 -> 60000;
            default -> -1;
        };
    }
}
