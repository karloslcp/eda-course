package io.karloslcp.topicrouting;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ConsumerListener {

    @RabbitListener(queues = "one-word-match")
    void consumeOneWordMatched(String tenantName, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        System.out.println("Receiving message and header as two arguments");
        System.out.println("Received: " + tenantName + " from one-word-match queue with routing key: " + routingKey);
    }

    @RabbitListener(queues = "one-word-match")
    void consumeOneWordMatched(Message message) {
        System.out.println("Receiving message as one argument");
        System.out.println("Received: " + message.getMessageProperties().getReceivedRoutingKey());
        System.out.println("Received: " + new String(message.getBody()));
    }

    @RabbitListener(queues = "zero-or-more-words-match")
    void consumeZeroOrMoreWordsMatched(String tenantName) {
        System.out.println("Received: " + tenantName + " from zero-or-more-words-match queue");
    }

    @RabbitListener(queues = "test-queue-n3")
    void consumeDirect(String tenantId) {
        System.out.println("Received: " + tenantId + " from test-queue-n3");
    }
}
