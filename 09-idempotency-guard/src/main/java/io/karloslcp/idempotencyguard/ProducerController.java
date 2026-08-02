package io.karloslcp.idempotencyguard;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/producers")
public class ProducerController {

    private final RabbitTemplate rabbitTemplate;

    public ProducerController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    void postMessage(@RequestBody CreditRequest creditRequest) {
//        Message message = MessageBuilder
//                .withBody(creditRequest) // FIXME: this builder doesn't work with objects now, how to fix it?
//                .setMessageId(UUID.randomUUID().toString())
//                .build();
        rabbitTemplate.convertAndSend("main-exchange", "main-routing-key", creditRequest, message -> {
            message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
            return message;
        });
    }
}
