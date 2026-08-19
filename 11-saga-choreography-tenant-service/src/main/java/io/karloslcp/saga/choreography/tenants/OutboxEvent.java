package io.karloslcp.saga.choreography.tenants;

import java.util.UUID;

public record OutboxEvent(UUID eventId, String exchange, String routingKey, String payload, String payloadType) {
}
