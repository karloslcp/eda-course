package io.karloslcp.manualack;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConsumerListener {

    @RabbitListener(queues = "manual-ack-queue")
    void consume(String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel)
            throws IOException {
        System.out.println("Received message: " + message);
        switch (message) {
            case "error" -> channel.basicReject(deliveryTag, false);
            case "retry" -> channel.basicNack(deliveryTag, false, true);
            default -> channel.basicAck(deliveryTag, false);
        }
    }
}
