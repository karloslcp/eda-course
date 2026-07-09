package io.karloslcp._2explicitexchange;

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
    void send(@RequestParam String message) {
//        rabbitTemplate.convertAndSend("test-queue-n2", message); // default exchange, matches routing key to queue name
//        rabbitTemplate.convertAndSend("test-exchange", message); // uses exchange name as routing key, IT DOES NOT MATCH/WORK
//        rabbitTemplate.convertAndSend("ola-k-routing", message); // a valid routing key without an exchange, DOES NOT MATCH
        rabbitTemplate.convertAndSend("test-exchange", "ola-k-routing", message);

    }
}
