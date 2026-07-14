package io.karloslcp.dlx;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConsumerListener {

    @RabbitListener(queues = "main-queue")
    void consume(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        System.out.println("message '" + message + "' with deliveryTag " + deliveryTag + " has been received");
        if (message.equals("error")) {
            channel.basicReject(deliveryTag, false);
        } else {
            channel.basicAck(deliveryTag, false);
        }
    }

    @RabbitListener(queues = "dlq-queue")
    void consumeDLQ(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        // changed `message` type from `String` to `Message` to validate `x-dead` header
        System.out.println("message '" + message + "' with deliveryTag " + deliveryTag + " has been discarded");
        channel.basicAck(deliveryTag, false); // you still need to ack the message in this DLQ path
    }
}
