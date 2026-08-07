package io.karloslcp.transactionaloutbox;

import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class ProducerController {

    private final TransactionTemplate transactionTemplate;
    private final OutboxService outboxService;

    public ProducerController(TransactionTemplate transactionTemplate, OutboxService outboxService) {
        this.transactionTemplate = transactionTemplate;
        this.outboxService = outboxService;
    }

    @PostMapping
    void produceMessage(@RequestBody CreditRequest creditRequest) {
        transactionTemplate.executeWithoutResult(status -> {
            // TODO: do some work

            // then send message to the outbox
            outboxService.sendToOutbox("main-ex", "main.kola", creditRequest);
        });
    }
}
