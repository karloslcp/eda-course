package io.karloslcp.transactionaloutbox;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

@Service
public class OutboxService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public OutboxService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendToOutbox(String exchange, String routingKey, Object message) {
        UUID messageId = UUID.randomUUID();
        String payload = objectMapper.writeValueAsString(message);
        String payloadType = message.getClass().getName();

        String sql = """
                INSERT INTO outbox
                (message_id, exchange, routing_key, payload, payload_type)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql, messageId, exchange, routingKey, payload, payloadType);
    }

    public Optional<OutboxMessage> getNextMessage() {
        String sql = """
                SELECT message_id, exchange, routing_key, payload, payload_type
                FROM outbox
                WHERE published = false
                ORDER BY created_at ASC
                LIMIT 1
                FOR UPDATE SKIP LOCKED;
                """;
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    Optional.of(new OutboxMessage(
                            UUID.fromString(rs.getString("message_id")),
                            rs.getString("exchange"),
                            rs.getString("routing_key"),
                            rs.getString("payload"),
                            rs.getString("payload_type")
                    )));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void markMessageAsPublished(UUID messageId) {
        jdbcTemplate.update("UPDATE outbox SET published = true WHERE message_id = ?", messageId);
    }
}
