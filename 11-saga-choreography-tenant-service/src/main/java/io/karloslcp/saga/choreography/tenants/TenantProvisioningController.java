package io.karloslcp.saga.choreography.tenants;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenants")
public class TenantProvisioningController {

    private final TenantService tenantService;

    public TenantProvisioningController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    void provisionTenant(@RequestBody TenantProvisioningRequest request) {
        tenantService.provisionTenant(request);
    }
}
