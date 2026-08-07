package io.karloslcp.transactionaloutbox;

public record CreditRequest(String accountId, int amount) {
}
