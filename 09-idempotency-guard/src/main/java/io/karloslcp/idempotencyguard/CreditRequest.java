package io.karloslcp.idempotencyguard;

import java.io.Serializable;

public record CreditRequest(String accountId, int amount) implements Serializable {
}
