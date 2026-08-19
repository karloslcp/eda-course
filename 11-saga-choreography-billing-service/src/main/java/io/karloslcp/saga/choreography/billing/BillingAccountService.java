package io.karloslcp.saga.choreography.billing;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Service
public class BillingAccountService {

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${eda.exchange}")
    private String exchangeName;

    @Value("${eda.billing.created.routing-key}")
    private String billingCreatedRoutingKey;

    @Value("${eda.billing.failed.routing-key}")
    private String billingFailedRoutingKey;

    @Value("${eda.billing.compensated.routing-key}")
    private String billingCompensatedRoutingKey;

    public BillingAccountService(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate,
                                 RabbitTemplate rabbitTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void createAccount(UUID tenantId) {
        transactionTemplate.executeWithoutResult(status -> {
            try {
                jdbcTemplate.update("INSERT INTO billing_accounts (tenant_id) VALUES (?)", tenantId);
                rabbitTemplate.convertAndSend(exchangeName, billingCreatedRoutingKey, tenantId);
            } catch (Exception e) {
                status.setRollbackOnly();
                rabbitTemplate.convertAndSend(exchangeName, billingFailedRoutingKey, tenantId);
            }
        });
    }

    public void deleteAccount(UUID tenantId) {
        transactionTemplate.executeWithoutResult(status -> {
            jdbcTemplate.update("DELETE FROM billing_accounts WHERE tenant_id = ?", tenantId);
            rabbitTemplate.convertAndSend(exchangeName, billingCompensatedRoutingKey, tenantId, message -> {
                message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
                return message;
            });
        });
    }
}
