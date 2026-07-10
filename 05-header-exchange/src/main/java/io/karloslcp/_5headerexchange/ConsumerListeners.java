package io.karloslcp._5headerexchange;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerListeners {

    @RabbitListener(queues = "new-test-queue")
    public void receiveMessage(Message message) {
        System.out.println("Received message: " + message);
    }
}
