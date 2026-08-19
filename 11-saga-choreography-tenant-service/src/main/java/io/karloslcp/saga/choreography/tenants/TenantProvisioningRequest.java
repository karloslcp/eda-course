package io.karloslcp.saga.choreography.tenants;

import java.util.UUID;

public record TenantProvisioningRequest(UUID id, String tenantName, Boolean simulateRoleFailure) {
}
