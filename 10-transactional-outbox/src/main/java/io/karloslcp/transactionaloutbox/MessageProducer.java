package io.karloslcp.transactionaloutbox;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;

@Component
public class MessageProducer {

    private final OutboxService outboxService;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(OutboxService outboxService, TransactionTemplate transactionTemplate,
                           ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.outboxService = outboxService;
        this.transactionTemplate = transactionTemplate;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 10000)
    public void sendMessage() {
        outboxService.getNextMessage().ifPresent(message ->
                transactionTemplate.executeWithoutResult(status -> {
                    outboxService.markMessageAsPublished(message.messageId());

                    try {
                        sendMessage(message);
                    } catch (ClassNotFoundException e) {
                        status.setRollbackOnly();
                    }
                })
        );
    }

    private void sendMessage(OutboxMessage outboxMessage) throws ClassNotFoundException {
        Class<?> payloadType = Class.forName(outboxMessage.payloadType());
        Object payload = objectMapper.readValue(outboxMessage.payload(), payloadType);

        rabbitTemplate.convertAndSend(outboxMessage.exchange(), outboxMessage.routingKey(), payload, message -> {
            message.getMessageProperties().setMessageId(outboxMessage.messageId().toString());
            return message;
        });
    }
}
