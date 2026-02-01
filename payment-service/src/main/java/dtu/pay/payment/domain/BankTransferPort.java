package dtu.pay.payment.domain;

import java.math.BigDecimal;

public interface BankTransferPort {
    boolean transfer(String fromAccountId, String toAccountId, BigDecimal amount, String description);
}
