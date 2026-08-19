package io.karloslcp.saga.choreography.tenants;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class OutboxService {

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    public OutboxService(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void addEvent(Object payload, String exchange, String routingKey) {
        String payloadJson = objectMapper.writeValueAsString(payload);
        String payloadType = payload.getClass().getName();

        String sql = "INSERT INTO outbox (event_id, exchange, routing_key, payload, payload_type) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, UUID.randomUUID(), exchange, routingKey, payloadJson, payloadType);
    }

    @Scheduled(fixedDelay = 1000)
    public void sendEvent() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OutboxEvent outboxEvent = nextEvent();
            if (outboxEvent != null) {
                // Event is marked as processed before actually sending it to RabbitMQ so if either of the steps fails,
                // the transaction will be rolled back and the event will be retried. If marking the event after sending it,
                // the transaction might fail and the sending of the event would not be rolled back.
                try {
                    markEventAsProcessed(outboxEvent.eventId());
                    sendEvent(outboxEvent);
                } catch (ClassNotFoundException e) {
                    transactionStatus.setRollbackOnly();
                }
            }
        });
    }

    private OutboxEvent nextEvent() {
        String query = """
                    SELECT event_id, exchange, routing_key, payload, payload_type
                    FROM outbox
                    WHERE processed = false
                    ORDER BY created_at ASC
                    FOR UPDATE SKIP LOCKED
                    LIMIT 1
                """;
        return jdbcTemplate.queryForObject(query, (rs, rowNum) ->
                new OutboxEvent(
                        UUID.fromString(rs.getString("event_id")),
                        rs.getString("exchange"),
                        rs.getString("routing_key"),
                        rs.getString("payload"),
                        rs.getString("payload_typ")
                ));
    }

    private void markEventAsProcessed(UUID eventId) {
        jdbcTemplate.update("UPDATE outbox SET processed = true WHERE event_id = ?", eventId);
    }

    private void sendEvent(OutboxEvent event) throws ClassNotFoundException {
        Class<?> payloadType = Class.forName(event.payloadType());
        Object payload = objectMapper.readValue(event.payload(), payloadType);
        rabbitTemplate.convertAndSend(event.exchange(), event.routingKey(), payload, message -> {
            message.getMessageProperties().setMessageId(event.eventId().toString());
            return message;
        });
    }
}
