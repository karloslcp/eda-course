package io.karloslcp._5headerexchange;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    private final RabbitTemplate rabbitTemplate;

    public ProducerController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    void sendMessage(@RequestParam String msg) {
        Message message = MessageBuilder
                .withBody(msg.getBytes())
                .setHeaderIfAbsent("ola", "k-ase") // x- headers don't work here!
                .build();
        rabbitTemplate.send("test-headers-exchange", "", message);
    }
}
