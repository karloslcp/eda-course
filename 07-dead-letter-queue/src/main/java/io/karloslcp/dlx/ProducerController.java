package io.karloslcp.dlx;

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
    void produce(@RequestParam String message) {
        rabbitTemplate.convertAndSend("main-exchange", "main.routing." + message, message);
    }
}
