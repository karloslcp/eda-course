package io.karloslcp.topicrouting;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenants/{tenantId}")
public class ProducerController {

    private final RabbitTemplate rabbitTemplate;

    public ProducerController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    void provisionTenant(@PathVariable String tenantId, @RequestBody Tenant tenant) {
        rabbitTemplate.convertAndSend("test-topic-exchange", "tenant." + tenantId + ".provisioned", tenant.name());
    }

    @PutMapping
    void processTenant(@PathVariable String tenantId, @RequestBody Tenant tenant) {
        rabbitTemplate.convertAndSend("test-topic-exchange", "tenant." + tenantId, tenant.name());
    }

    @PatchMapping
    void updateTenant(@PathVariable String tenantId, @RequestBody Tenant tenant) {
        rabbitTemplate.convertAndSend("test-topic-exchange", "tenant." + tenantId + ".update", tenant.name());
    }

    @DeleteMapping
    void deprovisionTenant(@PathVariable String tenantId) {
        rabbitTemplate.convertAndSend("test-topic-exchange", "tenant." + tenantId + ".deprovision.forever", tenantId);
    }

    @PutMapping("/direct")
    void processTenantDirectly(@PathVariable String tenantId, @RequestBody Tenant tenant) {
//        rabbitTemplate.convertAndSend("test-direct-exchange", "tenant." + tenantId, tenant.name()); // this doesn't route the message to the "test-queue-n3" queue, seems the '#' wildcard is treated as a literal by direct exchanges instead
        rabbitTemplate.convertAndSend("test-direct-exchange", "tenant.#", tenantId); // this worked! so the binding expects the actual '#' character here, not treated as wildcard

    }
}
