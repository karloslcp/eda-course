package io.karloslcp.fanout;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping
    void sendWithoutRoutingKey(@RequestParam String message) {
        rabbitTemplate.convertAndSend("test-fanout-exchange", "", message);
    }

    @PutMapping
    void sendWithRoutingKey(@RequestParam String message) {
        rabbitTemplate.convertAndSend("test-fanout-exchange", "delete.everything", message);
    }
}
