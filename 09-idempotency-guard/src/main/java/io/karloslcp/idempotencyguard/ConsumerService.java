package io.karloslcp.idempotencyguard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class ConsumerService {

    private static final Logger log = LoggerFactory.getLogger(ConsumerService.class);

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;

    public ConsumerService(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate) {
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void consume(CreditRequest creditRequest, String messageId) {
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.update("INSERT INTO processed_events (event_id) VALUES (?)", messageId);
            } catch (DataIntegrityViolationException e) {
                log.info("Message with ID: {} had already been processed", messageId);
                status.setRollbackOnly();
                return null;
            }

            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM accounts WHERE account_id = ?", Integer.class, creditRequest.accountId());
            if (count == 0) {
                jdbcTemplate.update("INSERT INTO accounts (account_id, balance) VALUES (?, ?)",
                        creditRequest.accountId(), creditRequest.amount());
            } else {
                jdbcTemplate.update("UPDATE accounts SET balance = balance + ? WHERE account_id = ?",
                        creditRequest.amount(), creditRequest.accountId());
            }
            throw new RuntimeException("Ola k ase Exception!!!");
//            return null;
        });
    }
}
