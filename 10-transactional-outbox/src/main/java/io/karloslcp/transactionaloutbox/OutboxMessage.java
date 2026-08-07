package io.karloslcp.transactionaloutbox;

import java.util.UUID;

public record OutboxMessage(UUID messageId, String exchange, String routingKey, String payload, String payloadType) {
}
