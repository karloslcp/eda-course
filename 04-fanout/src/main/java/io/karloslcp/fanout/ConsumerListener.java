package io.karloslcp.fanout;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerListener {

    @RabbitListener(queues = "queue-one")
    void receiveOne(String message) {
        System.out.println("Received '" + message + "' from queue-one");
    }

    @RabbitListener(queues = "queue-two")
    void receiveTwo(String message) {
        System.out.println("Received '" + message + "' from queue-two");
    }
}
