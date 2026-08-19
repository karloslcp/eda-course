package io.karloslcp.saga.choreography.tenants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Service
public class TenantService {

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final OutboxService outboxService;

    @Value("${eda.exchange}")
    private String exchangeName;

    @Value("${eda.tenants.provisioned.routing-key}")
    private String tenantProvisionedRoutingKey;

    public TenantService(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate, OutboxService outboxService) {
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.outboxService = outboxService;
    }

    public void provisionTenant(TenantProvisioningRequest request) {
        transactionTemplate.executeWithoutResult(status -> {
            UUID tenantId = UUID.randomUUID();
            jdbcTemplate.update("INSERT INTO tenants (id, name) VALUES (?, ?)", tenantId, request.tenantName());

            TenantProvisioningRequest event = new TenantProvisioningRequest(tenantId, request.tenantName(),
                    request.simulateRoleFailure());
            outboxService.addEvent(event, exchangeName, tenantProvisionedRoutingKey);
        });
    }

    public void deprovisionTenant(String tenantId, String eventId) {
        transactionTemplate.executeWithoutResult(status -> {
            try {
                jdbcTemplate.update("INSERT INTO processed_events (event_id) VALUES (?)", eventId);
                jdbcTemplate.update("DELETE FROM tenants WHERE id = ?", UUID.fromString(tenantId));
            } catch (DuplicateKeyException e) {
                status.setRollbackOnly();
            }
        });
    }
}
