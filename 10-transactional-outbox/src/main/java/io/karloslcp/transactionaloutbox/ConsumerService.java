package io.karloslcp.transactionaloutbox;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Service
public class ConsumerService {

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;

    public ConsumerService(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void processCreditMessage(String messageId, CreditRequest creditRequest) {
        transactionTemplate.executeWithoutResult(status -> {
            validateAccount(creditRequest.accountId());
            creditAccount(creditRequest);
            markMessageAsProcessed(messageId);
//            throw new RuntimeException("test error"); // TODO: uncomment to test error scenarios
        });
    }

    private void validateAccount(String accountId) {
        Long accounts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM accounts WHERE account_id = ?", Long.class, accountId);
        if (accounts == null || accounts <= 0) {
            jdbcTemplate.update("INSERT INTO accounts (account_id) VALUES (?)", accountId);
        }
    }

    private void creditAccount(CreditRequest creditRequest) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        jdbcTemplate.update(sql, creditRequest.amount(), creditRequest.accountId());
    }

    private void markMessageAsProcessed(String messageId) {
        String sql = "INSERT INTO processed_messages (message_id) VALUES (?)";
        jdbcTemplate.update(sql, UUID.fromString(messageId));
    }
}
