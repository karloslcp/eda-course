package io.karloslcp._2explicitexchange;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerListener {

    @RabbitListener(queues = "test-queue-n2")
    void receiveOla(String message) {
        System.out.println("Received: " + message);
    }
}
