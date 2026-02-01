package dtu.pay.payment.infrastructure;

import dtu.pay.payment.domain.BankTransferPort;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class StubBankTransferAdapter implements BankTransferPort {
    private boolean shouldFail = false;
    private Map<String, Boolean> accountFailures = new HashMap<>();

    @Override
    public boolean transfer(String fromAccountId, String toAccountId, BigDecimal amount, String description) {
        // Check global failure flag
        if (shouldFail) {
            return false;
        }
        
        // Check per-account failure configuration
        if (accountFailures.containsKey(fromAccountId) && accountFailures.get(fromAccountId)) {
            return false;
        }
        
        if (accountFailures.containsKey(toAccountId) && accountFailures.get(toAccountId)) {
            return false;
        }
        
        return true;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    public void setAccountShouldFail(String accountId, boolean shouldFail) {
        accountFailures.put(accountId, shouldFail);
    }

    public void reset() {
        this.shouldFail = false;
        this.accountFailures.clear();
    }
}
