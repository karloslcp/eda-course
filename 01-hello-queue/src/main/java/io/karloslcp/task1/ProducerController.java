package io.karloslcp.task1;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/messages")
public class ProducerController {

    private final RabbitTemplate rabbitTemplate;

    public ProducerController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    void sendMessage(@RequestParam String message) {
//        Message builtMessage = MessageBuilder
//                .withBody(message.getBytes(StandardCharsets.UTF_8))
//                .build();
//        rabbitTemplate.send(builtMessage);
        rabbitTemplate.convertAndSend("test-queue", message);
    }
}
